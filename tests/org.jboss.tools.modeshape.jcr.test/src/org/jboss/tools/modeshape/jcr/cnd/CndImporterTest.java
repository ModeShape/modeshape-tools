/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.version.OnParentVersionAction;

import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.attributes.PropertyType;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.text.ParsingException;

/**
 * 
 */
public class CndImporterTest {

    public static final String CND_FILE_PATH = "testdata/"; //$NON-NLS-1$

    public static final String[] NO_DEFAULTS = {};
    public static final String NO_PRIMARY_NAME = null;
    public static final String[] NO_SUPERTYPES = {};

    public static final String[] NO_VALUE_CONSTRAINTS = {};

    //
    // protected Name name( String name ) {
    // return context.getValueFactories().getNameFactory().create(name);
    // }

    private CompactNodeTypeDefinition cnd;

    private CndImporter importer;

    private Collection<Throwable> problems;

    private void assertChild( final String nodeTypeName,
                              final String childName,
                              final String requiredType,
                              final String defaultPrimaryType,
                              final OnParentVersion onParentVersioning,
                              final ChildOptions... childOptions ) {
        assertChild(nodeTypeName, childName, new String[] { requiredType }, defaultPrimaryType, childOptions, onParentVersioning);
    }

    private void assertChild( final String nodeTypeName,
                              final String childName,
                              final String[] requiredTypes,
                              final String defaultPrimaryType,
                              final ChildOptions[] childOptions,
                              final OnParentVersion onParentVersioning ) {
        final Set<ChildOptions> options = new HashSet<ChildOptions>();
        for (final ChildOptions option : childOptions) {
            options.add(option);
        }

        final NodeTypeDefinition defn = defn(nodeTypeName);
        final NodeDefinition childDefn = childDefn(defn, childName);

        assertEquals(childDefn.getName(), childName);
        assertEquals(childDefn.getDefaultPrimaryTypeName(), defaultPrimaryType);
        assertEquals(childDefn.isMandatory(), options.contains(ChildOptions.Mandatory));
        assertEquals(childDefn.isAutoCreated(), options.contains(ChildOptions.Autocreated));
        assertEquals(childDefn.isProtected(), options.contains(ChildOptions.Protected));
        assertEquals(childDefn.allowsSameNameSiblings(), options.contains(ChildOptions.Sns));
        assertEquals(childDefn.getOnParentVersion(), opv(onParentVersioning));
        assertArrayEquals(childDefn.getRequiredPrimaryTypeNames(), requiredTypes);
    }

    private void assertNodeType( final String name,
                                 final String[] superTypes,
                                 final String primaryItemName,
                                 final NodeOptions... nodeOptions ) {
        final Set<NodeOptions> options = new HashSet<NodeOptions>();

        for (final NodeOptions option : nodeOptions) {
            options.add(option);
        }

        final NodeTypeDefinition defn = defn(name);
        assertEquals(defn.getName(), name);
        assertEquals(defn.isAbstract(), options.contains(NodeOptions.Abstract));
        assertEquals(defn.hasOrderableChildNodes(), options.contains(NodeOptions.Ordered));
        assertEquals(defn.isMixin(), options.contains(NodeOptions.Mixin));
        // assertEquals(defn.isQueryable(), options.contains(NodeOptions.Queryable)); // defaults to variant
        assertTrue(defn.getState(NodeTypeDefinition.PropertyName.QUERYABLE) == org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value.VARIANT);
        assertEquals(defn.getPrimaryItemName(), primaryItemName);
        final String[] supertypeNames = defn.getDeclaredSupertypeNames();
        assertArrayEquals(supertypeNames, superTypes);
    }

    //
    // @Test(expected = ParsingException.class)
    // public void shouldReportErrorIfTheNodeTypeNameUsesInvalidNamespace() {
    //        String cnd = "<ns = 'http://namespace.com/ns'> [xyz:acme] abstract"; //$NON-NLS-1$
    // this.importer.parse(cnd);
    // }

    private void assertProperty( final String nodeTypeName,
                                 final String propertyName,
                                 final String requiredType,
                                 final String[] defaultValues,
                                 final OnParentVersion onParentVersion,
                                 final PropertyOptions... propertyOptions ) throws RepositoryException {
        assertProperty(nodeTypeName, propertyName, requiredType, defaultValues, propertyOptions, onParentVersion);
    }

    private void assertProperty( final String nodeTypeName,
                                 final String propertyName,
                                 final String requiredType,
                                 final String[] defaultValues,
                                 final PropertyOptions... propertyOptions ) throws RepositoryException {
        assertProperty(nodeTypeName, propertyName, requiredType, defaultValues, propertyOptions, null);
    }

    private void assertProperty( final String nodeTypeName,
                                 final String propertyName,
                                 final String requiredType,
                                 final String[] defaultValues,
                                 final PropertyOptions[] propertyOptions,
                                 final OnParentVersion onParentVersioning,
                                 final String... valueConstraints ) throws RepositoryException {
        final Set<PropertyOptions> options = new HashSet<PropertyOptions>();
        for (final PropertyOptions option : propertyOptions) {
            options.add(option);
        }

        final NodeTypeDefinition defn = defn(nodeTypeName);
        final PropertyDefinition propDefn = propDefn(defn, propertyName);

        assertEquals(propDefn.getName(), propertyName);
        assertEquals(propDefn.getRequiredType(), jcrPropertyType(requiredType));
        assertEquals(propDefn.isMandatory(), options.contains(PropertyOptions.Mandatory));
        assertEquals(propDefn.isAutoCreated(), options.contains(PropertyOptions.Autocreated));
        assertEquals(propDefn.isProtected(), options.contains(PropertyOptions.Protected));
        assertEquals(propDefn.isMultiple(), options.contains(PropertyOptions.Multiple));
        assertEquals(propDefn.isFullTextSearchable(), options.contains(PropertyOptions.FullTextSearchable));
        assertEquals(propDefn.isQueryOrderable(), options.contains(PropertyOptions.QueryOrderable));

        final int opv = opv(onParentVersioning);
        assertEquals(propDefn.getOnParentVersion(), opv);

        if ((defaultValues == null) || (defaultValues.length == 0)) {
            assertTrue(propDefn.getDefaultValues().length == 0);
        } else {
            int i = 0;

            for (final Value defaultValue : propDefn.getDefaultValues()) {
                assertEquals(defaultValues[i++], defaultValue.getString());
            }
        }

        if ((valueConstraints == null) || (valueConstraints.length == 0)) {
            assertTrue(propDefn.getValueConstraints().length == 0);
        } else {
            assertArrayEquals(propDefn.getValueConstraints(), valueConstraints);
        }
    }

