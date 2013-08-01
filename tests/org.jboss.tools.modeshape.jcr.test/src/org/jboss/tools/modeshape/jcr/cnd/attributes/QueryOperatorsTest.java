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
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class QueryOperatorsTest implements Constants {

    private QueryOperators attribute;

    @Before
    public void beforeEach() {
        this.attribute = new QueryOperators(); // initially supports all operators
    }

    @Test
    public void shouldSupportAllOperatorsAfterConstruction() {
        for (final QueryOperator operator : QueryOperator.values()) {
            assertTrue(this.attribute.supports(operator));
        }
    }

    @Test
    public void initialCndNotationShouldBeEmpty() {
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(CndElement.NotationType.COMPACT)));
    }

    @Test
    public void shouldAddUsingOperatorNotation() {
        // setup
        this.attribute.remove(OPERATOR_ONE);
        assertTrue(this.attribute.add(OPERATOR_ONE.toString()));

        // test
        assertTrue(this.attribute.supports(OPERATOR_ONE));
    }

    @Test
    public void shouldRemoveUsingOperatorNotation() {
        assertTrue(this.attribute.remove(OPERATOR_ONE.toString()));
    }

    @Test
    public void verifyAddedItem() {
        // setup
        this.attribute.remove(OPERATOR_ONE);
        this.attribute.add(OPERATOR_ONE);

        // tests
        assertTrue(this.attribute.supports(OPERATOR_ONE));
    }

    @Test
    public void verifyInitialStateShouldBeIs() {
        assertEquals(AttributeState.Value.IS, this.attribute.get());
    }

    @Test
    public void verifyMultipleElementsCndNotation() {
        // setup
        assertTrue(this.attribute.clear());
        this.attribute.add(OPERATOR_ONE);
        this.attribute.add(OPERATOR_TWO);
        this.attribute.add(OPERATOR_THREE);

        // tests
        assertEquals(QUERY_OPS_THREE_OPERATOR_COMPACT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
        assertEquals(QUERY_OPS_THREE_OPERATOR_COMPRESSED_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(QUERY_OPS_THREE_OPERATOR_LONG_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test
    public void verifyOneElementCndNotation() {
        // setup
        assertTrue(this.attribute.clear());
        this.attribute.add(OPERATOR_ONE);

        // tests
        assertEquals(QUERY_OPS_ONE_OPERATOR_COMPACT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
        assertEquals(QUERY_OPS_ONE_OPERATOR_COMPRESSED_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(QUERY_OPS_ONE_OPERATOR_LONG_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test
    public void verifyRemoveItem() {
        // setup
        this.attribute.remove(OPERATOR_ONE);

        // tests
        assertFalse(this.attribute.supports(OPERATOR_ONE));
    }

    @Test
    public void verifySameElementIsNotAdded() {
        assertFalse(this.attribute.add(OPERATOR_ONE));
    }

    @Test
    public void verifyStateShouldBeIsNotAfterClear() {
        // setup
        assertTrue(this.attribute.clear());

        // tests
        assertEquals(AttributeState.Value.IS_NOT, this.attribute.get());
    }

    @Test
    public void verifyVariantCndNotation() {
        // setup
        this.attribute.set(AttributeState.Value.VARIANT);

        // tests
        assertEquals(QUERY_OPS_VARIANT_COMPACT_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPACT));
        assertEquals(QUERY_OPS_VARIANT_COMPRESSED_FORM, this.attribute.toCndNotation(CndElement.NotationType.COMPRESSED));
        assertEquals(QUERY_OPS_VARIANT_LONG_FORM, this.attribute.toCndNotation(CndElement.NotationType.LONG));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyInvalidFindParameter() {
        QueryOperators.QueryOperator.find("bogus");
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyInvalidFindUsingJcrValueParameter() {
        QueryOperators.QueryOperator.findUsingJcrValue("bogus");
    }

}
