/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui;

import static org.jboss.tools.modeshape.jcr.ui.JcrUiConstants.PLUGIN_ID;
import static org.jboss.tools.modeshape.jcr.ui.JcrUiConstants.WorkspaceIds.CND_PROJECT_BUILDER;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.modeshape.jcr.ui.dnd.JcrClipboard;
import org.jboss.tools.modeshape.ui.graphics.GraphicsUtils;
import org.osgi.framework.BundleContext;

/**
 * The <code>org.jboss.tools.modeshape.jcr.ui</code> UI Plug-in class.
 */
public class Activator extends AbstractUIPlugin {

    private static Activator _sharedInstance;

    /**
     * @return the shared instance (<code>null</code> if the Eclipse platform is not running)
     */
    public static Activator getSharedInstance() {
        return _sharedInstance;
    }

    private JcrClipboard clipboard;

    private IResourceChangeListener listener;

    void addCndBuilder( final IProject project ) {
        try {
            final IProjectDescription desc = project.getDescription();
            final ICommand[] currentBuildCommands = desc.getBuildSpec();

            // don't add builder to project if it already exists
            for (int i = 0; i < currentBuildCommands.length; ++i) {
                if (CND_PROJECT_BUILDER.equals(currentBuildCommands[i].getBuilderName())) {
                    return;
                }
            }

            // create command for builder
            final ICommand command = desc.newCommand();
            command.setBuilderName(CND_PROJECT_BUILDER);

            // Add it before other builders.
            final ICommand[] updatedBuildCommands = new ICommand[currentBuildCommands.length + 1];
            System.arraycopy(currentBuildCommands, 0, updatedBuildCommands, 1, currentBuildCommands.length);
            updatedBuildCommands[0] = command;
            desc.setBuildSpec(updatedBuildCommands);

            getWorkbench().getDisplay().asyncExec(new Runnable() {

                /**
                 * {@inheritDoc}
                 *
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    try {
                        project.setDescription(desc, null);
                    } catch (CoreException e) {
                        getLog().log(new Status(IStatus.ERROR, PLUGIN_ID,
                                                NLS.bind(JcrUiMessages.errorAddingCndBuilderCommandToProject, project.getName()),
                                                e));
                    }
                }
            });
        } catch (Exception e) {
            getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(JcrUiMessages.errorAddingCndBuilderCommandToProject,
                                                                       project.getName()), e));
        }
    }

    /**
     * @return the JCR clipboard (never <code>null</code>)
     */
    public JcrClipboard getClipboard() {
        if (this.clipboard == null) {
            this.clipboard = new JcrClipboard(getWorkbench().getDisplay());
        }

        return this.clipboard;
    }

    /**
     * Retrieves the requested image from the image registry. If the image does not exist in the registry it will be created and
     * registered.
     *
     * @param pathToImage the path to the image file relative to the specified plug-in's folder (can be <code>null</code>)
     * @return the requested image or a standard "missing image" image (never <code>null</code>)
     */
    public final Image getImage( final String pathToImage ) {
        return GraphicsUtils.getImage(this, pathToImage);
    }

    /**
     * Retrieves the requested image descriptor from the image registry. If the image does not exist in the registry it will be
     * created and registered.
     *
     * @param pathToImage the path to the image file relative to the specified plug-in's folder (can be <code>null</code>)
     * @return the requested image descriptor or a standard "missing image" image descriptor (never <code>null</code>)
     */
    public final ImageDescriptor getImageDescriptor( final String pathToImage ) {
        getImage(pathToImage); // this creates entry in registry if necessary
        return getImageRegistry().getDescriptor(pathToImage);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        _sharedInstance = this;

        // create listener that will add CND build command to projects
        this.listener = new IResourceChangeListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
             */
            @Override
            public void resourceChanged( IResourceChangeEvent event ) {
                IResourceDelta delta = event.getDelta();

                if (delta != null) {
                    try {
                        delta.accept(new IResourceDeltaVisitor() {

                            /**
                             * {@inheritDoc}
                             *
                             * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
                             */
                            @Override
                            public boolean visit( final IResourceDelta delta ) {
                                IResource resource = delta.getResource();

                                if ((resource.getType() == IResource.FILE) && (delta.getKind() == IResourceDelta.ADDED)) {
                                    if (JcrUiUtils.isCndFile(resource)) {
                                        addCndBuilder(delta.getResource().getProject());
                                        return false; // stop visiting
                                    }
                                }

                                return true; // keep visiting
                            }
                        });
                    } catch (CoreException e) {
                        getLog().log(new Status(IStatus.ERROR, PLUGIN_ID,
                                                NLS.bind(JcrUiMessages.errorProcessingResourceChangeEvent,
                                                         delta.getResource().getName()), e));
                    }
                }
            }
        };

        // register to receive resource change events
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.listener);

        // clear and dispose JCR model object clipboard
        getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.ui.IWorkbenchListener#postShutdown(org.eclipse.ui.IWorkbench)
             */
            @Override
            public void postShutdown( final IWorkbench workbench ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.ui.IWorkbenchListener#preShutdown(org.eclipse.ui.IWorkbench, boolean)
             */
            @Override
            public boolean preShutdown( final IWorkbench workbench,
                                        final boolean forced ) {
                final JcrClipboard clipboard = getClipboard();

                if (clipboard != null) {
                    clipboard.clearContents();
                    clipboard.dispose();
                }

                workbench.removeWorkbenchListener(this);
                return true;
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        if (this.listener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.listener);
        }

        super.stop(context);
    }
}
