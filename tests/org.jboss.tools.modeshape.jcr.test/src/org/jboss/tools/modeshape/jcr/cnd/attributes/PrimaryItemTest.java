/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState;
import org.jboss.tools.modeshape.jcr.attributes.PrimaryItem;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class PrimaryItemTest implements Constants {

    private PrimaryItem attribute;

    @Before
    public void beforeEach() {
        this.attribute = new PrimaryItem();
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
    public void instancesWithDifferentPrimaryItemNamesShouldNotBeEqualAndHaveDifferentHashCodes() {
        this.attribute.setPrimaryItem(Constants.QUALIFIED_NAME1.get());
        final PrimaryItem that = new PrimaryItem();
        that.setPrimaryItem(this.attribute.getPrimaryItem().get() + "changed"); //$NON-NLS-1$
        assertFalse(this.attribute.equals(that));
        assertFalse(this.attribute.hashCode() == that.hashCode());
    }

    @Test
    public void instancesWithSamePrimaryItemNameShouldBeEqualAndHaveSameHashCode() {
        this.attribute.setPrimaryItem(Constants.QUALIFIED_NAME1.get());
        final PrimaryItem that = new PrimaryItem();
        that.setPrimaryItem(this.attribute.getPrimaryItem().get());
        assertEquals(this.attribute, that);
        assertEquals(this.attribute.hashCode(), that.hashCode());
    }

    @Test
    public void newInstancesShouldBeEqualAndHaveSameHashCode() {
        assertEquals(this.attribute, new PrimaryItem());
        assertEquals(this.attribute.hashCode(), new PrimaryItem().hashCode());
    }

    @Test
    public void stateShouldBeIsNotWhenSettingEmptyPrimaryItem() {
        assertTrue(this.attribute.setPrimaryItem("primaryItem")); //$NON-NLS-1$
        assertTrue(this.attribute.setPrimaryItem(Utils.EMPTY_STRING));
        assertTrue(this.attribute.isNot());

        assertTrue(this.attribute.setPrimaryItem("primaryItem")); //$NON-NLS-1$
        assertTrue(this.attribute.setPrimaryItem(null));
        assertTrue(this.attribute.isNot());
    }

    @Test
    public void stateShouldBeIsWhenSettingNonEmptyPrimaryItem() {
        assertTrue(this.attribute.setPrimaryItem("primaryItem")); //$NON-NLS-1$
        assertTrue(this.attribute.is());
    }

    @Test
    public void verifyCompactCndNotation() {
        this.attribute.setPrimaryItem(PRIMARY_ITEM);
        assertEquals(PRIMARY_ITEM_ITEM_COMPACT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyCompressedCndNotation() {
        this.attribute.setPrimaryItem(PRIMARY_ITEM);
        assertEquals(PRIMARY_ITEM_ITEM_COMPRESSED_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
    }

    @Test
    public void verifyLongCndNotation() {
        this.attribute.setPrimaryItem(PRIMARY_ITEM);
        assertEquals(PRIMARY_ITEM_ITEM_LONG_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test
    public void verifyVariantCompactCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(PRIMARY_ITEM_VARIANT_COMPACT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
    }

    @Test
    public void verifyVariantCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(PRIMARY_ITEM_VARIANT_COMPRESSED_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
    }

    @Test
    public void verifyVariantLongCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertEquals(PRIMARY_ITEM_VARIANT_LONG_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }
}
