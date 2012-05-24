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
import org.jboss.tools.modeshape.jcr.attributes.Queryable;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class QueryableTest implements Constants {

    private Queryable attribute;

    @Before
    public void beforeEach() {
        this.attribute = new Queryable();
    }

    @Test
    public void initialCndNotationShouldBeEmpty() {
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPACT)));
    }

    @Test
    public void initialStateShouldBeVariant() {
        assertEquals(AttributeState.Value.VARIANT, this.attribute.get());
    }

    @Test
    public void verifyNoQueryCompactCndNotation() {
        this.attribute.set(AttributeState.Value.IS_NOT);
        assertEquals(Queryable.NO_QUERY_NOTATION[NotationType.COMPACT_INDEX], this.attribute.toCndNotation(NotationType.COMPACT));
    }

    @Test
    public void verifyNoQueryCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.IS_NOT);
        assertEquals(Queryable.NO_QUERY_NOTATION[NotationType.COMPRESSED_INDEX],
                     this.attribute.toCndNotation(NotationType.COMPRESSED));
    }

    @Test
    public void verifyNoQueryLongCndNotation() {
        this.attribute.set(AttributeState.Value.IS_NOT);
        assertEquals(Queryable.NO_QUERY_NOTATION[NotationType.LONG_INDEX], this.attribute.toCndNotation(NotationType.LONG));
    }

    @Test
    public void verifyQueryCompactCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertEquals(Queryable.QUERY_NOTATION[NotationType.COMPACT_INDEX], this.attribute.toCndNotation(NotationType.COMPACT));
    }

    @Test
    public void verifyQueryCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertEquals(Queryable.QUERY_NOTATION[NotationType.COMPRESSED_INDEX], this.attribute.toCndNotation(NotationType.COMPRESSED));
    }

    @Test
    public void verifyQueryLongCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertEquals(Queryable.QUERY_NOTATION[NotationType.LONG_INDEX], this.attribute.toCndNotation(NotationType.LONG));
    }

    @Test
    public void verifyVariantCndNotationIsEmpty() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPACT)));
    }

}
