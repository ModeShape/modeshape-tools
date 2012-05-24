/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Test;

/**
 * 
 */
public class OnParentValueTest implements Constants {

    private static final String ABORT_NOTATION = "ABORT"; //$NON-NLS-1$ 
    private static final String COMPUTE_NOTATION = "COMPUTE"; //$NON-NLS-1$ 
    private static final String COPY_NOTATION = "COPY"; //$NON-NLS-1$ 
    private static final String IGNORE_NOTATION = "IGNORE"; //$NON-NLS-1$ 
    private static final String INITIALIZE_NOTATION = "INITIALIZE"; //$NON-NLS-1$ 
    private static final String VARIANT_NOTATION = "OPV?"; //$NON-NLS-1$ 
    private static final String VERSION_NOTATION = "VERSION"; //$NON-NLS-1$ 

    private OnParentVersion attribute;

    @Test
    public void verifyAbortCndNotation() {
        this.attribute = OnParentVersion.ABORT;
        assertEquals(ABORT_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertEquals(ABORT_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(ABORT_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyComputeCndNotation() {
        this.attribute = OnParentVersion.COMPUTE;
        assertEquals(COMPUTE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertEquals(COMPUTE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(COMPUTE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyCopyCndNotation() {
        this.attribute = OnParentVersion.COPY;
        assertEquals(COPY_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPACT)));
    }

    @Test
    public void verifyIgnoreCndNotation() {
        this.attribute = OnParentVersion.IGNORE;
        assertEquals(IGNORE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertEquals(IGNORE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(IGNORE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyInitializeCndNotation() {
        this.attribute = OnParentVersion.INITIALIZE;
        assertEquals(INITIALIZE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertEquals(INITIALIZE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(INITIALIZE_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyVariantCndNotation() {
        this.attribute = OnParentVersion.VARIANT;
        assertEquals(VARIANT_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertEquals(VARIANT_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(VARIANT_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyVersionCndNotation() {
        this.attribute = OnParentVersion.VERSION;
        assertEquals(VERSION_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.LONG));
        assertEquals(VERSION_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(VERSION_NOTATION, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

}
