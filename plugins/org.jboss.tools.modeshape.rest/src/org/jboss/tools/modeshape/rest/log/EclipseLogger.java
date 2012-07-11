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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.modeshape.rest.IUiConstants;
import org.modeshape.common.i18n.I18nResource;
import org.modeshape.common.logging.Logger;

/**
 * The <code>EclipseLogger</code> class provides an <code>org.modeshape.common.logging.Logger</code> implementation that uses the
 * Eclipse logger.
 */
public final class EclipseLogger extends Logger {

    private static boolean initialized = false;

    private static boolean DEBUG_MODE = Platform.isRunning();

    private static ILog LOGGER; // will be null when platform is not running

    private final String name;

    EclipseLogger( String name ) {
        this.name = name;

        if (!initialized) {
            initialized = true;

            if (Platform.isRunning()) {
                DEBUG_MODE = Platform.inDebugMode()
                             && Boolean.parseBoolean(Platform.getDebugOption(IUiConstants.PLUGIN_ID + "/debug")); //$NON-NLS-1$
                LOGGER = Platform.getLog(Platform.getBundle(IUiConstants.PLUGIN_ID));
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug( String message,
                       Object... params ) {
        debug(null, message, params);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#debug(java.lang.Throwable, java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug( Throwable t,
                       String message,
                       Object... params ) {
        if (isDebugEnabled()) {
            LOGGER.log(new Status(IStatus.INFO, IUiConstants.PLUGIN_ID, message, t));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#error(org.modeshape.common.i18n.I18nResource, java.lang.Object[])
     */
    @Override
    public void error( I18nResource message,
                       Object... params ) {
        error(null, message, params);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#error(java.lang.Throwable, org.modeshape.common.i18n.I18nResource,
     *      java.lang.Object[])
     */
    @Override
    public void error( Throwable t,
                       I18nResource message,
                       Object... params ) {
        if (isErrorEnabled() && LOGGER != null) {
            LOGGER.log(new Status(IStatus.ERROR, IUiConstants.PLUGIN_ID, message.text(params), t));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#info(org.modeshape.common.i18n.I18nResource, java.lang.Object[])
     */
    @Override
    public void info( I18nResource message,
                      Object... params ) {
        info(null, message, params);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#info(java.lang.Throwable, org.modeshape.common.i18n.I18nResource,
     *      java.lang.Object[])
     */
    @Override
    public void info( Throwable t,
                      I18nResource message,
                      Object... params ) {
        if (isInfoEnabled() && LOGGER != null) {
            LOGGER.log(new Status(IStatus.INFO, IUiConstants.PLUGIN_ID, message.text(params), t));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return DEBUG_MODE;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#trace(java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace( String message,
                       Object... params ) {
        trace(null, message, params);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#trace(java.lang.Throwable, java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace( Throwable t,
                       String message,
                       Object... params ) {
        if (isTraceEnabled()) {
            debug(message, t);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#warn(org.modeshape.common.i18n.I18nResource, java.lang.Object[])
     */
    @Override
    public void warn( I18nResource message,
                      Object... params ) {
        warn(null, message, params);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.modeshape.common.logging.Logger#warn(java.lang.Throwable, org.modeshape.common.i18n.I18nResource,
     *      java.lang.Object[])
     */
    @Override
    public void warn( Throwable t,
                      I18nResource message,
                      Object... params ) {
        if (isWarnEnabled() && LOGGER != null) {
            LOGGER.log(new Status(IStatus.WARNING, IUiConstants.PLUGIN_ID, message.text(params), t));
        }
    }
}
