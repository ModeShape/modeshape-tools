/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

/**
 * An element that can be found in a CND file.
 */
public interface CndElement {

    /**
     * @param notationType the notation type (cannot be <code>null</code>)
     * @return the CND notation (<code>null</code> or empty if the element is incomplete)
     */
    String toCndNotation( NotationType notationType );

    /**
     * The type of CND notation to use.
     */
    enum NotationType {

        /**
         * The shortest form.
         */
        COMPACT,

        /**
         * An intermediate-sized form.
         */
        COMPRESSED,

        /**
         * The longest form.
         */
        LONG;

        /**
         * Index is {@value}.
         */
        public static final int COMPACT_INDEX = 2;

        /**
         * Index is {@value}.
         */
        public static final int COMPRESSED_INDEX = 1;

        /**
         * Index is {@value}.
         */
        public static final int LONG_INDEX = 0;
    }

}
