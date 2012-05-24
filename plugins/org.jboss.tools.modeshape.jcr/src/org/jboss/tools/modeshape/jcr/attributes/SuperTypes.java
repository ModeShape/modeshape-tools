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
 * 
 */
public final class SuperTypes extends ListAttributeState<QualifiedName> {

    /**
     * The CND list suffix.
     */
    public static final String NOTATION_PREFIX = ">"; //$NON-NLS-1$

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
     * @return the collection of super types (never <code>null</code>)
     */
    public String[] toArray() {
        final List<QualifiedName> superTypes = getSupportedItems();

        if (Utils.isEmpty(superTypes)) {
            return Utils.EMPTY_STRING_ARRAY;
        }

        final String[] result = new String[superTypes.size()];
        int i = 0;

        for (final QualifiedName superType : superTypes) {
            result[i++] = superType.get();
        }

        return result;
    }

}
