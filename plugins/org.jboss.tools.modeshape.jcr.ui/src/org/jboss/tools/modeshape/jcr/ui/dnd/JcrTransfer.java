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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;
import org.jboss.tools.modeshape.jcr.ItemOwnerProvider;
import org.jboss.tools.modeshape.jcr.JcrModelObject;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;
import org.jboss.tools.modeshape.jcr.ui.cnd.CndMessages;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A base class for copying and pasting ModeShape JCR model objects to and from the system clipboard.
 */
abstract class JcrTransfer extends ByteArrayTransfer {

    private final String name;
    private final int id;

    private final int[] ids;
    private final String[] names;

    /**
     * @param transferClass the class the transfer type (cannot be <code>null</code>)
     */
    protected JcrTransfer( final Class<? extends JcrTransfer> transferClass ) {
        UiUtils.verifyIsNotNull(transferClass, "transferClass");

        this.name = transferClass.getName();
        this.names = new String[] {this.name};

        this.id = registerType(this.name);
        this.ids = new int[] {this.id};
    }

    /**
     * @return the JCR model object class being transferred to and from system clipboard (cannot be <code>null</code>)
     */
    protected abstract Class<? extends JcrModelObject> getJcrModelObjectClass();

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.dnd.Transfer#getTypeIds()
     */
    @Override
    protected final int[] getTypeIds() {
        return this.ids;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.dnd.Transfer#getTypeNames()
     */
    @Override
    protected final String[] getTypeNames() {
        return this.names;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
     */
    @Override
    protected final void javaToNative( final Object object,
                                       final TransferData transferData ) {
        if (!validate(object)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        assert (object instanceof JcrModelObject);

        ByteArrayOutputStream out = null;
        DataOutputStream dataOut = null;

        try {
            out = new ByteArrayOutputStream();
            dataOut = new DataOutputStream(out);
            writeToStream((JcrModelObject)object, dataOut);
            final byte[] bytes = out.toByteArray();
            super.javaToNative(bytes, transferData);
        } catch (final Exception e) {
            Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID,
                                                                  CndMessages.errorCopyingOrReadingJcrModelObjectToFromClipboard,
                                                                  e));

            if (dataOut != null) {
                try {
                    dataOut.close();
                } catch (final IOException e1) {
                    // ignore
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e1) {
                    // ignore
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
     */
    @Override
    protected final Object nativeToJava( final TransferData transferData ) {
        final byte[] bytes = (byte[])super.nativeToJava(transferData);

        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }

        try {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
            return readFromStream(in);
        } catch (final Exception e) {
            Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID,
                                                                  CndMessages.errorCopyingOrReadingJcrModelObjectToFromClipboard,
                                                                  e));
            return null;
        }
    }

    /**
     * @param inStream the input stream to read the JCR model object from (never <code>null</code>)
     * @return the JCR model object (never <code>null</code>)
     * @throws Exception if there is a problem reading the stream
     */
    protected abstract JcrModelObject readFromStream( final DataInputStream inStream ) throws Exception;

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.swt.dnd.Transfer#validate(java.lang.Object)
     */
    @Override
    protected final boolean validate( final Object object ) {
        final Class<? extends JcrModelObject> jcrModelObjectClass = getJcrModelObjectClass();
        return jcrModelObjectClass.isInstance(object);
    }

    /**
     * @param jcrModelObject the JCR object being copied to the clipboard (never <code>null</code>)
     * @param outStream the data output stream (never <code>null</code>)
     * @throws Exception if there is a problem writing to the stream
     */
    protected abstract void writeToStream( final JcrModelObject jcrModelObject,
                                           final DataOutputStream outStream ) throws Exception;

    /**
     * An internal implementation of an {@link org.jboss.tools.modeshape.jcr.ItemOwnerProvider}.
     */
    protected class OwnerProvider implements ItemOwnerProvider {

        private final QualifiedName qName;

        /**
         * @param qualifiedName a string representation of the qualified name (cannot be <code>null</code> or empty)
         */
        protected OwnerProvider( final String qualifiedName ) {
            this.qName = QualifiedName.parse(qualifiedName);
        }

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.ItemOwnerProvider#getOwnerQualifiedName()
         */
        @Override
        public QualifiedName getOwnerQualifiedName() {
            return this.qName;
        }
    }
}
