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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.jboss.tools.modeshape.rest.domain.ModeShapeRepository;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.domain.Repository;

/**
 * The <code>ServerManagerTest</code> class is a test class for the {@link ServerManager server manager} object.
 */
public final class ServerManagerTest {

    private static final String URL1 = "file:/tmp/temp.txt"; //$NON-NLS-1$
    private static final String URL2 = "http:www.redhat.com"; //$NON-NLS-1$

    private static final String USER1 = "user1"; //$NON-NLS-1$
    private static final String USER2 = "user2"; //$NON-NLS-1$

    private static final String PSWD1 = "pwsd1"; //$NON-NLS-1$
    private static final String PSWD2 = "pwsd2"; //$NON-NLS-1$

    private static ModeShapeServer SERVER1 = new ModeShapeServer(URL1, USER1, PSWD1, false);
    private static ModeShapeServer SERVER1_UPDATE = new ModeShapeServer(SERVER1.getOriginalUrl(),
                                                                        SERVER1.getUser(),
                                                                        SERVER1.getPassword(),
                                                                        SERVER1.isPasswordBeingPersisted());
    private static ModeShapeServer SERVER2 = new ModeShapeServer(URL2, USER2, PSWD2, !SERVER1.isPasswordBeingPersisted());

    private ServerManager serverManager;

    @Before
    public void beforeEach() {
        this.serverManager = new ServerManager(null, new MockRestClient());
    }

    @Test
    public void shouldBeRegisteredIfAdded() {
        this.serverManager.addServer(SERVER1);
        assertTrue(this.serverManager.isRegistered(SERVER1));
        assertEquals(1, this.serverManager.getServers().size());
    }

    @Test
    public void shouldBeRegisteredIfServerWithSameKeyHasBeenAdded() {
        this.serverManager.addServer(SERVER1);
        assertThat(this.serverManager.isRegistered(new ModeShapeServer(SERVER1.getOriginalUrl(),
                                                                       SERVER1.getUser(),
                                                                       PSWD2,
                                                                       SERVER1.isPasswordBeingPersisted())), is(true));
        assertEquals(1, this.serverManager.getServers().size());
    }

    @Test
    public void shouldDecreaseRegistrySizeWhenServerRemoved() {
        this.serverManager.addServer(SERVER1);
        this.serverManager.addServer(SERVER2);

        this.serverManager.removeServer(SERVER1);
        assertEquals(1, this.serverManager.getServers().size());

        this.serverManager.removeServer(SERVER2);
        assertTrue(this.serverManager.getServers().isEmpty());
    }

    @Test
    public void shouldHaveOkStatusWhenAddingServerSuccessfully() {
        assertTrue(this.serverManager.addServer(SERVER1).isOk());
        assertEquals(1, this.serverManager.getServers().size());
    }

    @Test
    public void shouldHaveOkStatusWhenRemovingServerSuccessfully() {
        this.serverManager.addServer(SERVER1);
        assertTrue(this.serverManager.removeServer(SERVER1).isOk());
        assertTrue(this.serverManager.getServers().isEmpty());
    }

    @Test
    public void shouldHaveServerExistsSystemPropertyIfThereIsARegisteredServer() {
        this.serverManager.addServer(SERVER1);
        assertThat(System.getProperty(ServerManager.SERVER_EXISTS_PROPERTY), is(notNullValue()));
    }

    @Test
    public void shouldHaveOkStatusWhenUpdateServerSuccessfully() {
        this.serverManager.addServer(SERVER1);
        assertTrue(this.serverManager.updateServer(SERVER1, SERVER1_UPDATE).isOk());
        assertEquals(1, this.serverManager.getServers().size());
    }

    @Test
    public void shouldIncreaseRegistrySizeWhenServerAdded() {
        this.serverManager.addServer(SERVER1);
        assertEquals(1, this.serverManager.getServers().size());

        this.serverManager.addServer(SERVER2);
        assertEquals(2, this.serverManager.getServers().size());
    }

    @Test
    public void shouldNotAddServerIfAlreadyAdded() {
        this.serverManager.addServer(SERVER1);
        assertFalse(this.serverManager.addServer(SERVER1).isOk());
        assertEquals(1, this.serverManager.getServers().size());
    }

    @Test
    public void shouldNotAddServerIfKeysMatch() {
        this.serverManager.addServer(SERVER1);
        Status status = this.serverManager.addServer(new ModeShapeServer(SERVER1.getOriginalUrl(), SERVER1.getUser(), PSWD2, true));
        assertThat(status.isOk(), is(false));
        assertThat(this.serverManager.getServers().size(), is(1));
    }

