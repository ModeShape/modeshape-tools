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

/**
 * The child node definition's default type property.
 */
public class DefaultType extends AttributeState {

    /**
     * The CND notation for each notation type.
     */
    public static final String NOTATION = "="; //$NON-NLS-1$

    private final QualifiedName defaultType = new QualifiedName();

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

        if (Utils.isEmpty(this.defaultType.get())) {
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
        return getNotation(NotationType.COMPACT);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompressedCndNotation()
     */
    @Override
    protected String getCompressedCndNotation() {
        return getNotation(NotationType.COMPRESSED);
    }

    /**
     * @return the default type's qualified name (never <code>null</code>)
     */
    public QualifiedName getDefaultType() {
        return this.defaultType;
    }

    /**
     * @return the default type name (can be <code>null</code> or empty)
     */
    public String getDefaultTypeName() {
        final String defaultTypeName = this.defaultType.get();

        // per API return null if it doesn't exist
        if (Utils.isEmpty(defaultTypeName)) {
            return null;
        }

        return defaultTypeName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getLongCndNotation()
     */
    @Override
    protected String getLongCndNotation() {
        return getNotation(NotationType.LONG);
    }

    private String getNotation( final NotationType notationType ) {
        if (isVariant()) {
            return getPrefix(notationType);
        }

        final String defaultType = getDefaultTypeName();

        if (Utils.isEmpty(defaultType)) {
            return Utils.EMPTY_STRING;
        }

        return getPrefix(notationType) + defaultType;
    }

    private String getPrefix( final NotationType notationType ) {
        final String delim = ((NotationType.LONG == notationType) ? Utils.SPACE_STRING : Utils.EMPTY_STRING);
        return (Utils.isEmpty(delim) ? NOTATION : (NOTATION + delim));
    }

    /**
     * {@inheritDoc} <strong>Can only be used to change to a variant state. Use {@link DefaultType#setDefaultType(String)} to set to
     * other states</strong>
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
     * @param newDefaultType the proposed new value for the default type (can be <code>null</code> or empty)
     * @return <code>true</code> if changed
     */
    public boolean setDefaultType( final String newDefaultType ) {
        if (this.defaultType.set(newDefaultType)) {
            if (Utils.isEmpty(newDefaultType) && !isVariant()) {
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
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#toVariantCndNotation(java.lang.String)
     */
    @Override
    protected String toVariantCndNotation( final String cndNotation ) {
        return cndNotation + AttributeState.VARIANT_CHAR;
    }
}