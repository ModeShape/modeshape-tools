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
import org.jboss.tools.modeshape.jcr.attributes.DefaultType;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class DefaultTypeTest implements Constants {

    private DefaultType attribute;

    @Before
    public void beforeEach() {
        this.attribute = new DefaultType();
    }

    @Test
    public void initialCndNotationShouldBeEmpty() {
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPACT)));
    }

    @Test
    public void initialStateShouldBeIsNot() {
        assertEquals(AttributeState.Value.IS_NOT, this.attribute.get());
    }

    @Test
    public void verifyCompactCndNotation() {
        this.attribute.setDefaultType(DEFAULT_TYPE);
        assertEquals(DEFAULT_TYPE_TYPE_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyCompressedCndNotation() {
        this.attribute.setDefaultType(DEFAULT_TYPE);
        assertEquals(DEFAULT_TYPE_TYPE_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
    }

    @Test
    public void verifyLongCndNotation() {
        this.attribute.setDefaultType(DEFAULT_TYPE);
        assertEquals(DEFAULT_TYPE_TYPE_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test
    public void verifyVariantCompactCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(DEFAULT_TYPE_VARIANT_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyVariantCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(DEFAULT_TYPE_VARIANT_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
    }

    @Test
    public void verifyVariantLongCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(DEFAULT_TYPE_VARIANT_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

}
