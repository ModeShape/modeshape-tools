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
package org.jboss.tools.modeshape.rest.preferences;

import org.eclipse.core.resources.IResource;
import org.modeshape.common.util.CheckArg;

/**
 * The <code>PublishingFileFilter</code> is a resource name filter.
 */
public final class PublishingFileFilter {

    /**
     * @param text the text being tested against the pattern (may not be <code>null</code> or empty)
     * @param pattern the pattern used (may not be <code>null</code> or empty)
     * @return <code>true</code> if the text is matched by the pattern
     */
    public static boolean matches( String text,
                                   String pattern ) {
        final String regex = pattern.replace("?", ".?").replace("*", ".*?"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return text.matches(regex);
    }

    /**
     * The data model (never <code>null</code>).
     */
    private final IgnoredResourcesModel model;

    /**
     * @param model the data model (may not be <code>null</code>)
     */
    public PublishingFileFilter( IgnoredResourcesModel model ) {
        CheckArg.isNotNull(model, "model"); //$NON-NLS-1$
        this.model = model;
    }

    /**
     * @param resource the resource being tested (may not be <code>null</code>)
     * @return <code>true</code> if the resource should be included (i.e., it is not filtered out)
     */
    public boolean accept( IResource resource ) {
        CheckArg.isNotNull(resource, "resource"); //$NON-NLS-1$
        final String name = resource.getName();

        for (ResourcePattern pattern : model.getPatterns()) {
            if (pattern.isEnabled() && matches(name, pattern.getPattern())) {
                return false;
            }
        }
        
        if (resource.getParent() == null) {
            return true;
        }

        // check parents
        return accept(resource.getParent());
    }

}
