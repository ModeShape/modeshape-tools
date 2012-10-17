/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.WorkspaceRegistry;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class WorkspaceRegistryTest {

    private WorkspaceRegistry registry;

    @Before
    public void beforeEach() throws Exception {
        this.registry = WorkspaceRegistry.get();
    }

    @Test
    public void jcrNamespaceShouldBeRegistered() {
        assertTrue(this.registry.isBuiltIn(new NamespaceMapping(Constants.BuiltInNamespaces.JCR.getPrefix(),
                                                                Constants.BuiltInNamespaces.JCR.getUri())));
        assertTrue(this.registry.isBuiltInNamespacePrefix(Constants.BuiltInNamespaces.JCR.getPrefix()));
        assertNotNull(this.registry.getNamespaceMapping(Constants.BuiltInNamespaces.JCR.getPrefix()));
        assertEquals(Constants.BuiltInNamespaces.JCR.getPrefix(), this.registry.getPrefix(Constants.BuiltInNamespaces.JCR.getUri()));
        assertEquals(Constants.BuiltInNamespaces.JCR.getUri(), this.registry.getUri(Constants.BuiltInNamespaces.JCR.getPrefix()));
    }

    @Test
    public void mixNamespaceShouldBeRegistered() {
        assertTrue(this.registry.isBuiltIn(new NamespaceMapping(Constants.BuiltInNamespaces.MIX.getPrefix(),
                                                                Constants.BuiltInNamespaces.MIX.getUri())));
        assertTrue(this.registry.isBuiltInNamespacePrefix(Constants.BuiltInNamespaces.MIX.getPrefix()));
        assertNotNull(this.registry.getNamespaceMapping(Constants.BuiltInNamespaces.MIX.getPrefix()));
        assertEquals(Constants.BuiltInNamespaces.MIX.getPrefix(), this.registry.getPrefix(Constants.BuiltInNamespaces.MIX.getUri()));
        assertEquals(Constants.BuiltInNamespaces.MIX.getUri(), this.registry.getUri(Constants.BuiltInNamespaces.MIX.getPrefix()));
    }

    @Test
    public void ntNamespaceShouldBeRegistered() {
        assertTrue(this.registry.isBuiltIn(new NamespaceMapping(Constants.BuiltInNamespaces.NT.getPrefix(),
                                                                Constants.BuiltInNamespaces.NT.getUri())));
        assertTrue(this.registry.isBuiltInNamespacePrefix(Constants.BuiltInNamespaces.NT.getPrefix()));
        assertNotNull(this.registry.getNamespaceMapping(Constants.BuiltInNamespaces.NT.getPrefix()));
        assertEquals(Constants.BuiltInNamespaces.NT.getPrefix(), this.registry.getPrefix(Constants.BuiltInNamespaces.NT.getUri()));
        assertEquals(Constants.BuiltInNamespaces.NT.getUri(), this.registry.getUri(Constants.BuiltInNamespaces.NT.getPrefix()));
    }

    @Test
    public void shouldGetInheriteProperties() {
        final Collection<PropertyDefinition> properties = this.registry.getPropertyDefinitions("nt:resource", true); //$NON-NLS-1$
        assertEquals(5, properties.size());

        final Collection<String> names = new ArrayList<String>(5);
        names.add("jcr:mimeType"); //$NON-NLS-1$
        names.add("jcr:encoding"); //$NON-NLS-1$
        names.add("jcr:lastModified"); //$NON-NLS-1$
        names.add("jcr:lastModifiedBy"); //$NON-NLS-1$
        names.add("jcr:data"); //$NON-NLS-1$

        for (final PropertyDefinition propDefn : properties) {
            names.remove(propDefn.getName());
        }

        assertTrue(names.isEmpty());
    }

    @Test
    public void shouldHaveRegisteredNodeTypes() throws Exception {
        assertNotNull(this.registry.getNodeTypeDefinition("nt:base")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:unstructured")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:created")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:hierarchyNode")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:file")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:linkedFile")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:folder")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:referenceable")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:mimeType")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:lastModified")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:resource")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:nodeType")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:propertyDefinition")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:childNodeDefinition")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:versionHistory")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:versionLabels")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:version")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:frozenNode")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:versionedChild")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:query")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:activity")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:simpleVersionable")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:versionable")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:configuration")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:address")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("nt:naturalText")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:etag")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:lockable")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:lifecycle")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:managedRetention")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:shareable")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:title")); //$NON-NLS-1$
        assertNotNull(this.registry.getNodeTypeDefinition("mix:language")); //$NON-NLS-1$
    }

    @Test
    public void shouldNotGetInheriteProperties() {
        final Collection<PropertyDefinition> properties = this.registry.getPropertyDefinitions("nt:resource", false); //$NON-NLS-1$
        assertEquals(1, properties.size());
        assertEquals("jcr:data", properties.iterator().next().getName()); //$NON-NLS-1$
    }
}
