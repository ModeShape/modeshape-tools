/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinitionTemplate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value;
import org.jboss.tools.modeshape.jcr.attributes.DefaultValues;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
import org.jboss.tools.modeshape.jcr.attributes.PropertyAttributes;
import org.jboss.tools.modeshape.jcr.attributes.PropertyType;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.jboss.tools.modeshape.jcr.attributes.ValueConstraints;
import org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * The <code>PropertyDefinition</code> class represents node type property definition.
 */
public class PropertyDefinition implements ItemDefinition, PropertyDefinitionTemplate {

    /**
     * The prefix used in CND notation before the property definition.
     */
    public static final String NOTATION_PREFIX = "-"; //$NON-NLS-1$

    /**
     * Name for a residual property definition.
     */
    public static final String RESIDUAL_PROPERTY_NAME = "*"; //$NON-NLS-1$

    /**
     * @param propertyBeingCopied the property definition being copied (cannot be <code>null</code>)
     * @param ownerProvider the item owner provider that owns this property (cannot be <code>null</code>)
     * @return the copy (never <code>null</code>)
     */
    public static final PropertyDefinition copy( final PropertyDefinition propertyBeingCopied,
                                                 final ItemOwnerProvider ownerProvider ) {
        final PropertyDefinition copy = new PropertyDefinition(ownerProvider);

        // name
        copy.setName(propertyBeingCopied.getName());

        // comment
        copy.comment = propertyBeingCopied.comment;

        // attributes
        copy.attributes.getAutocreated().set(propertyBeingCopied.attributes.getAutocreated().get());
        copy.attributes.getMandatory().set(propertyBeingCopied.attributes.getMandatory().get());
        copy.attributes.getMultiple().set(propertyBeingCopied.attributes.getMultiple().get());
        copy.attributes.getNoFullText().set(propertyBeingCopied.attributes.getNoFullText().get());
        copy.attributes.getNoQueryOrder().set(propertyBeingCopied.attributes.getNoQueryOrder().get());
        copy.attributes.getProtected().set(propertyBeingCopied.attributes.getProtected().get());
        copy.attributes.setOnParentVersion(propertyBeingCopied.attributes.getOnParentVersion());
        copy.attributes.getQueryOps().set(propertyBeingCopied.attributes.getQueryOps().get());
        copy.setAvailableQueryOperators(propertyBeingCopied.getAvailableQueryOperators());

        // default values
        copy.defaultValues.set(propertyBeingCopied.defaultValues.get());

        for (final String defaultValue : propertyBeingCopied.getDefaultValuesAsStrings()) {
            copy.defaultValues.add(defaultValue);
        }

        // required type
        copy.type = propertyBeingCopied.type;

        // value constraints
        copy.valueConstraints.set(propertyBeingCopied.valueConstraints.get());

        for (final String constraint : propertyBeingCopied.getValueConstraints()) {
            copy.valueConstraints.add(constraint);
        }

        return copy;
    }

    /**
     * The property attributes (never <code>null</code>).
     */
    private final PropertyAttributes attributes;

    /**
     * An optional comment (can be <code>null</code> or empty).
     */
    private String comment;

    /**
     * The property default values (never <code>null</code>).
     */
    private final DefaultValues defaultValues;

    /**
     * The registered property change listeners (never <code>null</code>).
     */
    private final CopyOnWriteArrayList<PropertyChangeListener> listeners;

    /**
     * The property identifier (can be <code>null</code> or empty).
     */
    private final QualifiedName name;

    /**
     * An owner provider for this property (never <code>null</code>).
     */
    private final ItemOwnerProvider ownerProvider;

    /**
     * The property type (never <code>null</code>).
     */
    private PropertyType type;

    /**
     * The property value constraints (never <code>null</code>).
     */
    private final ValueConstraints valueConstraints;

