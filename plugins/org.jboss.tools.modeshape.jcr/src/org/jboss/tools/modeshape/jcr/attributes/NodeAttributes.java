/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * The child node definition attribute collection.
 */
public class NodeAttributes implements CndElement {

    private final Autocreated autocreated;

    private final Mandatory mandatory;

    private final Protected notDeletable;

    private OnParentVersion opv;

    private final SameNameSiblings sns;

    /**
     * Constructs node attributes all set to default values.
     */
    public NodeAttributes() {
        this.autocreated = new Autocreated();
        this.mandatory = new Mandatory();
        this.notDeletable = new Protected();
        this.opv = OnParentVersion.DEFAULT_VALUE;
        this.sns = new SameNameSiblings();
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

        final NodeAttributes that = (NodeAttributes)obj;

        return (this.autocreated.equals(that.autocreated) && this.mandatory.equals(that.mandatory)
                && this.notDeletable.equals(that.notDeletable) && (this.opv == that.opv) && this.sns.equals(that.sns));
    }

    /**
     * @return the autocreated attribute (never <code>null</code>)
     */
    public Autocreated getAutocreated() {
        return this.autocreated;
    }

    /**
     * @return the mandatory attribute (never <code>null</code>)
     */
    public Mandatory getMandatory() {
        return this.mandatory;
    }

    /**
     * @return the on parent version attribute (never <code>null</code>)
     */
    public OnParentVersion getOnParentVersion() {
        return this.opv;
    }

    /**
     * @return the protected attribute (never <code>null</code>)
     */
    public Protected getProtected() {
        return this.notDeletable;
    }

    /**
     * @return the same named siblings attribute (never <code>null</code>)
     */
    public SameNameSiblings getSameNameSiblings() {
        return this.sns;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.autocreated, this.mandatory, this.notDeletable, this.opv, this.sns);
    }

    /**
     * @param newState the new value to set the autocreated attribute state to (cannot be <code>null</code>)
     * @return <code>true</code> if the state changed
     */
    public boolean setAutocreated( final Value newState ) {
        return this.autocreated.set(newState);
    }

    /**
     * @param newState the new value to set the mandatory attribute state to (cannot be <code>null</code>)
     * @return <code>true</code> if state changed
     */
    public boolean setMandatory( final Value newState ) {
        return this.mandatory.set(newState);
    }

    /**
     * @param newOpv the new value to set the on parent version attribute to (cannot be <code>null</code>)
     * @return <code>true</code> if changed
     */
    public boolean setOnParentVersion( final OnParentVersion newOpv ) {
        Utils.verifyIsNotNull(newOpv, "newOpv"); //$NON-NLS-1$

        if (this.opv != newOpv) {
            this.opv = newOpv;
            return true;
        }

        return false;
    }

    /**
     * @param newState the new value to set the protected attribute state to (cannot be <code>null</code>)
     * @return <code>true</code> if state changed
     */
    public boolean setProtected( final Value newState ) {
        return this.notDeletable.set(newState);
    }

    /**
     * @param newState the new value to set the same named siblings attribute state to (cannot be <code>null</code>)
     * @return <code>true</code> if state changed
     */
    public boolean setSameNameSibling( final Value newState ) {
        return this.sns.set(newState);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        final JcrPreferenceStore prefStore = JcrPreferenceStore.get();
        final String DELIM = prefStore.get(JcrPreferenceConstants.CndPreference.CHILD_NODE_ATTRIBUTES_DELIMITER);
        final StringBuilder builder = new StringBuilder();

        boolean addDelim = Utils.build(builder, false, DELIM, this.autocreated.toCndNotation(notationType));

        if (Utils.build(builder, addDelim, DELIM, this.mandatory.toCndNotation(notationType))) {
            addDelim = true;
        }

        if (Utils.build(builder, addDelim, DELIM, this.notDeletable.toCndNotation(notationType))) {
            addDelim = true;
        }

        if (Utils.build(builder, addDelim, DELIM, this.opv.toCndNotation(notationType))) {
            addDelim = true;
        }

        if (Utils.build(builder, addDelim, DELIM, this.sns.toCndNotation(notationType))) {
            addDelim = true;
        }

        return builder.toString().trim();
    }
}
