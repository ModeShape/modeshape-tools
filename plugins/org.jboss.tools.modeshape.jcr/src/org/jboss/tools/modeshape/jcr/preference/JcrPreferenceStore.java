/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.preference;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.modeshape.jcr.Activator;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants.CndPreference;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants.Preference;
import org.osgi.service.prefs.BackingStoreException;

/**
 * The <code>JcrPreferenceStore</code> can be used to access the ModeShape JCR plugin preference store. Can be used when the Eclipse
 * platform is running or not.
 */
public final class JcrPreferenceStore extends AbstractPreferenceInitializer {

    private static JcrPreferenceStore _store;

    /**
     * @param listener the listener being registered (cannot be <code>null</code>)
     */
    public static void addPreferenceChangeListener( final IPreferenceChangeListener listener ) {
        Utils.verifyIsNotNull(listener, "listener"); //$NON-NLS-1$
        get().prefs.addPreferenceChangeListener(listener);
    }

    /**
     * @return the shared preference store (never <code>null</code>)
     */
    public static JcrPreferenceStore get() {
        if (_store == null) {
            _store = new JcrPreferenceStore();
        }

        return _store;
    }

    /**
     * @param listener the listener being unregistered (cannot be <code>null</code>)
     */
    public static void removePreferenceChangeListener( final IPreferenceChangeListener listener ) {
        Utils.verifyIsNotNull(listener, "listener"); //$NON-NLS-1$
        get().prefs.removePreferenceChangeListener(listener);
    }

    private final boolean eclipse;
    private final IEclipsePreferences prefs;
    private final Map<String, String> testDefaultPrefs;
    private final Map<String, String> testPrefs;

    /**
     * <strong>Only should be called by the Eclipse platform when initializing default preferences. Use the {@link #get()} method to
     * access an instance of this store.</strong>
     */
    public JcrPreferenceStore() {
        if (Platform.isRunning()) {
            this.eclipse = true;
            this.prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
            this.testDefaultPrefs = null;
            this.testPrefs = null;
        } else {
            this.eclipse = false;
            this.prefs = null;
            this.testDefaultPrefs = new HashMap<String, String>();
            this.testPrefs = new HashMap<String, String>();
            initializeTestDefaultPreferences();
        }
    }

    /**
     * @param pref the preference whose current value is being requested (cannot be <code>null</code>)
     * @return the value or <code>null</code> if there is no default value
     */
    public String get( final Preference pref ) {
        Utils.verifyIsNotNull(pref, "pref"); //$NON-NLS-1$

        if (this.eclipse) {
            final String value = this.prefs.get(pref.getId(), null);

            if (value == null) {
                return getDefault(pref);
            }

            return value;
        }

        // test mode
        String value = this.testPrefs.get(pref.getId());

        if (value == null) {
            value = this.testDefaultPrefs.get(pref.getId());
        }

        return value;
    }

