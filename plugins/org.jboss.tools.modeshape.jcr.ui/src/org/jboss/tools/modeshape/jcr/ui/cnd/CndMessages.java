/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.jboss.tools.modeshape.jcr.ui.cnd;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.JcrModelObject;

/**
 * Localized messages used by classes in the <code>org.jboss.tools.modeshape.jcr.ui.cnd</code> package.
 */
public class CndMessages extends NLS {

    /**
     * A name for the abstract attribute name for a radio or check button.
     */
    public static String abstractAttribute;

    /**
     * A tool tip message of the node type definition's abstract attribute.
     */
    public static String abstractAttributeToolTip;

    /**
     * A dialog message indicating the namespace mapping is a built-in and asks the user to OK the dialog.
     */
    public static String acceptBuiltInNamespaceDialogMsg;

    /**
     * A dialog message indicating the default value has been changed and asks the user to OK the dialog. One parameter, the default
     * value, is required.
     */
    public static String acceptDefaultValueDialogMsg;

    /**
     * A dialog message indicating the namespace mapping has been changed and asks the user to OK the dialog.
     */
    public static String acceptNamespaceDialogMsg;

    /**
     * A dialog message indicating the qualified name has been changed and asks the user to OK the dialog. One parameter, the type
     * of qualified name, is required.
     */
    public static String acceptQualifiedNameDialogMsg;

    /**
     * A dialog message indicating the value constraint has been changed and asks the user to OK the dialog. One parameter, the
     * value constraint, is required.
     */
    public static String acceptValueConstraintDialogMsg;

    /**
     * The context menu item text for the add child node definition action.
     */
    public static String addChildNodeMenuText;

    /**
     * The tool tip message of the add child node definition action.
     */
    public static String addChildNodeToolTip;

    /**
     * The context menu item text for the add default value action.
     */
    public static String addDefaultValueMenuText;

    /**
     * The tool tip message of the add default value action.
     */
    public static String addDefaultValueToolTip;

    /**
     * The context menu item text for the add namespace mapping action.
     */
    public static String addNamespaceMenuText;

    /**
     * The tool tip message of the add namespace mapping action.
     */
    public static String addNamespaceToolTip;

    /**
     * The context menu item text for the add node type definition action.
     */
    public static String addNodeTypeMenuText;

    /**
     * The tool tip message of the add node type definition action.
     */
    public static String addNodeTypeToolTip;

    /**
     * The context menu item text for the add property action.
     */
    public static String addPropertyMenuText;

    /**
     * The tool tip message of the add property definition action.
     */
    public static String addPropertyToolTip;

    /**
     * The context menu item text for the add required type action.
     */
    public static String addRequiredTypeMenuText;

    /**
     * The tool tip message of the add required type action.
     */
    public static String addRequiredTypeToolTip;

    /**
     * The context menu item text for the add supertype action.
     */
    public static String addSuperTypeMenuText;

    /**
     * The tool tip message of the add supertype action.
     */
    public static String addSuperTypeToolTip;

    /**
     * The context menu item text for the add value constraint action.
     */
    public static String addValueConstraintMenuText;

    /**
     * The tool tip message of the add value constraint action.
     */
    public static String addValueConstraintToolTip;

    /**
     * The column header text for attributes.
     */
    public static String attributesHeaderText;

    /**
     * A name for the autocreated attribute suitable for a radio or check button.
     */
    public static String autocreatedAttribute;

    /**
     * A tool tip message of a child node definition's and property definition's autocreated attribute.
     */
    public static String autocreatedAttributeToolTip;

    /**
     * The tool tip for the attributes of a child node definition.
     */
    public static String childNodeAttributesToolTip;

    /**
     * A label for the default type of a child node definition.
     */
    public static String childNodeDefaultTypeLabel;

    /**
     * The tool tip text for the default type of a child node definition.
     */
    public static String childNodeDefaultTypeToolTip;

    /**
     * The message area title of the child node definition dialog when creating a new child node.
     */
    public static String childNodeDialogCreateTitle;

    /**
     * The message area title of the child node definition dialog when an existing child node is being edited.
     */
    public static String childNodeDialogEditTitle;

