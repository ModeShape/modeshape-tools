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
import java.util.Collection;
import java.util.Collections;
import org.jboss.tools.modeshape.jcr.Listener;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition.PropertyName;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
import org.jboss.tools.modeshape.jcr.attributes.PropertyType;
import org.jboss.tools.modeshape.jcr.attributes.PropertyValue;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class PropertyDefinitionTest {

    private static final QualifiedName OWNER_NAME = Constants.QUALIFIED_NAME1;

    private NodeTypeDefinition owner;
    private PropertyDefinition propDefn;

    @Before
    public void beforeEach() {
        this.owner = new NodeTypeDefinition();
        this.owner.setName(OWNER_NAME.get());
        this.propDefn = new PropertyDefinition(this.owner);
    }

    @Test
    public void copiesShouldBeEqualAndHaveSameHashCode() {
        PropertyDefinition thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setComment("comment goes here"); //$NON-NLS-1$
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setAvailableQueryOperators(Constants.Helper.getDefaultQueryOperators());
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setAutoCreated(true);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setAutoCreated(false);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setDefaultValues(Constants.Helper.getDefaultStringValues());
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setFullTextSearchable(true);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setFullTextSearchable(false);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMandatory(true);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMandatory(false);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMultiple(true);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMultiple(false);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setName(Constants.QUALIFIED_NAME1.get());
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setOnParentVersion(OnParentVersion.COMPUTE.asJcrValue());
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setProtected(true);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setProtected(false);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setQueryOrderable(true);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setQueryOrderable(false);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setRequiredType(PropertyType.BINARY.asJcrValue());
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setValueConstraints(Constants.DEFAULT_VALUE_CONSTRAINTS);
        thatPropDefn = PropertyDefinition.copy(this.propDefn, this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());
    }

    @Test
    public void differentInstancesWithSameValuesShouldBeEqual() {
        final PropertyDefinition thatPropDefn = new PropertyDefinition(this.owner);
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setAvailableQueryOperators(Constants.Helper.getDefaultQueryOperators());
        thatPropDefn.setAvailableQueryOperators(this.propDefn.getAvailableQueryOperators());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setAutoCreated(true);
        thatPropDefn.setAutoCreated(this.propDefn.isAutoCreated());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setAutoCreated(false);
        thatPropDefn.setAutoCreated(this.propDefn.isAutoCreated());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setComment("comment"); //$NON-NLS-1$
        thatPropDefn.setComment(this.propDefn.getComment());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setDefaultValues(Constants.Helper.getDefaultStringValues());
        thatPropDefn.setDefaultValues(this.propDefn.getDefaultValues());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setFullTextSearchable(true);
        thatPropDefn.setFullTextSearchable(this.propDefn.isFullTextSearchable());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setFullTextSearchable(false);
        thatPropDefn.setFullTextSearchable(this.propDefn.isFullTextSearchable());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMandatory(true);
        thatPropDefn.setMandatory(this.propDefn.isMandatory());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMandatory(false);
        thatPropDefn.setMandatory(this.propDefn.isMandatory());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMultiple(true);
        thatPropDefn.setMultiple(this.propDefn.isMultiple());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setMultiple(false);
        thatPropDefn.setMultiple(this.propDefn.isMultiple());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setName(Constants.QUALIFIED_NAME1.get());
        thatPropDefn.setName(this.propDefn.getName());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setOnParentVersion(OnParentVersion.COMPUTE.asJcrValue());
        thatPropDefn.setOnParentVersion(this.propDefn.getOnParentVersion());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setProtected(true);
        thatPropDefn.setProtected(this.propDefn.isProtected());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setProtected(false);
        thatPropDefn.setProtected(this.propDefn.isProtected());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setQueryOrderable(true);
        thatPropDefn.setQueryOrderable(this.propDefn.isQueryOrderable());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setQueryOrderable(false);
        thatPropDefn.setQueryOrderable(this.propDefn.isQueryOrderable());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setRequiredType(PropertyType.BINARY.asJcrValue());
        thatPropDefn.setRequiredType(this.propDefn.getRequiredType());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());

        this.propDefn.setValueConstraints(Constants.DEFAULT_VALUE_CONSTRAINTS);
        thatPropDefn.setValueConstraints(this.propDefn.getValueConstraints());
        assertEquals(this.propDefn, thatPropDefn);
        assertEquals(this.propDefn.hashCode(), thatPropDefn.hashCode());
    }

    @Test
    public void onParentVersionDefaultValueShouldBeCopy() {
        assertEquals(OnParentVersion.COPY, OnParentVersion.DEFAULT_VALUE);
    }

    @Test
    public void onParentVersionShouldBeSetToDefaultAfterConstruction() {
        assertEquals(OnParentVersion.DEFAULT_VALUE.asJcrValue(), this.propDefn.getOnParentVersion());
    }

    @Test
    public void propertyTypeDefaultValueShouldBeString() {
        assertEquals(PropertyType.STRING, PropertyType.DEFAULT_VALUE);
    }

    @Test
    public void propertyTypeShouldBeSetToDefaultAfterConstruction() {
        assertEquals(PropertyType.DEFAULT_VALUE, this.propDefn.getType());
        assertEquals(PropertyType.DEFAULT_VALUE.asJcrValue(), this.propDefn.getRequiredType());
    }

    @Test
    public void shouldAddDefaultValue() {
        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));
        assertEquals(1, this.propDefn.getDefaultValues().length);
        assertEquals(VALUE, this.propDefn.getDefaultValuesAsStrings().iterator().next());
    }

    @Test
    public void shouldAddValueConstraint() {
        final String CONSTRAINT = Constants.VALUE_CONSTRAINT1;
        assertTrue(this.propDefn.addValueConstraint(CONSTRAINT));
        assertEquals(1, this.propDefn.getValueConstraints().length);
        assertEquals(CONSTRAINT, this.propDefn.getValueConstraints()[0]);
    }

    @Test
    public void shouldAllowNullEmptyComment() {
        this.propDefn.setComment(null);
        this.propDefn.setComment(Utils.EMPTY_STRING);

    }

    @Test
    public void shouldAllowNullEmptyName() {
        this.propDefn.setName(null);
        this.propDefn.setName(Utils.EMPTY_STRING);

    }

    @Test
    public void shouldChangeAutocreatedPropertyState() {
        assertTrue(this.propDefn.changeState(PropertyName.AUTOCREATED, Value.IS));
        assertEquals(this.propDefn.getState(PropertyName.AUTOCREATED), Value.IS);
        assertTrue(this.propDefn.isAutoCreated());

        assertTrue(this.propDefn.changeState(PropertyName.AUTOCREATED, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.AUTOCREATED), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.AUTOCREATED));

        assertTrue(this.propDefn.changeState(PropertyName.AUTOCREATED, Value.IS_NOT));
        assertEquals(this.propDefn.getState(PropertyName.AUTOCREATED), Value.IS_NOT);
        assertFalse(this.propDefn.isAutoCreated());
    }

    @Test
    public void shouldChangeDefaultValuesPropertyToVariantStateOnly() {
        assertFalse(this.propDefn.changeState(PropertyName.DEFAULT_VALUES, Value.IS));

        assertTrue(this.propDefn.changeState(PropertyName.DEFAULT_VALUES, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.DEFAULT_VALUES), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.DEFAULT_VALUES));

        assertFalse(this.propDefn.changeState(PropertyName.DEFAULT_VALUES, Value.IS_NOT));
    }

    @Test
    public void shouldChangeMandatoryPropertyState() {
        assertTrue(this.propDefn.changeState(PropertyName.MANDATORY, Value.IS));
        assertEquals(this.propDefn.getState(PropertyName.MANDATORY), Value.IS);
        assertTrue(this.propDefn.isMandatory());

        assertTrue(this.propDefn.changeState(PropertyName.MANDATORY, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.MANDATORY), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.MANDATORY));

        assertTrue(this.propDefn.changeState(PropertyName.MANDATORY, Value.IS_NOT));
        assertEquals(this.propDefn.getState(PropertyName.MANDATORY), Value.IS_NOT);
        assertFalse(this.propDefn.isMandatory());
    }

    @Test
    public void shouldChangeMultiplePropertyState() {
        assertTrue(this.propDefn.changeState(PropertyName.MULTIPLE, Value.IS));
        assertEquals(this.propDefn.getState(PropertyName.MULTIPLE), Value.IS);
        assertTrue(this.propDefn.isMultiple());

        assertTrue(this.propDefn.changeState(PropertyName.MULTIPLE, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.MULTIPLE), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.MULTIPLE));

        assertTrue(this.propDefn.changeState(PropertyName.MULTIPLE, Value.IS_NOT));
        assertEquals(this.propDefn.getState(PropertyName.MULTIPLE), Value.IS_NOT);
        assertFalse(this.propDefn.isMultiple());
    }

    @Test
    public void shouldChangeNoFullTextPropertyState() {
        assertTrue(this.propDefn.changeState(PropertyName.NO_FULL_TEXT, Value.IS));
        assertEquals(this.propDefn.getState(PropertyName.NO_FULL_TEXT), Value.IS);
        assertFalse(this.propDefn.isFullTextSearchable());

        assertTrue(this.propDefn.changeState(PropertyName.NO_FULL_TEXT, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.NO_FULL_TEXT), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.NO_FULL_TEXT));

        assertTrue(this.propDefn.changeState(PropertyName.NO_FULL_TEXT, Value.IS_NOT));
        assertEquals(this.propDefn.getState(PropertyName.NO_FULL_TEXT), Value.IS_NOT);
        assertTrue(this.propDefn.isFullTextSearchable());
    }

    @Test
    public void shouldChangeNoQueryOrderPropertyState() {
        assertTrue(this.propDefn.changeState(PropertyName.NO_QUERY_ORDER, Value.IS));
        assertEquals(this.propDefn.getState(PropertyName.NO_QUERY_ORDER), Value.IS);
        assertFalse(this.propDefn.isQueryOrderable());

        assertTrue(this.propDefn.changeState(PropertyName.NO_QUERY_ORDER, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.NO_QUERY_ORDER), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.NO_QUERY_ORDER));

        assertTrue(this.propDefn.changeState(PropertyName.NO_QUERY_ORDER, Value.IS_NOT));
        assertEquals(this.propDefn.getState(PropertyName.NO_QUERY_ORDER), Value.IS_NOT);
        assertTrue(this.propDefn.isQueryOrderable());
    }

    @Test
    public void shouldChangeOnParentVersionPropertyToVariantStateOnly() {
        assertFalse(this.propDefn.changeState(PropertyName.ON_PARENT_VERSION, Value.IS));

        assertTrue(this.propDefn.changeState(PropertyName.ON_PARENT_VERSION, Value.VARIANT));
        assertTrue(this.propDefn.isVariant(PropertyName.ON_PARENT_VERSION));
        assertTrue(this.propDefn.isVariant(PropertyName.ON_PARENT_VERSION));

        assertFalse(this.propDefn.changeState(PropertyName.ON_PARENT_VERSION, Value.IS_NOT));
    }

    @Test
    public void shouldChangeProtectedPropertyState() {
        assertTrue(this.propDefn.changeState(PropertyName.PROTECTED, Value.IS));
        assertEquals(this.propDefn.getState(PropertyName.PROTECTED), Value.IS);
        assertTrue(this.propDefn.isProtected());

        assertTrue(this.propDefn.changeState(PropertyName.PROTECTED, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.PROTECTED), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.PROTECTED));

        assertTrue(this.propDefn.changeState(PropertyName.PROTECTED, Value.IS_NOT));
        assertEquals(this.propDefn.getState(PropertyName.PROTECTED), Value.IS_NOT);
        assertFalse(this.propDefn.isProtected());
    }

    @Test
    public void shouldChangeQueryOpsPropertyToVariantStateOnly() {
        assertFalse(this.propDefn.changeState(PropertyName.QUERY_OPS, Value.IS));

        assertTrue(this.propDefn.changeState(PropertyName.QUERY_OPS, Value.VARIANT));
        assertTrue(this.propDefn.isVariant(PropertyName.QUERY_OPS));
        assertTrue(this.propDefn.isVariant(PropertyName.QUERY_OPS));

        assertFalse(this.propDefn.changeState(PropertyName.QUERY_OPS, Value.IS_NOT));
    }

    @Test
    public void shouldChangeTypePropertyToVariantStateOnly() {
        assertFalse(this.propDefn.changeState(PropertyName.TYPE, Value.IS));

        assertTrue(this.propDefn.changeState(PropertyName.TYPE, Value.VARIANT));
        assertTrue(this.propDefn.isVariant(PropertyName.TYPE));
        assertTrue(this.propDefn.isVariant(PropertyName.TYPE));

        assertFalse(this.propDefn.changeState(PropertyName.TYPE, Value.IS_NOT));
    }

    @Test
    public void shouldChangeValueConstraintsPropertyToVariantStateOnly() {
        assertFalse(this.propDefn.changeState(PropertyName.VALUE_CONSTRAINTS, Value.IS));

        assertTrue(this.propDefn.changeState(PropertyName.VALUE_CONSTRAINTS, Value.VARIANT));
        assertEquals(this.propDefn.getState(PropertyName.VALUE_CONSTRAINTS), Value.VARIANT);
        assertTrue(this.propDefn.isVariant(PropertyName.VALUE_CONSTRAINTS));

        assertFalse(this.propDefn.changeState(PropertyName.VALUE_CONSTRAINTS, Value.IS_NOT));
    }

    @Test
    public void shouldClearDefaultValues() {
        assertTrue(this.propDefn.addDefaultValue("value")); //$NON-NLS-1$
        assertTrue(this.propDefn.clearDefaultValues());
        assertEquals(0, this.propDefn.getDefaultValues().length);
    }

    @Test
    public void shouldClearValueConstraints() {
        assertTrue(this.propDefn.addValueConstraint("value")); //$NON-NLS-1$
        assertTrue(this.propDefn.clearValueConstraints());
        assertEquals(0, this.propDefn.getValueConstraints().length);
    }

    @Test
    public void shouldNotAddDuplicateQueryOperator() {
        assertFalse(this.propDefn.addQueryOperator(QueryOperator.EQUALS));
    }

    @Test
    public void shouldNotAddSameDefaultValue() {
        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));
        assertFalse(this.propDefn.addDefaultValue(VALUE));
    }

    @Test
    public void shouldNotAddSameValueConstraint() {
        final String CONSTRAINT = "constraint"; //$NON-NLS-1$
        assertTrue(this.propDefn.addValueConstraint(CONSTRAINT));
        assertFalse(this.propDefn.addValueConstraint(CONSTRAINT));
    }

    @Test
    public void shouldNotChangeCommentToSameValue() {
        this.propDefn.setComment("newComment"); //$NON-NLS-1$
        assertFalse(this.propDefn.setComment(this.propDefn.getComment()));
    }

    @Test
    public void shouldNotClearDefaultValuesWhenEmpty() {
        assertFalse(this.propDefn.clearDefaultValues());
    }

    @Test
    public void shouldNotClearValueConstraintsWhenEmpty() {
        assertFalse(this.propDefn.clearValueConstraints());
    }

    @Test
    public void shouldNotHaveDefaultValuesAfterConstruction() {
        assertEquals(0, this.propDefn.getDefaultValues().length);
    }

    @Test
    public void shouldNotHaveNameAfterConstruction() {
        assertTrue(Utils.isEmpty(this.propDefn.getName()));
    }

    @Test
    public void shouldNotHaveValueConstraintsAfterConstruction() {
        assertEquals(0, this.propDefn.getValueConstraints().length);
    }

    @Test
    public void shouldNotReceiveEventAfterUnregistering() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));
        assertTrue(this.propDefn.removeListener(l));

        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));

        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotReceiveEventWhenNameIsNotChanged() {
        final String NAME = "name"; //$NON-NLS-1$
        this.propDefn.setName(NAME);

        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        this.propDefn.setName(NAME); // same value
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotRegisterAnAlreadyRegisteredListener() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));
        assertFalse(this.propDefn.addListener(l));
    }

    @Test
    public void shouldNotRemoveDefaultValueThatDoesNotExist() {
        assertFalse(this.propDefn.removeDefaultValue("defaultValueDoesNotExist")); //$NON-NLS-1$
    }

    @Test
    public void shouldNotRemoveQueryOperatorThatDoesNotExist() {
        assertTrue(this.propDefn.removeQueryOperator(QueryOperator.GREATER_THAN_EQUALS));
        assertFalse(this.propDefn.removeQueryOperator(QueryOperator.GREATER_THAN_EQUALS));
    }

    @Test
    public void shouldNotRemoveValueConstraintThatDoesNotExist() {
        assertFalse(this.propDefn.removeValueConstraint("valueConstraintdoesNotExist")); //$NON-NLS-1$
    }

    @Test
    public void shouldNotSetInvalidQueryOperators() {
        final String GOOD_OP = QueryOperator.EQUALS.toString();
        final String BAD_OP = "badOp"; //$NON-NLS-1$
        final String[] NEW_OPERATORS = new String[] { GOOD_OP, BAD_OP };
        this.propDefn.setAvailableQueryOperators(NEW_OPERATORS);

        final String[] queryOperators = this.propDefn.getAvailableQueryOperators();
        assertEquals((NEW_OPERATORS.length - 1), queryOperators.length);
        assertEquals(NEW_OPERATORS[0], queryOperators[0]);
    }

    @Test
    public void shouldNotUnregisterAnUnregisteredListener() {
        final Listener l = new Listener();
        assertFalse(this.propDefn.removeListener(l));
    }

    @Test
    public void shouldReceiveEventAfterAddingDefaultValue() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.DEFAULT_VALUES.toString(), l.getPropertyName());
        assertEquals(VALUE, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterAddingValueConstraint() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        final String CONSTRAINT = "constraint"; //$NON-NLS-1$
        assertTrue(this.propDefn.addValueConstraint(CONSTRAINT));

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.VALUE_CONSTRAINTS.toString(), l.getPropertyName());
        assertEquals(CONSTRAINT, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterClearingDefaultValues() {
        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));
        final Collection<String> oldValue = Collections.singletonList(VALUE);

        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        assertTrue(this.propDefn.clearDefaultValues());
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.DEFAULT_VALUES.toString(), l.getPropertyName());
        assertNull(l.getNewValue());
        assertEquals(oldValue, l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterClearingValueConstraints() {
        final String CONSTRAINT = "constraint"; //$NON-NLS-1$
        assertTrue(this.propDefn.addValueConstraint(CONSTRAINT));
        final Collection<String> oldValue = Collections.singletonList(CONSTRAINT);

        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        assertTrue(this.propDefn.clearValueConstraints());
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.VALUE_CONSTRAINTS.toString(), l.getPropertyName());
        assertNull(l.getNewValue());
        assertEquals(oldValue, l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterRemovingDefaultValue() {
        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));

        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        assertTrue(this.propDefn.removeDefaultValue(VALUE));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.DEFAULT_VALUES.toString(), l.getPropertyName());
        assertEquals(VALUE, l.getOldValue());
        assertNull(l.getNewValue());
    }

    @Test
    public void shouldReceiveEventAfterRemovingValueConstraint() {
        final String CONSTRAINT = "constraint"; //$NON-NLS-1$
        assertTrue(this.propDefn.addValueConstraint(CONSTRAINT));

        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        assertTrue(this.propDefn.removeValueConstraint(CONSTRAINT));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.VALUE_CONSTRAINTS.toString(), l.getPropertyName());
        assertEquals(CONSTRAINT, l.getOldValue());
        assertNull(l.getNewValue());
    }

    @Test
    public void shouldReceiveEventWhenCommentIsChanged() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        final String NEW_COMMENT = "comment"; //$NON-NLS-1$
        this.propDefn.setComment(NEW_COMMENT);

        assertEquals(NEW_COMMENT, this.propDefn.getComment());
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.COMMENT.toString(), l.getPropertyName());
        assertEquals(NEW_COMMENT, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventWhenNameIsChanged() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        final String NEW_NAME = "name"; //$NON-NLS-1$
        this.propDefn.setName(NEW_NAME);

        assertEquals(NEW_NAME, this.propDefn.getName());
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.NAME.toString(), l.getPropertyName());
        assertEquals(NEW_NAME, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventWhenOnParentVersionIsChanged() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        final int OPV = OnParentVersion.COMPUTE.asJcrValue();
        this.propDefn.setOnParentVersion(OPV);

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.ON_PARENT_VERSION.toString(), l.getPropertyName());
        assertEquals(OPV, l.getNewValue());
        assertEquals(OnParentVersion.DEFAULT_VALUE, l.getOldValue());
    }

    @Test
    public void shouldReceiveEventWhenPropertyTypeIsChanged() {
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        final PropertyType TYPE = PropertyType.BINARY;
        this.propDefn.setRequiredType(TYPE.asJcrValue());

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.TYPE.toString(), l.getPropertyName());
        assertEquals(TYPE, l.getNewValue());
        assertEquals(PropertyType.DEFAULT_VALUE, l.getOldValue());
    }

    @Test
    public void shouldReceiveEventWhenQueryOperatorIsAdded() {
        // setup
        final QueryOperator OP = QueryOperator.GREATER_THAN_EQUALS;
        assertTrue(this.propDefn.removeQueryOperator(OP));

        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        // test
        assertTrue(this.propDefn.addQueryOperator(OP));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.QUERY_OPS.toString(), l.getPropertyName());
        assertEquals(OP, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventWhenQueryOperatorIsRemoved() {
        final QueryOperator OP = QueryOperator.GREATER_THAN_EQUALS;
        final Listener l = new Listener();
        assertTrue(this.propDefn.addListener(l));

        assertTrue(this.propDefn.removeQueryOperator(OP));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.QUERY_OPS.toString(), l.getPropertyName());
        assertNull(l.getNewValue());
        assertEquals(OP, l.getOldValue());
    }

    @Test
    public void shouldRemoveDefaultValue() {
        final String VALUE = "value"; //$NON-NLS-1$
        assertTrue(this.propDefn.addDefaultValue(VALUE));
        assertTrue(this.propDefn.removeDefaultValue(VALUE));
        assertEquals(0, this.propDefn.getDefaultValues().length);
    }

    @Test
    public void shouldRemoveQueryOperator() {
        assertTrue(this.propDefn.removeQueryOperator(QueryOperator.GREATER_THAN_EQUALS));
    }

    @Test
    public void shouldRemoveValueConstraint() {
        final String CONSTRAINT = "constraint"; //$NON-NLS-1$
        assertTrue(this.propDefn.addValueConstraint(CONSTRAINT));
        assertTrue(this.propDefn.removeValueConstraint(CONSTRAINT));
        assertEquals(0, this.propDefn.getValueConstraints().length);
    }

    @Test
    public void shouldSetAutoCreated() {
        this.propDefn.setAutoCreated(true);
        assertTrue(this.propDefn.isAutoCreated());
        assertEquals(Value.IS, this.propDefn.getState(PropertyName.AUTOCREATED));

        this.propDefn.setAutoCreated(false);
        assertFalse(this.propDefn.isAutoCreated());
        assertEquals(Value.IS_NOT, this.propDefn.getState(PropertyName.AUTOCREATED));
    }

    @Test
    public void shouldSetComment() {
        final String NEW_COMMENT = "newComment"; //$NON-NLS-1$
        assertTrue(this.propDefn.setComment(NEW_COMMENT));
        assertEquals(NEW_COMMENT, this.propDefn.getComment());
    }

    @Test
    public void shouldSetDefaultValues() {
        assertTrue(this.propDefn.addDefaultValue("defaultValueBeingOverridden")); //$NON-NLS-1$

        final PropertyValue VALUE1 = new PropertyValue(PropertyType.STRING.asJcrValue(), "value1"); //$NON-NLS-1$
        final PropertyValue VALUE2 = new PropertyValue(PropertyType.STRING.asJcrValue(), "value2"); //$NON-NLS-1$
        final PropertyValue VALUE3 = new PropertyValue(PropertyType.STRING.asJcrValue(), "value3"); //$NON-NLS-1$
        final PropertyValue[] NEW_VALUES = new PropertyValue[] { VALUE1, VALUE2, VALUE3 };
        this.propDefn.setDefaultValues(NEW_VALUES);

        final javax.jcr.Value[] defaultValues = this.propDefn.getDefaultValues();
        assertEquals(NEW_VALUES.length, defaultValues.length);

        for (int i = 0; i < NEW_VALUES.length; ++i) {
            assertEquals(NEW_VALUES[i], defaultValues[i]);
        }

        assertEquals(Value.IS, this.propDefn.getState(PropertyName.DEFAULT_VALUES));
    }

    @Test
    public void shouldSetDefaultValuesWithEmptyArray() {
        this.propDefn.setDefaultValues(new javax.jcr.Value[0]);
        assertEquals(0, this.propDefn.getDefaultValues().length);
    }

    @Test
    public void shouldSetDefaultValuesWithNull() {
        this.propDefn.setDefaultValues(null);
        assertEquals(0, this.propDefn.getDefaultValues().length);
    }

    @Test
    public void shouldSetFullTextSearchable() {
        this.propDefn.setFullTextSearchable(true);
        assertTrue(this.propDefn.isFullTextSearchable());
        assertEquals(Value.IS_NOT, this.propDefn.getState(PropertyName.NO_FULL_TEXT));

        this.propDefn.setFullTextSearchable(false);
        assertFalse(this.propDefn.isFullTextSearchable());
        assertEquals(Value.IS, this.propDefn.getState(PropertyName.NO_FULL_TEXT));
    }

    @Test
    public void shouldSetMandatory() {
        this.propDefn.setMandatory(true);
        assertTrue(this.propDefn.isMandatory());
        assertEquals(Value.IS, this.propDefn.getState(PropertyName.MANDATORY));

        this.propDefn.setMandatory(false);
        assertFalse(this.propDefn.isMandatory());
        assertEquals(Value.IS_NOT, this.propDefn.getState(PropertyName.MANDATORY));
    }

    @Test
    public void shouldSetMultiple() {
        this.propDefn.setMultiple(true);
        assertTrue(this.propDefn.isMultiple());
        assertEquals(Value.IS, this.propDefn.getState(PropertyName.MULTIPLE));

        this.propDefn.setMultiple(false);
        assertFalse(this.propDefn.isMultiple());
        assertEquals(Value.IS_NOT, this.propDefn.getState(PropertyName.MULTIPLE));
    }

    @Test
    public void shouldSetName() {
        final String NAME = "name"; //$NON-NLS-1$
        this.propDefn.setName(NAME);
        assertEquals(NAME, this.propDefn.getName());

    }

    @Test
    public void shouldSetOnParentVersionUsingInt() {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv != OnParentVersion.VARIANT) {
                this.propDefn.setOnParentVersion(opv.asJcrValue());
                assertEquals(opv.asJcrValue(), this.propDefn.getOnParentVersion());
            }
        }
    }

    @Test
    public void shouldSetOnParentVersionUsingString() {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv != OnParentVersion.VARIANT) {
                assertTrue(this.propDefn.setOnParentVersion(opv.toString()));
                assertEquals(opv.asJcrValue(), this.propDefn.getOnParentVersion());
            }
        }
    }

    @Test
    public void shouldSetPropertyType() {
        for (final PropertyType type : PropertyType.values()) {
            if (type != PropertyType.VARIANT) {
                assertTrue(this.propDefn.setType(type));
                assertEquals(type.asJcrValue(), this.propDefn.getRequiredType());
            }
        }
    }

    @Test
    public void shouldSetPropertyTypeUsingInt() {
        for (final PropertyType type : PropertyType.values()) {
            if (type != PropertyType.VARIANT) {
                this.propDefn.setRequiredType(type.asJcrValue());
                assertEquals(type.asJcrValue(), this.propDefn.getRequiredType());
            }
        }
    }

    @Test
    public void shouldSetProtected() {
        this.propDefn.setProtected(true);
        assertTrue(this.propDefn.isProtected());
        assertEquals(Value.IS, this.propDefn.getState(PropertyName.PROTECTED));

        this.propDefn.setProtected(false);
        assertFalse(this.propDefn.isProtected());
        assertEquals(Value.IS_NOT, this.propDefn.getState(PropertyName.PROTECTED));
    }

    @Test
    public void shouldSetQueryOperators() {
        final String OP1 = QueryOperator.EQUALS.toString();
        final String OP2 = QueryOperator.LESS_THAN.toString();
        final String OP3 = QueryOperator.GREATER_THAN.toString();
        final String[] NEW_OPERATORS = new String[] { OP1, OP2, OP3 };
        this.propDefn.setAvailableQueryOperators(NEW_OPERATORS);

        final String[] queryOperators = this.propDefn.getAvailableQueryOperators();
        assertEquals(NEW_OPERATORS.length, queryOperators.length);

        for (int i = 0; i < NEW_OPERATORS.length; ++i) {
            assertEquals(NEW_OPERATORS[i], queryOperators[i]);
        }
    }

    @Test
    public void shouldSetQueryOperatorsWithEmptyArray() {
        this.propDefn.setAvailableQueryOperators(new String[0]);
        assertEquals(0, this.propDefn.getAvailableQueryOperators().length);
    }

    @Test
    public void shouldSetQueryOperatorsWithNull() {
        this.propDefn.setAvailableQueryOperators(null);
        assertEquals(0, this.propDefn.getAvailableQueryOperators().length);
    }

    @Test
    public void shouldSetQueryOrderable() {
        this.propDefn.setQueryOrderable(true);
        assertTrue(this.propDefn.isQueryOrderable());
        assertEquals(Value.IS_NOT, this.propDefn.getState(PropertyName.NO_QUERY_ORDER));

        this.propDefn.setQueryOrderable(false);
        assertFalse(this.propDefn.isQueryOrderable());
        assertEquals(Value.IS, this.propDefn.getState(PropertyName.NO_QUERY_ORDER));
    }

    @Test
    public void shouldSetValueContraints() {
        assertTrue(this.propDefn.addValueConstraint("constraintBeingOverridden")); //$NON-NLS-1$

        final String CONSTRAINT1 = "constraint1"; //$NON-NLS-1$
        final String CONSTRAINT2 = "constraint2"; //$NON-NLS-1$
        final String CONSTRAINT3 = "constraint3"; //$NON-NLS-1$
        final String[] NEW_CONSTRAINTS = new String[] { CONSTRAINT1, CONSTRAINT2, CONSTRAINT3 };
        this.propDefn.setValueConstraints(NEW_CONSTRAINTS);

        final String[] constraints = this.propDefn.getValueConstraints();
        assertEquals(NEW_CONSTRAINTS.length, constraints.length);

        for (int i = 0; i < NEW_CONSTRAINTS.length; ++i) {
            assertEquals(NEW_CONSTRAINTS[i], constraints[i]);
        }
    }

    @Test
    public void shouldSetValueContraintsWithEmptyArray() {
        this.propDefn.setValueConstraints(new String[0]);
        assertEquals(0, this.propDefn.getValueConstraints().length);
    }

    @Test
    public void shouldSetValueContraintsWithNull() {
        this.propDefn.setValueConstraints(null);
        assertEquals(0, this.propDefn.getValueConstraints().length);
    }

    @Test
    public void shouldSupportAllQueryOperatorsInitially() {
        // setup
        final String[] queryOps = this.propDefn.getAvailableQueryOperators();
        final QueryOperator[] allOperators = QueryOperator.values();

        assertEquals(allOperators.length, queryOps.length);

        for (final String queryOp : queryOps) {
            QueryOperator.find(queryOp); // throws exception if not found
        }
    }
}
