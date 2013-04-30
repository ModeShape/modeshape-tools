/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.jboss.tools.modeshape.jcr.LocalName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class LocalNameTest {

    private LocalName localName;

    @Before
    public void beforeEach() {
        this.localName = new LocalName();
    }

    @Test
    public void initialValueShouldBeCorrectWhenSet() {
        final String INITIAL_VALUE = "initialValue"; //$NON-NLS-1$
        this.localName = new LocalName(INITIAL_VALUE);
        assertEquals(INITIAL_VALUE, this.localName.get());
    }

    @Test
    public void initialValueShouldBeEmpty() {
        assertEquals(Utils.EMPTY_STRING, this.localName.get());
    }

    @Test
    public void sameModesDifferentValuesShouldNotBeEqual() {
        final String VALUE = "value"; //$NON-NLS-1$
        this.localName.set(VALUE);
        final LocalName thatLocalName = new LocalName(VALUE + "Changed"); //$NON-NLS-1$
        assertFalse(this.localName.equals(thatLocalName));
    }

    @Test
    public void sameModesSameValuesShouldBeEqual() {
        final String VALUE = "value"; //$NON-NLS-1$
        this.localName.set(VALUE);
        final LocalName thatLocalName = new LocalName(VALUE);
        assertTrue(this.localName.equals(thatLocalName));
    }

    @Test
    public void shouldHaveCorrectDoubleQuotedCndNotation() {
        final String VALUE = "value"; //$NON-NLS-1$
        this.localName.set(VALUE);
        JcrPreferenceStore.get().set(JcrPreferenceConstants.CndPreference.QUOTE_CHAR, Utils.DOUBLE_QUOTE);
        assertEquals('"' + VALUE + '"', this.localName.toCndNotation(null));
        resetPreference(JcrPreferenceConstants.CndPreference.QUOTE_CHAR);
    }

    @Test
    public void shouldHaveCorrectSingleQuotedCndNotation() {
        final String VALUE = "value"; //$NON-NLS-1$
        this.localName.set(VALUE);
        JcrPreferenceStore.get().set(JcrPreferenceConstants.CndPreference.QUOTE_CHAR, Utils.SINGLE_QUOTE);
        assertEquals('\'' + VALUE + '\'', this.localName.toCndNotation(null));
        resetPreference(JcrPreferenceConstants.CndPreference.QUOTE_CHAR);
    }

    @Test
    public void shouldHaveCorrectUnquotedCndNotation() {
        final String VALUE = "value"; //$NON-NLS-1$
        JcrPreferenceStore.get().set(JcrPreferenceConstants.CndPreference.QUOTE_CHAR, Utils.EMPTY_STRING);
        this.localName.set(VALUE);
        assertEquals(VALUE, this.localName.toCndNotation(null));
        resetPreference(JcrPreferenceConstants.CndPreference.QUOTE_CHAR);
    }

    @Test
    public void shouldHaveSameHashCodeWhenEqual() {
        final LocalName thatLocalName = new LocalName();
        assertEquals(this.localName.hashCode(), thatLocalName.hashCode());

        final String VALUE = "value"; //$NON-NLS-1$
        this.localName.set(VALUE);
        thatLocalName.set(VALUE);
        assertEquals(this.localName.hashCode(), thatLocalName.hashCode());
    }

    @Test
    public void shouldQuoteIfContainsSpacesAndModeIsUnquoted() {
        final String NEW_VALUE = "new value"; //$NON-NLS-1$
        this.localName.set(NEW_VALUE);
        JcrPreferenceStore.get().set(JcrPreferenceConstants.CndPreference.QUOTE_CHAR, Utils.EMPTY_STRING);
        assertEquals('\'' + NEW_VALUE + '\'', this.localName.toCndNotation(null));
        resetPreference(JcrPreferenceConstants.CndPreference.QUOTE_CHAR);
    }

    @Test
    public void shouldSetValue() {
        final String NEW_VALUE = "newValue"; //$NON-NLS-1$
        this.localName.set(NEW_VALUE);
        assertEquals(NEW_VALUE, this.localName.get());
    }

    @Test
    public void shouldSetValueToEmpty() {
        this.localName.set(Utils.EMPTY_STRING);
        assertEquals(Utils.EMPTY_STRING, this.localName.get());
    }

    @Test
    public void shouldSetValueToNull() {
        final String NULL_VALUE = null;
        this.localName.set(NULL_VALUE);
        assertNull(this.localName.get());
    }

    private void resetPreference( final JcrPreferenceConstants.CndPreference pref ) {
        final JcrPreferenceStore prefStore = JcrPreferenceStore.get();
        prefStore.set(pref, prefStore.getDefault(pref));
    }

}
