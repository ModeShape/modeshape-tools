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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;

/**
 * The <code>QualifiedNameDialog</code> is used to create or edit a qualified name.
 */
final class QualifiedNameDialog extends FormDialog {

    private Button btnOk;

    /**
     * An optional collection of existing qualified names. When this is non-empty, it is checked to make sure the qualified name
     * being edited is not a duplicate.
     */
    private Collection<QualifiedName> existingQNames;

    private QualifiedNameEditor nameEditor;

    /**
     * The qualified name being edited or <code>null</code> when creating a qualified name.
     */
    private QualifiedName qnameBeingEdited;

    private final String qualifiedNameType;

    private QualifiedNameProposalProvider proposalProvider;

    private ScrolledForm scrolledForm;

    private final String title;

    /**
     * A collection of known qualifiers/namespace prefixes to the CND (never <code>null</code>).
     */
    private final List<String> validQualifiers;

    /**
     * Used to construct a new qualified name.
     * 
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param title a short message area title for use when creating a new qualified name (cannot be <code>null</code> or empty)
     * @param qualifiedNameType a word describing what the qualified name represents (cannot be <code>null</code> or empty)
     * @param existingQualifiers the existing qualifies (can be <code>null</code> or empty)
     */
    public QualifiedNameDialog( final Shell parentShell,
                                final String title,
                                final String qualifiedNameType,
                                final Collection<String> existingQualifiers ) {
        super(parentShell);
        Utils.verifyIsNotNull(qualifiedNameType, "qualifiedNameType"); //$NON-NLS-1$

        this.title = title;
        this.qualifiedNameType = qualifiedNameType;
        this.validQualifiers = ((existingQualifiers == null) ? new ArrayList<String>(1) : new ArrayList<String>(existingQualifiers));
        this.validQualifiers.add(0, CndMessages.noNameQualifierChoice); // include empty qualifier at index 0
    }

    /**
     * Used to edit an existing qualified name.
     * 
     * @param parentShell the parent shell (may be <code>null</code>)
     * @param title a short message area title for use when creating a new qualified name (cannot be <code>null</code> or empty)
     * @param qualifiedNameType a word describing what the qualified name represents (cannot be <code>null</code> or empty)
     * @param existingQualifiers the existing qualifiers (can be <code>null</code> or empty)
     * @param qnameBeingEdited the qualified name being edited (cannot be <code>null</code>)
     */
    public QualifiedNameDialog( final Shell parentShell,
                                final String title,
                                final String qualifiedNameType,
                                final Collection<String> existingQualifiers,
                                final QualifiedName qnameBeingEdited ) {
        this(parentShell, title, qualifiedNameType, existingQualifiers);
        Utils.verifyIsNotNull(qnameBeingEdited, "qnameBeingEdited"); //$NON-NLS-1$

        this.qnameBeingEdited = qnameBeingEdited;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(CndMessages.qualifiedNameDialogTitle);
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
        this.scrolledForm.setText(this.title);
        this.scrolledForm.setImage(Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR));
        this.scrolledForm.setMessage(NLS.bind(CndMessages.qualifiedNameDialogMsg, this.qualifiedNameType), IMessageProvider.NONE);

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        final Composite body = this.scrolledForm.getBody();
        body.setLayout(new GridLayout(2, false));

        this.nameEditor = new QualifiedNameEditor(body,
                                                  SWT.NONE,
                                                  toolkit,
                                                  this.qualifiedNameType,
                                                  this.validQualifiers,
                                                  this.qnameBeingEdited);
        this.nameEditor.setExistingQNames(this.existingQNames);
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
        this.nameEditor.setProposalProvider(this.proposalProvider);
    }

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the new or edited qualified name (never <code>null</code>)
     */
    public QualifiedName getQualifiedName() {
        return this.nameEditor.getQualifiedName();
    }

    void handleNameChanged( final String newName ) {
        updateState();
    }

    private boolean isEditMode() {
        return (this.qnameBeingEdited != null);
    }

    /**
     * @param existingQNames used to check against for duplicate qualified names (can be <code>null</code> or empty)
     */
    void setExistingQNames( final Collection<QualifiedName> existingQNames ) {
        if (this.nameEditor == null) {
            this.existingQNames = existingQNames;
        } else {
            this.nameEditor.setExistingQNames(existingQNames);
        }
    }

    /**
     * @param proposalProvider the proposal provider (can be <code>null</code>)
     */
    public void setProposalProvider( final QualifiedNameProposalProvider proposalProvider ) {
        if (this.nameEditor == null) {
          this.proposalProvider = proposalProvider;
        } else {
            this.nameEditor.setProposalProvider(proposalProvider);
        }
    }

    private void updateState() {
        final QualifiedName modifiedQName = this.nameEditor.getQualifiedName();
        final ValidationStatus status = this.nameEditor.getStatus();
        boolean enable = !status.isError();

        // a bug in Eclipse doesn't reset the font color going from an error to NONE so first set to INFORMATION to get the
        // font color to change
        this.scrolledForm.setMessage(Utils.EMPTY_STRING, IMessageProvider.INFORMATION);

        if (!enable) {
            this.scrolledForm.setMessage(status.getMessage(), IMessageProvider.ERROR);
        } else if ((isEditMode() && modifiedQName.equals(this.qnameBeingEdited))
                || (!isEditMode() && Utils.isEmpty(modifiedQName.getQualifier()) && Utils.isEmpty(modifiedQName.getUnqualifiedName()))) {
            enable = false;
            this.scrolledForm.setMessage(NLS.bind(CndMessages.qualifiedNameDialogMsg, this.qualifiedNameType),
                                         IMessageProvider.NONE);
        } else {
            int severity = IMessageProvider.NONE; // OK severity
            String message = status.getMessage();

            if (status.isWarning()) {
                severity = IMessageProvider.WARNING;
            } else if (status.isInfo()) {
                severity = IMessageProvider.INFORMATION;
            } else {
                message = NLS.bind(CndMessages.acceptQualifiedNameDialogMsg, this.qualifiedNameType);
            }

            this.scrolledForm.setMessage(message, severity);
        }

        // set enabled state of OK button
        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }
}
