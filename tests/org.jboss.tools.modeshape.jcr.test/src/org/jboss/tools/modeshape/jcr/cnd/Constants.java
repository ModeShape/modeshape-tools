/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import java.util.Arrays;
import java.util.Collection;

import javax.jcr.PropertyType;

import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.Abstract;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value;
import org.jboss.tools.modeshape.jcr.attributes.Autocreated;
import org.jboss.tools.modeshape.jcr.attributes.DefaultType;
import org.jboss.tools.modeshape.jcr.attributes.DefaultValues;
import org.jboss.tools.modeshape.jcr.attributes.ListAttributeState;
import org.jboss.tools.modeshape.jcr.attributes.Mandatory;
import org.jboss.tools.modeshape.jcr.attributes.Mixin;
import org.jboss.tools.modeshape.jcr.attributes.Multiple;
import org.jboss.tools.modeshape.jcr.attributes.NoFullText;
import org.jboss.tools.modeshape.jcr.attributes.NoQueryOrder;
import org.jboss.tools.modeshape.jcr.attributes.Orderable;
import org.jboss.tools.modeshape.jcr.attributes.PrimaryItem;
import org.jboss.tools.modeshape.jcr.attributes.PropertyValue;
import org.jboss.tools.modeshape.jcr.attributes.Protected;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.jboss.tools.modeshape.jcr.attributes.RequiredTypes;
import org.jboss.tools.modeshape.jcr.attributes.SameNameSiblings;
import org.jboss.tools.modeshape.jcr.attributes.SuperTypes;
import org.jboss.tools.modeshape.jcr.attributes.ValueConstraints;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants.CndPreference;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;

/**
 * 
 */
public interface Constants {

    JcrPreferenceStore prefStore = JcrPreferenceStore.get();

    interface Preferences {
        //        String ATTRIBUTE_LIST_ELEMENT_DELIMITER = ", "; //$NON-NLS-1$
        // String ATTRIBUTE_LIST_ITEM_QUOTE_CHAR = Utils.EMPTY_STRING;
        String CHILD_NODE_DEFINITION_END_PREFIX_DELIMITER = " "; //$NON-NLS-1$
        String CHILD_NODE_PROPERTY_DELIMITER = Utils.SPACE_STRING;
        String DEFAULT_VALUES_END_PREFIX_DELIMITER = Utils.SPACE_STRING;
        String DEFAULT_VALUES_QUOTE_CHARACTER = "'"; //$NON-NLS-1$
        String DEFAULT_TYPE_END_PREFIX_DELIMITER = Utils.SPACE_STRING;
        String ATTRIBUTE_LIST_PREFIX_END_DELIMITER = Utils.SPACE_STRING;
        // String ATTRIBUTE_LIST_QUOTE_CHAR = Utils.EMPTY_STRING;
        String ELEMENT_DELIMITER = "\n"; //$NON-NLS-1$
        String ELEMENTS_END_DELIMITER = Utils.EMPTY_STRING;
        String ELEMENTS_START_DELIMITER = "\t"; //$NON-NLS-1$
        String NAMESPACE_MAPPING_DELIMITER = "\n"; //$NON-NLS-1$
        String NAMESPACE_MAPPING_SECTION_END_DELIMITER = "\n"; //$NON-NLS-1$
        String CHILD_NODE_ATTRIBUTES_DELIMITER = Utils.SPACE_STRING;
        String NODE_TYPE_DEFINITION_ATTRIBUTES_DELIMITER = Utils.SPACE_STRING;
        String NODE_TYPE_DEFINITION_ATTRIBUTES_END_DELIMITER = "\n"; //$NON-NLS-1$
        String NODE_TYPE_DEFINITION_DELIMITER = "\n"; //$NON-NLS-1$
        String NODE_TYPE_DEFINITION_NAME_END_DELIMITER = Utils.SPACE_STRING;
        String NODE_TYPE_DEFINITION_SECTION_END_DELIMITER = "\n"; //$NON-NLS-1$
        String PROPERTY_DEFINITION_ATTRIBUTES_DELIMITER = Utils.SPACE_STRING;
        String PROPERTY_DEFINITION_END_PREFIX_DELIMITER = Utils.SPACE_STRING;
        String REQUIRED_TYPES_END_PREFIX_DELIMITER = Utils.EMPTY_STRING;
        String SUPER_TYPES_END_DELIMITER = Utils.SPACE_STRING;
        //        String VALUE_CONSTRAINTS_ITEM_QUOTE_CHARACTER = "'"; //$NON-NLS-1$
    }