    /**
     * The message displayed in the child node dialog when first displayed.
     */
    public static String childNodeDialogMsg;

    /**
     * The title of the child node definition dialog.
     */
    public static String childNodeDialogTitle;

    /**
     * The tool tip for a child node definition name.
     */
    public static String childNodeNameToolTip;

    /**
     * The tool tip text for the required types of a child node definition.
     */
    public static String childNodeRequiredTypesToolTip;

    /**
     * The message of the dialog shown within the CND editor when the editor notices that the CND file has been changed outside the
     * editor. One parameter, the file name, is expected.
     */
    public static String cndChangedOnFileSystemDialogMsg;

    /**
     * The title of the dialog shown within the CND editor when the editor notices that the CND file has been changed outside the
     * editor.
     */
    public static String cndChangedOnFileSystemDialogTitle;

    /**
     * The description of the child node definition section in the CND editor's form page.
     */
    public static String cndEditorChildNodeSectionDescription;

    /**
     * The description of the child node definition section in the CND editor's form page. One parameter, the node type definition
     * name, is required.
     */
    public static String cndEditorChildNodeSectionDescriptionWithNodeTypeName;

    /**
     * The title of the child node definition section in the CND editor's form page.
     */
    public static String cndEditorChildNodeSectionTitle;

    /**
     * The description of the details section in the CND editor's form page.
     */
    public static String cndEditorDetailsSectionDescription;

    /**
     * The description of the details section in the CND editor's form page. One parameter, the node type definition name, is
     * required.
     */
    public static String cndEditorDetailsSectionDescriptionWithNodeTypeName;

    /**
     * The title of the node type definition's details section in the CND editor's form page.
     */
    public static String cndEditorDetailsSectionTitle;

    /**
     * The title of the forms page of the CND editor.
     */
    public static String cndEditorFormsPageTitle;

    /**
     * The message of the dialog shown when an input to the CND editor was not a file.
     */
    public static String cndEditorInputNotAFile;

    /**
     * The description of the namespace mappings section in the CND editor's form page.
     */
    public static String cndEditorNamespacesSectionDescription;

    /**
     * The title of the namespace mappings section in the CND editor's form page.
     */
    public static String cndEditorNamespacesSectionTitle;

    /**
     * The description of the namespace mappings section in the CND editor's form page.
     */
    public static String cndEditorNodeTypeSectionDescription;

    /**
     * The title of the namespace mappings section in the CND editor's form page.
     */
    public static String cndEditorNodeTypeSectionTitle;

    /**
     * The description of the property definition section in the CND editor's form page.
     */
    public static String cndEditorPropertySectionDescription;

    /**
     * The description of the property definition section in the CND editor's form page. One parameter, the node type definition
     * name, is required.
     */
    public static String cndEditorPropertySectionDescriptionWithNodeTypeName;

    /**
     * The title of the property definition section in the CND editor's form page.
     */
    public static String cndEditorPropertySectionTitle;

    /**
     * The message of the dialog shown when refreshing the CND editor with the CND file from the file system fails.
     */
    public static String cndEditorRefreshErrorMsg;

    /**
     * The title of the dialog shown when refreshing the CND editor with the CND file from the file system fails.
     */
    public static String cndEditorRefreshErrorTitle;

    /**
     * The message of an error dialog shown when saving the CND editor fails.
     */
    public static String cndEditorSaveError;

    /**
     * The title of the source page of the CND editor.
     */
    public static String cndEditorSourcePageTitle;

    /**
     * The title of the CND editor.
     */
    public static String cndEditorTitle;

    /**
     * The message area message of the CND validation message dialog. Four parameters, the name of the CND, the number of errors,
     * the number of warnings, and the number of info messages, are required.
     */
    public static String cndMessageDialogMessageAreaMessage;

    /**
     * The message area title of the CND validation message dialog.
     */
    public static String cndMessageDialogMessageAreaTitle;

    /**
     * The title of the CND validation message dialog.
     */
    public static String cndMessageDialogTitle;

    /**
     * The description of the CND preference page.
     */
    public static String cndPrefPageDescription;

    /**
     * The message of the CND preference page.
     */
    public static String cndPrefPageMessage;

