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
public final class ResourcePatternTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullPattern() {
        new ResourcePattern(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowEmptyPattern() {
        new ResourcePattern("", true); //$NON-NLS-1$
    }

    @Test
    public void shouldBeEqualWhenPatternsAreEqual() {
        final String pattern = "mypattern"; //$NON-NLS-1$
        ResourcePattern rp1 = new ResourcePattern(pattern, true);
        ResourcePattern rp2 = new ResourcePattern(pattern, false);
        assertThat(rp1.equals(rp2), is(true));
    }

    @Test
    public void shouldNotBeEqualWhenComparingWithNull() {
        ResourcePattern rp = new ResourcePattern("mypattern", true); //$NON-NLS-1$
        assertThat(rp.equals(null), is(false));
    }

    @Test
    public void shouldNotBeEqualWhenPatternsAreNotEqual() {
        ResourcePattern rp1 = new ResourcePattern("mypattern", true); //$NON-NLS-1$
        ResourcePattern rp2 = new ResourcePattern(rp1.getPattern() + "abc", rp1.isEnabled()); //$NON-NLS-1$
        assertThat(rp1.equals(rp2), is(false));
    }

    @Test
    public void shouldNotBeEqualWhenComparingWithDifferentClass() {
        final String pattern = "mypattern"; //$NON-NLS-1$
        ResourcePattern rp = new ResourcePattern(pattern, true);
        assertThat(rp.equals(pattern), is(false));
    }

    @Test
    public void shouldSetPatternCorrectly() {
        final String pattern = "mypattern"; //$NON-NLS-1$
        ResourcePattern rp = new ResourcePattern(pattern, true);
        assertThat(pattern, is(rp.getPattern()));
    }

    @Test
    public void shouldSetEnabledStateCorrectlyAtConstruction() {
        ResourcePattern rp = new ResourcePattern("mypattern", true); //$NON-NLS-1$
        assertThat(rp.isEnabled(), is(true));

        rp = new ResourcePattern("mypattern", false); //$NON-NLS-1$
        assertThat(rp.isEnabled(), is(false));
    }

    @Test
    public void shouldSetEnabledStateCorrectly() {
        ResourcePattern rp = new ResourcePattern("mypattern", true); //$NON-NLS-1$
        rp.setEnabled(false);
        assertThat(rp.isEnabled(), is(false));

        rp = new ResourcePattern("mypattern", false); //$NON-NLS-1$
        rp.setEnabled(true);
        assertThat(rp.isEnabled(), is(true));
    }

}
