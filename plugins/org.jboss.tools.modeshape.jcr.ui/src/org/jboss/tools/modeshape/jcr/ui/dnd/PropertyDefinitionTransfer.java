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
import org.jboss.tools.modeshape.jcr.JcrModelObject;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * A class used to read and write {@link org.jboss.tools.modeshape.jcr.PropertyDefinition}s to and from the system clipboard.
 */
public final class PropertyDefinitionTransfer extends JcrTransfer {

    /**
     * The shared instance.
     */
    private static PropertyDefinitionTransfer _instance;

    /**
     * @return the singleton instance (never <code>null</code>)
     */
    public static PropertyDefinitionTransfer get() {
        if (_instance == null) {
            _instance = new PropertyDefinitionTransfer();
        }

        return _instance;
    }

    /**
     * Don't allow construction outside of this class.
     */
    private PropertyDefinitionTransfer() {
        super(PropertyDefinitionTransfer.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.ui.dnd.JcrTransfer#getJcrModelObjectClass()
     */
    @Override
    protected Class<? extends JcrModelObject> getJcrModelObjectClass() {
        return PropertyDefinition.class;
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
        final PropertyDefinition propDefn = new PropertyDefinition(new OwnerProvider(ownerQualifiedName));

        // name and comments
        propDefn.setName(inStream.readUTF());
        propDefn.setComment(inStream.readUTF());

        // attributes
        propDefn.setAutoCreated(inStream.readBoolean());
        propDefn.setMandatory(inStream.readBoolean());
        propDefn.setMultiple(inStream.readBoolean());
        propDefn.setFullTextSearchable(inStream.readBoolean());
        propDefn.setQueryOrderable(inStream.readBoolean());
        propDefn.setProtected(inStream.readBoolean());
        propDefn.setOnParentVersion(inStream.readInt());

        { // query operators attribute
            final int queryOpsCount = inStream.readInt();

            if (queryOpsCount != 0) {
                final String[] queryOps = new String[queryOpsCount];

                for (int queryOpIndex = 0; queryOpIndex < queryOpsCount; ++queryOpIndex) {
                    queryOps[queryOpIndex] = inStream.readUTF();
                }

                propDefn.setAvailableQueryOperators(queryOps);
            }
        }

        { // default values
            final int defaultValuesCount = inStream.readInt();

            if (defaultValuesCount != 0) {
                for (int defaultValueIndex = 0; defaultValueIndex < defaultValuesCount; ++defaultValueIndex) {
                    propDefn.addDefaultValue(inStream.readUTF());
                }
            }
        }

        // required type
        propDefn.setRequiredType(inStream.readInt());

        { // value constraints
            final int valueConstraintsCount = inStream.readInt();

            if (valueConstraintsCount != 0) {
                for (int valueConstraintIndex = 0; valueConstraintIndex < valueConstraintsCount; ++valueConstraintIndex) {
                    propDefn.addValueConstraint(inStream.readUTF());
                }
            }
        }

        return propDefn;
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
        assert (jcrModelObject instanceof PropertyDefinition);
        final PropertyDefinition propDefn = (PropertyDefinition)jcrModelObject;

        // owner qualified name
        outStream.writeUTF(propDefn.getQualifiedName().get());

        // name and comments
        outStream.writeUTF(UiUtils.ensureNotNull(propDefn.getName()));
        outStream.writeUTF(UiUtils.ensureNotNull(propDefn.getComment()));

        // attributes
        outStream.writeBoolean(propDefn.isAutoCreated());
        outStream.writeBoolean(propDefn.isMandatory());
        outStream.writeBoolean(propDefn.isMultiple());
        outStream.writeBoolean(propDefn.isFullTextSearchable());
        outStream.writeBoolean(propDefn.isQueryOrderable());
        outStream.writeBoolean(propDefn.isProtected());
        outStream.writeInt(propDefn.getOnParentVersion());

        { // query operators attribute
            final String[] queryOps = propDefn.getAvailableQueryOperators();

            if ((queryOps == null) || (queryOps.length == 0)) {
                outStream.writeInt(0);
            } else {
                outStream.writeInt(queryOps.length);

                for (final String queryOp : queryOps) {
                    outStream.writeUTF(queryOp);
                }
            }
        }

        { // default values
            final List<String> defaultValues = propDefn.getDefaultValuesAsStrings();

            if (defaultValues.isEmpty()) {
                outStream.writeInt(0);
            } else {
                final int length = defaultValues.size();
                outStream.writeInt(length);

                for (final String defaultValue : defaultValues) {
                    outStream.writeUTF(defaultValue);
                }
            }
        }

        // required type
        outStream.writeInt(propDefn.getRequiredType());

        { // value constraints
            final String[] valueConstraints = propDefn.getValueConstraints();

            if ((valueConstraints == null) || (valueConstraints.length == 0)) {
                outStream.writeInt(0);
            } else {
                outStream.writeInt(valueConstraints.length);

                for (final String valueConstraint : valueConstraints) {
                    outStream.writeUTF(valueConstraint);
                }
            }
        }
    }
}
