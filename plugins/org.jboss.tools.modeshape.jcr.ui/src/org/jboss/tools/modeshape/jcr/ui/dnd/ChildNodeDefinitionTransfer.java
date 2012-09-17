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
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.JcrModelObject;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A class used to read and write {@link org.jboss.tools.modeshape.jcr.ChildNodeDefinition}s to and from the system clipboard.
 */
public final class ChildNodeDefinitionTransfer extends JcrTransfer {

    /**
     * The shared instance.
     */
    private static ChildNodeDefinitionTransfer _instance;

    /**
     * @return the singleton instance (never <code>null</code>)
     */
    public static ChildNodeDefinitionTransfer get() {
        if (_instance == null) {
            _instance = new ChildNodeDefinitionTransfer();
        }

        return _instance;
    }

    /**
     * Don't allow construction outside of this class.
     */
    private ChildNodeDefinitionTransfer() {
        super(ChildNodeDefinitionTransfer.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#getJcrModelObjectClass()
     */
    @Override
    protected Class<? extends JcrModelObject> getJcrModelObjectClass() {
        return ChildNodeDefinition.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#readFromStream(java.io.DataInputStream)
     */
    @Override
    protected JcrModelObject readFromStream( final DataInputStream inStream ) throws Exception {
        // owner qualified name
        final String ownerQualifiedName = inStream.readUTF();
        final ChildNodeDefinition childNodeDefn = new ChildNodeDefinition(new OwnerProvider(ownerQualifiedName));

        // name and comments
        childNodeDefn.setName(inStream.readUTF());
        childNodeDefn.setComment(inStream.readUTF());

        // attributes
        childNodeDefn.setAutoCreated(inStream.readBoolean());
        childNodeDefn.setMandatory(inStream.readBoolean());
        childNodeDefn.setProtected(inStream.readBoolean());
        childNodeDefn.setOnParentVersion(inStream.readInt());
        childNodeDefn.setSameNameSiblings(inStream.readBoolean());

        // default type
        childNodeDefn.setDefaultPrimaryTypeName(inStream.readUTF());

        { // required types
            final int requiredTypesCount = inStream.readInt();

            if (requiredTypesCount != 0) {
                final String[] requiredTypes = new String[requiredTypesCount];

                for (int requiredTypeIndex = 0; requiredTypeIndex < requiredTypesCount; ++requiredTypeIndex) {
                    requiredTypes[requiredTypeIndex] = inStream.readUTF();
                }

                childNodeDefn.setRequiredPrimaryTypeNames(requiredTypes);
            }
        }

        return childNodeDefn;
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
        assert (jcrModelObject instanceof ChildNodeDefinition);
        final ChildNodeDefinition childNodeDefn = (ChildNodeDefinition)jcrModelObject;

        // owner qualified name
        outStream.writeUTF(childNodeDefn.getQualifiedName().get());

        // name and comments
        outStream.writeUTF(UiUtils.ensureNotNull(childNodeDefn.getName()));
        outStream.writeUTF(UiUtils.ensureNotNull(childNodeDefn.getComment()));

        // attributes
        outStream.writeBoolean(childNodeDefn.isAutoCreated());
        outStream.writeBoolean(childNodeDefn.isMandatory());
        outStream.writeBoolean(childNodeDefn.isProtected());
        outStream.writeInt(childNodeDefn.getOnParentVersion());
        outStream.writeBoolean(childNodeDefn.allowsSameNameSiblings());

        // default type
        outStream.writeUTF(UiUtils.ensureNotNull(childNodeDefn.getDefaultPrimaryTypeName()));

        { // required types
            final String[] requiredTypes = childNodeDefn.getRequiredPrimaryTypeNames();

            if ((requiredTypes == null) || (requiredTypes.length == 0)) {
                outStream.writeInt(0);
            } else {
                outStream.writeInt(requiredTypes.length);

                for (final String requiredType : requiredTypes) {
                    outStream.writeUTF(requiredType);
                }
            }
        }
    }
}
