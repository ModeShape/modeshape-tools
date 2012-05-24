/*
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.
 *
 * This software is made available by Red Hat, Inc. under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution and is
 * available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 */
package org.jboss.tools.modeshape.rest.domain;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;
import org.jboss.tools.modeshape.rest.properties.ModeShapePropertySource;
import org.modeshape.web.jcr.rest.client.domain.Server;

/**
 * The <code>ModeShapeServer</code> class adds the concept of allowing a server's password to be persisted or not.
 */
@Immutable
public final class ModeShapeServer implements IAdaptable, ModeShapeDomainObject {

    /**
     * The ModeShape server's server object (never <code>null</code>).
     */
    private final Server delegate;

    /**
     * Indicates if the password should be stored locally when the server is persisted.
     */
    private final boolean persistPassword;

    /**
     * Constructs a new <code>ModeShapeServer</code>.
     * 
     * @param url the server URL (never <code>null</code>)
     * @param user the server user (never <code>null</code>)
     * @param password the server password (may be <code>null</code>)
     * @param persistPassword <code>true</code> if the password should be stored
     * @throws IllegalArgumentException if URL or user is <code>null</code> or empty
     */
    public ModeShapeServer( String url,
                            String user,
                            String password,
                            boolean persistPassword ) {
        this.delegate = new Server(url, user, password);
        this.persistPassword = persistPassword;
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

        ModeShapeServer that = (ModeShapeServer)obj;

        if (this.delegate.equals(that.delegate)) {
            return (this.persistPassword == that.persistPassword);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter == IPropertySource.class) {
            return new ModeShapePropertySource(this);
        }

        return null;
    }

    /**
     * @return the ModeShape server's server object (never <code>null</code>)
     */
    public Server getDelegate() {
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
     * @return the user's server password (maybe <code>null</code> or empty)
     * @see Server#getPassword()
     */
    public String getPassword() {
        return this.delegate.getPassword();
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
     * @return the server URL (never <code>null</code>)
     * @see Server#getUrl()
     */
    public String getUrl() {
        return this.delegate.getUrl();
    }

    /**
     * @return the server's user login (never <code>null</code>)
     * @see Server#getUser()
     */
    public String getUser() {
        return this.delegate.getUser();
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
     * @param server the server whose key is being compared to
     * @return <code>true</code> if the server's have the same key
     * @see Server#hasSameKey(Server)
     */
    public boolean hasSameKey( ModeShapeServer server ) {
        return this.delegate.hasSameKey(server.getDelegate());
    }

    /**
     * @return persistPassword <code>true</code> if the password is being persisted
     */
    public boolean isPasswordBeingPersisted() {
        return this.persistPassword;
    }

}
