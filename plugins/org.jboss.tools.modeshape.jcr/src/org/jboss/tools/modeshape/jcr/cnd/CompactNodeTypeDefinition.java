/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.modeshape.jcr.Activator;
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.ItemDefinition;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.WorkspaceRegistry;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * The <code>CompactNodeTypeDefinition</code> class represents one CND file.
 */
public class CompactNodeTypeDefinition implements CndElement {

    /**
     * @param cndToCopy the CND being copied (cannot be <code>null</code>)
     * @return a CND exactly equals to the CND that was copied (never <code>null</code>)
     */
    public static CompactNodeTypeDefinition copy( final CompactNodeTypeDefinition cndToCopy ) {
        Utils.verifyIsNotNull(cndToCopy, "cndToCopy"); //$NON-NLS-1$

        final CompactNodeTypeDefinition copy = new CompactNodeTypeDefinition();

        // namespace mappings
        for (final NamespaceMapping namespaceMapping : cndToCopy.getNamespaceMappings()) {
            copy.addNamespaceMapping(NamespaceMapping.copy(namespaceMapping));
        }

        // node type definitions
        for (final NodeTypeDefinition nodeTypeDefinition : cndToCopy.getNodeTypeDefinitions()) {
            copy.addNodeTypeDefinition(NodeTypeDefinition.copy(nodeTypeDefinition));
        }

        return copy;
    }

    /**
     * The registered property change listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * The namespace mappings (can be <code>null</code>).
     */
    private List<NamespaceMapping> namespaceMappings;

    /**
     * The node type definitions (can be <code>null</code>).
     */
    private List<NodeTypeDefinition> nodeTypeDefinitions;

    /**
     * Constructs an instance with no namespace mappings or node type definitions.
     */
    public CompactNodeTypeDefinition() {
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    }

