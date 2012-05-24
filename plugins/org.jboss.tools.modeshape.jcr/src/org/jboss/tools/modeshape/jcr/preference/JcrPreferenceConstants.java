/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.preference;

/**
 * The ModeShape JCR preferences.
 */
public interface JcrPreferenceConstants {

    /**
     * An interface for all JCR preferences.
     */
    interface Preference {

        /**
         * @return the preference identifier (never <code>null</code> or empty)
         */
        String getId();
    }

    /**
     * CND notation preferences.
     */
    enum CndPreference implements Preference {
        
        /**
         * The format used when writing out the CND (i.e, long, compressed, or compact.
         */
        NOTATION_TYPE,
        
        /**
         * The quote character used when quotes are being used (single, double, or empty).
         */
        QUOTE_CHAR,
        
        /**
         * The string version of the quote character (empty string, single quote, or double quote) surrounding each item in
         * attribute lists.
         */
        ATTRIBUTE_LIST_ITEM_QUOTE_CHAR,

        /**
         * The delimiter between child node definition attributes.
         */
        CHILD_NODE_ATTRIBUTES_DELIMITER,

        /**
         * The delimiter between child node properties.
         */
        CHILD_NODE_PROPERTY_DELIMITER,

        /**
         * The delimiter between CND elements.
         */
        ELEMENT_DELIMITER,

        /**
         * The delimiter after the last CND element.
         */
        ELEMENTS_END_DELIMITER,

        /**
         * The delimiter before each node type definition's item definitions (properties, child nodes).
         */
        ELEMENTS_START_DELIMITER,

        /**
         * The delimiter between namespace mappings.
         */
        NAMESPACE_MAPPING_DELIMITER,

        /**
         * The delimiter after the last namespace mapping.
         */
        NAMESPACE_MAPPING_SECTION_END_DELIMITER,

        /**
         * The delimiter between node type definition attributes.
         */
        NODE_TYPE_DEFINITION_ATTRIBUTES_DELIMITER,

        /**
         * The delimiter after all the node type definition attributes.
         */
        NODE_TYPE_DEFINITION_ATTRIBUTES_END_DELIMITER,

        /**
         * The delimiter between node type definitions.
         */
        NODE_TYPE_DEFINITION_DELIMITER,

        /**
         * The delimiter after the node type definition name.
         */
        NODE_TYPE_DEFINITION_NAME_END_DELIMITER,

        /**
         * The delimiter after the last node type definitions.
         */
        NODE_TYPE_DEFINITION_SECTION_END_DELIMITER,

        /**
         * The delimiter between property definition attributes.
         */
        PROPERTY_DEFINITION_ATTRIBUTES_DELIMITER,

        /**
         * The delimiter after the last super type.
         */
        SUPER_TYPES_END_DELIMITER;

        /**
         * {@inheritDoc}
         *
         * @see org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants.Preference#getId()
         */
        @Override
        public String getId() {
            return (getClass().getPackage().getName() + '.' + toString().toLowerCase());
        }
    }
}
