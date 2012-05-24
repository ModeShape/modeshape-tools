/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.jboss.tools.modeshape.rest.actions;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.PublishedResourceHelper;
import org.jboss.tools.modeshape.rest.PublishedResourceHelper.WorkspaceLocation;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.dialogs.PublishedLocationsDialog;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * The <code>ShowPublishedLocationsHandler</code> displays a dialog that shows information on which ModeShape repositories a
 * {@link org.eclipse.core.resources.IFile file} has been published to.
 */
public final class ShowPublishedLocationsHandler extends AbstractHandler {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) {
        ISelection temp = HandlerUtil.getCurrentSelection(event);
        assert (temp != null) : "selection is null"; //$NON-NLS-1$
        assert (!temp.isEmpty()) : "selection is empty"; //$NON-NLS-1$
        assert (temp instanceof IStructuredSelection) : "selection is not an IStructuredSelection"; //$NON-NLS-1$

        IStructuredSelection selection = (IStructuredSelection)temp;
        assert (selection.size() == 1) : "selection size is greater than one"; //$NON-NLS-1$
        assert (selection.getFirstElement() instanceof IFile) : "selected element is not a file"; //$NON-NLS-1$

        // open dialog
        ServerManager serverManager = Activator.getDefault().getServerManager();
        PublishedResourceHelper resourceHelper = new PublishedResourceHelper(serverManager);
        Shell shell = HandlerUtil.getActiveShell(event);

        try {
            Set<WorkspaceLocation> workspaceLocations = resourceHelper.getPublishedWorkspaceLocations((IFile)selection.getFirstElement());
            PublishedLocationsDialog dialog = new PublishedLocationsDialog(shell, (IFile)selection.getFirstElement(),
                                                                           workspaceLocations);
            dialog.open();
        } catch (Exception e) {
            Activator.getDefault().log(new Status(Severity.ERROR, RestClientI18n.showPublishedLocationsErrorMsg, e));
            MessageDialog.openError(shell,
                                    RestClientI18n.errorDialogTitle,
                                    RestClientI18n.showPublishedLocationsErrorMsg);
        }

        // per javadoc must return null
        return null;
    }

}
