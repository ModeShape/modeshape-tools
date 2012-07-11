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
package org.jboss.tools.modeshape.rest.log;

import java.util.HashMap;
import java.util.Map;
import org.modeshape.common.logging.LogFactory;
import org.modeshape.common.logging.Logger;

public final class EclipseLoggerFactory extends LogFactory {

    /**
     * The shared instance of the factory.
     */
    static final EclipseLoggerFactory INSTANCE = new EclipseLoggerFactory();

    /**
     * Map of loggers keyed by logger name.
     */
    private Map<String, Logger> loggerMap;

    /**
     * Constructs the factory.
     */
    public EclipseLoggerFactory() {
        this.loggerMap = new HashMap<String, Logger>();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.LogFactory#getLogger(java.lang.String)
     */
    @Override
    public Logger getLogger( String name ) {
        Logger logger = null;

        // protect against concurrent access of the loggerMap
        synchronized (this) {
            logger = this.loggerMap.get(name);

            if (logger == null) {
                logger = new EclipseLogger(name);
                this.loggerMap.put(name, logger);
            }
        }

        return logger;
    }

}
