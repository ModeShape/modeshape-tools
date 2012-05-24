/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Reusable localized messages.
 */
public class UiMessages extends NLS {

    /**
     * The title of a generic error message dialog.
     */
    public static String errorDialogTitle;

    /**
     * A message for a button for exporting data to a file.
     */
    public static String export;

    /**
     * The name of the default file name for the dialog that exports messages to a file.
     */
    public static String exportMessagesDialogDefaultFileName;

    /**
     * A title of the exports messages to a file dialog.
     */
    public static String exportMessagesDialogTitle;

    /**
     * A tooltip for a control that exports messages to a file.
     */
    public static String exportMessagesToolTip;

    /**
     * The title of a generic information message dialog.
     */
    public static String infoDialogTitle;

    /**
     * The table column header title for a message.
     */
    public static String messageColumnHeader;

    /**
     * A message indicating a <code>null</code> was found. One parameter, a string identifying the object, is required.
     */
    public static String objectIsNull;

    /**
     * The title of a generic question message dialog.
     */
    public static String questionDialogTitle;

    /**
     * A message indicating a <code>null</code> or empty string was found. One parameter, a name identifying the string, is
     * required.
     */
    public static String stringIsEmpty;

    /**
     * The title of a generic warning message dialog.
     */
    public static String warningDialogTitle;

    static {
        NLS.initializeMessages("org.jboss.tools.modeshape.ui.uiMessages", UiMessages.class); //$NON-NLS-1$
    }
}
