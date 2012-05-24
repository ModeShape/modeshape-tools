/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;

/**
 * The type property of a property definition.
 */
public enum PropertyType implements CndElement {

    /**
     * The property value is a UTF-8 encoded string.
     */
    BINARY(javax.jcr.PropertyType.BINARY),

    /**
     * The property value is any string that can be converted via {@link Boolean#valueOf(String)}, such as “true”, “false”, “0”,
     * “1”.
     */
    BOOLEAN(javax.jcr.PropertyType.BOOLEAN),

    /**
     * The property value is a valid date in ISO 8601:2000-compliant format: sYYYY-MM-DDThh:mm:ss.sssTZD.
     */
    DATE(javax.jcr.PropertyType.DATE),

    /**
     * The property value is a decimal number.
     */
    DECIMAL(javax.jcr.PropertyType.DECIMAL),

    /**
     * The property value is a double number.
     */
    DOUBLE(javax.jcr.PropertyType.DOUBLE),

    /**
     * The property value is a long number.
     */
    LONG(javax.jcr.PropertyType.LONG),

    /**
     * The property value is a valid JCR local name, fully qualified with namespace prefix.
     */
    NAME(javax.jcr.PropertyType.NAME),

    /**
     * The property value is a valid JCR path whose namespace prefixes are all registered correctly. The path does not have to point
     * to an existing node.
     */
    PATH(javax.jcr.PropertyType.PATH),

    /**
     * The property value is a valid JCR identifier.
     */
    REFERENCE(javax.jcr.PropertyType.REFERENCE),

    /**
     * The property value is a string.
     */
    STRING(javax.jcr.PropertyType.STRING),

    /**
     * The property value is any string. The value is automatically converted to the appropriate type of the property when created.
     */
    UNDEFINED(javax.jcr.PropertyType.UNDEFINED),

    /**
     * The property value is a URI string.
     */
    URI(javax.jcr.PropertyType.URI),

    /**
     * The property value is a UTF-8 encoded string.
     */
    VARIANT(-1),

    /**
     * The property value is a JCR identifier.
     */
    WEAKREFERENCE(javax.jcr.PropertyType.WEAKREFERENCE);

    /**
     * The default property type. Defaults to {@value PropertyType#STRING}.
     */
    public static final PropertyType DEFAULT_VALUE = PropertyType.STRING;

    /**
     * The value prefixing the CND notation of the type.
     */
    public static final String NOTATION_PREFIX = "("; //$NON-NLS-1$

    /**
     * The value suffixing the CND notation of the type.
     */
    public static final String NOTATION_SUFFIX = ")"; //$NON-NLS-1$

    /**
     * The shortened value for the {@link #UNDEFINED} type.
     */
    public static final String UNDEFINED_ADDITIONAL_NOTATION = "*"; //$NON-NLS-1$

    /**
     * @param cndNotation the CND notation of the property type being requested (cannot be <code>null</code> or empty)
     * @return the property type (never <code>null</code>)
     */
    public static PropertyType find( final String cndNotation ) {
        Utils.verifyIsNotEmpty(cndNotation, "cndNotation"); //$NON-NLS-1$ 

        if (UNDEFINED_ADDITIONAL_NOTATION.equals(cndNotation)) {
            return UNDEFINED;
        }

        for (final PropertyType type : PropertyType.values()) {
            if (PropertyType.VARIANT == type) {
                continue;
            }

            if (type.toString().equalsIgnoreCase(cndNotation)) {
                return type;
            }
        }

        throw new IllegalArgumentException(NLS.bind(Messages.invalidFindRequest, cndNotation));
    }

    /**
     * @param propertyType the {@link javax.jcr.PropertyType} of the type being requested
     * @return the property type (never <code>null</code>
     * @throws IllegalArgumentException if an invalid input value
     */
    public static PropertyType findUsingJcrValue( final int propertyType ) {
        for (final PropertyType type : PropertyType.values()) {
            if (type.asJcrValue() == propertyType) {
                return type;
            }
        }

        throw new IllegalArgumentException(NLS.bind(Messages.invalidFindUsingJcrValueRequest, propertyType));
    }

    /**
     * @return a collection of all the valid CND notations (never <code>null</code> or empty)
     */
    public static String[] validValues() {
        final PropertyType[] allTypes = PropertyType.values();
        // add one for additional undefined notation added later but subtract one for variant
        final String[] result = new String[allTypes.length];
        int i = 0;

        for (final PropertyType type : allTypes) {
            if (type != VARIANT) {
                result[i++] = type.toString();
            }
        }

        result[i] = UNDEFINED_ADDITIONAL_NOTATION;
        return result;
    }

    private final int jcrValue;

    private PropertyType( final int propertyType ) {
        this.jcrValue = propertyType;
    }

    /**
     * If variant, will return <code>-1</code> which is not a valid {@link javax.jcr.PropertyType}.
     * 
     * @return the {@link javax.jcr.PropertyType} of the type
     */
    public int asJcrValue() {
        return this.jcrValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        final StringBuilder builder = new StringBuilder(NOTATION_PREFIX);

        if (this == VARIANT) {
            builder.append(AttributeState.VARIANT_CHAR);
        } else {
            if ((this == UNDEFINED) && (NotationType.LONG != notationType)) {
                builder.append(UNDEFINED_ADDITIONAL_NOTATION);
            } else {
                builder.append(toString());
            }
        }

        return builder.append(NOTATION_SUFFIX).toString();
    }
}