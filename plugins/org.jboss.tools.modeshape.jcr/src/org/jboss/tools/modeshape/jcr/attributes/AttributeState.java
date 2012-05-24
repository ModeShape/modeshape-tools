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

/**
 * An attribute will have either a supported, unsupported, or variant state.
 */
public abstract class AttributeState implements CndElement {

    /**
     * The character used in CND notation to indicate the attribute is a variant.
     */
    public static final char VARIANT_CHAR = '?';

    /**
     * The CND variant character as a string.
     * 
     * @see #VARIANT_CHAR
     */
    public static final String VARIANT_STRING = Character.toString(VARIANT_CHAR);

    private Value state;

    /**
     * Constructs a not supported attribute state.
     * 
     * @see Value#IS_NOT
     */
    public AttributeState() {
        this.state = Value.IS_NOT;
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

        final AttributeState that = (AttributeState)obj;
        return (this.state == that.state);
    }

    /**
     * @return the attribute state (never <code>null</code>)
     */
    public Value get() {
        return this.state;
    }

    /**
     * @return the attribute's compact CND notation (can be <code>null</code> or empty)
     */
    protected abstract String getCompactCndNotation();

    /**
     * @return the attribute's compressed CND notation (can be <code>null</code> or empty)
     */
    protected abstract String getCompressedCndNotation();

    /**
     * @return the attribute's long CND notation (can be <code>null</code> or empty)
     */
    protected abstract String getLongCndNotation();

    /**
     * @return <code>true</code> if the CND notation is not empty
     */
    protected boolean hasCndNotation() {
        return !isNot();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    /**
     * @return <code>true</code> if attribute state is {@link Value#IS}.
     */
    public boolean is() {
        return (this.state == Value.IS);
    }

    /**
     * @return <code>true</code> if attribute state is {@link Value#IS_NOT}.
     */
    public boolean isNot() {
        return (this.state == Value.IS_NOT);
    }

    /**
     * @return <code>true</code> if attribute state is {@link Value#VARIANT}.
     */
    public boolean isVariant() {
        return (this.state == Value.VARIANT);
    }

    /**
     * @param newState the proposed new state (cannot be <code>null</code>)
     * @return <code>true</code> if state was changed
     */
    public boolean set( final Value newState ) {
        Utils.verifyIsNotNull(newState, "newState"); //$NON-NLS-1$

        if (this.state != newState) {
            this.state = newState;
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        if (hasCndNotation()) {
            String notation = Utils.EMPTY_STRING;

            if (NotationType.LONG == notationType) {
                notation = getLongCndNotation();
            } else if (NotationType.COMPRESSED == notationType) {
                notation = getCompressedCndNotation();
            } else if (NotationType.COMPACT == notationType) {
                notation = getCompactCndNotation();
            }

            if (isVariant()) {
                return toVariantCndNotation(notation);
            }

            return notation;
        }

        return Utils.EMPTY_STRING;
    }

    /**
     * @param cndNotation the CND notation without the variant indicator (can be <code>null</code> or empty)
     * @return the variant CND notation (never <code>null</code> or empty)
     */
    protected String toVariantCndNotation( final String cndNotation ) {
        if (Utils.isEmpty(cndNotation)) {
            return String.valueOf(AttributeState.VARIANT_CHAR);
        }

        return cndNotation + AttributeState.VARIANT_CHAR;
    }

    /**
     * The attribute state possible values.
     */
    public enum Value {

        /**
         * Indicates the attribute is supported.
         */
        IS,

        /**
         * Indicates the attribute is not supported.
         */
        IS_NOT,

        /**
         * Indicates the attribute is a variant.
         */
        VARIANT
    }
}
