/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.preferences;

import org.modeshape.common.util.CheckArg;

/**
 * A <code>ResourcePattern</code> is a resource name pattern that is used to determine if a resource shoiuld be include in
 * publishing operations. Wildcards (*, ?) are allowed.
 */
public final class ResourcePattern {

    /**
     * The name pattern (may not be <code>null</code>).
     */
    private final String pattern;

    /**
     * Indicates if the patter is enabled. Default is {@value} .
     */
    private boolean enabled;

    /**
     * @param pattern the resource name pattern (may not be <code>null</code> or empty)
     * @param enabled <code>true</code> if the pattern should be enabled
     */
    public ResourcePattern( String pattern,
                            boolean enabled ) {
        CheckArg.isNotEmpty(pattern, "pattern"); //$NON-NLS-1$
        this.pattern = pattern;
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        return this.pattern.equals(((ResourcePattern)obj).pattern);
    }

    /**
     * @return the resource name pattern (never <code>null</code>)
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    /**
     * @return <code>true</code> if pattern is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @param enabled <code>true</code> if pattern should be enabled
     */
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

}