    /**
     * The title of the CND prefrence page.
     */
    public static String cndPrefPageTitle;

    /**
     * The column header text indicating if a CND element has a comment.
     */
    public static String commentedHeaderText;

    /**
     * The tool tip message indicating the CND element has a comment.
     */
    public static String commentedToolTip;

    /**
     * A label for a control that shows a CND comment.
     */
    public static String commentLabel;

    /**
     * The context menu item text for the copy child node definition action.
     */
    public static String copyChildNodeMenuText;

    /**
     * The tool tip message of the copy child node definition action.
     */
    public static String copyChildNodeToolTip;

    /**
     * The context menu item text for the copy namespace action.
     */
    public static String copyNamespaceMenuText;

    /**
     * The tool tip message of the copy namespace action.
     */
    public static String copyNamespaceToolTip;

    /**
     * The context menu item text for the copy node type definition action.
     */
    public static String copyNodeTypeMenuText;

    /**
     * The tool tip message of the copy node type definition action.
     */
    public static String copyNodeTypeToolTip;

    /**
     * The context menu item text for the copy property action.
     */
    public static String copyPropertyMenuText;

    /**
     * The tool tip message of the copy property definition action.
     */
    public static String copyPropertyToolTip;

    /**
     * The column header text of the declaring node type of an item definition.
     */
    public static String declaringNodeTypeHeaderText;

    /**
     * The tool tip message of the declaring node type of an item definition.
     */
    public static String declaringNodeTypeToolTip;

    /**
     * The column header text of the default type of a child node definition.
     */
    public static String defaultTypeHeaderText;

    /**
     * The message area title of the default value dialog when creating a new default value.
     */
    public static String defaultValueDialogCreateMsg;

    /**
     * The message area title of the default value dialog when an existing default value is being edited.
     */
    public static String defaultValueDialogEditMsg;

    /**
     * The message displayed in the default value dialog when first displayed.
     */
    public static String defaultValueDialogMsg;

    /**
     * A label for a default value control.
     */
    public static String defaultValueLabel;

    /**
     * The column header text of the default values of a property definition.
     */
    public static String defaultValuesHeaderText;

    /**
     * A label for a default values control.
     */
    public static String defaultValuesLabel;

    /**
     * A tool tip message of a property definition's default value.
     */
    public static String defaultValuesToolTip;

    /**
     * A tool tip message for one default value of a property definition.
     */
    public static String defaultValueToolTip;

    /**
     * The question message of the dialog for deleting a child node definition. One parameter, the child node definition name, is
     * required.
     */
    public static String deleteChildNodeDialogMessage;

    /**
     * The title of the dialog for deleting a child node definition.
     */
    public static String deleteChildNodeDialogTitle;

    /**
     * The context menu item text for the delete child node definition action.
     */
    public static String deleteChildNodeMenuText;

    /**
     * The tool tip message of the delete child node definition action.
     */
    public static String deleteChildNodeToolTip;

    /**
     * The question message of the dialog for deleting a default value. One parameter, the default value, is required.
     */
    public static String deleteDefaultValueDialogMessage;

    /**
     * The title of the dialog for deleting a default value.
     */
    public static String deleteDefaultValueDialogTitle;

    /**
     * The context menu item text for the delete default value action.
     */
    public static String deleteDefaultValueMenuText;

    /**
     * The tool tip message of the delete default value action.
     */
    public static String deleteDefaultValueToolTip;

    /**
     * The question message of the dialog for deleting a namespace mapping. One parameter, the namespace prefix, is required.
     */
    public static String deleteNamespaceDialogMessage;

    /**
     * The title of the dialog for deleting a namespace mapping.
     */
    public static String deleteNamespaceDialogTitle;

    /**
     * The context menu item text for the delete namespace mapping action.
     */
    public static String deleteNamespaceMenuText;

    /**
     * The tool tip message of the delete namespace mapping action.
     */
    public static String deleteNamespaceToolTip;

    /**
     * The question message of the dialog for deleting a node type definition. One parameter, the node type definition name, is
     * required.
     */
    public static String deleteNodeTypeDialogMessage;

    /**
     * The title of the dialog for deleting a node type definition.
     */
    public static String deleteNodeTypeDialogTitle;

