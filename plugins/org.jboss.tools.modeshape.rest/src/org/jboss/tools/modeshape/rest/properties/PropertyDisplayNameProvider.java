/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.properties;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.modeshape.common.util.CheckArg;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * The <code>PropertyDisplayNameProvider</code> class provides localized names for properties and property categories.
 */
public final class PropertyDisplayNameProvider {

    /**
     * Constant appended to a property ID and used to obtain the property category.
     */
    private static final String CATEGORY_SUFFIX = ".CATEGORY"; //$NON-NLS-1$;

    /**
     * The name of the properties file.
     */
    private static final String RESOURCE_BUNDLE = PropertyDisplayNameProvider.class.getPackage().getName() + ".PropertyDisplayNames"; //$NON-NLS-1$

    /**
     * The properties file used for the display names and categories. Will be <code>null</code> if not loaded.
     */
    private static ResourceBundle bundle;

    /**
     * Indicates if the file has been loaded.
     */
    private static boolean loaded = false;

    /**
     * @param propertyId the property whose category is being requested (cannot be <code>null</code>)
     * @return the category or <code>null</code> if not categorized
     */
    public static String getCategory( String propertyId ) {
        return getDisplayName((propertyId + CATEGORY_SUFFIX), false);
    }

    /**
     * @param propertyId the property whose category is being requested (cannot be <code>null</code>)
     * @param idIfNotFound <code>true</code> if the ID should be returned if the property is not found
     * @return the display name or <code>null</code> if not found
     */
    private static String getDisplayName( String propertyId,
                                          boolean idIfNotFound ) {
        CheckArg.isNotNull(propertyId, "propertyId"); //$NON-NLS-1$

        if (!loaded) {
            load();
        }

        String displayName = null;

        if (bundle != null) {
            try {
                displayName = bundle.getString(propertyId);
            } catch (MissingResourceException e) {
                // nothing to do
            }
        }

        return ((idIfNotFound && (displayName == null)) ? propertyId : displayName);
    }

    /**
     * @param propertyId the property whose category is being requested (cannot be <code>null</code>)
     * @return the display name or, if the property is not found, the property ID (never <code>null</code>)
     */
    public static String getDisplayName( String propertyId ) {
        return getDisplayName(propertyId, true);
    }

    private static void load() {
        try {
            bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
        } catch (Exception e) {
            Activator.getDefault().log(new Status(Severity.ERROR, RestClientI18n.propertiesBundleLoadErrorMsg, e));
        }
    }

    /**
     * Don't allow construction.
     */
    private PropertyDisplayNameProvider() {
        // nothing to do
    }

}
