/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.graphics;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * 
 */
public final class GraphicsUtils {

    /**
     * @param uiPlugin the UI Plug-in instance (cannot be <code>null</code>)
     * @param pathToImage the path to the image file relative to the specified plug-in's folder (can be <code>null</code>)
     * @return the new image descriptor or <code>null</code> if path does not exist
     */
    private static ImageDescriptor createImageDescriptor( final AbstractUIPlugin uiPlugin,
                                                          final String pathToImage ) {
        assert (uiPlugin != null) : "UI Plugin is null"; //$NON-NLS-1$
        assert (pathToImage != null) : "pathToImage is null"; //$NON-NLS-1$

        final String pluginId = uiPlugin.getBundle().getSymbolicName();
        return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, pathToImage);
    }

    /**
     * @param uiPlugin the UI Plug-in instance (cannot be <code>null</code>)
     * @param pathToImage the path to the image file relative to the specified plug-in's folder (can be <code>null</code>)
     * @return the requested image or a standard "missing image" image (never <code>null</code>)
     * @throws IllegalArgumentException if the plugin is <code>null</code>
     */
    public static final Image getImage( final AbstractUIPlugin uiPlugin,
                                        final String pathToImage ) {
        UiUtils.verifyIsNotNull(uiPlugin, "uiPlugin"); //$NON-NLS-1$

        final ImageRegistry imageRegistry = uiPlugin.getImageRegistry();
        Image image = imageRegistry.get(pathToImage);

        if (image == null) {
            if (pathToImage != null) {
                final ImageDescriptor newImageDescriptor = createImageDescriptor(uiPlugin, pathToImage);

                if (newImageDescriptor != null) {
                    imageRegistry.put(pathToImage, newImageDescriptor);
                    image = imageRegistry.get(pathToImage);
                }
            }
        }

        return ((image == null) ? ImageDescriptor.getMissingImageDescriptor().createImage() : image);
    }

    /**
     * Don't allow construction.
     */
    private GraphicsUtils() {
        // nothing to do
    }
}
