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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.junit.Before;
import org.junit.Test;

public final class ServerRegistryEventTest {

    private static ModeShapeServer SERVER = new ModeShapeServer("url", "user", "pswd", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static ModeShapeServer UPDATED_SERVER = new ModeShapeServer("newurl", "newuser", "newpswd", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private ServerManager serverManager;

    @Before
    public void beforeEach() {
        this.serverManager = new ServerManager(null, new MockRestClient());
    }

    @Test
    public void shouldHaveNewType() {
        assertTrue(ServerRegistryEvent.createNewEvent(this.serverManager, SERVER).isNew());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerManagerWhenCreatingNewEvent() {
        ServerRegistryEvent.createNewEvent(null, SERVER);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerWhenCreatingNewEvent() {
        ServerRegistryEvent.createNewEvent(this.serverManager, null);
    }

    @Test( expected = UnsupportedOperationException.class )
    public void shouldNotBeAllowedToGetUpdatedServerOnNewEvent() {
        ServerRegistryEvent.createNewEvent(this.serverManager, SERVER).getUpdatedServer();
    }

    @Test
    public void shouldGetSameServerOnNewEvent() {
        assertThat(SERVER, is(sameInstance(ServerRegistryEvent.createNewEvent(this.serverManager, SERVER).getServer())));
    }

    @Test
    public void shouldHaveRemoveType() {
        assertTrue(ServerRegistryEvent.createRemoveEvent(this.serverManager, SERVER).isRemove());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerManagerWhenCreatingRemoveEvent() {
        ServerRegistryEvent.createRemoveEvent(null, SERVER);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerWhenCreatingRemoveEvent() {
        ServerRegistryEvent.createRemoveEvent(this.serverManager, null);
    }

    @Test( expected = UnsupportedOperationException.class )
    public void shouldNotBeAllowedToGetUpdatedServerOnRemoveEvent() {
        ServerRegistryEvent.createRemoveEvent(this.serverManager, SERVER).getUpdatedServer();
    }

    @Test
    public void shouldGetSameServerOnRemoveEvent() {
        assertThat(SERVER, is(sameInstance(ServerRegistryEvent.createRemoveEvent(this.serverManager, SERVER).getServer())));
    }

    @Test
    public void shouldHaveUpdateType() {
        assertTrue(ServerRegistryEvent.createUpdateEvent(this.serverManager, SERVER, UPDATED_SERVER).isUpdate());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerManagerWhenCreatingUpdateEvent() {
        ServerRegistryEvent.createUpdateEvent(null, SERVER, UPDATED_SERVER);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServersWhenCreatingUpdateEvent() {
        ServerRegistryEvent.createUpdateEvent(this.serverManager, null, null);
    }

    @Test
    public void shouldGetSameServerOnUpdateEvent() {
        assertThat(SERVER,
                   is(sameInstance(ServerRegistryEvent.createUpdateEvent(this.serverManager, SERVER, UPDATED_SERVER).getServer())));
    }

    @Test
    public void shouldGetSameUpdatedServerOnUpdateEvent() {
        assertThat(UPDATED_SERVER,
                   is(sameInstance(ServerRegistryEvent.createUpdateEvent(this.serverManager, SERVER, UPDATED_SERVER).getUpdatedServer())));
    }

}
