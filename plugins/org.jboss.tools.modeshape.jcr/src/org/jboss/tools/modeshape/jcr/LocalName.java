/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * 
 */
public class LocalName implements CndElement, Comparable<LocalName> {

    private String value;

    /**
     * Constructs an empty name.
     */
    public LocalName() {
        this(Utils.EMPTY_STRING);
    }

    /**
     * @param initialValue the initial name value (can be <code>null</code> or empty)
     */
    public LocalName( final String initialValue ) {
        if (initialValue == null) {
            this.value = Utils.EMPTY_STRING;
        } else {
            this.value = initialValue;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( final LocalName that ) {
        if (equals(that) || Utils.equals(get(), that.get())) {
            return 0;
        }

        // both can't be empty
        if (Utils.isEmpty(this.value)) {
            return -1;
        }

        if (Utils.isEmpty(that.value)) {
            return 1;
        }

        return this.value.compareTo(that.value);
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

        final LocalName that = (LocalName)obj;
        return Utils.equals(this.value, that.value);
    }

    /**
     * @return the name (can be <code>null</code> or empty)
     */
    public String get() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.value);
    }

    /**
     * @param newValue the new name value (can be <code>null</code> or empty)
     * @return <code>true</code> if the name was changed
     */
    public boolean set( String newValue ) {
        if (!Utils.isEmpty(newValue)) {
            newValue = newValue.trim();
        }

        if (!Utils.equals(this.value, newValue)) {
            this.value = newValue;
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
        final StringBuilder builder = new StringBuilder();

        if (Utils.isEmpty(this.value)) {
            return Utils.EMPTY_STRING;
        }

        String quoteString = JcrPreferenceStore.get().get(JcrPreferenceConstants.CndPreference.QUOTE_CHAR);

        if (Utils.isEmpty(quoteString) && !Utils.isEmpty(this.value) && this.value.contains(Utils.SPACE_STRING)) {
            quoteString = Utils.SINGLE_QUOTE;
        }

        builder.append(quoteString);
        builder.append((this.value == null) ? Utils.EMPTY_STRING : this.value);
        builder.append(quoteString);

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (Utils.isEmpty(this.value)) {
            return Utils.EMPTY_STRING;
        }

        return this.value;
    }
}
