/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
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
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        _sharedInstance = this;
    }
}