    String NAMESPACE_PREFIX1 = "NAMESPACE_PREFIX1"; //$NON-NLS-1$
    String NAMESPACE_PREFIX2 = "NAMESPACE_PREFIX2"; //$NON-NLS-1$
    String NAMESPACE_PREFIX3 = "NAMESPACE_PREFIX3"; //$NON-NLS-1$
    String[] DEFAULT_NAMESPACE_PREFIXES = new String[] { NAMESPACE_PREFIX1, NAMESPACE_PREFIX2, NAMESPACE_PREFIX3 };

    String NAMESPACE_URI1 = "NAMESPACE_URI1"; //$NON-NLS-1$
    String NAMESPACE_URI2 = "NAMESPACE_URI2"; //$NON-NLS-1$
    String NAMESPACE_URI3 = "NAMESPACE_URI3"; //$NON-NLS-1$
    String[] DEFAULT_NAMESPACE_URIS = new String[] { NAMESPACE_URI1, NAMESPACE_URI2, NAMESPACE_URI3 };

    NamespaceMapping NAMESPACE1 = new NamespaceMapping(NAMESPACE_PREFIX1, NAMESPACE_URI1);
    NamespaceMapping NAMESPACE2 = new NamespaceMapping(NAMESPACE_PREFIX2, NAMESPACE_URI2);
    NamespaceMapping NAMESPACE3 = new NamespaceMapping(NAMESPACE_PREFIX3, NAMESPACE_URI3);
    NamespaceMapping[] DEFAULT_NAMESPACE_MAPPINGS = new NamespaceMapping[] { NAMESPACE1, NAMESPACE2, NAMESPACE3 };

    String QUALIFIER1 = NAMESPACE_PREFIX1;
    String QUALIFIER2 = NAMESPACE_PREFIX2;
    String QUALIFIER3 = NAMESPACE_PREFIX3;
    String[] DEFAULT_QUALIFIERS = DEFAULT_NAMESPACE_PREFIXES;

    String UNQUALIFIED_NAME1 = "UNQUALIFIED_NAME1"; //$NON-NLS-1$
    String UNQUALIFIED_NAME2 = "UNQUALIFIED_NAME2"; //$NON-NLS-1$
    String UNQUALIFIED_NAME3 = "UNQUALIFIED_NAME3"; //$NON-NLS-1$
    String[] DEFAULT_UNQUALIFIED_NAMES = new String[] { UNQUALIFIED_NAME1, UNQUALIFIED_NAME2, UNQUALIFIED_NAME3 };

    QualifiedName QUALIFIED_NAME1 = new QualifiedName(QUALIFIER1, UNQUALIFIED_NAME1);
    QualifiedName QUALIFIED_NAME2 = new QualifiedName(QUALIFIER2, UNQUALIFIED_NAME2);
    QualifiedName QUALIFIED_NAME3 = new QualifiedName(QUALIFIER3, UNQUALIFIED_NAME3);
    QualifiedName[] DEFAULT_QUALIFIED_NAMES = new QualifiedName[] { QUALIFIED_NAME1, QUALIFIED_NAME2, QUALIFIED_NAME3 };
    QualifiedName NAME_WITH_EMPTY_QUALIFIER = new QualifiedName(null, UNQUALIFIED_NAME1);
    QualifiedName NAME_WITH_NON_DEFAULT_QUALIFIER = new QualifiedName(QUALIFIER1 + "changed", UNQUALIFIED_NAME1); //$NON-NLS-1$

