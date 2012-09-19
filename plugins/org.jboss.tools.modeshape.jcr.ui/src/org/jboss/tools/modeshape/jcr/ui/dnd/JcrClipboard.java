/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tools.modeshape.jcr.ui.dnd;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.modeshape.jcr.JcrModelObject;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A clipboard for copying and reading of {@link org.jboss.tools.modeshape.jcr.JcrModelObject JCR model objects}. Instances of
 * this class <strong>must</strong> be disposed manually and their delegate clipboard contents cleared.
 */
public final class JcrClipboard {

    private final Clipboard delegate;

    /**
     * @param display the clipboard display (cannot be <code>null</code>)
     */
    public JcrClipboard( final Display display ) {
        UiUtils.verifyIsNotNull(display, "display");
        this.delegate = new Clipboard(display);
    }

    /**
     * Clears all JCR model objects from the system clipboard. Should be called when the clipboard is not longer needed.
     */
    public void clearContents() {
        this.delegate.clearContents();
    }

    /**
     * Disposes of operating system resources associated with this clipboard. Should be called when the clipboard is not longer
     * needed.
     */
    public void dispose() {
        this.delegate.dispose();
    }

    /**
     * Reads a single JCR model object from the system clipboard.
     *
     * @param jcrTransfer the object converting a native representation to a JCR model object (cannot be <code>null</code>)
     * @return the JCR model object being read from the clipboard (can be <code>null</code>)
     */
    public JcrModelObject getContents( final JcrTransfer jcrTransfer ) {
        UiUtils.verifyIsNotNull(jcrTransfer, "jcrTransfer");
        final Object contents = (this.delegate.isDisposed() ? null : this.delegate.getContents(jcrTransfer));

        if (contents == null) {
            return null;
        }

        return (JcrModelObject)contents;
    }

    /**
     * @param jcrTransfer the transfer type (cannot be <code>null</code>)
     * @return <code>true</code> if system clipboard contains contents with the specified type
     */
    public boolean hasContents( final JcrTransfer jcrTransfer ) {
        return (!this.delegate.isDisposed() && (getContents(jcrTransfer) != null));
    }

    /**
     * Copies a single JCR model object to the system clipboard.
     *
     * @param jcrModelObject the JCR model object being copied to the clipboard (cannot be <code>null</code>)
     * @param jcrTransfer the object converting the model object to a native representation (cannot be <code>null</code>)
     */
    public void setContents( final JcrModelObject jcrModelObject,
                             final JcrTransfer jcrTransfer ) {
        UiUtils.verifyIsNotNull(jcrModelObject, "jcrModelObject");
        UiUtils.verifyIsNotNull(jcrTransfer, "jcrTransfer");
        this.delegate.setContents(new Object[] {jcrModelObject}, new Transfer[] {jcrTransfer});
    }
}
