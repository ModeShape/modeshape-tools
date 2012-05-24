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
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition.PropertyName;
import org.jboss.tools.modeshape.jcr.ItemOwnerProvider;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.attributes.OnParentVersion;
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
 * A dialog used to create and edit {@link ChildNodeDefinition}s.
 */
final class ChildNodeDialog extends FormDialog {

    private IAction addRequiredType;
    private Button btnOk;
    private CCombo cbxDefaultType;
    private ChildNodeDefinition childNodeBeingEdited;
    private final ErrorMessage defaultTypeError;
    private IAction deleteRequiredType;
    private IAction editRequiredType;
    /**
     * The existing child node definition names contained in the CND (never <code>null</code> but can be empty).
     */
    private final Collection<QualifiedName> existingChildNodeNames;

    /**
     * The existing namespace prefixes contained in the CND (never <code>null</code> but can be empty).
     */
    private final Collection<String> existingNamespacePrefixes;

    private QualifiedNameEditor nameEditor;

    private final ErrorMessage nameError;

    private ChildNodeDefinition originalChildNode;

    private QualifiedNameProposalProvider requiredTypeProposalProvider;

    private final ErrorMessage requiredTypesError;

    private TableViewer requiredTypesViewer;

    private ScrolledForm scrolledForm;

    /**
     * Used to create a new child node definition.
     * 
     * @param parentShell the parent shell (can be <code>null</code>)
     * @param ownerProvider an item owner provider for the new child node (cannot be <code>null</code>)
     * @param existingChildNodeNames the existing child node names (can be <code>null</code> or empty)
     * @param existingNamespacePrefixes the existing CND namespace prefixes (can be <code>null</code> or empty)
     */
    public ChildNodeDialog( final Shell parentShell,
                            final ItemOwnerProvider ownerProvider,
                            final Collection<QualifiedName> existingChildNodeNames,
                            final Collection<String> existingNamespacePrefixes ) {
        super(parentShell);
        Utils.verifyIsNotNull(ownerProvider, "ownerProvider"); //$NON-NLS-1$

        this.existingChildNodeNames = ((existingChildNodeNames == null) ? Collections.<QualifiedName> emptyList()
                                                                       : new ArrayList<QualifiedName>(existingChildNodeNames));
        this.existingNamespacePrefixes = ((existingNamespacePrefixes == null) ? Collections.<String> emptyList()
                                                                             : new ArrayList<String>(existingNamespacePrefixes));
        this.nameError = new ErrorMessage();
        this.defaultTypeError = new ErrorMessage();
        this.requiredTypesError = new ErrorMessage();
        this.childNodeBeingEdited = new ChildNodeDefinition(ownerProvider);
    }

    /**
     * Used to edit a child node definition.
     * 
     * @param parentShell the parent shell (can be <code>null</code>)
     * @param ownerProvider an item owner provider for the new child node (cannot be <code>null</code>)
     * @param existingChildNodeNames the existing child node names (can be <code>null</code> or empty)
     * @param existingNamespacePrefixes the existing CND namespace prefixes (can be <code>null</code> or empty)
     * @param childNodeBeingEdited the child node definition being edited (cannot be <code>null</code>)
     */
    public ChildNodeDialog( final Shell parentShell,
                            final ItemOwnerProvider ownerProvider,
                            final Collection<QualifiedName> existingChildNodeNames,
                            final Collection<String> existingNamespacePrefixes,
                            final ChildNodeDefinition childNodeBeingEdited ) {
        this(parentShell, ownerProvider, existingChildNodeNames, existingNamespacePrefixes);

        Utils.verifyIsNotNull(childNodeBeingEdited, "childNodeBeingEdited"); //$NON-NLS-1$
        this.originalChildNode = childNodeBeingEdited;

        // create copy of child node being edited
        this.childNodeBeingEdited = ChildNodeDefinition.copy(this.originalChildNode, ownerProvider);

        // remove name from existing names so that validation won't show it as a duplicate
        if (!Utils.isEmpty(this.childNodeBeingEdited.getName())) {
            this.existingChildNodeNames.remove(this.childNodeBeingEdited.getQualifiedName());
        }
    }

