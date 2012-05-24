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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.rest.domain.ModeShapeRepository;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;
import org.jboss.tools.modeshape.rest.domain.WorkspaceArea;
import org.modeshape.common.util.Base64;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.IJcrConstants;
import org.modeshape.web.jcr.rest.client.IRestClient;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;
import org.modeshape.web.jcr.rest.client.domain.QueryRow;
import org.modeshape.web.jcr.rest.client.domain.Repository;
import org.modeshape.web.jcr.rest.client.domain.Workspace;
import org.modeshape.web.jcr.rest.client.json.JsonRestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>ServerManager</code> class manages the creation, deletion, and editing of servers hosting ModeShape repositories.
 */
@ThreadSafe
public final class ServerManager {

    /**
     * The tag used to persist a server's login password.
     */
    private static final String PASSWORD_TAG = "password"; //$NON-NLS-1$

    /**
     * The file name used when persisting the server registry.
     */
    private static final String REGISTRY_FILE = "serverRegistry.xml"; //$NON-NLS-1$

    /**
     * The name of the system property that is set when at least one server exists. This property is used by the menus extension
     * that is responsible for showing the ModeShape context menu items.
     */
    public static final String SERVER_EXISTS_PROPERTY = "org.jboss.tools.modeshape.rest.serverExists"; //$NON-NLS-1$

    /**
     * The tag used when persisting a server.
     */
    private static final String SERVER_TAG = "server"; //$NON-NLS-1$

    /**
     * The server collection tag used when persisting the server registry.
     */
    private static final String SERVERS_TAG = "servers"; //$NON-NLS-1$

    /**
     * The tag used to persist a server's URL.
     */
    private static final String URL_TAG = "url"; //$NON-NLS-1$

    /**
     * The tag used to persist a server's login user.
     */
    private static final String USER_TAG = "user"; //$NON-NLS-1$

    /**
     * The listeners registered to receive {@link ServerRegistryEvent server registry events}.
     */
    private final CopyOnWriteArrayList<IServerRegistryListener> listeners;

    /**
     * Executes the commands run on the ModeShape REST server.
     */
    private final IRestClient delegate;

    /**
     * The path where the server registry is persisted or <code>null</code> if not persisted.
     */
    private final String stateLocationPath;

    /**
     * The server registry.
     */
    @GuardedBy("serverLock")
    private final List<ModeShapeServer> servers;

    /**
     * Lock used for when accessing the server registry.
     */
    private final ReadWriteLock serverLock = new ReentrantReadWriteLock();

    /**
     * @param stateLocationPath the directory where the {@link ModeShapeServer} registry} is persisted (may be <code>null</code> if
     *            persistence is not desired)
     * @param restClient the client that will communicate with the REST server (never <code>null</code>)
     */
    public ServerManager( String stateLocationPath,
                          IRestClient restClient ) {
        CheckArg.isNotNull(restClient, "restClient"); //$NON-NLS-1$

        this.servers = new ArrayList<ModeShapeServer>();
        this.stateLocationPath = stateLocationPath;
        this.delegate = restClient;
        this.listeners = new CopyOnWriteArrayList<IServerRegistryListener>();
    }

    /**
     * This server manager uses the default REST Client.
     * 
     * @param stateLocationPath the directory where the {@link ModeShapeServer} registry is persisted (may be <code>null</code> if
     *            persistence is not desired)
     */
    public ServerManager( String stateLocationPath ) {
        this(stateLocationPath, new JsonRestClient());
    }

    /**
     * Listeners already registered will not be added again. The new listener will receive events for all existing servers.
     * 
     * @param listener the listener being register to receive events (never <code>null</code>)
     * @return <code>true</code> if listener was added
     */
    public boolean addRegistryListener( IServerRegistryListener listener ) {
        CheckArg.isNotNull(listener, "listener"); //$NON-NLS-1$
        boolean result = this.listeners.addIfAbsent(listener);

        // inform new listener of registered servers
        for (ModeShapeServer server : getServers()) {
            listener.serverRegistryChanged(ServerRegistryEvent.createNewEvent(this, server));
        }

        return result;
    }

    /**
     * Registers the specified <code>ModeShapeServer</code>.
     * 
     * @param server the server being added (never <code>null</code>)
     * @return a status indicating if the server was added to the registry
     */
    public Status addServer( ModeShapeServer server ) {
        CheckArg.isNotNull(server, "server"); //$NON-NLS-1$
        return internalAddServer(server, true);
    }

