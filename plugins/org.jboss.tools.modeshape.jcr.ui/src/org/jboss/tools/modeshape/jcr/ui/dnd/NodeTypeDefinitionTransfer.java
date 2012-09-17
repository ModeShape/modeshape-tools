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
import java.util.List;
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.JcrModelObject;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A class used to read and write {@link org.jboss.tools.modeshape.jcr.NodeTypeDefinition}s to and from the system clipboard.
 */
public final class NodeTypeDefinitionTransfer extends JcrTransfer {

    /**
     * The shared instance.
     */
    private static NodeTypeDefinitionTransfer _instance;

    /**
     * @return the singleton instance (never <code>null</code>)
     */
    public static NodeTypeDefinitionTransfer get() {
        if (_instance == null) {
            _instance = new NodeTypeDefinitionTransfer();
        }

        return _instance;
    }

    /**
     * Don't allow construction outside of this class.
     */
    private NodeTypeDefinitionTransfer() {
        super(NodeTypeDefinitionTransfer.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#getJcrModelObjectClass()
     */
    @Override
    protected Class<? extends JcrModelObject> getJcrModelObjectClass() {
        return NodeTypeDefinition.class;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#readFromStream(java.io.DataInputStream)
     */
    @Override
    protected JcrModelObject readFromStream( final DataInputStream inStream ) throws Exception {
        final NodeTypeDefinition nodeTypeDefn = new NodeTypeDefinition();

        // name and comments
        nodeTypeDefn.setName(inStream.readUTF());
        nodeTypeDefn.setComment(inStream.readUTF());

        // attributes
        nodeTypeDefn.setAbstract(inStream.readBoolean());
        nodeTypeDefn.setMixin(inStream.readBoolean());
        nodeTypeDefn.setOrderableChildNodes(inStream.readBoolean());
        nodeTypeDefn.setQueryable(inStream.readBoolean());
        nodeTypeDefn.setPrimaryItemName(inStream.readUTF());

        { // supertypes
            final int superTypesCount = inStream.readInt();

            if (superTypesCount != 0) {
                for (int superTypeIndex = 0; superTypeIndex < superTypesCount; ++superTypeIndex) {
                    nodeTypeDefn.addSuperType(inStream.readUTF());
                }
            }
        }

        { // property definitions
            final int propDefnsCount = inStream.readInt();

            if (propDefnsCount != 0) {
                final PropertyDefinitionTransfer propTransfer = PropertyDefinitionTransfer.get();

                for (int propDefnIndex = 0; propDefnIndex < propDefnsCount; ++propDefnIndex) {
                    final Object propDefn = propTransfer.readFromStream(inStream);
                    assert (propDefn instanceof PropertyDefinition);
                    nodeTypeDefn.addPropertyDefinition((PropertyDefinition)propDefn);
                }
            }
        }

        { // property definitions
            final int childNodeDefnsCount = inStream.readInt();

            if (childNodeDefnsCount != 0) {
                final ChildNodeDefinitionTransfer propTransfer = ChildNodeDefinitionTransfer.get();

                for (int childNodeDefnIndex = 0; childNodeDefnIndex < childNodeDefnsCount; ++childNodeDefnIndex) {
                    final Object childNodeDefn = propTransfer.readFromStream(inStream);
                    assert (childNodeDefn instanceof ChildNodeDefinition);
                    nodeTypeDefn.addChildNodeDefinition((ChildNodeDefinition)childNodeDefn);
                }
            }
        }

        return nodeTypeDefn;
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
        assert (jcrModelObject instanceof NodeTypeDefinition);
        final NodeTypeDefinition nodeTypeDefn = (NodeTypeDefinition)jcrModelObject;

        // name and comments
        outStream.writeUTF(UiUtils.ensureNotNull(nodeTypeDefn.getName()));
        outStream.writeUTF(UiUtils.ensureNotNull(nodeTypeDefn.getComment()));

        // attributes
        outStream.writeBoolean(nodeTypeDefn.isAbstract());
        outStream.writeBoolean(nodeTypeDefn.isMixin());
        outStream.writeBoolean(nodeTypeDefn.hasOrderableChildNodes());
        outStream.writeBoolean(nodeTypeDefn.isQueryable());
        outStream.writeUTF(UiUtils.ensureNotNull(nodeTypeDefn.getPrimaryItemName()));

        { // supertypes
            final String[] superTypes = nodeTypeDefn.getDeclaredSupertypeNames();

            if ((superTypes == null) || (superTypes.length == 0)) {
                outStream.writeInt(0);
            } else {
                outStream.writeInt(superTypes.length);

                for (final String superType : superTypes) {
                    outStream.writeUTF(superType);
                }
            }
        }

        { // property definitions
            final List<PropertyDefinition> propertyDefinitions = nodeTypeDefn.getPropertyDefinitions();

            if (propertyDefinitions.isEmpty()) {
                outStream.writeInt(0);
            } else {
                final PropertyDefinitionTransfer propTransfer = PropertyDefinitionTransfer.get();
                final int length = propertyDefinitions.size();
                outStream.writeInt(length);

                for (final PropertyDefinition propDefn : propertyDefinitions) {
                    propTransfer.writeToStream(propDefn, outStream);
                }
            }
        }

        { // child node definitions
            final List<ChildNodeDefinition> childNodeDefinitions = nodeTypeDefn.getChildNodeDefinitions();

            if (childNodeDefinitions.isEmpty()) {
                outStream.writeInt(0);
            } else {
                final ChildNodeDefinitionTransfer childNodeTransfer = ChildNodeDefinitionTransfer.get();
                final int length = childNodeDefinitions.size();
                outStream.writeInt(length);

                for (final ChildNodeDefinition childNodeDefn : childNodeDefinitions) {
                    childNodeTransfer.writeToStream(childNodeDefn, outStream);
                }
            }
        }
    }
}
