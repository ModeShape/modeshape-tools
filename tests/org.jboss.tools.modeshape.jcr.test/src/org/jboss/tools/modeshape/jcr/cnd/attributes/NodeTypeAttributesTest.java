/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.Abstract;
import org.jboss.tools.modeshape.jcr.attributes.Mixin;
import org.jboss.tools.modeshape.jcr.attributes.NodeTypeAttributes;
import org.jboss.tools.modeshape.jcr.attributes.Orderable;
import org.jboss.tools.modeshape.jcr.attributes.PrimaryItem;
import org.jboss.tools.modeshape.jcr.attributes.Queryable;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class NodeTypeAttributesTest {

    private NodeTypeAttributes attributes;

    @Before
    public void beforeEach() {
        this.attributes = new NodeTypeAttributes();
    }

    @Test
    public void defaultAttributesShouldHaveEmptyCndNotation() {
        assertTrue(Utils.isEmpty(this.attributes.toCndNotation(NotationType.LONG)));
        assertTrue(Utils.isEmpty(this.attributes.toCndNotation(NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attributes.toCndNotation(NotationType.COMPACT)));
    }

    @Test
    public void queryShouldBeVariantAfterConstruction() {
        final Queryable attribute = this.attributes.getQueryable();
        assertTrue(attribute.isVariant());
        assertFalse(attribute.is());
        assertFalse(attribute.isNot());
    }

    @Test
    public void shouldNotBeAbstractAfterConstruction() {
        final Abstract attribute = this.attributes.getAbstract();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

    @Test
    public void shouldNotBeMixinAfterConstruction() {
        final Mixin attribute = this.attributes.getMixin();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

    @Test
    public void shouldNotBeOrderableAfterConstruction() {
        final Orderable attribute = this.attributes.getOrderable();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

    @Test
    public void shouldNotSupportPrimaryItemAfterConstruction() {
        final PrimaryItem attribute = this.attributes.getPrimaryItem();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

}
