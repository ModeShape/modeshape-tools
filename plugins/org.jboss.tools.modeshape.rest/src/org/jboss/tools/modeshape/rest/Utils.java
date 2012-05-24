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

import static org.jboss.tools.modeshape.rest.IUiConstants.PLUGIN_ID;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

public final class Utils {

    /**
     * Converts the non-Eclipse status severity to an Eclipse severity level. An {@link org.modeshape.web.jcr.rest.client.Status.Severity#UNKNOWN unknown status} is
     * converted to {@link IStatus#CANCEL cancel}.
     * 
     * @param severity the eclipse status severity level
     * @return the converted severity level (never <code>null</code>)
     * @see IStatus
     */
    public static int convertSeverity( Severity severity ) {
        if (severity == Severity.OK) return IStatus.OK;
        if (severity == Severity.ERROR) return IStatus.ERROR;
        if (severity == Severity.WARNING) return IStatus.WARNING;
        if (severity == Severity.INFO) return IStatus.INFO;
        return IStatus.CANCEL;
    }

    /**
     * Converts the Eclipse status severity level to a non-Eclipse severity.
     * 
     * @param severity the eclipse status severity level
     * @return the converted severity level (never <code>null</code>)
     * @see IStatus
     */
    public static Severity convertSeverity( int severity ) {
        if (severity == IStatus.OK) return Severity.OK;
        if (severity == IStatus.ERROR) return Severity.ERROR;
        if (severity == IStatus.WARNING) return Severity.WARNING;
        if (severity == IStatus.INFO) return Severity.INFO;
        return Severity.UNKNOWN;
    }

    /**
     * @param status the status being converted (never <code>null</code>)
     * @return the Eclipse status object (never <code>null</code>)
     */
    public static IStatus convert( Status status ) {
        CheckArg.isNotNull(status, "status"); //$NON-NLS-1$
        return new org.eclipse.core.runtime.Status(convertSeverity(status.getSeverity()), PLUGIN_ID, status.getMessage(),
                                                   status.getException());
    }

    /**
     * The OK status does not have an image.
     * 
     * @param status the status whose image is being requested (never <code>null</code>)
     * @return the image or <code>null</code> if no associated image for the status severity
     */
    public static Image getImage( Status status ) {
        CheckArg.isNotNull(status, "status"); //$NON-NLS-1$
        String imageId = null;

        if (status.isError()) {
            imageId = ISharedImages.IMG_OBJS_ERROR_TSK;
        } else if (status.isInfo()) {
            imageId = ISharedImages.IMG_OBJS_INFO_TSK;
        } else if (status.isWarning()) {
            imageId = ISharedImages.IMG_OBJS_WARN_TSK;
        }

        if (imageId != null) {
            return Activator.getDefault().getSharedImage(imageId);
        }

        return null;
    }

    /**
     * The image can be used to decorate an existing image.
     * 
     * @param status the status whose image overlay is being requested (never <code>null</code>)
     * @return the image descriptor or <code>null</code> if none found for the status severity
     */
    public static ImageDescriptor getOverlayImage( Status status ) {
        CheckArg.isNotNull(status, "status"); //$NON-NLS-1$
        String imageId = null;

        if (status.isError()) {
            imageId = IUiConstants.ERROR_OVERLAY_IMAGE;
        }

        if (imageId != null) {
            return Activator.getDefault().getImageDescriptor(imageId);
        }

        return null;
    }

    /**
     * @param password the password being validated
     * @return a validation status (never <code>null</code>)
     */
    public static Status isPasswordValid( String password ) {
        return Status.OK_STATUS;
    }

    /**
     * This does not verify that a server with the same primary field values doesn't already exist in the server registry.
     * 
     * @param url the URL being validated
     * @param user the user being validated
     * @param password the password being validated
     * @return a validation status (never <code>null</code>)
     */
    public static Status isServerValid( String url,
                                        String user,
                                        String password ) {
        Status status = isUrlValid(url);

        if (!status.isError()) {
            status = isUserValid(user);

            if (!status.isError()) {
                status = isPasswordValid(password);
            }
        }

        return status;
    }

    /**
     * @param url the URL being validated
     * @return a validation status (never <code>null</code>)
     */
    public static Status isUrlValid( String url ) {
        if ((url == null) || (url.length() == 0)) {
            return new Status(Severity.ERROR, RestClientI18n.serverEmptyUrlMsg, null);
        }

        try {
            URL testUrl = new URL(url);

            // make sure there is a host
            String host = testUrl.getHost();

            if ((host == null) || "".equals(host)) { //$NON-NLS-1$
                return new Status(Severity.ERROR, RestClientI18n.serverInvalidUrlHostMsg, null);
            }

            // make sure there is a port
            int port = testUrl.getPort();

            if (port == -1) {
                return new Status(Severity.ERROR, RestClientI18n.serverInvalidUrlPortMsg, null);
            }
        } catch (Exception e) {
            return new Status(Severity.ERROR, NLS.bind(RestClientI18n.serverInvalidUrlMsg, url), e);
        }

        return Status.OK_STATUS;
    }

    /**
     * @param user the user being validated
     * @return a validation status (never <code>null</code>)
     */
    public static Status isUserValid( String user ) {
        if ((user == null) || (user.length() == 0)) {
            return new Status(Severity.ERROR, RestClientI18n.serverEmptyUserMsg, null);
        }

        return Status.OK_STATUS;
    }

    /**
     * Don't allow construction.
     */
    public Utils() {
        // nothing to do
    }

}
