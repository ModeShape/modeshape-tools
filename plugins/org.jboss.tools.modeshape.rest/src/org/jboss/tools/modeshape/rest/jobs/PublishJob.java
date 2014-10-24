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
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.PublishedResourceHelper;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;
import org.jboss.tools.modeshape.rest.views.ModeShapeContentProvider;
import org.jboss.tools.modeshape.rest.views.ModeShapeMessageConsole;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.Status;

/**
 * The <code>PublishJob</code> publishes or unpublishes one or more files using the {@link ServerManager}.
 */
public final class PublishJob extends Job {

    /**
     * The job type.
     */
    public enum Type {
        /**
         * Indicates a publish job.
         */
        PUBLISH,

        /**
         * Indicates an unpublish job.
         */
        UNPUBLISH
    }

    /**
     * A unique job identifier given to each publishing/unpublishing job.
     */
    private static final AtomicInteger JOB_ID = new AtomicInteger();

    /**
     * @param type the job type (never <code>null</code>)
     * @param jobId the job identifier
     * @return the job name
     */
    private static String getJobName( Type type,
                                      int jobId ) {
        CheckArg.isNotNull(type, "type"); //$NON-NLS-1$

        if (Type.PUBLISH == type) {
            return NLS.bind(RestClientI18n.publishJobPublishName, jobId);
        }

        // unpublish
        return NLS.bind(RestClientI18n.publishJobUnpublishName, jobId);
    }

    /**
     * The files being published or unpublished.
     */
    private final List<IFile> files;

    /**
     * Indicates if the published path should include the Eclipse workspace project.
     */
    private boolean includeProjectPath = false;

    /**
     * The unique job identifier.
     */
    private final int jobId;

    /**
     * The job type.
     */
    private final Type type;

    /**
     * Indicates if published resources should be versioned on the ModeShape server.
     */
    private final boolean version;

    /**
     * The workspace to use when publishing or unpublishing.
     */
    private final ModeShapeWorkspace workspace;

    /**
     * The path segment prepended to the resource project path (never <code>null</code> but can be empty)
     */
    private final String workspaceArea;

