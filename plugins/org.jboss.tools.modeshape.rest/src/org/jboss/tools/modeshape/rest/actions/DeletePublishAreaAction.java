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
package org.jboss.tools.modeshape.rest.actions;

import static org.jboss.tools.modeshape.rest.IUiConstants.DELETE_PUBLISH_AREA_IMAGE;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.domain.WorkspaceArea;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * The <code>DeletePublishAreaAction</code> removes a workspace path from being a publish area.
 */
public final class DeletePublishAreaAction extends BaseSelectionListenerAction {

    /**
     * The server manager used to remove publish areas.
     */
    private final ServerManager serverManager;

    /**
     * The publish area being removed (never <code>null</code>).
     */
    private WorkspaceArea publishArea;

    /**
     * The shell used to display the confirmation dialog.
     */
    private final Shell shell;

    /**
     * The ModeShape View tree viewer (can be <code>null</code> when ModeShape View is not being displayed).
     */
    private final TreeViewer viewer;

    /**
     * @param shell the parent shell used to display the confirmation dialog
     * @param serverManager the server manager to use when removing publish areas (cannot be <code>null</code>)
     * @param viewer the ModeShape View tree viewer (can be <code>null</code>)
     */
    public DeletePublishAreaAction( final Shell shell,
                                    final ServerManager serverManager,
                                    final TreeViewer viewer ) {
        super(RestClientI18n.unmarkPublishActionText);

        CheckArg.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$

        setToolTipText(RestClientI18n.unmarkPublishAreaActionToolTip);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(DELETE_PUBLISH_AREA_IMAGE));
        setEnabled(false);

        this.shell = shell;
        this.serverManager = serverManager;
        this.viewer = viewer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (MessageDialog.openConfirm(this.shell, RestClientI18n.deletePublishAreaDialogTitle, 
                                      NLS.bind(RestClientI18n.deletePublishAreaDialogMessage, this.publishArea.getName(),
                                               this.publishArea.getWorkspace().getName()))) {
            boolean errorOccurred = false;

            try {
                final Status status = this.serverManager.unmarkAsPublishArea(this.publishArea);

                if (status.isOk()) {
                    if (this.viewer != null) {
                        this.viewer.refresh(this.publishArea.getWorkspace());
                    }
                } else {
                    Activator.getDefault().log(status);

                    if (status.isError()) {
                        errorOccurred = true;
                    }
                }
            } catch (final Exception e) {
                Activator.getDefault().log(new Status(Severity.ERROR, NLS.bind(RestClientI18n.errorUnmarkingPublishArea,
                                                                               this.publishArea.getWorkspace().getName(),
                                                                               this.publishArea.getName()), e));
                errorOccurred = true;
            }

            if (errorOccurred) {
                MessageDialog.openError(this.shell,
                                        RestClientI18n.errorDialogTitle,
                                        RestClientI18n.unmarkPublishAreaDialogErrorsOccurredMsg);

            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        // reset publish area
        this.publishArea = null;

        // disable if empty or multi-selection
        if (selection.isEmpty() || (selection.size() > 1)) {
            return false;
        }

        // enable when selection is a workspace
        final Object obj = selection.getFirstElement();

        if (obj instanceof WorkspaceArea) {
            this.publishArea = (WorkspaceArea)obj;
            return true;
        }

        // disable since not a publish area
        return false;
    }

}
