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

import static org.jboss.tools.modeshape.rest.IUiConstants.REFRESH_IMAGE;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.jboss.tools.modeshape.rest.jobs.ReconnectJob;

/**
 * The <code>ReconnectToServerAction</code> tries to reconnect to a selected server.
 */
public final class ReconnectToServerAction extends BaseSelectionListenerAction {

    /**
     * The server view tree viewer.
     */
    private final TreeViewer viewer;

    /**
     * @param viewer the server view tree viewer
     */
    public ReconnectToServerAction( TreeViewer viewer ) {
        super(RestClientI18n.serverReconnectActionText);
        setToolTipText(RestClientI18n.serverReconnectActionToolTip);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(REFRESH_IMAGE));
        setEnabled(false);

        this.viewer = viewer;
    }

    /**
     * @return the view's tree viewer
     */
    StructuredViewer getViewer() {
        return this.viewer;
    }

    /**
     * @param server the server being connected to
     */
    void refresh( final ModeShapeServer server ) {
        final Display display = this.viewer.getControl().getDisplay();

        if (!display.isDisposed()) {
            // make sure we are in the UI thread
            display.asyncExec(new Runnable() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    getViewer().refresh(server);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        final ModeShapeServer server = (ModeShapeServer)getStructuredSelection().getFirstElement();
        final ReconnectJob job = new ReconnectJob(server);

        // add listener so we can refresh tree
        job.addJobChangeListener(new JobChangeAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
             */
            @Override
            public void done( IJobChangeEvent event ) {
                refresh(server);
                job.removeJobChangeListener(this);
            }
        });

        // run job in own thread not in the UI thread
        Thread t = new Thread();
        t.run();
        job.setThread(t);
        job.schedule();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        return ((selection.size() == 1) && (selection.getFirstElement() instanceof ModeShapeServer));
    }

}
