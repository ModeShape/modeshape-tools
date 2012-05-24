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
package org.jboss.tools.modeshape.rest.jobs;

import static org.jboss.tools.modeshape.rest.IUiConstants.PLUGIN_ID;
import static org.jboss.tools.modeshape.rest.IUiConstants.PUBLISHING_JOB_FAMILY;
import static org.jboss.tools.modeshape.rest.RestClientI18n.reconnectJobTaskName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.modeshape.web.jcr.rest.client.Status;

/**
 * The <code>ReconnectJob</code> attempts to reconnect to the selected {@link ModeShapeServer server(s)}.
 */
public final class ReconnectJob extends Job {

    /**
     * The server being reconnected to.
     */
    private final ModeShapeServer server;

    /**
     * @param server the server being connected to (never <code>null</code>)
     */
    public ReconnectJob( ModeShapeServer server ) {
        super(NLS.bind(reconnectJobTaskName, server.getShortDescription()));
        this.server = server;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    @Override
    public boolean belongsTo( Object family ) {
        return PUBLISHING_JOB_FAMILY.equals(family);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
        ServerManager serverManager = Activator.getDefault().getServerManager();

        try {
            String taskName = NLS.bind(reconnectJobTaskName, this.server.getShortDescription());
            monitor.beginTask(taskName, 1);
            monitor.setTaskName(taskName);
            Status status = serverManager.ping(this.server);

            if (status.isError()) {
                Activator.getDefault().log(status);
            }
        } catch (Exception e) {
            Activator.getDefault().getLog().log(new org.eclipse.core.runtime.Status(IStatus.ERROR,
                                                         PLUGIN_ID,
                                                         RestClientI18n.publishJobUnexpectedErrorMsg,
                                                         e));
        } finally {
            monitor.done();
        }

        // if an error status is returned a dialog is displayed by the UI event loop so we don't want that
        return org.eclipse.core.runtime.Status.OK_STATUS;
    }

}
