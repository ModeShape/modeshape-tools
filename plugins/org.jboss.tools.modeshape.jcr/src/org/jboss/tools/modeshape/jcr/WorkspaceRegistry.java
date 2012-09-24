/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.cnd.CndImporter;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinition;
import org.osgi.framework.Bundle;

/**
 * A registry containing {@link NamespaceMapping namespace mappings} and {@link NodeTypeDefinition node type definitions}. At
 * construction the registry holds the built-in JSR namespaces and node types.
 */
public class WorkspaceRegistry {

    private static WorkspaceRegistry _registry;

    private static String BUILT_INS_CND_FILE_NAME = "cnd/jsr_283_builtins.cnd"; //$NON-NLS-1$

    /**
     * @return the shared instance of the workspace registry (never <code>null</code>)
     * @throws Exception if there is a problem loading the registry
     */
    public static WorkspaceRegistry get() throws Exception {
        if (_registry == null) {
            _registry = new WorkspaceRegistry();
        }

        return _registry;
    }

    private final Collection<NamespaceMapping> namespaces;

    private final Collection<NodeTypeDefinition> nodeTypes;

    /**
     * Don't allow construction outside of this class.
     */
    private WorkspaceRegistry() throws Exception {
        final CndImporter importer = new CndImporter();
        final List<Throwable> errors = new ArrayList<Throwable>();
        File builtInsCndFile = null;

        if (Platform.isRunning()) {
            final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
            final URL url = bundle.getEntry(BUILT_INS_CND_FILE_NAME);

            if (url == null) {
                throw new Exception(NLS.bind(Messages.jsrBuiltInsCndFileNotFound, BUILT_INS_CND_FILE_NAME));
            }

            builtInsCndFile = new File(org.eclipse.core.runtime.FileLocator.toFileURL(url).getFile());

            if (!builtInsCndFile.exists()) {
                throw new Exception(NLS.bind(Messages.jsrBuiltInsCndFileNotFoundInFilesystem, BUILT_INS_CND_FILE_NAME));
            }
        } else {
            // when running unit tests
            builtInsCndFile = new File(BUILT_INS_CND_FILE_NAME);
        }

        final CompactNodeTypeDefinition jsrBuiltIns = importer.importFrom(builtInsCndFile, errors);

        // check for parse errors
        if (!errors.isEmpty()) {
            final Throwable t = errors.iterator().next();

            if (t.getCause() == null) {
                throw new RuntimeException(t);
            }

            throw new Exception(t.getCause());
        }

        this.namespaces = new ArrayList<NamespaceMapping>();
        this.nodeTypes = new ArrayList<NodeTypeDefinition>();

        // register namespace mappings
        for (final NamespaceMapping namespace : jsrBuiltIns.getNamespaceMappings()) {
            this.namespaces.add(namespace);
        }

        // register node type definitions
        for (final NodeTypeDefinition nodeType : jsrBuiltIns.getNodeTypeDefinitions()) {
            this.nodeTypes.add(nodeType);
        }
    }

    /**
     * @param nodeTypeDefinitionName the name of the node type definition whose child nodes are being requested (cannot be
     *            <code>null</code> or empty)
     * @param includeInherited indicates if inherited child nodes should be included
     * @return the child nodes (never <code>null</code>)
     */
    public Collection<ChildNodeDefinition> getChildNodeDefinitions( final String nodeTypeDefinitionName,
                                                                    final boolean includeInherited ) {
        final NodeTypeDefinition nodeType = getNodeTypeDefinition(nodeTypeDefinitionName);

        if (nodeType == null) {
            return Collections.emptyList();
        }

        final Collection<ChildNodeDefinition> childNodes = new ArrayList<ChildNodeDefinition>(nodeType.getChildNodeDefinitions());

        if (includeInherited) {
            for (final QualifiedName superType : nodeType.getSupertypes()) {
                final NodeTypeDefinition superTypeNodeType = getNodeTypeDefinition(superType.get());

                if (superTypeNodeType != null) {
                    childNodes.addAll(getChildNodeDefinitions(superTypeNodeType.getName(), true));
                }
            }
        }

        return childNodes;
    }

    /**
     * Obtains the node type definitions registered to the specified namespace.
     *
     * @param namespacePrefix the namespace prefix of the node type definitions being requested (can be <code>null</code> or
     *            empty)
     * @return the requested node type definitions (never <code>null</code> but can be empty)
     */
    public List<NodeTypeDefinition> getMatchingNodeTypeDefinitions( final String namespacePrefix ) {
        final boolean matchEmptyPrefix = Utils.isEmpty(namespacePrefix);
        final List<NodeTypeDefinition> matches = new ArrayList<NodeTypeDefinition>();

        for (final NodeTypeDefinition nodeType : getNodeTypeDefinitions()) {
            final String qualifier = nodeType.getQualifiedName().getQualifier();

            if (matchEmptyPrefix) {
                if (Utils.isEmpty(qualifier)) {
                    matches.add(nodeType);
                }
            } else if (namespacePrefix.equals(qualifier)) {
                matches.add(nodeType);
            }
        }

        return matches;
    }