    String VARIANT = AttributeState.VARIANT_STRING;

    String ABSTRACT_VARIANT_COMPACT_FORM = Abstract.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String ABSTRACT_VARIANT_COMPRESSED_FORM = Abstract.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String ABSTRACT_VARIANT_LONG_FORM = Abstract.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String AUTOCREATED_VARIANT_COMPACT_FORM = Autocreated.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String AUTOCREATED_VARIANT_COMPRESSED_FORM = Autocreated.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String AUTOCREATED_VARIANT_LONG_FORM = Autocreated.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String DEFAULT_TYPE_VARIANT_FORM = DefaultType.NOTATION + Preferences.DEFAULT_TYPE_END_PREFIX_DELIMITER + VARIANT;
    String DEFAULT_TYPE_VARIANT_SHORT_FORM = DefaultType.NOTATION + VARIANT;
    String DEFAULT_TYPE = "jcr:data"; //$NON-NLS-1$
    String DEFAULT_TYPE_TYPE_FORM = DefaultType.NOTATION + Preferences.DEFAULT_TYPE_END_PREFIX_DELIMITER + DEFAULT_TYPE;
    String DEFAULT_TYPE_TYPE_SHORT_FORM = DefaultType.NOTATION + DEFAULT_TYPE;

    String MANDATORY_VARIANT_COMPACT_FORM = Mandatory.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String MANDATORY_VARIANT_COMPRESSED_FORM = Mandatory.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String MANDATORY_VARIANT_LONG_FORM = Mandatory.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String MIXIN_VARIANT_COMPACT_FORM = Mixin.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String MIXIN_VARIANT_COMPRESSED_FORM = Mixin.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String MIXIN_VARIANT_LONG_FORM = Mixin.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String MULTIPLE_VARIANT_COMPACT_FORM = Multiple.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String MULTIPLE_VARIANT_COMPRESSED_FORM = Multiple.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String MULTIPLE_VARIANT_LONG_FORM = Multiple.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String NO_FULL_TEXT_VARIANT_COMPACT_FORM = NoFullText.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String NO_FULL_TEXT_VARIANT_COMPRESSED_FORM = NoFullText.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String NO_FULL_TEXT_VARIANT_LONG_FORM = NoFullText.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String NO_QUERY_ORDER_VARIANT_COMPACT_FORM = NoQueryOrder.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String NO_QUERY_ORDER_VARIANT_COMPRESSED_FORM = NoQueryOrder.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String NO_QUERY_ORDER_VARIANT_LONG_FORM = NoQueryOrder.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String ORDERABLE_VARIANT_COMPACT_FORM = Orderable.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String ORDERABLE_VARIANT_COMPRESSED_FORM = Orderable.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String ORDERABLE_VARIANT_LONG_FORM = Orderable.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String PRIMARY_ITEM_VARIANT_COMPACT_FORM = PrimaryItem.NOTATION[NotationType.COMPACT_INDEX] + ' ' + VARIANT;
    String PRIMARY_ITEM_VARIANT_COMPRESSED_FORM = PrimaryItem.NOTATION[NotationType.COMPRESSED_INDEX] + ' ' + VARIANT;
    String PRIMARY_ITEM_VARIANT_LONG_FORM = PrimaryItem.NOTATION[NotationType.LONG_INDEX] + ' ' + VARIANT;
    String PRIMARY_ITEM = "jcr:data"; //$NON-NLS-1$
    String PRIMARY_ITEM_ITEM_COMPACT_FORM = PrimaryItem.NOTATION[NotationType.COMPACT_INDEX] + ' ' + PRIMARY_ITEM;
    String PRIMARY_ITEM_ITEM_COMPRESSED_FORM = PrimaryItem.NOTATION[NotationType.COMPRESSED_INDEX] + ' ' + PRIMARY_ITEM;
    String PRIMARY_ITEM_ITEM_LONG_FORM = PrimaryItem.NOTATION[NotationType.LONG_INDEX] + ' ' + PRIMARY_ITEM;

