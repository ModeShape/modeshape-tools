/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.ItemDefinition;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.attributes.PropertyType;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.jboss.tools.modeshape.jcr.cnd.CndValidator.StatusCodes;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class CndValidatorTest {

    private ChildNodeDefinition childNodeDefinition;
    private CompactNodeTypeDefinition cnd;
    private NamespaceMapping namespaceMapping;
    private NodeTypeDefinition nodeTypeDefinition;
    private PropertyDefinition propertyDefinition;

    @Before
    public void beforeEach() {
        cnd = new CompactNodeTypeDefinition();
        namespaceMapping = new NamespaceMapping();

        nodeTypeDefinition = new NodeTypeDefinition();
        nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());

        childNodeDefinition = new ChildNodeDefinition(nodeTypeDefinition);
        childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());

        propertyDefinition = new PropertyDefinition(nodeTypeDefinition);
        propertyDefinition.setName(Constants.QUALIFIED_NAME1.get());
    }

    @Test
    public void childNodeDefinitionWithEmptyNameShouldBeAnError() {
        // setup
        childNodeDefinition.setName(null);
        ValidationStatus status = CndValidator.validateChildNodeDefinition(childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue(status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME));

        // setup
        childNodeDefinition.setName(Utils.EMPTY_STRING);
        status = CndValidator.validateChildNodeDefinition(childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void childNodeDefinitionWithInvalidDefaultTypeNameShouldBeAnError() {
        // setup
        childNodeDefinition.setName("name"); //$NON-NLS-1$
        childNodeDefinition.setDefaultPrimaryTypeName("missingName:"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateChildNodeDefinition(childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void childNodeDefinitionWithInvalidNameShouldBeAnError() {
        // setup
        childNodeDefinition.setName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateChildNodeDefinition(childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void childNodeDefinitionWithInvalidRequiredTypeNameShouldBeAnError() {
        // setup
        childNodeDefinition.setName("name"); //$NON-NLS-1$
        childNodeDefinition.addRequiredType("missingName:"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateChildNodeDefinition(childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void childNodeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        childNodeDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(childNodeDefinition,
                                                                  Constants.Helper.getDefaultNamespacePrefixes(), null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void cndWithoutNamespaceMappingsAndNodeTypeDefintionsShouldBeAWarning() {
        // setup
        final ValidationStatus status = CndValidator.validateCnd(cnd);

        // tests
        assertTrue(status.isWarning());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.CND_HAS_NO_NAMESPACES_OR_NODE_TYPE_DEFINITIONS)); //$NON-NLS-1$
    }

    @Test
    public void defaultTypeNameShoudMatchRequiredType() {
        // setup
        childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        childNodeDefinition.addRequiredType(Constants.QUALIFIED_NAME1.get());
        childNodeDefinition.setDefaultPrimaryTypeName(Constants.QUALIFIED_NAME2.get());
        final ValidationStatus status = CndValidator.validateDefaultType(childNodeDefinition,
                                                                         Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DEFAULT_TYPE_DOES_NOT_MATCH_REQUIRED_TYPE)); //$NON-NLS-1$
    }

    @Test
    public void defaultTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        childNodeDefinition.setDefaultPrimaryTypeName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateDefaultType(childNodeDefinition,
                                                                         Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void defaultValueWithInvalidQualifierShouldBeAnError() {
        // setup
        propertyDefinition.setType(PropertyType.NAME);
        propertyDefinition.addDefaultValue("bogus:value"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateDefaultValues(propertyDefinition,
                                                                           Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_QUALIFIER_FOR_DEFAULT_VALUE)); //$NON-NLS-1$
    }

    @Test
    public void emptyNamespaceMappingPrefixShouldBeAnError() {
        // setup
        namespaceMapping.setUri("uri"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_LOCAL_NAME)); //$NON-NLS-1$
    }

    @Test
    public void emptyNamespaceMappingUriShouldBeAnError() {
        // setup
        namespaceMapping.setPrefix("prefix"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_VALUE)); //$NON-NLS-1$
    }

    @Test
    public void emptyQualifiedNameQualifierShouldBeValid() {
        assertTrue(CndValidator.validateQualifiedName(Constants.NAME_WITH_EMPTY_QUALIFIER, "propertyName", //$NON-NLS-1$
                                                      Constants.Helper.getDefaultQualifiers(), null).isOk());
    }

    @Test
    public void emptyQueryOperatorShouldBeAnError() {
        // setup
        final ValidationStatus status = CndValidator.validateQueryOperator(Utils.EMPTY_STRING, "propName"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_QUERY_OPERATOR)); //$NON-NLS-1$
    }

    @Test
    public void invalidQualifiedNameQualifierShouldBeAnError() {
        // setup
        final QualifiedName qname = new QualifiedName(Constants.QUALIFIER1 + "Changed", Constants.UNQUALIFIED_NAME1); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateQualifiedName(qname, "propertyName", //$NON-NLS-1$
                                                                           Constants.Helper.getDefaultQualifiers(), null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void invalidQueryOperatorShouldBeAnError() {
        // setup
        final ValidationStatus status = CndValidator.validateQueryOperator("a", "propName"); //$NON-NLS-1$ //$NON-NLS-2$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_QUERY_OPERATOR)); //$NON-NLS-1$
    }

    @Test
    public void localNameEqualToParentShouldBeAnError() {
        // setup
        childNodeDefinition.setName(".."); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_EQUAL_TO_SELF_OR_PARENT)); //$NON-NLS-1$
    }

    @Test
    public void localNameEqualToSelfShouldBeAnError() {
        // setup
        childNodeDefinition.setName("."); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_EQUAL_TO_SELF_OR_PARENT)); //$NON-NLS-1$
    }

    @Test
    public void localNameWithInvalidCharactersShouldBeAnError() {
        // setup
        childNodeDefinition.setName("name/"); //$NON-NLS-1$
        ValidationStatus status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        childNodeDefinition.setName("name:"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        childNodeDefinition.setName("name["); //$NON-NLS-1$
        status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        childNodeDefinition.setName("name]"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        childNodeDefinition.setName("name|"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        childNodeDefinition.setName("name*"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithDuplicateChildNodeNamesShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        childNodeDefinition.setName("name"); //$NON-NLS-1$);
        final ChildNodeDefinition child2 = new ChildNodeDefinition(nodeTypeDefinition);
        child2.setName(childNodeDefinition.getName());

        nodeTypeDefinition.addChildNodeDefinition(childNodeDefinition);
        nodeTypeDefinition.addChildNodeDefinition(child2);

        // tests
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, true);
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_CHILD_NODE_DEFINITION_NAME)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithDuplicatePropertyNamesShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        propertyDefinition.setName("name"); //$NON-NLS-1$
        final PropertyDefinition prop2 = new PropertyDefinition(nodeTypeDefinition);
        prop2.setName(propertyDefinition.getName());

        nodeTypeDefinition.addPropertyDefinition(propertyDefinition);
        nodeTypeDefinition.addPropertyDefinition(prop2);

        // tests
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, true);
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_PROPERTY_DEFINITION_NAME)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithEmptyNameShouldAnError() {
        // setup
        nodeTypeDefinition.setName(null);
        ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$

        // setup
        nodeTypeDefinition.setName(Utils.EMPTY_STRING);
        status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithInvalidNameShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithInvalidPrimaryItemNameShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        nodeTypeDefinition.setPrimaryItemName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithInvalidSuperTypeNameShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        nodeTypeDefinition.addSuperType("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition, null, null, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(nodeTypeDefinition,
                                                                  Constants.Helper.getDefaultNamespacePrefixes(), null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void nullQueryOperatorShouldBeAnError() {
        // setup
        final ValidationStatus status = CndValidator.validateQueryOperator(null, "propName"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_QUERY_OPERATOR)); //$NON-NLS-1$
    }

    @Test
    public void primaryItemNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        nodeTypeDefinition.setPrimaryItemName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(nodeTypeDefinition,
                                                                                Constants.Helper.getDefaultNamespacePrefixes(),
                                                                                null, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithEmptyNameShouldNotBeValid() {
        // setup
        propertyDefinition.setName(null);
        ValidationStatus status = CndValidator.validatePropertyDefinition(propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$

        // setup
        propertyDefinition.setName(Utils.EMPTY_STRING);
        status = CndValidator.validatePropertyDefinition(propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithInvalidDefaultValueShouldBeAnError() {
        // setup
        propertyDefinition.setName("name"); //$NON-NLS-1$
        propertyDefinition.setType(PropertyType.LONG);
        propertyDefinition.addDefaultValue("notALongValue"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validatePropertyDefinition(propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithInvalidNameShouldBeAnError() {
        // setup
        propertyDefinition.setName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validatePropertyDefinition(propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithMultipleDefaultValuesButSingleValuedShouldBeAnError() {
        // setup
        propertyDefinition.setName("name"); //$NON-NLS-1$
        propertyDefinition.addDefaultValue("defaultValue1"); //$NON-NLS-1$
        propertyDefinition.addDefaultValue("defaultValue2"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validatePropertyDefinition(propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.MULTIPLE_DEFAULT_VALUES_FOR_SINGLE_VALUED_PROPERTY)); //$NON-NLS-1$
    }

    @Test
    public void propertyNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        propertyDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(propertyDefinition,
                                                                  Constants.Helper.getDefaultNamespacePrefixes(), null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void requiredTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        childNodeDefinition.addRequiredType(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateRequiredTypes(childNodeDefinition,
                                                                           Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void residualChildNodeDoesNotNeedDefaultType() {
        // setup
        childNodeDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        childNodeDefinition.addRequiredType(Constants.QUALIFIED_NAME1.get());
        final ValidationStatus status = CndValidator.validateDefaultType(childNodeDefinition,
                                                                         Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertFalse(status.isError());
    }

    @Test
    public void shouldAllowChildNodeDefinitionsWithResidualNames() {
        // setup
        childNodeDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final ValidationStatus status = CndValidator.validateName(childNodeDefinition, null, null);

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldAllowMultipleChildNodeDefinitionsWithResidualNames() {
        // setup
        nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        childNodeDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final ChildNodeDefinition childNode2 = new ChildNodeDefinition(nodeTypeDefinition);
        childNode2.setName(ItemDefinition.RESIDUAL_NAME);
        nodeTypeDefinition.addChildNodeDefinition(childNodeDefinition);
        nodeTypeDefinition.addChildNodeDefinition(childNode2);
        final ValidationStatus status = CndValidator.validateChildNodeDefinitions(nodeTypeDefinition.getName(), null,
                                                                                  nodeTypeDefinition.getChildNodeDefinitions());

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldAllowMultiplePropertyDefinitionsWithResidualNames() {
        // setup
        nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        propertyDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final PropertyDefinition propDefn2 = new PropertyDefinition(nodeTypeDefinition);
        propDefn2.setName(ItemDefinition.RESIDUAL_NAME);
        nodeTypeDefinition.addPropertyDefinition(propertyDefinition);
        nodeTypeDefinition.addPropertyDefinition(propDefn2);
        final ValidationStatus status = CndValidator.validatePropertyDefinitions(nodeTypeDefinition.getName(), null,
                                                                                 nodeTypeDefinition.getPropertyDefinitions());

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldAllowPropertyDefinitionsWithResidualNames() {
        // setup
        propertyDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final ValidationStatus status = CndValidator.validateName(propertyDefinition, null, null);

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldNotAllowBuiltInPrefixWithIncorrectUri() {
        // setup
        namespaceMapping.setPrefix(Constants.BuiltInNamespaces.JCR.getPrefix());
        namespaceMapping.setUri("foo"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_URI_FOR_BUILT_IN_NAMESPACE_PREFIX)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowBuiltInUriWithIncorrectPrefix() {
        // setup
        namespaceMapping.setPrefix("foo"); //$NON-NLS-1$
        namespaceMapping.setUri(Constants.BuiltInNamespaces.JCR.getUri());
        final ValidationStatus status = CndValidator.validateNamespaceMapping(namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_PREFIX_FOR_BUILT_IN_NAMESPACE_URI)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowChildNodeDefinitionsWithSameName() {
        // setup
        nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        childNodeDefinition.setName("name"); //$NON-NLS-1$
        final ChildNodeDefinition childNode2 = new ChildNodeDefinition(nodeTypeDefinition);
        childNode2.setName(childNodeDefinition.getName());
        nodeTypeDefinition.addChildNodeDefinition(childNodeDefinition);
        nodeTypeDefinition.addChildNodeDefinition(childNode2);
        final ValidationStatus status = CndValidator.validateChildNodeDefinitions(nodeTypeDefinition.getName(), null,
                                                                                  nodeTypeDefinition.getChildNodeDefinitions());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_CHILD_NODE_DEFINITION_NAME)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowDuplicateNamespacePrefixes() {
        // setup
        // create a namespace mapping with a prefix that already exists and a URI that doesn't exist in the default namespaces
        final NamespaceMapping namespaceMapping = new NamespaceMapping(Constants.NAMESPACE_PREFIX1, "xyz"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(namespaceMapping,
                                                                              Constants.Helper.getDefaultNamespaces());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_NAMESPACE_PREFIX)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowDuplicateNamespaceUris() {
        // setup
        // create a namespace mapping with a URI that already exists and a prefix that doesn't exist in the default namespaces
        final NamespaceMapping namespaceMapping = new NamespaceMapping("xyz", Constants.NAMESPACE_URI1); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(namespaceMapping,
                                                                              Constants.Helper.getDefaultNamespaces());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_NAMESPACE_URI)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowDuplicateQualifiedNames() {
        // setup
        final ValidationStatus status = CndValidator.validateQualifiedName(Constants.QUALIFIED_NAME1,
                                                                           "propertyName", //$NON-NLS-1$
                                                                           Constants.Helper.getDefaultQualifiers(),
                                                                           Constants.Helper.getDefaultQualifiedNames());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_QUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowPropertyDefinitionsWithSameName() {
        // setup
        nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        propertyDefinition.setName("name"); //$NON-NLS-1$
        final PropertyDefinition propDefn2 = new PropertyDefinition(nodeTypeDefinition);
        propDefn2.setName(propertyDefinition.getName());
        nodeTypeDefinition.addPropertyDefinition(propertyDefinition);
        nodeTypeDefinition.addPropertyDefinition(propDefn2);
        final ValidationStatus status = CndValidator.validatePropertyDefinitions(nodeTypeDefinition.getName(), null,
                                                                                 nodeTypeDefinition.getPropertyDefinitions());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_PROPERTY_DEFINITION_NAME)); //$NON-NLS-1$
    }

    @Test
    public void shouldValidateAllQueryOperators() {
        for (final QueryOperator operator : QueryOperator.values()) {
            assertTrue(CndValidator.validateQueryOperator(operator.toString(), "propName").isOk()); //$NON-NLS-1$
        }
    }

    @Test
    public void shouldValidatePathsWithNoErrors() {
        final String PROP_NAME = "testProperty"; //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A/B/C[1]/D[2]", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A/B/C[1]/../../D[2]", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A/B/C[1]/././D[2]", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A/B/C[1]/../D[2]/./E/..", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A/../../../B", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("../../B", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("A/./../../B/.", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("/.", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath(".", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("/A", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("/A/B/C[1]/D[2]", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("/A/B/C[1]/../../D[2]", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("/A/B/C[1]/././D[2]", PROP_NAME).isError()); //$NON-NLS-1$
        assertFalse(CndValidator.validatePath("/A/B/C[1]/../D[2]/./E/..", PROP_NAME).isError()); //$NON-NLS-1$
    }

    @Test
    public void superTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        nodeTypeDefinition.addSuperType(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateSuperTypes(nodeTypeDefinition.getName(),
                                                                        Constants.Helper.getDefaultNamespacePrefixes(),
                                                                        nodeTypeDefinition.getState(NodeTypeDefinition.PropertyName.SUPERTYPES),
                                                                        nodeTypeDefinition.getSupertypes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }
}
