/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jboss.tools.modeshape.jcr.MultiValidationStatus;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.WorkspaceRegistry;
import org.jboss.tools.modeshape.jcr.cnd.CndValidator;
import org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;
import org.jboss.tools.modeshape.ui.forms.FormUtils.Styles;

/**
 * The <code>NamespaceMappingDialog</code> is used to create or edit a namespace mapping.
 */
final class NamespaceMappingDialog extends FormDialog {

    private Button btnOk;

    private String comment;

    /**
     * An optional list of existing namespace mappings. When this is non-empty, it is checked to make sure the prefix and URI being
     * edited is not a duplicate.
     */
    private final List<NamespaceMapping> existingNamespaces;

    /**
     * The namespace mapping being edited or <code>null</code> when creating a namespace mapping.
     */
    private NamespaceMapping namespaceBeingEdited;

    private String prefix;

    private String saveUri = Utils.EMPTY_STRING;

    private ScrolledForm scrolledForm;

    private Text txtUri;

    private String uri;

    /**
     * Used to construct a new namespace mapping.
     * 
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingNamespaces the existing namespace mappings (can be <code>null</code> or empty)
     */
    public NamespaceMappingDialog( final Shell parentShell,
                                   final Collection<NamespaceMapping> existingNamespaces ) {
        super(parentShell);

        this.existingNamespaces = ((existingNamespaces == null) ? new ArrayList<NamespaceMapping>()
                                                               : new ArrayList<NamespaceMapping>(existingNamespaces));
    }

    /**
     * Used to edit an existing namespace mapping.
     * 
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param existingNamespaces the existing namespace mappings (can be <code>null</code> or empty)
     * @param namespaceBeingEdited the namespace mapping being edited (cannot be <code>null</code>)
     */
    public NamespaceMappingDialog( final Shell parentShell,
                                   final Collection<NamespaceMapping> existingNamespaces,
                                   final NamespaceMapping namespaceBeingEdited ) {
        this(parentShell, existingNamespaces);
        Utils.verifyIsNotNull(namespaceBeingEdited, "namespaceBeingEdited"); //$NON-NLS-1$

        this.namespaceBeingEdited = namespaceBeingEdited;
        this.prefix = this.namespaceBeingEdited.getPrefix();
        this.uri = this.namespaceBeingEdited.getUri();
        this.comment = this.namespaceBeingEdited.getComment();

        // remove the namespace mapping being edited so validating doesn't show it as a duplicate
        this.existingNamespaces.remove(this.namespaceBeingEdited);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(CndMessages.namespaceDialogTitle);
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
        this.scrolledForm.setText(isEditMode() ? CndMessages.editNamespaceDialogTitle : CndMessages.newNamespaceDialogTitle);
        this.scrolledForm.setImage(Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR));
        this.scrolledForm.setMessage(CndMessages.namespaceDialogMsg, IMessageProvider.NONE);

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        final Composite body = this.scrolledForm.getBody();
        body.setLayout(new GridLayout(2, false));