    ChildNodeDefinition accessModel() {
        return this.childNodeBeingEdited;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(CndMessages.childNodeDialogTitle);
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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( final IManagedForm managedForm ) {
        this.scrolledForm = managedForm.getForm();
        this.scrolledForm.setText(isEditMode() ? CndMessages.childNodeDialogEditTitle : CndMessages.childNodeDialogCreateTitle);
        this.scrolledForm.setImage(Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR));
        this.scrolledForm.setMessage(CndMessages.childNodeDialogMsg, IMessageProvider.NONE);

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        final Composite body = this.scrolledForm.getBody();
        body.setLayout(new GridLayout(2, false));

        { // left-side (name, default type, attributes)
            final Composite leftContainer = toolkit.createComposite(body);
            leftContainer.setLayout(new GridLayout(2, false));
            leftContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            toolkit.paintBordersFor(leftContainer);

            { // name
                this.nameEditor = new QualifiedNameEditor(leftContainer,
                                                          SWT.NONE,
                                                          toolkit,
                                                          Messages.childNodeDefinitionName,
                                                          this.existingNamespacePrefixes,
                                                          this.childNodeBeingEdited.getQualifiedName());
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

            { // default type
                final Composite defaultTypeContainer = toolkit.createComposite(leftContainer);
                defaultTypeContainer.setLayout(new GridLayout(2, false));
                defaultTypeContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                ((GridData)defaultTypeContainer.getLayoutData()).horizontalSpan = 2;
                toolkit.paintBordersFor(defaultTypeContainer);

                final Label lblDefaultType = toolkit.createLabel(defaultTypeContainer, CndMessages.childNodeDefaultTypeLabel,
                                                                 SWT.NONE);
                lblDefaultType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

                this.cbxDefaultType = new CCombo(defaultTypeContainer, Styles.COMBO_STYLE);
                this.cbxDefaultType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                ((GridData)this.cbxDefaultType.getLayoutData()).heightHint = this.cbxDefaultType.getItemHeight() + 4;
                this.cbxDefaultType.setToolTipText(CndMessages.childNodeDefaultTypeToolTip);
                toolkit.adapt(this.cbxDefaultType, true, false);

                this.cbxDefaultType.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        String newDefaultType = ((CCombo)e.widget).getText();

                        if (CndMessages.notAssignedItemChoice.equals(newDefaultType)) {
                            newDefaultType = Utils.EMPTY_STRING;
                        }

                        handleDefaultTypeChanged(newDefaultType);
                    }
                });

                refreshDefaultTypes(); // populate default type choices
                this.defaultTypeError.setControl(this.cbxDefaultType);
            }

            { // attributes
                final Group attributesContainer = new Group(leftContainer, SWT.SHADOW_NONE);
                attributesContainer.setText(CndMessages.attributesHeaderText);
                attributesContainer.setLayout(new GridLayout(2, true));
                final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
                gd.horizontalSpan = 2;
                attributesContainer.setLayoutData(gd);
                toolkit.adapt(attributesContainer);
                toolkit.paintBordersFor(attributesContainer);

                final Button btnAutocreated = toolkit.createButton(attributesContainer, CndMessages.autocreatedAttribute, SWT.CHECK);
                btnAutocreated.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.childNodeBeingEdited.isAutoCreated()) {
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

                final Button btnMandatory = toolkit.createButton(attributesContainer, CndMessages.mandatoryAttribute, SWT.CHECK);
                btnMandatory.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.childNodeBeingEdited.isMandatory()) {
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

                final Button btnProtected = toolkit.createButton(attributesContainer, CndMessages.protectedAttribute, SWT.CHECK);
                btnProtected.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.childNodeBeingEdited.isProtected()) {
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

                final Button btnSameNamedSiblings = toolkit.createButton(attributesContainer,
                                                                         CndMessages.sameNamedSiblingsAttribute, SWT.CHECK);
                btnSameNamedSiblings.setBackground(attributesContainer.getBackground());

                if (isEditMode() && this.childNodeBeingEdited.allowsSameNameSiblings()) {
                    btnSameNamedSiblings.setSelection(true);
                }

                btnSameNamedSiblings.addSelectionListener(new SelectionAdapter() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected( final SelectionEvent e ) {
                        handleSameNamedSiblingsChanged(((Button)e.widget).getSelection());
                    }
                });
                btnSameNamedSiblings.setToolTipText(CndMessages.sameNamedSiblingsAttributeToolTip);