    @Before
    public void beforeEach() {
        this.cnd = null;
        this.problems = new ArrayList<Throwable>();

        // Set up the importer ...
        this.importer = new CndImporter();
    }

    private NodeDefinition childDefn( final NodeTypeDefinition nodeType,
                                      final String name ) {
        for (final NodeDefinition defn : nodeType.getDeclaredChildNodeDefinitions()) {
            if (defn.getName().equals(name)) {
                return defn;
            }
        }

        assertFalse("Failed to find child node definition \"" + name + "\"", false); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    private NodeTypeDefinition defn( final String name ) {
        NodeTypeDefinition result = null;

        for (final NodeTypeDefinition defn : this.cnd.getNodeTypeDefinitions()) {
            if (defn.getName().equals(name)) {
                result = defn;
                break;
            }
        }

        assertNotNull("Failed to find node type definition \"" + name + "\"", result); //$NON-NLS-1$ //$NON-NLS-2$
        return result;
    }

    private int jcrPropertyType( final String typeName ) {
        final PropertyType type = PropertyType.find(typeName);
        return type.asJcrValue();
        // org.modeshape.jcr.value.PropertyType type = org.modeshape.jcr.value.PropertyType.valueFor(typeName.toLowerCase());
        // return PropertyTypeUtil.jcrPropertyTypeFor(type);
    }

    protected File openCndFile( final String cndFileName ) {
        final File result = new File(CND_FILE_PATH + cndFileName);
        assertTrue(result.exists());
        return result;
    }

    protected InputStream openCndStream( final String cndFileName ) {
        return this.getClass().getClassLoader().getResourceAsStream("cnd/" + cndFileName); //$NON-NLS-1$
    }

    //
    // @Test
    // public void shouldImportCndThatUsesExtensions() throws RepositoryException {
    // // this.importer.setDebug(true);
    //        String content = "<ex = 'http://namespace.com/ns'>\n" //$NON-NLS-1$
    //                + "[ex:NodeType] > ex:ParentType1, ex:ParentType2 abstract {mode:desc 'ex:NodeType description'} orderable mixin noquery primaryitem ex:property\n" //$NON-NLS-1$
    //                + "- ex:property (STRING) = 'default1', 'default2' mandatory autocreated protected multiple VERSION\n" //$NON-NLS-1$
    //                + " queryops '=, <>, <, <=, >, >=, LIKE' {mode:desc 'ex:property description'} {mode:altName Cool Property} nofulltext noqueryorder < 'constraint1', 'constraint2'" //$NON-NLS-1$
    //                + "+ ex:node (ex:reqType1, ex:reqType2) = ex:defaultType {} mandatory autocreated protected sns version"; //$NON-NLS-1$
    //        CompactNodeTypeDefinition cnd = this.importer.importFrom(content, problems, "string"); //$NON-NLS-1$
    //
    // // Check the namespace ...
    // assertEquals(cnd.getNamespaceMappings().size(), 1);
    // NamespaceMapping ns = cnd.getNamespaceMappings().iterator().next();
    //        assertEquals(ns.getUri(), "http://namespace.com/ns"); //$NON-NLS-1$
    //
    // Set<NodeTypeDefinition> defns = cnd.getNodeTypeDefinitions();
    // assertEquals(defns.size(), 1);
    //
    // NodeTypeDefinition defn = defns.iterator().next();
    //        assertEquals(defn.getName(), "ex:NodeType"); //$NON-NLS-1$
    // assertTrue(defn.isAbstract());
    // assertTrue(defn.hasOrderableChildNodes());
    // assertTrue(defn.isMixin());
    // assertFalse(defn.isQueryable());
    //        assertEquals(defn.getPrimaryItemName(), "ex:property"); //$NON-NLS-1$
    // String[] supertypeNames = defn.getDeclaredSupertypeNames();
    //        assertEquals(supertypeNames[0], "ex:ParentType1"); //$NON-NLS-1$
    //        assertEquals(supertypeNames[1], "ex:ParentType2"); //$NON-NLS-1$
    //
    // PropertyDefinition[] propDefns = defn.getDeclaredPropertyDefinitions();
    // assertEquals(propDefns.length, 1);
    // PropertyDefinition propDefn = propDefns[0];
    //        assertEquals(propDefn.getName(), "ex:property"); //$NON-NLS-1$
    // assertEquals(propDefn.getRequiredType(), PropertyType.STRING);
    // assertTrue(propDefn.isMandatory());
    // assertTrue(propDefn.isAutoCreated());
    // assertTrue(propDefn.isProtected());
    // assertTrue(propDefn.isMultiple());
    // assertEquals(propDefn.getOnParentVersion(), OnParentVersionAction.VERSION);
    // assertFalse(propDefn.isFullTextSearchable());
    // assertFalse(propDefn.isQueryOrderable());
    // Value[] defaultValues = propDefn.getDefaultValues();
    //        assertEquals(defaultValues[0].getString(), "default1"); //$NON-NLS-1$
    //        assertEquals(defaultValues[1].getString(), "default2"); //$NON-NLS-1$
    // String[] queryOps = propDefn.getAvailableQueryOperators();
    //        assertEquals(queryOps[0], "="); //$NON-NLS-1$
    //        assertEquals(queryOps[1], "<>"); //$NON-NLS-1$
    //        assertEquals(queryOps[2], "<"); //$NON-NLS-1$
    //        assertEquals(queryOps[3], "<="); //$NON-NLS-1$
    //        assertEquals(queryOps[4], ">"); //$NON-NLS-1$
    //        assertEquals(queryOps[5], ">="); //$NON-NLS-1$
    //        assertEquals(queryOps[6], "LIKE"); //$NON-NLS-1$
    // String[] constraints = propDefn.getValueConstraints();
    //        assertEquals(constraints[0], "constraint1"); //$NON-NLS-1$
    //        assertEquals(constraints[1], "constraint2"); //$NON-NLS-1$
    //
    // NodeDefinition[] childDefns = defn.getDeclaredChildNodeDefinitions();
    // assertEquals(childDefns.length, 1);
    // NodeDefinition childDefn = childDefns[0];
    //        assertEquals(childDefn.getName(), "ex:node"); //$NON-NLS-1$
    //        assertEquals(childDefn.getDefaultPrimaryTypeName(), "ex:defaultType"); //$NON-NLS-1$
    // assertTrue(childDefn.isMandatory());
    // assertTrue(childDefn.isAutoCreated());
    // assertTrue(childDefn.isProtected());
    // assertTrue(childDefn.allowsSameNameSiblings());
    // assertEquals(childDefn.getOnParentVersion(), OnParentVersionAction.VERSION);
    // String[] requiredTypeNames = childDefn.getRequiredPrimaryTypeNames();
    //        assertEquals(requiredTypeNames[0], "ex:reqType1"); //$NON-NLS-1$
    //        assertEquals(requiredTypeNames[1], "ex:reqType2"); //$NON-NLS-1$
    // }

    protected int opv( final OnParentVersion onParentVersioning ) {
        int opv = OnParentVersionAction.COPY;
        if (onParentVersioning != null) {
            switch (onParentVersioning) {
            case Abort:
                opv = OnParentVersionAction.ABORT;
                break;
            case Compute:
                opv = OnParentVersionAction.COMPUTE;
                break;
            case Copy:
                opv = OnParentVersionAction.COPY;
                break;
            case Ignore:
                opv = OnParentVersionAction.IGNORE;
                break;
            case Initialize:
                opv = OnParentVersionAction.INITIALIZE;
                break;
            case Version:
                opv = OnParentVersionAction.VERSION;
                break;
            }
        }
        return opv;
    }

    protected void printProblems() {
        for (final Throwable problem : this.problems) {
            System.out.println(problem.getLocalizedMessage());
        }
    }

    private PropertyDefinition propDefn( final NodeTypeDefinition nodeType,
                                         final String name ) {
        for (final PropertyDefinition defn : nodeType.getDeclaredPropertyDefinitions()) {
            if (defn.getName().equals(name)) {
                return defn;
            }
        }

        assertFalse("Failed to find property type definition \"" + name + "\"", false); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    @Test
    public void shouldImportCndForAircraft() throws Exception {
        this.importer.importFrom(openCndFile("aircraft.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    //
    // @Test
    // public void shouldImportBuiltInNodeTypes() throws Exception {
    // this.importer.importBuiltIns(this.problems);
    // if (this.problems.size() != 0)
    // printProblems();
    // assertThat(this.problems.size(), is(0));
    //
    // // Verify a select few from the JCR and ModeShape builtin types ...
    // registerImportedNamespaces();
    // assertNodeType("nt:base", new String[] {}, NO_PRIMARY_NAME, NodeOptions.Abstract, NodeOptions.Queryable);
    // assertNodeType("mode:root", new String[] { "nt:base", "mix:referenceable" }, NO_PRIMARY_NAME, NodeOptions.Queryable,
    // NodeOptions.Ordered);
    // }

    @Test
    public void shouldImportCndForCars() throws Exception {
        this.importer.importFrom(openCndFile("cars.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportCndForCommentsTest() throws Exception {
        final CompactNodeTypeDefinition cnd = this.importer.importFrom(openCndFile("commentsTest.cnd"), this.problems); //$NON-NLS-1$
        final NodeTypeDefinition nodeType = cnd.getNodeTypeDefinitions().get(0);

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());

        { // check node type comments
            final String expected = "comment above node type\n" + "comment same line as node type name\n" + "comment above supertypes\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "comment same line as first supertype\n" + "comment between 2 supertypes\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment same line as last supertype\n" + "comment above node type attributes\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment same line as abstract node type attribute\n" //$NON-NLS-1$
                    + "comment between abstract and orderable node type attributes\n" //$NON-NLS-1$
                    + "comment same line as orderable node type attribute\n" //$NON-NLS-1$
                    + "comment between orderable and mixin node type attributes\n" //$NON-NLS-1$
                    + "comment same line as mixin node type attribute\n" //$NON-NLS-1$
                    + "comment between mixin and noquery node type attributes\n" //$NON-NLS-1$
                    + "comment same line as noquery node type attribute\n" //$NON-NLS-1$
                    + "comment between noquery and primaryitem node type attributes\n" //$NON-NLS-1$
                    + "comment same line as primaryitem node type attribute"; //$NON-NLS-1$
            assertEquals(expected, nodeType.getComment());
        }

        { // check property comments
            final PropertyDefinition property = nodeType.getPropertyDefinitions().get(0);
            final String expected = "comment above property\n" + "comment same line as property name\n" + "comment above property type\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "comment above default values\n" + "comment same line as first default value\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment between default values\n" + "comment same line as last default value\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment above property attributes\n" + "comment same line as mandatory property attribute\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment between mandatory and autocreated property attributes\n" //$NON-NLS-1$
                    + "comment same line as autocreated property attribute\n" //$NON-NLS-1$
                    + "comment between autocreated and protected property attributes\n" //$NON-NLS-1$
                    + "comment same line as protected property attribute\n" //$NON-NLS-1$
                    + "comment between protected and multiple property attributes\n" //$NON-NLS-1$
                    + "comment same line as multiple property attribute\n" //$NON-NLS-1$
                    + "comment between multiple and VERSION property attributes\n" //$NON-NLS-1$
                    + "comment same line as VERSION property attribute\n" //$NON-NLS-1$
                    + "comment between VERSION and queryops property attributes\n" //$NON-NLS-1$
                    + "comment same line as queryops property attribute\n" //$NON-NLS-1$
                    + "comment between queryops and nofulltext property attributes\n" //$NON-NLS-1$
                    + "comment same line as nofulltext property attribute\n" //$NON-NLS-1$
                    + "comment between nofulltext and noqueryorder property attributes\n" //$NON-NLS-1$
                    + "comment same line as noqueryorder property attribute\n" + "comment above value constraints\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment same line as first value constraint\n" + "comment between value constraints\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment same line as last value constraint"; //$NON-NLS-1$
            assertEquals(expected, property.getComment());
        }

        { // check child node comments
            final ChildNodeDefinition childNode = nodeType.getChildNodeDefinitions().get(0);
            final String expected = "comment above child node\n" + "comment same line as child node name\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment above required types\n" + "comment same line as required types\n" + "comment above default type\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "comment same line as default type\n" + "comment above child node attributes\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment same line as mandatory child node attribute\n" //$NON-NLS-1$
                    + "comment between mandatory and autocreated child node attributes\n" //$NON-NLS-1$
                    + "comment same line as autocreated child node attribute\n" //$NON-NLS-1$
                    + "comment between autocreated and protected child node attributes\n" //$NON-NLS-1$
                    + "comment same line as protected child node attribute\n" //$NON-NLS-1$
                    + "comment between protected and sns child node attributes\n" //$NON-NLS-1$
                    + "comment same line as sns child node attribute\n" + "comment between sns and version child node attributes\n" //$NON-NLS-1$ //$NON-NLS-2$
                    + "comment same line as version child node attribute"; //$NON-NLS-1$
            assertEquals(expected, childNode.getComment());
        }
    }

    @Test
    public void shouldImportCndForImageSequencer() throws Exception {
        this.importer.importFrom(openCndFile("images.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportCndForJavaSequencer() throws Exception {
        this.importer.importFrom(openCndFile("javaSource.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportCndForMp3Sequencer() throws Exception {
        this.importer.importFrom(openCndFile("mp3.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportCndForTeiidSequencer() throws Exception {
        this.cnd = this.importer.importFrom(openCndFile("teiid.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        // registerImportedNamespaces();
        assertEquals(0, this.problems.size());
        assertNodeType("relational:catalog", new String[] { "nt:unstructured", "relational:relationalEntity" }, NO_PRIMARY_NAME, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       NodeOptions.Queryable, NodeOptions.Ordered);
    }

    @Test
    public void shouldImportCndThatHasNoChildren() {
        final String cnd = "<ns = 'http://namespace.com/ns'>\n" //$NON-NLS-1$
                + "<ex = 'http://namespace.com/ex'>\n" //$NON-NLS-1$
                + "[ns:NodeType] > ns:ParentType1, ns:ParentType2 abstract orderable mixin noquery primaryitem ex:property\n" //$NON-NLS-1$
                + "- ex:property (STRING) = 'default1', 'default2' mandatory autocreated protected multiple VERSION < 'constraint1', 'constraint2'\n" //$NON-NLS-1$
                + " queryops '=, <>, <, <=, >, >=, LIKE' nofulltext noqueryorder"; //$NON-NLS-1$
        this.importer.importFrom(cnd, this.problems, "string"); //$NON-NLS-1$
    }

    @Test
    public void shouldImportCndThatIsEmpty() throws Exception {
        this.importer.importFrom(openCndFile("empty.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportCndThatIsOnOneLine() {
        final String cnd = "<ns = 'http://namespace.com/ns'> " //$NON-NLS-1$
                + "<ex = 'http://namespace.com/ex'>\n" //$NON-NLS-1$
                + "[ns:NodeType] > ns:ParentType1, ns:ParentType2 abstract orderable mixin noquery primaryitem ex:property " //$NON-NLS-1$
                + "- ex:property (STRING) = 'default1', 'default2' mandatory autocreated protected multiple VERSION < 'constraint1', 'constraint2' " //$NON-NLS-1$
                + " queryops '=, <>, <, <=, >, >=, LIKE' nofulltext noqueryorder " //$NON-NLS-1$
                + "+ ns:node (ns:reqType1, ns:reqType2) = ns:defaultType mandatory autocreated protected sns version"; //$NON-NLS-1$
        this.importer.importFrom(cnd, this.problems, "string"); //$NON-NLS-1$
    }

    @Test
    public void shouldParseEmptyNodeType() {
        final String content = "[]"; //$NON-NLS-1$
        this.cnd = this.importer.importFrom(content, this.problems, "string"); //$NON-NLS-1$

        if (!this.problems.isEmpty()) {
            printProblems();
        }
    }

    @Test
    public void shouldImportCndThatUsesAllFeatures() throws RepositoryException {
        // this.importer.setDebug(true);
        final String content = "<ex = 'http://namespace.com/ns'>\n" //$NON-NLS-1$
                + "[ex:NodeType] > ex:ParentType1, ex:ParentType2 abstract orderable mixin noquery primaryitem ex:property\n" //$NON-NLS-1$
                + "- ex:property (STRING) = 'default1', 'default2' mandatory autocreated protected multiple VERSION\n" //$NON-NLS-1$
                + " queryops '=, <>, <, <=, >, >=, LIKE' nofulltext noqueryorder < 'constraint1', 'constraint2'" //$NON-NLS-1$
                + "+ ex:node (ex:reqType1, ex:reqType2) = ex:defaultType mandatory autocreated protected sns version"; //$NON-NLS-1$
        this.cnd = this.importer.importFrom(content, this.problems, "string"); //$NON-NLS-1$

        if (!this.problems.isEmpty()) {
            printProblems();
        }

        // check the namespace
        assertEquals(this.cnd.getNamespaceMappings().size(), 1);
        final NamespaceMapping ns = this.cnd.getNamespaceMappings().iterator().next();
        assertEquals(ns.getUri(), "http://namespace.com/ns"); //$NON-NLS-1$

        final List<NodeTypeDefinition> defns = this.cnd.getNodeTypeDefinitions();
        assertEquals(defns.size(), 1);

        final NodeTypeDefinition defn = defns.iterator().next();
        assertEquals(defn.getName(), "ex:NodeType"); //$NON-NLS-1$
        assertTrue(defn.isAbstract());
        assertTrue(defn.hasOrderableChildNodes());
        assertTrue(defn.isMixin());
        assertFalse(defn.isQueryable());
        assertEquals(defn.getPrimaryItemName(), "ex:property"); //$NON-NLS-1$
        final String[] supertypeNames = defn.getDeclaredSupertypeNames();
        assertEquals(supertypeNames[0], "ex:ParentType1"); //$NON-NLS-1$
        assertEquals(supertypeNames[1], "ex:ParentType2"); //$NON-NLS-1$

        final PropertyDefinition[] propDefns = defn.getDeclaredPropertyDefinitions();
        assertEquals(propDefns.length, 1);
        final PropertyDefinition propDefn = propDefns[0];
        assertEquals(propDefn.getName(), "ex:property"); //$NON-NLS-1$
        assertEquals(propDefn.getRequiredType(), PropertyType.STRING.asJcrValue());
        assertTrue(propDefn.isMandatory());
        assertTrue(propDefn.isAutoCreated());
        assertTrue(propDefn.isProtected());
        assertTrue(propDefn.isMultiple());
        assertEquals(propDefn.getOnParentVersion(), OnParentVersionAction.VERSION);
        assertFalse(propDefn.isFullTextSearchable());
        assertFalse(propDefn.isQueryOrderable());
        final Value[] defaultValues = propDefn.getDefaultValues();
        assertEquals(defaultValues[0].getString(), "default1"); //$NON-NLS-1$
        assertEquals(defaultValues[1].getString(), "default2"); //$NON-NLS-1$
        final String[] queryOps = propDefn.getAvailableQueryOperators();
        assertEquals(queryOps[0], "="); //$NON-NLS-1$
        assertEquals(queryOps[1], "<>"); //$NON-NLS-1$
        assertEquals(queryOps[2], "<"); //$NON-NLS-1$
        assertEquals(queryOps[3], "<="); //$NON-NLS-1$
        assertEquals(queryOps[4], ">"); //$NON-NLS-1$
        assertEquals(queryOps[5], ">="); //$NON-NLS-1$
        assertEquals(queryOps[6], "LIKE"); //$NON-NLS-1$
        final String[] constraints = propDefn.getValueConstraints();
        assertEquals(constraints[0], "constraint1"); //$NON-NLS-1$
        assertEquals(constraints[1], "constraint2"); //$NON-NLS-1$

        final NodeDefinition[] childDefns = defn.getDeclaredChildNodeDefinitions();
        assertEquals(childDefns.length, 1);
        final NodeDefinition childDefn = childDefns[0];
        assertEquals(childDefn.getName(), "ex:node"); //$NON-NLS-1$
        assertEquals(childDefn.getDefaultPrimaryTypeName(), "ex:defaultType"); //$NON-NLS-1$
        assertTrue(childDefn.isMandatory());
        assertTrue(childDefn.isAutoCreated());
        assertTrue(childDefn.isProtected());
        assertTrue(childDefn.allowsSameNameSiblings());
        assertEquals(childDefn.getOnParentVersion(), OnParentVersionAction.VERSION);
        final String[] requiredTypeNames = childDefn.getRequiredPrimaryTypeNames();
        assertEquals(requiredTypeNames[0], "ex:reqType1"); //$NON-NLS-1$
        assertEquals(requiredTypeNames[1], "ex:reqType2"); //$NON-NLS-1$
    }

    @Test
    public void shouldImportDerbyDdlCnd() throws Exception {
        this.importer.importFrom(openCndFile("DerbyDdl.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportJackRabbitCndReaderTestCnd() throws Exception {
        this.importer.importFrom(openCndFile("cnd-reader-test-input.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportJcrBuiltinNodeTypesForJSR170() throws Exception {
        this.cnd = this.importer.importFrom(openCndFile("jcr-builtins-170.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        // registerImportedNamespaces();
        assertEquals(0, this.problems.size());

        // [nt:base]
        // - jcr:primaryType (name) mandatory autocreated protected compute
        // - jcr:mixinTypes (name) protected multiple compute
        assertNodeType("nt:base", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("nt:base", "jcr:primaryType", "Name", NO_DEFAULTS, new PropertyOptions[] { PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                PropertyOptions.Autocreated, PropertyOptions.Protected, PropertyOptions.FullTextSearchable,
                PropertyOptions.QueryOrderable }, OnParentVersion.Compute);
        assertProperty("nt:base", "jcr:mixinTypes", "Name", NO_DEFAULTS, new PropertyOptions[] { PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                               PropertyOptions.Protected, PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable },
                       OnParentVersion.Compute);

        // [nt:unstructured]
        // orderable
        // - * (undefined) multiple
        // - * (undefined)
        // + * (nt:base) = nt:unstructured multiple version
        assertNodeType("nt:unstructured", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Ordered, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("nt:unstructured", "*", "Undefined", NO_DEFAULTS, PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        // We should test for this, but we'd have to rewrite node() to look more like
        // RepositoryNodeTypeManager.findChildNodeDefinition
        // assertProperty("nt:unstructured", "*", "Undefined", NO_DEFAULTS);
        assertChild("nt:unstructured", "*", "nt:base", "nt:unstructured", OnParentVersion.Version, ChildOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    ChildOptions.Sns);

        // [mix:referenceable]
        // mixin
        // - jcr:uuid (string) mandatory autocreated protected initialize
        assertNodeType("mix:referenceable", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Mixin, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("mix:referenceable", "jcr:uuid", "String", NO_DEFAULTS, OnParentVersion.Initialize, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.Mandatory, PropertyOptions.Autocreated, PropertyOptions.Protected,
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);

        // [mix:lockable]
        // mixin
        // - jcr:lockOwner (string) protected ignore
        // - jcr:lockIsDeep (boolean) protected ignore
        assertNodeType("mix:lockable", new String[] { "mix:referenceable" }, NO_PRIMARY_NAME, NodeOptions.Mixin, //$NON-NLS-1$ //$NON-NLS-2$
                       NodeOptions.Queryable);
        assertProperty("mix:lockable", "jcr:lockOwner", "String", NO_DEFAULTS, OnParentVersion.Ignore, PropertyOptions.Protected, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("mix:lockable", "jcr:lockIsDeep", "Boolean", NO_DEFAULTS, OnParentVersion.Ignore, PropertyOptions.Protected, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);

        // [nt:propertyDefinition]
        // - jcr:name (name)
        // - jcr:autoCreated (boolean) mandatory
        // - jcr:mandatory (boolean) mandatory
        // - jcr:onParentVersion (string) mandatory
        // < 'COPY', 'VERSION', 'INITIALIZE', 'COMPUTE', 'IGNORE', 'ABORT'
        // - jcr:protected (boolean) mandatory
        // - jcr:requiredType (string) mandatory
        // < 'STRING', 'BINARY', 'LONG', 'DOUBLE', 'BOOLEAN', 'DATE', 'NAME', 'PATH', 'REFERENCE', 'UNDEFINED'
        // - jcr:valueConstraints (string) multiple
        // - jcr:defaultValues (undefined) multiple
        // - jcr:multiple (boolean) mandatory
        assertNodeType("nt:propertyDefinition", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("nt:propertyDefinition", "jcr:name", "Name", NO_DEFAULTS, PropertyOptions.FullTextSearchable, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:autoCreated", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:mandatory", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:onParentVersion", "String", NO_DEFAULTS, new PropertyOptions[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.Mandatory, PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable }, null,
                       new String[] { "COPY", "VERSION", "INITIALIZE", "COMPUTE", "IGNORE", "ABORT" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertProperty("nt:propertyDefinition", "jcr:protected", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:requiredType", "String", NO_DEFAULTS, new PropertyOptions[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.Mandatory, PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable }, null,
                       new String[] { "STRING", "BINARY", "LONG", "DOUBLE", "BOOLEAN", "DATE", "NAME", "PATH", "REFERENCE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                               "UNDEFINED" }); //$NON-NLS-1$
        assertProperty("nt:propertyDefinition", "jcr:valueConstraints", "String", NO_DEFAULTS, PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:defaultValues", "Undefined", NO_DEFAULTS, PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:multiple", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
    }

    @Test
    public void shouldImportJcrBuiltinNodeTypesForJSR283() throws Exception {
        this.cnd = this.importer.importFrom(openCndFile("jcr-builtins-283-early-draft.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        // registerImportedNamespaces();
        assertEquals(0, this.problems.size());

        // [nt:base]
        // - jcr:primaryType (name) mandatory autocreated protected compute
        // - jcr:mixinTypes (name) protected multiple compute
        assertNodeType("nt:base", new String[] { "mode:defined" }, NO_PRIMARY_NAME, NodeOptions.Abstract, NodeOptions.Queryable); //$NON-NLS-1$ //$NON-NLS-2$
        assertProperty("nt:base", "jcr:primaryType", "Name", NO_DEFAULTS, new PropertyOptions[] { PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                PropertyOptions.Autocreated, PropertyOptions.Protected, PropertyOptions.FullTextSearchable,
                PropertyOptions.QueryOrderable }, OnParentVersion.Compute);
        assertProperty("nt:base", "jcr:mixinTypes", "Name", NO_DEFAULTS, new PropertyOptions[] { PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                               PropertyOptions.Protected, PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable },
                       OnParentVersion.Compute);

        // [nt:unstructured]
        // orderable
        // - * (undefined) multiple
        // - * (undefined)
        // + * (nt:base) = nt:unstructured multiple version
        assertNodeType("nt:unstructured", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Ordered, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("nt:unstructured", "*", "Undefined", NO_DEFAULTS, PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        // We should test for this, but we'd have to rewrite node() to look more like
        // RepositoryNodeTypeManager.findChildNodeDefinition
        // assertProperty("nt:unstructured", "*", "Undefined", NO_DEFAULTS);
        assertChild("nt:unstructured", "*", "nt:base", "nt:unstructured", OnParentVersion.Version, ChildOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    ChildOptions.Sns);

        // [mix:referenceable]
        // mixin
        // - jcr:uuid (string) mandatory autocreated protected initialize
        assertNodeType("mix:referenceable", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Mixin, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("mix:referenceable", "jcr:uuid", "String", NO_DEFAULTS, OnParentVersion.Initialize, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.Mandatory, PropertyOptions.Autocreated, PropertyOptions.Protected,
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);

        // [mix:lockable]
        // mixin
        // - jcr:lockOwner (string) protected ignore
        // - jcr:lockIsDeep (boolean) protected ignore
        assertNodeType("mix:lockable", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Mixin, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("mix:lockable", "jcr:lockOwner", "String", NO_DEFAULTS, OnParentVersion.Ignore, PropertyOptions.Protected, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("mix:lockable", "jcr:lockIsDeep", "Boolean", NO_DEFAULTS, OnParentVersion.Ignore, PropertyOptions.Protected, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);

        // [nt:propertyDefinition]
        // - jcr:name (name)
        // - jcr:autoCreated (boolean) mandatory
        // - jcr:mandatory (boolean) mandatory
        // - jcr:onParentVersion (string) mandatory
        // < 'COPY', 'VERSION', 'INITIALIZE', 'COMPUTE', 'IGNORE', 'ABORT'
        // - jcr:protected (boolean) mandatory
        // - jcr:requiredType (string) mandatory
        // < 'STRING', 'BINARY', 'LONG', 'DOUBLE', 'BOOLEAN', 'DATE', 'NAME', 'PATH', 'REFERENCE', 'UNDEFINED'
        // - jcr:valueConstraints (string) multiple
        // - jcr:defaultValues (undefined) multiple
        // - jcr:multiple (boolean) mandatory
        assertNodeType("nt:propertyDefinition", NO_SUPERTYPES, NO_PRIMARY_NAME, NodeOptions.Queryable); //$NON-NLS-1$
        assertProperty("nt:propertyDefinition", "jcr:name", "Name", NO_DEFAULTS, PropertyOptions.FullTextSearchable, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:autoCreated", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:mandatory", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:onParentVersion", "String", NO_DEFAULTS, new PropertyOptions[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.Mandatory, PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable }, null,
                       new String[] { "COPY", "VERSION", "INITIALIZE", "COMPUTE", "IGNORE", "ABORT" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertProperty("nt:propertyDefinition", "jcr:protected", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:requiredType", "String", NO_DEFAULTS, new PropertyOptions[] { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.Mandatory, PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable }, null,
                       new String[] { "STRING", "BINARY", "LONG", "DOUBLE", "BOOLEAN", "DATE", "NAME", "PATH", "REFERENCE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                               "UNDEFINED" }); //$NON-NLS-1$
        assertProperty("nt:propertyDefinition", "jcr:valueConstraints", "String", NO_DEFAULTS, PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:defaultValues", "Undefined", NO_DEFAULTS, PropertyOptions.Multiple, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
        assertProperty("nt:propertyDefinition", "jcr:multiple", "Boolean", NO_DEFAULTS, PropertyOptions.Mandatory, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                       PropertyOptions.FullTextSearchable, PropertyOptions.QueryOrderable);
    }

    //
    // protected void registerImportedNamespaces() {
    // for (NamespaceRegistry.Namespace ns : this.importer.getNamespaces()) {
    // context.getNamespaceRegistry().register(ns.getPrefix(), ns.getNamespaceUri());
    // }
    // }

    @Test
    public void shouldImportOracleDdlCnd() throws Exception {
        this.importer.importFrom(openCndFile("OracleDdl.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportPostgresDdlCnd() throws Exception {
        this.importer.importFrom(openCndFile("PostgresDdl.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldImportStandardDdlCnd() throws Exception {
        this.importer.importFrom(openCndFile("StandardDdl.cnd"), this.problems); //$NON-NLS-1$

        if (this.problems.size() != 0) {
            printProblems();
        }

        assertEquals(0, this.problems.size());
    }

    @Test
    public void shouldNotImportFileThatIsNotAValidCnd() throws Exception {
        this.importer.importFrom(openCndFile("invalid.cnd"), this.problems); //$NON-NLS-1$
        assertEquals(1, this.problems.size());
    }

    @Test
    public void shouldParseMinimalNodeDefinition() {
        final String content = "[nodeTypeName]"; //$NON-NLS-1$
        this.cnd = this.importer.parse(content);

        final List<NodeTypeDefinition> nodeTypeDefns = this.cnd.getNodeTypeDefinitions();
        assertEquals(1, nodeTypeDefns.size());

        final NodeTypeDefinition nodeTypeDefn = nodeTypeDefns.iterator().next();
        assertEquals("nodeTypeName", nodeTypeDefn.getName()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMinimalNodeDefinitionWithSupertype() {
        final String cnd = "[nodeTypeName] > supertype"; //$NON-NLS-1$
        this.cnd = this.importer.parse(cnd);

        final List<NodeTypeDefinition> nodeTypeDefns = this.cnd.getNodeTypeDefinitions();
        assertEquals(1, nodeTypeDefns.size());

        final NodeTypeDefinition nodeTypeDefn = nodeTypeDefns.iterator().next();
        assertEquals("nodeTypeName", nodeTypeDefn.getName()); //$NON-NLS-1$

        final String[] superTypes = nodeTypeDefn.getDeclaredSupertypeNames();
        assertEquals(1, superTypes.length);
        assertEquals("supertype", superTypes[0]); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMinimalNodeDefinitionWithSupertypes() {
        final String cnd = "[nodeTypeName] > supertype1, supertype2"; //$NON-NLS-1$
        this.cnd = this.importer.parse(cnd);

        final List<NodeTypeDefinition> nodeTypeDefns = this.cnd.getNodeTypeDefinitions();
        assertEquals(1, nodeTypeDefns.size());

        final NodeTypeDefinition nodeTypeDefn = nodeTypeDefns.iterator().next();
        assertEquals("nodeTypeName", nodeTypeDefn.getName()); //$NON-NLS-1$

        final String[] superTypes = nodeTypeDefn.getDeclaredSupertypeNames();
        assertEquals(2, superTypes.length);
        assertEquals("supertype1", superTypes[0]); //$NON-NLS-1$
        assertEquals("supertype2", superTypes[1]); //$NON-NLS-1$
    }

    @Test
    public void shouldParseNamespaceDeclarationWithQuotedUriAndQuotedPrefix() {
        final String content = "<'ns' = 'http://namespace.com/ns'>"; //$NON-NLS-1$
        this.cnd = this.importer.parse(content);

        final List<NamespaceMapping> namespaces = this.cnd.getNamespaceMappings();
        assertEquals(1, namespaces.size());

        final NamespaceMapping namespace = namespaces.iterator().next();
        assertEquals("ns", namespace.getPrefix()); //$NON-NLS-1$
        assertEquals("http://namespace.com/ns", namespace.getUri()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseNamespacesAndNodeTypesInAnyOrder() {
        final String content = "[foo] > supertype1, supertype2\n" //$NON-NLS-1$
                + "<ns1 = 'http://namespace1.com/ns1'>\n" //$NON-NLS-1$
                + "<ns2 = 'http://namespace2.com/ns2'>\n" //$NON-NLS-1$
                + "[bar] > supertype1, supertype2\n" //$NON-NLS-1$
                + "<ns3 = 'http://namespace3.com/ns3'>"; //$NON-NLS-1$
        this.cnd = this.importer.parse(content);

        final List<NamespaceMapping> namespaces = this.cnd.getNamespaceMappings();
        assertEquals(3, namespaces.size());

        final List<NodeTypeDefinition> nodeTypes = this.cnd.getNodeTypeDefinitions();
        assertEquals(2, nodeTypes.size());
    }

    @Test
    public void shouldParseNamespaceDeclarationWithQuotedUriAndUnquotedPrefix() {
        final String content = "<ns = 'http://namespace.com/ns'>"; //$NON-NLS-1$
        this.cnd = this.importer.parse(content);

        final List<NamespaceMapping> namespaces = this.cnd.getNamespaceMappings();
        assertEquals(1, namespaces.size());

        final NamespaceMapping namespace = namespaces.iterator().next();
        assertEquals("ns", namespace.getPrefix()); //$NON-NLS-1$
        assertEquals("http://namespace.com/ns", namespace.getUri()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseNamespaceDeclarationWithUnquotedUriAndQuotedPrefix() {
        final String content = "<'ns' = http_namespace.com_ns>"; //$NON-NLS-1$
        this.cnd = this.importer.parse(content);

        final List<NamespaceMapping> namespaces = this.cnd.getNamespaceMappings();
        assertEquals(1, namespaces.size());

        final NamespaceMapping namespace = namespaces.iterator().next();
        assertEquals("ns", namespace.getPrefix()); //$NON-NLS-1$
        assertEquals("http_namespace.com_ns", namespace.getUri()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseNamespaceDeclarationWithUnquotedUriAndUnquotedPrefix() {
        final String content = "<ns = http_namespace.com_ns>"; //$NON-NLS-1$
        this.cnd = this.importer.parse(content);

        final List<NamespaceMapping> namespaces = this.cnd.getNamespaceMappings();
        assertEquals(1, namespaces.size());

        final NamespaceMapping namespace = namespaces.iterator().next();
        assertEquals("ns", namespace.getPrefix()); //$NON-NLS-1$
        assertEquals("http_namespace.com_ns", namespace.getUri()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseNodeDefinitionWithNameThatIsKeyword() {
        final String cnd = "[abstract] > supertype1, supertype2"; //$NON-NLS-1$
        this.cnd = this.importer.parse(cnd);

        final List<NodeTypeDefinition> nodeTypeDefns = this.cnd.getNodeTypeDefinitions();
        assertEquals(1, nodeTypeDefns.size());

        final NodeTypeDefinition nodeTypeDefn = nodeTypeDefns.iterator().next();
        assertEquals("abstract", nodeTypeDefn.getName()); //$NON-NLS-1$
    }

    @Test(expected = ParsingException.class)
    public void shouldReportErrorIfTheNodeTypeNameIsNotFollowedByClosingBracket() {
        final String content = "<ns = 'http://namespace.com/ns'> [  abstract"; //$NON-NLS-1$
        this.importer.parse(content);
    }

    public static enum ChildOptions {
        Autocreated,
        Mandatory,
        Multiple,
        Protected,
        Sns
    }

    public static enum NodeOptions {
        Abstract,
        Mixin,
        Ordered,
        Queryable
    }

    //
    // private void assertChild( String nodeTypeName,
    // String childName,
    // String requiredType,
    // String defaultPrimaryType,
    // ChildOptions[] childOptions,
    // OnParentVersion onParentVersioning ) {
    // assertChild(nodeTypeName, childName, new String[] { requiredType }, defaultPrimaryType, childOptions, onParentVersioning);
    // }

    public static enum OnParentVersion {
        Abort,
        Compute,
        Copy,
        Ignore,
        Initialize,
        Version
    }

    public static enum PropertyOptions {
        Autocreated,
        FullTextSearchable,
        Mandatory,
        Multiple,
        Protected,
        QueryOrderable
    }
}
