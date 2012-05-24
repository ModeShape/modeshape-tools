/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import org.jboss.tools.modeshape.jcr.cnd.CndElement;

/**
 * A name that may or may not have a qualifier.
 */
public class QualifiedName implements CndElement, Comparable<QualifiedName> {

    /**
     * The character that separates the qualifier and the local name. Value is {@value} ;
     */
    public static final char DELIM = ':';

    /**
     * A string representation of the {@link #DELIM delimiter character}.
     */
    public static final String DELIM_STRING = Character.toString(DELIM);

    /**
     * @param qualifiedName the qualified name being parsed (can be <code>null</code> or empty)
     * @return the qualified name (never <code>null</code>)
     */
    public static QualifiedName parse( final String qualifiedName ) {
        String qualifier = Utils.EMPTY_STRING;
        String name = Utils.EMPTY_STRING;

        if (!Utils.isEmpty(qualifiedName)) {
            final int index = qualifiedName.indexOf(DELIM_STRING);

            if ((index == -1) || (qualifiedName.length() == 1)) {
                name = qualifiedName;
            } else {
                qualifier = qualifiedName.substring(0, index);

                if ((index + 1) <= qualifiedName.length()) {
                    name = qualifiedName.substring(index + 1);
                }
            }
        }

        return new QualifiedName(qualifier, name);
    }

    /**
     * The part of the name that comes after the delimiter (never <code>null</code>).
     */
    private final LocalName name;

    /**
     * The part of the name that comes before the delimiter (never <code>null</code>).
     */
    private final LocalName qualifier;

    /**
     * Constructs a qualified name with an empty qualifier and an empty unqualified name.
     */
    public QualifiedName() {
        this.qualifier = new LocalName();
        this.name = new LocalName();
    }

    /**
     * @param initialQualifier the local name that is the qualifier (can be <code>null</code> or empty)
     * @param initialUnqualifiedName the local name without a qualifier (can be <code>null</code> or empty)
     */
    public QualifiedName( final String initialQualifier,
                          final String initialUnqualifiedName ) {
        this();
        this.qualifier.set(initialQualifier);
        this.name.set(initialUnqualifiedName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( final QualifiedName that ) {
        if (equals(that)) {
            return 0;
        }

        final int qualifierResult = this.qualifier.compareTo(that.qualifier);
        final int nameResult = this.name.compareTo(that.name);

        if (qualifierResult < 0) {
            // less than qualifier and less than name
            if (nameResult < 0) {
                return -1000;
            }

            // less than qualifier and equal to name
            if (nameResult == 0) {
                return -100;
            }

            // less than qualifier and greater than name
            return -10;
        }

        if (qualifierResult == 0) {
            // equal to qualifier and less than name
            if (nameResult < 0) {
                return -1;
            }

            // equal to qualifier and equal to name
            if (nameResult == 0) {
                return 0;
            }

            // equal to qualifier and greater than name
            return 1;
        }

        // greater than qualifier and less than name
        if (nameResult < 0) {
            return 10;
        }

        // greater than qualifier and equal to name
        if (nameResult == 0) {
            return 100;
        }

        // greater than qualifier and greater than name
        return 1000;
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

        final QualifiedName that = (QualifiedName)obj;
        return (this.qualifier.equals(that.qualifier) && this.name.equals(that.name));
    }

    /**
     * If there is no qualifier, just the unqualified name is returned (no preceding delimiter). If there is only a qualifier, the
     * qualifier and the delimiter is returned. If there is neither a qualifier or an unqualified name, an empty string is returned.
     * 
     * @return the unqualified name (never <code>null</code> but can be empty)
     */
    public String get() {
        final StringBuilder builder = new StringBuilder();
        final String qualifierString = ((getQualifier() == null) ? Utils.EMPTY_STRING : getQualifier());
        final String nameString = ((getUnqualifiedName() == null) ? Utils.EMPTY_STRING : getUnqualifiedName());

        if (!Utils.isEmpty(qualifierString)) {
            builder.append(qualifierString);
            builder.append(DELIM);
        }

        if (!Utils.isEmpty(nameString)) {
            builder.append(nameString);
        }

        return builder.toString();
    }

    /**
     * @return the qualifier part (can be <code>null</code> or empty)
     */
    public String getQualifier() {
        return this.qualifier.get();
    }

    /**
     * @return the unqualified name part (can be <code>null</code> or empty)
     */
    public String getUnqualifiedName() {
        return this.name.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.qualifier, this.name);
    }

    /**
     * @param newQualifiedName the proposed new qualified name (can be <code>null</code> or empty)
     * @return <code>true</code> if either the qualifier or unqualified name was changed
     */
    public boolean set( final String newQualifiedName ) {
        final QualifiedName temp = QualifiedName.parse(newQualifiedName);
        final boolean qualifierChanged = setQualifier(temp.getQualifier());
        final boolean nameChanged = setUnqualifiedName(temp.getUnqualifiedName());
        return (qualifierChanged || nameChanged);
    }

    /**
     * @param newQualifier the proposed new qualifier (can be <code>null</code> or empty)
     * @return <code>true</code> if the qualifier part was changed
     */
    public boolean setQualifier( final String newQualifier ) {
        return this.qualifier.set(newQualifier);
    }

    /**
     * @param newUnqualifiedName the proposed new unqualified name (can be <code>null</code> or empty)
     * @return <code>true</code> if the unqualified name part was changed
     */
    public boolean setUnqualifiedName( final String newUnqualifiedName ) {
        return this.name.set(newUnqualifiedName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( final NotationType notationType ) {
        StringBuilder builder = new StringBuilder();
        
        if (!Utils.isEmpty(this.qualifier.get())) {
            builder.append(this.qualifier.toCndNotation(notationType));
            builder.append(DELIM);
        }

        if (!Utils.isEmpty(this.name.get())) {
            builder.append(this.name.toCndNotation(notationType));
        }

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return get();
    }
}
