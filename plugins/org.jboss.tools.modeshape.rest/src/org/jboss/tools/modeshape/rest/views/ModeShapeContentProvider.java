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
package org.jboss.tools.modeshape.rest.views;

import static org.jboss.tools.modeshape.rest.IUiConstants.PUBLISHED_OVERLAY_IMAGE;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.jcip.annotations.GuardedBy;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IDecoratorManager;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.IServerRegistryListener;
import org.jboss.tools.modeshape.rest.IUiConstants;
import org.jboss.tools.modeshape.rest.PublishedResourceHelper;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.ServerRegistryEvent;
import org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject;
import org.jboss.tools.modeshape.rest.domain.ModeShapeRepository;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;

/**
 * The <code>ModeShapeContentProvider</code> is a content and label provider for the repositories. This class <strong>MUST</strong>
 * be registered, and then unregistered, to receive server registry events.
 */
public final class ModeShapeContentProvider extends ColumnLabelProvider implements ILightweightLabelDecorator,
        IServerRegistryListener, ILazyTreeContentProvider {

    /**
     * The decorator ID.
     */
    private static final String ID = "org.jboss.tools.modeshape.rest.decorator"; //$NON-NLS-1$

    /**
     * If a server connection cannot be established, wait this amount of time before trying again.
     */
    private static final long RETRY_DURATION = 5000;

    /**
     * Indicates if the Eclipse lazy content provider has already called this provider. This occurs when the associated viewer of
     * this provider has been shown once or if the Eclipse window loses focus.
     */
    private static boolean _alreadyCalledByFramework;

    /**
     * Servers that a connection can't be established. Value is the last time a establishing a connection was tried.
     */
    @GuardedBy("offlineServersLock")
    private final static Map<ModeShapeServer, Long> _offlineServerMap = new HashMap<ModeShapeServer, Long>();

    /**
     * Lock used for when accessing the offline server map. The map will be accessed in different threads as the decorator runs in
     * its own thread (not the UI thread).
     */
    private final static ReadWriteLock _offlineServersLock = new ReentrantReadWriteLock();

    /**
     * @return the decorator
     */
    public static ModeShapeContentProvider getDecorator() {
        final IDecoratorManager decoratorMgr = Activator.getDefault().getWorkbench().getDecoratorManager();

        if (decoratorMgr.getEnabled(ID)) {
            return (ModeShapeContentProvider)decoratorMgr.getBaseLabelProvider(ID);
        }

        return null;
    }

    /**
     * The server manager where the server registry is managed.
     */
    private ServerManager serverManager;

    private TreeViewer viewer;

    /**
     * @param server the server that is offline
     */
    void addOfflineServer( final ModeShapeServer server ) {
        if (this.viewer != null) {
            this.viewer.setHasChildren(server, false); // get rid of the expansion button
        }

        try {
            _offlineServersLock.writeLock().lock();
            _offlineServerMap.put(server, System.currentTimeMillis());
            refresh(server);
        } finally {
            _offlineServersLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    @Override
    public void decorate( final Object element,
                          final IDecoration decoration ) {
        ImageDescriptor overlay = null;
        final Display display = Display.getDefault();

        if (display.isDisposed()) {
            return;
        }

        if (element instanceof ModeShapeServer) {
            final ModeShapeServer server = (ModeShapeServer)element;

            // if server is offline then decorate with error overlay
            try {
                _offlineServersLock.readLock().lock();

                if (_offlineServerMap.containsKey(server)) {
                    overlay = Activator.getDefault().getImageDescriptor(IUiConstants.ERROR_OVERLAY_IMAGE);
                }
            } finally {
                _offlineServersLock.readLock().unlock();
            }
        } else if (element instanceof IFile) {
            final IFile file = (IFile)element;

            if (file.exists() && !file.isHidden() && new PublishedResourceHelper(getServerManager()).isPublished((IFile)element)) {
                overlay = Activator.getDefault().getImageDescriptor(PUBLISHED_OVERLAY_IMAGE);
            }
        }

        if (overlay != null) {
            decoration.addOverlay(overlay);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( final Object element ) {
        return Activator.getDefault().getImage(element);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ILazyTreeContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent( final Object element ) {
        assert (element instanceof ModeShapeDomainObject);

        if (element instanceof ModeShapeWorkspace) {
            return ((ModeShapeWorkspace)element).getRepository();
        }

        if (element instanceof ModeShapeRepository) {
            return ((ModeShapeRepository)element).getServer();
        }

        if (element instanceof ModeShapeServer) {
            return getServerManager();
        }

        assert false : "unknown ModeShapeDomainObject=" + element.getClass(); //$NON-NLS-1$
        return null;
    }

    ServerManager getServerManager() {
        if (this.serverManager == null) {
            this.serverManager = Activator.getDefault().getServerManager();
        }

        return this.serverManager;
    }

    Shell getShell() {
        return this.viewer.getTree().getShell();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( final Object element ) {
        assert (element instanceof ModeShapeDomainObject);
        return ((ModeShapeDomainObject)element).getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipImage(java.lang.Object)
     */
    @Override
    public Image getToolTipImage( final Object object ) {
        return getImage(object);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
     */
    @Override
    public String getToolTipText( final Object element ) {
        if (element instanceof ModeShapeDomainObject) {
            return ((ModeShapeDomainObject)element).getShortDescription();
        }

        return super.getToolTipText(element);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipTimeDisplayed(java.lang.Object)
     */
    @Override
    public int getToolTipTimeDisplayed( final Object object ) {
        return 3000;
    }

    TreeViewer getViewer() {
        return this.viewer;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void inputChanged( final Viewer viewer,
                              final Object oldInput,
                              final Object newInput ) {
        // nothing to do
    }

    /**
     * Determines if a try to connect to a server should be done based on the last time a try was done and failed.
     *
     * @param server the server being checked
     * @return <code>true</code> if it is OK to try and connect
     */
    private boolean isOkToConnect( final ModeShapeServer server ) {
        boolean check = false; // check map for time

        try {
            _offlineServersLock.readLock().lock();
            check = _offlineServerMap.containsKey(server);
        } finally {
            _offlineServersLock.readLock().unlock();
        }

        if (check) {
            try {
                _offlineServersLock.writeLock().lock();

                if (_offlineServerMap.containsKey(server)) {
                    final long checkTime = _offlineServerMap.get(server);

                    // OK to try and connect if last failed attempt was too long ago
                    if ((System.currentTimeMillis() - checkTime) > RETRY_DURATION) {
                        _offlineServerMap.remove(server);
                        refresh(server);
                        return true;
                    }

                    // don't try and connect because we just tried and failed
                    return false;
                }
            } finally {
                _offlineServersLock.writeLock().unlock();
            }
        }

        // OK to try and connect
        return true;
    }

    public void refresh( final Object element ) {
        final Display display = Display.getDefault();

        if (display.isDisposed()) {
            return;
        }

        display.asyncExec(new Runnable() {
            /**
             * {@inheritDoc}
             *
             * @see java.lang.Runnable#run()
             */
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                fireLabelProviderChanged(new LabelProviderChangedEvent(ModeShapeContentProvider.this, element));
            }
        });
    }

    private void run( final ServerOperation op,
                      final boolean enforceNonEmptyPassword ) {
        try {
            final ModeShapeServer server = op.getServer();

            if (enforceNonEmptyPassword && ((server.getPassword() == null) || server.getPassword().isEmpty())) {
                if (MessageDialog.openQuestion(getShell(), RestClientI18n.missingServerPasswordDialogTitle,
                                               RestClientI18n.missingServerPasswordDialogMsg)) {
                    final NewPasswordDialog dialog = new NewPasswordDialog(getShell());

                    if (dialog.open() == Window.OK) {
                        getServerManager().updateServer(server,
                                                        new ModeShapeServer(server.getOriginalUrl(),
                                                                            server.getUser(),
                                                                            dialog.getNewPassword(),
                                                                            server.isPasswordBeingPersisted()));

                        // need to obtain changed server from server manager as servers are not mutable
                        for (final ModeShapeServer registeredServer : getServerManager().getServers()) {
                            if (registeredServer.hasSameKey(server)) {
                                op.setUpdatedServer(registeredServer);
                                break;
                            }
                        }
                    }
                }
            }

            final ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell()) {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.jface.dialogs.ProgressMonitorDialog#configureShell(org.eclipse.swt.widgets.Shell)
                 */
                @Override
                protected void configureShell( final Shell shell ) {
                    super.configureShell(shell);
                    shell.setText(RestClientI18n.requestDialogTitle);
                }
            };

            dialog.run(true, false, op);
        } catch (final InvocationTargetException e) {
            // should not happen as ServerOperation handles this but just in case
            if (op.getUpdatedServer() == null) {
                addOfflineServer(op.getServer());
            } else {
                addOfflineServer(op.getUpdatedServer());
            }

            final org.eclipse.core.runtime.Status error = new org.eclipse.core.runtime.Status(IStatus.ERROR,
                                                                                              IUiConstants.PLUGIN_ID,
                                                                                              RestClientI18n.runningServerRequestErrorMsg,
                                                                                              e.getCause());
            ErrorDialog.openError(getShell(), RestClientI18n.runningServerRequestErrorDialogTitle,
                                  RestClientI18n.runningServerRequestErrorDialogMsg, error);
        } catch (final InterruptedException e) {
            // won't happen as runnable is not cancelable
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.rest.IServerRegistryListener#serverRegistryChanged(org.jboss.tools.modeshape.rest.ServerRegistryEvent)
     */
    @Override
    public Exception[] serverRegistryChanged( final ServerRegistryEvent event ) {
        Exception[] errors = null;

        // only care about servers being removed or updated
        if (event.isRemove() || event.isUpdate()) {
            try {
                _offlineServersLock.writeLock().lock();
                _offlineServerMap.remove(event.getServer());
            } catch (final Exception e) {
                errors = new Exception[] { e };
            } finally {
                _offlineServersLock.writeLock().unlock();
            }
        }

        return errors;
    }

    void setViewer( final TreeViewer viewer ) {
        this.viewer = viewer;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ILazyTreeContentProvider#updateChildCount(java.lang.Object, int)
     */
    @Override
    public void updateChildCount( final Object element,
                                  final int currentChildCount ) {
        getServerManager(); // make sure server manager has been set

        if (element == this.serverManager) {
            final Collection<ModeShapeServer> servers = this.serverManager.getServers();

            if (servers.size() != currentChildCount) {
                this.viewer.setChildCount(element, servers.size());
            }

            if (_alreadyCalledByFramework) {
                updateElement(element, 0);
            } else {
                _alreadyCalledByFramework = true;
            }
        } else if (element instanceof ModeShapeServer) {
            final ModeShapeServer server = (ModeShapeServer)element;

            if (isOkToConnect(server)) {
                try {
                    final ServerOperation op = new ServerOperation(server) {

                        /**
                         * {@inheritDoc}
                         *
                         * @see org.jboss.tools.modeshape.rest.views.ModeShapeContentProvider.ServerOperation#doRun(org.eclipse.core.runtime.IProgressMonitor)
                         */
                        @Override
                        void doRun( final IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                            if (monitor.isCanceled()) {
                                throw new InterruptedException();
                            }

                            monitor.beginTask(RestClientI18n.runningRepositoriesQueryMsg, IProgressMonitor.UNKNOWN);
                            final ModeShapeServer serverToQuery = ((getUpdatedServer() == null) ? server : getUpdatedServer());

                            try {
                                final Collection<ModeShapeRepository> repositories = getServerManager().getRepositories(serverToQuery);

                                if (repositories.size() != currentChildCount) {
                                    getViewer().setChildCount(serverToQuery, repositories.size());
                                }
                            } catch (final Exception e) {
                                throw new InvocationTargetException(e);
                            } finally {
                                monitor.done();
                            }

                        }

                        /**
                         * {@inheritDoc}
                         *
                         * @see org.jboss.tools.modeshape.rest.views.ModeShapeContentProvider.ServerOperation#getDisplay()
                         */
                        @Override
                        Display getDisplay() {
                            return getViewer().getTree().getDisplay();
                        }
                    };

                    run(op, true);
                } catch (final Exception e) {
                    addOfflineServer(server);
                }
            }
        } else if (element instanceof ModeShapeRepository) {
            final ModeShapeRepository repository = (ModeShapeRepository)element;

            if (isOkToConnect(repository.getServer())) {
                try {
                    final ServerOperation op = new ServerOperation(repository.getServer()) {

                        /**
                         * {@inheritDoc}
                         *
                         * @see org.jboss.tools.modeshape.rest.views.ModeShapeContentProvider.ServerOperation#doRun(org.eclipse.core.runtime.IProgressMonitor)
                         */
                        @Override
                        void doRun( final IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                            if (monitor.isCanceled()) {
                                throw new InterruptedException();
                            }

                            monitor.beginTask(RestClientI18n.runningWorkspacesQueryMsg, IProgressMonitor.UNKNOWN);

                            try {
                                final Collection<ModeShapeWorkspace> workspaces = getServerManager().getWorkspaces(repository);

                                if (workspaces.size() != currentChildCount) {
                                    getViewer().setChildCount(repository, workspaces.size());
                                }
                            } catch (final Exception e) {
                                throw new InvocationTargetException(e);
                            } finally {
                                monitor.done();
                            }
                        }

                        /**
                         * {@inheritDoc}
                         *
                         * @see org.jboss.tools.modeshape.rest.views.ModeShapeContentProvider.ServerOperation#getDisplay()
                         */
                        @Override
                        Display getDisplay() {
                            return getViewer().getTree().getDisplay();
                        }
                    };

                    run(op, true);
                } catch (final Exception e) {
                    addOfflineServer(repository.getServer());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ILazyTreeContentProvider#updateElement(java.lang.Object, int)
     */
    @Override
    public void updateElement( final Object parent,
                               final int index ) {
        getServerManager(); // make sure server manager has been set

        if (parent == this.serverManager) {
            final ModeShapeServer server = new ArrayList<ModeShapeServer>(this.serverManager.getServers()).get(index);
            this.viewer.replace(this.serverManager, index, server);
            this.viewer.setHasChildren(server, true);
        } else if (parent instanceof ModeShapeServer) {
            final ModeShapeServer server = (ModeShapeServer)parent;

            if (isOkToConnect(server)) {
                try {
                    final ModeShapeRepository repository = new ArrayList<ModeShapeRepository>(this.serverManager.getRepositories(server)).get(index);
                    this.viewer.replace(server, index, repository);
                    this.viewer.setHasChildren(repository, true);
                } catch (final Exception e) {
                    addOfflineServer(server);
                }
            }
        } else if (parent instanceof ModeShapeRepository) {
            final ModeShapeRepository repository = (ModeShapeRepository)parent;

            if (isOkToConnect(repository.getServer())) {
                try {
                    final ModeShapeWorkspace workspace = new ArrayList<ModeShapeWorkspace>(this.serverManager.getWorkspaces(repository)).get(index);
                    this.viewer.replace(repository, index, workspace);
                } catch (final Exception e) {
                    addOfflineServer(repository.getServer());
                }
            }
        }
    }

    class NewPasswordDialog extends MessageDialog {

        private Button btnOk;
        private String newPassword = ""; //$NON-NLS-1$

        public NewPasswordDialog( final Shell parentShell ) {
            super(parentShell, RestClientI18n.runningServerRequestNewPasswordDialogTitle, null,
                    RestClientI18n.runningServerRequestNewPasswordDialogMsg, MessageDialog.NONE, new String[] {
                            IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
            setShellStyle(getShellStyle() | SWT.RESIZE);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String,
         *      boolean)
         */
        @Override
        protected Button createButton( final Composite parent,
                                       final int id,
                                       final String label,
                                       final boolean defaultButton ) {
            final Button btn = super.createButton(parent, id, label, defaultButton);

            if (id == IDialogConstants.OK_ID) {
                // disable OK button initially
                this.btnOk = btn;
                btn.setEnabled(false);
            }

            return btn;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createCustomArea( final Composite parent ) {
            final Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout(2, false));
            panel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            final Label lbl = new Label(panel, SWT.NONE);
            lbl.setText(RestClientI18n.serverPagePasswordLabel);

            final Text txt = new Text(panel, SWT.BORDER);
            txt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txt.setToolTipText(RestClientI18n.serverPagePasswordToolTip);
            txt.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( final ModifyEvent e ) {
                    handlePasswordChanged(((Text)e.widget).getText());
                }
            });

            return super.createCustomArea(parent);
        }

        public String getNewPassword() {
            return this.newPassword;
        }

        void handlePasswordChanged( final String newPassword ) {
            this.newPassword = newPassword;

            // update OK button enablement
            final boolean enable = ((this.newPassword != null) && !this.newPassword.isEmpty());

            if (this.btnOk.getEnabled() != enable) {
                this.btnOk.setEnabled(enable);
            }
        }
    }

    abstract class ServerOperation implements IRunnableWithProgress {
        private ModeShapeServer updatedServer;
        private final ModeShapeServer server;

        public ServerOperation( final ModeShapeServer server ) {
            this.server = server;
        }

        abstract void doRun( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException;

        abstract Display getDisplay();

        ModeShapeServer getServer() {
            return this.server;
        }

        ModeShapeServer getUpdatedServer() {
            return this.updatedServer;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public final void run( final IProgressMonitor monitor ) {
            getDisplay().asyncExec(new Runnable() {

                /**
                 * {@inheritDoc}
                 *
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    try {
                        doRun(monitor);
                    } catch (final InvocationTargetException e) {
                        if (getUpdatedServer() == null) {
                            addOfflineServer(getServer());
                        } else {
                            addOfflineServer(getUpdatedServer());
                        }

                        final org.eclipse.core.runtime.Status error = new org.eclipse.core.runtime.Status(IStatus.ERROR,
                                                                                                          IUiConstants.PLUGIN_ID,
                                                                                                          RestClientI18n.runningServerRequestErrorMsg,
                                                                                                          e.getCause());
                        ErrorDialog.openError(getShell(), RestClientI18n.runningServerRequestErrorDialogTitle,
                                              RestClientI18n.runningServerRequestErrorDialogMsg, error);
                    } catch (final InterruptedException e) {
                        // should not happen has monitor is not cancelable
                    }
                }
            });
        }

        void setUpdatedServer( final ModeShapeServer updatedServer ) {
            this.updatedServer = updatedServer;
        }
    }
}
