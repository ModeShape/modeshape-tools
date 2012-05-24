/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * Property attributes of a node type definitions.
 */
public class NodeTypeAttributes implements CndElement {

    private Mixin mixin;

    private Abstract notConcrete;

    private Orderable orderable;

    private PrimaryItem primaryItem;

    private Queryable queryable;

    /**
     * Constructs a node type definition's attribute collection all set to there default values.
     */
    public NodeTypeAttributes() {
        this.orderable = new Orderable();
        this.mixin = new Mixin();
        this.notConcrete = new Abstract();
        this.primaryItem = new PrimaryItem();
        this.queryable = new Queryable();
    }

    /**
     * @param initialOrderable the initial orderable value (can be <code>null</code>)
     * @param initialMixin the initial mixin value (can be <code>null</code>)
     * @param initialAbstract the initial abstract value (can be <code>null</code>)
     * @param initialQueryable the initial queryable value (can be <code>null</code>)
     * @param initialPrimaryItem the initial primary item value (can be <code>null</code>)
     */
    public NodeTypeAttributes( final Orderable initialOrderable,
                               final Mixin initialMixin,
                               final Abstract initialAbstract,
                               final Queryable initialQueryable,
                               final PrimaryItem initialPrimaryItem ) {
        this();

        if (!this.orderable.equals(initialOrderable)) {
            this.orderable = initialOrderable;
        }

        if (!this.mixin.equals(initialMixin)) {
            this.mixin = initialMixin;
        }

        if (!this.notConcrete.equals(initialOrderable)) {
            this.notConcrete = initialAbstract;
        }

        if (!this.queryable.equals(initialQueryable)) {
            this.queryable = initialQueryable;
        }

        if (!this.primaryItem.equals(initialPrimaryItem)) {
            this.primaryItem = initialPrimaryItem;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final NodeTypeAttributes that = (NodeTypeAttributes)obj;

        return (this.mixin.equals(that.mixin) && this.notConcrete.equals(that.notConcrete) && this.orderable.equals(that.orderable)
                && this.queryable.equals(that.queryable) && this.primaryItem.equals(that.primaryItem));
    }

    /**
     * @return the abstract attribute (never <code>null</code>)
     */
    public Abstract getAbstract() {
        return this.notConcrete;
    }

    /**
     * @return the mixin attribute (never <code>null</code>)
     */
    public Mixin getMixin() {
        return this.mixin;
    }

    /**
     * @return the orderable attribute (never <code>null</code>)
     */
    public Orderable getOrderable() {
        return this.orderable;
    }

    /**
     * @return the primary item attribute (never <code>null</code>)
     */
    public PrimaryItem getPrimaryItem() {
        return this.primaryItem;
    }

    /**
     * @return the queryable attribute (never <code>null</code>)
     */
    public Queryable getQueryable() {
        return this.queryable;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.mixin, this.notConcrete, this.orderable, this.queryable, this.primaryItem);
    }

    /**
     * @param newState the proposed new state of the abstract attribute (cannot be <code>null</code>)
     * @return <code>true</code> if the state was changed
     */
    public boolean setAbstract( final AttributeState.Value newState ) {
        return this.notConcrete.set(newState);
    }

    /**
     * @param newState the proposed new state of the mixin attribute (cannot be <code>null</code>)
     * @return <code>true</code> if the state was changed
     */
    public boolean setMixin( final AttributeState.Value newState ) {
        return this.mixin.set(newState);
    }

    /**
     * @param newState the proposed new state of the orderable attribute (cannot be <code>null</code>)
     * @return <code>true</code> if the state was changed
     */
    public boolean setOrderable( final AttributeState.Value newState ) {
        return this.orderable.set(newState);
    }

    /**
     * <strong>Can only be used to set to variant state.</strong>
     * 
     * @param newState the proposed new state of the primary item attribute (cannot be <code>null</code>)
     * @return <code>true</code> if the state was changed
     */
    public boolean setPrimaryItem( final AttributeState.Value newState ) {
        return this.primaryItem.set(newState);
    }

    /**
     * @param newPrimaryItem the proposed new value for the primary item (can be <code>null</code> or empty)
     * @return <code>true</code> if primary item was changed
     */
    public boolean setPrimaryItem( final String newPrimaryItem ) {
        return this.primaryItem.setPrimaryItem(newPrimaryItem);
    }

    /**
     * @param newState the proposed new state of the queryable attribute (cannot be <code>null</code>)
     * @return <code>true</code> if the state was changed
     */
    public boolean setQueryable( final AttributeState.Value newState ) {
        return this.queryable.set(newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        final JcrPreferenceStore prefStore = JcrPreferenceStore.get();
        final String DELIM = prefStore.get(JcrPreferenceConstants.CndPreference.NODE_TYPE_DEFINITION_ATTRIBUTES_DELIMITER);
        final StringBuilder builder = new StringBuilder();

        boolean addDelim = Utils.build(builder, false, DELIM, this.orderable.toCndNotation(notationType));

        if (Utils.build(builder, addDelim, DELIM, this.mixin.toCndNotation(notationType))) {
            addDelim = true;
        }

        if (Utils.build(builder, addDelim, DELIM, this.notConcrete.toCndNotation(notationType))) {
            addDelim = true;
        }

        if (Utils.build(builder, addDelim, DELIM, this.queryable.toCndNotation(notationType))) {
            addDelim = true;
        }

        if (Utils.build(builder, addDelim, DELIM, this.primaryItem.toCndNotation(notationType))) {
            addDelim = true;
        }

        return builder.toString().trim();
    }

}
