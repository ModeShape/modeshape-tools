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

import static org.jboss.tools.modeshape.rest.IUiConstants.NEW_PUBLISH_AREA_IMAGE;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.dialogs.AddPublishAreaDialog;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * The <code>AddPublishAreaAction</code> creates a new publishing area under a workspace.
 */
public final class AddPublishAreaAction extends BaseSelectionListenerAction {

    private boolean success;

    /**
     * The server manager used to create the publish area.
     */
    private final ServerManager serverManager;

    /**
     * The workspace where the publishing area is being created (<code>null</code> when a workspace is not selected).
     */
    private ModeShapeWorkspace workspace;

    /**
     * The shell used to display the dialog.
     */
    private final Shell shell;

    private final boolean updateOnSelection;

    /**
     * The ModeShape View tree viewer (can be <code>null</code> when ModeShape View is not being displayed).
     */
    private final TreeViewer viewer;

    private AddPublishAreaAction( final Shell shell,
                                  final ServerManager serverManager,
                                  final TreeViewer viewer,
                                  final boolean updateOnSelection ) {
        super(RestClientI18n.addPublishAreaActionText);

        CheckArg.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$

        setToolTipText(RestClientI18n.addPublishAreaActionToolTip);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(NEW_PUBLISH_AREA_IMAGE));
        setEnabled(false);

        this.shell = shell;
        this.serverManager = serverManager;
        this.viewer = viewer;
        this.updateOnSelection = updateOnSelection;
    }

    /**
     * Constructs and action whose enablement is determined by the current workbench selection.
     * 
     * @param shell the parent shell used to display the confirmation dialog
     * @param serverManager the server manager used to add the publishing area (cannot be <code>null</code>)
     * @param viewer the ModeShape View tree viewer (can be <code>null</code>)
     */
    public AddPublishAreaAction( final Shell shell,
                                 final ServerManager serverManager,
                                 final TreeViewer viewer ) {
        this(shell, serverManager, viewer, true);
    }

    /**
     * Constructs an action that does not care about workspace selection and is always enabled.
     * 
     * @param shell the parent shell used to display the confirmation dialog
     * @param serverManager the server manager used to add the publishing area (cannot be <code>null</code>)
     * @param workspace the workspace where the publish area is being created (cannot be <code>null</code>)
     * @param viewer the ModeShape View tree viewer (can be <code>null</code>)
     */
    public AddPublishAreaAction( final Shell shell,
                                 final ServerManager serverManager,
                                 final ModeShapeWorkspace workspace,
                                 final TreeViewer viewer ) {
        this(shell, serverManager, viewer, false);

        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$

        setEnabled(true);
        this.workspace = workspace;
    }

    /**
     * @return <code>true</code> if the action ran successfully
     */
    public boolean success() {
        return this.success;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        this.success = false;
        final AddPublishAreaDialog dialog = new AddPublishAreaDialog(this.shell, this.workspace);

        if (dialog.open() == Window.OK) {
            boolean errorOccurred = false;
            String publishArea = dialog.publishArea();

            // make sure a leading path segment separator
            if (publishArea.charAt(0) != '/') {
                publishArea = '/' + publishArea;
            }

            try {
                final Status status = this.serverManager.markAsPublishArea(this.workspace, publishArea);

                if (status.isOk()) {
                    if (this.viewer != null) {
                        final TreeViewer finalViewer = this.viewer;
                        final ModeShapeWorkspace finalWorkspace = this.workspace;

                        this.viewer.getControl().getDisplay().asyncExec(new Runnable() {

                            /**
                             * {@inheritDoc}
                             * 
                             * @see java.lang.Runnable#run()
                             */
                            @Override
                            public void run() {
                                finalViewer.refresh(finalWorkspace);
                            }
                        });
                    }
                } else {
                    Activator.getDefault().log(status);

                    if (status.isError()) {
                        errorOccurred = true;
                    }
                }
            } catch (final Exception e) {
                Activator.getDefault().log(new Status(Severity.ERROR, NLS.bind(RestClientI18n.errorMarkingPublishArea,
                                                                               this.workspace.getName(),
                                                                               publishArea), e));
                errorOccurred = true;
            }

            if (errorOccurred) {
                MessageDialog.openError(this.shell,
                                        RestClientI18n.errorDialogTitle,
                                        RestClientI18n.addPublishAreaDialogErrorsOccurredMsg);

            } else {
                this.success = true;
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
        if (this.updateOnSelection) {
            // reset workspace
            this.workspace = null;

            // disable if empty or multi-selection
            if (selection.isEmpty() || (selection.size() > 1)) {
                return false;
            }

            // enable when selection is a workspace
            final Object obj = selection.getFirstElement();

            if (obj instanceof ModeShapeWorkspace) {
                this.workspace = (ModeShapeWorkspace)obj;
                return true;
            }

            // disable since not a workspace
            return false;
        }

        return isEnabled();
    }

}