                { // opv
                    final Composite opvContainer = toolkit.createComposite(attributesContainer);
                    opvContainer.setLayout(new GridLayout(2, false));
                    final GridData gdOpv = new GridData(SWT.FILL, SWT.CENTER, true, false);
                    gdOpv.horizontalSpan = 2;
                    opvContainer.setLayoutData(gdOpv);
                    toolkit.paintBordersFor(opvContainer);

                    final Label lblOpv = toolkit.createLabel(opvContainer, CndMessages.onParentVersionLabel, SWT.NONE);
                    lblOpv.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

                    final CCombo cbxOpvs = new CCombo(opvContainer, Styles.COMBO_STYLE);
                    toolkit.adapt(cbxOpvs, true, false);
                    cbxOpvs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
                    ((GridData)cbxOpvs.getLayoutData()).heightHint = cbxOpvs.getItemHeight() + 4;
                    cbxOpvs.setToolTipText(CndMessages.onParentVersionToolTip);

                    // populate opv values
                    for (final OnParentVersion opv : OnParentVersion.values()) {
                        if (opv != OnParentVersion.VARIANT) {
                            cbxOpvs.add(opv.toString());
                        }
                    }

                    // select the current qualifier
                    if (isEditMode()) {
                        final String currentOpv = OnParentVersion.findUsingJcrValue(this.childNodeBeingEdited.getOnParentVersion())
                                                                 .toString();
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
            }
        }

        { // right-side (required types)
            final Composite rightContainer = toolkit.createComposite(body);
            rightContainer.setLayout(new GridLayout(2, false));
            rightContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            toolkit.paintBordersFor(rightContainer);

            final Label label = toolkit.createLabel(rightContainer, CndMessages.requiredTypesLabel);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

            createRequiredTypesActions();

            // add toolbar buttons (add, edit, delete)
            final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
            final ToolBar toolBar = toolBarManager.createControl(rightContainer);
            toolkit.adapt(toolBar);

            final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
            toolBar.setCursor(handCursor);
            toolBarManager.add(this.addRequiredType);
            toolBarManager.add(this.editRequiredType);
            toolBarManager.add(this.deleteRequiredType);
            toolBarManager.update(true);

            final Table table = FormUtils.createTable(toolkit, rightContainer);
            table.setHeaderVisible(false);
            table.setLinesVisible(false);
            GridData gd = (GridData)table.getLayoutData();
            gd.horizontalSpan = 2;
            gd.heightHint = table.getItemHeight() * 4;
            gd.widthHint = UiUtils.convertWidthInCharsToPixels(table, 40);
            this.requiredTypesError.setControl(table);

            // table context menu
            final MenuManager menuManager = new MenuManager();
            menuManager.add(new DelegateAction(CndMessages.addRequiredTypeMenuText, this.addRequiredType));
            menuManager.add(new DelegateAction(CndMessages.editRequiredTypeMenuText, this.editRequiredType));
            menuManager.add(new DelegateAction(CndMessages.deleteRequiredTypeMenuText, this.deleteRequiredType));
            table.setMenu(menuManager.createContextMenu(table));

            createRequiredTypesViewer(table);

            // fill with data
            this.requiredTypesViewer.setInput(this);
        }

