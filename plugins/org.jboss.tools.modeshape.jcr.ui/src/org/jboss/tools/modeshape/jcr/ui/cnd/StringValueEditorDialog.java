/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;
import org.jboss.tools.modeshape.ui.forms.FormUtils.Styles;

/**
 * The <code>StringValueEditorDialog</code> is used to create or edit a string value.
 */
abstract class StringValueEditorDialog extends FormDialog {

    private Button btnOk;

    /**
     * The original value (<code>null</code> when creating a new value).
     */
    private String originalValue;

    private ScrolledForm scrolledForm;

    private final Settings settings;

    /**
     * The value being edited (never <code>null</code>).
     */
    private String valueBeingEdited;

    /**
     * Used to construct a new value.
     * 
     * @param parentShell the parent shell (may be <code>null</code>)
     */
    protected StringValueEditorDialog( final Shell parentShell ) {
        super(parentShell);

        this.settings = getSettings();
        assert (this.settings != null) : "settings is null"; //$NON-NLS-1$
        assert (this.settings.acceptMessage != null) : "settings.acceptMessage is null"; //$NON-NLS-1$
        assert (this.settings.initialMessage != null) : "settings.initialMessage is null"; //$NON-NLS-1$
        assert (this.settings.labelText != null) : "settings.labelText is null"; //$NON-NLS-1$
        assert (this.settings.messageAreaTitle != null) : "settings.messageAreaTitle is null"; //$NON-NLS-1$

        if (this.settings.valueBeingEdited == null) {
            this.valueBeingEdited = Utils.EMPTY_STRING;
        } else {
            this.originalValue = this.settings.valueBeingEdited;
            this.valueBeingEdited = this.settings.valueBeingEdited;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(CndMessages.valueEditorDialogTitle);
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
        this.scrolledForm.setText(this.settings.messageAreaTitle);
        this.scrolledForm.setImage(Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR));
        this.scrolledForm.setMessage(this.settings.initialMessage, IMessageProvider.NONE);

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        final Composite body = this.scrolledForm.getBody();
        body.setLayout(new GridLayout(2, false));

        final Label lblValue = toolkit.createLabel(body, this.settings.labelText, SWT.NONE);
        lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        final Text txtValue = toolkit.createText(body, null, Styles.TEXT_STYLE);
        txtValue.setToolTipText(this.settings.valueToolTip);

        final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.verticalIndent += ((GridLayout)body.getLayout()).verticalSpacing;
        txtValue.setLayoutData(gd);

        if (isEditMode()) {
            txtValue.setText(this.valueBeingEdited);
        }

        txtValue.addModifyListener(new ModifyListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( final ModifyEvent e ) {
                handleValueChanged(((Text)e.widget).getText());
            }
        });

        txtValue.setFocus();
    }

    /**
     * @return the dialog settings (never <code>null</code>)
     */
    protected abstract Settings getSettings();

    /**
     * <strong>Should only be called after the OK button has been pressed.</strong>
     * 
     * @return the new or edited value (never <code>null</code>)
     */
    public String getValue() {
        return this.valueBeingEdited;
    }

    void handleValueChanged( final String newValue ) {
        this.valueBeingEdited = newValue;
        updateState();
    }

    private boolean isEditMode() {
        return (this.originalValue != null);
    }

    private void updateState() {
        boolean enable = true;
        ValidationStatus status = ValidationStatus.OK_STATUS;

        // validate value
        if (this.settings.validator != null) {
            status = this.settings.validator.validate(this.valueBeingEdited);
            enable = !status.isError();
        }

        // a bug in Eclipse doesn't reset the font color going from an error to NONE so first set to INFORMATION to get the
        // font color to change
        this.scrolledForm.setMessage(Utils.EMPTY_STRING, IMessageProvider.INFORMATION);

        if (!enable) {
            this.scrolledForm.setMessage(status.getMessage(), IMessageProvider.ERROR);
        } else if ((isEditMode() && this.originalValue.equals(this.valueBeingEdited))
                || (!isEditMode() && Utils.isEmpty(this.valueBeingEdited))) {
            enable = false;
            this.scrolledForm.setMessage(this.settings.initialMessage, IMessageProvider.NONE);
        } else {
            int severity = IMessageProvider.NONE; // OK severity
            String message = status.getMessage();

            if (status.isWarning()) {
                severity = IMessageProvider.WARNING;
            } else if (status.isInfo()) {
                severity = IMessageProvider.INFORMATION;
            } else {
                message = this.settings.initialMessage;
            }

            this.scrolledForm.setMessage(message, severity);
        }

        // set enabled state of OK button
        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }

    class Settings {

        /**
         * Message area message when OK button is enabled and value is valid (cannot be <code>null</code> or empty).
         */
        String acceptMessage;

        /**
         * Message area message when the dialog is first displayed or when the value is changed back to the initial value (cannot be
         * <code>null</code> or empty).
         */
        String initialMessage;

        /**
         * Text of the label to the left of the textfield (cannot be <code>null</code> or empty).
         */
        String labelText;

        /**
         * Message area title (cannot be <code>null</code> or empty).
         */
        String messageAreaTitle;

        /**
         * A validator used to determine if the current value is valid (<code>null</code> if no validation is needed).
         */
        Validator validator;

        /**
         * The existing value being edited (can be <code>null</code> if creating a new value).
         */
        String valueBeingEdited;

        /**
         * A tooltip for the textfield where the value is being edited (can be <code>null</code> or empty).
         */
        String valueToolTip;
    }

    /**
     * Validator of the values.
     */
    interface Validator {

        /**
         * @param newValue the value being validated (can be <code>null</code> or empty)
         * @return the validation status (never be <code>null</code>)
         */
        ValidationStatus validate( String newValue );
    }
}
