/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.forms;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.jboss.tools.modeshape.ui.UiMessages;

/**
 * A form-based message dialog.
 */
public class MessageFormDialog extends FormDialog {

    private static String getMessageTitle( final Kind kind ) {
        if (Kind.ERROR == kind) {
            return UiMessages.errorDialogTitle;
        }

        if (Kind.INFORMATION == kind) {
            return UiMessages.infoDialogTitle;
        }

        if (Kind.QUESTION == kind) {
            return UiMessages.questionDialogTitle;
        }

        if (Kind.WARNING == kind) {
            return UiMessages.warningDialogTitle;
        }

        assert false : "null or unknown dialog kind found"; //$NON-NLS-1$
        throw new IllegalArgumentException();
    }

    private static int getMessageType( final Kind kind ) {
        if (Kind.ERROR == kind) {
            return IMessageProvider.ERROR;
        }

        if ((Kind.INFORMATION == kind) || (Kind.QUESTION == kind)) {
            return IMessageProvider.INFORMATION;
        }

        if (Kind.WARNING == kind) {
            return IMessageProvider.WARNING;
        }

        assert false : "null or unknown dialog kind found"; //$NON-NLS-1$
        throw new IllegalArgumentException();
    }

    private static boolean open( final Kind kind,
                                 final Shell parent,
                                 final String msgTitle,
                                 final Image titleImage,
                                 final String message ) {
        final FormDialog dialog = new MessageFormDialog(kind, parent, msgTitle, titleImage, message);
        dialog.create();
        dialog.getShell().pack();
        return (dialog.open() == Window.OK);
    }

    /**
     * Open a dialog with the warning title.
     * 
     * @param parent the parent (can be <code>null</code>)
     * @param msgTitle the message area title (if <code>null</code> or empty the message area will not have a title)
     * @param titleImage the title image or <code>null</code>
     * @param message the dialog message (if <code>null</code> or empty the dialog area will not have a message)
     * @return <code>true</code> if the dialog was OK'd
     */
    public static boolean openError( final Shell parent,
                                     final String msgTitle,
                                     final Image titleImage,
                                     final String message ) {
        return open(Kind.ERROR, parent, msgTitle, titleImage, message);
    }

    /**
     * Open a dialog with the warning title.
     * 
     * @param parent the parent (can be <code>null</code>)
     * @param msgTitle the message area title (if <code>null</code> or empty the message area will not have a title)
     * @param titleImage the title image or <code>null</code>
     * @param message the dialog message (if <code>null</code> or empty the dialog area will not have a message)
     * @return <code>true</code> if the dialog was OK'd
     */
    public static boolean openInfo( final Shell parent,
                                    final String msgTitle,
                                    final Image titleImage,
                                    final String message ) {
        return open(Kind.INFORMATION, parent, msgTitle, titleImage, message);
    }

    /**
     * Open a dialog with the question title.
     * 
     * @param parent the parent (can be <code>null</code>)
     * @param msgTitle the message area title (if <code>null</code> or empty the message area will not have a title)
     * @param titleImage the title image or <code>null</code>
     * @param message the dialog message (if <code>null</code> or empty the dialog area will not have a message)
     * @return <code>true</code> if the dialog was OK'd
     */
    public static boolean openQuestion( final Shell parent,
                                        final String msgTitle,
                                        final Image titleImage,
                                        final String message ) {
        return open(Kind.QUESTION, parent, msgTitle, titleImage, message);
    }

    /**
     * Open a dialog with the warning title.
     * 
     * @param parent the parent (can be <code>null</code>)
     * @param msgTitle the message area title (if <code>null</code> or empty the message area will not have a title)
     * @param titleImage the title image or <code>null</code>
     * @param message the dialog message (if <code>null</code> or empty the dialog area will not have a message)
     * @return <code>true</code> if the dialog was OK'd
     */
    public static boolean openWarning( final Shell parent,
                                       final String msgTitle,
                                       final Image titleImage,
                                       final String message ) {
        return open(Kind.WARNING, parent, msgTitle, titleImage, message);
    }

    private final Image titleImage;

    private final String message;
    private final String msgTitle;
    private final Kind kind;

    private MessageFormDialog( final Kind kind,
                               final Shell parent,
                               final String msgTitle,
                               final Image titleImage,
                               final String message ) {
        super(parent);
        this.kind = kind;
        this.msgTitle = msgTitle;
        this.titleImage = titleImage;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);

        newShell.setText(getMessageTitle(this.kind));

        if (this.titleImage != null) {
            newShell.setImage(this.titleImage);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( final Composite parent,
                                   final int id,
                                   String label,
                                   final boolean defaultButton ) {
        if (this.kind == Kind.QUESTION) {
            if (Window.OK == id) {
                label = IDialogConstants.YES_LABEL;
            } else if (Window.CANCEL == id) {
                label = IDialogConstants.NO_LABEL;
            }
        }

        return super.createButton(parent, id, label, defaultButton);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( final Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

        if (this.kind == Kind.QUESTION) {
            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createFormContent( final IManagedForm managedForm ) {
        final ScrolledForm scrolledForm = managedForm.getForm();
        scrolledForm.setText(this.msgTitle);
        scrolledForm.setMessage("", getMessageType(this.kind)); //$NON-NLS-1$

        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(scrolledForm.getForm());

        final Composite body = scrolledForm.getBody();
        body.setLayout(new TableWrapLayout());
        toolkit.createLabel(body, this.message, SWT.WRAP);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected int getShellStyle() {
        if (this.kind != Kind.QUESTION) {
            return super.getShellStyle() | SWT.SHEET;
        }
        return super.getShellStyle();
    }

    private enum Kind {
        ERROR,
        QUESTION,
        INFORMATION,
        WARNING
    }
}