        { // bottom - comments
            final Composite commentsContainer = toolkit.createComposite(body);
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

            if (isEditMode() && !Utils.isEmpty(this.childNodeBeingEdited.getComment())) {
                txtComment.setText(CommentedCndElement.Helper.removeCommentCharacters(this.childNodeBeingEdited.getComment()));
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

        // must be done after constructor
        this.childNodeBeingEdited.addListener(new PropertyChangeListener() {

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
        validateDefaultType();
        validateName();
        validateRequiredTypes();
    }

    void createRequiredTypesActions() {
        this.addRequiredType = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleAddRequiredType();
            }
        };
        this.addRequiredType.setToolTipText(CndMessages.addRequiredTypeToolTip);
        this.addRequiredType.setImageDescriptor(JcrUiUtils.getNewImageDescriptor());

        this.deleteRequiredType = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleDeleteRequiredType();
            }
        };
        this.deleteRequiredType.setEnabled(false);
        this.deleteRequiredType.setToolTipText(CndMessages.deleteRequiredTypeToolTip);
        this.deleteRequiredType.setImageDescriptor(JcrUiUtils.getDeleteImageDescriptor());

        this.editRequiredType = new Action(Utils.EMPTY_STRING) {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleEditRequiredType();
            }
        };
        this.editRequiredType.setEnabled(false);
        this.editRequiredType.setToolTipText(CndMessages.editRequiredTypeToolTip);
        this.editRequiredType.setImageDescriptor(JcrUiUtils.getEditImageDescriptor());
    }

    private void createRequiredTypesViewer( final Table requiredTypesTable ) {
        this.requiredTypesViewer = new TableViewer(requiredTypesTable);
        this.requiredTypesViewer.setLabelProvider(new LabelProvider());
        this.requiredTypesViewer.setContentProvider(new IStructuredContentProvider() {

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
                final ChildNodeDefinition childNode = accessModel();
                final String[] requiredTypes = childNode.getRequiredPrimaryTypeNames();

                if (requiredTypes == null) {
                    return Utils.EMPTY_OBJECT_ARRAY;
                }

                return requiredTypes;
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

        this.requiredTypesViewer.addDoubleClickListener(new IDoubleClickListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
             */
            @Override
            public void doubleClick( final DoubleClickEvent event ) {
                handleEditRequiredType();
            }
        });

        this.requiredTypesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                handleRequiredTypeSelected();
            }
        });
    }

    /**
     * <strong>Should only be called after the dialog's <code>OK</code> button has been selected.</strong>
     * 
     * @return the child node definition represented by the dialog UI controls (never <code>null</code>)
     */
    public ChildNodeDefinition getChildNodeDefinition() {
        return this.childNodeBeingEdited;
    }

    /**
     * @return the selected required type or <code>null</code> if the viewer has an empty selection
     */
    private String getSelectedRequiredType() {
        final IStructuredSelection selection = (IStructuredSelection)this.requiredTypesViewer.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        assert (selection.size() == 1) : "required types viewer should not allow multiple selections"; //$NON-NLS-1$
        assert (selection.getFirstElement() instanceof String) : "selection was not a string"; //$NON-NLS-1$
        return (String)selection.getFirstElement();
    }

    void handleAddRequiredType() {
        final QualifiedNameDialog dialog = new QualifiedNameDialog(getShell(),
                                                                   CndMessages.newRequiredTypeDialogTitle,
                                                                   Messages.requiredTypeName,
                                                                   this.existingNamespacePrefixes);
        dialog.setExistingQNames(this.childNodeBeingEdited.getRequiredTypes());
        dialog.setProposalProvider(this.requiredTypeProposalProvider);
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            final QualifiedName newQName = dialog.getQualifiedName();

            if (!this.childNodeBeingEdited.addRequiredType(newQName.get())) {
                MessageFormDialog.openError(getShell(), UiMessages.errorDialogTitle, JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorAddingRequiredType, newQName));
            }
        }
    }

    void handleAutocreatedChanged( final boolean newAutocreated ) {
        this.childNodeBeingEdited.setAutoCreated(newAutocreated);
    }

    void handleCommentChanged( final String newComment ) {
        this.childNodeBeingEdited.setComment(newComment);
    }

    void handleDefaultTypeChanged( final String newDefaultType ) {
        this.childNodeBeingEdited.setDefaultPrimaryTypeName(newDefaultType);
    }

    void handleDeleteRequiredType() {
        assert (getSelectedRequiredType() != null) : "Delete required type handler called and there is no required type selected"; //$NON-NLS-1$

        String requiredTypeName = getSelectedRequiredType();

        // should always have a name but just in case
        if (Utils.isEmpty(requiredTypeName)) {
            requiredTypeName = Messages.missingName;
        }

        // show confirmation dialog
        if (MessageFormDialog.openQuestion(getShell(), CndMessages.deleteRequiredTypeDialogTitle, JcrUiUtils.getCndEditorImage(),
                                           NLS.bind(CndMessages.deleteRequiredTypeDialogMessage, requiredTypeName))) {
            this.childNodeBeingEdited.removeRequiredType(requiredTypeName);
        }
    }

    void handleEditRequiredType() {
        assert (getSelectedRequiredType() != null) : "Edit required type handler has been called when there is no required type selected"; //$NON-NLS-1$
        final String selectedRequiredType = getSelectedRequiredType();

        final QualifiedNameDialog dialog = new QualifiedNameDialog(getShell(),
                                                                   CndMessages.editRequiredTypeDialogTitle,
                                                                   Messages.requiredTypeName,
                                                                   this.existingNamespacePrefixes,
                                                                   QualifiedName.parse(selectedRequiredType));
        dialog.setExistingQNames(this.childNodeBeingEdited.getRequiredTypes());
        dialog.setProposalProvider(this.requiredTypeProposalProvider);
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            final QualifiedName modifiedRequiredType = dialog.getQualifiedName();
            boolean removed = false;
            boolean added = false;

            // remove existing and add in new
            if (this.childNodeBeingEdited.removeRequiredType(selectedRequiredType)) {
                removed = true;

                if (this.childNodeBeingEdited.addRequiredType(modifiedRequiredType.get())) {
                    added = true;
                }
            }

            if (!removed || !added) {
                MessageFormDialog.openError(getShell(),
                                            UiMessages.errorDialogTitle,
                                            JcrUiUtils.getCndEditorImage(),
                                            NLS.bind(CndMessages.errorEditingRequiredType, new Object[] { modifiedRequiredType,
                                                    removed, added }));
            }
        }
    }

    void handleMandatoryChanged( final boolean newMandatory ) {
        this.childNodeBeingEdited.setMandatory(newMandatory);
    }

    void handleNameChanged( final String newName ) {
        this.childNodeBeingEdited.setName(newName);
    }

    void handleOnParentVersionChanged( final String newOpv ) {
        this.childNodeBeingEdited.setOnParentVersion(newOpv);
    }

    void handlePropertyChanged( final PropertyChangeEvent e ) {
        final String propName = e.getPropertyName();

        if (PropertyName.AUTOCREATED.toString().equals(propName) || PropertyName.MANDATORY.toString().equals(propName)
                || PropertyName.PROTECTED.toString().equals(propName)
                || PropertyName.SAME_NAME_SIBLINGS.toString().equals(propName)
                || PropertyName.ON_PARENT_VERSION.toString().equals(propName)) {
            validateAttributes();
        } else if (PropertyName.DEFAULT_TYPE.toString().equals(propName)) {
            validateDefaultType();
        } else if (PropertyName.NAME.toString().equals(propName)) {
            validateName();
        } else if (PropertyName.REQUIRED_TYPES.toString().equals(propName)) {
            validateRequiredTypes();
            this.requiredTypesViewer.refresh();
            validateDefaultType();
        }

        updateState();
    }

    void handleProtectedChanged( final boolean newProtected ) {
        this.childNodeBeingEdited.setProtected(newProtected);
    }

    void handleRequiredTypeSelected() {
        // update button enablements
        final boolean enable = (getSelectedRequiredType() != null);

        if (this.editRequiredType.isEnabled() != enable) {
            this.editRequiredType.setEnabled(enable);
        }

        if (this.deleteRequiredType.isEnabled() != enable) {
            this.deleteRequiredType.setEnabled(enable);
        }
    }

    void handleSameNamedSiblingsChanged( final boolean newSns ) {
        this.childNodeBeingEdited.setSameNameSiblings(newSns);
    }

    private boolean isEditMode() {
        return (this.originalChildNode != null);
    }

    private void refreshDefaultTypes() {
        // set default type choices if they have changed
        final String[] temp = this.childNodeBeingEdited.getRequiredPrimaryTypeNames();
        final String[] currentRequiredTypes = new String[temp.length + 1];
        currentRequiredTypes[0] = CndMessages.notAssignedItemChoice;

        if (temp.length != 0) {
            System.arraycopy(temp, 0, currentRequiredTypes, 1, temp.length);
        }

        if ((this.cbxDefaultType.getItemCount() != currentRequiredTypes.length)
                || !Arrays.asList(currentRequiredTypes).containsAll(Arrays.asList(this.cbxDefaultType.getItems()))) {
            this.cbxDefaultType.setItems(currentRequiredTypes);
        }

        // select the current default type
        final String defaultTypeName = this.childNodeBeingEdited.getDefaultPrimaryTypeName();

        if (Utils.isEmpty(defaultTypeName)) {
            this.cbxDefaultType.select(0); // select no default type assigned
        } else {
            final int index = this.cbxDefaultType.indexOf(defaultTypeName);

            if (index == -1) {
                // not a valid primary item but add and select
                this.cbxDefaultType.add(defaultTypeName);
                this.cbxDefaultType.select(this.cbxDefaultType.getItemCount() - 1);
            } else {
                this.cbxDefaultType.select(index);
            }
        }
    }

    /**
     * @param proposalProvider the required type proposal provider (can be <code>null</code>)
     */
    public void setRequiredTypeProposalProvider( final QualifiedNameProposalProvider proposalProvider ) {
        this.requiredTypeProposalProvider = proposalProvider;
    }

    private void updateMessage( final ValidationStatus status,
                                final ErrorMessage errorMsg ) {
        JcrUiUtils.setMessage(status, errorMsg);

        if (errorMsg.isOk()) {
            this.scrolledForm.getMessageManager().removeMessage(errorMsg.getKey(), errorMsg.getControl());
        } else {
            this.scrolledForm.getMessageManager().addMessage(errorMsg.getKey(), errorMsg.getMessage(), null,
                                                             errorMsg.getMessageType(), errorMsg.getControl());
        }
    }

    private void updateState() {
        final int messageType = this.scrolledForm.getMessageType();
        boolean enable = (messageType != IMessageProvider.ERROR);

        if (enable && isEditMode() && this.originalChildNode.equals(this.childNodeBeingEdited)) {
            enable = false;
        }

        // set enabled state of OK button
        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }

    private void validateAttributes() {
        // no validation required since user cannot enter an invalid value
    }

    private void validateDefaultType() {
        updateMessage(CndValidator.validateDefaultType(this.childNodeBeingEdited, this.existingNamespacePrefixes),
                      this.defaultTypeError);
    }

    private void validateName() {
        updateMessage(this.nameEditor.getStatus(), this.nameError);
    }

    private void validateRequiredTypes() {
        updateMessage(CndValidator.validateRequiredTypes(this.childNodeBeingEdited, this.existingNamespacePrefixes),
                      this.requiredTypesError);
    }
}
