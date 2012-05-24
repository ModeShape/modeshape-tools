/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import java.util.List;

import org.jboss.tools.modeshape.jcr.Utils;

/**
 * The value constraints of a property definition.
 */
public final class ValueConstraints extends ListAttributeState<String> {

    /**
     * The CND list suffix.
     */
    public static final String NOTATION_PREFIX = "<"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getCndNotationPrefix(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    protected String getCndNotationPrefix( final NotationType notationType ) {
        return NOTATION_PREFIX;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getItemQuoteCharacter()
     */
    @Override
    protected String getItemQuoteCharacter() {
        return Utils.SINGLE_QUOTE;
    }

    /**
     * @return the collection of value constraints (never <code>null</code>)
     */
    public String[] toArray() {
        final List<String> constraints = getSupportedItems();

        if (Utils.isEmpty(constraints)) {
            return Utils.EMPTY_STRING_ARRAY;
        }

        final String[] result = new String[constraints.size()];
        int i = 0;

        for (final String constraint : constraints) {
            result[i++] = constraint;
        }

        return result;
    }

}
