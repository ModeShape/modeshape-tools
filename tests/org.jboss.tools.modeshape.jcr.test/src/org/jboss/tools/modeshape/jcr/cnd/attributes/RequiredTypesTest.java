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
import static org.junit.Assert.fail;

import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState;
import org.jboss.tools.modeshape.jcr.attributes.RequiredTypes;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class RequiredTypesTest implements Constants {

    private RequiredTypes attribute;

    private void add( final QualifiedName item ) {
        if (!this.attribute.add(item)) {
            fail();
        }
    }

    @Before
    public void beforeEach() {
        this.attribute = new RequiredTypes();
    }

    @Test
    public void initialCndNotationShouldBeEmpty() {
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPACT)));
    }

    private void remove( final QualifiedName item ) {
        if (!this.attribute.remove(item)) {
            fail();
        }
    }

    @Test
    public void verifyAddedItem() {
        // setup
        add(QUALIFIED_NAME1);

        // tests
        assertEquals(1, this.attribute.getSupportedItems().size());
        assertTrue(this.attribute.getSupportedItems().contains(QUALIFIED_NAME1));
    }

    @Test
    public void verifyInitiallyNoSupportedItems() {
        assertEquals(0, this.attribute.getSupportedItems().size());
    }

    @Test
    public void verifyInitialStateShouldBeIsNot() {
        assertEquals(AttributeState.Value.IS_NOT, this.attribute.get());
    }

    @Test
    public void verifyMultipleElementsCndNotation() {
        // setup
        add(QUALIFIED_NAME1);
        add(QUALIFIED_NAME2);
        add(QUALIFIED_NAME3);

        // tests
        assertEquals(REQUIRED_TYPES_THREE_ITEM_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
        assertEquals(REQUIRED_TYPES_THREE_ITEM_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(REQUIRED_TYPES_THREE_ITEM_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test
    public void verifyOneElementCndNotation() {
        // setup
        add(QUALIFIED_NAME1);

        // tests
        assertEquals(REQUIRED_TYPES_ONE_ITEM_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
        assertEquals(REQUIRED_TYPES_ONE_ITEM_SHORT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(REQUIRED_TYPES_ONE_ITEM_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test
    public void verifyRemoveItem() {
        // setup
        add(QUALIFIED_NAME1);
        add(QUALIFIED_NAME2);
        remove(QUALIFIED_NAME1);

        // tests
        assertFalse(this.attribute.getSupportedItems().contains(QUALIFIED_NAME1));
    }

    @Test
    public void verifySameElementIsNotAdded() {
        // setup
        add(QUALIFIED_NAME1);

        // tests
        if (this.attribute.add(QUALIFIED_NAME1)) {
            fail();
        }

        assertEquals(1, this.attribute.getSupportedItems().size());
    }

    @Test
    public void verifyStateShouldBeIsAfterAdd() {
        // setup
        add(QUALIFIED_NAME1);

        // tests
        assertEquals(AttributeState.Value.IS, this.attribute.get());
    }

    @Test
    public void verifyStateShouldBeIsNotWhenEmpty() {
        // setup
        add(QUALIFIED_NAME1);
        remove(QUALIFIED_NAME1);

        // tests
        assertEquals(0, this.attribute.getSupportedItems().size());
        assertEquals(AttributeState.Value.IS_NOT, this.attribute.get());
    }

    @Test
    public void verifyVariantCndNotation() {
        // setup
        this.attribute.set(AttributeState.Value.VARIANT);

        // tests
        assertEquals(REQUIRED_TYPES_SHORT_VARIANT, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
        assertEquals(REQUIRED_TYPES_SHORT_VARIANT, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(REQUIRED_TYPES_VARIANT, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

}
