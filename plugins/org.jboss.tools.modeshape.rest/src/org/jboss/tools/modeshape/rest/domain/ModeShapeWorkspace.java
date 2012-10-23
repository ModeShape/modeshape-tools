/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.domain;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;
import org.jboss.tools.modeshape.rest.properties.ModeShapePropertySource;
import org.modeshape.common.annotation.Immutable;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.domain.Workspace;

/**
 * The <code>ModeShapeWorkspace</code> is a UI wrapper for a ModeShape workspace.
 */
@Immutable
public final class ModeShapeWorkspace implements IAdaptable, ModeShapeDomainObject {

    /**
     * The ModeShape server's workspace object (never <code>null</code>).
     */
    private final Workspace delegate;

    /**
     * The wrapped ModeShape server's repository object (never <code>null</code>).
     */
    private final ModeShapeRepository repository;

    /**
     * @param workspace the ModeShape server's workspace object being wrapped (cannot be <code>null</code>)
     * @param repository the wrapped ModeShape repository object (cannot be <code>null</code>)
     */
    public ModeShapeWorkspace( Workspace workspace,
                               ModeShapeRepository repository ) {
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$
        CheckArg.isNotNull(repository, "repository"); //$NON-NLS-1$

        this.delegate = workspace;
        this.repository = repository;
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

        ModeShapeWorkspace that = (ModeShapeWorkspace)obj;
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
     * @return the ModeShape server's workspace object (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the workspace delegate
     */
    public Workspace getDelegate() throws Exception {
        getServer().validateDelegate(); // make sure repository's server has been validate
        return this.delegate;
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
     * @return the wrapped repository (never <code>null</code>)
     */
    public ModeShapeRepository getRepository() {
        return this.repository;
    }

    /**
     * @return the wrapped server (never <code>null</code>)
     */
    public ModeShapeServer getServer() {
        return this.repository.getServer();
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