    /**
     * @param url the URL of the server being requested (never <code>null</code> )
     * @param user the user ID of the server being requested (never <code>null</code>)
     * @return the requested server or <code>null</code> if not found in the registry
     */
    public ModeShapeServer findServer( String url,
                                       String user ) {
        CheckArg.isNotNull(url, "url"); //$NON-NLS-1$
        CheckArg.isNotNull(user, "user"); //$NON-NLS-1$

        for (ModeShapeServer server : getServers()) {
            if (url.equals(server.getUrl()) && user.equals(server.getUser())) {
                return server;
            }
        }

        return null;
    }

    /**
     * @return an unmodifiable collection of registered servers (never <code>null</code>)
     */
    public Collection<ModeShapeServer> getServers() {
        try {
            this.serverLock.readLock().lock();
            return Collections.unmodifiableCollection(new ArrayList<ModeShapeServer>(this.servers));
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * @return the name of the state file that the server registry is persisted to or <code>null</code>
     */
    private String getStateFileName() {
        String name = this.stateLocationPath;

        if (this.stateLocationPath != null) {
            name += File.separatorChar + REGISTRY_FILE;
        }

        return name;
    }

    /**
     * @param server the server whose repositories are being requested (cannot be <code>null</code>)
     * @return the repositories (never <code>null</code>)
     * @throws Exception if server is not registered or if there is an unexpected problem obtaining the repositories
     */
    public Collection<ModeShapeRepository> getRepositories( ModeShapeServer server ) throws Exception {
        CheckArg.isNotNull(server, "server"); //$NON-NLS-1$

        try {
            this.serverLock.readLock().lock();

            if (isRegistered(server)) {
                // need to wrap each repository
                Collection<Repository> repositories = this.delegate.getRepositories(server.getDelegate());
                Collection<ModeShapeRepository> result = new ArrayList<ModeShapeRepository>(repositories.size());

                for (Repository repository : repositories) {
                    result.add(new ModeShapeRepository(repository, server));
                }

                return Collections.unmodifiableCollection(result);
            }

            // server must be registered in order to obtain it's repositories
            throw new RuntimeException(NLS.bind(RestClientI18n.serverManagerUnregisteredServer, server.getShortDescription()));
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * @param file the file whose URL is being requested (cannot be <code>null</code>)
     * @param path the repository path where the file path should start from
     * @param workspace the workspace where the file is published (cannot be <code>null</code>)
     * @return the URL where the file was published
     * @throws Exception if there is a problem obtaining the URL
     */
    public URL getUrl( File file,
                       String path,
                       ModeShapeWorkspace workspace ) throws Exception {
        return this.delegate.getUrl(file, path, workspace.getDelegate());
    }

    /**
     * @param workspace the workspace whose publishing areas are being requested
     * @return the workspace areas (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the workspace areas
     */
    public WorkspaceArea[] getWorkspaceAreas( ModeShapeWorkspace workspace ) throws Exception {
        final String path = "jcr:path"; //$NON-NLS-1$
        final String title = "jcr:title"; //$NON-NLS-1$
        final String statement = "SELECT [" + path + "], " + '[' + title + ']' + " FROM [mode:publishArea]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        List<QueryRow> rows = this.delegate.query(workspace.getDelegate(), IJcrConstants.JCR_SQL2, statement);
        WorkspaceArea[] workspaceAreas = new WorkspaceArea[rows.size()];

        for (int numRows = rows.size(), i = 0; i < numRows; ++i) {
            QueryRow row = rows.get(i);
            workspaceAreas[i] = new WorkspaceArea(workspace, (String)row.getValue(path), (String)row.getValue(title));
        }

        return workspaceAreas;
    }

    /**
     * @param repository the repository whose workspaces are being requested (cannot be <code>null</code>)
     * @return the workspaces (never <code>null</code>)
     * @throws Exception if server is not registered or if there is an unexpected problem obtaining the workspaces
     */
    public Collection<ModeShapeWorkspace> getWorkspaces( ModeShapeRepository repository ) throws Exception {
        CheckArg.isNotNull(repository, "repository"); //$NON-NLS-1$

        try {
            this.serverLock.readLock().lock();

            if (isRegistered(repository.getServer())) {
                // wrap workspaces
                Collection<Workspace> workspaces = this.delegate.getWorkspaces(repository.getDelegate());
                Collection<ModeShapeWorkspace> result = new ArrayList<ModeShapeWorkspace>(workspaces.size());

                for (Workspace workspace : workspaces) {
                    result.add(new ModeShapeWorkspace(workspace, repository));
                }

                return Collections.unmodifiableCollection(result);
            }

            // a repository's server must be registered in order to obtain it's workspaces
            String msg = NLS.bind(RestClientI18n.serverManagerUnregisteredServer, repository.getServer().getShortDescription());
            throw new RuntimeException(msg);
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * Registers the specified <code>Server</code>.
     * 
     * @param server the server being added
     * @param notifyListeners indicates if registry listeners should be notified
     * @return a status indicating if the server was added to the registry
     */
    private Status internalAddServer( ModeShapeServer server,
                                      boolean notifyListeners ) {
        boolean added = false;

        try {
            this.serverLock.writeLock().lock();

            if (!isRegistered(server)) {
                added = this.servers.add(server);
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (added) {
            // set system property if not set
            String serverExists = System.getProperty(SERVER_EXISTS_PROPERTY);

            if (serverExists == null) {
                // value of "true" is coded in the plugin.xml as well
                System.setProperty(SERVER_EXISTS_PROPERTY, "true"); //$NON-NLS-1$
            }

            // let listeners know of new server
            if (notifyListeners) {
                Exception[] errors = notifyRegistryListeners(ServerRegistryEvent.createNewEvent(this, server));
                return processRegistryListenerErrors(errors);
            }

            return Status.OK_STATUS;
        }

        // server already exists
        return new Status(Severity.ERROR, NLS.bind(RestClientI18n.serverExistsMsg, server.getShortDescription()), null);
    }

    /**
     * @param server the server being removed
     * @param notifyListeners indicates if registry listeners should be notified
     * @return a status indicating if the specified server was removed from the registry
     */
    private Status internalRemoveServer( ModeShapeServer server,
                                         boolean notifyListeners ) {
        boolean removed = false;

        try {
            this.serverLock.writeLock().lock();

            // see if registered server has the same key
            for (ModeShapeServer registeredServer : this.servers) {
                if (registeredServer.hasSameKey(server)) {
                    removed = this.servers.remove(registeredServer);
                    break;
                }
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (removed) {
            // remove system property if no more servers
            if (getServers().isEmpty()) {
                System.clearProperty(SERVER_EXISTS_PROPERTY);
            }

            // let listeners know of new server
            if (notifyListeners) {
                Exception[] errors = notifyRegistryListeners(ServerRegistryEvent.createRemoveEvent(this, server));
                return processRegistryListenerErrors(errors);
            }

            return Status.OK_STATUS;
        }

        // server could not be removed
        return new Status(Severity.ERROR,
                          NLS.bind(RestClientI18n.serverManagerRegistryRemoveUnexpectedError, server.getShortDescription()),
                          null);
    }

    /**
     * @param server the server being tested (never <code>null</code>)
     * @return <code>true</code> if the server has been registered
     */
    public boolean isRegistered( ModeShapeServer server ) {
        CheckArg.isNotNull(server, "server"); //$NON-NLS-1$

        try {
            this.serverLock.readLock().lock();

            // check to make sure no other registered server has the same key
            for (ModeShapeServer registeredServer : this.servers) {
                if (registeredServer.hasSameKey(server)) {
                    return true;
                }
            }

            return false;
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * @param event the event the registry listeners are to process
     * @return any errors thrown by or found by the listeners or <code>null</code> (never empty)
     */
    private Exception[] notifyRegistryListeners( ServerRegistryEvent event ) {
        Collection<Exception> errors = null;

        for (IServerRegistryListener l : this.listeners) {
            try {
                Exception[] problems = l.serverRegistryChanged(event);

                if ((problems != null) && (problems.length != 0)) {
                    if (errors == null) {
                        errors = new ArrayList<Exception>();
                    }

                    errors.addAll(Arrays.asList(problems));
                }
            } catch (Exception e) {
                if (errors == null) {
                    errors = new ArrayList<Exception>();
                }

                errors.add(e);
            }
        }

        if ((errors != null) && !errors.isEmpty()) {
            return errors.toArray(new Exception[errors.size()]);
        }

        return null;
    }

    /**
     * @param errors the errors reported by the registry listeners
     * @return a status indicating if registry listeners reported any errors
     */
    private Status processRegistryListenerErrors( Exception[] errors ) {
        if (errors == null) {
            return Status.OK_STATUS;
        }

        ILog logger = Activator.getDefault().getLog();

        for (Exception error : errors) {
            logger.log(new org.eclipse.core.runtime.Status(IStatus.ERROR,
                                                           IUiConstants.PLUGIN_ID,
                                                           RestClientI18n.serverManagerRegistryListenerError,
                                                           error));
        }

        return new Status(Severity.WARNING, RestClientI18n.serverManagerRegistryListenerErrorsOccurred, null);
    }

    /**
     * Attempts to connect to the server. The server does <strong>NOT</strong> need to be registered.
     * 
     * @param server the server being pinged (never <code>null</code>)
     * @return a status indicating if the server can be connected to
     * @see #isRegistered(ModeShapeServer)
     */
    public Status ping( ModeShapeServer server ) {
        CheckArg.isNotNull(server, "server"); //$NON-NLS-1$

        try {
            this.delegate.getRepositories(server.getDelegate());
            return new Status(Severity.OK, RestClientI18n.serverManagerConnectionEstablishedMsg, null);
        } catch (Exception e) {
            return new Status(Severity.ERROR, NLS.bind(RestClientI18n.serverManagerConnectionFailedMsg, e), null);
        }
    }

    /**
     * @param workspace the workspace where the file should be published (cannot be <code>null</code>)
     * @param path the starting path in the repository (cannot be <code>null</code>)
     * @param file the file being published (cannot be <code>null</code>)
     * @param version <code>true</code> if the file should be put under version control by ModeShape
     * @return the status of the outcome of this publishing operation
     */
    public Status publish( ModeShapeWorkspace workspace,
                           String path,
                           File file,
                           boolean version ) {
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$
        CheckArg.isNotNull(path, "path"); //$NON-NLS-1$
        CheckArg.isNotNull(file, "file"); //$NON-NLS-1$

        ModeShapeServer server = workspace.getServer();

        if (isRegistered(server)) {
            if (version) {
                return this.delegate.publish(workspace.getDelegate(), path, file, true);
            }

            // If version is false it could mean that versioning is not supported by the repository, or it is not enabled by the
            // by the repository, or that the user does not want the file versioned. If repository is running on an older server
            // that did not have versioning, then the only publishing method available on the server was the publish method without
            // the version parameter.
            return this.delegate.publish(workspace.getDelegate(), path, file);
        }

        // server must be registered in order to publish
        throw new RuntimeException(NLS.bind(RestClientI18n.serverManagerUnregisteredServer, server.getShortDescription()));
    }

    /**
     * @param listener the listener being unregistered and will no longer receive events (never <code>null</code>)
     * @return <code>true</code> if listener was removed
     */
    public boolean removeRegistryListener( IServerRegistryListener listener ) {
        CheckArg.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * @param server the server being removed (never <code>null</code>)
     * @return a status indicating if the specified server was removed from the registry (never <code>null</code>)
     */
    public Status removeServer( ModeShapeServer server ) {
        CheckArg.isNotNull(server, "server"); //$NON-NLS-1$
        return internalRemoveServer(server, true);
    }

    /**
     * @return a status indicating if the previous session state was restored successfully
     */
    public Status restoreState() {
        if (this.stateLocationPath != null) {
            if (stateFileExists()) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    Document doc = docBuilder.parse(new File(getStateFileName()));
                    Node root = doc.getDocumentElement();
                    NodeList servers = root.getChildNodes();

                    for (int size = servers.getLength(), i = 0; i < size; ++i) {
                        Node server = servers.item(i);

                        if (server.getNodeType() != Node.TEXT_NODE) {
                            NamedNodeMap attributeMap = server.getAttributes();

                            if (attributeMap == null)
                                continue;

                            Node urlNode = attributeMap.getNamedItem(URL_TAG);
                            Node userNode = attributeMap.getNamedItem(USER_TAG);
                            Node passwordNode = attributeMap.getNamedItem(PASSWORD_TAG);
                            String pswd = ((passwordNode == null) ? null : new String(Base64.decode(passwordNode.getNodeValue()),
                                                                                      "UTF-8")); //$NON-NLS-1$

                            // add server to registry
                            addServer(new ModeShapeServer(urlNode.getNodeValue(), userNode.getNodeValue(), pswd, (pswd != null)));
                        }
                    }
                } catch (Exception e) {
                    return new Status(Severity.ERROR, NLS.bind(RestClientI18n.errorRestoringServerRegistry, getStateFileName()), e);
                }
            }
        }

        // do nothing of there is no save location or state file does not exist
        return Status.OK_STATUS;
    }

    /**
     * Saves the {@link ModeShapeServer} registry to the file system.
     * 
     * @return a status indicating if the registry was successfully saved
     */
    public Status saveState() {
        if ((this.stateLocationPath != null) && !getServers().isEmpty()) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = factory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();

                // create root element
                Element root = doc.createElement(SERVERS_TAG);
                doc.appendChild(root);

                for (ModeShapeServer server : getServers()) {
                    Element serverElement = doc.createElement(SERVER_TAG);
                    root.appendChild(serverElement);

                    serverElement.setAttribute(URL_TAG, server.getUrl());
                    serverElement.setAttribute(USER_TAG, server.getUser());

                    if (server.isPasswordBeingPersisted()) {
                        serverElement.setAttribute(PASSWORD_TAG, Base64.encodeBytes(server.getPassword().getBytes()));
                    }
                }

                DOMSource source = new DOMSource(doc);
                StreamResult resultXML = new StreamResult(new FileOutputStream(getStateFileName()));
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
                transformer.transform(source, resultXML);
            } catch (Exception e) {
                return new Status(Severity.ERROR, NLS.bind(RestClientI18n.errorSavingServerRegistry, getStateFileName()), e);
            }
        } else if ((this.stateLocationPath != null) && stateFileExists()) {
            // delete current registry file since all servers have been deleted
            try {
                new File(getStateFileName()).delete();
            } catch (Exception e) {
                return new Status(Severity.ERROR, NLS.bind(RestClientI18n.errorDeletingServerRegistryFile, getStateFileName()), e);
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * @return <code>true</code> if the state file already exists
     */
    private boolean stateFileExists() {
        return new File(getStateFileName()).exists();
    }

    /**
     * @param workspace the workspace where the file should be unpublished from (cannot be <code>null</code>)
     * @param path the starting path in the repository (cannot be <code>null</code>)
     * @param file the file being unpublished (cannot be <code>null</code>)
     * @return the status of the outcome of this unpublishing operation
     */
    public Status unpublish( ModeShapeWorkspace workspace,
                             String path,
                             File file ) {
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$
        CheckArg.isNotNull(path, "path"); //$NON-NLS-1$
        CheckArg.isNotNull(file, "file"); //$NON-NLS-1$

        ModeShapeServer server = workspace.getServer();

        if (isRegistered(server)) {
            return this.delegate.unpublish(workspace.getDelegate(), path, file);
        }

        // server must be registered in order to unpublish
        throw new RuntimeException(NLS.bind(RestClientI18n.serverManagerUnregisteredServer, server.getShortDescription()));
    }

    /**
     * Updates the server registry with a new version of a server.
     * 
     * @param previousServerVersion the version of the server being replaced (never <code>null</code>)
     * @param newServerVersion the new version of the server being put in the server registry (never <code>null</code>)
     * @return a status indicating if the server was updated in the registry (never <code>null</code>)
     */
    public Status updateServer( ModeShapeServer previousServerVersion,
                                ModeShapeServer newServerVersion ) {
        CheckArg.isNotNull(previousServerVersion, "previousServerVersion"); //$NON-NLS-1$
        CheckArg.isNotNull(newServerVersion, "newServerVersion"); //$NON-NLS-1$

        Status status = null;

        try {
            this.serverLock.writeLock().lock();
            status = internalRemoveServer(previousServerVersion, false);

            if (status.isOk()) {
                status = internalAddServer(newServerVersion, false);

                if (status.isOk()) {
                    // all good so notify listeners
                    Exception[] errors = notifyRegistryListeners(ServerRegistryEvent.createUpdateEvent(this, previousServerVersion,
                                                                                                       newServerVersion));
                    return processRegistryListenerErrors(errors);
                }

                // unexpected problem adding new version of server to registry
                return new Status(Severity.ERROR,
                                  NLS.bind(RestClientI18n.serverManagerRegistryUpdateAddError, status.getMessage()),
                                  status.getException());
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        // unexpected problem removing server from registry
        return new Status(Severity.ERROR,
                          NLS.bind(RestClientI18n.serverManagerRegistryUpdateRemoveError, status.getMessage()),
                          status.getException());
    }

}
