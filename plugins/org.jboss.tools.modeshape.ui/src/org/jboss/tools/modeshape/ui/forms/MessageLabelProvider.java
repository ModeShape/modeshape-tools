/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.forms;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.forms.IMessage;
import org.jboss.tools.modeshape.ui.Activator;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A label provider for {@link IMessage}s.
 */
public class MessageLabelProvider extends ColumnLabelProvider {

    private final boolean messageTypeFlag;

    /**
     * @param messageTypeFlag a flag indicating if the {@link TableViewerColumn} is message type column or the the message column.
     */
    public MessageLabelProvider( final boolean messageTypeFlag ) {
        this.messageTypeFlag = messageTypeFlag;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( final Object element ) {
        if (this.messageTypeFlag) {
            final ISharedImages sharedImages = Activator.getSharedInstance().getWorkbench().getSharedImages();
            final int messageType = ((IMessage)element).getMessageType();

            if (messageType == IMessageProvider.ERROR) {
                return sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            }

            if (messageType == IMessageProvider.WARNING) {
                return sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
            }

            if (messageType == IMessageProvider.INFORMATION) {
                return sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( final Object element ) {
        if (this.messageTypeFlag) {
            return UiUtils.EMPTY_STRING;
        }

        final IMessage message = (IMessage)element;
        return message.getMessage();
    }
}