    /**
     * The context menu item text for the delete node type definition action.
     */
    public static String deleteNodeTypeMenuText;

    /**
     * The tool tip message of the delete node type definition action.
     */
    public static String deleteNodeTypeToolTip;

    /**
     * The question message of the dialog for deleting a property definition. One parameter, the property definition name, is
     * required.
     */
    public static String deletePropertyDialogMessage;

    /**
     * The title of the dialog for deleting a property definition.
     */
    public static String deletePropertyDialogTitle;

    /**
     * The context menu item text for the delete property action.
     */
    public static String deletePropertyMenuText;

    /**
     * The tool tip message of the delete property definition action.
     */
    public static String deletePropertyToolTip;

    /**
     * The question message of the dialog for deleting a required type. One parameter, the required type name, is required.
     */
    public static String deleteRequiredTypeDialogMessage;

    /**
     * The title of the dialog for deleting a required type.
     */
    public static String deleteRequiredTypeDialogTitle;

    /**
     * The context menu item text for the delete required type action.
     */
    public static String deleteRequiredTypeMenuText;

    /**
     * The tool tip message of the delete required type action.
     */
    public static String deleteRequiredTypeToolTip;

    /**
     * The question message of the dialog for deleting a supertype. One parameter, the super type name, is required.
     */
    public static String deleteSuperTypeDialogMessage;

    /**
     * The title of the dialog for deleting a supertype.
     */
    public static String deleteSuperTypeDialogTitle;

    /**
     * The context menu item text for the delete supertype action.
     */
    public static String deleteSuperTypeMenuText;

    /**
     * The tool tip message of the delete supertype action.
     */
    public static String deleteSuperTypeToolTip;

    /**
     * The question message of the dialog for deleting a value constraint. One parameter, the value constraint, is required.
     */
    public static String deleteValueConstraintDialogMessage;

    /**
     * The title of the dialog for deleting a value constraint.
     */
    public static String deleteValueConstraintDialogTitle;

    /**
     * The context menu item text for the delete value constraint action.
     */
    public static String deleteValueConstraintMenuText;

    /**
     * The tool tip message of the delete value constraint action.
     */
    public static String deleteValueConstraintToolTip;

    /**
     * The context menu item text for the edit child node definition action.
     */
    public static String editChildNodeMenuText;

    /**
     * The tool tip message of the edit child node definition action.
     */
    public static String editChildNodeToolTip;

    /**
     * The context menu item text for the edit default value action.
     */
    public static String editDefaultValueMenuText;

    /**
     * The tool tip message of the edit default value action.
     */
    public static String editDefaultValueToolTip;

    /**
     * The namespace editor dialog message area title when editing an existing namespace mapping.
     */
    public static String editNamespaceDialogTitle;

    /**
     * The context menu item text for the edit namespace mapping action.
     */
    public static String editNamespaceMenuText;

    /**
     * The tool tip message of the edit namespace mapping action.
     */
    public static String editNamespaceToolTip;

    /**
     * The context menu item text for the edit node type definition action.
     */
    public static String editNodeTypeMenuText;

    /**
     * The tool tip message of the edit node type definition action.
     */
    public static String editNodeTypeToolTip;

    /**
     * The context menu item text for the edit property action.
     */
    public static String editPropertyMenuText;

    /**
     * The tool tip message of the edit property definition action.
     */
    public static String editPropertyToolTip;

    /**
     * The qualified name editor dialog message area title when editing an existing required type.
     */
    public static String editRequiredTypeDialogTitle;

    /**
     * The context menu item text for the edit required type action.
     */
    public static String editRequiredTypeMenuText;

    /**
     * The tool tip message of the edit required type action.
     */
    public static String editRequiredTypeToolTip;

    /**
     * The qualified name editor dialog message area title when editing an existing supertype.
     */
    public static String editSuperTypeDialogTitle;

    /**
     * The context menu item text for the edit supertype action.
     */
    public static String editSuperTypeMenuText;

    /**
     * The tool tip message of the edit supertype action.
     */
    public static String editSuperTypeToolTip;

    /**
     * The context menu item text for the edit value constraint action.
     */
    public static String editValueConstraintMenuText;

