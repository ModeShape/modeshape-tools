/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.preferences;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.modeshape.common.util.CheckArg;

/**
 * The <code>IgnoredResourcesModel</code> class is used to keep track of ignored resources patterns used for resources publishing
 * operations.
 */
public final class IgnoredResourcesModel {

    /**
     * Delimiter used when creating a single string representative of all patterns. Also used when parsing.
     * 
     * @see #createList()
     * @see #load(String)
     */
    private final static char DELIMITER = '\n';

    /**
     * Combines the ignored resource patterns into a single string. This method is the converse of <code>load</code>.
     * 
     * @param patterns the patterns that will be combined into a string representation (never <code>null</code>)
     * @return the string representation of the specified patterns (never <code>null</code> but can be empty)
     */
    public static String createList( ResourcePattern... patterns ) {
        StringBuilder value = new StringBuilder();

        for (ResourcePattern pattern : patterns) {
            value.append(pattern.getPattern()).append(DELIMITER).append(pattern.isEnabled()).append(DELIMITER);
        }

        return value.toString();
    }

    /**
     * @param stringList a string representation of zero or more patterns (may be <code>null</code> or empty)
     * @return the patterns parsed from the string (never <code>null</code> but can be empty)
     */
    public ResourcePattern[] parseList( String stringList ) {
        if ((stringList != null) && !stringList.equals("")) { //$NON-NLS-1$
            StringTokenizer st = new StringTokenizer(stringList, Character.toString(DELIMITER));
            ResourcePattern[] patterns = new ResourcePattern[(st.countTokens() > 0) ? (st.countTokens() / 2) : 0];
            int i = 0;

            while (st.hasMoreTokens()) {
                String pattern = st.nextToken();
                boolean enabled = Boolean.parseBoolean(st.nextToken());
                patterns[i++] = new ResourcePattern(pattern, enabled);
            }
            
            return patterns;
        }

        return new ResourcePattern[0];
    }

    /**
     * The patterns included in this model (never <code>null</code>).
     */
    private final Set<ResourcePattern> patterns = new HashSet<ResourcePattern>();

    /**
     * Adds the specified pattern if one with that same pattern does not already exist.
     * 
     * @param pattern the pattern being added (may not be <code>null</code>)
     */
    public void addPattern( ResourcePattern pattern ) {
        CheckArg.isNotNull(pattern, "pattern"); //$NON-NLS-1$
        this.patterns.add(pattern);
    }

    /**
     * Combines the ignored resource patterns into a single string. This method is the converse of <code>load</code>.
     * 
     * @return the combined string (never <code>null</code> but can be empty)
     * @see #load(String)
     */
    public String createList() {
        return createList(this.patterns.toArray(new ResourcePattern[this.patterns.size()]));
    }

    /**
     * @return an unmodifiable collection of patterns contained in this model (never <code>null</code>)
     */
    public Collection<ResourcePattern> getPatterns() {
        return Collections.unmodifiableCollection(this.patterns);
    }

    /**
     * Clears the current patterns and loads new patterns by parsing the specified string.
     * 
     * @param stringList a string representation of zero or more patterns (may be <code>null</code> or empty)
     */
    public void load( String stringList ) {
        this.patterns.clear();

        // parse list into new patterns
        ResourcePattern[] temp = parseList(stringList);

        if (temp.length != 0) {
            this.patterns.addAll(Arrays.asList(temp));
        }
    }

    /**
     * @param pattern the pattern being removed if it exists in the model (may not be <code>null</code>)
     */
    public void removePattern( ResourcePattern pattern ) {
        CheckArg.isNotNull(pattern, "pattern"); //$NON-NLS-1$
        this.patterns.remove(pattern);
    }

}
