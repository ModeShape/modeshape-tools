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

import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition.PropertyName;
import org.jboss.tools.modeshape.jcr.Listener;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class ChildNodeDefinitionTest {

    private static final QualifiedName OWNER_NAME = Constants.QUALIFIED_NAME1;

    private NodeTypeDefinition owner;
    private ChildNodeDefinition childNodeDefinition;

    @Before
    public void beforeEach() {
        this.owner = new NodeTypeDefinition();
        this.owner.setName(OWNER_NAME.get());
        this.childNodeDefinition = new ChildNodeDefinition(this.owner);
    }

    @Test
    public void copiesShouldBeEqualAndHaveSameHashCode() {
        ChildNodeDefinition thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setComment("comment goes here"); //$NON-NLS-1$
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setDefaultPrimaryTypeName(Constants.DEFAULT_TYPE);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setAutoCreated(!this.childNodeDefinition.isAutoCreated());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setMandatory(!this.childNodeDefinition.isMandatory());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setProtected(!this.childNodeDefinition.isProtected());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setSameNameSiblings(!this.childNodeDefinition.allowsSameNameSiblings());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setOnParentVersion(OnParentVersion.ABORT.asJcrValue());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());

        this.childNodeDefinition.setRequiredPrimaryTypeNames(Constants.Helper.getDefaultQualifiedNamesAsStringArray());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertEquals(this.childNodeDefinition, thatChildNodeDefinition);
        assertEquals(this.childNodeDefinition.hashCode(), thatChildNodeDefinition.hashCode());
    }

    @Test
    public void differentInstancesWithSameValuesShouldBeEqual() {
        final ChildNodeDefinition that = new ChildNodeDefinition(this.owner);
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        that.setName(this.childNodeDefinition.getName());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setComment("comment goes here"); //$NON-NLS-1$
        that.setComment(this.childNodeDefinition.getComment());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setDefaultPrimaryTypeName(Constants.DEFAULT_TYPE);
        that.setDefaultPrimaryTypeName(this.childNodeDefinition.getDefaultPrimaryTypeName());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setAutoCreated(!this.childNodeDefinition.isAutoCreated());
        that.setAutoCreated(this.childNodeDefinition.isAutoCreated());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setMandatory(!this.childNodeDefinition.isMandatory());
        that.setMandatory(this.childNodeDefinition.isMandatory());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setProtected(!this.childNodeDefinition.isProtected());
        that.setProtected(this.childNodeDefinition.isProtected());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setSameNameSiblings(!this.childNodeDefinition.allowsSameNameSiblings());
        that.setSameNameSiblings(this.childNodeDefinition.allowsSameNameSiblings());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setOnParentVersion(OnParentVersion.ABORT.asJcrValue());
        that.setOnParentVersion(this.childNodeDefinition.getOnParentVersion());
        assertEquals(this.childNodeDefinition, that);

        this.childNodeDefinition.setRequiredPrimaryTypeNames(Constants.Helper.getDefaultQualifiedNamesAsStringArray());
        that.setRequiredPrimaryTypeNames(this.childNodeDefinition.getRequiredPrimaryTypeNames());
        assertEquals(this.childNodeDefinition, that);
    }

    @Test
    public void differentInstancesWithSameValuesShouldHaveSameHashCode() {
        assertEquals(this.childNodeDefinition.hashCode(), new ChildNodeDefinition(this.owner).hashCode());
    }

    @Test
    public void onParentVersionDefaultValueShouldBeCopy() {
        assertEquals(OnParentVersion.COPY, OnParentVersion.DEFAULT_VALUE);
    }

    @Test
    public void onParentVersionShouldBeSetToDefaultAfterConstruction() {
        assertTrue(this.childNodeDefinition.getOnParentVersion() == OnParentVersion.DEFAULT_VALUE.asJcrValue());
    }

    @Test
    public void shouldAddRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));
        assertEquals(REQUIRED_TYPE, this.childNodeDefinition.getRequiredPrimaryTypeNames()[0]);
    }

    @Test
    public void shouldAllowNullEmptyComment() {
        this.childNodeDefinition.setComment(null);
        this.childNodeDefinition.setComment(Utils.EMPTY_STRING);

    }

    @Test
    public void shouldChangeAutocreatedPropertyState() {
        assertTrue(this.childNodeDefinition.changeState(PropertyName.AUTOCREATED, Value.IS));
        assertEquals(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), Value.IS);
        assertTrue(this.childNodeDefinition.isAutoCreated());

        assertTrue(this.childNodeDefinition.changeState(PropertyName.AUTOCREATED, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.AUTOCREATED));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.AUTOCREATED, Value.IS_NOT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), Value.IS_NOT);
        assertFalse(this.childNodeDefinition.isAutoCreated());
    }

    @Test
    public void shouldChangeDefaultTypePropertyStateToVariantOnly() {
        assertFalse(this.childNodeDefinition.changeState(PropertyName.DEFAULT_TYPE, Value.IS));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.DEFAULT_TYPE, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.DEFAULT_TYPE), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.DEFAULT_TYPE));

        assertFalse(this.childNodeDefinition.changeState(PropertyName.DEFAULT_TYPE, Value.IS_NOT));
    }

    @Test
    public void shouldChangeMandatoryPropertyState() {
        assertTrue(this.childNodeDefinition.changeState(PropertyName.MANDATORY, Value.IS));
        assertEquals(this.childNodeDefinition.getState(PropertyName.MANDATORY), Value.IS);
        assertTrue(this.childNodeDefinition.isMandatory());

        assertTrue(this.childNodeDefinition.changeState(PropertyName.MANDATORY, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.MANDATORY), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.MANDATORY));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.MANDATORY, Value.IS_NOT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.MANDATORY), Value.IS_NOT);
        assertFalse(this.childNodeDefinition.isMandatory());
    }

    @Test
    public void shouldChangeOnParentVersionPropertyToVariantStateOnly() {
        assertFalse(this.childNodeDefinition.changeState(PropertyName.ON_PARENT_VERSION, Value.IS));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.ON_PARENT_VERSION, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.ON_PARENT_VERSION), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.ON_PARENT_VERSION));

        assertFalse(this.childNodeDefinition.changeState(PropertyName.ON_PARENT_VERSION, Value.IS_NOT));
    }

    @Test
    public void shouldChangeProtectedPropertyState() {
        assertTrue(this.childNodeDefinition.changeState(PropertyName.PROTECTED, Value.IS));
        assertEquals(this.childNodeDefinition.getState(PropertyName.PROTECTED), Value.IS);
        assertTrue(this.childNodeDefinition.isProtected());

        assertTrue(this.childNodeDefinition.changeState(PropertyName.PROTECTED, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.PROTECTED), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.PROTECTED));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.PROTECTED, Value.IS_NOT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.PROTECTED), Value.IS_NOT);
        assertFalse(this.childNodeDefinition.isProtected());
    }

    @Test
    public void shouldChangeRequiredTypesPropertyToVariantStateOnly() {
        assertFalse(this.childNodeDefinition.changeState(PropertyName.REQUIRED_TYPES, Value.IS));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.REQUIRED_TYPES, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.REQUIRED_TYPES), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.REQUIRED_TYPES));

        assertFalse(this.childNodeDefinition.changeState(PropertyName.REQUIRED_TYPES, Value.IS_NOT));
    }

    @Test
    public void shouldChangeSameNameSiblingsPropertyState() {
        assertTrue(this.childNodeDefinition.changeState(PropertyName.SAME_NAME_SIBLINGS, Value.IS));
        assertEquals(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), Value.IS);
        assertTrue(this.childNodeDefinition.allowsSameNameSiblings());

        assertTrue(this.childNodeDefinition.changeState(PropertyName.SAME_NAME_SIBLINGS, Value.VARIANT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), Value.VARIANT);
        assertTrue(this.childNodeDefinition.isVariant(PropertyName.SAME_NAME_SIBLINGS));

        assertTrue(this.childNodeDefinition.changeState(PropertyName.SAME_NAME_SIBLINGS, Value.IS_NOT));
        assertEquals(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), Value.IS_NOT);
        assertFalse(this.childNodeDefinition.allowsSameNameSiblings());
    }

    @Test
    public void shouldClearRequiredTypes() {
        assertTrue(this.childNodeDefinition.addRequiredType("requiredType")); //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.clearRequiredTypes());
        assertEquals(0, this.childNodeDefinition.getRequiredPrimaryTypeNames().length);
    }

    @Test
    public void shouldNotAllowDuplicateRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));
        assertFalse(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));
    }

    @Test
    public void shouldNotChangeCommentToSameValue() {
        this.childNodeDefinition.setComment("newComment"); //$NON-NLS-1$
        assertFalse(this.childNodeDefinition.setComment(this.childNodeDefinition.getComment()));
    }

    @Test
    public void shouldNotClearSuperTypesWhenEmpty() {
        assertFalse(this.childNodeDefinition.clearRequiredTypes());
    }

    @Test
    public void shouldNotDefaultTypeAfterConstruction() {
        assertNull(this.childNodeDefinition.getDefaultPrimaryTypeName());
    }

    @Test
    public void shouldNotHaveDefaultRequiredTypeAfterConstruction() {
        assertNull(this.childNodeDefinition.getDefaultPrimaryTypeName());
    }

    @Test
    public void shouldNotHaveRequiredTypesAfterConstruction() {
        assertEquals(0, this.childNodeDefinition.getRequiredPrimaryTypeNames().length);
    }

    @Test
    public void shouldNotRemoveRequiredTypeThatDoesNotExist() {
        assertFalse(this.childNodeDefinition.removeRequiredType("requiredType")); //$NON-NLS-1$
    }

    @Test
    public void shouldReceiveEventAfterAddingRequiredType() {
        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.REQUIRED_TYPES.toString(), l.getPropertyName());
        assertEquals(REQUIRED_TYPE, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterChangingName() {
        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        final String NEW_NAME = "newName"; //$NON-NLS-1$
        this.childNodeDefinition.setName(NEW_NAME);

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.NAME.toString(), l.getPropertyName());
        assertEquals(NEW_NAME, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterChangingPrimaryTypeName() {
        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        final String PRIMARY_TYPE = "primaryType"; //$NON-NLS-1$
        this.childNodeDefinition.setDefaultPrimaryTypeName(PRIMARY_TYPE);

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.DEFAULT_TYPE.toString(), l.getPropertyName());
        assertEquals(PRIMARY_TYPE, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventAfterClearingSuperTypes() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));
        final String[] oldValue = new String[] { REQUIRED_TYPE };

        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        assertTrue(this.childNodeDefinition.clearRequiredTypes());
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.REQUIRED_TYPES.toString(), l.getPropertyName());
        assertNull(l.getNewValue());
        assertEquals(oldValue[0], ((String[])l.getOldValue())[0]);
    }

    @Test
    public void shouldReceiveEventAfterRemovingRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));

        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        assertTrue(this.childNodeDefinition.removeRequiredType(REQUIRED_TYPE));
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.REQUIRED_TYPES.toString(), l.getPropertyName());
        assertEquals(REQUIRED_TYPE, l.getOldValue());
        assertNull(l.getNewValue());
    }

    @Test
    public void shouldReceiveEventWhenCommentIsChanged() {
        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        final String NEW_COMMENT = "comment"; //$NON-NLS-1$
        this.childNodeDefinition.setComment(NEW_COMMENT);

        assertEquals(NEW_COMMENT, this.childNodeDefinition.getComment());
        assertEquals(1, l.getCount());
        assertEquals(PropertyName.COMMENT.toString(), l.getPropertyName());
        assertEquals(NEW_COMMENT, l.getNewValue());
        assertNull(l.getOldValue());
    }

    @Test
    public void shouldReceiveEventWhenOnParentVersionIsChanged() {
        final Listener l = new Listener();
        assertTrue(this.childNodeDefinition.addListener(l));

        final int OPV = OnParentVersion.COMPUTE.asJcrValue();
        this.childNodeDefinition.setOnParentVersion(OPV);

        assertEquals(1, l.getCount());
        assertEquals(PropertyName.ON_PARENT_VERSION.toString(), l.getPropertyName());
        assertEquals(OPV, l.getNewValue());
        assertEquals(OnParentVersion.DEFAULT_VALUE, l.getOldValue());
    }

    @Test
    public void shouldRemoveRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE));
        assertTrue(this.childNodeDefinition.removeRequiredType(REQUIRED_TYPE));
        assertEquals(0, this.childNodeDefinition.getRequiredPrimaryTypeNames().length);
    }

    @Test
    public void shouldSetAutoCreated() {
        this.childNodeDefinition.setAutoCreated(true);
        assertTrue(this.childNodeDefinition.isAutoCreated());
        assertTrue(this.childNodeDefinition.getState(PropertyName.AUTOCREATED) == Value.IS);

        this.childNodeDefinition.setAutoCreated(false);
        assertFalse(this.childNodeDefinition.isAutoCreated());
        assertTrue(this.childNodeDefinition.getState(PropertyName.AUTOCREATED) == Value.IS_NOT);
    }

    @Test
    public void shouldSetComment() {
        final String NEW_COMMENT = "newComment"; //$NON-NLS-1$
        assertTrue(this.childNodeDefinition.setComment(NEW_COMMENT));
        assertEquals(NEW_COMMENT, this.childNodeDefinition.getComment());
    }

    @Test
    public void shouldSetMandatory() {
        this.childNodeDefinition.setMandatory(true);
        assertTrue(this.childNodeDefinition.isMandatory());
        assertTrue(this.childNodeDefinition.getState(PropertyName.MANDATORY) == Value.IS);

        this.childNodeDefinition.setMandatory(false);
        assertFalse(this.childNodeDefinition.isMandatory());
        assertTrue(this.childNodeDefinition.getState(PropertyName.MANDATORY) == Value.IS_NOT);
    }

    @Test
    public void shouldSetName() {
        final String NEW_NAME = "newName"; //$NON-NLS-1$
        this.childNodeDefinition.setName(NEW_NAME);
        assertEquals(NEW_NAME, this.childNodeDefinition.getName());
    }

    @Test
    public void shouldSetNameWithEmptyString() {
        this.childNodeDefinition.setName(Utils.EMPTY_STRING);
        assertTrue(Utils.isEmpty(this.childNodeDefinition.getName()));
    }

    @Test
    public void shouldSetNameWithNull() {
        this.childNodeDefinition.setName(null);
        assertTrue(Utils.isEmpty(this.childNodeDefinition.getName()));
    }

    @Test
    public void shouldSetOnParentVersionUsingInt() {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv != OnParentVersion.VARIANT) {
                this.childNodeDefinition.setOnParentVersion(opv.asJcrValue());
                assertEquals(opv.asJcrValue(), this.childNodeDefinition.getOnParentVersion());
            }
        }
    }

    @Test
    public void shouldSetOnParentVersionUsingString() {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv != OnParentVersion.VARIANT) {
                assertTrue(this.childNodeDefinition.setOnParentVersion(opv.toString()));
                assertEquals(opv.asJcrValue(), this.childNodeDefinition.getOnParentVersion());
            }
        }
    }

    @Test
    public void shouldSetPrimaryTypeName() {
        final String PRIMARY_TYPE = "primaryType"; //$NON-NLS-1$
        this.childNodeDefinition.setDefaultPrimaryTypeName(PRIMARY_TYPE);
        assertEquals(PRIMARY_TYPE, this.childNodeDefinition.getDefaultPrimaryTypeName());
    }

    @Test
    public void shouldSetPrimaryTypeNameUsingEmptyValue() {
        this.childNodeDefinition.setDefaultPrimaryTypeName(Utils.EMPTY_STRING);
        assertTrue(Utils.isEmpty(this.childNodeDefinition.getDefaultPrimaryTypeName()));
    }

    @Test
    public void shouldSetPrimaryTypeNameUsingNull() {
        this.childNodeDefinition.setDefaultPrimaryTypeName(null);
        assertTrue(Utils.isEmpty(this.childNodeDefinition.getDefaultPrimaryTypeName()));
    }

    @Test
    public void shouldSetProtected() {
        this.childNodeDefinition.setProtected(true);
        assertTrue(this.childNodeDefinition.isProtected());
        assertTrue(this.childNodeDefinition.getState(PropertyName.PROTECTED) == Value.IS);

        this.childNodeDefinition.setProtected(false);
        assertFalse(this.childNodeDefinition.isProtected());
        assertTrue(this.childNodeDefinition.getState(PropertyName.PROTECTED) == Value.IS_NOT);
    }

    @Test
    public void shouldSetRequiredTypes() {
        final String REQUIRED_TYPE1 = "requiredType1"; //$NON-NLS-1$
        final String REQUIRED_TYPE2 = "requiredType2"; //$NON-NLS-1$
        final String REQUIRED_TYPE3 = "requiredType3"; //$NON-NLS-1$
        final String[] SUPER_TYPES = new String[] { REQUIRED_TYPE1, REQUIRED_TYPE2, REQUIRED_TYPE3 };
        this.childNodeDefinition.setRequiredPrimaryTypeNames(SUPER_TYPES);

        final String[] requiredTypeNames = this.childNodeDefinition.getRequiredPrimaryTypeNames();
        assertEquals(REQUIRED_TYPE1, requiredTypeNames[0]);
        assertEquals(REQUIRED_TYPE2, requiredTypeNames[1]);
        assertEquals(REQUIRED_TYPE3, requiredTypeNames[2]);
    }

    @Test
    public void shouldSetRequiredTypesWithEmptyArray() {
        assertTrue(this.childNodeDefinition.addRequiredType("requiredType")); //$NON-NLS-1$
        this.childNodeDefinition.setRequiredPrimaryTypeNames(new String[0]);
        assertEquals(0, this.childNodeDefinition.getRequiredPrimaryTypeNames().length);
    }

    @Test
    public void shouldSetRequiredTypesWithNull() {
        assertTrue(this.childNodeDefinition.addRequiredType("requiredType")); //$NON-NLS-1$
        this.childNodeDefinition.setRequiredPrimaryTypeNames(null);
        assertEquals(0, this.childNodeDefinition.getRequiredPrimaryTypeNames().length);
    }

    @Test
    public void shouldSetSameNameSiblings() {
        this.childNodeDefinition.setSameNameSiblings(true);
        assertTrue(this.childNodeDefinition.allowsSameNameSiblings());
        assertTrue(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS) == Value.IS);

        this.childNodeDefinition.setSameNameSiblings(false);
        assertFalse(this.childNodeDefinition.allowsSameNameSiblings());
        assertTrue(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS) == Value.IS_NOT);
    }
}