    /**
     * The tool tip message of the edit value constraint action.
     */
    public static String editValueConstraintToolTip;

    /**
     * A tool tip message for the "equals" query operator.
     */
    public static String equalsOperatorToolTip;

    /**
     * An error message indicating the new child node definition was not added to the node type definition. One parameter, the child
     * definition name, is required.
     */
    public static String errorAddingChildNode;

    /**
     * An error message indicating the new default value was not added to the property definition. One parameter, the default value,
     * is required.
     */
    public static String errorAddingDefaultValue;

    /**
     * An error message indicating the new namespace mapping was not added to the CND. One parameter, the namespace mapping, is
     * required.
     */
    public static String errorAddingNamespaceMapping;

    /**
     * An error message indicating the new node type definition was not added to the CND. One parameter, the node type definition
     * name, is required.
     */
    public static String errorAddingNodeType;

    /**
     * An error message indicating the new property definition was not added to the node type definition. One parameter, the
     * property name, is required.
     */
    public static String errorAddingProperty;

    /**
     * An error message indicating the new required type was not added to the child node definition. One parameter, the required
     * type, is required.
     */
    public static String errorAddingRequiredType;

    /**
     * An error message indicating the new supertype was not added to the node type definition. One parameter, the supertype, is
     * required.
     */
    public static String errorAddingSupertype;

    /**
     * An error message indicating the new value constraint was not added to the property definition. One parameter, the value
     * constraint, is required.
     */
    public static String errorAddingValueConstraint;

    /**
     * An error message indicating copying a {@link JcrModelObject JCR model object} to the clipboard new value constraint was not added to the property definition. One parameter, the value
     * constraint, is required.
     */
    public static String errorCopyingOrReadingJcrModelObjectToFromClipboard;

    /**
     * An error message indicating deleting the child node definition did not get saved. One parameter, the child node definition
     * name, is required.
     */
    public static String errorDeletingChildNode;

    /**
     * An error message indicating deleting the default value did not get saved. One parameter, the default value, is required.
     */
    public static String errorDeletingDefaultValue;

    /**
     * An error message indicating deleting the namespace mapping did not get saved. One parameter, the namespace prefix, is
     * required.
     */
    public static String errorDeletingNamespace;

    /**
     * An error message indicating deleting the node type definition did not get saved. One parameter, the node type definition
     * name, is required.
     */
    public static String errorDeletingNodeType;

    /**
     * An error message indicating deleting the property definition did not get saved. One parameter, the property definition name,
     * is required.
     */
    public static String errorDeletingProperty;

    /**
     * An error message indicating deleting the supertype did not get saved. One parameter, the supertype name, is required.
     */
    public static String errorDeletingSupertype;

    /**
     * An error message indicating deleting the value constraint did not get saved. One parameter, the value constraint, is
     * required.
     */
    public static String errorDeletingValueConstraint;

    /**
     * An error message indicating the updated child node definition did not get saved to the node type definition. Three
     * parameters, the child node definition name, a flag indicating if the remove succeeded, and a flag indicating if the add
     * succeeded, are required.
     */
    public static String errorEditingChildNode;

    /**
     * An error message indicating the updated default value did not get saved to the CND. Three parameters, the default value, a
     * flag indicating if the remove succeeded, and a flag indicating if the add succeeded, are required.
     */
    public static String errorEditingDefaultValue;

    /**
     * An error message indicating the updated namespace mapping did not get saved to the CND. Three parameters, the namespace
     * mapping, a flag indicating if the remove succeeded, and a flag indicating if the add succeeded, are required.
     */
    public static String errorEditingNamespaceMapping;

    /**
     * An error message indicating the updated property definition did not get saved to the node type definition. Three parameters,
     * the property definition name, a flag indicating if the remove succeeded, and a flag indicating if the add succeeded, are
     * required.
     */
    public static String errorEditingProperty;

    /**
     * An error message indicating the updated required type did not get saved to the CND. Three parameters, the required type, a
     * flag indicating if the remove succeeded, and a flag indicating if the add succeeded, are required.
     */
    public static String errorEditingRequiredType;