    /**
     * @param prefix the prefix whose namespace mapping is being requested (cannot be <code>null</code> or empty)
     * @return thre requested namespace mapping or <code>null</code> if not found
     */
    public NamespaceMapping getNamespaceMapping( final String prefix ) {
        Utils.verifyIsNotEmpty(prefix, "prefix"); //$NON-NLS-1$

        for (final NamespaceMapping namespace : getNamespaceMappings()) {
            if (namespace.getPrefix().equals(prefix)) {
                return namespace;
            }
        }

        return null;
    }

    /**
     * @return an unmodifiable collection of namespace mappings (never <code>null</code>)
     */
    public Collection<NamespaceMapping> getNamespaceMappings() {
        return Collections.unmodifiableCollection(this.namespaces);
    }

    /**
     * @param name the name of the node type definition being requested (cannot be <code>null</code> or empty)
     * @return the node type definition or <code>null</code> if not found
     */
    public NodeTypeDefinition getNodeTypeDefinition( final String name ) {
        Utils.verifyIsNotEmpty(name, "name"); //$NON-NLS-1$

        for (final NodeTypeDefinition nodeType : getNodeTypeDefinitions()) {
            if (nodeType.getName().equals(name)) {
                return nodeType;
            }
        }

        return null;
    }

    /**
     * @return an unmodifiable collection of all primary and mixin node type definitions (never <code>null</code>)
     */
    public Collection<NodeTypeDefinition> getNodeTypeDefinitions() {
        return Collections.unmodifiableCollection(this.nodeTypes);
    }

    /**
     * @param uri the URI whose prefix is being requested (cannot be <code>null</code> or empty)
     * @return the prefix or <code>null</code> if a namespace mapping was not found
     */
    public String getPrefix( final String uri ) {
        Utils.verifyIsNotEmpty(uri, "uri"); //$NON-NLS-1$

        for (final NamespaceMapping namespace : this.namespaces) {
            if (namespace.getUri().equals(uri)) {
                return namespace.getPrefix();
            }
        }

        return null;
    }

    /**
     * @return the registered prefixes (never <code>null</code>)
     */
    public Collection<String> getPrefixes() {
        final Collection<String> prefixes = new ArrayList<String>(this.namespaces.size());

        for (final NamespaceMapping namespace : this.namespaces) {
            prefixes.add(namespace.getPrefix());
        }

        return prefixes;
    }

    /**
     * @param nodeTypeDefinitionName the name of the node type definition whose properties are being requested (cannot be
     *            <code>null</code> or empty)
     * @param includeInherited indicates if inherited properties should be included
     * @return the properties (never <code>null</code>)
     */
    public Collection<PropertyDefinition> getPropertyDefinitions( final String nodeTypeDefinitionName,
                                                                  final boolean includeInherited ) {
        final NodeTypeDefinition nodeType = getNodeTypeDefinition(nodeTypeDefinitionName);

        if (nodeType == null) {
            return Collections.emptyList();
        }

        final Collection<PropertyDefinition> properties = new ArrayList<PropertyDefinition>(nodeType.getPropertyDefinitions());

        if (includeInherited) {
            for (final QualifiedName superType : nodeType.getSupertypes()) {
                final NodeTypeDefinition superTypeNodeType = getNodeTypeDefinition(superType.get());

                if (superTypeNodeType != null) {
                    properties.addAll(getPropertyDefinitions(superTypeNodeType.getName(), true));
                }
            }
        }

        return properties;
    }

    /**
     * @param prefix the prefix whose URI is being requested (cannot be <code>null</code> or empty)
     * @return the URI or <code>null</code> if the namespace mapping does not exist
     */
    public String getUri( final String prefix ) {
        Utils.verifyIsNotEmpty(prefix, "prefix"); //$NON-NLS-1$

        for (final NamespaceMapping namespace : this.namespaces) {
            if (namespace.getPrefix().equals(prefix)) {
                return namespace.getUri();
            }
        }

        return null;
    }

    /**
     * @return the registered prefixes (never <code>null</code>)
     */
    public Collection<String> getUris() {
        final Collection<String> uris = new ArrayList<String>(this.namespaces.size());

        for (final NamespaceMapping namespace : this.namespaces) {
            uris.add(namespace.getUri());
        }

        return uris;
    }

    /**
     * @param namespaceMapping the namespace mapping being checked (cannot be <code>null</code>)
     * @return <code>true</code> if a built-in namespace mapping
     */
    public boolean isBuiltIn( final NamespaceMapping namespaceMapping ) {
        Utils.verifyIsNotNull(namespaceMapping, "namespaceMapping"); //$NON-NLS-1$
        final NamespaceMapping builtIn = getNamespaceMapping(namespaceMapping.getPrefix());
        return ((builtIn != null) && builtIn.equals(namespaceMapping));
    }

    /**
     * @param prefix the prefix being checked (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the prefix matches one of a built-in namespace mapping
     */
    public boolean isBuiltInNamespacePrefix( final String prefix ) {
        Utils.verifyIsNotNull(prefix, "prefix"); //$NON-NLS-1$
        return (getUri(prefix) != null);
    }

    /**
     * @param uri the URI being checked (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the URI matches one of a built-in namespace mapping
     */
    public boolean isBuiltInNamespaceUri( final String uri ) {
        Utils.verifyIsNotNull(uri, "uri"); //$NON-NLS-1$
        return (getPrefix(uri) != null);
    }
}
