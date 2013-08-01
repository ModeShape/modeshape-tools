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
        this.cnd = new CompactNodeTypeDefinition();

        this.namespaceMapping = new NamespaceMapping();
        this.cnd.addNamespaceMapping(this.namespaceMapping);

        this.nodeTypeDefinition = new NodeTypeDefinition();
        this.cnd.addNodeTypeDefinition(this.nodeTypeDefinition);

        this.childNodeDefinition = new ChildNodeDefinition(this.nodeTypeDefinition);
        this.nodeTypeDefinition.addChildNodeDefinition(this.childNodeDefinition);

        this.propertyDefinition = new PropertyDefinition(this.nodeTypeDefinition);
        this.nodeTypeDefinition.addPropertyDefinition(this.propertyDefinition);
    }

    @Test
    public void childNodeDefinitionWithEmptyNameShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName(null);
        ValidationStatus status = CndValidator.validateChildNodeDefinition(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue(status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME));

        // setup
        this.childNodeDefinition.setName(Utils.EMPTY_STRING);
        status = CndValidator.validateChildNodeDefinition(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void childNodeDefinitionWithInvalidDefaultTypeNameShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName("name"); //$NON-NLS-1$
        this.childNodeDefinition.setDefaultPrimaryTypeName("missingName:"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateChildNodeDefinition(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void childNodeDefinitionWithInvalidNameShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateChildNodeDefinition(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void childNodeDefinitionWithInvalidRequiredTypeNameShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName("name"); //$NON-NLS-1$
        this.childNodeDefinition.addRequiredType("missingName:"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateChildNodeDefinition(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void childNodeNameWithQualifierAndNoValidPrefixesShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void childNodeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(this.childNodeDefinition,
                                                                  Constants.Helper.getDefaultNamespacePrefixes(),
                                                                  null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void cndWithoutNamespaceMappingsAndNodeTypeDefintionsShouldBeAWarning() {
        // setup
        final ValidationStatus status = CndValidator.validateCnd(new CompactNodeTypeDefinition());

        // tests
        assertTrue(status.isWarning());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.CND_HAS_NO_NAMESPACES_OR_NODE_TYPE_DEFINITIONS)); //$NON-NLS-1$
    }

    @Test
    public void defaultTypeNameShoudMatchRequiredType() {
        // setup
        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        this.childNodeDefinition.addRequiredType(Constants.QUALIFIED_NAME1.get());
        this.childNodeDefinition.setDefaultPrimaryTypeName(Constants.QUALIFIED_NAME2.get());
        final ValidationStatus status = CndValidator.validateDefaultType(this.childNodeDefinition,
                                                                         Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DEFAULT_TYPE_DOES_NOT_MATCH_REQUIRED_TYPE)); //$NON-NLS-1$
    }

    @Test
    public void defaultTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        this.childNodeDefinition.setDefaultPrimaryTypeName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateDefaultType(this.childNodeDefinition,
                                                                         Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void defaultValueWithInvalidQualifierShouldBeAnError() {
        // setup
        this.propertyDefinition.setType(PropertyType.NAME);
        this.propertyDefinition.addDefaultValue("bogus:value"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateDefaultValues(this.propertyDefinition,
                                                                           Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_QUALIFIER_FOR_DEFAULT_VALUE)); //$NON-NLS-1$
    }

    @Test
    public void emptyNamespaceMappingPrefixShouldBeAnError() {
        // setup
        this.namespaceMapping.setUri("uri"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(this.namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_LOCAL_NAME)); //$NON-NLS-1$
    }

    @Test
    public void emptyNamespaceMappingUriShouldBeAnError() {
        // setup
        this.namespaceMapping.setPrefix("prefix"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(this.namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_VALUE)); //$NON-NLS-1$
    }

    @Test
    public void emptyQualifiedNameQualifierShouldBeValid() {
        assertTrue(CndValidator.validateQualifiedName(Constants.NAME_WITH_EMPTY_QUALIFIER, "propertyName", //$NON-NLS-1$
                                                      Constants.Helper.getDefaultQualifiers(),
                                                      null).isOk());
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
    public void emptyValueConstraintShouldBeAnError() {
        // setup
        final ValidationStatus status = CndValidator.validateValueConstraint(Utils.EMPTY_STRING);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_VALUE_CONSTRAINT)); //$NON-NLS-1$
    }

    @Test
    public void invalidQualifiedNameQualifierShouldBeAnError() {
        // setup
        final QualifiedName qname = new QualifiedName(Constants.QUALIFIER1 + "Changed", Constants.UNQUALIFIED_NAME1); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateQualifiedName(qname, "propertyName", //$NON-NLS-1$
                                                                           Constants.Helper.getDefaultQualifiers(),
                                                                           null);

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
        this.childNodeDefinition.setName(".."); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_EQUAL_TO_SELF_OR_PARENT)); //$NON-NLS-1$
    }

    @Test
    public void localNameEqualToSelfShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName("."); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_EQUAL_TO_SELF_OR_PARENT)); //$NON-NLS-1$
    }

    @Test
    public void localNameWithInvalidCharactersShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName("name/"); //$NON-NLS-1$
        ValidationStatus status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        this.childNodeDefinition.setName("name:"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        this.childNodeDefinition.setName("name["); //$NON-NLS-1$
        status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        this.childNodeDefinition.setName("name]"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        this.childNodeDefinition.setName("name|"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$

        // setup
        this.childNodeDefinition.setName("name*"); //$NON-NLS-1$
        status = CndValidator.validateLocalName(this.childNodeDefinition.getName(), "name"); //$NON-NLS-1$

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void mixinsShouldNotAllowNonMixinSupertype() {
        this.nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());

        final NodeTypeDefinition mixin = new NodeTypeDefinition();
        mixin.setMixin(true);
        this.cnd.addNodeTypeDefinition(mixin);
        mixin.addSuperType(this.nodeTypeDefinition.getName());

        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(mixin, this.cnd, true);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.SUPER_TYPE_NOT_A_MIXIN)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionCannotHaveItselfAsASupertype() {
        this.nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        this.nodeTypeDefinition.addSuperType(this.nodeTypeDefinition.getName());
        final ValidationStatus status = CndValidator.validateSuperTypes(this.nodeTypeDefinition.getName(),
                                                                        Constants.Helper.getDefaultNamespacePrefixes(),
                                                                        this.nodeTypeDefinition.getState(NodeTypeDefinition.PropertyName.SUPERTYPES),
                                                                        this.nodeTypeDefinition.getSupertypes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NODE_TYPE_CANNOT_BE_SUPER_TYPE_OF_ITSELF)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithDuplicateChildNodeNamesShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        this.childNodeDefinition.setName("name"); //$NON-NLS-1$);
        final ChildNodeDefinition child2 = new ChildNodeDefinition(this.nodeTypeDefinition);
        child2.setName(this.childNodeDefinition.getName());

        this.nodeTypeDefinition.addChildNodeDefinition(this.childNodeDefinition);
        this.nodeTypeDefinition.addChildNodeDefinition(child2);

        // tests
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, true);
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_CHILD_NODE_DEFINITION_NAME)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithDuplicatePropertyNamesShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        this.propertyDefinition.setName("name"); //$NON-NLS-1$
        final PropertyDefinition prop2 = new PropertyDefinition(this.nodeTypeDefinition);
        prop2.setName(this.propertyDefinition.getName());

        this.nodeTypeDefinition.addPropertyDefinition(this.propertyDefinition);
        this.nodeTypeDefinition.addPropertyDefinition(prop2);

        // tests
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, true);
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_PROPERTY_DEFINITION_NAME)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithEmptyNameShouldAnError() {
        // setup
        this.nodeTypeDefinition.setName(null);
        ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$

        // setup
        this.nodeTypeDefinition.setName(Utils.EMPTY_STRING);
        status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithInvalidNameShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithInvalidPrimaryItemNameShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        this.nodeTypeDefinition.setPrimaryItemName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeDefinitionWithInvalidSuperTypeNameShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName("nodeTypeName"); //$NON-NLS-1$
        this.nodeTypeDefinition.addSuperType("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void nodeTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(this.nodeTypeDefinition,
                                                                  Constants.Helper.getDefaultNamespacePrefixes(),
                                                                  null);

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
    public void nullValueConstraintShouldBeAnError() {
        // setup
        final ValidationStatus status = CndValidator.validateValueConstraint(null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_VALUE_CONSTRAINT)); //$NON-NLS-1$
    }

    @Test
    public void primaryItemNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        this.nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        this.nodeTypeDefinition.setPrimaryItemName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateNodeTypeDefinition(this.nodeTypeDefinition, this.cnd, false);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithEmptyNameShouldNotBeValid() {
        // setup
        this.propertyDefinition.setName(null);
        ValidationStatus status = CndValidator.validatePropertyDefinition(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$

        // setup
        this.propertyDefinition.setName(Utils.EMPTY_STRING);
        status = CndValidator.validatePropertyDefinition(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_UNQUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithInvalidDefaultValueShouldBeAnError() {
        // setup
        this.propertyDefinition.setName("name"); //$NON-NLS-1$
        this.propertyDefinition.setType(PropertyType.LONG);
        this.propertyDefinition.addDefaultValue("notALongValue"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validatePropertyDefinition(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithInvalidNameShouldBeAnError() {
        // setup
        this.propertyDefinition.setName("invalid/name"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validatePropertyDefinition(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS)); //$NON-NLS-1$
    }

    @Test
    public void propertyDefinitionWithMultipleDefaultValuesButSingleValuedShouldBeAnError() {
        // setup
        this.propertyDefinition.setName("name"); //$NON-NLS-1$
        this.propertyDefinition.addDefaultValue("defaultValue1"); //$NON-NLS-1$
        this.propertyDefinition.addDefaultValue("defaultValue2"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validatePropertyDefinition(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.MULTIPLE_DEFAULT_VALUES_FOR_SINGLE_VALUED_PROPERTY)); //$NON-NLS-1$
    }

    @Test
    public void propertyNameWithQualifierAndNoValidPrefixesShouldBeAnError() {
        // setup
        this.propertyDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void propertyNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        this.propertyDefinition.setName(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateName(this.propertyDefinition,
                                                                  Constants.Helper.getDefaultNamespacePrefixes(),
                                                                  null);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void requiredTypeNameWithNonMatchingQualifierShouldBeAnError() {
        // setup
        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        this.childNodeDefinition.addRequiredType(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateRequiredTypes(this.childNodeDefinition,
                                                                           Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void residualChildNodeDoesNotNeedDefaultType() {
        // setup
        this.childNodeDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        this.childNodeDefinition.addRequiredType(Constants.QUALIFIED_NAME1.get());
        final ValidationStatus status = CndValidator.validateDefaultType(this.childNodeDefinition,
                                                                         Constants.Helper.getDefaultNamespacePrefixes());

        // tests
        assertFalse(status.isError());
    }

    @Test
    public void shouldAllowChildNodeDefinitionsWithResidualNames() {
        // setup
        this.childNodeDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final ValidationStatus status = CndValidator.validateName(this.childNodeDefinition, null, null);

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldAllowMultipleChildNodeDefinitionsWithResidualNames() {
        // setup
        this.nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        this.childNodeDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final ChildNodeDefinition childNode2 = new ChildNodeDefinition(this.nodeTypeDefinition);
        childNode2.setName(ItemDefinition.RESIDUAL_NAME);
        this.nodeTypeDefinition.addChildNodeDefinition(this.childNodeDefinition);
        this.nodeTypeDefinition.addChildNodeDefinition(childNode2);
        final ValidationStatus status = CndValidator.validateChildNodeDefinitions(this.nodeTypeDefinition.getName(),
                                                                                  null,
                                                                                  this.nodeTypeDefinition.getChildNodeDefinitions());

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldAllowMultiplePropertyDefinitionsWithResidualNames() {
        // setup
        this.nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        this.propertyDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final PropertyDefinition propDefn2 = new PropertyDefinition(this.nodeTypeDefinition);
        propDefn2.setName(ItemDefinition.RESIDUAL_NAME);
        this.nodeTypeDefinition.addPropertyDefinition(this.propertyDefinition);
        this.nodeTypeDefinition.addPropertyDefinition(propDefn2);
        final ValidationStatus status = CndValidator.validatePropertyDefinitions(this.nodeTypeDefinition.getName(),
                                                                                 null,
                                                                                 this.nodeTypeDefinition.getPropertyDefinitions());

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldAllowPropertyDefinitionsWithResidualNames() {
        // setup
        this.propertyDefinition.setName(ItemDefinition.RESIDUAL_NAME);
        final ValidationStatus status = CndValidator.validateName(this.propertyDefinition, null, null);

        // tests
        assertTrue(status.isOk());
    }

    @Test
    public void shouldNotAllowBuiltInPrefixWithIncorrectUri() {
        // setup
        this.namespaceMapping.setPrefix(Constants.BuiltInNamespaces.JCR.getPrefix());
        this.namespaceMapping.setUri("foo"); //$NON-NLS-1$
        final ValidationStatus status = CndValidator.validateNamespaceMapping(this.namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_URI_FOR_BUILT_IN_NAMESPACE_PREFIX)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowBuiltInUriWithIncorrectPrefix() {
        // setup
        this.namespaceMapping.setPrefix("foo"); //$NON-NLS-1$
        this.namespaceMapping.setUri(Constants.BuiltInNamespaces.JCR.getUri());
        final ValidationStatus status = CndValidator.validateNamespaceMapping(this.namespaceMapping);

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_PREFIX_FOR_BUILT_IN_NAMESPACE_URI)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowChildNodeDefinitionsWithSameName() {
        // setup
        this.nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        this.childNodeDefinition.setName("name"); //$NON-NLS-1$
        final ChildNodeDefinition childNode2 = new ChildNodeDefinition(this.nodeTypeDefinition);
        childNode2.setName(this.childNodeDefinition.getName());
        this.nodeTypeDefinition.addChildNodeDefinition(this.childNodeDefinition);
        this.nodeTypeDefinition.addChildNodeDefinition(childNode2);
        final ValidationStatus status = CndValidator.validateChildNodeDefinitions(this.nodeTypeDefinition.getName(),
                                                                                  null,
                                                                                  this.nodeTypeDefinition.getChildNodeDefinitions());

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
        final ValidationStatus status = CndValidator.validateQualifiedName(Constants.QUALIFIED_NAME1, "propertyName", //$NON-NLS-1$
                                                                           Constants.Helper.getDefaultQualifiers(),
                                                                           Constants.Helper.getDefaultQualifiedNames());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.DUPLICATE_QUALIFIED_NAME)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotAllowPropertyDefinitionsWithSameName() {
        // setup
        this.nodeTypeDefinition.setName("nodeName"); //$NON-NLS-1$
        this.propertyDefinition.setName("name"); //$NON-NLS-1$
        final PropertyDefinition propDefn2 = new PropertyDefinition(this.nodeTypeDefinition);
        propDefn2.setName(this.propertyDefinition.getName());
        this.nodeTypeDefinition.addPropertyDefinition(this.propertyDefinition);
        this.nodeTypeDefinition.addPropertyDefinition(propDefn2);
        final ValidationStatus status = CndValidator.validatePropertyDefinitions(this.nodeTypeDefinition.getName(),
                                                                                 null,
                                                                                 this.nodeTypeDefinition.getPropertyDefinitions());

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
        this.nodeTypeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        this.nodeTypeDefinition.addSuperType(Constants.NAME_WITH_NON_DEFAULT_QUALIFIER.get());
        final ValidationStatus status = CndValidator.validateSuperTypes(this.nodeTypeDefinition.getName(),
                                                                        Constants.Helper.getDefaultNamespacePrefixes(),
                                                                        this.nodeTypeDefinition.getState(NodeTypeDefinition.PropertyName.SUPERTYPES),
                                                                        this.nodeTypeDefinition.getSupertypes());

        // tests
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test( expected = IllegalArgumentException.class )
    public void isValidShouldNotAllowNullPropertyType() {
        CndValidator.isValid("value", null, "propertyName", null);
    }

    @Test
    public void isValidShouldNotAllowEmptyPropertyValue() {
        final ValidationStatus status = CndValidator.isValid(null, PropertyType.STRING, "propertyName", null);
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.EMPTY_VALUE)); //$NON-NLS-1$
    }

    @Test
    public void validBooleanValuesShouldBeValid() {
        { // true
            final ValidationStatus status = CndValidator.isValid(Boolean.TRUE.toString(),
                                                                 PropertyType.BOOLEAN,
                                                                 "propertyName",
                                                                 null);
            assertTrue(status.isOk());
        }

        { // false
            final ValidationStatus status = CndValidator.isValid(Boolean.FALSE.toString(),
                                                                 PropertyType.BOOLEAN,
                                                                 "propertyName",
                                                                 null);
            assertTrue(status.isOk());
        }
    }

    @Test
    public void invalidBooleanValuesShouldNotBeValid() {
        final ValidationStatus status = CndValidator.isValid("badValue", PropertyType.BOOLEAN, "propertyName", null);
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE)); //$NON-NLS-1$
    }

    @Test
    public void unregisteredQualifierForNamePropertyValueShouldNotBeValid() {
        final ValidationStatus status = CndValidator.isValid("nt:name",
                                                             PropertyType.NAME,
                                                             "propertyName",
                                                             Constants.Helper.getDefaultNamespacePrefixes());
        assertTrue(status.isError());
        assertTrue("Code is " + status.getCode(), status.containsCode(StatusCodes.NAME_QUALIFIER_NOT_FOUND)); //$NON-NLS-1$
    }

    @Test
    public void registeredQualifierForNamePropertyValueShouldBeValid() {
        final ValidationStatus status = CndValidator.isValid(Constants.NAMESPACE_PREFIX1 + ":name",
                                                             PropertyType.NAME,
                                                             "propertyName",
                                                             Constants.Helper.getDefaultNamespacePrefixes());
        assertTrue(status.isOk());
    }
}