    /**
     * An error message indicating the updated supertype did not get saved to the CND. Three parameters, the supertype, a flag
     * indicating if the remove succeeded, and a flag indicating if the add succeeded, are required.
     */
    public static String errorEditingSupertype;

    /**
     * An error message indicating the updated value constraint did not get saved to the CND. Three parameters, the value
     * constraint, a flag indicating if the remove succeeded, and a flag indicating if the add succeeded, are required.
     */
    public static String errorEditingValueConstraint;

    /**
     * The error message when there was a problem obtaining the node type definition's inherited properties and child nodes. One
     * parameter, the node type definition name, is required.
     */
    public static String errorObtainingInheritedItemDefinitions;

    /**
     * The error message when the CND editor could not be opened.
     */
    public static String errorOpeningCndEditor;

    /**
     * A tool tip message for the "greater than or equals to" query operator.
     */
    public static String greaterThanEqualsOperatorToolTip;

    /**
     * A tool tip message for the "greater than" query operator.
     */
    public static String greaterThanOperatorToolTip;

    /**
     * A tool tip message for the "less than or equals to" query operator.
     */
    public static String lessThanEqualsOperatorToolTip;

    /**
     * A tool tip message for the "less than" query operator.
     */
    public static String lessThanOperatorToolTip;

    /**
     * A name for the mandatory attribute suitable for a radio or check button.
     */
    public static String mandatoryAttribute;

    /**
     * A tool tip message of the child node definition's and property definition's mandatory attribute.
     */
    public static String mandatoryAttributeToolTip;

    /**
     * A message indicating there is no value.
     */
    public static String missingValue;

    /**
     * A name for the mixin attribute suitable for a radio or check button.
     */
    public static String mixinAttribute;

    /**
     * A tool tip message of the node type definition's mixin attribute.
     */
    public static String mixinAttributeToolTip;

    /**
     * A name for the multiple attribute suitable for a radio or check button.
     */
    public static String multipleAttribute;

    /**
     * A tool tip message of the property definition's multiple attribute.
     */
    public static String multipleAttributeToolTip;

    /**
     * The column header text for a generic name property or attribute.
     */
    public static String nameHeaderText;

    /**
     * A label for a generic name control.
     */
    public static String nameLabel;

    /**
     * The message displayed in the namespace mapping dialog when first displayed.
     */
    public static String namespaceDialogMsg;

    /**
     * The title of the namespace mapping dialog.
     */
    public static String namespaceDialogTitle;

    /**
     * A label for a namespace mapping control.
     */
    public static String namespaceLabel;

    /**
     * The column header text for a namespace prefix.
     */
    public static String namespacePrefixHeaderText;

    /**
     * A label for a namespace prefix control.
     */
    public static String namespacePrefixLabel;

    /**
     * The tool tip for a namespace prefix.
     */
    public static String namespacePrefixToolTip;

    /**
     * The column header text for a namespace URI.
     */
    public static String namespaceUriHeaderText;

    /**
     * The tool tip for a namespace URI.
     */
    public static String namespaceUriToolTip;

    /**
     * The namespace editor dialog message area title when creating a new namespace mapping.
     */
    public static String newNamespaceDialogTitle;

    /**
     * The qualified name editor dialog message area title when creating a new node type definition.
     */
    public static String newNodeTypeDialogTitle;

    /**
     * The qualified name editor dialog message area title when creating a new required type.
     */
    public static String newRequiredTypeDialogTitle;

    /**
     * The qualified name editor dialog message area title when creating a new supertype.
     */
    public static String newSuperTypeDialogTitle;

    /**
     * The column header text for the node type definition name.
     */
    public static String nodeTypeNameHeaderText;

    /**
     * The message displayed in the node type name pattern filter when the pattern is empty.
     */
    public static String nodeTypeNamePatternMessage;

    /**
     * The tool tip for a node type definition name.
     */
    public static String nodeTypeNameToolTip;

    /**
     * A name for the noFullText attribute suitable for a radio or check button.
     */
    public static String noFullTextAttribute;

    /**
     * A tool tip message of the property definition's noFullText attribute.
     */
    public static String noFullTextAttributeToolTip;

    /**
     * The text indicating the qualified name does not have a qualifier.
     */
    public static String noNameQualifierChoice;