    /**
     * @param pref the preference whose current value is being requested (cannot be <code>null</code>)
     * @return the value or <code>null</code> if there is no default value
     */
    public String getDefault( final Preference pref ) {
        if (this.eclipse) {
            return DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(pref.getId(), null);
        }

        // test mode
        return this.testDefaultPrefs.get(pref.getId());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        final IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        prefs.put(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR.getId(), Utils.EMPTY_STRING);
        prefs.put(CndPreference.CHILD_NODE_ATTRIBUTES_DELIMITER.getId(), Utils.SPACE_STRING);
        prefs.put(CndPreference.CHILD_NODE_PROPERTY_DELIMITER.getId(), Utils.SPACE_STRING);
        prefs.put(CndPreference.ELEMENT_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        prefs.put(CndPreference.ELEMENTS_END_DELIMITER.getId(), Utils.EMPTY_STRING);
        prefs.put(CndPreference.ELEMENTS_START_DELIMITER.getId(), "\t"); //$NON-NLS-1$
        prefs.put(CndPreference.NAMESPACE_MAPPING_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        prefs.put(CndPreference.NAMESPACE_MAPPING_SECTION_END_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        prefs.put(CndPreference.NODE_TYPE_DEFINITION_ATTRIBUTES_DELIMITER.getId(), Utils.SPACE_STRING);
        prefs.put(CndPreference.NODE_TYPE_DEFINITION_ATTRIBUTES_END_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        prefs.put(CndPreference.NODE_TYPE_DEFINITION_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        prefs.put(CndPreference.NODE_TYPE_DEFINITION_NAME_END_DELIMITER.getId(), Utils.SPACE_STRING);
        prefs.put(CndPreference.NODE_TYPE_DEFINITION_SECTION_END_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        prefs.put(CndPreference.NOTATION_TYPE.getId(), CndElement.NotationType.LONG.toString());
        prefs.put(CndPreference.PROPERTY_DEFINITION_ATTRIBUTES_DELIMITER.getId(), Utils.SPACE_STRING);
        prefs.put(CndPreference.QUOTE_CHAR.getId(), Utils.EMPTY_STRING);
        prefs.put(CndPreference.SUPER_TYPES_END_DELIMITER.getId(), Utils.SPACE_STRING);
    }

    /**
     * Must be kept in-sync with loading the Eclipse preference store.
     */
    private void initializeTestDefaultPreferences() {
        this.testDefaultPrefs.put(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR.getId(), Utils.EMPTY_STRING);
        this.testDefaultPrefs.put(CndPreference.CHILD_NODE_ATTRIBUTES_DELIMITER.getId(), Utils.SPACE_STRING);
        this.testDefaultPrefs.put(CndPreference.CHILD_NODE_PROPERTY_DELIMITER.getId(), Utils.SPACE_STRING);
        this.testDefaultPrefs.put(CndPreference.ELEMENT_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.ELEMENTS_END_DELIMITER.getId(), Utils.EMPTY_STRING);
        this.testDefaultPrefs.put(CndPreference.ELEMENTS_START_DELIMITER.getId(), "\t"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.NAMESPACE_MAPPING_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.NAMESPACE_MAPPING_SECTION_END_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.NODE_TYPE_DEFINITION_ATTRIBUTES_DELIMITER.getId(), Utils.SPACE_STRING);
        this.testDefaultPrefs.put(CndPreference.NODE_TYPE_DEFINITION_ATTRIBUTES_END_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.NODE_TYPE_DEFINITION_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.NODE_TYPE_DEFINITION_NAME_END_DELIMITER.getId(), Utils.SPACE_STRING);
        this.testDefaultPrefs.put(CndPreference.NODE_TYPE_DEFINITION_SECTION_END_DELIMITER.getId(), "\n"); //$NON-NLS-1$
        this.testDefaultPrefs.put(CndPreference.NOTATION_TYPE.getId(), CndElement.NotationType.LONG.toString());
        this.testDefaultPrefs.put(CndPreference.PROPERTY_DEFINITION_ATTRIBUTES_DELIMITER.getId(), Utils.SPACE_STRING);
        this.testDefaultPrefs.put(CndPreference.QUOTE_CHAR.getId(), Utils.EMPTY_STRING);
        this.testDefaultPrefs.put(CndPreference.SUPER_TYPES_END_DELIMITER.getId(), Utils.SPACE_STRING);
    }

    /**
     * @param pref the preference whose value is being set (cannot be <code>null</code>)
     * @param newValue the new value (can be <code>null</code> or empty)
     */
    public void set( final Preference pref,
                     final String newValue ) {
        Utils.verifyIsNotNull(pref, "pref"); //$NON-NLS-1$

        if (this.eclipse) {
            this.prefs.put(pref.getId(), newValue);

            // save
            try {
                this.prefs.flush();
            } catch (final BackingStoreException e) {
                if (Platform.isRunning()) {
                    Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
                } else {
                    System.err.print(e.getMessage());
                }
            }
        } else {
            // test mode
            this.testPrefs.put(pref.getId(), newValue);
        }
    }
}
