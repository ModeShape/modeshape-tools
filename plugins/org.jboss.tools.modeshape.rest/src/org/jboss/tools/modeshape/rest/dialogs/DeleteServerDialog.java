/*
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.
 *
 * This software is made available by Red Hat, Inc. under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution and is
 * available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 */
package org.jboss.tools.modeshape.rest.dialogs;

import static org.jboss.tools.modeshape.rest.IUiConstants.ModeShape_IMAGE_16x;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.modeshape.common.util.CheckArg;

/**
 * The <code>DeleteServerDialog</code> class provides a UI for deleting a {@link ModeShapeServer server}.
 */
public final class DeleteServerDialog extends MessageDialog {

    /**
     * Collection of servers which will be deleted.
     */
    private final Collection<ModeShapeServer> serversBeingDeleted;

    /**
     * @param parentShell the dialog parent
     * @param serversBeingDeleted the servers being deleted (never <code>null</code>)
     */
    public DeleteServerDialog( Shell parentShell,
                               Collection<ModeShapeServer> serversBeingDeleted ) {
        super(parentShell, RestClientI18n.deleteServerDialogTitle, Activator.getDefault().getImage(ModeShape_IMAGE_16x),
                null, MessageDialog.QUESTION, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

        CheckArg.isNotNull(serversBeingDeleted, "serversBeingDeleted"); //$NON-NLS-1$
        this.serversBeingDeleted = serversBeingDeleted;

        // make sure dialog is resizable
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);

        // now set message
        String msg;

        if (this.serversBeingDeleted.size() == 1) {
            ModeShapeServer server = this.serversBeingDeleted.iterator().next();
            msg = NLS.bind(RestClientI18n.deleteServerDialogOneServerMsg, server.getName(), server.getUser());
        } else {
            msg = NLS.bind(RestClientI18n.deleteServerDialogMultipleServersMsg, this.serversBeingDeleted.size());
        }

        this.message = msg;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        if (this.serversBeingDeleted.size() != 1) {
            List serverList = new List(parent, SWT.NONE);
            serverList.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
            gd.horizontalIndent = 40;
            serverList.setLayoutData(gd);

            for (ModeShapeServer server : this.serversBeingDeleted) {
                serverList.add(server.getName());
            }
        }

        return null;
    }

}