    /**
     * A name for the noQueryOrder attribute suitable for a radio or check button.
     */
    public static String noQueryOrderAttribute;

    /**
     * A tool tip message of the property definition's noQueryOrder attribute.
     */
    public static String noQueryOrderAttributeToolTip;

    /**
     * The text indicating a choice has not been made.
     */
    public static String notAssignedItemChoice;

    /**
     * A label for a notation type choice control.
     */
    public static String notationTypeLabel;

    /**
     * A tool tip for CND notation type.
     */
    public static String notationTypeToolTip;

    /**
     * A tool tip message for the "not equals" query operator.
     */
    public static String notEqualsOperatorToolTip;

    /**
     * A label for an on parent version attribute control.
     */
    public static String onParentVersionLabel;

    /**
     * A tool tip message of a child node definition's and property definition's on parent version attribute.
     */
    public static String onParentVersionToolTip;

    /**
     * The context menu item text for the open declaring node type action.
     */
    public static String openDeclaringNodeTypeMenuText;

    /**
     * A tool tip message of the open declaring node type action.
     */
    public static String openDeclaringNodeTypeToolTip;

    /**
     * A label of a hyperlink that opens the CND Editor preferences.
     */
    public static String openCndEditorPreferencesHyperlink;

    /**
     * A name for the orderable attribute suitable for a radio or check button.
     */
    public static String orderableAttribute;

    /**
     * A tool tip message of the node type definition's orderable attribute.
     */
    public static String orderableAttributeToolTip;

    /**
     * The context menu item text for the paste child node definition action.
     */
    public static String pasteChildNodeMenuText;

    /**
     * The tool tip message of the paste child node definition action.
     */
    public static String pasteChildNodeToolTip;

    /**
     * The context menu item text for the paste namespace action.
     */
    public static String pasteNamespaceMenuText;

    /**
     * The tool tip message of the paste namespace action.
     */
    public static String pasteNamespaceToolTip;

    /**
     * The context menu item text for the paste node type definition action.
     */
    public static String pasteNodeTypeMenuText;

    /**
     * The tool tip message of the paste node type definition action.
     */
    public static String pasteNodeTypeToolTip;

    /**
     * The context menu item text for the paste property action.
     */
    public static String pastePropertyMenuText;

    /**
     * The tool tip message of the paste property definition action.
     */
    public static String pastePropertyToolTip;

    /**
     * A label suitable for a preview control.
     */
    public static String previewLabel;

    /**
     * A message stating that preview is not available.
     */
    public static String previewNotAvailableMessage;

    /**
     * A label for a primary item control.
     */
    public static String primaryItemLabel;

    /**
     * A tool tip message of the node type definition's primary item attribute.
     */
    public static String primaryItemToolTip;

    /**
     * The tool tip for the attributes of a property definition.
     */
    public static String propertyAttributesToolTip;

    /**
     * The tool tip for the default values of a property definition.
     */
    public static String propertyDefaultValuesToolTip;

    /**
     * The message area title of the property definition dialog when creating a new property.
     */
    public static String propertyDialogCreateTitle;

    /**
     * The message area title of the property definition dialog when an existing property is being edited.
     */
    public static String propertyDialogEditTitle;

    /**
     * The message displayed in the property dialog when first displayed.
     */
    public static String propertyDialogMsg;

    /**
     * The title of the property definition dialog.
     */
    public static String propertyDialogTitle;

    /**
     * The tool tip for a property definition name.
     */
    public static String propertyNameToolTip;

    /**
     * The tool tip for the data type of a property definition.
     */
    public static String propertyTypeToolTip;

    /**
     * The tool tip for the value constraints of a property definition.
     */
    public static String propertyValueConstraintsToolTip;

    /**
     * A name for the protected attribute suitable for a radio or check button.
     */
    public static String protectedAttribute;

    /**
     * A tool tip message of the child node definition's and property definition's protected attribute.
     */
    public static String protectedAttributeToolTip;

    /**
     * The message displayed in the qualified name dialog when first displayed. One parameter, the type of qualified name, is
     * required.
     */
    public static String qualifiedNameDialogMsg;

    /**
     * The title of the qualified name dialog.
     */
    public static String qualifiedNameDialogTitle;

