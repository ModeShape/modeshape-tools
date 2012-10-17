/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.preferences;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 */
public final class PublishingFileFilterTest {

    private static final String PARTS_PROJECT = "PartsProject"; //$NON-NLS-1$

    @Test
    public void shouldMatchPartsProject() {
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "PartsProject"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "Parts*"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "*Parts*"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "?arts*"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "Part?*"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "Part?*t"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "*Project"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "P*r*s*r*j*c*"), is(true)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "?a?t?P?o?e?t"), is(true)); //$NON-NLS-1$
    }

    @Test
    public void shouldNotMatchPartsProject() {
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, " PartsProject"), is(false)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "PartsProject "), is(false)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "PartsProject2"), is(false)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "artsProject2"), is(false)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "Parts?"), is(false)); //$NON-NLS-1$
        assertThat(PublishingFileFilter.matches(PARTS_PROJECT, "*artsP*2"), is(false)); //$NON-NLS-1$
    }

}
