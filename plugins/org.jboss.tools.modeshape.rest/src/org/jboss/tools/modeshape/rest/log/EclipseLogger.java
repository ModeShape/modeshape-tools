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

import java.text.MessageFormat;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.modeshape.rest.IUiConstants;
import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * The <code>EclipseLogger</code> class provides an <code>org.slf4j.Logger</code> implementation that uses the Eclipse logger.
 */
public final class EclipseLogger implements Logger {

    private static boolean initialized = false;

    private static boolean DEBUG_MODE = Platform.isRunning();

    private static ILog LOGGER; // will be null when platform is not running

    private String name;

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
     * @see org.slf4j.Logger#debug(java.lang.String)
     */
    @Override
    public void debug( String message ) {
        debug(message, (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object)
     */
    @Override
    public void debug( String pattern,
                       Object arg ) {
        debug(pattern, new Object[] { arg });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug( String pattern,
                       Object[] arguments ) {
        if (isDebugEnabled()) {
            info(MessageFormat.format(pattern, arguments), arguments);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug( String message,
                       Throwable e ) {
        if (isDebugEnabled()) {
            info(message, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void debug( Marker marker,
                       String message ) {
        debug(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void debug( String pattern,
                       Object arg1,
                       Object arg2 ) {
        debug(pattern, new Object[] { arg1, arg2 });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void debug( Marker marker,
                       String pattern,
                       Object arg ) {
        debug(pattern, arg);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug( Marker marker,
                       String pattern,
                       Object[] arguments ) {
        debug(pattern, arguments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug( Marker marker,
                       String message,
                       Throwable e ) {
        debug(message, e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void debug( Marker marker,
                       String pattern,
                       Object arg1,
                       Object arg2 ) {
        debug(pattern, arg1, arg2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String)
     */
    @Override
    public void error( String message ) {
        error(message, (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object)
     */
    @Override
    public void error( String pattern,
                       Object arg ) {
        error(pattern, new Object[] { arg });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object[])
     */
    @Override
    public void error( String pattern,
                       Object[] arguments ) {
        error(MessageFormat.format(pattern, arguments), (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error( String message,
                       Throwable e ) {
        if (isErrorEnabled() && (LOGGER != null)) {
            LOGGER.log(new Status(IStatus.ERROR, IUiConstants.PLUGIN_ID, message, e));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void error( Marker marker,
                       String message ) {
        error(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void error( String pattern,
                       Object arg1,
                       Object arg2 ) {
        error(pattern, new Object[] { arg1, arg2 });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void error( Marker marker,
                       String pattern,
                       Object arg ) {
        error(pattern, arg);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void error( Marker marker,
                       String pattern,
                       Object[] arguments ) {
        error(pattern, arguments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error( Marker marker,
                       String message,
                       Throwable e ) {
        error(message, e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void error( Marker marker,
                       String pattern,
                       Object arg1,
                       Object arg2 ) {
        error(pattern, arg1, arg2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String)
     */
    @Override
    public void info( String message ) {
        info(message, (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object)
     */
    @Override
    public void info( String pattern,
                      Object arg ) {
        info(pattern, new Object[] { arg });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object[])
     */
    @Override
    public void info( String pattern,
                      Object[] arguments ) {
        info(MessageFormat.format(pattern, arguments), (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info( String message,
                      Throwable e ) {
        if (isInfoEnabled() && (LOGGER != null)) {
            LOGGER.log(new Status(IStatus.INFO, IUiConstants.PLUGIN_ID, message, e));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void info( Marker marker,
                      String message ) {
        info(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void info( String pattern,
                      Object arg1,
                      Object arg2 ) {
        info(pattern, new Object[] { arg1, arg2 });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void info( Marker marker,
                      String pattern,
                      Object arg ) {
        info(pattern, arg);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void info( Marker marker,
                      String pattern,
                      Object[] arguments ) {
        info(pattern, arguments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info( Marker marker,
                      String message,
                      Throwable e ) {
        info(message, e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void info( Marker marker,
                      String pattern,
                      Object arg1,
                      Object arg2 ) {
        info(pattern, arg1, arg2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return DEBUG_MODE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isDebugEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isDebugEnabled( Marker marker ) {
        return isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isErrorEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isErrorEnabled( Marker marker ) {
        return isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isInfoEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isInfoEnabled( Marker marker ) {
        return isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isTraceEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isTraceEnabled( Marker marker ) {
        return isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#isWarnEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isWarnEnabled( Marker marker ) {
        return isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String)
     */
    @Override
    public void trace( String message ) {
        trace(message, (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object)
     */
    @Override
    public void trace( String pattern,
                       Object arg ) {
        trace(pattern, new Object[] { arg });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace( String pattern,
                       Object[] arguments ) {
        trace(MessageFormat.format(pattern, arguments), (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void trace( String message,
                       Throwable e ) {
        if (isTraceEnabled()) {
            debug(message, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void trace( Marker marker,
                       String message ) {
        trace(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void trace( String pattern,
                       Object arg1,
                       Object arg2 ) {
        trace(pattern, new Object[] { arg1, arg2 });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void trace( Marker marker,
                       String pattern,
                       Object arg ) {
        trace(pattern, arg);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace( Marker marker,
                       String pattern,
                       Object[] arguments ) {
        trace(pattern, arguments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void trace( Marker marker,
                       String message,
                       Throwable e ) {
        trace(message, e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void trace( Marker marker,
                       String pattern,
                       Object arg1,
                       Object arg2 ) {
        trace(pattern, arg1, arg2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String)
     */
    @Override
    public void warn( String message ) {
        warn(message, (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object)
     */
    @Override
    public void warn( String pattern,
                      Object arg ) {
        warn(pattern, new Object[] { arg });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn( String pattern,
                      Object[] arguments ) {
        warn(MessageFormat.format(pattern, arguments), (Throwable)null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn( String message,
                      Throwable e ) {
        if (isWarnEnabled() && (LOGGER != null)) {
            LOGGER.log(new Status(IStatus.WARNING, IUiConstants.PLUGIN_ID, message, e));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void warn( Marker marker,
                      String message ) {
        warn(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void warn( String pattern,
                      Object arg1,
                      Object arg2 ) {
        warn(pattern, new Object[] { arg1, arg2 });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object)
     */
    @Override
    public void warn( Marker marker,
                      String pattern,
                      Object arg ) {
        warn(pattern, arg);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn( Marker marker,
                      String pattern,
                      Object[] arguments ) {
        warn(pattern, arguments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn( Marker marker,
                      String message,
                      Throwable e ) {
        warn(message, e);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String, java.lang.Object, java.lang.Object)
     */
    @Override
    public void warn( Marker marker,
                      String pattern,
                      Object arg1,
                      Object arg2 ) {
        warn(pattern, arg1, arg2);
    }

}
