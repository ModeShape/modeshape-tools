/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;


/**
 * The abstract attribute used by node type definitions.
 */
public class Abstract extends AttributeState {

    /**
     * The CND notation for each notation type.
     */
    public static final String[] NOTATION = new String[] { "abstract", "abs", "a" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompactCndNotation()
     */
    @Override
    protected String getCompactCndNotation() {
        return NOTATION[NotationType.COMPACT_INDEX];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getCompressedCndNotation()
     */
    @Override
    protected String getCompressedCndNotation() {
        return NOTATION[NotationType.COMPRESSED_INDEX];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.AttributeState#getLongCndNotation()
     */
    @Override
    protected String getLongCndNotation() {
        return NOTATION[NotationType.LONG_INDEX];
    }

}