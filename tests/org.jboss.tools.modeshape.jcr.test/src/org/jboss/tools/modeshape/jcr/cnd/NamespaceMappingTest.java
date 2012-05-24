/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.modeshape.jcr.Listener;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class NamespaceMappingTest {

    private NamespaceMapping namespaceMapping;

    @Before
    public void beforeEach() {
        this.namespaceMapping = new NamespaceMapping();
    }

    @Test
    public void compactCndNotationShouldBeCorrect() {
        final String PREFIX = "prefix"; //$NON-NLS-1$
        final String URI = "uri"; //$NON-NLS-1$
        this.namespaceMapping = new NamespaceMapping(PREFIX, URI);
        assertEquals(NamespaceMapping.NOTATION_PREFIX + PREFIX + NamespaceMapping.NOTATION_DELIMITER + '\'' + URI + '\''
                + NamespaceMapping.NOTATION_SUFFIX, this.namespaceMapping.toCndNotation(NotationType.COMPACT));
    }

    @Test
    public void compressedCndNotationShouldBeCorrect() {
        final String PREFIX = "prefix"; //$NON-NLS-1$
        final String URI = "uri"; //$NON-NLS-1$
        this.namespaceMapping = new NamespaceMapping(PREFIX, URI);
        assertEquals(NamespaceMapping.NOTATION_PREFIX + PREFIX + NamespaceMapping.NOTATION_DELIMITER + '\'' + URI + '\''
                + NamespaceMapping.NOTATION_SUFFIX, this.namespaceMapping.toCndNotation(NotationType.COMPRESSED));
    }

    @Test
    public void copiesShouldBeEqualAndHaveSameHashCode() {
        NamespaceMapping thatNamespaceMapping = NamespaceMapping.copy(this.namespaceMapping);
        assertEquals(this.namespaceMapping, thatNamespaceMapping);
        assertEquals(this.namespaceMapping.hashCode(), thatNamespaceMapping.hashCode());

        assertTrue(this.namespaceMapping.setComment("comment goes here")); //$NON-NLS-1$
        thatNamespaceMapping = NamespaceMapping.copy(this.namespaceMapping);
        assertEquals(this.namespaceMapping, thatNamespaceMapping);
        assertEquals(this.namespaceMapping.hashCode(), thatNamespaceMapping.hashCode());

        assertTrue(this.namespaceMapping.setPrefix(Constants.NAMESPACE_PREFIX1));
        thatNamespaceMapping = NamespaceMapping.copy(this.namespaceMapping);
        assertEquals(this.namespaceMapping, thatNamespaceMapping);
        assertEquals(this.namespaceMapping.hashCode(), thatNamespaceMapping.hashCode());

        assertTrue(this.namespaceMapping.setUri(Constants.NAMESPACE_URI1));
        thatNamespaceMapping = NamespaceMapping.copy(this.namespaceMapping);
        assertEquals(this.namespaceMapping, thatNamespaceMapping);
        assertEquals(this.namespaceMapping.hashCode(), thatNamespaceMapping.hashCode());
    }

    @Test
    public void longCndNotationShouldBeCorrect() {
        final String PREFIX = "prefix"; //$NON-NLS-1$
        final String URI = "uri"; //$NON-NLS-1$
        this.namespaceMapping = new NamespaceMapping(PREFIX, URI);
        assertEquals(NamespaceMapping.NOTATION_PREFIX + PREFIX + Utils.SPACE_STRING + NamespaceMapping.NOTATION_DELIMITER
                             + Utils.SPACE_STRING + '\'' + URI + '\'' + NamespaceMapping.NOTATION_SUFFIX,
                     this.namespaceMapping.toCndNotation(NotationType.LONG));
    }

    @Test
    public void shouldHaveEmptyCommentAfterConstruction() {
        assertTrue(Utils.isEmpty(this.namespaceMapping.getComment()));
    }

    @Test
    public void shouldHaveEmptyPrefixAfterConstruction() {
        assertTrue(Utils.isEmpty(this.namespaceMapping.getPrefix()));
    }

    @Test
    public void shouldHaveEmptyUriAfterConstruction() {
        assertTrue(Utils.isEmpty(this.namespaceMapping.getUri()));
    }

    @Test
    public void shouldNotReceivePropertyChangeEventsWhenUnregistered() {
        final Listener l = new Listener();
        this.namespaceMapping.addListener(l);
        this.namespaceMapping.removeListener(l);

        assertTrue(this.namespaceMapping.setPrefix("newPrefix")); //$NON-NLS-1$
        assertEquals(0, l.getCount());
        assertTrue(this.namespaceMapping.setUri("newUri")); //$NON-NLS-1$
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotSetCommentToSameValue() {
        final String COMMENT = "comment"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setComment(COMMENT));
        assertFalse(this.namespaceMapping.setComment(COMMENT));
    }

    @Test
    public void shouldNotSetPrefixToSameValue() {
        final String PREFIX = "prefix"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setPrefix(PREFIX));
        assertFalse(this.namespaceMapping.setPrefix(PREFIX));
    }

    @Test
    public void shouldNotSetUriToSameValue() {
        final String URI = "uri"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setUri(URI));
        assertFalse(this.namespaceMapping.setUri(URI));
    }

    @Test
    public void shouldReceivePropertyChangeEventWhenCommentIsChanged() {
        final Listener l = new Listener();
        this.namespaceMapping.addListener(l);

        final String OLD_VALUE = this.namespaceMapping.getComment();
        final String NEW_VALUE = "comment"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setComment(NEW_VALUE));
        assertEquals(1, l.getCount());
        assertEquals(NamespaceMapping.PropertyName.COMMENT.toString(), l.getPropertyName());
        assertEquals(OLD_VALUE, l.getOldValue());
        assertEquals(NEW_VALUE, l.getNewValue());
    }

    @Test
    public void shouldReceivePropertyChangeEventWhenPrefixIsChanged() {
        final Listener l = new Listener();
        this.namespaceMapping.addListener(l);

        final String OLD_VALUE = this.namespaceMapping.getPrefix();
        final String NEW_VALUE = "prefix"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setPrefix(NEW_VALUE));
        assertEquals(1, l.getCount());
        assertEquals(NamespaceMapping.PropertyName.PREFIX.toString(), l.getPropertyName());
        assertEquals(OLD_VALUE, l.getOldValue());
        assertEquals(NEW_VALUE, l.getNewValue());
    }

    @Test
    public void shouldReceivePropertyChangeEventWhenUriIsChanged() {
        final Listener l = new Listener();
        this.namespaceMapping.addListener(l);

        final String OLD_VALUE = this.namespaceMapping.getUri();
        final String NEW_VALUE = "uri"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setUri(NEW_VALUE));
        assertEquals(1, l.getCount());
        assertEquals(NamespaceMapping.PropertyName.URI.toString(), l.getPropertyName());
        assertEquals(OLD_VALUE, l.getOldValue());
        assertEquals(NEW_VALUE, l.getNewValue());
    }

    @Test
    public void shouldSetComment() {
        final String COMMENT = "comment"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setComment(COMMENT));
        assertEquals(COMMENT, this.namespaceMapping.getComment());
    }

    @Test
    public void shouldSetPrefix() {
        final String PREFIX = "prefix"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setPrefix(PREFIX));
        assertEquals(PREFIX, this.namespaceMapping.getPrefix());
    }

    @Test
    public void shouldSetPrefixAtConstruction() {
        final String PREFIX = "prefix"; //$NON-NLS-1$
        this.namespaceMapping = new NamespaceMapping(PREFIX, null);
        assertEquals(PREFIX, this.namespaceMapping.getPrefix());
    }

    @Test
    public void shouldSetUri() {
        final String URI = "uri"; //$NON-NLS-1$
        assertTrue(this.namespaceMapping.setUri(URI));
        assertEquals(URI, this.namespaceMapping.getUri());
    }

    @Test
    public void shouldSetUriAtConstruction() {
        final String URI = "uri"; //$NON-NLS-1$
        this.namespaceMapping = new NamespaceMapping(null, URI);
        assertEquals(URI, this.namespaceMapping.getUri());
    }

    @Test
    public void twoNamespaceMappingsWithDifferentPrefixesAndSameUriShouldNotBeEqual() {
        final NamespaceMapping namespace1 = new NamespaceMapping("prefix", "uri"); //$NON-NLS-1$ //$NON-NLS-2$
        final NamespaceMapping namespace2 = new NamespaceMapping(namespace1.getPrefix() + "different", namespace1.getUri()); //$NON-NLS-1$

        assertFalse(namespace1.equals(namespace2));
        assertFalse(namespace1.hashCode() == namespace2.hashCode());
    }

    @Test
    public void twoNamespaceMappingsWithSamePrefixAndDifferentUrisShouldNotBeEqual() {
        final NamespaceMapping namespace1 = new NamespaceMapping("prefix", "uri"); //$NON-NLS-1$ //$NON-NLS-2$
        final NamespaceMapping namespace2 = new NamespaceMapping(namespace1.getPrefix(), namespace1.getUri() + "different"); //$NON-NLS-1$

        assertFalse(namespace1.equals(namespace2));
        assertFalse(namespace1.hashCode() == namespace2.hashCode());
    }

    @Test
    public void twoNamespaceMappingsWithSamePrefixAndSameUriButDifferentCommentShouldNotBeEqual() {
        final NamespaceMapping namespace1 = new NamespaceMapping("prefix", "uri"); //$NON-NLS-1$ //$NON-NLS-2$
        namespace1.setComment("comment"); //$NON-NLS-1$
        final NamespaceMapping namespace2 = new NamespaceMapping(namespace1.getPrefix(), namespace1.getUri());
        namespace2.setComment(namespace1.getComment() + "changed"); //$NON-NLS-1$

        assertFalse(namespace1.equals(namespace2));
        assertFalse(namespace1.hashCode() == namespace2.hashCode());
    }

    @Test
    public void twoNamespaceMappingsWithSamePrefixAndSameUriShouldBeEqual() {
        final NamespaceMapping namespace1 = new NamespaceMapping("prefix", "uri"); //$NON-NLS-1$ //$NON-NLS-2$
        final NamespaceMapping namespace2 = new NamespaceMapping(namespace1.getPrefix(), namespace1.getUri());

        assertTrue(namespace1.equals(namespace2));
        assertEquals(namespace1.hashCode(), namespace2.hashCode());
    }
}
