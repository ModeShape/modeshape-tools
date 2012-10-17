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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.jboss.tools.modeshape.jcr.Listener;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinition.PropertyName;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CompactNodeTypeDefinitionTest {

    private static List<NodeTypeDefinition> _nodeTypeDefinitions;

    private static final int EMPTY_PREFIX_NODE_TYPE_MATCHES = 2;
    private static final int NT_NODE_TYPE_MATCHES = 3;
    private static final int JCR_NODE_TYPE_MATCHES = 4;

    private CompactNodeTypeDefinition cnd;
    private NamespaceMapping namespaceMapping;
    private NodeTypeDefinition nodeTypeDefinition;

    private void addNodeTypeDefinitions() {
        for (NodeTypeDefinition ntd : _nodeTypeDefinitions) {
            this.cnd.addNodeTypeDefinition(ntd);
        }
    }

    @Before
    public void beforeEach() {
        this.cnd = new CompactNodeTypeDefinition();
        this.namespaceMapping = new NamespaceMapping();
        this.nodeTypeDefinition = new NodeTypeDefinition();

        if (_nodeTypeDefinitions == null) {
            _nodeTypeDefinitions = new ArrayList<NodeTypeDefinition>();

            NodeTypeDefinition nodeType = new NodeTypeDefinition();
            nodeType.setName("unqualified1");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("unqualified2");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("nt:nt1");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("nt:nt2");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("nt:nt3");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("jcr:jcr1");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("jcr:jcr2");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("jcr:jcr3");
            _nodeTypeDefinitions.add(nodeType);

            nodeType = new NodeTypeDefinition();
            nodeType.setName("jcr:jcr4");
            _nodeTypeDefinitions.add(nodeType);
        }
    }

    @Test
    public void emptyCndShouldProduceWarning() {
        assertTrue(CndValidator.validateCnd(this.cnd).isWarning());
    }

    @Test
    public void shouldAddNamespace() {
        assertTrue(this.cnd.addNamespaceMapping(this.namespaceMapping));
        assertEquals(this.namespaceMapping, this.cnd.getNamespaceMappings().iterator().next());
    }

    @Test
    public void shouldAddNodeTypeDefinition() {
        assertTrue(this.cnd.addNodeTypeDefinition(this.nodeTypeDefinition));
        assertEquals(this.nodeTypeDefinition, this.cnd.getNodeTypeDefinitions().iterator().next());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullNamespaceToBeAdded() {
        this.cnd.addNamespaceMapping(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullNodeTypeDefinitionToBeAdded() {
        this.cnd.addNodeTypeDefinition(null);
    }

    @Test
    public void shouldNotReceiveEventAfterUnregistering() {
        final Listener l = new Listener();
        assertTrue(this.cnd.addListener(l));
        assertTrue(this.cnd.removeListener(l));

        assertTrue(this.cnd.addNamespaceMapping(this.namespaceMapping));
        assertEquals(0, l.getCount());

        // these needed so Listener class can get max code coverage
        assertNull(l.getEvent());
        assertNull(l.getNewValue());
        assertNull(l.getOldValue());
        assertNull(l.getPropertyName());
    }

    @Test
    public void shouldNotRemoveNamespaceMappingThatDoesNotExist() {
        assertFalse(this.cnd.removeNamespaceMapping(this.namespaceMapping));
    }

    @Test
    public void shouldNotRemoveNodeTypeDefinitionThatDoesNotExist() {
        assertFalse(this.cnd.removeNodeTypeDefinition(this.nodeTypeDefinition));
    }

    @Test
    public void shouldReceiveEventAfterAddingNamespace() {
        final Listener l = new Listener();
        assertTrue(this.cnd.addListener(l));

        assertTrue(this.cnd.addNamespaceMapping(this.namespaceMapping));

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.NAMESPACE_MAPPINGS.toString(), l.getPropertyName());
        assertEquals(this.namespaceMapping, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterAddingNodeTypeDefinition() {
        final Listener l = new Listener();
        assertTrue(this.cnd.addListener(l));

        assertTrue(this.cnd.addNodeTypeDefinition(this.nodeTypeDefinition));

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.NODE_TYPE_DEFINITIONS.toString(), l.getPropertyName());
        assertEquals(this.nodeTypeDefinition, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterRemoveNamespaceMapping() {
        assertTrue(this.cnd.addNamespaceMapping(this.namespaceMapping));

        final Listener l = new Listener();
        assertTrue(this.cnd.addListener(l));

        assertTrue(this.cnd.removeNamespaceMapping(this.namespaceMapping));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.NAMESPACE_MAPPINGS.toString(), l.getPropertyName());
        assertEquals(this.namespaceMapping, l.getOldValue());
        assertNull(l.getNewValue());
    }

    @Test
    public void shouldReceiveEventAfterRemoveNodeTypeDefinition() {
        assertTrue(this.cnd.addNodeTypeDefinition(this.nodeTypeDefinition));

        final Listener l = new Listener();
        assertTrue(this.cnd.addListener(l));

        assertTrue(this.cnd.removeNodeTypeDefinition(this.nodeTypeDefinition));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.NODE_TYPE_DEFINITIONS.toString(), l.getPropertyName());
        assertEquals(this.nodeTypeDefinition, l.getOldValue());
        assertNull(l.getNewValue());
    }

    @Test
    public void shouldRemoveNamespaceMapping() {
        assertTrue(this.cnd.addNamespaceMapping(this.namespaceMapping));
        assertTrue(this.cnd.removeNamespaceMapping(this.namespaceMapping));
        assertEquals(0, this.cnd.getNamespaceMappings().size());
    }

    @Test
    public void shouldRemoveNodeTypeDefinition() {
        assertTrue(this.cnd.addNodeTypeDefinition(this.nodeTypeDefinition));
        assertTrue(this.cnd.removeNodeTypeDefinition(this.nodeTypeDefinition));
        assertEquals(0, this.cnd.getNodeTypeDefinitions().size());
    }

    @Test
    public void shouldMatchEmptyPrefixNodeTypeDefinitions() {
        addNodeTypeDefinitions();

        { // NT matches
            final String prefix = "nt";
            List<NodeTypeDefinition> matches = this.cnd.getMatchingNodeTypeDefinitions(prefix, false);
            assertEquals(NT_NODE_TYPE_MATCHES, matches.size());
        }

        { // JCR matches
            final String prefix = "jcr";
            List<NodeTypeDefinition> matches = this.cnd.getMatchingNodeTypeDefinitions(prefix, false);
            assertEquals(JCR_NODE_TYPE_MATCHES, matches.size());
        }
    }

    @Test
    public void shouldMatchPrefixNodeTypeDefinitions() {
        addNodeTypeDefinitions();

        { // null matches
            List<NodeTypeDefinition> matches = this.cnd.getMatchingNodeTypeDefinitions(null, false);
            assertEquals(EMPTY_PREFIX_NODE_TYPE_MATCHES, matches.size());
        }

        { // empty matches
            List<NodeTypeDefinition> matches = this.cnd.getMatchingNodeTypeDefinitions(Utils.EMPTY_STRING, false);
            assertEquals(EMPTY_PREFIX_NODE_TYPE_MATCHES, matches.size());
        }
    }
}
