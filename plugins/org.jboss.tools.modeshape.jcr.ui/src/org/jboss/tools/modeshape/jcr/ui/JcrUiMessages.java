/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.jboss.tools.modeshape.jcr.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Localized messages used by classes in the <code>org.jboss.tools.modeshape.jcr.ui</code> package.
 */
public class JcrUiMessages extends NLS {

    /**
     * An error message if an exception occurs adding the CND project builder command. One parameter, the project name, is
     * required.
     */
    public static String errorAddingCndBuilderCommandToProject;

    /**
     * An error message if an exception occurs processing a resource change event. One parameter, the resource name being
     * processed, is required.
     */
    public static String errorProcessingResourceChangeEvent;

    static {
        NLS.initializeMessages("org.jboss.tools.modeshape.jcr.ui.cnd.jcrUiMessages", JcrUiMessages.class); //$NON-NLS-1$
    }
}
