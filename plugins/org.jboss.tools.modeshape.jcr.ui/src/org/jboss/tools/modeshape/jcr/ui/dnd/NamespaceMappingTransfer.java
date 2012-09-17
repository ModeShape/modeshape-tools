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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.jboss.tools.modeshape.jcr.JcrModelObject;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A class used to read and write {@link org.jboss.tools.modeshape.jcr.NamespaceMapping}s to and from the system clipboard.
 */
public final class NamespaceMappingTransfer extends JcrTransfer {

    /**
     * The shared instance.
     */
    private static NamespaceMappingTransfer _instance;

    /**
     * @return the singleton instance (never <code>null</code>)
     */
    public static NamespaceMappingTransfer get() {
        if (_instance == null) {
            _instance = new NamespaceMappingTransfer();
        }

        return _instance;
    }

    /**
     * Don't allow construction outside of this class.
     */
    private NamespaceMappingTransfer() {
        super(NamespaceMappingTransfer.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#getJcrModelObjectClass()
     */
    @Override
    protected Class<? extends JcrModelObject> getJcrModelObjectClass() {
        return NamespaceMapping.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#readFromStream(java.io.DataInputStream)
     */
    @Override
    protected JcrModelObject readFromStream( final DataInputStream inStream ) throws Exception {
        final NamespaceMapping namespaceMapping = new NamespaceMapping();

        // prefix
        namespaceMapping.setPrefix(inStream.readUTF());

        // uri
        namespaceMapping.setUri(inStream.readUTF());

        // comments
        namespaceMapping.setComment(inStream.readUTF());

        return namespaceMapping;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#writeToStream(org.jboss.tools.modeshape.jcr.JcrModelObject,
     *      java.io.DataOutputStream)
     */
    @Override
    protected void writeToStream( final JcrModelObject jcrModelObject,
                                  final DataOutputStream outStream ) throws Exception {
        assert (jcrModelObject instanceof NamespaceMapping);
        final NamespaceMapping namespaceMapping = (NamespaceMapping)jcrModelObject;

        // prefix
        outStream.writeUTF(UiUtils.ensureNotNull(namespaceMapping.getPrefix()));

        // uri
        outStream.writeUTF(UiUtils.ensureNotNull(namespaceMapping.getUri()));

        // comments
        outStream.writeUTF(UiUtils.ensureNotNull(namespaceMapping.getComment()));
    }
}
