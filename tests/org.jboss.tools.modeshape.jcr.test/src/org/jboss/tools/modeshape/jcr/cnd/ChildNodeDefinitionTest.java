/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
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
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setComment("comment goes here"); //$NON-NLS-1$
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setDefaultPrimaryTypeName(Constants.DEFAULT_TYPE);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setAutoCreated(true);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setAutoCreated(false);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setMandatory(true);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setMandatory(false);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setProtected(true);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setProtected(false);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setSameNameSiblings(true);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setSameNameSiblings(false);
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setOnParentVersion(OnParentVersion.ABORT.asJcrValue());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));

        this.childNodeDefinition.setRequiredPrimaryTypeNames(Constants.Helper.getDefaultQualifiedNamesAsStringArray());
        thatChildNodeDefinition = ChildNodeDefinition.copy(this.childNodeDefinition, this.owner);
        assertThat(this.childNodeDefinition, is(thatChildNodeDefinition));
        assertThat(this.childNodeDefinition.hashCode(), is(thatChildNodeDefinition.hashCode()));
    }

    @Test
    public void differentInstancesWithSameValuesShouldBeEqual() {
        final ChildNodeDefinition that = new ChildNodeDefinition(this.owner);
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setName(Constants.QUALIFIED_NAME1.get());
        that.setName(this.childNodeDefinition.getName());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setComment("comment goes here"); //$NON-NLS-1$
        that.setComment(this.childNodeDefinition.getComment());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setDefaultPrimaryTypeName(Constants.DEFAULT_TYPE);
        that.setDefaultPrimaryTypeName(this.childNodeDefinition.getDefaultPrimaryTypeName());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setAutoCreated(true);
        that.setAutoCreated(this.childNodeDefinition.isAutoCreated());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setAutoCreated(false);
        that.setAutoCreated(this.childNodeDefinition.isAutoCreated());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setMandatory(true);
        that.setMandatory(this.childNodeDefinition.isMandatory());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setMandatory(false);
        that.setMandatory(this.childNodeDefinition.isMandatory());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setProtected(true);
        that.setProtected(this.childNodeDefinition.isProtected());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setProtected(false);
        that.setProtected(this.childNodeDefinition.isProtected());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setSameNameSiblings(true);
        that.setSameNameSiblings(this.childNodeDefinition.allowsSameNameSiblings());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setSameNameSiblings(false);
        that.setSameNameSiblings(this.childNodeDefinition.allowsSameNameSiblings());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setOnParentVersion(OnParentVersion.ABORT.asJcrValue());
        that.setOnParentVersion(this.childNodeDefinition.getOnParentVersion());
        assertThat(this.childNodeDefinition, is(that));

        this.childNodeDefinition.setRequiredPrimaryTypeNames(Constants.Helper.getDefaultQualifiedNamesAsStringArray());
        that.setRequiredPrimaryTypeNames(this.childNodeDefinition.getRequiredPrimaryTypeNames());
        assertThat(this.childNodeDefinition, is(that));
    }

    @Test
    public void differentInstancesWithSameValuesShouldHaveSameHashCode() {
        assertThat(this.childNodeDefinition.hashCode(), is(new ChildNodeDefinition(this.owner).hashCode()));
    }

    @Test
    public void onParentVersionDefaultValueShouldBeCopy() {
        assertThat(OnParentVersion.COPY, is(OnParentVersion.DEFAULT_VALUE));
    }

    @Test
    public void onParentVersionShouldBeSetToDefaultAfterConstruction() {
        assertThat(OnParentVersion.DEFAULT_VALUE.asJcrValue(), is(this.childNodeDefinition.getOnParentVersion()));
    }

    @Test
    public void shouldAddRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(true));
        assertThat(REQUIRED_TYPE, is(this.childNodeDefinition.getRequiredPrimaryTypeNames()[0]));
    }

    @Test
    public void shouldAllowNullEmptyComment() {
        this.childNodeDefinition.setComment(null);
        this.childNodeDefinition.setComment(Utils.EMPTY_STRING);

    }

    @Test
    public void shouldChangeAutocreatedPropertyState() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.AUTOCREATED, Value.IS), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), is(Value.IS));
        assertThat(this.childNodeDefinition.isAutoCreated(), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.AUTOCREATED, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.AUTOCREATED), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.AUTOCREATED, Value.IS_NOT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), is(Value.IS_NOT));
        assertThat(this.childNodeDefinition.isAutoCreated(), is(false));
    }

    @Test
    public void shouldChangeDefaultTypePropertyStateToVariantOnly() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.DEFAULT_TYPE, Value.IS), is(false));

        assertThat(this.childNodeDefinition.changeState(PropertyName.DEFAULT_TYPE, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.DEFAULT_TYPE), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.DEFAULT_TYPE), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.DEFAULT_TYPE, Value.IS_NOT), is(false));
    }

    @Test
    public void shouldChangeMandatoryPropertyState() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.MANDATORY, Value.IS), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.MANDATORY), is(Value.IS));
        assertThat(this.childNodeDefinition.isMandatory(), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.MANDATORY, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.MANDATORY), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.MANDATORY), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.MANDATORY, Value.IS_NOT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.MANDATORY), is(Value.IS_NOT));
        assertThat(this.childNodeDefinition.isMandatory(), is(false));
    }

    @Test
    public void shouldChangeOnParentVersionPropertyToVariantStateOnly() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.ON_PARENT_VERSION, Value.IS), is(false));

        assertThat(this.childNodeDefinition.changeState(PropertyName.ON_PARENT_VERSION, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.ON_PARENT_VERSION), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.ON_PARENT_VERSION), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.ON_PARENT_VERSION, Value.IS_NOT), is(false));
    }

    @Test
    public void shouldChangeProtectedPropertyState() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.PROTECTED, Value.IS), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.PROTECTED), is(Value.IS));
        assertThat(this.childNodeDefinition.isProtected(), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.PROTECTED, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.PROTECTED), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.PROTECTED), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.PROTECTED, Value.IS_NOT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.PROTECTED), is(Value.IS_NOT));
        assertThat(this.childNodeDefinition.isProtected(), is(false));
    }

    @Test
    public void shouldChangeRequiredTypesPropertyToVariantStateOnly() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.REQUIRED_TYPES, Value.IS), is(false));

        assertThat(this.childNodeDefinition.changeState(PropertyName.REQUIRED_TYPES, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.REQUIRED_TYPES), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.REQUIRED_TYPES), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.REQUIRED_TYPES, Value.IS_NOT), is(false));
    }

    @Test
    public void shouldChangeSameNameSiblingsPropertyState() {
        assertThat(this.childNodeDefinition.changeState(PropertyName.SAME_NAME_SIBLINGS, Value.IS), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), is(Value.IS));
        assertThat(this.childNodeDefinition.allowsSameNameSiblings(), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.SAME_NAME_SIBLINGS, Value.VARIANT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), is(Value.VARIANT));
        assertThat(this.childNodeDefinition.isVariant(PropertyName.SAME_NAME_SIBLINGS), is(true));

        assertThat(this.childNodeDefinition.changeState(PropertyName.SAME_NAME_SIBLINGS, Value.IS_NOT), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), is(Value.IS_NOT));
        assertThat(this.childNodeDefinition.allowsSameNameSiblings(), is(false));
    }

    @Test
    public void shouldClearRequiredTypes() {
        assertThat(this.childNodeDefinition.addRequiredType("requiredType"), is(true)); //$NON-NLS-1$
        assertThat(this.childNodeDefinition.clearRequiredTypes(), is(true));
        assertThat(this.childNodeDefinition.getRequiredPrimaryTypeNames().length, is(0));
    }

    @Test
    public void shouldNotAllowDuplicateRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(true));
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(false));
    }

    @Test
    public void shouldNotChangeCommentToSameValue() {
        this.childNodeDefinition.setComment("newComment"); //$NON-NLS-1$
        assertThat(this.childNodeDefinition.setComment(this.childNodeDefinition.getComment()), is(false));
    }

    @Test
    public void shouldNotClearSuperTypesWhenEmpty() {
        assertThat(this.childNodeDefinition.clearRequiredTypes(), is(false));
    }

    @Test
    public void shouldNotDefaultTypeAfterConstruction() {
        assertThat(this.childNodeDefinition.getDefaultPrimaryTypeName(), is(nullValue()));
    }

    @Test
    public void shouldNotHaveDefaultRequiredTypeAfterConstruction() {
        assertThat(this.childNodeDefinition.getDefaultPrimaryTypeName(), is(nullValue()));
    }

    @Test
    public void shouldNotHaveRequiredTypesAfterConstruction() {
        assertThat(this.childNodeDefinition.getRequiredPrimaryTypeNames().length, is(0));
    }

    @Test
    public void shouldNotRemoveRequiredTypeThatDoesNotExist() {
        assertThat(this.childNodeDefinition.removeRequiredType("requiredType"), is(false)); //$NON-NLS-1$
    }

    @Test
    public void shouldReceiveEventAfterAddingRequiredType() {
        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(true));

        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.REQUIRED_TYPES.toString()));
        assertThat((String)l.getNewValue(), is(REQUIRED_TYPE));
        assertThat(l.getOldValue(), is(nullValue()));
    }

    @Test
    public void shouldReceiveEventAfterChangingName() {
        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        final String NEW_NAME = "newName"; //$NON-NLS-1$
        this.childNodeDefinition.setName(NEW_NAME);

        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.NAME.toString()));
        assertThat((String)l.getNewValue(), is(NEW_NAME));
        assertThat(l.getOldValue(), is(nullValue()));
    }

    @Test
    public void shouldReceiveEventAfterChangingPrimaryTypeName() {
        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        final String PRIMARY_TYPE = "primaryType"; //$NON-NLS-1$
        this.childNodeDefinition.setDefaultPrimaryTypeName(PRIMARY_TYPE);

        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.DEFAULT_TYPE.toString()));
        assertThat((String)l.getNewValue(), is(PRIMARY_TYPE));
        assertThat(l.getOldValue(), is(nullValue()));
    }

    @Test
    public void shouldReceiveEventAfterClearingSuperTypes() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(true));
        final String[] oldValue = new String[] { REQUIRED_TYPE };

        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        assertThat(this.childNodeDefinition.clearRequiredTypes(), is(true));
        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.REQUIRED_TYPES.toString()));
        assertThat(l.getNewValue(), is(nullValue()));
        assertThat(((String[])l.getOldValue())[0], is(oldValue[0]));
    }

    @Test
    public void shouldReceiveEventAfterRemovingRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(true));

        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        assertThat(this.childNodeDefinition.removeRequiredType(REQUIRED_TYPE), is(true));
        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.REQUIRED_TYPES.toString()));
        assertThat((String)l.getOldValue(), is(REQUIRED_TYPE));
        assertThat(l.getNewValue(), is(nullValue()));
    }

    @Test
    public void shouldReceiveEventWhenCommentIsChanged() {
        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        final String NEW_COMMENT = "comment"; //$NON-NLS-1$
        this.childNodeDefinition.setComment(NEW_COMMENT);

        assertThat(this.childNodeDefinition.getComment(), is(NEW_COMMENT));
        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.COMMENT.toString()));
        assertThat((String)l.getNewValue(), is(NEW_COMMENT));
        assertThat(l.getOldValue(), is(nullValue()));
    }

    @Test
    public void shouldReceiveEventWhenOnParentVersionIsChanged() {
        final Listener l = new Listener();
        assertThat(this.childNodeDefinition.addListener(l), is(true));

        final int OPV = OnParentVersion.COMPUTE.asJcrValue();
        this.childNodeDefinition.setOnParentVersion(OPV);

        assertThat(l.getCount(), is(1));
        assertThat(l.getPropertyName(), is(PropertyName.ON_PARENT_VERSION.toString()));
        assertThat((Integer)l.getNewValue(), is(OPV));
        assertThat((Integer)l.getOldValue(), is(OnParentVersion.DEFAULT_VALUE.asJcrValue()));
    }

    @Test
    public void shouldRemoveRequiredType() {
        final String REQUIRED_TYPE = "requiredType"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.addRequiredType(REQUIRED_TYPE), is(true));
        assertThat(this.childNodeDefinition.removeRequiredType(REQUIRED_TYPE), is(true));
        assertThat(this.childNodeDefinition.getRequiredPrimaryTypeNames().length, is(0));
    }

    @Test
    public void shouldSetAutoCreated() {
        this.childNodeDefinition.setAutoCreated(true);
        assertThat(this.childNodeDefinition.isAutoCreated(), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), is(Value.IS));

        this.childNodeDefinition.setAutoCreated(false);
        assertThat(this.childNodeDefinition.isAutoCreated(), is(false));
        assertThat(this.childNodeDefinition.getState(PropertyName.AUTOCREATED), is(Value.IS_NOT));
    }

    @Test
    public void shouldSetComment() {
        final String NEW_COMMENT = "newComment"; //$NON-NLS-1$
        assertThat(this.childNodeDefinition.setComment(NEW_COMMENT), is(true));
        assertThat(this.childNodeDefinition.getComment(), is(NEW_COMMENT));
    }

    @Test
    public void shouldSetMandatory() {
        this.childNodeDefinition.setMandatory(true);
        assertThat(this.childNodeDefinition.isMandatory(), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.MANDATORY), is(Value.IS));

        this.childNodeDefinition.setMandatory(false);
        assertThat(this.childNodeDefinition.isMandatory(), is(false));
        assertThat(this.childNodeDefinition.getState(PropertyName.MANDATORY), is(Value.IS_NOT));
    }

    @Test
    public void shouldSetName() {
        final String NEW_NAME = "newName"; //$NON-NLS-1$
        this.childNodeDefinition.setName(NEW_NAME);
        assertThat(this.childNodeDefinition.getName(), is(NEW_NAME));
    }

    @Test
    public void shouldSetNameWithEmptyString() {
        this.childNodeDefinition.setName(Utils.EMPTY_STRING);
        assertThat(Utils.isEmpty(this.childNodeDefinition.getName()), is(true));
    }

    @Test
    public void shouldSetNameWithNull() {
        this.childNodeDefinition.setName(null);
        assertThat(Utils.isEmpty(this.childNodeDefinition.getName()), is(true));
    }

    @Test
    public void shouldSetOnParentVersionUsingInt() {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv != OnParentVersion.VARIANT) {
                this.childNodeDefinition.setOnParentVersion(opv.asJcrValue());
                assertThat(this.childNodeDefinition.getOnParentVersion(), is(opv.asJcrValue()));
            }
        }
    }

    @Test
    public void shouldSetOnParentVersionUsingString() {
        for (final OnParentVersion opv : OnParentVersion.values()) {
            if (opv != OnParentVersion.VARIANT) {
                assertThat(this.childNodeDefinition.setOnParentVersion(opv.toString()), is(true));
                assertThat(this.childNodeDefinition.getOnParentVersion(), is(opv.asJcrValue()));
            }
        }
    }

    @Test
    public void shouldSetPrimaryTypeName() {
        final String PRIMARY_TYPE = "primaryType"; //$NON-NLS-1$
        this.childNodeDefinition.setDefaultPrimaryTypeName(PRIMARY_TYPE);
        assertThat(this.childNodeDefinition.getDefaultPrimaryTypeName(), is(PRIMARY_TYPE));
    }

    @Test
    public void shouldSetPrimaryTypeNameUsingEmptyValue() {
        this.childNodeDefinition.setDefaultPrimaryTypeName(Utils.EMPTY_STRING);
        assertThat(Utils.isEmpty(this.childNodeDefinition.getDefaultPrimaryTypeName()), is(true));
    }

    @Test
    public void shouldSetPrimaryTypeNameUsingNull() {
        this.childNodeDefinition.setDefaultPrimaryTypeName(null);
        assertThat(Utils.isEmpty(this.childNodeDefinition.getDefaultPrimaryTypeName()), is(true));
    }

    @Test
    public void shouldSetProtected() {
        this.childNodeDefinition.setProtected(true);
        assertThat(this.childNodeDefinition.isProtected(), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.PROTECTED), is(Value.IS));

        this.childNodeDefinition.setProtected(false);
        assertThat(this.childNodeDefinition.isProtected(), is(false));
        assertThat(this.childNodeDefinition.getState(PropertyName.PROTECTED), is(Value.IS_NOT));
    }

    @Test
    public void shouldSetRequiredTypes() {
        final String REQUIRED_TYPE1 = "requiredType1"; //$NON-NLS-1$
        final String REQUIRED_TYPE2 = "requiredType2"; //$NON-NLS-1$
        final String REQUIRED_TYPE3 = "requiredType3"; //$NON-NLS-1$
        final String[] SUPER_TYPES = new String[] { REQUIRED_TYPE1, REQUIRED_TYPE2, REQUIRED_TYPE3 };
        this.childNodeDefinition.setRequiredPrimaryTypeNames(SUPER_TYPES);

        final String[] requiredTypeNames = this.childNodeDefinition.getRequiredPrimaryTypeNames();
        assertThat(requiredTypeNames[0], is(REQUIRED_TYPE1));
        assertThat(requiredTypeNames[1], is(REQUIRED_TYPE2));
        assertThat(requiredTypeNames[2], is(REQUIRED_TYPE3));
    }

    @Test
    public void shouldSetRequiredTypesWithEmptyArray() {
        assertThat(this.childNodeDefinition.addRequiredType("requiredType"), is(true)); //$NON-NLS-1$
        this.childNodeDefinition.setRequiredPrimaryTypeNames(new String[0]);
        assertThat(this.childNodeDefinition.getRequiredPrimaryTypeNames().length, is(0));
    }

    @Test
    public void shouldSetRequiredTypesWithNull() {
        assertThat(this.childNodeDefinition.addRequiredType("requiredType"), is(true)); //$NON-NLS-1$
        this.childNodeDefinition.setRequiredPrimaryTypeNames(null);
        assertThat(this.childNodeDefinition.getRequiredPrimaryTypeNames().length, is(0));
    }

    @Test
    public void shouldSetSameNameSiblings() {
        this.childNodeDefinition.setSameNameSiblings(true);
        assertThat(this.childNodeDefinition.allowsSameNameSiblings(), is(true));
        assertThat(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), is(Value.IS));

        this.childNodeDefinition.setSameNameSiblings(false);
        assertThat(this.childNodeDefinition.allowsSameNameSiblings(), is(false));
        assertThat(this.childNodeDefinition.getState(PropertyName.SAME_NAME_SIBLINGS), is(Value.IS_NOT));
    }
}
