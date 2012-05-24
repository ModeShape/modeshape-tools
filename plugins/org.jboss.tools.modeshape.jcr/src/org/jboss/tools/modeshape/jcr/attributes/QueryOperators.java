/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.attributes;

import java.util.List;

import javax.jcr.query.qom.QueryObjectModelConstants;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;

/**
 * The query operators attribute used by property definitions. Initially a property supports all operators.
 */
public final class QueryOperators extends ListAttributeState<QueryOperator> {

    /**
     * The CND notation for each notation type.
     */
    public static final String[] NOTATION = new String[] { "queryops", "qop", "qop" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /**
     * Constructs a query operators attribute that supports all operators.
     */
    public QueryOperators() {
        for (final QueryOperator operator : QueryOperator.values()) {
            add(operator);
        }
    }

    /**
     * @param operator the operator notation (cannot be <code>null</code> or empty)
     * @return <code>true</code> if added
     * @throws IllegalArgumentException if an invalid operator notation
     */
    public boolean add( final String operator ) {
        Utils.verifyIsNotEmpty(operator, "operator"); //$NON-NLS-1$
        return add(QueryOperator.find(operator));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getCndNotationPrefix(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    protected String getCndNotationPrefix( final NotationType notationType ) {
        if (NotationType.LONG == notationType) {
            return NOTATION[CndElement.NotationType.LONG_INDEX];
        }

        if (NotationType.COMPRESSED == notationType) {
            return NOTATION[CndElement.NotationType.COMPRESSED_INDEX];
        }

        return NOTATION[CndElement.NotationType.COMPACT_INDEX];
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getCndNotationSuffix(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    protected String getCndNotationSuffix( NotationType notationType ) {
        return (isVariant() ? Utils.EMPTY_STRING : Utils.SINGLE_QUOTE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getItemQuoteCharacter()
     */
    @Override
    protected String getItemQuoteCharacter() {
        return Utils.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#getPrefixEndDelimiter(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    protected String getPrefixEndDelimiter( NotationType notationType ) {
        return (Utils.SPACE_STRING + (isVariant() ? Utils.EMPTY_STRING : Utils.SINGLE_QUOTE));
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 0;

        for (final QueryOperator operator : QueryOperator.values()) {
            if (supports(operator)) {
                result = Utils.hashCode(result, operator);
            }
        }

        return super.hashCode();
    }

    /**
     * @param operator the operator notation of the operator being removed (cannot be <code>null</code> or empty)
     * @return <code>true</code> if removed
     * @throws IllegalArgumentException if an invalid operator notation
     */
    public boolean remove( final String operator ) {
        Utils.verifyIsNotEmpty(operator, "operator"); //$NON-NLS-1$
        return remove(QueryOperator.find(operator));
    }

    private boolean supportsAll() {
        for (QueryOperator operator : QueryOperator.values()) {
            if (!supports(operator)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return a list of supported operator notations (never <code>null</code> but can be empty)
     */
    public String[] toArray() {
        final List<QueryOperator> operators = getSupportedItems();

        if (Utils.isEmpty(operators)) {
            return Utils.EMPTY_STRING_ARRAY;
        }

        final String[] result = new String[operators.size()];
        int i = 0;

        for (final QueryOperator operator : operators) {
            result[i++] = operator.toString();
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.jcr.attributes.ListAttributeState#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
     */
    @Override
    public String toCndNotation( NotationType notationType ) {
        if (supportsAll()) {
            return Utils.EMPTY_STRING;
        }

        return super.toCndNotation(notationType);
    }

    /**
     * The valid query operators.
     */
    public enum QueryOperator implements CndElement {

        /**
         * The equals sign.
         */
        EQUALS("=", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), //$NON-NLS-1$

        /**
         * The greater than sign.
         */
        GREATER_THAN(">", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), //$NON-NLS-1$

        /**
         * The greater than or equal to sign.
         */
        GREATER_THAN_EQUALS(">=", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), //$NON-NLS-1$

        /**
         * The less than sign.
         */
        LESS_THAN("<", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), //$NON-NLS-1$

        /**
         * The less than or equal to sign.
         */
        LESS_THAN_EQUALS("<=", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), //$NON-NLS-1$

        /**
         * The LIKE sign.
         */
        LIKE("LIKE", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO), //$NON-NLS-1$

        /**
         * The not equals sign.
         */
        NOT_EQUALS("<>", QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO); //$NON-NLS-1$

        /**
         * @param notation the operator's CND notation (cannot be <code>null</code> or empty)
         * @return the query operator (never <code>null</code>)
         * @throws IllegalArgumentException if an invalid CND notation
         */
        public static QueryOperator find( final String notation ) {
            for (final QueryOperator operator : QueryOperator.values()) {
                if (operator.notation.equals(notation)) {
                    return operator;
                }
            }

            throw new IllegalArgumentException(NLS.bind(Messages.invalidFindRequest, notation));
        }

        /**
         * @param jcrValue the {@link QueryObjectModelConstants} value of the query operator (cannot be <code>null</code> or empty)
         * @return the query operator (never <code>null</code>)
         * @throws IllegalArgumentException if an invalid JCR value
         */
        public static QueryOperator findUsingJcrValue( final String jcrValue ) {
            for (final QueryOperator operator : QueryOperator.values()) {
                if (operator.asJcrValue().equals(jcrValue)) {
                    return operator;
                }
            }

            throw new IllegalArgumentException(NLS.bind(Messages.invalidFindUsingJcrValueRequest, jcrValue));
        }

        private final String jcrValue;
        private final String notation;

        private QueryOperator( final String notation,
                               final String jcrValue ) {
            this.notation = notation;
            this.jcrValue = jcrValue;
        }

        /**
         * @return the {@link QueryObjectModelConstants} value (never <code>null</code> or empty)
         */
        public String asJcrValue() {
            return this.jcrValue;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.jboss.tools.modeshape.jcr.cnd.CndElement#toCndNotation(org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType)
         */
        @Override
        public String toCndNotation( final NotationType notationType ) {
            return toString();
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return this.notation;
        }
    }
}
