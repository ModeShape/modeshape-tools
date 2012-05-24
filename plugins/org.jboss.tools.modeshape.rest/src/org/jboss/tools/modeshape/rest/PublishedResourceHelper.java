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
package org.jboss.tools.modeshape.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.rest.domain.ModeShapeRepository;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;
import org.modeshape.common.util.CheckArg;
import org.modeshape.common.util.HashCode;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * The <code>PublishedResourceHelper</code> knows how to get and set the property on a resource that indicates it has been
 * published to one or more workspaces.
 */
public final class PublishedResourceHelper {

    /**
     * Delimiter between a workspace's properties.
     */
    private static final String ID_DELIM = "$"; //$NON-NLS-1$

    /**
     * Delimiter between workspaces.
     */
    private static final String DELIM = "|"; //$NON-NLS-1$

    /**
     * The name of the persisted file property indicating if the resource has been published. This property will only exist if the
     * file has been published to at least one repository. The value of the property is a list of repository workspaces where this
     * file has been published.
     */
    private static QualifiedName PUBLISHED_RESOURCE_PROPERTY = new QualifiedName(IUiConstants.PLUGIN_ID, "publishedLocations"); //$NON-NLS-1$

    /**
     * The server manager used by the helper to obtain workspaces.
     */
    private final ServerManager serverManager;

    /**
     * @param serverManager the server manager used by this helper (never <code>null</code>)
     */
    public PublishedResourceHelper( ServerManager serverManager ) {
        CheckArg.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$
        this.serverManager = serverManager;
    }

    /**
     * @param file the file that was just published (never <code>null</code>)
     * @param workspace the workspace where the file was published (never <code>null</code>)
     * @param url the URL where the resource was published (never <code>null</code>)
     * @throws Exception if there is a problem setting the property
     */
    public void addPublishedProperty( IFile file,
                                      ModeShapeWorkspace workspace,
                                      String url ) throws Exception {
        CheckArg.isNotNull(file, "file"); //$NON-NLS-1$
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$
        CheckArg.isNotNull(url, "url"); //$NON-NLS-1$

        Set<WorkspaceLocation> workspaceLocations = getPublishedWorkspaceLocations(file);
        workspaceLocations.add(new WorkspaceLocation(workspace, url));

        // set new value
        setPublishedOnPropertyValue(file, workspaceLocations);
    }

    /**
     * @param workspaceLocations the workspace locations used to create the property value
     * @return the property value
     */
    private String createPublishedPropertyValue( Set<WorkspaceLocation> workspaceLocations ) {
        StringBuilder value = new StringBuilder();

        for (WorkspaceLocation workspaceLocation : workspaceLocations) {
            value.append(createWorkspaceLocationId(workspaceLocation)).append(DELIM);
        }

        return value.toString();
    }

    /**
     * @param workspaceLocation the workspace location whose identifier is being created
     * @return the ID
     */
    private String createWorkspaceLocationId( WorkspaceLocation workspaceLocation ) {
        ModeShapeWorkspace workspace = workspaceLocation.getWorkspace();

        StringBuilder result = new StringBuilder();
        result.append(workspace.getServer().getUrl()).append(ID_DELIM).append(workspace.getServer().getUser()).append(ID_DELIM);
        result.append(workspace.getRepository().getName()).append(ID_DELIM);
        result.append(workspace.getName()).append(ID_DELIM);
        result.append(workspaceLocation.getUrl());

        return result.toString();
    }

