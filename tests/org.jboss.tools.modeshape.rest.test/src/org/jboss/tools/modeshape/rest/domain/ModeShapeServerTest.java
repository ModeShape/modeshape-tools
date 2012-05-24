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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public final class ModeShapeServerTest {

    private static final ModeShapeServer PERSISTED = new ModeShapeServer("url", "user", "pswd", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final ModeShapeServer NOT_PERSISTED = new ModeShapeServer(PERSISTED.getUrl(), PERSISTED.getUser(),
                                                                             PERSISTED.getPassword(), false);

    @Test
    public void shouldNotBeEqualIfDifferentPasswordSettings() {
        assertThat(PERSISTED.equals(NOT_PERSISTED), is(false));
    }

    @Test
    public void shouldHaveSameHashCodeIfDifferentPasswordSettings() {
        assertThat(PERSISTED.hashCode(), is(NOT_PERSISTED.hashCode()));
    }

    @Test
    public void shouldHavePasswordBeingPersisted() {
        assertThat(PERSISTED.isPasswordBeingPersisted(), is(true));
    }

    @Test
    public void shouldNotHavePasswordBeingPersisted() {
        assertThat(NOT_PERSISTED.isPasswordBeingPersisted(), is(false));
    }

}