    String PROTECTED_VARIANT_COMPACT_FORM = Protected.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String PROTECTED_VARIANT_COMPRESSED_FORM = Protected.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String PROTECTED_VARIANT_LONG_FORM = Protected.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String QUERY_OPS_COMPACT_FORM = QueryOperators.NOTATION[NotationType.COMPACT_INDEX] + Utils.SPACE_STRING;
    String QUERY_OPS_COMPRESSED_FORM = QueryOperators.NOTATION[NotationType.COMPRESSED_INDEX] + Utils.SPACE_STRING;
    String QUERY_OPS_LONG_FORM = QueryOperators.NOTATION[NotationType.LONG_INDEX] + Utils.SPACE_STRING;
    String QUERY_OPS_VARIANT_COMPACT_FORM = QUERY_OPS_COMPACT_FORM + VARIANT;
    String QUERY_OPS_VARIANT_COMPRESSED_FORM = QUERY_OPS_COMPRESSED_FORM + VARIANT;
    String QUERY_OPS_VARIANT_LONG_FORM = QUERY_OPS_LONG_FORM + VARIANT;

    QueryOperator OPERATOR_ONE = QueryOperator.EQUALS;
    QueryOperator OPERATOR_TWO = QueryOperator.GREATER_THAN;
    QueryOperator OPERATOR_THREE = QueryOperator.LESS_THAN;
    QueryOperator[] DEFAULT_OPERATORS = new QueryOperator[] { OPERATOR_ONE, OPERATOR_TWO, OPERATOR_THREE };

