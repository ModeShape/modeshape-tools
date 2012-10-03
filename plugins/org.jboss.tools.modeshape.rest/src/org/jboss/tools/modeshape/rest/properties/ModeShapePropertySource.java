/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.properties;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject;
import org.jboss.tools.modeshape.rest.domain.ModeShapeRepository;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;
import org.modeshape.common.util.CheckArg;

/**
 * The <code>ModeShapePropertySource</code> class is the property source for all ModeShape objects appearing in ModeShape View.
 */
public final class ModeShapePropertySource implements IPropertySource {

    private static final String SERVER_PASSWORD_PERSISTED = "server.passwordPersisted"; //$NON-NLS-1$
    private static final String SERVER_URL = "server.url"; //$NON-NLS-1$
    private static final String SERVER_USER = "server.user"; //$NON-NLS-1$

    private static final String WORKSPACE_NAME = "workspace.name"; //$NON-NLS-1$
    private static final String WORKSPACE_REPOSITORY = "workspace.repository"; //$NON-NLS-1$

    private IPropertyDescriptor[] descriptors;

    /**
     * The object this property source is being used for.
     */
    private final ModeShapeDomainObject modeShapeObject;

    /**
     * @param modeShapeObject the object whose properties are being requested (cannot be <code>null</code>)
     */
    public ModeShapePropertySource( ModeShapeDomainObject modeShapeObject ) {
        CheckArg.isNotNull(modeShapeObject, "modeShapeObject"); //$NON-NLS-1$
        this.modeShapeObject = modeShapeObject;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    @Override
    public Object getEditableValue() {
        return null; // nothing is editable
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (this.descriptors == null) {
            List<IPropertyDescriptor> temp = new ArrayList<IPropertyDescriptor>();

            if (this.modeShapeObject instanceof ModeShapeServer) {
                // add URL
                PropertyDescriptor descriptor = new PropertyDescriptor(SERVER_URL,
                                                                       PropertyDisplayNameProvider.getDisplayName(SERVER_URL));
                descriptor.setCategory(PropertyDisplayNameProvider.getCategory(SERVER_URL));
                temp.add(descriptor);

                // add user
                descriptor = new PropertyDescriptor(SERVER_USER, PropertyDisplayNameProvider.getDisplayName(SERVER_USER));
                descriptor.setCategory(PropertyDisplayNameProvider.getCategory(SERVER_USER));
                temp.add(descriptor);

                // add password persisted
                descriptor = new PropertyDescriptor(SERVER_PASSWORD_PERSISTED,
                                                    PropertyDisplayNameProvider.getDisplayName(SERVER_PASSWORD_PERSISTED));
                descriptor.setCategory(PropertyDisplayNameProvider.getCategory(SERVER_PASSWORD_PERSISTED));
                temp.add(descriptor);
            } else if (this.modeShapeObject instanceof ModeShapeRepository) {
                for (String propKey : ((ModeShapeRepository)this.modeShapeObject).getProperties().keySet()) {
                    PropertyDescriptor descriptor = new PropertyDescriptor(propKey,
                                                                           PropertyDisplayNameProvider.getDisplayName(propKey));
                    descriptor.setCategory(PropertyDisplayNameProvider.getCategory(propKey));
                    temp.add(descriptor);
                }
            } else if (this.modeShapeObject instanceof ModeShapeWorkspace) {
                // add name
                PropertyDescriptor descriptor = new PropertyDescriptor(WORKSPACE_NAME,
                                                                       PropertyDisplayNameProvider.getDisplayName(WORKSPACE_NAME));
                descriptor.setCategory(PropertyDisplayNameProvider.getCategory(WORKSPACE_NAME));
                temp.add(descriptor);

                // add repository name
                descriptor = new PropertyDescriptor(WORKSPACE_REPOSITORY,
                                                    PropertyDisplayNameProvider.getDisplayName(WORKSPACE_REPOSITORY));
                descriptor.setCategory(PropertyDisplayNameProvider.getCategory(WORKSPACE_REPOSITORY));
                temp.add(descriptor);
            }

            this.descriptors = temp.toArray(new IPropertyDescriptor[temp.size()]);
        }

        return this.descriptors;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue( Object id ) {
        if (this.modeShapeObject instanceof ModeShapeServer) {
            ModeShapeServer server = (ModeShapeServer)this.modeShapeObject;

            if (SERVER_URL.equals(id)) {
                return server.getOriginalUrl();
            }

            if (SERVER_USER.equals(id)) {
                return server.getUser();
            }

            if (SERVER_PASSWORD_PERSISTED.equals(id)) {
                return server.isPasswordBeingPersisted();
            }
        } else if (this.modeShapeObject instanceof ModeShapeRepository) {
            Object value = ((ModeShapeRepository)this.modeShapeObject).getProperties().get(id);

            if (value instanceof String) {
                return value;
            }

            if (value.getClass().isArray()) {
                Object[] values = (Object[])value;
                StringBuilder txt = new StringBuilder();

                for (int i = 0, size = values.length; i < size; ++i) {
                    txt.append(values[i]);

                    if (i < (size - 1)) {
                        txt.append(", "); //$NON-NLS-1$
                    }
                }

                return txt.toString();
            }

            return value.toString();
        } else if (this.modeShapeObject instanceof ModeShapeWorkspace) {
            ModeShapeWorkspace workspace = (ModeShapeWorkspace)this.modeShapeObject;

            if (WORKSPACE_NAME.equals(id)) {
                return workspace.getName();
            }

            if (WORKSPACE_REPOSITORY.equals(id)) {
                return workspace.getRepository().getName();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    @Override
    public boolean isPropertySet( Object id ) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    @Override
    public void resetPropertyValue( Object id ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue( Object id,
                                  Object value ) {
        // nothing to do
    }

}
