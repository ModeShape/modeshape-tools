/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.preferences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 */
public final class PublishingFileFilterTest {

    private static final String PARTS_PROJECT = "PartsProject"; //$NON-NLS-1$

    @Test
    public void shouldMatchPartsProject() {
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "PartsProject")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "Parts*")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "*Parts*")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "?arts*")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "Part?*")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "Part?*t")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "*Project")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "P*r*s*r*j*c*")); //$NON-NLS-1$
        assertTrue(PublishingFileFilter.matches(PARTS_PROJECT, "?a?t?P?o?e?t")); //$NON-NLS-1$
    }

    @Test
    public void shouldNotMatchPartsProject() {
        assertFalse(PublishingFileFilter.matches(PARTS_PROJECT, " PartsProject")); //$NON-NLS-1$
        assertFalse(PublishingFileFilter.matches(PARTS_PROJECT, "PartsProject ")); //$NON-NLS-1$
        assertFalse(PublishingFileFilter.matches(PARTS_PROJECT, "PartsProject2")); //$NON-NLS-1$
        assertFalse(PublishingFileFilter.matches(PARTS_PROJECT, "artsProject2")); //$NON-NLS-1$
        assertFalse(PublishingFileFilter.matches(PARTS_PROJECT, "Parts?")); //$NON-NLS-1$
        assertFalse(PublishingFileFilter.matches(PARTS_PROJECT, "*artsP*2")); //$NON-NLS-1$
    }

}
