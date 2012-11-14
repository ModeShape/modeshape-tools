/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tools.modeshape.jcr.ui.resources;

import static org.jboss.tools.modeshape.jcr.ui.JcrUiConstants.PLUGIN_ID;
import static org.jboss.tools.modeshape.jcr.ui.JcrUiConstants.WorkspaceIds.CND_PROBLEM_MARKER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.jboss.tools.modeshape.jcr.MultiValidationStatus;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.cnd.CndImporter;
import org.jboss.tools.modeshape.jcr.cnd.CndValidator;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiUtils;
import org.jboss.tools.modeshape.jcr.ui.cnd.CndMessages;

/**
 * A project builder that creates markers for CND file (*.cnd) problems.
 */
public final class CndProjectBuilder extends IncrementalProjectBuilder {

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] build( final int kind,
                                final Map<String, String> args,
                                final IProgressMonitor monitor ) throws CoreException {
        final IProject project = getProject();

        // don't do anything if project is closed or doesn't exist
        if ((project == null) || !project.isAccessible()) {
            return null;
        }

        final CndVisitor visitor = new CndVisitor();

        if ((IncrementalProjectBuilder.FULL_BUILD == kind) || (getDelta(project) == null)) {
            getProject().accept(visitor); // gather all MEDs in project
        } else {
            final IResourceDelta delta = getDelta(project);
            delta.accept(visitor); // gather MEDs that have changed since last build
        }

        // collect the CND files we need to build
        final Collection<IFile> cndFilesToBuild = visitor.getCndFiles();
        monitor.beginTask(CndMessages.cndBuildTaskName, cndFilesToBuild.size());

        if (!cndFilesToBuild.isEmpty()) {
            final MultiStatus status = new MultiStatus(PLUGIN_ID, IStatus.OK, null, null);

            for (final IFile cndFile : cndFilesToBuild) {
                monitor.subTask(NLS.bind(CndMessages.cndBuildSubTaskName, cndFile.getName()));

                try {
                    // clear existing markers if not already done by the clean build
                    cndFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);

                    final List<Throwable> errors = new ArrayList<Throwable>();
                    final CompactNodeTypeDefinition cnd = new CndImporter().importFrom(cndFile.getContents(),
                                                                                       errors,
                                                                                       cndFile.getName());

                    if (errors.isEmpty()) {
                        createMarkers(cndFile, CndValidator.validateCnd(cnd));
                    } else {
                        createMarkers(cndFile, errors);
                    }
                } catch (final Exception e) {
                    final IStatus cndStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(CndMessages.cndFileParseError,
                                                                                            cndFile.getName()), e);
                    status.add(cndStatus);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }

            if (!status.isOK()) {
                Activator.getSharedInstance().getLog().log(status);
            }
        }

        // no other projects need also be rebuilt because this project was built
        return null;
    }

    /**
     * @param cndFile the CND file associated with the problem marker (cannot be <code>null</code>)
     * @param severity the marker severity
     * @param message the marker message (cannot be <code>null</code> or empty)
     */
    @SuppressWarnings( "unchecked" )
    private void createMarker( final IFile cndFile,
                               final int severity,
                               final String message ) {
        assert (cndFile != null) : "file is null"; //$NON-NLS-1$
        assert ((message != null) && !message.isEmpty()) : "message is empty"; //$NON-NLS-1$

        @SuppressWarnings( "rawtypes" )
        final Map attributes = new HashMap();
        attributes.put(IMarker.SEVERITY, severity);
        attributes.put(IMarker.MESSAGE, message);

        try {
            MarkerUtilities.createMarker(cndFile, attributes, CND_PROBLEM_MARKER);
        } catch (final CoreException e) {
            Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, null, e));
        }
    }

    /**
     * @param cndFile the CND file associated with the problem markers being created (cannot be <code>null</code>)
     * @param errors the errors being used to create the problem markers (can be <code>null</code> or empty)
     */
    private void createMarkers( final IFile cndFile,
                                final List<Throwable> errors ) {
        assert (cndFile != null) : "file is null"; //$NON-NLS-1$

        if ((errors == null) || errors.isEmpty()) {
            return;
        }

        for (final Throwable t : errors) {
            createMarker(cndFile, IMarker.SEVERITY_ERROR, t.getLocalizedMessage());
        }
    }

    /**
     * @param cndFile the CND file associated with the problem markers being created (cannot be <code>null</code>)
     * @param status the status used to create the problem markers (can be <code>null</code>)
     */
    private void createMarkers( final IFile cndFile,
                                final MultiValidationStatus status ) {
        if ((status == null) || status.isOk()) {
            return;
        }

        for (final ValidationStatus validationStatus : status.getAll()) {
            if (!validationStatus.isOk()) {
                if (validationStatus instanceof MultiValidationStatus) {
                    createMarkers(cndFile, (MultiValidationStatus)validationStatus);
                } else {
                    createMarker(cndFile, JcrUiUtils.getMarkerSeverity(validationStatus), validationStatus.getMessage());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected void clean( final IProgressMonitor monitor ) throws CoreException {
        final CndVisitor visitor = new CndVisitor();
        getProject().accept(visitor); // gather all CNDs in project

        try {
            final Collection<IFile> cndFilesToClean = visitor.getCndFiles();
            monitor.beginTask(CndMessages.cndCleanTaskName, cndFilesToClean.size());

            // clean all CND problem markers
            for (final IFile cndFile : cndFilesToClean) {
                try {
                    monitor.subTask(NLS.bind(CndMessages.cndCleanSubTaskName, cndFile.getName()));
                    cndFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }
        } finally {
            monitor.done();
        }
    }

    /**
     * The <code>CndVisitor</code> gathers CND files that need their problem markers refreshed. A new visitor must be constructed
     * for each build.
     */
    class CndVisitor implements IResourceDeltaVisitor, IResourceVisitor {

        private final Collection<IFile> cndFiles = new ArrayList<IFile>();

        /**
         * @return the CND files whose problem markers need to be refreshed (never <code>null</code>)
         */
        public Collection<IFile> getCndFiles() {
            return this.cndFiles;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
         */
        @Override
        public boolean visit( final IResource resource ) {
            if (JcrUiUtils.isCndFile(resource)) {
                this.cndFiles.add((IFile)resource);
            }

            return true; // visit resource's members
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        @Override
        public boolean visit( final IResourceDelta delta ) {
            final IResource resource = delta.getResource();

            if (JcrUiUtils.isCndFile(resource)) {
                this.cndFiles.add((IFile)resource);
            }

            return true; // visit delta's children
        }
    }
}
