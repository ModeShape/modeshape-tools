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
package org.jboss.tools.modeshape.rest;

import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.modeshape.common.util.CheckArg;

/**
 * The <code>ServerRegistryEvent</code> class is the event that is broadcast from the {@link ServerManager server manager} when a
 * server is added, removed, or changed.
 */
public final class ServerRegistryEvent {

    /**
     * The status severity levels.
     */
    private enum Type {
        /**
         * Indicates that a new server was added to the server registry.
         */
        NEW,

        /**
         * Indicates that a server was removed from the server registry.
         */
        REMOVE,

        /**
         * Indicates that properties of an existing server in the registry has been changed.
         */
        UPDATE
    }

    /**
     * @param serverManager the server manager sourcing this event (never <code>null</code>)
     * @param newServer the server that was added to the server registry (never <code>null</code>)
     * @return the event (never <code>null</code>)
     * @see Type#NEW
     */
    public static ServerRegistryEvent createNewEvent( ServerManager serverManager,
                                                      ModeShapeServer newServer ) {
        CheckArg.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$
        CheckArg.isNotNull(newServer, "newServer"); //$NON-NLS-1$
        return new ServerRegistryEvent(serverManager, Type.NEW, newServer);
    }

    /**
     * @param serverManager the server manager sourcing this event (never <code>null</code>)
     * @param removedServer the server removed from the server registry (never <code>null</code>)
     * @return the event (never <code>null</code>)
     * @see Type#REMOVE
     */
    public static ServerRegistryEvent createRemoveEvent( ServerManager serverManager,
                                                         ModeShapeServer removedServer ) {
        CheckArg.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$
        CheckArg.isNotNull(removedServer, "removedServer"); //$NON-NLS-1$
        return new ServerRegistryEvent(serverManager, Type.REMOVE, removedServer);
    }

    /**
     * @param serverManager the server manager sourcing this event (never <code>null</code>)
     * @param previousServerVersion the server being updated (never <code>null</code>)
     * @param newServerVersion the updated version of the server (never <code>null</code>)
     * @return the event (never <code>null</code>)
     * @see Type#UPDATE
     */
    public static ServerRegistryEvent createUpdateEvent( ServerManager serverManager,
                                                         ModeShapeServer previousServerVersion,
                                                         ModeShapeServer newServerVersion ) {
        CheckArg.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$
        CheckArg.isNotNull(previousServerVersion, "previousServerVersion"); //$NON-NLS-1$
        CheckArg.isNotNull(newServerVersion, "newServerVersion"); //$NON-NLS-1$

        ServerRegistryEvent event = new ServerRegistryEvent(serverManager, Type.UPDATE, previousServerVersion);
        event.updatedServer = newServerVersion;
        return event;
    }

    /**
     * The server being added, removed, or updated.
     */
    private final ModeShapeServer server;

    /**
     * The server manager in charge of the server registry the event is associated with.
     */
    private final ServerManager serverManager;

    /**
     * The event type.
     */
    private final Type type;

    /**
     * The server that is replacing an existing server. Will be <code>null</code> for all types except {@link Type#UPDATE update}.
     */
    private ModeShapeServer updatedServer;

    /**
     * @param serverManager the server manager sourcing this event
     * @param type the event type
     * @param server the server being added, removed, or updated
     */
    private ServerRegistryEvent( ServerManager serverManager,
                                 Type type,
                                 ModeShapeServer server ) {
        assert (serverManager != null);
        assert (type != null);
        assert (server != null);

        this.serverManager = serverManager;
        this.type = type;
        this.server = server;
    }

    /**
     * @return the added, removed, or the old version of the server that has been updated
     */
    public ModeShapeServer getServer() {
        return this.server;
    }

    /**
     * @return the server manager sourcing this event
     */
    public ServerManager getServerManager() {
        return this.serverManager;
    }

    /**
     * @return the new version of an existing server that has been updated
     * @throws UnsupportedOperationException if method is called when the type is not an update
     * @see Type#UPDATE
     */
    public ModeShapeServer getUpdatedServer() {
        if (this.type != Type.UPDATE) {
            throw new UnsupportedOperationException();
        }

        return this.updatedServer;
    }

    /**
     * @return <code>true</code> if the event is adding a new server to the registry
     * @see Type#NEW
     */
    public boolean isNew() {
        return (this.type == Type.NEW);
    }

    /**
     * @return <code>true</code> if the event is removing a server from the registry
     * @see Type#REMOVE
     */
    public boolean isRemove() {
        return (this.type == Type.REMOVE);
    }

    /**
     * @return <code>true</code> if the event is updating properties of an existing server in the registry
     * @see Type#UPDATE
     */
    public boolean isUpdate() {
        return (this.type == Type.UPDATE);
    }

}
