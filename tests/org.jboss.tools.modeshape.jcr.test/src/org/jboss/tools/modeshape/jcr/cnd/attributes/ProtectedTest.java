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
import org.jboss.tools.modeshape.jcr.attributes.AttributeState;
import org.jboss.tools.modeshape.jcr.attributes.Protected;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class ProtectedTest implements Constants {

    private Protected attribute;

    @Before
    public void beforeEach() {
        this.attribute = new Protected();
    }

    @Test
    public void initialCndNotationShouldBeEmpty() {
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPACT)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPACT)));
    }

    @Test
    public void initialStateShouldBeIsNot() {
        assertEquals(AttributeState.Value.IS_NOT, this.attribute.get());
    }

    @Test
    public void verifyCompactCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertEquals(Protected.NOTATION[NotationType.COMPACT_INDEX], this.attribute.toCndNotation(NotationType.COMPACT));
    }

    @Test
    public void verifyCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertEquals(Protected.NOTATION[NotationType.COMPRESSED_INDEX], this.attribute.toCndNotation(NotationType.COMPRESSED));
    }

    @Test
    public void verifyLongCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertEquals(Protected.NOTATION[NotationType.LONG_INDEX], this.attribute.toCndNotation(NotationType.LONG));
    }

    @Test
    public void verifyVariantCompactCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(PROTECTED_VARIANT_COMPACT_FORM, this.attribute.toCndNotation(NotationType.COMPACT));
    }

    @Test
    public void verifyVariantCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(PROTECTED_VARIANT_COMPRESSED_FORM, this.attribute.toCndNotation(NotationType.COMPRESSED));
    }

    @Test
    public void verifyVariantLongCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(PROTECTED_VARIANT_LONG_FORM, this.attribute.toCndNotation(NotationType.LONG));
    }

}
