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
import org.jboss.tools.modeshape.jcr.attributes.Autocreated;
import org.jboss.tools.modeshape.jcr.attributes.Mandatory;
import org.jboss.tools.modeshape.jcr.attributes.NodeAttributes;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
import org.jboss.tools.modeshape.jcr.attributes.Protected;
import org.jboss.tools.modeshape.jcr.attributes.SameNameSiblings;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class NodeAttributesTest {

    private NodeAttributes attributes;

    @Before
    public void beforeEach() {
        this.attributes = new NodeAttributes();
    }

    @Test
    public void defaultAttributesShouldHaveEmptyCompressedAndCompactCndNotation() {
        assertTrue(Utils.isEmpty(this.attributes.toCndNotation(NotationType.COMPRESSED)));
        assertTrue(Utils.isEmpty(this.attributes.toCndNotation(NotationType.COMPACT)));
    }

    @Test
    public void equalInstancesShouldHaveSameHashCode() {
        final NodeAttributes second = new NodeAttributes();
        assertEquals(this.attributes.hashCode(), second.hashCode());
    }

    @Test
    public void opvShouldBeDefaultAfterConstruction() {
        assertEquals(OnParentVersion.DEFAULT_VALUE, this.attributes.getOnParentVersion());
    }

    @Test
    public void shouldNotBeAutocreatedAfterConstruction() {
        final Autocreated attribute = this.attributes.getAutocreated();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

    @Test
    public void shouldNotBeMandatoryAfterConstruction() {
        final Mandatory attribute = this.attributes.getMandatory();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

    @Test
    public void shouldNotBeProtectedAfterConstruction() {
        final Protected attribute = this.attributes.getProtected();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }

    @Test
    public void shouldNotBeSameNameSiblingsAfterConstruction() {
        final SameNameSiblings attribute = this.attributes.getSameNameSiblings();
        assertTrue(attribute.isNot());
        assertFalse(attribute.is());
        assertFalse(attribute.isVariant());
    }
}
