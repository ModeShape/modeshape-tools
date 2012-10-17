/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd.attributes;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.Abstract;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.jboss.tools.modeshape.jcr.cnd.Constants;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class AbstractTest implements Constants {

    private Abstract attribute;

    @Before
    public void beforeEach() {
        this.attribute = new Abstract();
    }

    @Test
    public void initialCndNotationShouldBeEmpty() {
        assertThat(Utils.isEmpty(this.attribute.toCndNotation(NotationType.LONG)), is(true));
        assertThat(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPRESSED)), is(true));
        assertThat(Utils.isEmpty(this.attribute.toCndNotation(NotationType.COMPACT)), is(true));
    }

    @Test
    public void initialStateShouldBeIsNot() {
        assertThat(this.attribute.get(), is(AttributeState.Value.IS_NOT));
    }

    @Test
    public void verifyCompactCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertThat(this.attribute.toCndNotation(NotationType.COMPACT), is(Abstract.NOTATION[NotationType.COMPACT_INDEX]));
    }

    @Test
    public void verifyCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertThat(this.attribute.toCndNotation(NotationType.COMPRESSED), is(Abstract.NOTATION[NotationType.COMPRESSED_INDEX]));
    }

    @Test
    public void verifyLongCndNotation() {
        this.attribute.set(AttributeState.Value.IS);
        assertThat(this.attribute.toCndNotation(NotationType.LONG), is(Abstract.NOTATION[NotationType.LONG_INDEX]));
    }

    @Test
    public void verifyVariantCompactCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertThat(this.attribute.toCndNotation(NotationType.COMPACT), is(ABSTRACT_VARIANT_COMPACT_FORM));
    }

    @Test
    public void verifyVariantCompressedCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertThat(this.attribute.toCndNotation(NotationType.COMPRESSED), is(ABSTRACT_VARIANT_COMPRESSED_FORM));
    }

    @Test
    public void verifyVariantLongCndNotation() {
        this.attribute.set(AttributeState.Value.VARIANT);
        assertThat(this.attribute.toCndNotation(NotationType.LONG), is(ABSTRACT_VARIANT_LONG_FORM));
    }

}
