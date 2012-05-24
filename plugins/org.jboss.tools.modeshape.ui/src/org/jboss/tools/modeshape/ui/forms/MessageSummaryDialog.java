/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.forms;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jboss.tools.modeshape.ui.Activator;
import org.jboss.tools.modeshape.ui.UiConstants;
import org.jboss.tools.modeshape.ui.UiMessages;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A dialog that displays messages in a table.
 */
public class MessageSummaryDialog extends FormDialog {

    private final String dialogTitle;
    private final String messageAreaMessage;
    private final String messageAreaTitle;
    private final int messageType;
    private final IMessage[] messages;

    /**
     * @param parent the parent (can be <code>null</code>)
     * @param dialogTitle the dialog window title (cannot be <code>null</code> or empty)
     * @param messageAreaTitle the message area title (cannot be <code>null</code> or empty)
     * @param messageAreaMessage the message area message (cannot be <code>null</code> or empty)
     * @param messageType the message type (see {@link IMessageProvider})
     * @param messages the messages being displayed (cannot be <code>null</code>)
     */
    public MessageSummaryDialog( final Shell parent,
                                 final String dialogTitle,
                                 final String messageAreaTitle,
                                 final String messageAreaMessage,
                                 final int messageType,
                                 final IMessage[] messages ) {
        super(parent);
        UiUtils.verifyIsNotNull(messages, "messages"); //$NON-NLS-1$

        this.dialogTitle = dialogTitle;
        this.messages = messages;
        this.messageAreaTitle = messageAreaTitle;
        this.messageAreaMessage = messageAreaMessage;
        this.messageType = messageType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( final Shell newShell ) {
        super.configureShell(newShell);
        newShell.setText(dialogTitle);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( final Composite parent ) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

        // export button
        final Button btn = createButton(parent, IDialogConstants.CLIENT_ID, UiMessages.export, false);
        btn.setToolTipText(UiMessages.exportMessagesToolTip);
        btn.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( final SelectionEvent e ) {
                handleExportMessages();
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
        final ScrolledForm scrolledForm = managedForm.getForm();
        scrolledForm.setText(messageAreaTitle); // set header area title
        scrolledForm.setMessage(messageAreaMessage, messageType, messages);
        scrolledForm.getBody().setLayout(new GridLayout());
        scrolledForm.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final FormToolkit toolkit = managedForm.getToolkit();
        final Composite container = toolkit.createComposite(managedForm.getForm().getBody());
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        toolkit.paintBordersFor(container);

        int count = messages.length;

        if (count > 20) {
            count = 20;
        } else {
            count += 2;
        }

        final Table table = FormUtils.createTable(toolkit, container);
        final GridData gd = (GridData)table.getLayoutData();
        gd.heightHint = table.getItemHeight() * count;
        gd.minimumHeight = gd.heightHint;

        final TableViewer viewer = new TableViewer(table);
        viewer.setLabelProvider(new LabelProvider());
        viewer.setContentProvider(new IStructuredContentProvider() {

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
                return getMessages();
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

        { // create message type column
            final TableViewerColumn messageTypeColumn = new TableViewerColumn(viewer, SWT.CENTER);
            UiUtils.configureColumn(messageTypeColumn, new MessageLabelProvider(true), UiUtils.EMPTY_STRING, UiUtils.EMPTY_STRING,
                                    false, false);
        }

        { // create message column
            final TableViewerColumn messageColumn = new TableViewerColumn(viewer, SWT.LEFT);
            UiUtils.configureColumn(messageColumn, new MessageLabelProvider(false), UiMessages.messageColumnHeader,
                                    UiUtils.EMPTY_STRING, false, true);
        }

        // this will sort by child node name
        viewer.setSorter(new MessageViewerSorter());

        // populate table with data
        viewer.setInput(this);

        // size columns to the data
        UiUtils.pack(viewer);
    }

    IMessage[] getMessages() {
        return messages;
    }

    void handleExportMessages() {
        final FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] { "*.txt" }); //$NON-NLS-1$
        dlg.setText(UiMessages.exportMessagesDialogTitle);
        dlg.setFileName(UiMessages.exportMessagesDialogDefaultFileName);
        final String fileName = dlg.open();

        if (fileName != null) {
            final String delim = " - "; //$NON-NLS-1$
            final StringBuilder builder = new StringBuilder();

            for (final IMessage message : messages) {
                final int messageType = message.getMessageType();

                if (messageType == IMessageProvider.ERROR) {
                    builder.append(UiMessages.errorDialogTitle).append(delim);
                } else if (messageType == IMessageProvider.WARNING) {
                    builder.append(UiMessages.errorDialogTitle).append(delim);
                } else if (messageType == IMessageProvider.INFORMATION) {
                    builder.append(UiMessages.errorDialogTitle).append(delim);
                }

                builder.append(message.getMessage()).append('\n');
            }

            BufferedWriter out = null;

            try {
                out = new BufferedWriter(new FileWriter(fileName));
                out.write(builder.toString());
                out.flush();
            } catch (final Exception e) {
                Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, null, e));
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (final java.io.IOException e) {
                    // ignore
                }
            }
        }
    }
}
