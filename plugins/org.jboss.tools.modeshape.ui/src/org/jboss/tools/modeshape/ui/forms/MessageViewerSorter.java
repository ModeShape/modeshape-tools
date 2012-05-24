/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.forms;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.forms.IMessage;

/**
 * A structured viewer sorter for {@link IMessage}s.
 */
public class MessageViewerSorter extends ViewerSorter {

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare( Viewer viewer,
                        Object thisMessage,
                        Object thatMessage ) {
        int thisMsgType = ((IMessage)thisMessage).getMessageType();
        int thatMsgType = ((IMessage)thatMessage).getMessageType();

        return Integer.valueOf(thisMsgType).compareTo(Integer.valueOf(thatMsgType));
    }
}
