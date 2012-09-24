/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jboss.tools.modeshape.jcr.ItemOwnerProvider;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.MultiValidationStatus;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition.PropertyName;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
import org.jboss.tools.modeshape.jcr.attributes.PropertyType;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.jboss.tools.modeshape.jcr.cnd.CndValidator;
import org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;
import org.jboss.tools.modeshape.jcr.ui.JcrUiUtils;
import org.jboss.tools.modeshape.ui.UiMessages;
import org.jboss.tools.modeshape.ui.UiUtils;
import org.jboss.tools.modeshape.ui.actions.DelegateAction;
import org.jboss.tools.modeshape.ui.forms.ErrorMessage;
import org.jboss.tools.modeshape.ui.forms.FormUtils;
import org.jboss.tools.modeshape.ui.forms.FormUtils.Styles;
import org.jboss.tools.modeshape.ui.forms.MessageFormDialog;

/**
 * A dialog used to create and edit {@link PropertyDefinition}s.
 */
final class PropertyDialog extends FormDialog {

    private IAction addDefaultValue;
    private IAction addValueConstraint;
    private Button btnOk;
    private final ErrorMessage defaultValuesError;
    private TableViewer defaultValuesViewer;
    private IAction deleteDefaultValue;
    private IAction deleteValueConstraint;
    private IAction editDefaultValue;
    private IAction editValueConstraint;
    /**
     * The existing namespace prefixes contained in the CND (never <code>null</code> but can be empty).
     */
    private final Collection<String> existingNamespacePrefixes;
    /**
     * The existing child node definition names contained in the CND (never <code>null</code> but can be empty).
     */
    private final Collection<QualifiedName> existingPropertyNames;
    private QualifiedNameEditor nameEditor;
    private final ErrorMessage nameError;
    private final boolean nodeTypeQueryable; // query ops are not used if node type is not queryable
    private PropertyDefinition originalProperty;
    private PropertyDefinition propertyBeingEdited;
    private ScrolledForm scrolledForm;
    private final ErrorMessage valueConstraintsError;
    private TableViewer valueConstraintsViewer;

    /**
     * Used to create a new property definition.
     *
     * @param parentShell the parent shell (can be <code>null</code>)
     * @param ownerProvider an item owner provider for the new property (cannot be <code>null</code>)
     * @param existingPropertyNames the existing property names (can be <code>null</code> or empty)
     * @param existingNamespacePrefixes the existing CND namespace prefixes (can be <code>null</code> or empty)
     * @param nodeTypeQueryable indicates if node type of the property being created is queryable
     */
    public PropertyDialog( final Shell parentShell,
                           final ItemOwnerProvider ownerProvider,
                           final Collection<QualifiedName> existingPropertyNames,
                           final Collection<String> existingNamespacePrefixes,
                           final boolean nodeTypeQueryable ) {
        super(parentShell);
        this.existingPropertyNames = ((existingPropertyNames == null) ? Collections.<QualifiedName>emptyList()
                                                                      : new ArrayList<QualifiedName>(existingPropertyNames));
        this.existingNamespacePrefixes = ((existingNamespacePrefixes == null) ? Collections.<String>emptyList()
                                                                              : new ArrayList<String>(existingNamespacePrefixes));
        this.nodeTypeQueryable = nodeTypeQueryable;
        this.defaultValuesError = new ErrorMessage();
        this.nameError = new ErrorMessage();
        this.valueConstraintsError = new ErrorMessage();
        this.propertyBeingEdited = new PropertyDefinition(ownerProvider);
    }

    /**
     * Used to edit a property definition.
     *
     * @param parentShell the parent shell (can be <code>null</code>)
     * @param ownerProvider an item owner provider for the new property (cannot be <code>null</code>)
     * @param existingPropertyNames the existing property names (can be <code>null</code> or empty)
     * @param existingNamespacePrefixes the existing CND namespace prefixes (can be <code>null</code> or empty)
     * @param nodeTypeQueryable indicates if node type of the property being edited is queryable
     * @param propertyDefinitionBeingEdited the property definition being edited (cannot be <code>null</code>)
     */
    public PropertyDialog( final Shell parentShell,
                           final ItemOwnerProvider ownerProvider,
                           final Collection<QualifiedName> existingPropertyNames,
                           final Collection<String> existingNamespacePrefixes,
                           final boolean nodeTypeQueryable,
                           final PropertyDefinition propertyDefinitionBeingEdited ) {
        this(parentShell, ownerProvider, existingPropertyNames, existingNamespacePrefixes, nodeTypeQueryable);

        Utils.verifyIsNotNull(propertyDefinitionBeingEdited, "propertyDefinitionBeingEdited"); //$NON-NLS-1$
        this.originalProperty = propertyDefinitionBeingEdited;

        // create copy of property being edited
        this.propertyBeingEdited = PropertyDefinition.copy(this.originalProperty, ownerProvider);

        // remove name from existing names so that validation won't show it as a duplicate
        if (!Utils.isEmpty(this.propertyBeingEdited.getName())) {
            this.existingPropertyNames.remove(this.propertyBeingEdited.getQualifiedName());
        }
    }

    PropertyDefinition accessModel() {
        return this.propertyBeingEdited;
    }

    Collection<String> accessExistingNamespacePrefixes() {
        return this.existingNamespacePrefixes;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(CndMessages.propertyDialogTitle);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( final Composite parent,
                                   final int id,
                                   final String label,
                                   final boolean defaultButton ) {
        final Button btn = super.createButton(parent, id, label, defaultButton);

        if (id == IDialogConstants.OK_ID) {
            // disable OK button initially
            this.btnOk = btn;
            btn.setEnabled(false);
        }

        return btn;
    }

    void createDefaultValuesActions() {
        this.addDefaultValue = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleAddDefaultValue();
            }
        };
        this.addDefaultValue.setToolTipText(CndMessages.addDefaultValueToolTip);
        this.addDefaultValue.setImageDescriptor(JcrUiUtils.getNewImageDescriptor());

