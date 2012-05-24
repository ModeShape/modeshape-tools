/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class Activator extends Plugin {

    /**
     * The plug-in bundle's symbolic name.
     */
    public static final String PLUGIN_ID = Utils.class.getPackage().getName();

    private static Activator _instance;
    
    /**
     * @return the shared instance
     */
    public static Activator get() {
        return _instance;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        _instance = this;
    }
}