    /**
     * @param file the file whose published <code>WorkspaceLocation</code>s is being requested (never <code>null</code>)
     * @return the workspace locations (never <code>null</code>)
     * @throws Exception if there is a problem reading one of the file's persistent properties or a problem with the server
     *         manager
     */
    public Set<WorkspaceLocation> getPublishedWorkspaceLocations( IFile file ) throws Exception {
        CheckArg.isNotNull(file, "file"); //$NON-NLS-1$

        Set<WorkspaceLocation> publishedWorkspaceLocations = null;
        String value = file.getPersistentProperty(PUBLISHED_RESOURCE_PROPERTY);

        if (value == null) {
            publishedWorkspaceLocations = new HashSet<WorkspaceLocation>(1);
        } else {
            StringTokenizer wsTokenizer = new StringTokenizer(value, DELIM);
            publishedWorkspaceLocations = new HashSet<WorkspaceLocation>(wsTokenizer.countTokens());

            while (wsTokenizer.hasMoreTokens()) {
                StringTokenizer propsTokenizer = new StringTokenizer(wsTokenizer.nextToken(), ID_DELIM);

                PARSE_WORKSPACE: while (propsTokenizer.hasMoreTokens()) {
                    String url = propsTokenizer.nextToken();
                    String user = propsTokenizer.nextToken();

                    // find server
                    ModeShapeServer server = this.serverManager.findServer(url, user);

                    if ((server != null) && this.serverManager.ping(server).isOk()) {
                        Collection<ModeShapeRepository> repositories = this.serverManager.getRepositories(server);

                        // find repository
                        if (!repositories.isEmpty()) {
                            String repositoryName = propsTokenizer.nextToken();

                            for (ModeShapeRepository repository : repositories) {
                                if (repository.getName().equals(repositoryName)) {
                                    Collection<ModeShapeWorkspace> workspaces = this.serverManager.getWorkspaces(repository);

                                    // find workspace
                                    if (!workspaces.isEmpty()) {
                                        String workspaceName = propsTokenizer.nextToken();

                                        for (ModeShapeWorkspace workspace : workspaces) {
                                            if (workspace.getName().equals(workspaceName)) {
                                                // get URL
                                                String publishedUrl = propsTokenizer.nextToken();
                                                publishedWorkspaceLocations.add(new WorkspaceLocation(workspace, publishedUrl));
                                                break PARSE_WORKSPACE;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                        // this will remove workspace as being one that the file has been published on
                        break PARSE_WORKSPACE;
                    }
                }
            }
        }

        return publishedWorkspaceLocations;
    }

    /**
     * @param file the file whose published status is being requested (never <code>null</code>)
     * @return <code>true</code> if the file has been published to a repository
     */
    public boolean isPublished( IFile file ) {
        CheckArg.isNotNull(file, "file"); //$NON-NLS-1$

        try {
            return !getPublishedWorkspaceLocations(file).isEmpty();
        } catch (Exception e) {
            Activator.getDefault().log(new Status(Severity.ERROR,
                                                  NLS.bind(RestClientI18n.publishedResourcePropertyErrorMsg, file),
                                                  e));
        }

        return false;
    }

    /**
     * @param file the file that was just unpublished (never <code>null</code>)
     * @param workspace the workspace where the file was unpublished (never <code>null</code>)
     * @param url the URL where the resource was unpublished (never <code>null</code>)
     * @throws Exception if there is a problem changing the property value
     */
    public void removePublishedProperty( IFile file,
                                         ModeShapeWorkspace workspace,
                                         String url ) throws Exception {
        CheckArg.isNotNull(file, "file"); //$NON-NLS-1$
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$
        CheckArg.isNotNull(url, "url"); //$NON-NLS-1$

        Set<WorkspaceLocation> workspaceLocations = getPublishedWorkspaceLocations(file);
        workspaceLocations.remove(new WorkspaceLocation(workspace, url));

        // set new value
        setPublishedOnPropertyValue(file, workspaceLocations);
    }

    /**
     * @param file the file whose property is being cleared
     * @throws CoreException if there was a problem removing the property
     */
    public void clear( IFile file ) throws CoreException {
        file.setPersistentProperty(PUBLISHED_RESOURCE_PROPERTY, null);
    }

    /**
     * @param file the file whose property is being set
     * @param workspaceLocations the workspace locations the file has been published to or <code>null</code> if the file has not
     *        been published
     * @throws CoreException if there was a problem changing the property value
     */
    private void setPublishedOnPropertyValue( IFile file,
                                              Set<WorkspaceLocation> workspaceLocations ) throws CoreException {
        if ((workspaceLocations == null) || workspaceLocations.isEmpty()) {
            clear(file);
        } else {
            String value = createPublishedPropertyValue(workspaceLocations);
            file.setPersistentProperty(PUBLISHED_RESOURCE_PROPERTY, value);
        }
    }

    /**
     * Holds the workspace and the URL where the file was published to.
     */
    public class WorkspaceLocation {

        private final ModeShapeWorkspace workspace;

        private final String url;

        public WorkspaceLocation( ModeShapeWorkspace workspace,
                                  String url ) {
            this.workspace = workspace;
            this.url = url;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {
            if (obj == null) return false;
            if (!getClass().equals(obj.getClass())) return false;

            WorkspaceLocation thatLocation = (WorkspaceLocation)obj;

            if (this.workspace.equals(thatLocation.workspace)) {
                if ((this.url == null) || this.url.equals("")) { //$NON-NLS-1$
                    return ((thatLocation.url == null) || thatLocation.url.equals("")); //$NON-NLS-1$
                }

                return this.url.equals(thatLocation.url);
            }

            return false;
        }

        /**
         * @return the repository where the workspace is located (never <code>null</code>)
         */
        public ModeShapeRepository getRepository() {
            return this.workspace.getRepository();
        }

        /**
         * @return the server where the workspace is located (never <code>null</code>)
         */
        public ModeShapeServer getServer() {
            return this.workspace.getServer();
        }

        /**
         * @return the workspace where published (never <code>null</code>)
         */
        public ModeShapeWorkspace getWorkspace() {
            return this.workspace;
        }

        /**
         * @return the URL where the file is published (never <code>null</code>)
         */
        public String getUrl() {
            return this.url;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return HashCode.compute(this.workspace, this.url);
        }

    }

}
