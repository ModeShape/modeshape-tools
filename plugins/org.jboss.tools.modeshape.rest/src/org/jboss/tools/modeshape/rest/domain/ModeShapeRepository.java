/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.domain;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;
import org.jboss.tools.modeshape.rest.properties.ModeShapePropertySource;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.domain.Repository;

/**
 *
 */
public final class ModeShapeRepository implements IAdaptable, ModeShapeDomainObject {

    /**
     * The ModeShape server's repository object (never <code>null</code>).
     */
    private final Repository delegate;

    /**
     * The wrapped ModeShape server's server object (never <code>null</code>).
     */
    private final ModeShapeServer server;

    /**
     * @param repository the ModeShape server's repository object being wrapped (cannot be <code>null</code>)
     * @param server the wrapped ModeShape server object (cannot be <code>null</code>)
     */
    public ModeShapeRepository( Repository repository,
                                ModeShapeServer server ) {
        CheckArg.isNotNull(repository, "repository"); //$NON-NLS-1$
        CheckArg.isNotNull(server, "server"); //$NON-NLS-1$

        this.delegate = repository;
        this.server = server;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        ModeShapeRepository that = (ModeShapeRepository)obj;
        return this.delegate.equals(that.delegate);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( @SuppressWarnings( "rawtypes" ) Class adapter ) {
        if (adapter == IPropertySource.class) {
            return new ModeShapePropertySource(this);
        }

        return null;
    }

    /**
     * @return the ModeShape server's repository object (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the repository delegate
     */
    public Repository getDelegate() throws Exception {
        getServer().validateDelegate(); // make sure repository's server has been validate
        return this.delegate;
    }

    /**
     * @return the repository properties (never <code>null</code>)
     */
    public Map<String, Object> getProperties() {
        return this.delegate.getMetadata();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject#getName()
     */
    @Override
    public String getName() {
        return this.delegate.getName();
    }

    /**
     * @return the wrapped server (never <code>null</code>)
     */
    public ModeShapeServer getServer() {
        return this.server;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject#getShortDescription()
     */
    @Override
    public String getShortDescription() {
        return this.delegate.getShortDescription();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.delegate.toString();
    }

}