    /**
     * @param newListener the listener being registered (cannot be <code>null</code>)
     * @return <code>true</code> if registered
     */
    public boolean addListener( final PropertyChangeListener newListener ) {
        Utils.verifyIsNotNull(newListener, "newListener"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(newListener);
    }

    /**
     * If added, broadcasts a {@link PropertyChangeEvent} with an old value of <code>null</code> and a new value equal to
     * <code>namespaceMappingToAdd</code>.
     * 
     * @param namespaceMappingToAdd the namespace mapping being added (cannot be <code>null</code>)
     * @return <code>true</code> if added
     */
    public boolean addNamespaceMapping( final NamespaceMapping namespaceMappingToAdd ) {
        Utils.verifyIsNotNull(namespaceMappingToAdd, "namespaceMappingToAdd"); //$NON-NLS-1$

        if (this.namespaceMappings == null) {
            this.namespaceMappings = new ArrayList<NamespaceMapping>();
        }

        if (this.namespaceMappings.add(namespaceMappingToAdd)) {
            notifyChangeListeners(PropertyName.NAMESPACE_MAPPINGS, null, namespaceMappingToAdd);
            return true; // added
        }

        return false; // not added
    }

    /**
     * If added, broadcasts a {@link PropertyChangeEvent} with an old value of <code>null</code> and a new value of
     * <code>nodeTypeDefinitionToAdd</code>.
     * 
     * @param nodeTypeDefinitionToAdd the node type definition being added (cannot be <code>null</code>)
     * @return <code>true</code> if added
     */
    public boolean addNodeTypeDefinition( final NodeTypeDefinition nodeTypeDefinitionToAdd ) {
        Utils.verifyIsNotNull(nodeTypeDefinitionToAdd, "nodeTypeDefinitionToAdd"); //$NON-NLS-1$

        if (this.nodeTypeDefinitions == null) {
            this.nodeTypeDefinitions = new ArrayList<NodeTypeDefinition>();
        }

        if (this.nodeTypeDefinitions.add(nodeTypeDefinitionToAdd)) {
            notifyChangeListeners(PropertyName.NODE_TYPE_DEFINITIONS, null, nodeTypeDefinitionToAdd);
            return true; // added
        }

        return false; // not added
    }

    /**
     * If at least one namespace mapping was removed, broadcasts a {@link PropertyChangeEvent} with an old value equal to the old
     * namespace mappings collection and a new value of <code>null</code>.
     * 
     * @return <code>true</code> if at least one namespace mapping was removed
     */
    public boolean clearNamespaceMappings() {
        if (this.namespaceMappings == null) {
            return false; // nothing to clear
        }

        boolean wasCleared = false;

        if (!this.namespaceMappings.isEmpty()) {
            final Object oldValue = new ArrayList<NamespaceMapping>(this.namespaceMappings);
            wasCleared = true;
            this.namespaceMappings.clear();
            notifyChangeListeners(PropertyName.NAMESPACE_MAPPINGS, oldValue, null);
        }

        this.namespaceMappings = null;
        return wasCleared;
    }

    /**
     * If at least one node type definition was removed, broadcasts a {@link PropertyChangeEvent} with an old value equal to the old
     * node type definitions collection and a new value of <code>null</code>.
     * 
     * @return <code>true</code> if at least one namespace mapping was removed
     */
    public boolean clearNodeTypeDefinitions() {
        if (this.nodeTypeDefinitions == null) {
            return false; // nothing to clear
        }

        boolean wasCleared = false;

        if (!this.nodeTypeDefinitions.isEmpty()) {
            final Object oldValue = new ArrayList<NodeTypeDefinition>(this.nodeTypeDefinitions);
            wasCleared = true;
            this.nodeTypeDefinitions.clear();
            notifyChangeListeners(PropertyName.NODE_TYPE_DEFINITIONS, oldValue, null);
        }

        this.nodeTypeDefinitions = null;
        return wasCleared;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        final CompactNodeTypeDefinition that = (CompactNodeTypeDefinition)obj;

        { // compare namespace mappings
            final List<NamespaceMapping> thisNamespaces = getNamespaceMappings();
            final List<NamespaceMapping> thatNamespaces = that.getNamespaceMappings();

            if (!Utils.equivalent(thisNamespaces, thatNamespaces)) {
                return false;
            }
        }

        { // compare node type definitions
            final List<NodeTypeDefinition> thisNodeTypes = getNodeTypeDefinitions();
            final List<NodeTypeDefinition> thatNodeTypes = that.getNodeTypeDefinitions();

            if (!Utils.equivalent(thisNodeTypes, thatNodeTypes)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The node type definition must exist in this CND for it to return child nodes.
     * 
     * @param nodeTypeDefinitionName the name of the node type definition whose child nodes are being requested (cannot be
     *            <code>null</code> or empty)
     * @param includeInherited indicates if inherited child nodes should be included
     * @return the child nodes (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the inherited child node definitions
     */
    public Collection<ChildNodeDefinition> getChildNodeDefinitions( final String nodeTypeDefinitionName,
                                                                    final boolean includeInherited ) throws Exception {
        final NodeTypeDefinition nodeType = getNodeTypeDefinition(nodeTypeDefinitionName);

        // if not found in CND return empty collection
        if (nodeType == null) {
            return Collections.emptyList();
        }

        // first add declared child nodes
        final Collection<ChildNodeDefinition> childNodes = new ArrayList<ChildNodeDefinition>(nodeType.getChildNodeDefinitions());

        // now add inherited child nodes
        if (includeInherited) {
            for (final QualifiedName superType : nodeType.getSupertypes()) {
                final NodeTypeDefinition superTypeNodeType = getNodeTypeDefinition(superType.get());

                if (superTypeNodeType == null) {
                    // super type not found in CND so see if it is a built-in
                    childNodes.addAll(WorkspaceRegistry.get().getChildNodeDefinitions(superType.get(), true));
                } else {
                    childNodes.addAll(getChildNodeDefinitions(superTypeNodeType.getName(), true));
                }
            }
        }

        return childNodes;
    }

    /**
     * The node type definition must exist in this CND for it to return child nodes.
     * 
     * @param nodeTypeDefinitionName the name of the node type definition whose item definitions are being requested (cannot be
     *            <code>null</code> or empty)
     * @param includeInherited indicates if inherited item definitions should be included
     * @return the item definitions (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the inherited item definitions
     */
    public Collection<ItemDefinition> getItemDefinitions( final String nodeTypeDefinitionName,
                                                          final boolean includeInherited ) throws Exception {
        final List<ItemDefinition> itemDefinitions = new ArrayList<ItemDefinition>(getChildNodeDefinitions(nodeTypeDefinitionName,
                                                                                                           includeInherited));
        itemDefinitions.addAll(getPropertyDefinitions(nodeTypeDefinitionName, includeInherited));
        Collections.sort(itemDefinitions);
        return itemDefinitions;
    }

    /**
     * @param namespacePrefix the namespace prefix of the node type definitions being requested (cannot be <code>null</code> or
     *            empty)
     * @param includeInherited indicates if inherited node type definitions should be included
     * @return the requested node type definitions (never <code>null</code> but can be empty)
     */
    public List<NodeTypeDefinition> getMatchingNodeTypeDefinitions( final String namespacePrefix,
                                                                    final boolean includeInherited ) {
        Utils.verifyIsNotEmpty(namespacePrefix, "namespacePrefix"); //$NON-NLS-1$
        final List<NodeTypeDefinition> matches = new ArrayList<NodeTypeDefinition>();

        // collect local matches
        for (final NodeTypeDefinition nodeType : getNodeTypeDefinitions()) {
            if (namespacePrefix.equals(nodeType.getQualifiedName().getQualifier())) {
                matches.add(nodeType);
            }
        }

        // collect inherited matches
        if (includeInherited) {
            try {
                matches.addAll(WorkspaceRegistry.get().getMatchingNodeTypeDefinitions(namespacePrefix));
            } catch (final Exception e) {
                if (Platform.isRunning()) {
                    Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                } else {
                    System.err.print(e.getMessage());
                }
            }
        }

        return matches;
    }

    /**
     * @return the namespace mappings (never <code>null</code>)
     */
    public List<NamespaceMapping> getNamespaceMappings() {
        if (this.namespaceMappings == null) {
            return Collections.emptyList();
        }

        return this.namespaceMappings;
    }

    /**
     * @return the prefixes of all the namespace mappings (never <code>null</code> but can be empty)
     */
    public Collection<String> getNamespacePrefixes() {
        final List<NamespaceMapping> namespaces = getNamespaceMappings();

        if (namespaces.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> prefixes = new ArrayList<String>(namespaces.size());

        for (final NamespaceMapping namespace : namespaces) {
            prefixes.add(namespace.getPrefix());
        }

        return prefixes;
    }

    /**
     * @param name the name of the node type definition being requested (cannot be <code>null</code> or empty)
     * @return the node type definition or <code>null</code> if not found
     */
    public NodeTypeDefinition getNodeTypeDefinition( final String name ) {
        Utils.verifyIsNotEmpty(name, "name"); //$NON-NLS-1$

        for (final NodeTypeDefinition ntd : getNodeTypeDefinitions()) {
            if (name.equals(ntd.getName())) {
                return ntd;
            }
        }

        return null;
    }

    /**
     * @return the node type definitions (never <code>null</code>)
     */
    public List<NodeTypeDefinition> getNodeTypeDefinitions() {
        if (this.nodeTypeDefinitions == null) {
            return Collections.emptyList();
        }

        return this.nodeTypeDefinitions;
    }

    /**
     * The node type definition must exist in this CND for it to return properties.
     * 
     * @param nodeTypeDefinitionName the name of the node type definition whose properties are being requested (cannot be
     *            <code>null</code> or empty)
     * @param includeInherited indicates if inherited properties should be included
     * @return the properties (never <code>null</code>)
     * @throws Exception if there is a problem obtaining inherited property definitions
     */
    public Collection<PropertyDefinition> getPropertyDefinitions( final String nodeTypeDefinitionName,
                                                                  final boolean includeInherited ) throws Exception {
        final NodeTypeDefinition nodeType = getNodeTypeDefinition(nodeTypeDefinitionName);

        // if not found in CND return empty collection
        if (nodeType == null) {
            return Collections.emptyList();
        }

        // first add declared properties
        final Collection<PropertyDefinition> properties = new ArrayList<PropertyDefinition>(nodeType.getPropertyDefinitions());

        // now add inherited properties
        if (includeInherited) {
            for (final QualifiedName superType : nodeType.getSupertypes()) {
                final NodeTypeDefinition superTypeNodeType = getNodeTypeDefinition(superType.get());

                if (superTypeNodeType == null) {
                    // super type not found in CND so see if it is a built-in
                    properties.addAll(WorkspaceRegistry.get().getPropertyDefinitions(superType.get(), true));
                } else {
                    properties.addAll(getPropertyDefinitions(superTypeNodeType.getName(), true));
                }
            }
        }

        return properties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int result1 = Utils.hashCode(getNamespaceMappings().toArray());
        final int result2 = Utils.hashCode(getNodeTypeDefinitions().toArray());
        return Utils.hashCode(result1, result2);
    }

    /**
     * @param property the property that was changed (never <code>null</code>)
     * @param oldValue the old value (can be <code>null</code>)
     * @param newValue the new value (can be <code>null</code>)
     */
    private void notifyChangeListeners( final PropertyName property,
                                        final Object oldValue,
                                        final Object newValue ) {
        assert (property != null) : "property is null"; //$NON-NLS-1$

        final PropertyChangeEvent event = new PropertyChangeEvent(this, property.toString(), oldValue, newValue);

        for (final Object listener : this.listeners.toArray()) {
            try {
                ((PropertyChangeListener)listener).propertyChange(event);
            } catch (final Exception e) {
                if (Platform.isRunning()) {
                    Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                } else {
                    System.err.print(e.getMessage());
                }

                this.listeners.remove(listener);
            }
        }
    }

    /**
     * @param listener the listener being unregistered (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeListener( final PropertyChangeListener listener ) {
        Utils.verifyIsNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * If namespace mapping is removed, broadcasts a {@link PropertyChangeEvent} with an old value of
     * <code>namespaceMappingToRemove</code> and a new value of <code>null</code>.
     * 
     * @param namespaceMappingToRemove the namespace mapping being removed (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeNamespaceMapping( final NamespaceMapping namespaceMappingToRemove ) {
        Utils.verifyIsNotNull(namespaceMappingToRemove, "namespaceMappingToRemove"); //$NON-NLS-1$

        if ((this.namespaceMappings != null) && this.namespaceMappings.remove(namespaceMappingToRemove)) {
            notifyChangeListeners(PropertyName.NAMESPACE_MAPPINGS, namespaceMappingToRemove, null);

            if (this.namespaceMappings.isEmpty()) {
                this.namespaceMappings = null;
            }

            return true; // removed
        }

        return false; // not removed
    }

    /**
     * If node type definition is removed, broadcasts a {@link PropertyChangeEvent} with an old value of
     * <code>nodeTypeDefinitionToRemove</code> and a new value of <code>null</code>.
     * 
     * @param nodeTypeDefinitionToRemove the node type definition being removed (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeNodeTypeDefinition( final NodeTypeDefinition nodeTypeDefinitionToRemove ) {
        Utils.verifyIsNotNull(nodeTypeDefinitionToRemove, "nodeTypeDefinitionToRemove"); //$NON-NLS-1$

        if ((this.nodeTypeDefinitions != null) && this.nodeTypeDefinitions.remove(nodeTypeDefinitionToRemove)) {
            notifyChangeListeners(PropertyName.NODE_TYPE_DEFINITIONS, nodeTypeDefinitionToRemove, null);

            if (this.nodeTypeDefinitions.isEmpty()) {
                this.nodeTypeDefinitions = null;
            }

            return true; // removed
        }

        return false; // not removed
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        final JcrPreferenceStore prefStore = JcrPreferenceStore.get();
        final StringBuilder builder = new StringBuilder();
        boolean addDelim = false;

        { // namespace mappings
            if (!Utils.isEmpty(this.namespaceMappings)) {
                final String DELIM = prefStore.get(JcrPreferenceConstants.CndPreference.NAMESPACE_MAPPING_DELIMITER);

                for (final NamespaceMapping namespaceMapping : this.namespaceMappings) {
                    if (Utils.build(builder, addDelim, DELIM, namespaceMapping.toCndNotation(notationType))) {
                        addDelim = true;
                    }
                }

                if (NotationType.COMPACT != notationType) {
                    builder.append(prefStore.get(JcrPreferenceConstants.CndPreference.NAMESPACE_MAPPING_SECTION_END_DELIMITER));
                }
            }
        }

        { // node type definitions
            if (!Utils.isEmpty(this.nodeTypeDefinitions)) {
                final String DELIM = prefStore.get(JcrPreferenceConstants.CndPreference.NODE_TYPE_DEFINITION_DELIMITER);

                for (final NodeTypeDefinition nodeTypeDefinition : this.nodeTypeDefinitions) {
                    if (Utils.build(builder, addDelim, DELIM, nodeTypeDefinition.toCndNotation(notationType))) {
                        addDelim = (NotationType.COMPACT != notationType);
                    }
                }

                if (NotationType.COMPACT != notationType) {
                    builder.append(prefStore.get(JcrPreferenceConstants.CndPreference.NODE_TYPE_DEFINITION_SECTION_END_DELIMITER));
                }
            }
        }

        return builder.toString();
    }

    /**
     * The property names whose <code>toString()</code> is used in {@link PropertyChangeEvent}s.
     */
    public enum PropertyName {

        /**
         * The namespace mappings property.
         */
        NAMESPACE_MAPPINGS,

        /**
         * The node type definitions property.
         */
        NODE_TYPE_DEFINITIONS;

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return (getClass().getName() + super.toString());
        }
    }

}