    /**
     * A label for a qualifier control of a qualified name.
     */
    public static String qualifierLabel;

    /**
     * A name for the queryable attribute suitable for a radio or check button.
     */
    public static String queryableAttribute;

    /**
     * A tool tip message of the node type definition's queryable attribute.
     */
    public static String queryableAttributeToolTip;

    /**
     * A label for a query operators control.
     */
    public static String queryOperatorsLabel;

    /**
     * Choice text for using a double quote character.
     */
    public static String quoteCharDoubleChoiceLabel;

    /**
     * Choice text for not using a quote character.
     */
    public static String quoteCharNoneChoiceLabel;

    /**
     * A label for a quote character control.
     */
    public static String quoteCharPolicyLabel;

    /**
     * A tool tip for a quote character choice control.
     */
    public static String quoteCharPolicyToolTip;

    /**
     * Choice text for using a single quote character.
     */
    public static String quoteCharSingleChoiceLabel;

    /**
     * The column header text for the required types of a child node definition.
     */
    public static String requiredTypesHeaderText;

    /**
     * A label for a required types control.
     */
    public static String requiredTypesLabel;

    /**
     * A name for the same named siblings attribute suitable for a radio or check button.
     */
    public static String sameNamedSiblingsAttribute;

    /**
     * A tool tip message of a child node definition's same named siblings attribute.
     */
    public static String sameNamedSiblingsAttributeToolTip;

    /**
     * A tool tip message for the show inherited child node action.
     */
    public static String showInheritedChildNodesToolTip;

    /**
     * A tool tip message for the show inherited properties action.
     */
    public static String showInheritedPropertiesToolTip;

    /**
     * A tool tip message of a status bar field displaying the selected child node definition's name.
     */
    public static String statusBarChildNodeToolTip;

    /**
     * A tool tip message of a status bar field displaying the read-only status of a CND.
     */
    public static String statusBarCndEditableTip;

    /**
     * A tool tip message of a status bar field displaying the selected node type definition's name.
     */
    public static String statusBarNodeTypeToolTip;

    /**
     * A status bar message for when there is no selection.
     */
    public static String statusBarNoSelection;

    /**
     * A tool tip message of a status bar field displaying the selected property definition's name.
     */
    public static String statusBarPropertyToolTip;

    /**
     * A label for a supertypes control.
     */
    public static String supertypesLabel;

    /**
     * A tool tip message of a node type definition's supertypes property.
     */
    public static String supertypesToolTip;

    /**
     * The column header text for a type.
     */
    public static String typeHeaderText;

    /**
     * A label for a type control.
     */
    public static String typeLabel;

    /**
     * A tooltip message for the control where the name of the qualified name is being edited.
     */
    public static String unqualifiedNameToolTip;

    /**
     * A label for a URI control.
     */
    public static String uriLabel;

    /**
     * A tooltip message for a control containing all the valid qualifed name qualifiers in a CND.
     */
    public static String validQualifiersToolTip;

    /**
     * The message area title of the value constraint dialog when creating a new value constraint.
     */
    public static String valueConstraintDialogCreateMsg;

    /**
     * The message area title of the value constraint dialog when an existing value constraint is being edited.
     */
    public static String valueConstraintDialogEditMsg;

    /**
     * The message displayed in the value constraint dialog when first displayed.
     */
    public static String valueConstraintDialogMsg;

    /**
     * A label for a value constraint control.
     */
    public static String valueConstraintLabel;

    /**
     * The column header text for the value constraints of a property definition.
     */
    public static String valueConstraintsHeaderText;

    /**
     * A label for a value constraints control.
     */
    public static String valueConstraintsLabel;

    /**
     * A tool tip message of a property definition's value constraints.
     */
    public static String valueConstraintsToolTip;

    /**
     * A tool tip message for one value constraint of a property definition.
     */
    public static String valueConstraintToolTip;

    /**
     * The title of the string value editor dialog.
     */
    public static String valueEditorDialogTitle;

    static {
        NLS.initializeMessages("org.jboss.tools.modeshape.jcr.ui.cnd.cndMessages", CndMessages.class); //$NON-NLS-1$
    }
}
