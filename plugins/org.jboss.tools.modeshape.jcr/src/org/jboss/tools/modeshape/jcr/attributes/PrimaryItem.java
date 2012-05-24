/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;

/**
 * The primary item attribute used by node type definitions. This primary item may be a child node or a property.
 */
public class PrimaryItem extends AttributeState {

    /**
     * The CND notation for each notation type.
     */
    public static final String[] NOTATION = new String[] { "primaryitem", "!", "!" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private final QualifiedName primaryItem = new QualifiedName();

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final PrimaryItem that = (PrimaryItem)obj;
        return (super.equals(obj) && getPrimaryItem().equals(that.getPrimaryItem()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#get()
     */
    @Override
    public Value get() {
        final Value state = super.get();

        if (state == Value.VARIANT) {
            return Value.VARIANT;
        }

        if (Utils.isEmpty(this.primaryItem.get())) {
            return Value.IS_NOT;
        }

        return Value.IS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompactCndNotation()
     */
    @Override
    protected String getCompactCndNotation() {
        return NOTATION[CndElement.NotationType.COMPACT_INDEX];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompressedCndNotation()
     */
    @Override
    protected String getCompressedCndNotation() {
        return NOTATION[CndElement.NotationType.COMPRESSED_INDEX];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getLongCndNotation()
     */
    @Override
    protected String getLongCndNotation() {
        return NOTATION[CndElement.NotationType.LONG_INDEX];
    }

    /**
     * @return the primary item (never <code>null</code>)
     */
    public QualifiedName getPrimaryItem() {
        return this.primaryItem;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(super.hashCode(), this.primaryItem);
    }

    /**
     * {@inheritDoc} Only can be used to set to variant state.
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#set(org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value)
     */
    @Override
    public boolean set( final Value newState ) {
        if (newState == Value.VARIANT) {
            if (super.set(Value.VARIANT)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param newPrimaryItem the proposed new primary item (can be <code>null</code> or empty)
     * @return <code>true</code> if the primary item was changed
     */
    public boolean setPrimaryItem( final String newPrimaryItem ) {
        if (this.primaryItem.set(newPrimaryItem)) {
            if (Utils.isEmpty(this.primaryItem.get()) && !isVariant()) {
                super.set(Value.IS_NOT);
            } else {
                super.set(Value.IS);
            }

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        String notation = super.toCndNotation(notationType);

        if (!isVariant() && is()) {
            notation += ' ' + this.primaryItem.toCndNotation(notationType);
        }

        return notation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#toVariantCndNotation(java.lang.String)
     */
    @Override
    protected String toVariantCndNotation( final String cndNotation ) {
        return cndNotation + ' ' + AttributeState.VARIANT_CHAR;
    }
}