    /**
     * Constructs an instance with a default type of {@link PropertyType#STRING}.
     * 
     * @param ownerProvider the item owner provider that owns this property (cannot be <code>null</code>)
     */
    public PropertyDefinition( final ItemOwnerProvider ownerProvider ) {
        Utils.verifyIsNotNull(ownerProvider, "ownerProvider"); //$NON-NLS-1$

        this.ownerProvider = ownerProvider;
        this.type = PropertyType.DEFAULT_VALUE;
        this.name = new QualifiedName();
        this.attributes = new PropertyAttributes();
        this.defaultValues = new DefaultValues();
        this.valueConstraints = new ValueConstraints();
        this.listeners = new CopyOnWriteArrayList<PropertyChangeListener>();
    }

    /**
     * If added, broadcasts a {@link PropertyChangeEvent} with an old value of <code>null</code> and a new value equal to
     * <code>defaultValueBeingAdded</code>.
     * 
     * @param defaultValueBeingAdded the default value being added (cannot be <code>null</code>)
     * @return <code>true</code> if added
     */
    public boolean addDefaultValue( final String defaultValueBeingAdded ) {
        if (this.defaultValues.add(defaultValueBeingAdded)) {
            notifyChangeListeners(PropertyName.DEFAULT_VALUES, null, defaultValueBeingAdded);
            return true; // added
        }

        return false; // not added
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
     * @param operator the query operator being added (cannot be <code>null</code>)
     * @return <code>true</code> if added
     */
    public boolean addQueryOperator( final QueryOperator operator ) {
        if (this.attributes.getQueryOps().add(operator)) {
            notifyChangeListeners(PropertyName.QUERY_OPS, null, operator);
            return true; // added
        }

        return false; // not added
    }

    /**
     * If added, broadcasts a {@link PropertyChangeEvent} with an old value of <code>null</code> and a new value equal to
     * <code>valueConstraintBeingAdded</code>.
     * 
     * @param valueConstraintBeingAdded the value constraint being added (cannot be <code>null</code>)
     * @return <code>true</code> if added
     */
    public boolean addValueConstraint( final String valueConstraintBeingAdded ) {
        if (this.valueConstraints.add(valueConstraintBeingAdded)) {
            notifyChangeListeners(PropertyName.VALUE_CONSTRAINTS, null, valueConstraintBeingAdded);
            return true; // added
        }

        return false; // not added
    }

    /**
     * If state was changed, a property change event is broadcast to all registered listeners. Can be used to change the
     * autocreated, default values, mandatory, multiple, no full text, no query order, on parent version variant, protected, query
     * operators, property type, and value constraints properties.
     * 
     * @param propertyName the property whose attribute state is being changed (cannot be <code>null</code>)
     * @param newState the new attribute state (cannot be <code>null</code>)
     * @return <code>true</code> if the attribute state was changed
     */
    public boolean changeState( final PropertyName propertyName,
                                final AttributeState.Value newState ) {
        Utils.verifyIsNotNull(propertyName, "propertyName"); //$NON-NLS-1$
        Utils.verifyIsNotNull(newState, "newState"); //$NON-NLS-1$

        Object oldValue = null;
        Object newValue = newState;
        boolean changed = false;

        if (PropertyName.AUTOCREATED == propertyName) {
            oldValue = this.attributes.getAutocreated().get();
            changed = this.attributes.getAutocreated().set(newState);
        } else if (PropertyName.DEFAULT_VALUES == propertyName) {
            oldValue = this.defaultValues.get();
            changed = this.defaultValues.set(newState);
        } else if (PropertyName.MANDATORY == propertyName) {
            oldValue = this.attributes.getMandatory().get();
            changed = this.attributes.getMandatory().set(newState);
        } else if (PropertyName.MULTIPLE == propertyName) {
            oldValue = this.attributes.getMultiple().get();
            changed = this.attributes.getMultiple().set(newState);
        } else if (PropertyName.NO_FULL_TEXT == propertyName) {
            oldValue = this.attributes.getNoFullText().get();
            changed = this.attributes.getNoFullText().set(newState);
        } else if (PropertyName.NO_QUERY_ORDER == propertyName) {
            oldValue = this.attributes.getNoQueryOrder().get();
            changed = this.attributes.getNoQueryOrder().set(newState);
        } else if (PropertyName.ON_PARENT_VERSION == propertyName) {
            if (Value.VARIANT == newValue) {
                oldValue = this.attributes.getOnParentVersion();
                newValue = OnParentVersion.VARIANT;
                changed = this.attributes.setOnParentVersion(OnParentVersion.VARIANT);
            }
        } else if (PropertyName.PROTECTED == propertyName) {
            oldValue = this.attributes.getProtected().get();
            changed = this.attributes.getProtected().set(newState);
        } else if (PropertyName.QUERY_OPS == propertyName) {
            oldValue = this.attributes.getQueryOps().get();
            changed = this.attributes.getQueryOps().set(newState);
        } else if (PropertyName.TYPE == propertyName) {
            if (Value.VARIANT == newValue) {
                return setType(PropertyType.VARIANT); // this will fire property change event
            }
        } else if (PropertyName.VALUE_CONSTRAINTS == propertyName) {
            oldValue = this.valueConstraints.get();
            changed = this.valueConstraints.set(newState);
        }

        if (changed) {
            notifyChangeListeners(propertyName, oldValue, newValue);
            return true; // changed state
        }

        return false; // did not change state
    }

    /**
     * If at least one default value was removed, broadcasts a {@link PropertyChangeEvent} with an old value equal to the old
     * default values collection and a new value of <code>null</code>.
     * 
     * @return <code>true</code> if at least one default value was removed
     */
    public boolean clearDefaultValues() {
        final List<String> oldValue = new ArrayList<String>(this.defaultValues.getSupportedItems());

        if (this.defaultValues.clear()) {
            notifyChangeListeners(PropertyName.DEFAULT_VALUES, oldValue, null);
            return true;
        }

        return false;
    }

    /**
     * If at least one value constraint was removed, broadcasts a {@link PropertyChangeEvent} with an old value equal to the old
     * value constraints collection and a new value of <code>null</code>.
     * 
     * @return <code>true</code> if at least one value constraint was removed
     */
    public boolean clearValueConstraints() {
        final List<String> oldValue = new ArrayList<String>(this.valueConstraints.getSupportedItems());

        if (this.valueConstraints.clear()) {
            notifyChangeListeners(PropertyName.VALUE_CONSTRAINTS, oldValue, null);
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( final ItemDefinition itemDefinition ) {
        return getQualifiedName().compareTo(itemDefinition.getQualifiedName());
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

        final PropertyDefinition that = (PropertyDefinition)obj;

        if (!this.attributes.equals(that.attributes)) {
            return false;
        }

        if (!Utils.equals(getName(), that.getName())) {
            return false;
        }

        if (this.type != that.type) {
            return false;
        }

        if (!this.defaultValues.equals(that.defaultValues)) {
            return false;
        }

        if (!Utils.equals(getDeclaringNodeTypeDefinitionName(), that.getDeclaringNodeTypeDefinitionName())) {
            return false;
        }

        if (!this.valueConstraints.equals(that.valueConstraints)) {
            return false;
        }

        return Utils.equals(this.comment, that.comment);
    }

    /**
     * @param notationType the notation type being requested (cannot be <code>null</code>)
     * @return the CND notation (never <code>null</code>)
     */
    public String getAttributesCndNotation( final NotationType notationType ) {
        final String cndNotation = this.attributes.toCndNotation(notationType);

        if (cndNotation == null) {
            return Utils.EMPTY_STRING;
        }

        return cndNotation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#getAvailableQueryOperators()
     */
    @Override
    public String[] getAvailableQueryOperators() {
        return this.attributes.getQueryOps().toArray();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement#getComment()
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.ItemDefinition#getDeclaringNodeType()
     * @throws UnsupportedOperationException if method is called
     */
    @Override
    public NodeType getDeclaringNodeType() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ItemDefinition#getDeclaringNodeTypeDefinitionName()
     */
    @Override
    public QualifiedName getDeclaringNodeTypeDefinitionName() {
        return this.ownerProvider.getOwnerQualifiedName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#getDefaultValues()
     */
    @Override
    public javax.jcr.Value[] getDefaultValues() {
        return this.defaultValues.asJcrValues();
    }

    /**
     * @return the default values (never <code>null</code>)
     */
    public List<String> getDefaultValuesAsStrings() {
        return this.defaultValues.getSupportedItems();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.ItemDefinition#getName()
     */
    @Override
    public String getName() {
        // API requires null to be returned
        if (Utils.isEmpty(this.name.get())) {
            return null;
        }

        return this.name.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.ItemDefinition#getOnParentVersion()
     */
    @Override
    public int getOnParentVersion() {
        return this.attributes.getOnParentVersion().asJcrValue();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ItemDefinition#getQualifiedName()
     */
    @Override
    public QualifiedName getQualifiedName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#getRequiredType()
     */
    @Override
    public int getRequiredType() {
        return getType().asJcrValue();
    }

    /**
     * Can be used to find the attribute state of the autocreated, default values, mandatory, multiple, no full text, no query
     * order, on parent version, protected, query operators, property type, and value constraints properties.
     * 
     * @param propertyName the property whose attribute state is being requested (cannot be <code>null</code>)
     * @return the attribute state (never <code>null</code>)
     * @throws IllegalArgumentException if a property that does not have an attribute state is specified
     */
    public Value getState( final PropertyName propertyName ) {
        Utils.verifyIsNotNull(propertyName, "propertyName"); //$NON-NLS-1$

        if (PropertyName.AUTOCREATED == propertyName) {
            return this.attributes.getAutocreated().get();
        }

        if (PropertyName.DEFAULT_VALUES == propertyName) {
            return this.defaultValues.get();
        }

        if (PropertyName.MANDATORY == propertyName) {
            return this.attributes.getMandatory().get();
        }

        if (PropertyName.MULTIPLE == propertyName) {
            return this.attributes.getMultiple().get();
        }

        if (PropertyName.NO_FULL_TEXT == propertyName) {
            return this.attributes.getNoFullText().get();
        }

        if (PropertyName.NO_QUERY_ORDER == propertyName) {
            return this.attributes.getNoQueryOrder().get();
        }

        if (PropertyName.ON_PARENT_VERSION == propertyName) {
            if (isVariant(PropertyName.ON_PARENT_VERSION)) {
                return Value.VARIANT;
            }

            return Value.IS;
        }

        if (PropertyName.PROTECTED == propertyName) {
            return this.attributes.getProtected().get();
        }

        if (PropertyName.QUERY_OPS == propertyName) {
            return this.attributes.getQueryOps().get();
        }

        if (PropertyName.VALUE_CONSTRAINTS == propertyName) {
            return this.valueConstraints.get();
        }

        throw new IllegalArgumentException(NLS.bind(Messages.invalidGetStateRequest, propertyName));
    }

    /**
     * @return the type (never <code>null</code>)
     */
    public PropertyType getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#getValueConstraints()
     */
    @Override
    public String[] getValueConstraints() {
        return this.valueConstraints.toArray();
    }

    /**
     * @param notationType
     * @return the CND notation for the value constraints (never <code>null</code> but can be empty)
     */
    public String getValueConstraintsCndNotation( final NotationType notationType ) {
        return this.valueConstraints.toCndNotation(notationType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.attributes, this.name, this.defaultValues, this.type, this.valueConstraints,
                              getDeclaringNodeTypeDefinitionName(), this.comment);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.ItemDefinition#isAutoCreated()
     */
    @Override
    public boolean isAutoCreated() {
        return this.attributes.getAutocreated().is();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#isFullTextSearchable()
     */
    @Override
    public boolean isFullTextSearchable() {
        return this.attributes.getNoFullText().isNot();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.ItemDefinition#isMandatory()
     */
    @Override
    public boolean isMandatory() {
        return this.attributes.getMandatory().is();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#isMultiple()
     */
    @Override
    public boolean isMultiple() {
        return this.attributes.getMultiple().is();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.ItemDefinition#isProtected()
     */
    @Override
    public boolean isProtected() {
        return this.attributes.getProtected().is();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinition#isQueryOrderable()
     */
    @Override
    public boolean isQueryOrderable() {
        return this.attributes.getNoQueryOrder().isNot();
    }

    /**
     * @param propertyName the property being checked (cannot be <code>null</code>)
     * @return <code>true</code> if property is a variant
     */
    public boolean isVariant( final PropertyName propertyName ) {
        Utils.verifyIsNotNull(propertyName, "propertyName"); //$NON-NLS-1$

        if (PropertyName.ON_PARENT_VERSION == propertyName) {
            return (this.attributes.getOnParentVersion() == OnParentVersion.VARIANT);
        }

        if (PropertyName.TYPE == propertyName) {
            return (this.type == PropertyType.VARIANT);
        }

        return (getState(propertyName) == Value.VARIANT);
    }

    /**
     * @param property the property that was changed (never <code>null</code>)
     * @param oldValue the old value (can be <code>null</code>)
     * @param newValue the new value (can be <code>null</code>)
     */
    private void notifyChangeListeners( final PropertyName property,
                                        final Object oldValue,
                                        final Object newValue ) {
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
     * If default value is removed, broadcasts a {@link PropertyChangeEvent} with an old value of
     * <code>defaultValueBeingRemoved</code> and a new value of <code>null</code>.
     * 
     * @param defaultValueBeingRemoved the default value being removed (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeDefaultValue( final String defaultValueBeingRemoved ) {
        if (this.defaultValues.remove(defaultValueBeingRemoved)) {
            notifyChangeListeners(PropertyName.DEFAULT_VALUES, defaultValueBeingRemoved, null);
            return true; // removed
        }

        return false; // not removed
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
     * @param operator the query operator being removed (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeQueryOperator( final QueryOperator operator ) {
        if (this.attributes.getQueryOps().remove(operator)) {
            notifyChangeListeners(PropertyName.QUERY_OPS, operator, null);
            return true; // removed
        }

        return false; // not removed
    }

    /**
     * If value constraint is removed, broadcasts a {@link PropertyChangeEvent} with an old value of
     * <code>valueConstraintBeingRemoved</code> and a new value of <code>null</code>.
     * 
     * @param valueConstraintBeingRemoved the value constraint being removed (cannot be <code>null</code>)
     * @return <code>true</code> if removed
     */
    public boolean removeValueConstraint( final String valueConstraintBeingRemoved ) {
        if (this.valueConstraints.remove(valueConstraintBeingRemoved)) {
            notifyChangeListeners(PropertyName.VALUE_CONSTRAINTS, valueConstraintBeingRemoved, null);
            return true; // removed
        }

        return false; // not removed
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setAutoCreated(boolean)
     */
    @Override
    public void setAutoCreated( final boolean newAutocreated ) {
        final AttributeState.Value newState = (newAutocreated ? AttributeState.Value.IS : AttributeState.Value.IS_NOT);
        changeState(PropertyName.AUTOCREATED, newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setAvailableQueryOperators(java.lang.String[])
     */
    @Override
    public void setAvailableQueryOperators( final String[] newOperators ) {
        final QueryOperators queryOps = this.attributes.getQueryOps();
        final List<QueryOperator> oldOperators = queryOps.getSupportedItems();
        boolean changed = queryOps.clear();

        if (!Utils.isEmpty(newOperators)) {
            for (final String operator : newOperators) {
                try {
                    if (queryOps.add(operator.trim())) {
                        changed = true;
                    }
                } catch (final Exception e) {
                    if (Platform.isRunning()) {
                        Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                    } else {
                        System.err.print(e.getMessage());
                    }
                }
            }
        }

        if (changed) {
            notifyChangeListeners(PropertyName.QUERY_OPS, oldOperators, queryOps.getSupportedItems());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement#setComment(java.lang.String)
     */
    @Override
    public boolean setComment( String newComment ) {
        if (!Utils.isEmpty(newComment)) {
            newComment = newComment.trim();
        }

        final Object oldValue = this.comment;
        final boolean changed = !Utils.equivalent(this.comment, newComment);

        if (changed) {
            this.comment = newComment;
            notifyChangeListeners(PropertyName.COMMENT, oldValue, newComment);
        }

        return changed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setDefaultValues(javax.jcr.Value[])
     */
    @Override
    public void setDefaultValues( final javax.jcr.Value[] newDefaultValues ) {
        final List<String> items = this.defaultValues.getSupportedItems();
        boolean changed = this.defaultValues.clear();

        if (!Utils.isEmpty(newDefaultValues)) {
            for (final javax.jcr.Value defaultValue : newDefaultValues) {
                try {
                    if (this.defaultValues.add(defaultValue.getString())) {
                        changed = true;
                    }
                } catch (final Exception e) {
                    if (Platform.isRunning()) {
                        Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                    } else {
                        System.err.print(e.getMessage());
                    }
                }
            }
        }

        if (changed) {
            notifyChangeListeners(PropertyName.DEFAULT_VALUES, items, this.defaultValues.getSupportedItems());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setFullTextSearchable(boolean)
     */
    @Override
    public void setFullTextSearchable( final boolean newFullTextSearchable ) {
        final AttributeState.Value newState = (newFullTextSearchable ? AttributeState.Value.IS_NOT : AttributeState.Value.IS);
        changeState(PropertyName.NO_FULL_TEXT, newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setMandatory(boolean)
     */
    @Override
    public void setMandatory( final boolean newMandatory ) {
        final AttributeState.Value newState = (newMandatory ? AttributeState.Value.IS : AttributeState.Value.IS_NOT);
        changeState(PropertyName.MANDATORY, newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setMultiple(boolean)
     */
    @Override
    public void setMultiple( final boolean newMultiple ) {
        final AttributeState.Value newState = (newMultiple ? AttributeState.Value.IS : AttributeState.Value.IS_NOT);
        changeState(PropertyName.MULTIPLE, newState);
    }

    /**
     * @param newName the new node name (can be <code>null</code> or empty)
     */
    @Override
    public void setName( final String newName ) {
        final Object oldValue = getName();

        if (this.name.set(newName)) {
            notifyChangeListeners(PropertyName.NAME, oldValue, newName);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setOnParentVersion(int)
     */
    @Override
    public void setOnParentVersion( final int newOpv ) {
        final OnParentVersion oldValue = this.attributes.getOnParentVersion();

        if (this.attributes.setOnParentVersion(OnParentVersion.findUsingJcrValue(newOpv))) {
            notifyChangeListeners(PropertyName.ON_PARENT_VERSION, oldValue, newOpv);
        }
    }

    /**
     * If changed, a property change event is broadcast to all registered listeners.
     * 
     * @param newOpv the new OPV value (cannot be <code>null</code>)
     * @return <code>true</code> if successfully changed
     */
    public boolean setOnParentVersion( final String newOpv ) {
        final OnParentVersion oldValue = this.attributes.getOnParentVersion();

        if (this.attributes.setOnParentVersion(OnParentVersion.find(newOpv))) {
            notifyChangeListeners(PropertyName.ON_PARENT_VERSION, oldValue, newOpv);
            return true; // value changed
        }

        return false; // value not changed
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setProtected(boolean)
     */
    @Override
    public void setProtected( final boolean newProtected ) {
        final AttributeState.Value newState = (newProtected ? AttributeState.Value.IS : AttributeState.Value.IS_NOT);
        changeState(PropertyName.PROTECTED, newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setQueryOrderable(boolean)
     */
    @Override
    public void setQueryOrderable( final boolean newQueryOrderable ) {
        final AttributeState.Value newState = (newQueryOrderable ? AttributeState.Value.IS_NOT : AttributeState.Value.IS);
        changeState(PropertyName.NO_QUERY_ORDER, newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setRequiredType(int)
     */
    @Override
    public void setRequiredType( final int newPropertyType ) {
        setType(PropertyType.findUsingJcrValue(newPropertyType));
    }

    /**
     * If type is changed, a {@link PropertyChangeEvent} is broadcast.
     * 
     * @param newType the proposed new type (cannot be <code>null</code>)
     * @return <code>true</code> if the type was changed
     */
    public boolean setType( final PropertyType newType ) {
        if (this.type != newType) {
            final PropertyType oldValue = this.type;
            this.type = newType;
            notifyChangeListeners(PropertyName.TYPE, oldValue, this.type);
            return true; // type changed
        }

        return false; // type not changed
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.nodetype.PropertyDefinitionTemplate#setValueConstraints(java.lang.String[])
     */
    @Override
    public void setValueConstraints( final String[] newConstraints ) {
        final List<String> items = this.valueConstraints.getSupportedItems();
        boolean changed = this.valueConstraints.clear();

        if (!Utils.isEmpty(newConstraints)) {
            for (final String constraint : newConstraints) {
                if (this.valueConstraints.add(constraint)) {
                    changed = true;
                }
            }
        }

        if (changed) {
            notifyChangeListeners(PropertyName.VALUE_CONSTRAINTS, items, this.valueConstraints.getSupportedItems());
        }
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
        String indent = Utils.EMPTY_STRING;

        if (NotationType.LONG == notationType) {
            indent = prefStore.get(JcrPreferenceConstants.CndPreference.ELEMENTS_START_DELIMITER);
        }

        { // comment
            if (!Utils.isEmpty(this.comment)) {
                String commentNotation = Utils.EMPTY_STRING;

                if (NotationType.LONG == notationType) {
                    commentNotation += '\n';
                }

                commentNotation += CommentedCndElement.Helper.addCommentCharacters(this.comment, indent) + '\n';

                // add comment above node type
                builder.append(commentNotation);

                if (NotationType.LONG == notationType) {
                    builder.append(indent);
                }
            }
        }

        builder.append(NOTATION_PREFIX);
        builder.append((NotationType.LONG == notationType) ? Utils.SPACE_STRING : Utils.EMPTY_STRING);

        final String DELIM = prefStore.get(JcrPreferenceConstants.CndPreference.PROPERTY_DEFINITION_ATTRIBUTES_DELIMITER);
        builder.append(this.name.toCndNotation(notationType));

        Utils.build(builder, true, DELIM, this.type.toCndNotation(notationType));
        Utils.build(builder, true, DELIM, this.defaultValues.toCndNotation(notationType));
        Utils.build(builder, true, DELIM, this.attributes.toCndNotation(notationType));
        Utils.build(builder, true, DELIM, this.valueConstraints.toCndNotation(notationType));

        return builder.toString();
    }

    /**
     * The property names whose <code>toString()</code> is used in {@link PropertyChangeEvent}s.
     */
    public enum PropertyName {

        /**
         * The autocreated attribute.
         */
        AUTOCREATED,

        /**
         * The comment.
         */
        COMMENT,

        /**
         * A collection of default values.
         */
        DEFAULT_VALUES,

        /**
         * The mandatory attribute.
         */
        MANDATORY,

        /**
         * The multiple attribute.
         */
        MULTIPLE,

        /**
         * The property name.
         */
        NAME,

        /**
         * The does not support full text search attribute.
         */
        NO_FULL_TEXT,

        /**
         * The does not support query order attribute.
         */
        NO_QUERY_ORDER,

        /**
         * The on parent version value.
         */
        ON_PARENT_VERSION,

        /**
         * The protected attribute.
         */
        PROTECTED,

        /**
         * The collection of supported query operators.
         */
        QUERY_OPS,

        /**
         * The property type.
         */
        TYPE,

        /**
         * The collection of property constraints.
         */
        VALUE_CONSTRAINTS;

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return (getClass().getName() + '.' + super.toString());
        }
    }
}
