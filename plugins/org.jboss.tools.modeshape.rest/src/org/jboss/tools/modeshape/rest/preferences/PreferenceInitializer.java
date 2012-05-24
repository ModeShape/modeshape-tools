/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.preferences;

import static org.jboss.tools.modeshape.rest.IUiConstants.PLUGIN_ID;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferenceDefaultScopeNotFound;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferenceFileNotFound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.rest.Activator;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * 
 */
public final class PreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * The name of the preferences default values properties file located in this package.
     */
    private static final String PREFERENCES_FILE = "prefs.properties"; //$NON-NLS-1$

    /**
     * The default preference values.
     */
    private Properties preferenceDefaults;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences defaultValues = DefaultScope.INSTANCE.getNode(PLUGIN_ID);

        if (defaultValues == null) {
            // would only happen if PLUGIN_ID is wrong
            Activator.getDefault().log(new Status(Severity.ERROR, NLS.bind(preferenceDefaultScopeNotFound, PLUGIN_ID), null));
        } else {
            load();

            // set default values
            for (String property : this.preferenceDefaults.stringPropertyNames()) {
                defaultValues.put(property, this.preferenceDefaults.getProperty(property));
            }
        }
    }

    private void load() {
        this.preferenceDefaults = new Properties();
        InputStream input = null;

        try {
            input = getClass().getResource(PREFERENCES_FILE).openStream();

            if (input == null) {
                Activator.getDefault().log(new Status(Severity.ERROR, NLS.bind(preferenceFileNotFound, PREFERENCES_FILE), null));
            } else {
                this.preferenceDefaults.load(input);
            }
        } catch (IOException e) {
            Activator.getDefault().log(new Status(Severity.ERROR, NLS.bind(preferenceFileNotFound, PREFERENCES_FILE), null));
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                Activator.getDefault().log(new Status(Severity.ERROR, e.getMessage(), e));
            }
        }
    }

}
