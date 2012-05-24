/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Binary;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.Utils;

/**
 * 
 */
public class PropertyValue implements Value {

    private final int type;
    private String value;

    /**
     * @param jcrType the {@link PropertyType} used to create the value
     */
    public PropertyValue( final int jcrType ) {
        this.type = jcrType;
    }

    /**
     * @param jcrType the {@link PropertyType} used to create the value
     * @param initialValue the initial property value (can be <code>null</code> or empty)
     */
    public PropertyValue( final int jcrType,
                          final String initialValue ) {
        this(jcrType);
        this.value = initialValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        final PropertyValue that = (PropertyValue)obj;
        return (Utils.equals(this.type, that.type) && Utils.equals(this.value, that.value));
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getBinary()
     */
    @Override
    public Binary getBinary() {
        final byte[] bytes = this.value.getBytes();
        return new Binary() {
            /**
             * {@inheritDoc}
             * 
             * @see javax.jcr.Binary#dispose()
             */
            @Override
            public void dispose() {
                // do nothing
            }

            /**
             * {@inheritDoc}
             * 
             * @see javax.jcr.Binary#getSize()
             */
            @Override
            public long getSize() {
                return bytes.length;
            }

            /**
             * {@inheritDoc}
             * 
             * @see javax.jcr.Binary#getStream()
             */
            @Override
            public InputStream getStream() {
                return new ByteArrayInputStream(bytes);
            }

            /**
             * {@inheritDoc}
             * 
             * @see javax.jcr.Binary#read(byte[], long)
             */
            @Override
            public int read( final byte[] b,
                             final long position ) throws IOException {
                if (getSize() <= position) {
                    return -1;
                }

                InputStream stream = null;
                IOException error = null;

                try {
                    stream = getStream();
                    // Read/skip the next 'position' bytes ...
                    long skip = position;

                    while (skip > 0) {
                        final long skipped = stream.skip(skip);

                        if (skipped <= 0) {
                            return -1;
                        }

                        skip -= skipped;
                    }

                    return stream.read(b);
                } catch (final IOException e) {
                    error = e;
                    throw e;
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (final RuntimeException t) {
                            // Only throw if we've not already thrown an exception ...
                            if (error == null) {
                                throw t;
                            }
                        } catch (final IOException t) {
                            // Only throw if we've not already thrown an exception ...
                            if (error == null) {
                                throw t;
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getBoolean()
     */
    @Override
    public boolean getBoolean() throws ValueFormatException {
        if (!Utils.isEmpty(this.value)) {
            if (this.value.equals(Boolean.TRUE.toString())) {
                return true;
            }

            if (this.value.equals(Boolean.FALSE.toString())) {
                return false;
            }
        }

        throw new ValueFormatException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getDate()
     */
    @Override
    public Calendar getDate() throws ValueFormatException {
        try {
            // TODO how do you determine the format here
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); //$NON-NLS-1$
            final Calendar cal = Calendar.getInstance();
            final Date d1 = df.parse(this.value);
            cal.setTime(d1);
            return cal;
        } catch (final Exception e) {
            final String from = PropertyType.nameFromValue(getType());
            final String to = PropertyType.nameFromValue(PropertyType.LONG);
            throw new ValueFormatException(NLS.bind(Messages.unableToConvertValue, new Object[] {this.value, from, to}), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getDecimal()
     */
    @Override
    public BigDecimal getDecimal() throws ValueFormatException {
        try {
            return new BigDecimal(this.value);
        } catch (final NumberFormatException t) {
            final String from = PropertyType.nameFromValue(getType());
            final String to = PropertyType.nameFromValue(PropertyType.DECIMAL);
            throw new ValueFormatException(NLS.bind(Messages.unableToConvertValue, new Object[] {this.value, from, to}), t);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getDouble()
     */
    @Override
    public double getDouble() throws ValueFormatException {
        try {
            return Double.parseDouble(this.value);
        } catch (final NumberFormatException t) {
            final String from = PropertyType.nameFromValue(getType());
            final String to = PropertyType.nameFromValue(PropertyType.DOUBLE);
            throw new ValueFormatException(NLS.bind(Messages.unableToConvertValue, new Object[] {this.value, from, to}), t);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getLong()
     */
    @Override
    public long getLong() throws ValueFormatException {
        try {
            return Long.parseLong(this.value);
        } catch (final NumberFormatException t) {
            final String from = PropertyType.nameFromValue(getType());
            final String to = PropertyType.nameFromValue(PropertyType.LONG);
            throw new ValueFormatException(NLS.bind(Messages.unableToConvertValue, new Object[] {this.value, from, to}), t);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getStream()
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public InputStream getStream() throws RepositoryException {
        return getBinary().getStream();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getString()
     */
    @Override
    public String getString() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Value#getType()
     */
    @Override
    public int getType() {
        return ((this.type == PropertyType.UNDEFINED) ? PropertyType.STRING : this.type);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Utils.hashCode(this.type, this.value);
    }
}
