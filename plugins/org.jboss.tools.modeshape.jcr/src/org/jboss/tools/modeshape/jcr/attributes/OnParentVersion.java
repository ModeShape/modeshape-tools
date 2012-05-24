/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import javax.jcr.version.OnParentVersionAction;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;

/**
 * The primary item attribute used by child node definitions and property definitions.
 */
public enum OnParentVersion implements CndElement {

    /**
     * Upon check-in, a VersionException will be thrown as the creation of a version is prohibited.
     */
    ABORT(OnParentVersionAction.ABORT),

    /**
     * Upon check-in, the new version will contain this property. However, when the node is restored from a previous node, the value
     * of this property is NOT being restored.
     */
    COMPUTE(OnParentVersionAction.COMPUTE),

    /**
     * Upon check-in, the value of the property will be copied to the new version.
     */
    COPY(OnParentVersionAction.COPY),

    /**
     * Upon check-in, the version entry will not contain this property, i.e. it is not versioned.
     */
    IGNORE(OnParentVersionAction.IGNORE),

    /**
     * Upon check-in, a new property with the same name will be created and re-initialized with any default values specified or as
     * empty. Essentially, the property is re-set when a new version of the node is created.
     */
    INITIALIZE(OnParentVersionAction.INITIALIZE),

    /**
     * Indicates that OPV is a variant. Cannot be used with {@link #find(String)}.
     */
    VARIANT(-1),

    /**
     * Upon check-in, the value of the property will be copied to the new version.
     */
    VERSION(OnParentVersionAction.VERSION);

    /**
     * The default value. Value is {@value} .
     */
    public static OnParentVersion DEFAULT_VALUE = COPY;

    /**
     * @param notation the CND representation of the OPV (cannot be <code>null</code> or empty)
     * @return the OPV (never <code>null</code>)
     * @throws IllegalArgumentException if the <code>jcrValue</code> is not valid
     */
    public static OnParentVersion find( final String notation ) {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv.toCndNotation(NotationType.LONG).equalsIgnoreCase(notation)
                    || opv.toCndNotation(NotationType.COMPRESSED).equalsIgnoreCase(notation)
                    || opv.toCndNotation(NotationType.COMPACT).equalsIgnoreCase(notation)) {
                return opv;
            }
        }

        throw new IllegalArgumentException(NLS.bind(Messages.invalidFindRequest, notation));
    }

    /**
     * @param jcrValue the integer representation
     * @return the OPV (never <code>null</code>)
     * @throws IllegalArgumentException if the <code>jcrValue</code> is not valid
     */
    public static OnParentVersion findUsingJcrValue( final int jcrValue ) {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv.asJcrValue() == jcrValue) {
                return opv;
            }
        }

        throw new IllegalArgumentException(NLS.bind(Messages.invalidFindUsingJcrValueRequest, jcrValue));
    }

    /**
     * @return a collection of all valid CND notation values (never <code>null</code> or empty)
     */
    public static String[] toArray() {
        final OnParentVersion[] allOpv = OnParentVersion.values();
        final String[] notations = new String[allOpv.length];
        int i = 0;

        for (final OnParentVersion opv : allOpv) {
            notations[i++] = opv.toCndNotation(NotationType.LONG);
        }

        return notations;
    }

    private final int jcrValue;

    private OnParentVersion( final int jcrValue ) {
        this.jcrValue = jcrValue;
    }

    /**
     * @return the JCR integer representation
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
        if ((this == DEFAULT_VALUE) && (NotationType.LONG != notationType)) {
            return Utils.EMPTY_STRING;
        }

        if (this == VARIANT) {
            return "OPV?"; //$NON-NLS-1$
        }

        return super.toString();
    }
    //
    // /**
    // * {@inheritDoc}
    // *
    // * @see java.lang.Enum#toString()
    // */
    // @Override
    // public String toString() {
    // return OnParentVersionAction.nameFromValue(asJcrValue());
    // }
}