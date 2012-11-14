/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui;

import static org.jboss.tools.modeshape.jcr.ui.JcrUiConstants.FileExtensions.CND;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.ui.forms.ErrorMessage;

/**
 * Commonly used utility methods for the JCR UI project.
 */
public class JcrUiUtils {

    /**
     * @return the CND editor icon (never <code>null</code>)
     */
    public static Image getCndEditorImage() {
        return Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR);
    }

    /**
     * @return the generic delete toolbar/menu icon (never <code>null</code>)
     */
    public static ImageDescriptor getDeleteImageDescriptor() {
        return org.jboss.tools.modeshape.ui.Activator.getSharedInstance()
                                                     .getImageDescriptor(org.jboss.tools.modeshape.ui.UiConstants.Images.DELETE_16X);
    }

    /**
     * @return the generic edit toolbar/menu icon (never <code>null</code>)
     */
    public static ImageDescriptor getEditImageDescriptor() {
        return org.jboss.tools.modeshape.ui.Activator.getSharedInstance()
                                                     .getImageDescriptor(org.jboss.tools.modeshape.ui.UiConstants.Images.EDIT_16X);
    }

    /**
     * @param status the validation status whose severity is being converted to a form message area message type
     * @return the {@link org.eclipse.core.resources.IMarker marker severity} or -1 if status is OK
     */
    public static int getMarkerSeverity( final ValidationStatus status) {
        Utils.verifyIsNotNull(status, "status"); //$NON-NLS-1$

        if (status.isOk()) {
            return -1;
        }

        if (status.isError()) {
            return IMarker.SEVERITY_ERROR;
        }

        if (status.isWarning()) {
            return IMarker.SEVERITY_WARNING;
        }

        if (status.isInfo()) {
            return IMarker.SEVERITY_INFO;
        }

        assert false : "Unexpected severity type"; //$NON-NLS-1$
        return IMarker.SEVERITY_ERROR;
    }

    /**
     * @param status the validation status whose severity is being converted to a form message area message type
     * @return the message type
     */
    public static int getMessageType( final ValidationStatus status ) {
        Utils.verifyIsNotNull(status, "status"); //$NON-NLS-1$

        if (status.isOk()) {
            return IMessageProvider.NONE;
        }

        if (status.isError()) {
            return IMessageProvider.ERROR;
        }

        if (status.isWarning()) {
            return IMessageProvider.WARNING;
        }

        if (status.isInfo()) {
            return IMessageProvider.INFORMATION;
        }

        assert false : "Unexpected severity type"; //$NON-NLS-1$
        return IMessageProvider.ERROR;
    }

    /**
     * @return the generic new toolbar/menu icon (never <code>null</code>)
     */
    public static ImageDescriptor getNewImageDescriptor() {
        return org.jboss.tools.modeshape.ui.Activator.getSharedInstance()
                                                     .getImageDescriptor(org.jboss.tools.modeshape.ui.UiConstants.Images.NEW_16X);
    }

    /**
     * @param resource the resource being checked (can be <code>null</code>)
     * @return <code>true</code> if resource is a CND file
     */
    public static boolean isCndFile( final IResource resource ) {
        return ((resource != null) && (resource.getType() == IResource.FILE) && CND.equals(resource.getFileExtension()) && resource.exists());
    }

    /**
     * @param status the status being used to set the message (cannot be <code>null</code>)
     * @param message the message being set (cannot be <code>null</code>)
     */
    public static void setMessage( final ValidationStatus status,
                                   final ErrorMessage message ) {
        if (status.isError()) {
            message.setErrorMessage(status.getMessage());
        } else if (status.isWarning()) {
            message.setWarningMessage(status.getMessage());
        } else if (status.isInfo()) {
            message.setInformationMessage(status.getMessage());
        } else if (status.isOk()) {
            message.setOkMessage(status.getMessage());
        } else {
            assert false : "Unexpected status type"; //$NON-NLS-1$
        }
    }

    /**
     * Don't allow construction.
     */
    private JcrUiUtils() {
        // nothing to do
    }
}
