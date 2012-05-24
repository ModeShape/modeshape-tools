/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui;

/**
 * Constants used within the <code>org.jboss.tools.modeshape.jcr.ui</code> plug-in.
 */
public interface UiConstants {

    /**
     * The plug-in bundle's symbolic name.
     */
    String PLUGIN_ID = UiConstants.class.getPackage().getName();

    /**
     * The image paths.
     */
    interface Images {

        /**
         * The relative path from the plugin folder to the icons folder.
         */
        String ICONS_FOLDER = "icons/"; //$NON-NLS-1$

        /**
         * The relative path from the plugin folder to the icon used for editing objects.
         */
        String EDIT_16X = ICONS_FOLDER + "edit-16x.png"; //$NON-NLS-1$

        /**
         * The relative path from the plugin folder to the icon used for deleting objects.
         */
        String DELETE_16X = ICONS_FOLDER + "delete-16x.gif"; //$NON-NLS-1$

        /**
         * The relative path from the plugin folder to the icon used for creating new objects.
         */
        String NEW_16X = ICONS_FOLDER + "new-16x.png"; //$NON-NLS-1$
    }
}
