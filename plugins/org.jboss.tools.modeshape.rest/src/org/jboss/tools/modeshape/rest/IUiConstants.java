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

public interface IUiConstants {

    /**
     * The Plug-in's identifier.
     */
    String PLUGIN_ID = "org.jboss.tools.modeshape.rest"; //$NON-NLS-1$

    String ICON_FOLDER = "icons/"; //$NON-NLS-1$

    //
    // /icons/objects/
    //

    String OBJECT_ICONS_FOLDER = ICON_FOLDER + "objects/"; //$NON-NLS-1$

    String CHECKMARK_IMAGE = OBJECT_ICONS_FOLDER + "checkmark.gif"; //$NON-NLS-1$

    String REPOSITORY_IMAGE = OBJECT_ICONS_FOLDER + "repository.gif"; //$NON-NLS-1$

    String SERVER_IMAGE = OBJECT_ICONS_FOLDER + "server.gif"; //$NON-NLS-1$

    String WORKSPACE_IMAGE = OBJECT_ICONS_FOLDER + "workspace.gif"; //$NON-NLS-1$

    //
    // /icons/views/
    //

    String VIEWS_ICON_FOLDER = ICON_FOLDER + "views/"; //$NON-NLS-1$

    String BLANK_IMAGE = VIEWS_ICON_FOLDER + "blank.gif"; //$NON-NLS-1$

    String COLLAPSE_ALL_IMAGE = VIEWS_ICON_FOLDER + "collapse_all.gif"; //$NON-NLS-1$

    String DELETE_SERVER_IMAGE = VIEWS_ICON_FOLDER + "delete_server.gif"; //$NON-NLS-1$

    String ModeShape_IMAGE_16x = VIEWS_ICON_FOLDER + "modeShape_icon_16x.png"; //$NON-NLS-1$

    String EDIT_SERVER_IMAGE = VIEWS_ICON_FOLDER + "edit_server.gif"; //$NON-NLS-1$

    String ERROR_OVERLAY_IMAGE = VIEWS_ICON_FOLDER + "error_overlay.gif"; //$NON-NLS-1$

    String NEW_SERVER_IMAGE = VIEWS_ICON_FOLDER + "new_server.gif"; //$NON-NLS-1$

    String PUBLISH_IMAGE = VIEWS_ICON_FOLDER + "publish.png"; //$NON-NLS-1$

    String PUBLISHED_OVERLAY_IMAGE = VIEWS_ICON_FOLDER + "published_overlay.png"; //$NON-NLS-1$

    String REFRESH_IMAGE = VIEWS_ICON_FOLDER + "refresh.gif"; //$NON-NLS-1$

    String UNPUBLISH_IMAGE = VIEWS_ICON_FOLDER + "unpublish.png"; //$NON-NLS-1$

    //
    // /icons/wizards/
    //

    String WIZARD_ICONS_FOLDER = ICON_FOLDER + "wizards/"; //$NON-NLS-1$

    String WIZARD_BANNER_IMAGE = WIZARD_ICONS_FOLDER + "wizard_banner.png"; //$NON-NLS-1$

    //
    // jobs
    //

    /**
     * The <code>Job</code> framework job family for the ModeShape publishing and unpublishing operations.
     */
    String PUBLISHING_JOB_FAMILY = "modeshape.publishing.job.family"; //$NON-NLS-1$

    /**
     * Constants associated with help contexts.
     */
    interface HelpContexts {

        String HELP_CONTEXT_PREFIX = IUiConstants.PLUGIN_ID + '.';

        /**
         * The message console help context.
         */
        String MESSAGE_CONSOLE_HELP_CONTEXT = HELP_CONTEXT_PREFIX + "messageConsoleHelpContext"; //$NON-NLS-1$

        /**
         * The preference pages help context.
         */
        String PREFERENCE_PAGE_HELP_CONTEXT = HELP_CONTEXT_PREFIX + "preferencesHelpContext"; //$NON-NLS-1$

        /**
         * The publish and unpublish dialog help context.
         */
        String PUBLISH_DIALOG_HELP_CONTEXT = HELP_CONTEXT_PREFIX + "publishDialogHelpContext"; //$NON-NLS-1$

        /**
         * The server wizard and dialog page help context.
         */
        String SERVER_DIALOG_HELP_CONTEXT = HELP_CONTEXT_PREFIX + "serverDialogHelpContext"; //$NON-NLS-1$

        /**
         * The server view help context.
         */
        String SERVER_VIEW_HELP_CONTEXT = HELP_CONTEXT_PREFIX + "serverViewHelpContext"; //$NON-NLS-1$

    }

    /**
     * Constants associated with preferences.
     */
    interface Preferences {

        /**
         * A preference that indicates if the ModeShape server should use resource versioning. If versioning is not used, only one
         * copy of the resource will be persisted.
         */
        String ENABLE_RESOURCE_VERSIONING = "modeShape.preference.enableResourceVersioning"; //$NON-NLS-1$

        /**
         * A preference for a list of ignored resource patterns. Files or folders whose name matches on of the patterns will not be
         * part of publishing operations.
         */
        String IGNORED_RESOURCES_PREFERENCE = "modeShape.preference.ignoredResources"; //$NON-NLS-1$

        /**
         * The ignored resources preference page ID.
         */
        String IGNORED_RESOURCES_PREFERENCE_PAGE_ID = "org.jboss.tools.modeshape.rest.modeShapeIgnoredResourcesPreferencePage"; //$NON-NLS-1$

        /**
         * The main ModeShape preference page ID.
         */
        String PUBLISHING_PREFERENCE_PAGE_ID = "org.jboss.tools.modeshape.rest.publishingPreferencePage"; //$NON-NLS-1$

    }

}