        this.deleteDefaultValue = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleDeleteDefaultValue();
            }
        };
        this.deleteDefaultValue.setEnabled(false);
        this.deleteDefaultValue.setToolTipText(CndMessages.deleteDefaultValueToolTip);
        this.deleteDefaultValue.setImageDescriptor(JcrUiUtils.getDeleteImageDescriptor());

        this.editDefaultValue = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleEditDefaultValue();
            }
        };
        this.editDefaultValue.setEnabled(false);
        this.editDefaultValue.setToolTipText(CndMessages.editDefaultValueToolTip);
        this.editDefaultValue.setImageDescriptor(JcrUiUtils.getEditImageDescriptor());
    }

    private void createDefaultValuesViewer( final Table defaultValuesTable ) {
        this.defaultValuesViewer = new TableViewer(defaultValuesTable);
        this.defaultValuesViewer.setLabelProvider(new LabelProvider());
        this.defaultValuesViewer.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( final Object inputElement ) {
                final PropertyDefinition propDefn = accessModel();
                return propDefn.getDefaultValuesAsStrings().toArray();
            }

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
                // nothing to do
            }
        });

        this.defaultValuesViewer.addDoubleClickListener(new IDoubleClickListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
             */
            @Override
            public void doubleClick( final DoubleClickEvent event ) {
                handleEditDefaultValue();
            }
        });

        this.defaultValuesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                handleDefaultValueSelected();
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( final IManagedForm managedForm ) {
        this.scrolledForm = managedForm.getForm();
        this.scrolledForm.setText(isEditMode() ? CndMessages.propertyDialogEditTitle : CndMessages.propertyDialogCreateTitle);
        this.scrolledForm.setImage(Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR));
        this.scrolledForm.setMessage(CndMessages.propertyDialogMsg, IMessageProvider.NONE);

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        final Composite body = this.scrolledForm.getBody();
        body.setLayout(new GridLayout(2, false));

        { // top (name editor)
            final Composite topContainer = toolkit.createComposite(body);
            topContainer.setLayout(new GridLayout(2, false));
            topContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            ((GridData)topContainer.getLayoutData()).horizontalSpan = 2;
            toolkit.paintBordersFor(topContainer);

            { // name
                this.nameEditor = new QualifiedNameEditor(topContainer, SWT.NONE, toolkit, Messages.propertyDefinitionName,
                                                          this.existingNamespacePrefixes,
                                                          this.propertyBeingEdited.getQualifiedName());
                ((GridData)this.nameEditor.getLayoutData()).horizontalSpan = 2;
                this.nameEditor.setAllowsResidualName(true);
                this.nameEditor.addListener(SWT.Modify, new Listener() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
                     */
                    @Override
                    public void handleEvent( final Event e ) {
                        handleNameChanged(e.text);
                    }
                });

                this.nameError.setControl(this.nameEditor);
            }

            { // type
                final Label lblType = toolkit.createLabel(topContainer, CndMessages.typeLabel, SWT.NONE);
                lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

                final CCombo cbxType = new CCombo(topContainer, Styles.COMBO_STYLE);
                toolkit.adapt(cbxType, true, false);
                cbxType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                ((GridData)cbxType.getLayoutData()).heightHint = cbxType.getItemHeight() + 4;
                cbxType.setToolTipText(CndMessages.propertyTypeToolTip);

                // populate property type values
                for (final PropertyType type : PropertyType.values()) {
                    if (type != PropertyType.VARIANT) {
                        cbxType.add(type.toString());
                    }
                }

                // select the current type
                if (isEditMode()) {
                    final String currentType = this.propertyBeingEdited.getType().toString();
                    final int index = cbxType.indexOf(currentType);

                    if (index == -1) {
                        // select default type
                        cbxType.setText(PropertyType.DEFAULT_VALUE.toString());
                    } else {
                        cbxType.select(index);
                    }
                } else {
                    // select default type
                    cbxType.setText(PropertyType.DEFAULT_VALUE.toString());
                }

                cbxType.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        final String newOpv = ((CCombo)e.widget).getText();
                        handleTypeChanged(newOpv);
                    }
                });
            }
        }

        { // middle (attributes)
            final Group attributesContainer = new Group(body, SWT.SHADOW_NONE);
            attributesContainer.setText(CndMessages.attributesHeaderText);
            attributesContainer.setLayout(new GridLayout(2, true));
            final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd.horizontalSpan = 2;
            attributesContainer.setLayoutData(gd);
            toolkit.adapt(attributesContainer);
            toolkit.paintBordersFor(attributesContainer);

            { // autocreated
                final Button btnAutocreated = toolkit.createButton(attributesContainer,
                                                                   CndMessages.autocreatedAttribute,
                                                                   SWT.CHECK);
                btnAutocreated.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.propertyBeingEdited.isAutoCreated()) {
                    btnAutocreated.setSelection(true);
                }

                btnAutocreated.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleAutocreatedChanged(((Button)e.widget).getSelection());
                    }
                });
                btnAutocreated.setToolTipText(CndMessages.autocreatedAttributeToolTip);
            }

            { // mandatory
                final Button btnMandatory = toolkit.createButton(attributesContainer, CndMessages.mandatoryAttribute, SWT.CHECK);
                btnMandatory.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.propertyBeingEdited.isMandatory()) {
                    btnMandatory.setSelection(true);
                }

                btnMandatory.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleMandatoryChanged(((Button)e.widget).getSelection());
                    }
                });
                btnMandatory.setToolTipText(CndMessages.mandatoryAttributeToolTip);
            }

            { // protected
                final Button btnProtected = toolkit.createButton(attributesContainer, CndMessages.protectedAttribute, SWT.CHECK);
                btnProtected.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.propertyBeingEdited.isProtected()) {
                    btnProtected.setSelection(true);
                }

                btnProtected.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleProtectedChanged(((Button)e.widget).getSelection());
                    }
                });
                btnProtected.setToolTipText(CndMessages.protectedAttributeToolTip);
            }

            { // multiple
                final Button btnMultiple = toolkit.createButton(attributesContainer, CndMessages.multipleAttribute, SWT.CHECK);
                btnMultiple.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.propertyBeingEdited.isMultiple()) {
                    btnMultiple.setSelection(true);
                }

                btnMultiple.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleMultipleChanged(((Button)e.widget).getSelection());
                    }
                });
                btnMultiple.setToolTipText(CndMessages.multipleAttributeToolTip);
            }

            { // nofulltext
                final Button btnNoFullText = toolkit.createButton(attributesContainer, CndMessages.noFullTextAttribute, SWT.CHECK);
                btnNoFullText.setBackground(attributesContainer.getBackground());

                if (isEditMode() && !this.propertyBeingEdited.isFullTextSearchable()) {
                    btnNoFullText.setSelection(true);
                }

                btnNoFullText.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleNoFullTextChanged(((Button)e.widget).getSelection());
                    }
                });
                btnNoFullText.setToolTipText(CndMessages.noFullTextAttributeToolTip);
            }

            { // noQueryOrder
                final Button btnNoQueryOrder = toolkit.createButton(attributesContainer,
                                                                    CndMessages.noQueryOrderAttribute,
                                                                    SWT.CHECK);
                btnNoQueryOrder.setBackground(attributesContainer.getBackground());

                if (isEditMode() && !this.propertyBeingEdited.isQueryOrderable()) {
                    btnNoQueryOrder.setSelection(true);
                }

                btnNoQueryOrder.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleNoQueryOrderChanged(((Button)e.widget).getSelection());
                    }
                });
                btnNoQueryOrder.setToolTipText(CndMessages.noQueryOrderAttributeToolTip);
            }

            { // opv
                final Composite opvContainer = toolkit.createComposite(attributesContainer);
                opvContainer.setLayout(new GridLayout(2, false));
                ((GridLayout)opvContainer.getLayout()).marginHeight = 0;
                ((GridLayout)opvContainer.getLayout()).marginWidth = 0;
                opvContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
                ((GridData)opvContainer.getLayoutData()).horizontalSpan = 2;
                toolkit.paintBordersFor(opvContainer);

                final Label lblOpv = toolkit.createLabel(opvContainer, CndMessages.onParentVersionLabel, SWT.NONE);
                lblOpv.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

                final CCombo cbxOpvs = new CCombo(opvContainer, Styles.COMBO_STYLE);
                cbxOpvs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
                ((GridData)cbxOpvs.getLayoutData()).heightHint = cbxOpvs.getItemHeight() + 4;
                cbxOpvs.setToolTipText(CndMessages.onParentVersionToolTip);
                toolkit.adapt(cbxOpvs, true, false);

                // populate opv values
                for (final OnParentVersion opv : OnParentVersion.values()) {
                    if (opv != OnParentVersion.VARIANT) {
                        cbxOpvs.add(opv.toString());
                    }
                }

                // select the current qualifier
                if (isEditMode()) {
                    final String currentOpv = OnParentVersion.findUsingJcrValue(this.propertyBeingEdited.getOnParentVersion()).toString();
                    final int index = cbxOpvs.indexOf(currentOpv);

                    if (index != -1) {
                        cbxOpvs.select(index);
                    }
                }

                cbxOpvs.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        final String newOpv = ((CCombo)e.widget).getText();
                        handleOnParentVersionChanged(newOpv);
                    }
                });
            }

            if (this.nodeTypeQueryable) {
                { // queryOps
                    final Collection<String> supportedQueryOps = Arrays.asList(this.propertyBeingEdited.getAvailableQueryOperators());
                    final Composite queryOpsContainer = toolkit.createComposite(attributesContainer);
                    queryOpsContainer.setLayout(new GridLayout(7, false));
                    ((GridLayout)queryOpsContainer.getLayout()).marginHeight = 0;
                    ((GridLayout)queryOpsContainer.getLayout()).marginWidth = 0;
                    queryOpsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                    toolkit.paintBordersFor(queryOpsContainer);
                    toolkit.createLabel(queryOpsContainer, CndMessages.queryOperatorsLabel);

                    { // equals query operator
                        final Button btnEquals = toolkit.createButton(queryOpsContainer,
                                                                      QueryOperator.EQUALS.toString(),
                                                                      SWT.CHECK);
                        btnEquals.setBackground(attributesContainer.getBackground());

                        if (supportedQueryOps.contains(btnEquals.getText())) {
                            btnEquals.setSelection(true);
                        }

                        btnEquals.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                             */
                            @Override
                            public void widgetSelected( final SelectionEvent e ) {
                                final Button btn = (Button)e.widget;
                                handleQueryOperatorChanged(btn.getText(), btn.getSelection());
                            }
                        });
                        btnEquals.setToolTipText(CndMessages.equalsOperatorToolTip);
                    }

                    { // not equals query operator
                        final Button btnNotEquals = toolkit.createButton(queryOpsContainer,
                                                                         QueryOperator.NOT_EQUALS.toString(),
                                                                         SWT.CHECK);
                        btnNotEquals.setBackground(attributesContainer.getBackground());

                        if (supportedQueryOps.contains(btnNotEquals.getText())) {
                            btnNotEquals.setSelection(true);
                        }

                        btnNotEquals.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                             */
                            @Override
                            public void widgetSelected( final SelectionEvent e ) {
                                final Button btn = (Button)e.widget;
                                handleQueryOperatorChanged(btn.getText(), btn.getSelection());
                            }
                        });
                        btnNotEquals.setToolTipText(CndMessages.notEqualsOperatorToolTip);
                    }

                    { // greater than query operator
                        final Button btnGreaterThan = toolkit.createButton(queryOpsContainer,
                                                                           QueryOperator.GREATER_THAN.toString(),
                                                                           SWT.CHECK);
                        btnGreaterThan.setBackground(attributesContainer.getBackground());

                        if (supportedQueryOps.contains(btnGreaterThan.getText())) {
                            btnGreaterThan.setSelection(true);
                        }

                        btnGreaterThan.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                             */
                            @Override
                            public void widgetSelected( final SelectionEvent e ) {
                                final Button btn = (Button)e.widget;
                                handleQueryOperatorChanged(btn.getText(), btn.getSelection());
                            }
                        });
                        btnGreaterThan.setToolTipText(CndMessages.greaterThanOperatorToolTip);
                    }

                    { // less than query operator
                        final Button btnLessThan = toolkit.createButton(queryOpsContainer,
                                                                        QueryOperator.LESS_THAN.toString(),
                                                                        SWT.CHECK);
                        btnLessThan.setBackground(attributesContainer.getBackground());

                        if (supportedQueryOps.contains(btnLessThan.getText())) {
                            btnLessThan.setSelection(true);
                        }

                        btnLessThan.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                             */
                            @Override
                            public void widgetSelected( final SelectionEvent e ) {
                                final Button btn = (Button)e.widget;
                                handleQueryOperatorChanged(btn.getText(), btn.getSelection());
                            }
                        });
                        btnLessThan.setToolTipText(CndMessages.lessThanOperatorToolTip);
                    }

                    { // greater than or equal query operator
                        final Button btnGreaterThanEquals = toolkit.createButton(queryOpsContainer,
                                                                                 QueryOperator.GREATER_THAN_EQUALS.toString(),
                                                                                 SWT.CHECK);
                        btnGreaterThanEquals.setBackground(attributesContainer.getBackground());

                        if (supportedQueryOps.contains(btnGreaterThanEquals.getText())) {
                            btnGreaterThanEquals.setSelection(true);
                        }

                        btnGreaterThanEquals.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                             */
                            @Override
                            public void widgetSelected( final SelectionEvent e ) {
                                final Button btn = (Button)e.widget;
                                handleQueryOperatorChanged(btn.getText(), btn.getSelection());
                            }
                        });
                        btnGreaterThanEquals.setToolTipText(CndMessages.greaterThanEqualsOperatorToolTip);
                    }

                    { // less than or equals query operator
                        final Button btnLessThanEquals = toolkit.createButton(queryOpsContainer,
                                                                              QueryOperator.LESS_THAN_EQUALS.toString(),
                                                                              SWT.CHECK);
                        btnLessThanEquals.setBackground(attributesContainer.getBackground());

                        if (supportedQueryOps.contains(btnLessThanEquals.getText())) {
                            btnLessThanEquals.setSelection(true);
                        }

                        btnLessThanEquals.addSelectionListener(new SelectionAdapter() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                             */
                            @Override
                            public void widgetSelected( final SelectionEvent e ) {
                                final Button btn = (Button)e.widget;
                                handleQueryOperatorChanged(btn.getText(), btn.getSelection());
                            }
                        });
                        btnLessThanEquals.setToolTipText(CndMessages.lessThanEqualsOperatorToolTip);
                    }
                }
            }
        }

        { // bottom (default values, value constraints, comments)
            final Composite bottomContainer = toolkit.createComposite(body);
            bottomContainer.setLayout(new GridLayout(2, false));
            bottomContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            toolkit.paintBordersFor(bottomContainer);

            { // default values
                final Composite defaultValuesContainer = toolkit.createComposite(bottomContainer);
                defaultValuesContainer.setLayout(new GridLayout(2, false));
                defaultValuesContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                toolkit.paintBordersFor(defaultValuesContainer);

                final Label label = toolkit.createLabel(defaultValuesContainer, CndMessages.defaultValuesLabel);
                label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

                createDefaultValuesActions();

                // add toolbar buttons (add, edit, delete)
                final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
                final ToolBar toolBar = toolBarManager.createControl(defaultValuesContainer);
                toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
                toolkit.adapt(toolBar);

                final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
                toolBar.setCursor(handCursor);
                toolBarManager.add(this.addDefaultValue);
                toolBarManager.add(this.editDefaultValue);
                toolBarManager.add(this.deleteDefaultValue);
                toolBarManager.update(true);

                final Table table = FormUtils.createTable(toolkit, defaultValuesContainer);
                table.setHeaderVisible(false);
                table.setLinesVisible(false);
                GridData gd = (GridData)table.getLayoutData();
                gd.heightHint = table.getItemHeight() * 4;
                gd.horizontalSpan = 2;
                gd.widthHint = UiUtils.convertWidthInCharsToPixels(table, 40);
                table.setToolTipText(CndMessages.defaultValuesToolTip);
                this.defaultValuesError.setControl(table);

                // table context menu
                final MenuManager menuManager = new MenuManager();
                menuManager.add(new DelegateAction(CndMessages.addDefaultValueMenuText, this.addDefaultValue));
                menuManager.add(new DelegateAction(CndMessages.editDefaultValueMenuText, this.editDefaultValue));
                menuManager.add(new DelegateAction(CndMessages.deleteDefaultValueMenuText, this.deleteDefaultValue));
                table.setMenu(menuManager.createContextMenu(table));

                createDefaultValuesViewer(table);

                // fill with data
                this.defaultValuesViewer.setInput(this);
            }

            { // value constraints
                final Composite valueConstraintsContainer = toolkit.createComposite(bottomContainer);
                valueConstraintsContainer.setLayout(new GridLayout(2, false));
                valueConstraintsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                toolkit.paintBordersFor(valueConstraintsContainer);

                final Label label = toolkit.createLabel(valueConstraintsContainer, CndMessages.valueConstraintsLabel);
                label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

                createValueConstraintsActions();

                // add toolbar buttons (add, edit, delete)
                final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
                final ToolBar toolBar = toolBarManager.createControl(valueConstraintsContainer);
                toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
                toolkit.adapt(toolBar);

                final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
                toolBar.setCursor(handCursor);
                toolBarManager.add(this.addValueConstraint);
                toolBarManager.add(this.editValueConstraint);
                toolBarManager.add(this.deleteValueConstraint);
                toolBarManager.update(true);

                final Table table = FormUtils.createTable(toolkit, valueConstraintsContainer);
                table.setHeaderVisible(false);
                table.setLinesVisible(false);
                GridData gd = (GridData)table.getLayoutData();
                gd.heightHint = table.getItemHeight() * 4;
                gd.horizontalSpan = 2;
                gd.widthHint = UiUtils.convertWidthInCharsToPixels(table, 40);
                table.setToolTipText(CndMessages.valueConstraintsToolTip);
                this.valueConstraintsError.setControl(table);

                // table context menu
                final MenuManager menuManager = new MenuManager();
                menuManager.add(new DelegateAction(CndMessages.addValueConstraintMenuText, this.addValueConstraint));
                menuManager.add(new DelegateAction(CndMessages.editValueConstraintMenuText, this.editValueConstraint));
                menuManager.add(new DelegateAction(CndMessages.deleteValueConstraintMenuText, this.deleteValueConstraint));
                table.setMenu(menuManager.createContextMenu(table));

                createValueConstraintsViewer(table);

                // fill with data
                this.valueConstraintsViewer.setInput(this);
            }

            { // comments
                final Composite commentsContainer = toolkit.createComposite(bottomContainer);
                commentsContainer.setLayout(new GridLayout(2, false));
                commentsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                ((GridData)commentsContainer.getLayoutData()).horizontalSpan = 2;
                toolkit.paintBordersFor(commentsContainer);

                final Label lblComment = toolkit.createLabel(commentsContainer, CndMessages.commentLabel, SWT.NONE);
                lblComment.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

                Text txtComment = toolkit.createText(commentsContainer, null, Styles.TEXT_STYLE | SWT.MULTI | SWT.H_SCROLL
                                                                              | SWT.V_SCROLL);
                txtComment.setToolTipText(CndMessages.commentedToolTip);

                final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
                gd.verticalIndent += ((GridLayout)body.getLayout()).verticalSpacing;
                gd.heightHint = txtComment.getLineHeight() * 3;
                gd.widthHint = UiUtils.convertWidthInCharsToPixels(txtComment, 80);
                txtComment.setLayoutData(gd);

                if (isEditMode() && !Utils.isEmpty(this.propertyBeingEdited.getComment())) {
                    txtComment.setText(CommentedCndElement.Helper.removeCommentCharacters(this.propertyBeingEdited.getComment()));
                }

                txtComment.addModifyListener(new ModifyListener() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                     */
                    @Override
                    public void modifyText( final ModifyEvent e ) {
                        handleCommentChanged(((Text)e.widget).getText());
                    }
                });
            }
        }

        // must be done after constructor
        this.propertyBeingEdited.addListener(new PropertyChangeListener() {

            /**
             * {@inheritDoc}
             *
             * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
             */
            @Override
            public void propertyChange( final PropertyChangeEvent e ) {
                handlePropertyChanged(e);
            }
        });

        // set messages
        validateAttributes();
        validateName();
        validateDefaultValues();
        validateValueConstraints();
        validateType();
    }

    void createValueConstraintsActions() {
        this.addValueConstraint = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleAddValueConstraint();
            }
        };
        this.addValueConstraint.setToolTipText(CndMessages.addValueConstraintToolTip);
        this.addValueConstraint.setImageDescriptor(JcrUiUtils.getNewImageDescriptor());

        this.deleteValueConstraint = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleDeleteValueConstraint();
            }
        };
        this.deleteValueConstraint.setEnabled(false);
        this.deleteValueConstraint.setToolTipText(CndMessages.deleteValueConstraintToolTip);
        this.deleteValueConstraint.setImageDescriptor(JcrUiUtils.getDeleteImageDescriptor());

        this.editValueConstraint = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleEditValueConstraint();
            }
        };
        this.editValueConstraint.setEnabled(false);
        this.editValueConstraint.setToolTipText(CndMessages.editValueConstraintToolTip);
        this.editValueConstraint.setImageDescriptor(JcrUiUtils.getEditImageDescriptor());
    }

    private void createValueConstraintsViewer( final Table valueConstraintsTable ) {
        this.valueConstraintsViewer = new TableViewer(valueConstraintsTable);
        this.valueConstraintsViewer.setLabelProvider(new LabelProvider());
        this.valueConstraintsViewer.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( final Object inputElement ) {
                final PropertyDefinition propDefn = accessModel();
                final String[] valueConstraints = propDefn.getValueConstraints();

                if (valueConstraints == null) {
                    return Utils.EMPTY_OBJECT_ARRAY;
                }

                return valueConstraints;
            }

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( final Viewer viewer,
                                      final Object oldInput,
                                      final Object newInput ) {
                // nothing to do
            }
        });

        this.valueConstraintsViewer.addDoubleClickListener(new IDoubleClickListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
             */
            @Override
            public void doubleClick( final DoubleClickEvent event ) {
                handleEditValueConstraint();
            }
        });

        this.valueConstraintsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                handleValueConstraintSelected();
            }
        });
    }

    /**
     * <strong>Should only be called after the dialog's <code>OK</code> button has been selected.</strong>
     *
     * @return the property definition represented by the dialog UI controls (never <code>null</code>)
     */
    public PropertyDefinition getPropertyDefinition() {
        return this.propertyBeingEdited;
    }

    String getSelectedDefaultValue() {
        final IStructuredSelection selection = (IStructuredSelection)this.defaultValuesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        assert (selection.size() == 1) : "default values viewer should not allow multiple selections"; //$NON-NLS-1$
        assert (selection.getFirstElement() instanceof String) : "selection was not a string"; //$NON-NLS-1$
        return (String)selection.getFirstElement();
    }

    String getSelectedValueConstraint() {
        final IStructuredSelection selection = (IStructuredSelection)this.valueConstraintsViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        assert (selection.size() == 1) : "value constraints viewer should not allow multiple selections"; //$NON-NLS-1$
        assert (selection.getFirstElement() instanceof String) : "selection was not a string"; //$NON-NLS-1$
        return (String)selection.getFirstElement();
    }

    void handleAddDefaultValue() {
        final PropertyDefinition propDefn = getPropertyDefinition();
        final Collection<String> currentDefaultValues = new ArrayList<String>(propDefn.getDefaultValuesAsStrings());
        final StringValueEditorDialog dialog = new StringValueEditorDialog(getShell()) {
            /**
             * {@inheritDoc}
             *
             * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog#getSettings()
             */
            @Override
            protected Settings getSettings() {
                final StringValueEditorDialog.Settings settings = new StringValueEditorDialog.Settings();
                settings.acceptMessage = CndMessages.acceptDefaultValueDialogMsg;
                settings.initialMessage = CndMessages.defaultValueDialogMsg;
                settings.labelText = CndMessages.defaultValueLabel;
                settings.messageAreaTitle = CndMessages.defaultValueDialogCreateMsg;
                settings.valueToolTip = CndMessages.defaultValueToolTip;
                settings.validator = new Validator() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog.Validator#validate(java.lang.String)
                     */
                    @Override
                    public ValidationStatus validate( final String newValue ) {
                        final ValidationStatus status = CndValidator.isValid(newValue,
                                                                             propDefn.getType(),
                                                                             propDefn.getName(),
                                                                             accessExistingNamespacePrefixes());

                        if (status.isError()) {
                            return status;
                        }

                        // check for duplicate
                        currentDefaultValues.add(newValue);
                        MultiValidationStatus validationStatus = CndValidator.validateDefaultValues(propDefn.getName(),
                                                                                                    propDefn.getType(),
                                                                                                    currentDefaultValues,
                                                                                                    accessExistingNamespacePrefixes());
                        currentDefaultValues.remove(newValue);
                        return validationStatus;
                    }
                };

                return settings;
            }
        };

        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            final String newDefaultValue = dialog.getValue();

            if (!this.propertyBeingEdited.addDefaultValue(newDefaultValue)) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorAddingDefaultValue, newDefaultValue));
            }
        }
    }

    void handleAddValueConstraint() {
        final PropertyDefinition propDefn = getPropertyDefinition();
        final Collection<String> currentConstraints = new ArrayList<String>(Arrays.asList(propDefn.getValueConstraints()));
        final StringValueEditorDialog dialog = new StringValueEditorDialog(getShell()) {

            /**
             * {@inheritDoc}
             *
             * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog#getSettings()
             */
            @Override
            protected Settings getSettings() {
                final StringValueEditorDialog.Settings settings = new StringValueEditorDialog.Settings();
                settings.acceptMessage = CndMessages.acceptValueConstraintDialogMsg;
                settings.initialMessage = CndMessages.valueConstraintDialogMsg;
                settings.labelText = CndMessages.valueConstraintLabel;
                settings.messageAreaTitle = CndMessages.valueConstraintDialogCreateMsg;
                settings.valueToolTip = CndMessages.valueConstraintToolTip;
                settings.validator = new Validator() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog.Validator#validate(java.lang.String)
                     */
                    @Override
                    public ValidationStatus validate( final String newValue ) {
                        final ValidationStatus status = CndValidator.validateValueConstraint(newValue);

                        if (status.isError()) {
                            return status;
                        }

                        // check for duplicate
                        currentConstraints.add(newValue);
                        MultiValidationStatus validationStatus = CndValidator.validateValueConstraints(propDefn.getName(),
                                                                                                       currentConstraints);
                        currentConstraints.remove(newValue);
                        return validationStatus;
                    }
                };

                return settings;
            }
        };
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            final String newValueConstraint = dialog.getValue();

            if (!this.propertyBeingEdited.addValueConstraint(newValueConstraint)) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorAddingValueConstraint, newValueConstraint));
            }
        }
    }

    void handleAutocreatedChanged( final boolean newAutocreated ) {
        this.propertyBeingEdited.setAutoCreated(newAutocreated);
    }

    void handleCommentChanged( final String newComment ) {
        this.propertyBeingEdited.setComment(newComment);
    }

    void handleDefaultValueSelected() {
        // update button enablements
        final boolean enable = (getSelectedDefaultValue() != null);

        if (this.editDefaultValue.isEnabled() != enable) {
            this.editDefaultValue.setEnabled(enable);
        }

        if (this.deleteDefaultValue.isEnabled() != enable) {
            this.deleteDefaultValue.setEnabled(enable);
        }
    }

    void handleDeleteDefaultValue() {
        assert (getSelectedDefaultValue() != null) : "Delete default value handler called and there is no default value selected"; //$NON-NLS-1$
        final String defaultValue = getSelectedDefaultValue();

        // show confirmation dialog
        if (MessageFormDialog.openQuestion(getShell(),
                                           CndMessages.deleteDefaultValueDialogTitle,
                                           JcrUiUtils.getCndEditorImage(),
                                           NLS.bind(CndMessages.deleteDefaultValueDialogMessage, defaultValue))) {
            if (!this.propertyBeingEdited.removeDefaultValue(defaultValue)) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorDeletingDefaultValue, defaultValue));
            }
        }
    }

    void handleDeleteValueConstraint() {
        assert (getSelectedValueConstraint() != null) : "Delete value constraint handler called and there is no value constraint selected"; //$NON-NLS-1$
        final String valueConstraint = getSelectedValueConstraint();

        // show confirmation dialog
        if (MessageFormDialog.openQuestion(getShell(),
                                           CndMessages.deleteValueConstraintDialogTitle,
                                           JcrUiUtils.getCndEditorImage(),
                                           NLS.bind(CndMessages.deleteValueConstraintDialogMessage, valueConstraint))) {
            if (!this.propertyBeingEdited.removeValueConstraint(valueConstraint)) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorDeletingValueConstraint, valueConstraint));
            }
        }
    }

    void handleEditDefaultValue() {
        assert (getSelectedDefaultValue() != null) : "Edit default value handler called but there is not a default value selected"; //$NON-NLS-1$

        final PropertyDefinition propDefn = getPropertyDefinition();
        final String selectedDefaultValue = getSelectedDefaultValue();
        final Collection<String> currentDefaultValues = new ArrayList<String>(propDefn.getDefaultValuesAsStrings());
        currentDefaultValues.remove(selectedDefaultValue); // remove selected so validation doesn't flag it as a duplicate

        final StringValueEditorDialog dialog = new StringValueEditorDialog(getShell()) {
            /**
             * {@inheritDoc}
             *
             * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog#getSettings()
             */
            @Override
            protected Settings getSettings() {
                final StringValueEditorDialog.Settings settings = new StringValueEditorDialog.Settings();
                settings.acceptMessage = CndMessages.acceptDefaultValueDialogMsg;
                settings.initialMessage = CndMessages.defaultValueDialogMsg;
                settings.labelText = CndMessages.defaultValueLabel;
                settings.messageAreaTitle = CndMessages.defaultValueDialogEditMsg;
                settings.valueToolTip = CndMessages.defaultValueToolTip;
                settings.valueBeingEdited = selectedDefaultValue;
                settings.validator = new Validator() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog.Validator#validate(java.lang.String)
                     */
                    @Override
                    public ValidationStatus validate( final String newValue ) {
                        final ValidationStatus status = CndValidator.isValid(newValue,
                                                                             propDefn.getType(),
                                                                             propDefn.getName(),
                                                                             accessExistingNamespacePrefixes());

                        if (status.isError()) {
                            return status;
                        }

                        // check for duplicate
                        currentDefaultValues.add(newValue);
                        MultiValidationStatus validationStatus = CndValidator.validateDefaultValues(propDefn.getName(),
                                                                                                    propDefn.getType(),
                                                                                                    currentDefaultValues,
                                                                                                    accessExistingNamespacePrefixes());
                        currentDefaultValues.remove(newValue);
                        return validationStatus;
                    }
                };

                return settings;
            }
        };

        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            final String modifiedDefaultValue = dialog.getValue();
            boolean removed = false;
            boolean added = false;

            // remove existing and add in new
            if (this.propertyBeingEdited.removeDefaultValue(selectedDefaultValue)) {
                removed = true;

                if (this.propertyBeingEdited.addDefaultValue(modifiedDefaultValue)) {
                    added = true;
                }
            }

            if (!removed || !added) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorEditingDefaultValue, new Object[] {modifiedDefaultValue,
                                                removed, added}));
            }
        }
    }

    void handleEditValueConstraint() {
        assert (getSelectedValueConstraint() != null) : "Edit value constraint handler called but there is not a value constraint selected"; //$NON-NLS-1$

        final PropertyDefinition propDefn = getPropertyDefinition();
        final String selectedValueConstraint = getSelectedValueConstraint();
        final Collection<String> currentConstraints = new ArrayList<String>(Arrays.asList(propDefn.getValueConstraints()));
        currentConstraints.remove(selectedValueConstraint); // remove selected so validation doesn't flag it as a duplicate

        final StringValueEditorDialog dialog = new StringValueEditorDialog(getShell()) {

            /**
             * {@inheritDoc}
             *
             * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog#getSettings()
             */
            @Override
            protected Settings getSettings() {
                final StringValueEditorDialog.Settings settings = new StringValueEditorDialog.Settings();
                settings.acceptMessage = CndMessages.acceptValueConstraintDialogMsg;
                settings.initialMessage = CndMessages.valueConstraintDialogMsg;
                settings.labelText = CndMessages.valueConstraintLabel;
                settings.messageAreaTitle = CndMessages.valueConstraintDialogEditMsg;
                settings.valueToolTip = CndMessages.valueConstraintToolTip;
                settings.valueBeingEdited = selectedValueConstraint;
                settings.validator = new Validator() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.jboss.tools.modeshape.jcr.ui.cnd.StringValueEditorDialog.Validator#validate(java.lang.String)
                     */
                    @Override
                    public ValidationStatus validate( final String newValue ) {
                        final ValidationStatus status = CndValidator.validateValueConstraint(newValue);

                        if (status.isError()) {
                            return status;
                        }

                        // check for duplicate
                        currentConstraints.add(newValue);
                        MultiValidationStatus validationStatus = CndValidator.validateValueConstraints(propDefn.getName(),
                                                                                                       currentConstraints);
                        currentConstraints.remove(newValue);
                        return validationStatus;
                    }
                };

                return settings;
            }
        };
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            final String modifiedValueConstraint = dialog.getValue();
            boolean removed = false;
            boolean added = false;

            // remove existing and add in new
            if (this.propertyBeingEdited.removeValueConstraint(selectedValueConstraint)) {
                removed = true;

                if (this.propertyBeingEdited.addValueConstraint(modifiedValueConstraint)) {
                    added = true;
                }
            }

            if (!removed || !added) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorEditingValueConstraint, new Object[] {
                                                modifiedValueConstraint, removed, added}));
            }
        }
    }

    void handleMandatoryChanged( final boolean newMandatory ) {
        this.propertyBeingEdited.setMandatory(newMandatory);
    }

    void handleMultipleChanged( final boolean newMultiple ) {
        this.propertyBeingEdited.setMultiple(newMultiple);
    }

    void handleNameChanged( final String newName ) {
        this.propertyBeingEdited.setName(newName);
    }

    void handleNoFullTextChanged( final boolean newNoFullText ) {
        this.propertyBeingEdited.setFullTextSearchable(!newNoFullText);
    }

    void handleNoQueryOrderChanged( final boolean newNoQueryOrder ) {
        this.propertyBeingEdited.setQueryOrderable(!newNoQueryOrder);
    }

    void handleOnParentVersionChanged( final String newOpv ) {
        this.propertyBeingEdited.setOnParentVersion(newOpv);
    }

    void handlePropertyChanged( final PropertyChangeEvent e ) {
        final String propName = e.getPropertyName();

        if (PropertyName.AUTOCREATED.toString().equals(propName) || PropertyName.MANDATORY.toString().equals(propName)
            || PropertyName.MULTIPLE.toString().equals(propName) || PropertyName.NO_FULL_TEXT.toString().equals(propName)
            || PropertyName.NO_QUERY_ORDER.toString().equals(propName) || PropertyName.PROTECTED.toString().equals(propName)
            || PropertyName.ON_PARENT_VERSION.toString().equals(propName) || PropertyName.QUERY_OPS.toString().equals(propName)) {
            validateAttributes();
        } else if (PropertyName.DEFAULT_VALUES.toString().equals(propName)) {
            validateDefaultValues();
            this.defaultValuesViewer.refresh();
        } else if (PropertyName.NAME.toString().equals(propName)) {
            validateName();
        } else if (PropertyName.TYPE.toString().equals(propName)) {
            validateType();
        } else if (PropertyName.VALUE_CONSTRAINTS.toString().equals(propName)) {
            validateValueConstraints();
            this.valueConstraintsViewer.refresh();
        }

        updateState();
    }

    void handleProtectedChanged( final boolean newProtected ) {
        this.propertyBeingEdited.setProtected(newProtected);
    }

    /**
     * @param text
     * @param selection
     */
    protected void handleQueryOperatorChanged( final String text,
                                               final boolean selection ) {
        if (selection) {
            this.propertyBeingEdited.addQueryOperator(QueryOperator.find(text));
        } else {
            this.propertyBeingEdited.removeQueryOperator(QueryOperator.find(text));
        }
    }

    void handleTypeChanged( final String newType ) {
        this.propertyBeingEdited.setType(PropertyType.valueOf(newType));
    }

    void handleValueConstraintSelected() {
        // update button enablements
        final boolean enable = (getSelectedValueConstraint() != null);

        if (this.editValueConstraint.isEnabled() != enable) {
            this.editValueConstraint.setEnabled(enable);
        }

        if (this.deleteValueConstraint.isEnabled() != enable) {
            this.deleteValueConstraint.setEnabled(enable);
        }
    }

    private boolean isEditMode() {
        return (this.originalProperty != null);
    }

    private void updateMessage( final ValidationStatus status,
                                final ErrorMessage errorMsg ) {
        JcrUiUtils.setMessage(status, errorMsg);

        if (errorMsg.isOk()) {
            this.scrolledForm.getMessageManager().removeMessage(errorMsg.getKey(), errorMsg.getControl());
        } else {
            this.scrolledForm.getMessageManager().addMessage(errorMsg.getKey(),
                                                             errorMsg.getMessage(),
                                                             null,
                                                             errorMsg.getMessageType(),
                                                             errorMsg.getControl());
        }
    }

    private void updateState() {
        final int messageType = this.scrolledForm.getMessageType();
        boolean enable = (messageType != IMessageProvider.ERROR);

        if (enable && isEditMode() && this.originalProperty.equals(this.propertyBeingEdited)) {
            enable = false;
        }

        // set enabled state of OK button
        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }

    private void validateAttributes() {
        // nothing to do
    }

    private void validateDefaultValues() {
        updateMessage(CndValidator.validateDefaultValues(this.propertyBeingEdited, this.existingNamespacePrefixes),
                      this.defaultValuesError);
    }

    private void validateName() {
        updateMessage(this.nameEditor.getStatus(), this.nameError);
    }

    private void validateType() {
        // no validation required since user cannot enter an invalid value
    }

    private void validateValueConstraints() {
        updateMessage(CndValidator.validateValueConstraints(this.propertyBeingEdited), this.valueConstraintsError);
    }
}
