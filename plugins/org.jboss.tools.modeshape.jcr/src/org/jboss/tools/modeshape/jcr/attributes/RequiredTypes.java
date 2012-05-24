/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import java.util.List;

import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;

/**
 * The required types of a child node definition.
 */
public final class RequiredTypes extends ListAttributeState<QualifiedName> {

    /**
     * The CND list prefix.
     */
    public static final String NOTATION_PREFIX = "("; //$NON-NLS-1$;

    /**
     * The CND list suffix.
     */
    public static final String NOTATION_SUFFIX = ")"; //$NON-NLS-1$

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
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getCndNotationSuffix(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    protected String getCndNotationSuffix( final NotationType notationType ) {
        return NOTATION_SUFFIX;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getPrefixEndDelimiter(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    protected String getPrefixEndDelimiter( NotationType notationType ) {
        return Utils.EMPTY_STRING;
    }
    
    /**
     * @return the collection of required types (never <code>null</code>)
     */
    public String[] toArray() {
        final List<QualifiedName> typeNames = getSupportedItems();

        if (Utils.isEmpty(typeNames)) {
            return Utils.EMPTY_STRING_ARRAY;
        }

        final String[] result = new String[typeNames.size()];
        int i = 0;

        for (final QualifiedName typeName : typeNames) {
            result[i++] = typeName.get();
        }

        return result;
    }
}