    @Test
    public void shouldNotBeRegisteredIfNotAdded() {
        this.serverManager.addServer(SERVER1);
        assertFalse(this.serverManager.isRegistered(SERVER2));
        assertEquals(1, this.serverManager.getServers().size());
    }

    @Test
    public void shouldNotBeRegisteredIfRemoved() {
        this.serverManager.addServer(SERVER1);
        this.serverManager.removeServer(SERVER1);
        assertFalse(this.serverManager.isRegistered(SERVER1));
        assertTrue(this.serverManager.getServers().isEmpty());
    }

    @Test
    public void shouldNotHaveServerExistsSystemPropertyIfNoRegisteredServers() {
        // make sure initially property doesn't exist
        assertThat(System.getProperty(ServerManager.SERVER_EXISTS_PROPERTY), is(nullValue()));
    }

    @Test
    public void shouldNotReceiveNotificationIfListenerUnregistered() {
        RegistryListener listener = new RegistryListener();

        this.serverManager.addRegistryListener(listener);
        this.serverManager.removeRegistryListener(listener);
        this.serverManager.addServer(SERVER1);
        assertFalse(listener.wasNotified());
    }

    @Test
    public void shouldNotRemoveServerIfNotAdded() {
        assertFalse(this.serverManager.removeServer(SERVER1).isOk());
        assertTrue(this.serverManager.getServers().isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotObtainRepositoriesForUnregisteredServer() throws Exception {
        this.serverManager.getRepositories(SERVER1);
    }

    @Test(expected = Exception.class)
    public void shouldNotBeAbleToConstructRepositoryForUnreachableServer() throws Exception {
        new ModeShapeRepository(new Repository("repo", SERVER1.getDelegate()), SERVER1);//$NON-NLS-1$
    }

    @Test
    public void shouldReceiveOneEventIfRegisteredMoreThanOnce() {
        RegistryListener listener = new RegistryListener();
        this.serverManager.addRegistryListener(listener);
        this.serverManager.addRegistryListener(listener);

        this.serverManager.addServer(SERVER1);
        assertTrue(listener.wasNotified());
    }

    @Test
    public void shouldReceiveNotificationIfRegisteredListener() {
        RegistryListener listener = new RegistryListener();
        assertTrue(this.serverManager.addRegistryListener(listener));

        this.serverManager.addServer(SERVER1);
        assertTrue(listener.wasNotified());
    }

    @Test
    public void shouldReceiveNewServerEvent() {
        RegistryListener listener = new RegistryListener();
        this.serverManager.addRegistryListener(listener);

        this.serverManager.addServer(SERVER1);
        assertTrue(listener.getEvent().isNew());
        assertFalse(listener.getEvent().isRemove());
        assertFalse(listener.getEvent().isUpdate());
    }

    @Test
    public void shouldReceiveRemoveServerEvent() {
        this.serverManager.addServer(SERVER1);

        RegistryListener listener = new RegistryListener();
        this.serverManager.addRegistryListener(listener);

        this.serverManager.removeServer(SERVER1);
        assertTrue(listener.getEvent().isRemove());
        assertFalse(listener.getEvent().isNew());
        assertFalse(listener.getEvent().isUpdate());
    }

    @Test
    public void shouldReceiveUpdateServerEvent() {
        this.serverManager.addServer(SERVER1);

        RegistryListener listener = new RegistryListener();
        this.serverManager.addRegistryListener(listener);

        this.serverManager.updateServer(SERVER1,
                                        new ModeShapeServer(SERVER1.getOriginalUrl(),
                                                            SERVER1.getUser(),
                                                            PSWD2,
                                                            !SERVER1.isPasswordBeingPersisted()));
        assertTrue(listener.getEvent().isUpdate());
        assertFalse(listener.getEvent().isNew());
        assertFalse(listener.getEvent().isRemove());
    }

    @Test
    public void shouldRemoveServerIfNotAddedButKeyMatches() {
        this.serverManager.addServer(SERVER1_UPDATE);
        assertTrue(this.serverManager.removeServer(SERVER1).isOk());
        assertTrue(this.serverManager.getServers().isEmpty());
    }

    class RegistryListener implements IServerRegistryListener {
        boolean[] notified = new boolean[] { false };
        ServerRegistryEvent event = null;

        @Override
        public Exception[] serverRegistryChanged( ServerRegistryEvent event ) {
            notified[0] = !notified[0];
            this.event = event;
            return null;
        }

        public ServerRegistryEvent getEvent() {
            return this.event;
        }

        public boolean wasNotified() {
            return notified[0];
        }
    }

}