    String QUERY_OPS_ONE_OPERATOR_COMPACT_FORM = QUERY_OPS_COMPACT_FORM + Utils.SINGLE_QUOTE
            + OPERATOR_ONE.toCndNotation(NotationType.COMPACT) + Utils.SINGLE_QUOTE;
    String QUERY_OPS_ONE_OPERATOR_COMPRESSED_FORM = QUERY_OPS_COMPRESSED_FORM + Utils.SINGLE_QUOTE
            + OPERATOR_ONE.toCndNotation(NotationType.COMPRESSED) + Utils.SINGLE_QUOTE;
    String QUERY_OPS_ONE_OPERATOR_LONG_FORM = QUERY_OPS_LONG_FORM + Utils.SINGLE_QUOTE
            + OPERATOR_ONE.toCndNotation(NotationType.LONG) + Utils.SINGLE_QUOTE;
    String QUERY_OPS_THREE_OPERATOR_COMPACT_FORM = QUERY_OPS_COMPACT_FORM + Utils.SINGLE_QUOTE
            + OPERATOR_ONE.toCndNotation(NotationType.COMPACT) + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX]
            + OPERATOR_TWO.toCndNotation(NotationType.COMPACT) + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX]
            + OPERATOR_THREE.toCndNotation(NotationType.COMPACT) + Utils.SINGLE_QUOTE;
    String QUERY_OPS_THREE_OPERATOR_COMPRESSED_FORM = QUERY_OPS_COMPRESSED_FORM + Utils.SINGLE_QUOTE
            + OPERATOR_ONE.toCndNotation(NotationType.COMPRESSED)
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPRESSED_INDEX]
            + OPERATOR_TWO.toCndNotation(NotationType.COMPRESSED)
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPRESSED_INDEX]
            + OPERATOR_THREE.toCndNotation(NotationType.COMPRESSED) + Utils.SINGLE_QUOTE;
    String QUERY_OPS_THREE_OPERATOR_LONG_FORM = QUERY_OPS_LONG_FORM + Utils.SINGLE_QUOTE
            + OPERATOR_ONE.toCndNotation(NotationType.LONG) + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX]
            + OPERATOR_TWO.toCndNotation(NotationType.LONG) + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX]
            + OPERATOR_THREE.toCndNotation(NotationType.LONG) + Utils.SINGLE_QUOTE;

    String SAME_NAME_SIBLINGS_VARIANT_COMPACT_FORM = SameNameSiblings.NOTATION[NotationType.COMPACT_INDEX] + VARIANT;
    String SAME_NAME_SIBLINGS_VARIANT_COMPRESSED_FORM = SameNameSiblings.NOTATION[NotationType.COMPRESSED_INDEX] + VARIANT;
    String SAME_NAME_SIBLINGS_VARIANT_LONG_FORM = SameNameSiblings.NOTATION[NotationType.LONG_INDEX] + VARIANT;

    String ITEM_ONE = "item1"; //$NON-NLS-1$ 
    String ITEM_TWO = "item2"; //$NON-NLS-1$ 
    String ITEM_THREE = "item3"; //$NON-NLS-1$
    String ONE_ITEM_SINGLE_QUOTED_FORM = prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR) + ITEM_ONE
            + prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR);
    String THREE_ITEM_SINGLE_QUOTED_FORM = prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR) + ITEM_ONE
            + prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR)
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX]
            + prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR) + ITEM_TWO
            + prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR)
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX]
            + prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR) + ITEM_THREE
            + prefStore.get(CndPreference.ATTRIBUTE_LIST_ITEM_QUOTE_CHAR);

    String DEFAULT_VALUES_VARIANT = DefaultValues.NOTATION_PREFIX + Preferences.DEFAULT_VALUES_END_PREFIX_DELIMITER + VARIANT;
    String DEFAULT_VALUES_SHORT_VARIANT = DefaultValues.NOTATION_PREFIX + VARIANT;
    String DEFAULT_VALUES_ONE_ITEM_FORM = DefaultValues.NOTATION_PREFIX + Preferences.DEFAULT_VALUES_END_PREFIX_DELIMITER
            + Utils.SINGLE_QUOTE + ITEM_ONE + Utils.SINGLE_QUOTE;
    String DEFAULT_VALUES_ONE_ITEM_SHORT_FORM = DefaultValues.NOTATION_PREFIX + Utils.SINGLE_QUOTE + ITEM_ONE + Utils.SINGLE_QUOTE;
    String DEFAULT_VALUES_THREE_ITEM_FORM = DefaultValues.NOTATION_PREFIX + Preferences.DEFAULT_VALUES_END_PREFIX_DELIMITER
            + Utils.SINGLE_QUOTE + ITEM_ONE + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX]
            + Utils.SINGLE_QUOTE + ITEM_TWO + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX]
            + Utils.SINGLE_QUOTE + ITEM_THREE + Utils.SINGLE_QUOTE;
    String DEFAULT_VALUES_THREE_ITEM_SHORT_FORM = DefaultValues.NOTATION_PREFIX + Utils.SINGLE_QUOTE + ITEM_ONE
            + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX] + Utils.SINGLE_QUOTE
            + ITEM_TWO + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX]
            + Utils.SINGLE_QUOTE + ITEM_THREE + Utils.SINGLE_QUOTE;

    String REQUIRED_TYPES_SHORT_VARIANT = RequiredTypes.NOTATION_PREFIX + VARIANT + RequiredTypes.NOTATION_SUFFIX;
    String REQUIRED_TYPES_VARIANT = RequiredTypes.NOTATION_PREFIX + VARIANT + RequiredTypes.NOTATION_SUFFIX;
    String REQUIRED_TYPES_ONE_ITEM_FORM = RequiredTypes.NOTATION_PREFIX + QUALIFIED_NAME1 + RequiredTypes.NOTATION_SUFFIX;
    String REQUIRED_TYPES_ONE_ITEM_SHORT_FORM = RequiredTypes.NOTATION_PREFIX + QUALIFIED_NAME1 + RequiredTypes.NOTATION_SUFFIX;
    String REQUIRED_TYPES_THREE_ITEM_SHORT_FORM = RequiredTypes.NOTATION_PREFIX + QUALIFIED_NAME1
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX] + QUALIFIED_NAME2
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX] + QUALIFIED_NAME3 + RequiredTypes.NOTATION_SUFFIX;
    String REQUIRED_TYPES_THREE_ITEM_FORM = RequiredTypes.NOTATION_PREFIX + QUALIFIED_NAME1
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX] + QUALIFIED_NAME2
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX] + QUALIFIED_NAME3 + RequiredTypes.NOTATION_SUFFIX;

    String SUPER_TYPES_VARIANT = SuperTypes.NOTATION_PREFIX + Preferences.ATTRIBUTE_LIST_PREFIX_END_DELIMITER + VARIANT;
    String SUPER_TYPES_SHORT_VARIANT = SuperTypes.NOTATION_PREFIX + VARIANT;
    String SUPER_TYPES_ONE_ITEM_FORM = SuperTypes.NOTATION_PREFIX + Preferences.ATTRIBUTE_LIST_PREFIX_END_DELIMITER
            + QUALIFIED_NAME1;
    String SUPER_TYPES_ONE_ITEM_SHORT_FORM = SuperTypes.NOTATION_PREFIX + QUALIFIED_NAME1;
    String SUPER_TYPES_THREE_ITEM_FORM = SuperTypes.NOTATION_PREFIX + Preferences.ATTRIBUTE_LIST_PREFIX_END_DELIMITER
            + QUALIFIED_NAME1 + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX] + QUALIFIED_NAME2
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX] + QUALIFIED_NAME3;
    String SUPER_TYPES_THREE_ITEM_SHORT_FORM = SuperTypes.NOTATION_PREFIX + QUALIFIED_NAME1
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX] + QUALIFIED_NAME2
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX] + QUALIFIED_NAME3;

    String VALUE_CONSTRAINT1 = "(19|20)\\d{2}"; //$NON-NLS-1$
    String VALUE_CONSTRAINT2 = "[$]\\d{1,3}[,]?\\d{3}([.]\\d{2})?"; //$NON-NLS-1$
    String VALUE_CONSTRAINT3 = "[1,5]"; //$NON-NLS-1$
    String[] DEFAULT_VALUE_CONSTRAINTS = new String[] { VALUE_CONSTRAINT1, VALUE_CONSTRAINT2, VALUE_CONSTRAINT3 };

    String VALUE_CONSTRAINTS_VARIANT = ValueConstraints.NOTATION_PREFIX + Preferences.ATTRIBUTE_LIST_PREFIX_END_DELIMITER + VARIANT;
    String VALUE_CONSTRAINTS_SHORT_VARIANT = ValueConstraints.NOTATION_PREFIX + VARIANT;
    String VALUE_CONSTRAINTS_ONE_ITEM_FORM = ValueConstraints.NOTATION_PREFIX + Preferences.ATTRIBUTE_LIST_PREFIX_END_DELIMITER
            + Utils.SINGLE_QUOTE + VALUE_CONSTRAINT1 + Utils.SINGLE_QUOTE;
    String VALUE_CONSTRAINTS_ONE_ITEM_SHORT_FORM = ValueConstraints.NOTATION_PREFIX + Utils.SINGLE_QUOTE + VALUE_CONSTRAINT1
            + Utils.SINGLE_QUOTE;
    String VALUE_CONSTRAINTS_THREE_ITEM_FORM = ValueConstraints.NOTATION_PREFIX + Preferences.ATTRIBUTE_LIST_PREFIX_END_DELIMITER
            + Utils.SINGLE_QUOTE + VALUE_CONSTRAINT1 + Utils.SINGLE_QUOTE
            + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX] + Utils.SINGLE_QUOTE + VALUE_CONSTRAINT2
            + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.LONG_INDEX] + Utils.SINGLE_QUOTE
            + VALUE_CONSTRAINT3 + Utils.SINGLE_QUOTE;
    String VALUE_CONSTRAINTS_THREE_ITEM_SHORT_FORM = ValueConstraints.NOTATION_PREFIX + Utils.SINGLE_QUOTE + VALUE_CONSTRAINT1
            + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX] + Utils.SINGLE_QUOTE
            + VALUE_CONSTRAINT2 + Utils.SINGLE_QUOTE + ListAttributeState.ITEM_DELIM_NOTATION[NotationType.COMPACT_INDEX]
            + Utils.SINGLE_QUOTE + VALUE_CONSTRAINT3 + Utils.SINGLE_QUOTE;

    class Helper {
        public static void changeValue( AttributeState attribute ) {
            if (attribute.is()) {
                attribute.set(Value.IS_NOT);
            } else if (attribute.isNot()) {
                attribute.set(Value.IS);
            } else {
                attribute.set(Value.IS);
            }
        }

        public static Collection<NamespaceMapping> getDefaultNamespaces() {
            return Arrays.asList(DEFAULT_NAMESPACE_MAPPINGS);
        }

        public static Collection<QualifiedName> getDefaultQualifiedNames() {
            return Arrays.asList(DEFAULT_QUALIFIED_NAMES);
        }

        public static String[] getDefaultQualifiedNamesAsStringArray() {
            String[] names = new String[DEFAULT_QUALIFIED_NAMES.length];
            int i = 0;

            for (QualifiedName qname : DEFAULT_QUALIFIED_NAMES) {
                names[i++] = qname.get();
            }

            return names;
        }

        public static Collection<String> getDefaultNamespacePrefixes() {
            return Arrays.asList(DEFAULT_NAMESPACE_PREFIXES);
        }

        public static Collection<String> getDefaultQualifiers() {
            return Arrays.asList(DEFAULT_QUALIFIERS);
        }

        public static String[] getDefaultQueryOperators() {
            String[] result = new String[DEFAULT_OPERATORS.length];
            int i = 0;

            for (QueryOperator operator : DEFAULT_OPERATORS) {
                result[i++] = operator.toString();
            }

            return result;
        }

        public static javax.jcr.Value[] getDefaultStringValues() {
            return new PropertyValue[] { new PropertyValue(PropertyType.STRING, ITEM_ONE),
                    new PropertyValue(PropertyType.STRING, ITEM_TWO), new PropertyValue(PropertyType.STRING, ITEM_THREE) };
        }
    }

    /**
     * The built-in namespace mappings.
     */
    public interface BuiltInNamespaces {

        /**
         * Reserved for items defined within built-in node types.
         */
        NamespaceMapping JCR = new NamespaceMapping("jcr", "http://www.jcp.org/jcr/1.0"); //$NON-NLS-1$ //$NON-NLS-2$

        /**
         * Reserved for the names of built-in mixin node types.
         */
        NamespaceMapping MIX = new NamespaceMapping("mix", "http://www.jcp.org/jcr/mix/1.0"); //$NON-NLS-1$ //$NON-NLS-2$

        /**
         * Reserved for the names of built-in primary node types.
         */
        NamespaceMapping NT = new NamespaceMapping("nt", "http://www.jcp.org/jcr/nt/1.0"); //$NON-NLS-1$ //$NON-NLS-2$

        /**
         * Reserved for reasons of compatibility with XML.
         */
        NamespaceMapping XML = new NamespaceMapping("xml", "http://www.w3.org/XML/1998/namespace"); //$NON-NLS-1$ //$NON-NLS-2$

        /**
         * A collection of all built-in namespace mappings.
         */
        NamespaceMapping[] ALL_BUILT_INS = new NamespaceMapping[] { JCR, NT, MIX, XML };
    }
}