    /**
     * @param type the job type (never <code>null</code>)
     * @param files the files being published or unpublished (never <code>null</code>)
     * @param workspace the workspace to use when publishing or unpublishing (never <code>null</code>)
     * @param workspaceArea the path segment prepended to the file project path (maybe be <code>null</code> or empty)
     * @param includeProjectPath <true> if the Eclipse project should be included in the published path
     * @param version <true> if published resources should be versioned
     */
    public PublishJob( Type type,
                       List<IFile> files,
                       ModeShapeWorkspace workspace,
                       String workspaceArea,
                       boolean includeProjectPath,
                       boolean version ) {
        super(getJobName(type, JOB_ID.incrementAndGet()));

        CheckArg.isNotNull(files, "files"); //$NON-NLS-1$
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$

        this.type = type;
        this.files = files;
        this.workspace = workspace;
        this.includeProjectPath = includeProjectPath;
        this.version = version;
        this.jobId = JOB_ID.get();

        // setup the workspace area and remove trailing separator if necessary
        String temp = ((workspaceArea == null) ? "" : workspaceArea); //$NON-NLS-1$

        if (temp.endsWith(File.separator)) {
            this.workspaceArea = temp.substring(0, (temp.length() - 1));
        } else {
            this.workspaceArea = temp;
        }

        setUser(true); // allow user to run in background
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
     * @return the server manager
     */
    private ServerManager getServerManager() {
        return Activator.getDefault().getServerManager();
    }

    /**
     * @return <code>true</code> if a publishing job
     */
    private boolean isPublishing() {
        return (this.type == Type.PUBLISH);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
        assert (this.workspace != null);
        long startTime = System.currentTimeMillis();
        boolean canceled = false;
        int numProcessed = 0;

        try {
            int fileCount = this.files.size();
            String name = (isPublishing() ? NLS.bind(RestClientI18n.publishJobPublishTaskName, this.jobId)
                                         : NLS.bind(RestClientI18n.publishJobUnpublishTaskName, this.jobId));
            monitor.beginTask(name, fileCount);
            monitor.setTaskName(name);

            String serverUrl = this.workspace.getServer().getOriginalUrl();
            String repositoryName = this.workspace.getRepository().getName();
            String workspaceName = this.workspace.getName();

            // write initial message to console
            if (isPublishing()) {
                ModeShapeMessageConsole.writeln(NLS.bind(RestClientI18n.publishJobPublish, new Object[] { this.jobId, serverUrl,
                        repositoryName, workspaceName, fileCount, this.workspaceArea }));
            } else {
                ModeShapeMessageConsole.writeln(NLS.bind(RestClientI18n.publishJobUnpublish, new Object[] { this.jobId, serverUrl,
                        repositoryName, workspaceName, fileCount, this.workspaceArea }));
            }

            PublishedResourceHelper resourceHelper = new PublishedResourceHelper(getServerManager());

            // process the files
            for (IFile eclipseFile : this.files) {
                if (monitor.isCanceled()) {
                    canceled = true;
                    throw new InterruptedException(NLS.bind(RestClientI18n.publishJobCanceled, jobId));
                }

                File file = eclipseFile.getLocation().toFile();
                String path = this.workspaceArea;
                IPath parentPath = eclipseFile.getParent().getFullPath();

                if (this.includeProjectPath) {
                    path += parentPath;
                } else {
                    // strip off project which is first segment
                    path += '/' + parentPath.removeFirstSegments(1).toString();
                }

                URL url = getServerManager().getUrl(file, path, this.workspace);
                Status status = null;

                if (isPublishing()) {
                    status = getServerManager().publish(this.workspace, path, file, this.version);

                    // set persistent property on resource indicating it has been published
                    if (!status.isError()) {
                        resourceHelper.addPublishedProperty(eclipseFile, this.workspace, url.toString());
                    }
                } else {
                    status = getServerManager().unpublish(this.workspace, path, file);

                    // clear persistent property on resource indicating it has been unpublished
                    if (!status.isError()) {
                        resourceHelper.removePublishedProperty(eclipseFile, this.workspace, url.toString());
                    }
                }

                ++numProcessed;
                monitor.worked(1);

                // let decorator know publishing state has changed on this file
                ModeShapeContentProvider decorator = ModeShapeContentProvider.getDecorator();

                if (decorator != null) {
                    decorator.refresh(eclipseFile);
                }

                // write outcome message to console
                if (isPublishing() && status.isOk()) {
                    writeToConsole(eclipseFile, url, status);
                } else {
                    writeToConsole(eclipseFile, null, status);
                }
            }

            return org.eclipse.core.runtime.Status.OK_STATUS;
        } catch (Exception e) {
            String msg = null;

            if (e instanceof InterruptedException) {
                msg = e.getLocalizedMessage();
            } else {
                msg = RestClientI18n.publishJobUnexpectedErrorMsg;
            }

            return new org.eclipse.core.runtime.Status(IStatus.INFO, PLUGIN_ID, msg, e);
        } finally {
            monitor.done();

            // add operation completed message
            String duration;
            long milliseconds = (System.currentTimeMillis() - startTime);
            long hours = milliseconds / (1000 * 60 * 60);
            long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

            if (hours > 0) {
                duration = NLS.bind(RestClientI18n.publishJobDurationMsg, new Object[] {hours, minutes, seconds});
            } else if (minutes > 0) {
                duration = NLS.bind(RestClientI18n.publishJobDurationNoHoursMsg, minutes, seconds);
            } else if (seconds > 0) {
                duration = NLS.bind(RestClientI18n.publishJobDurationNoHoursNoMinutesMsg, seconds);
            } else {
                duration = RestClientI18n.publishJobDurationShortMsg;
            }

            if (canceled) {
                if (isPublishing()) {
                    ModeShapeMessageConsole.writeln(NLS.bind(RestClientI18n.publishJobPublishCanceledMsg, new Object[] {
                            this.jobId, numProcessed, this.files.size(), duration }));
                } else {
                    ModeShapeMessageConsole.writeln(NLS.bind(RestClientI18n.publishJobUnpublishCanceledMsg, new Object[] {
                            this.jobId, numProcessed, this.files.size(), duration }));
                }
            } else {
                if (isPublishing()) {
                    ModeShapeMessageConsole.writeln(NLS.bind(RestClientI18n.publishJobPublishFinishedMsg, this.jobId, duration));
                } else {
                    ModeShapeMessageConsole.writeln(NLS.bind(RestClientI18n.publishJobUnpublishFinishedMsg, this.jobId, duration));
                }
            }
        }
    }

    /**
     * Create a hyperlink in console.
     *
     * @param file the file involved in the publishing operation
     * @param url the destination file URL or <code>null</code>
     * @param status the status of the publishing operation
     */
    private void writeToConsole( final IFile file,
                                 URL url,
                                 Status status ) {
        String message = null;

        if (status.isOk()) {
            if (isPublishing()) {
                message = NLS.bind(RestClientI18n.publishJobPublishFile,
                                   new Object[] { this.jobId, file.getFullPath(), url.toString() });
            } else {
                message = NLS.bind(RestClientI18n.publishJobUnpublishFile, this.jobId, file.getFullPath());
            }
        } else if (status.isError()) {
            if (isPublishing()) {
                message = NLS.bind(RestClientI18n.publishJobPublishFileFailed, this.jobId, file.getFullPath());
            } else {
                message = NLS.bind(RestClientI18n.publishJobUnpublishFileFailed, this.jobId, file.getFullPath());
            }

            // log
            Activator.getDefault().log(status);
        } else if (status.isWarning()) {
            if (isPublishing()) {
                message = NLS.bind(RestClientI18n.publishJobPublishFileWarning, this.jobId, file.getFullPath());
            } else {
                message = NLS.bind(RestClientI18n.publishJobUnpublishFileWarning, this.jobId, file.getFullPath());
            }

            // log
            Activator.getDefault().log(status);
        } else {
            if (isPublishing()) {
                message = NLS.bind(RestClientI18n.publishJobPublishFileInfo, this.jobId, file.getFullPath());
            } else {
                message = NLS.bind(RestClientI18n.publishJobUnpublishFileInfo, this.jobId, file.getFullPath());
            }

            // log
            Activator.getDefault().log(status);
        }

        // write to console creating a hyperlink
        ModeShapeMessageConsole.writeln(message, file);
    }

}