        { // prefix
            final Label lblPrefix = toolkit.createLabel(body, CndMessages.namespacePrefixLabel, SWT.NONE);
            lblPrefix.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

            final Text txtPrefix = toolkit.createText(body, null, Styles.TEXT_STYLE);
            txtPrefix.setToolTipText(CndMessages.namespacePrefixToolTip);

            final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd.verticalIndent += ((GridLayout)body.getLayout()).verticalSpacing;
            txtPrefix.setLayoutData(gd);

            if (isEditMode()) {
                txtPrefix.setText(this.namespaceBeingEdited.getPrefix());
            }

            txtPrefix.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( final ModifyEvent e ) {
                    handlePrefixChanged(((Text)e.widget).getText());
                }
            });

            txtPrefix.setFocus();
        }

        { // URI
            final Label lblUri = toolkit.createLabel(body, CndMessages.uriLabel, SWT.NONE);
            lblUri.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

            this.txtUri = toolkit.createText(body, null, Styles.TEXT_STYLE);
            this.txtUri.setToolTipText(CndMessages.namespaceUriToolTip);

            final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
            gd.verticalIndent += ((GridLayout)body.getLayout()).verticalSpacing;
            this.txtUri.setLayoutData(gd);

            if (isEditMode()) {
                this.txtUri.setText(this.namespaceBeingEdited.getUri());
            }

            this.txtUri.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( final ModifyEvent e ) {
                    handleUriChanged(((Text)e.widget).getText());
                }
            });
        }

        { // comment
            final Label lblComment = toolkit.createLabel(body, CndMessages.commentLabel, SWT.NONE);
            lblComment.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            Text txtComment = toolkit.createText(body, null, Styles.TEXT_STYLE | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            txtComment.setToolTipText(CndMessages.commentedToolTip);

            final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            gd.verticalIndent += ((GridLayout)body.getLayout()).verticalSpacing;
            gd.heightHint = txtComment.getLineHeight() * 3;
            txtComment.setLayoutData(gd);

            if (isEditMode() && !Utils.isEmpty(this.namespaceBeingEdited.getComment())) {
                txtComment.setText(CommentedCndElement.Helper.removeCommentCharacters(this.namespaceBeingEdited.getComment()));
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

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return a namespace mapping representing the dialog changes (never <code>null</code>)
     */
    public NamespaceMapping getNamespaceMapping() {
        NamespaceMapping namespaceMapping = new NamespaceMapping(this.prefix, this.uri);
        namespaceMapping.setComment(this.comment);
        return namespaceMapping;
    }

    void handleCommentChanged( final String newComment ) {
        this.comment = newComment;
        updateState();
    }

    void handlePrefixChanged( final String newPrefix ) {
        this.prefix = newPrefix;

        try {
            if (!Utils.isEmpty(this.prefix)) {
                if (!Utils.isEmpty(this.saveUri) && WorkspaceRegistry.get().getUri(this.prefix) == null) {
                    this.txtUri.setText(this.saveUri);
                    this.saveUri = Utils.EMPTY_STRING;
                } else {
                    this.saveUri = this.txtUri.getText();
                    this.txtUri.setText(WorkspaceRegistry.get().getUri(this.prefix));
                }
            }
        } catch (final Exception e) {
            Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID, null, e));
        }

        updateState();
    }

    void handleUriChanged( final String newUri ) {
        this.uri = newUri;
        updateState();
    }

    private boolean isEditMode() {
        return (this.namespaceBeingEdited != null);
    }

    private void updateState() {
        // validate qname
        final NamespaceMapping currentNamespace = getNamespaceMapping();
        final MultiValidationStatus status = CndValidator.validateNamespaceMapping(currentNamespace, this.existingNamespaces);
        boolean enable = !status.isError();

        // a bug in Eclipse doesn't reset the font color going from an error to NONE so first set to INFORMATION to get the
        // font color to change
        this.scrolledForm.setMessage(Utils.EMPTY_STRING, IMessageProvider.INFORMATION);

        if (!enable) {
            this.scrolledForm.setMessage(status.getMessage(), IMessageProvider.ERROR);
            Point preferredSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);

            if (preferredSize.x > getShell().getSize().x) {
                getShell().pack();
            }
        } else if ((isEditMode() && currentNamespace.equals(this.namespaceBeingEdited))
                || (!isEditMode() && Utils.isEmpty(this.prefix) && Utils.isEmpty(this.uri))) {
            enable = false;
            this.scrolledForm.setMessage(CndMessages.namespaceDialogMsg, IMessageProvider.NONE);
        } else {
            int severity = IMessageProvider.NONE; // OK severity
            String message = status.getMessage();

            if (status.isWarning()) {
                severity = IMessageProvider.WARNING;
            } else if (status.isInfo()) {
                severity = IMessageProvider.INFORMATION;
            } else {
                try {
                    if (WorkspaceRegistry.get().isBuiltIn(currentNamespace)) {
                        message = CndMessages.acceptBuiltInNamespaceDialogMsg;
                    } else {
                        message = CndMessages.acceptNamespaceDialogMsg;
                    }
                } catch (Exception e) {
                    message = CndMessages.acceptNamespaceDialogMsg;
                }
            }

            this.scrolledForm.setMessage(message, severity);
        }

        // set enabled state of OK button
        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }
}
