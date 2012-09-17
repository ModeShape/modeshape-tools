/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import java.math.BigDecimal;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.modeshape.jcr.Activator;
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.ItemDefinition;
import org.jboss.tools.modeshape.jcr.Messages;
import org.jboss.tools.modeshape.jcr.MultiValidationStatus;
import org.jboss.tools.modeshape.jcr.NamespaceMapping;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.WorkspaceRegistry;
import org.jboss.tools.modeshape.jcr.attributes.AttributeState.Value;
import org.jboss.tools.modeshape.jcr.attributes.PropertyType;
import org.jboss.tools.modeshape.jcr.attributes.QueryOperators.QueryOperator;

/**
 * Used to validate values stored in a CND.
 */
public final class CndValidator {

    /**
     * The valid characters allowed in a local name.
     */
    public static final String LOCAL_NAME_VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_."; //$NON-NLS-1$

    private static final String PARENT_PATH_SEGMENT = ".."; //$NON-NLS-1$

    private static final String SELF_PATH_SEGMENT = "."; //$NON-NLS-1$

    /**
     * @param value the value being checked (can be <code>null</code> or empty)
     * @param propertyType the property type of the property definition the value is for (cannot be <code>null</code>)
     * @param propertyName the name to use to identify the property definition (cannot be <code>null</code> empty)
     * @return the status (never <code>null</code>)
     */
    public static ValidationStatus isValid( final String value,
                                            final PropertyType propertyType,
                                            String propertyName ) {
        Utils.verifyIsNotNull(propertyType, "propertyType"); //$NON-NLS-1$

        if (Utils.isEmpty(propertyName)) {
            propertyName = Messages.missingName;
        }

        if (Utils.isEmpty(value)) {
            return ValidationStatus.createErrorMessage(StatusCodes.EMPTY_VALUE, NLS.bind(Messages.emptyValue, propertyName));
        }

        try {
            if (PropertyType.STRING == propertyType) {
                return ValidationStatus.OK_STATUS; // always valid
            }

            if (PropertyType.BINARY == propertyType) {
                return ValidationStatus.OK_STATUS; // always valid
            } else if (PropertyType.BOOLEAN == propertyType) {
                if (!value.equalsIgnoreCase(Boolean.TRUE.toString()) && !value.equalsIgnoreCase(Boolean.FALSE.toString())) {
                    return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                               NLS.bind(Messages.invalidPropertyValueForType, new Object[] {
                                                                   value, PropertyType.BOOLEAN, propertyName}));
                }
            } else if (PropertyType.DATE == propertyType) {
                try {
                    Date.valueOf(value);
                } catch (final Exception e) {
                    return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                               NLS.bind(Messages.invalidPropertyValueForType, new Object[] {
                                                                   value, PropertyType.DATE, propertyName}));
                }
            } else if (PropertyType.DECIMAL == propertyType) {
                try {
                    new BigDecimal(value);
                } catch (final Exception e) {
                    return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                               NLS.bind(Messages.invalidPropertyValueForType,
                                                                        value,
                                                                        PropertyType.DECIMAL));
                }
            } else if (PropertyType.DOUBLE == propertyType) {
                try {
                    Double.parseDouble(value);
                } catch (final Exception e) {
                    return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                               NLS.bind(Messages.invalidPropertyValueForType,
                                                                        value,
                                                                        PropertyType.DOUBLE));
                }
            } else if (PropertyType.LONG == propertyType) {
                try {
                    Long.parseLong(value);
                } catch (final Exception e) {
                    return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                               NLS.bind(Messages.invalidPropertyValueForType,
                                                                        value,
                                                                        PropertyType.LONG));
                }
            } else if (PropertyType.NAME == propertyType) {
                return validateQualifiedName(QualifiedName.parse(value), propertyName, null, null);
            } else if (PropertyType.PATH == propertyType) {
                return validatePath(value, propertyName);
            } else if (PropertyType.REFERENCE == propertyType) {
                return ValidationStatus.OK_STATUS; // always valid
            } else if (PropertyType.UNDEFINED == propertyType) {
                return ValidationStatus.OK_STATUS; // always valid
            } else if (PropertyType.URI == propertyType) {
                return validateUri(value, propertyName);
            } else if (PropertyType.WEAKREFERENCE == propertyType) {
                return ValidationStatus.OK_STATUS; // always valid
            }

            return ValidationStatus.OK_STATUS;
        } catch (final Exception e) {
            return ValidationStatus.createErrorMessage(StatusCodes.ERROR_VALIDATING_PROPERTY_VALUE_FOR_TYPE,
                                                       NLS.bind(Messages.errorValidatingPropertyValueForType, new Object[] {
                                                           value, propertyType, propertyName}));
        }
    }

    /**
     * @param value the value being checked (can be <code>null</code> or empty)
     * @param propertyType the property type of the property definition the value is for (cannot be <code>null</code>)
     * @param propertyName the name to use to identify the property definition (cannot be <code>null</code> empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void isValid( final String value,
                                final PropertyType propertyType,
                                final String propertyName,
                                final MultiValidationStatus status ) {
        final ValidationStatus newStatus = isValid(value, propertyType, propertyName);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param childNodeDefinition the child node definition being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingChildNodeNames the existing child node names used to check for a duplicate (can be <code>null</code> or
     *        empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateChildNodeDefinition( final ChildNodeDefinition childNodeDefinition,
                                                                     final Collection<String> validNamespacePrefixes,
                                                                     final Collection<QualifiedName> existingChildNodeNames ) {
        Utils.verifyIsNotNull(childNodeDefinition, "childNodeDefinition"); //$NON-NLS-1$

        /**
         * <pre>
         *     ERROR - Empty or invalid child node definition name
         *     ERROR - Invalid required type name
         *     ERROR - Duplicate required type name
         *     ERROR - Cannot have explicit required types when required types is marked as a variant
         *     ERROR - Invalid default type name
         *     ERROR - Cannot have explicit default type when default type is marked as a variant
         * </pre>
         */

        final MultiValidationStatus status = new MultiValidationStatus();
        String childNodeName = childNodeDefinition.getName();

        if (Utils.isEmpty(childNodeName)) {
            childNodeName = Messages.missingName;
        }

        // name
        validateName(childNodeDefinition, validNamespacePrefixes, existingChildNodeNames, status);

        // required types
        validateRequiredTypes(childNodeDefinition, validNamespacePrefixes, status);

        // default type
        validateDefaultType(childNodeDefinition, validNamespacePrefixes, status);

        return status;
    }

    /**
     * @param childNodeDefinition the child node definition being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingChildNodeNames the existing child node names used to check for a duplicate (can be <code>null</code> or
     *        empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateChildNodeDefinition( final ChildNodeDefinition childNodeDefinition,
                                                    final Collection<String> validNamespacePrefixes,
                                                    final Collection<QualifiedName> existingChildNodeNames,
                                                    final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateChildNodeDefinition(childNodeDefinition,
                                                                       validNamespacePrefixes,
                                                                       existingChildNodeNames);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param nodeTypeName the node type name whose child node definitions are being checked (cannot be <code>null</code> or
     *        empty)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param childNodeDefinitions the collection of a node type definition's child node definitions to validate (can be
     *        <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateChildNodeDefinitions( final String nodeTypeName,
                                                                      final Collection<String> validNamespacePrefixes,
                                                                      final Collection<ChildNodeDefinition> childNodeDefinitions ) {
        Utils.verifyIsNotEmpty(nodeTypeName, "nodeTypeName"); //$NON-NLS-1$

        /**
         * <pre>
         *     ERROR - Duplicate child node definition names
         * </pre>
         */

        // OK to have none
        if (Utils.isEmpty(childNodeDefinitions)) {
            return MultiValidationStatus.OK_STATUS;
        }

        final MultiValidationStatus status = new MultiValidationStatus();
        final Collection<String> childNodeNames = new ArrayList<String>(childNodeDefinitions.size());

        for (final ChildNodeDefinition childNodeDefn : childNodeDefinitions) {
            validateChildNodeDefinition(childNodeDefn, validNamespacePrefixes, null, status);

            { // ERROR - Duplicate child node definition names (allow duplicate residual names)
                final String childNodeName = childNodeDefn.getName();

                if (!Utils.isEmpty(childNodeName) && !ItemDefinition.RESIDUAL_NAME.equals(childNodeName)) {
                    if (childNodeNames.contains(childNodeName)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_CHILD_NODE_DEFINITION_NAME,
                                                                       NLS.bind(Messages.duplicateChildNodeDefinitionName,
                                                                                nodeTypeName,
                                                                                childNodeName)));
                    } else {
                        childNodeNames.add(childNodeName);
                    }
                }
            }
        }

        return status;
    }

    /**
     * @param nodeTypeName the node type name whose child node definitions are being checked (cannot be <code>null</code> or
     *        empty)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param childNodeDefinitions the collection of a node type definition's child node definitions to validate (can be
     *        <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateChildNodeDefinitions( final String nodeTypeName,
                                                     final Collection<String> validNamespacePrefixes,
                                                     final Collection<ChildNodeDefinition> childNodeDefinitions,
                                                     final MultiValidationStatus status ) {
        final MultiValidationStatus newStatus = validateChildNodeDefinitions(nodeTypeName,
                                                                             validNamespacePrefixes,
                                                                             childNodeDefinitions);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param cnd the CND being validated (cannot be <code>null</code>)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateCnd( final CompactNodeTypeDefinition cnd ) {
        Utils.verifyIsNotNull(cnd, "cnd"); //$NON-NLS-1$

        /**
         * <pre>
         *     WARNING - No namespace declarations or node type definitions exist
         * </pre>
         */

        final MultiValidationStatus status = new MultiValidationStatus();
        boolean noNamespaceMappings = false;
        boolean noNodeTypeDefinitions = false;

        { // namespace mappings
            final Collection<NamespaceMapping> namespaceMappings = cnd.getNamespaceMappings();

            if (Utils.isEmpty(namespaceMappings)) {
                noNamespaceMappings = true;
            } else {
                validateNamespaceMappings(namespaceMappings, status);
            }
        }

        { // node type definitions
            final Collection<NodeTypeDefinition> nodeTypeDefinitions = cnd.getNodeTypeDefinitions();

            if (Utils.isEmpty(nodeTypeDefinitions)) {
                noNodeTypeDefinitions = true;
            } else {
                validateNodeTypeDefinitions(nodeTypeDefinitions, cnd.getNamespacePrefixes(), true, status);
            }
        }

        // WARNING - No namespace declarations or node type definitions exist
        if (noNamespaceMappings && noNodeTypeDefinitions) {
            status.add(ValidationStatus.createWarningMessage(StatusCodes.CND_HAS_NO_NAMESPACES_OR_NODE_TYPE_DEFINITIONS,
                                                             Messages.cndHasNoNamespacesOrNodeTypeDefinitions));
        }

        return status;
    }

    /**
     * @param cnd the CND being validated (cannot be <code>null</code>)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateCnd( final CompactNodeTypeDefinition cnd,
                                    final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateCnd(cnd);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param childNodeDefinition the child node definition whose default type is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateDefaultType( final ChildNodeDefinition childNodeDefinition,
                                                             final Collection<String> validNamespacePrefixes ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateDefaultType(childNodeDefinition, validNamespacePrefixes, status);
        return status;
    }

    /**
     * @param childNodeDefinition the child node definition whose default type is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateDefaultType( final ChildNodeDefinition childNodeDefinition,
                                            final Collection<String> validNamespacePrefixes,
                                            final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(childNodeDefinition, "childNodeDefinition"); //$NON-NLS-1$
        Utils.verifyIsNotNull(status, "status"); //$NON-NLS-1$

        String childNodeName = childNodeDefinition.getName();

        if (Utils.isEmpty(childNodeName)) {
            childNodeName = Messages.missingName;
        }

        final String defaultTypeName = childNodeDefinition.getDefaultPrimaryTypeName();

        if (childNodeDefinition.getState(ChildNodeDefinition.PropertyName.DEFAULT_TYPE) == Value.IS) {
            // ERROR - Invalid default type name (cannot be empty)
            validateQualifiedName(childNodeDefinition.getDefaultType().getDefaultType(),
                                  Messages.defaultTypeName,
                                  validNamespacePrefixes,
                                  null,
                                  status);

            // ERROR - Must be equal to a required type if not a residual
            if (!ItemDefinition.RESIDUAL_NAME.equals(childNodeName)) {
                final String[] requiredTypes = childNodeDefinition.getRequiredPrimaryTypeNames();
                boolean foundMatch = false;

                if (!Utils.isEmpty(defaultTypeName)) {
                    for (final String requiredType : requiredTypes) {
                        if (defaultTypeName.equals(requiredType)) {
                            foundMatch = true;
                            break;
                        }
                    }

                    if (!foundMatch) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DEFAULT_TYPE_DOES_NOT_MATCH_REQUIRED_TYPE,
                                                                       NLS.bind(Messages.defaultTypeDoesNotMatchRequiredType,
                                                                                childNodeName,
                                                                                defaultTypeName)));
                    }
                }
            }
        } else if (!Utils.isEmpty(defaultTypeName)) {
            // ERROR - Cannot have explicit default type when default type is marked as a variant
            status.add(ValidationStatus.createErrorMessage(StatusCodes.DEFAULT_TYPE_EXISTS_BUT_MARKED_AS_VARIANT,
                                                           NLS.bind(Messages.defaultTypeExistsButMarkedAsVariant, childNodeName)));
        }
    }

    /**
     * @param propertyDefinition the property definition whose default values are being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateDefaultValues( final PropertyDefinition propertyDefinition,
                                                               final Collection<String> validNamespacePrefixes ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateDefaultValues(propertyDefinition, validNamespacePrefixes, status);
        return status;
    }

    /**
     * @param propertyDefinition the property definition whose default values are being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateDefaultValues( final PropertyDefinition propertyDefinition,
                                              final Collection<String> validNamespacePrefixes,
                                              final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(propertyDefinition, "propertyDefinition"); //$NON-NLS-1$
        Utils.verifyIsNotNull(status, "status"); //$NON-NLS-1$

        String propertyName = propertyDefinition.getName();

        if (Utils.isEmpty(propertyName)) {
            propertyName = Messages.missingName;
        }

        final Collection<String> defaultValues = propertyDefinition.getDefaultValuesAsStrings();

        if (Utils.isEmpty(defaultValues)) {
            if (propertyDefinition.getState(PropertyDefinition.PropertyName.DEFAULT_VALUES) == Value.IS) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.EMPTY_DEFAULT_VALUES,
                                                               NLS.bind(Messages.emptyDefaultValues, propertyName)));
            }
        } else {
            // ERROR - Cannot have multiple default values when the property definition is single-valued
            if ((defaultValues.size() > 1)
                && (propertyDefinition.getState(PropertyDefinition.PropertyName.MULTIPLE) == Value.IS_NOT)) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.MULTIPLE_DEFAULT_VALUES_FOR_SINGLE_VALUED_PROPERTY,
                                                               NLS.bind(Messages.multipleDefaultValuesForSingleValuedProperty,
                                                                        propertyName)));
            }

            validateDefaultValues(propertyName, propertyDefinition.getType(), defaultValues, validNamespacePrefixes, status);

            // ERROR - Cannot have explicit default values when default values is marked as a variant
            if (propertyDefinition.getState(PropertyDefinition.PropertyName.DEFAULT_VALUES) != Value.IS) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.DEFAULT_VALUES_EXIST_BUT_MARKED_AS_VARIANT,
                                                               NLS.bind(Messages.defaultValuesExistButMarkedAsVariant,
                                                                        propertyDefinition)));
            }
        }
    }

    /**
     * Checks for duplicates and correct value for type.
     *
     * @param propertyName the name of the property definition (can be <code>null</code> or empty)
     * @param propertyType the property definition's property type (cannot be <code>null</code>)
     * @param defaultValues the default values (can be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateDefaultValues( final String propertyName,
                                                               final PropertyType propertyType,
                                                               final Collection<String> defaultValues,
                                                               final Collection<String> validNamespacePrefixes ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateDefaultValues(propertyName, propertyType, defaultValues, validNamespacePrefixes, status);
        return status;
    }

    /**
     * Checks for duplicates and correct value for type.
     *
     * @param propertyName the name of the property definition (cannot be <code>null</code> or empty)
     * @param propertyType the property definition's property type (cannot be <code>null</code>)
     * @param defaultValues the default values (can be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateDefaultValues( String propertyName,
                                              final PropertyType propertyType,
                                              final Collection<String> defaultValues,
                                              final Collection<String> validNamespacePrefixes,
                                              final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(propertyType, "propertyType"); //$NON-NLS-1$

        if (propertyName == null) {
            propertyName = Messages.missingName;
        }

        if (!Utils.isEmpty(defaultValues)) {
            final Collection<String> values = new ArrayList<String>(defaultValues.size());

            for (final String defaultValue : defaultValues) {
                // ERROR - Default value is not valid for the property definition type
                isValid(defaultValue, propertyType, Messages.defaultValue, status);

                // make sure if NAME type the qualifier is valid
                final Collection<String> qualifiers = (Utils.isEmpty(validNamespacePrefixes) ? Collections.<String>emptyList() : validNamespacePrefixes);

                if (propertyType == PropertyType.NAME) {
                    final QualifiedName qname = QualifiedName.parse(defaultValue);
                    final String qualifier = qname.getQualifier();
                    boolean valid = false;

                    if (!Utils.isEmpty(qualifier)) {
                        for (final String validQualifier : qualifiers) {
                            if (validQualifier.equals(qualifier)) {
                                valid = true;
                                break;
                            }
                        }
                    }

                    if (!valid) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.INVALID_QUALIFIER_FOR_DEFAULT_VALUE,
                                                                       NLS.bind(Messages.invalidQualifierForDefaultValue,
                                                                                propertyName,
                                                                                defaultValue)));
                    }
                }

                if (!Utils.isEmpty(defaultValue)) {
                    // ERROR - Duplicate default value
                    if (values.contains(defaultValue)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_DEFAULT_VALUE,
                                                                       NLS.bind(Messages.duplicateDefaultValue,
                                                                                propertyName,
                                                                                defaultValue)));
                    } else {
                        values.add(defaultValue);
                    }
                }
            }
        }
    }

    /**
     * @param localName the local name being validated (cannot be <code>null</code>)
     * @param messagePrefix the text that will print before the message indicating the local name is invalid (cannot be
     *        <code>null</code>)
     * @return the status (never <code>null</code>)
     */
    public static ValidationStatus validateLocalName( final String localName,
                                                      final String messagePrefix ) {
        Utils.verifyIsNotEmpty(messagePrefix, messagePrefix);

        if (Utils.isEmpty(localName)) {
            return ValidationStatus.createErrorMessage(StatusCodes.EMPTY_LOCAL_NAME,
                                                       NLS.bind(Messages.emptyLocalName, messagePrefix));
        }

        // ERROR Local name cannot be self or parent
        if (localName.equals(SELF_PATH_SEGMENT) || localName.equals(PARENT_PATH_SEGMENT)) {
            return ValidationStatus.createErrorMessage(StatusCodes.LOCAL_NAME_EQUAL_TO_SELF_OR_PARENT,
                                                       NLS.bind(Messages.localNameEqualToSelfOrParent, messagePrefix));
        }

        for (final char c : localName.toCharArray()) {
            switch (c) {
                case '/':
                case ':':
                case '[':
                case ']':
                case '|':
                case '*':
                    // ERROR invalid character
                    return ValidationStatus.createErrorMessage(StatusCodes.LOCAL_NAME_HAS_INVALID_CHARACTERS,
                                                               NLS.bind(Messages.localNameHasInvalidCharacters,
                                                                        messagePrefix,
                                                                        localName));
                default:
                    continue;
            }
        }

        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param localName the local name being validated (cannot be <code>null</code>)
     * @param propertyName the name to use in the validation message (cannot be <code>null</code>)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateLocalName( final String localName,
                                          final String propertyName,
                                          final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateLocalName(localName, propertyName);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param childNodeDefinition the child node definition whose name is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingChildNodeNames the existing child node names used to check for a duplicate (can be <code>null</code> or
     *        empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateName( final ChildNodeDefinition childNodeDefinition,
                                                      final Collection<String> validNamespacePrefixes,
                                                      final Collection<QualifiedName> existingChildNodeNames ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateName(childNodeDefinition, validNamespacePrefixes, existingChildNodeNames, status);
        return status;
    }

    /**
     * @param childNodeDefinition the child node definition whose name is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingChildNodeNames the existing child node names used to check for a duplicate (can be <code>null</code> or
     *        empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateName( final ChildNodeDefinition childNodeDefinition,
                                     final Collection<String> validNamespacePrefixes,
                                     final Collection<QualifiedName> existingChildNodeNames,
                                     final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(childNodeDefinition, "childNodeDefinition"); //$NON-NLS-1$

        // allow residual name
        if (!ItemDefinition.RESIDUAL_NAME.equals(childNodeDefinition.getName())) {
            // ERROR - Empty or invalid child node definition name
            validateQualifiedName(childNodeDefinition.getQualifiedName(),
                                  Messages.childNodeDefinitionName,
                                  validNamespacePrefixes,
                                  existingChildNodeNames,
                                  status);
        }
    }

    /**
     * @param nodeTypeDefinition the node type definition whose name is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingNodeTypeNames the existing node type names used to check for a duplicate (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateName( final NodeTypeDefinition nodeTypeDefinition,
                                                      final Collection<String> validNamespacePrefixes,
                                                      final Collection<QualifiedName> existingNodeTypeNames ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateName(nodeTypeDefinition, validNamespacePrefixes, existingNodeTypeNames, status);
        return status;
    }

    /**
     * @param nodeTypeDefinition the node type definition whose name is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingNodeTypeNames the existing node type names used to check for a duplicate (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateName( final NodeTypeDefinition nodeTypeDefinition,
                                     final Collection<String> validNamespacePrefixes,
                                     final Collection<QualifiedName> existingNodeTypeNames,
                                     final MultiValidationStatus status ) {
        // ERROR - Empty or invalid node type definition name
        validateQualifiedName(nodeTypeDefinition.getQualifiedName(),
                              Messages.nodeTypeDefinitionName,
                              validNamespacePrefixes,
                              existingNodeTypeNames,
                              status);
    }

    /**
     * @param propertyDefinition the property definition whose name is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingPropertyNames the existing property names used to check for a duplicate (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateName( final PropertyDefinition propertyDefinition,
                                                      final Collection<String> validNamespacePrefixes,
                                                      final Collection<QualifiedName> existingPropertyNames ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateName(propertyDefinition, validNamespacePrefixes, existingPropertyNames, status);
        return status;
    }

    /**
     * @param propertyDefinition the property definition whose name is being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingPropertyNames the existing property names used to check for a duplicate (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateName( final PropertyDefinition propertyDefinition,
                                     final Collection<String> validNamespacePrefixes,
                                     final Collection<QualifiedName> existingPropertyNames,
                                     final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(propertyDefinition, "propertyDefinition"); //$NON-NLS-1$

        // allow residual name
        if (!ItemDefinition.RESIDUAL_NAME.equals(propertyDefinition.getName())) {
            // ERROR - Empty or invalid child node definition name
            validateQualifiedName(propertyDefinition.getQualifiedName(),
                                  Messages.propertyDefinitionName,
                                  validNamespacePrefixes,
                                  existingPropertyNames,
                                  status);
        }
    }

    /**
     * @param namespaceMapping the namespace mapping being validated (cannot be <code>null</code>)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateNamespaceMapping( final NamespaceMapping namespaceMapping ) {
        Utils.verifyIsNotNull(namespaceMapping, "namespaceMapping"); //$NON-NLS-1$

        /**
         * <pre>
         *     ERROR - Empty or invalid prefix
         *     ERROR - Empty or invalid URI
         * </pre>
         */

        final MultiValidationStatus status = new MultiValidationStatus();
        final String prefix = namespaceMapping.getPrefix();
        final String uri = namespaceMapping.getUri();

        // ERROR - Empty or invalid prefix
        validateLocalName(prefix, Messages.namespacePrefix, status);

        // ERROR - Empty or invalid URI
        final ValidationStatus uriStatus = validateUri(uri, Messages.namespaceUri);

        if (!uriStatus.isOk()) {
            status.add(uriStatus);
        }

        // ERROR - prefix matches a built-in but URI does not match
        try {
            if (!Utils.isEmpty(prefix) && WorkspaceRegistry.get().isBuiltInNamespacePrefix(prefix)) {
                if (!Utils.equals(uri, WorkspaceRegistry.get().getUri(prefix))) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.INVALID_URI_FOR_BUILT_IN_NAMESPACE_PREFIX,
                                                                   NLS.bind(Messages.invalidUriForBuiltInNamespacePrefix,
                                                                            new Object[] {uri, prefix,
                                                                                WorkspaceRegistry.get().getUri(prefix)})));
                }
            }

            // ERROR - URI matches a built-in but prefix does not match
            if (!Utils.isEmpty(uri) && WorkspaceRegistry.get().isBuiltInNamespaceUri(uri)) {
                if (!Utils.equals(prefix, WorkspaceRegistry.get().getPrefix(uri))) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.INVALID_PREFIX_FOR_BUILT_IN_NAMESPACE_URI,
                                                                   NLS.bind(Messages.invalidPrefixForBuiltInNamespaceUri,
                                                                            new Object[] {prefix, uri,
                                                                                WorkspaceRegistry.get().getPrefix(uri)})));
                }
            }
        } catch (final Exception e) {
            if (Platform.isRunning()) {
                Activator.get().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, null, e));
            } else {
                System.err.print(e.getMessage());
            }
        }

        return status;
    }

    /**
     * @param namespaceMapping the namespace mapping being validated (cannot be <code>null</code>)
     * @param existingNamespaces the existing namespaces whose prefixes and URIs will be checked against (can be <code>null</code>
     *        or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateNamespaceMapping( final NamespaceMapping namespaceMapping,
                                                                  final Collection<NamespaceMapping> existingNamespaces ) {
        final MultiValidationStatus status = validateNamespaceMapping(namespaceMapping);

        // check for duplicate prefix or URI
        if (!Utils.isEmpty(existingNamespaces)) {
            boolean checkPrefix = true;
            boolean checkUri = true;

            for (final NamespaceMapping namespace : existingNamespaces) {
                if (checkPrefix && Utils.equivalent(namespaceMapping.getPrefix(), namespace.getPrefix())) {
                    checkPrefix = false;
                    String prefix = namespaceMapping.getPrefix();

                    if (prefix == null) {
                        prefix = Utils.EMPTY_STRING;
                    }

                    status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_NAMESPACE_PREFIX,
                                                                   NLS.bind(Messages.duplicateNamespacePrefix, prefix)));
                }

                if (checkUri && Utils.equivalent(namespaceMapping.getUri(), namespace.getUri())) {
                    checkUri = false;
                    String uri = namespaceMapping.getUri();

                    if (uri == null) {
                        uri = Utils.EMPTY_STRING;
                    }

                    status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_NAMESPACE_URI,
                                                                   NLS.bind(Messages.duplicateNamespaceUri, uri)));
                }
            }
        }

        return status;
    }

    /**
     * @param namespaceMapping the namespace mapping being validated (cannot be <code>null</code>)
     * @param existingNamespaces the existing namespaces whose prefixes and URIs will be checked against (can be <code>null</code>
     *        or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateNamespaceMapping( final NamespaceMapping namespaceMapping,
                                                 final Collection<NamespaceMapping> existingNamespaces,
                                                 final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateNamespaceMapping(namespaceMapping, existingNamespaces);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param namespaceMapping the namespace mapping being validated (cannot be <code>null</code>)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateNamespaceMapping( final NamespaceMapping namespaceMapping,
                                                 final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateNamespaceMapping(namespaceMapping);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param namespaceMappings the collection of namespace mappings being validated (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateNamespaceMappings( final Collection<NamespaceMapping> namespaceMappings ) {
        /**
         * <pre>
         *     ERROR - Duplicate namespace mapping prefix
         *     ERROR - Duplicate namespace mapping URI
         * </pre>
         */

        // OK not to have namespaces
        if (Utils.isEmpty(namespaceMappings)) {
            return MultiValidationStatus.OK_STATUS;
        }

        final MultiValidationStatus status = new MultiValidationStatus();
        final Collection<String> prefixes = new ArrayList<String>(namespaceMappings.size());
        final Collection<String> uris = new ArrayList<String>(namespaceMappings.size());

        for (final NamespaceMapping namespaceMapping : namespaceMappings) {
            validateNamespaceMapping(namespaceMapping, status);

            { // ERROR - Duplicate namespace mapping prefix
                final String prefix = namespaceMapping.getPrefix();

                if (!Utils.isEmpty(prefix)) {
                    if (prefixes.contains(prefix)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_NAMESPACE_PREFIX,
                                                                       NLS.bind(Messages.duplicateNamespacePrefix, prefix)));
                    } else {
                        prefixes.add(prefix);
                    }
                }
            }

            { // ERROR - Duplicate namespace mapping URI
                final String uri = namespaceMapping.getUri();

                if (!Utils.isEmpty(uri)) {
                    if (uris.contains(uri)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_NAMESPACE_URI,
                                                                       NLS.bind(Messages.duplicateNamespaceUri, uri)));
                    } else {
                        uris.add(uri);
                    }
                }
            }
        }

        return status;
    }

    /**
     * @param namespaceMappings the collection of namespace mappings being validated (can be <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateNamespaceMappings( final Collection<NamespaceMapping> namespaceMappings,
                                                  final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateNamespaceMappings(namespaceMappings);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param nodeTypeDefinition the node type definition being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes a collection of namespace prefixes that the qualified name must match (can be
     *        <code>null</code> or empty)
     * @param existingNodeTypeNames the existing node type names used to check for a duplicate (can be <code>null</code> or empty)
     * @param validateEachPropertyAndChildNode indicates if property definition and child node definition validation should be
     *        done
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateNodeTypeDefinition( final NodeTypeDefinition nodeTypeDefinition,
                                                                    final Collection<String> validNamespacePrefixes,
                                                                    final Collection<QualifiedName> existingNodeTypeNames,
                                                                    final boolean validateEachPropertyAndChildNode ) {
        Utils.verifyIsNotNull(nodeTypeDefinition, "nodeTypeDefinition"); //$NON-NLS-1$

        /**
         * <pre>
         *     WARNING - No property definitions or child node definitions exist
         *     ERROR - Empty or invalid node type definition name
         *     ERROR - Duplicate super type name
         *     ERROR - Invalid super type name
         *     ERROR - Cannot have explicit super types when super types is marked as a variant
         *     ERROR - Empty or invalid primary item name
         *     ERROR - Cannot have a primary item name when the primary item node type attribute is marked as a variant
         *     ERROR - Duplicate property definition names
         *     ERROR - Duplicate child node definition names
         * </pre>
         */

        final MultiValidationStatus status = new MultiValidationStatus();
        String nodeTypeDefinitionName = nodeTypeDefinition.getName();

        if (Utils.isEmpty(nodeTypeDefinitionName)) {
            nodeTypeDefinitionName = Messages.missingName;
        }

        { // name
          // ERROR - Empty or invalid node type definition name
            validateName(nodeTypeDefinition, validNamespacePrefixes, existingNodeTypeNames, status);
        }

        { // super types
            final Collection<QualifiedName> superTypeNames = nodeTypeDefinition.getSupertypes();

            if (Utils.isEmpty(superTypeNames)) {
                if (nodeTypeDefinition.getState(NodeTypeDefinition.PropertyName.SUPERTYPES) == Value.IS) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.EMPTY_SUPER_TYPES,
                                                                   NLS.bind(Messages.emptySuperTypes, nodeTypeDefinitionName)));
                }
            } else {
                validateSuperTypes(nodeTypeDefinitionName,
                                   validNamespacePrefixes,
                                   nodeTypeDefinition.getState(NodeTypeDefinition.PropertyName.SUPERTYPES),
                                   superTypeNames,
                                   status);
            }
        }

        { // primary item
            final QualifiedName primaryItemName = nodeTypeDefinition.getPrimaryItem();

            if (nodeTypeDefinition.getState(NodeTypeDefinition.PropertyName.PRIMARY_ITEM) == Value.IS) {
                // ERROR - Empty or invalid primary item name
                validateQualifiedName(primaryItemName, Messages.primaryItemName, validNamespacePrefixes, null, status);
            } else if (!Utils.isEmpty(primaryItemName.get())) {
                // ERROR Cannot have a primary item name when the primary item node type attribute is marked as a variant
                status.add(ValidationStatus.createErrorMessage(StatusCodes.PRIMARY_ITEM_EXISTS_BUT_MARKED_AS_VARIANT,
                                                               NLS.bind(Messages.primaryItemExistsButMarkedAsVariant,
                                                                        nodeTypeDefinitionName)));
            }
        }

        if (validateEachPropertyAndChildNode) {
            { // property definitions
                final Collection<PropertyDefinition> propertyDefinitions = nodeTypeDefinition.getPropertyDefinitions();

                if (!Utils.isEmpty(propertyDefinitions)) {
                    validatePropertyDefinitions(nodeTypeDefinitionName, validNamespacePrefixes, propertyDefinitions, status);
                }
            }

            { // child node definitions
                final Collection<ChildNodeDefinition> childNodeDefinitions = nodeTypeDefinition.getChildNodeDefinitions();

                if (!Utils.isEmpty(childNodeDefinitions)) {
                    validateChildNodeDefinitions(nodeTypeDefinitionName, validNamespacePrefixes, childNodeDefinitions, status);
                }
            }
        }

        return status;
    }

    /**
     * @param nodeTypeDefinition the node type definition being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes a collection of namespace prefixes that the qualified name must match (can be
     *        <code>null</code> or empty)
     * @param existingNodeTypeNames the existing node type names used to check for a duplicate (can be <code>null</code> or empty)
     * @param validateEachPropertyAndChildNode indicates if property definition and child node definition validation should be
     *        done
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateNodeTypeDefinition( final NodeTypeDefinition nodeTypeDefinition,
                                                   final Collection<String> validNamespacePrefixes,
                                                   final Collection<QualifiedName> existingNodeTypeNames,
                                                   final boolean validateEachPropertyAndChildNode,
                                                   final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateNodeTypeDefinition(nodeTypeDefinition,
                                                                      validNamespacePrefixes,
                                                                      existingNodeTypeNames,
                                                                      validateEachPropertyAndChildNode);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param nodeTypeDefinitions the collection of namespace mappings to validate (can be <code>null</code> or empty)
     * @param validNamespacePrefixes a collection of namespace prefixes that the qualified name must match (can be
     *        <code>null</code> or empty)
     * @param validateEachPropertyAndChildNode indicates if property definition and child node definition validation should be
     *        done
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateNodeTypeDefinitions( final Collection<NodeTypeDefinition> nodeTypeDefinitions,
                                                                     final Collection<String> validNamespacePrefixes,
                                                                     final boolean validateEachPropertyAndChildNode ) {
        /**
         * <pre>
         *     ERROR - Duplicate node type definition names
         * </pre>
         */

        // OK not to have node type definitions
        if (Utils.isEmpty(nodeTypeDefinitions)) {
            return MultiValidationStatus.OK_STATUS;
        }

        final MultiValidationStatus status = new MultiValidationStatus();
        final Collection<String> names = new ArrayList<String>(nodeTypeDefinitions.size());

        for (final NodeTypeDefinition nodeTypeDefinition : nodeTypeDefinitions) {
            validateNodeTypeDefinition(nodeTypeDefinition, validNamespacePrefixes, null, validateEachPropertyAndChildNode, status);

            { // ERROR - Duplicate node type definition names
                final String name = nodeTypeDefinition.getName();

                if (!Utils.isEmpty(name)) {
                    if (names.contains(name)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_NODE_TYPE_DEFINITION_NAME,
                                                                       NLS.bind(Messages.duplicateNodeTypeDefinitionName, name)));
                    } else {
                        names.add(name);
                    }
                }
            }
        }

        return status;
    }

    /**
     * @param nodeTypeDefinitions the collection of namespace mappings to validate (can be <code>null</code> or empty)
     * @param validNamespacePrefixes a collection of namespace prefixes that the qualified name must match (can be
     *        <code>null</code> or empty)
     * @param validateEachPropertyAndChildNode indicates if property definition and child node definition validation should be
     *        done
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateNodeTypeDefinitions( final Collection<NodeTypeDefinition> nodeTypeDefinitions,
                                                    final Collection<String> validNamespacePrefixes,
                                                    final boolean validateEachPropertyAndChildNode,
                                                    final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateNodeTypeDefinitions(nodeTypeDefinitions,
                                                                       validNamespacePrefixes,
                                                                       validateEachPropertyAndChildNode);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param path the path value being validated (can be <code>null</code> or empty)
     * @param propertyName the property name whose path value is being validated (cannot be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static ValidationStatus validatePath( final String path,
                                                 String propertyName ) {
        Utils.verifyIsNotNull(propertyName, "propertyName"); //$NON-NLS-1$

        if (Utils.isEmpty(propertyName)) {
            propertyName = Messages.missingName;
        }

        if (Utils.isEmpty(path)) {
            return ValidationStatus.createErrorMessage(StatusCodes.EMPTY_VALUE, NLS.bind(Messages.emptyValue, propertyName));
        }

        final StringTokenizer pathTokenizer = new StringTokenizer(path, "/"); //$NON-NLS-1$

        if (pathTokenizer.hasMoreTokens()) {
            while (pathTokenizer.hasMoreElements()) {
                final String segment = pathTokenizer.nextToken();

                if (Utils.isEmpty(segment)) {
                    if (pathTokenizer.hasMoreTokens()) {
                        // found empty segment
                        return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                                   NLS.bind(Messages.invalidPropertyValueForType,
                                                                            path,
                                                                            PropertyType.PATH));
                    }
                } else {
                    final StringTokenizer segmentTokenizer = new StringTokenizer(segment, "[]"); //$NON-NLS-1$

                    if (segmentTokenizer.countTokens() == 2) {
                        // has SNS index
                        final String qualifiedName = segmentTokenizer.nextToken();

                        if (Utils.isEmpty(qualifiedName)) {
                            // found SNS but now qualified name
                            return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                                       NLS.bind(Messages.invalidPropertyValueForType,
                                                                                path,
                                                                                PropertyType.PATH));
                        }

                        // OK if segment is self or parent
                        if (PARENT_PATH_SEGMENT.equals(qualifiedName) || SELF_PATH_SEGMENT.equals(qualifiedName)) {
                            continue;
                        }

                        // validate qualified name
                        final QualifiedName qname = QualifiedName.parse(qualifiedName);
                        final MultiValidationStatus status = validateQualifiedName(qname, propertyName, null, null);

                        // return if invalid qualified
                        if (status.isError()) {
                            return status;
                        }

                        // valid qualified name so check SNS index
                        if (segmentTokenizer.countTokens() == 1) {
                            final String snsIndex = segmentTokenizer.nextToken();

                            // make sure SNS index is a number
                            for (final char c : snsIndex.toCharArray()) {
                                if (!Character.isDigit(c)) {
                                    // found invalid character
                                    return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                                               NLS.bind(Messages.invalidPropertyValueForType,
                                                                                        path,
                                                                                        PropertyType.PATH));
                                }
                            }
                        } else {
                            // no ending SNS bracket
                            return ValidationStatus.createErrorMessage(StatusCodes.INVALID_PROPERTY_VALUE_FOR_TYPE,
                                                                       NLS.bind(Messages.invalidPropertyValueForType,
                                                                                path,
                                                                                PropertyType.PATH));
                        }
                    } else {
                        // OK if segment is self or parent
                        if (PARENT_PATH_SEGMENT.equals(segment) || SELF_PATH_SEGMENT.equals(segment)) {
                            continue;
                        }

                        // no SNS index
                        final QualifiedName qname = QualifiedName.parse(segment);
                        final MultiValidationStatus status = validateQualifiedName(qname, propertyName, null, null);

                        // return if invalid segment
                        if (status.isError()) {
                            return status;
                        }
                    }
                }
            }
        } else {
            // only one segment
            final QualifiedName qname = QualifiedName.parse(path);
            return validateQualifiedName(qname, propertyName, null, null);
        }

        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param propertyDefinition the property definition being validated (never <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingPropertyNames the existing property names used to check for a duplicate (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validatePropertyDefinition( final PropertyDefinition propertyDefinition,
                                                                    final Collection<String> validNamespacePrefixes,
                                                                    final Collection<QualifiedName> existingPropertyNames ) {
        Utils.verifyIsNotNull(propertyDefinition, "propertyDefinition"); //$NON-NLS-1$

        /**
         * <pre>
         *     ERROR - Empty or invalid property definition name
         *     ERROR - Invalid property type
         *     ERROR - Cannot have multiple default values when the property definition is single-valued
         *     ERROR - Default value is not valid for the property definition type
         *     ERROR - Duplicate default value
         *     ERROR - Cannot have explicit default values when default values is marked as a variant
         *     ERROR - Invalid value constraint
         *     ERROR - Duplicate value constraint
         *     ERROR - Cannot have explicit value constraints when value constraints is marked as a variant
         *     ERROR - Invalid query operator
         *     ERROR - Duplicate query operator
         *     ERROR - Cannot have explicit query operators when query operators is marked as a variant
         * </pre>
         */

        final MultiValidationStatus status = new MultiValidationStatus();
        String propertyDefinitionName = propertyDefinition.getName();

        if (Utils.isEmpty(propertyDefinitionName)) {
            propertyDefinitionName = Messages.missingName;
        }

        { // name
          // ERROR - Empty or invalid property definition name
            validateName(propertyDefinition, validNamespacePrefixes, existingPropertyNames, status);
        }

        { // property type
          // ERROR - Invalid property type
          // no validation needed since type is an enum
        }

        { // default values
            validateDefaultValues(propertyDefinition, validNamespacePrefixes, status);
        }

        { // value constraints
            validateValueConstraints(propertyDefinition, status);
        }

        { // query operators
            final String[] queryOperators = propertyDefinition.getAvailableQueryOperators();

            if (Utils.isEmpty(queryOperators)) {
                if (propertyDefinition.getState(PropertyDefinition.PropertyName.QUERY_OPS) == Value.IS) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.EMPTY_QUERY_OPERATORS,
                                                                   NLS.bind(Messages.emptyQueryOperators, propertyDefinitionName)));
                }
            } else {
                final Collection<String> operators = new ArrayList<String>(queryOperators.length);

                for (final String operator : queryOperators) {
                    // ERROR - Invalid query operator
                    validateQueryOperator(operator, propertyDefinitionName, status);

                    if (!Utils.isEmpty(operator)) {
                        // ERROR - Duplicate query operator
                        if (operators.contains(operator)) {
                            status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_QUERY_OPERATOR,
                                                                           NLS.bind(Messages.duplicateQueryOperator,
                                                                                    propertyDefinitionName,
                                                                                    operator)));
                        } else {
                            operators.add(operator);
                        }
                    }
                }

                // ERROR - Cannot have explicit query operators when query operators is marked as a variant
                if (propertyDefinition.getState(PropertyDefinition.PropertyName.QUERY_OPS) != Value.IS) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.QUERY_OPERATORS_EXIST_BUT_MARKED_AS_VARIANT,
                                                                   NLS.bind(Messages.queryOperatorsExistButMarkedAsVariant,
                                                                            propertyDefinition)));
                }
            }
        }

        return status;
    }

    /**
     * @param propertyDefinition the property definition being validated (never <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param existingPropertyNames the existing property names used to check for a duplicate (can be <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validatePropertyDefinition( final PropertyDefinition propertyDefinition,
                                                   final Collection<String> validNamespacePrefixes,
                                                   final Collection<QualifiedName> existingPropertyNames,
                                                   final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validatePropertyDefinition(propertyDefinition,
                                                                      validNamespacePrefixes,
                                                                      existingPropertyNames);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param nodeTypeName the node type name whose property definitions are being checked (cannot be <code>null</code> or empty)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param propertyDefinitions the collection of a node type definition's property definitions to validate (can be
     *        <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validatePropertyDefinitions( final String nodeTypeName,
                                                                     final Collection<String> validNamespacePrefixes,
                                                                     final Collection<PropertyDefinition> propertyDefinitions ) {
        Utils.verifyIsNotEmpty(nodeTypeName, "nodeTypeName"); //$NON-NLS-1$

        /**
         * <pre>
         *     ERROR - Duplicate property definition names
         * </pre>
         */

        // OK to have none
        if (Utils.isEmpty(propertyDefinitions)) {
            return MultiValidationStatus.OK_STATUS;
        }

        final MultiValidationStatus status = new MultiValidationStatus();
        final Collection<String> propNames = new ArrayList<String>(propertyDefinitions.size());

        for (final PropertyDefinition propertyDefn : propertyDefinitions) {
            validatePropertyDefinition(propertyDefn, validNamespacePrefixes, null, status);

            { // ERROR - Duplicate property definition names (allow duplicate residual names)
                final String propName = propertyDefn.getName();

                if (!Utils.isEmpty(propName) && !ItemDefinition.RESIDUAL_NAME.equals(propName)) {
                    if (propNames.contains(propName)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_PROPERTY_DEFINITION_NAME,
                                                                       NLS.bind(Messages.duplicatePropertyDefinitionName,
                                                                                nodeTypeName,
                                                                                propName)));
                    } else {
                        propNames.add(propName);
                    }
                }
            }
        }

        return status;
    }

    /**
     * @param nodeTypeName the node type name whose property definitions are being checked (cannot be <code>null</code> or empty)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param propertyDefinitions the collection of a node type definition's property definitions to validate (can be
     *        <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validatePropertyDefinitions( final String nodeTypeName,
                                                    final Collection<String> validNamespacePrefixes,
                                                    final Collection<PropertyDefinition> propertyDefinitions,
                                                    final MultiValidationStatus status ) {
        final MultiValidationStatus newStatus = validatePropertyDefinitions(nodeTypeName,
                                                                            validNamespacePrefixes,
                                                                            propertyDefinitions);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param qname the qualified name being validated (cannot be <code>null</code>)
     * @param propertyName the name to use to identify the qualified name (cannot be <code>null</code> empty)
     * @param validNamespacePrefixes a collection of namespace prefixes that the qualified name must match (can be
     *        <code>null</code> or empty)
     * @param existingQNames a list of existing qualified names used to make sure the name being validated is not a duplicate (can
     *        be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateQualifiedName( final QualifiedName qname,
                                                               final String propertyName,
                                                               final Collection<String> validNamespacePrefixes,
                                                               final Collection<QualifiedName> existingQNames ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateQualifiedName(qname, propertyName, validNamespacePrefixes, existingQNames, status);
        return status;
    }

    /**
     * @param qname the qualified name being validated (cannot be <code>null</code>)
     * @param propertyName the name to use to identify the qualified name (cannot be <code>null</code> empty)
     * @param validNamespacePrefixes a collection of namespace prefixes that the qualified name must match (can be
     *        <code>null</code> or empty)
     * @param existingQNames a list of existing qualified names used to make sure the name being validated is not a duplicate (can
     *        be <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateQualifiedName( final QualifiedName qname,
                                              final String propertyName,
                                              final Collection<String> validNamespacePrefixes,
                                              final Collection<QualifiedName> existingQNames,
                                              final MultiValidationStatus status ) {
        final String qualifier = qname.getQualifier();

        { // qualifier part
            if (!Utils.isEmpty(qualifier)) {
                final ValidationStatus qualifierStatus = validateLocalName(qualifier, propertyName);

                if (!qualifierStatus.isOk()) {
                    status.add(qualifierStatus);
                } else if (Utils.isEmpty(validNamespacePrefixes) || !validNamespacePrefixes.contains(qualifier)) {
                    final ValidationStatus newStatus = ValidationStatus.createErrorMessage(StatusCodes.NAME_QUALIFIER_NOT_FOUND,
                                                                                           NLS.bind(Messages.nameQualifierNotFound,
                                                                                                    propertyName,
                                                                                                    qualifier));
                    status.add(newStatus);
                }
            }
        }

        { // unqualified name part
            final String unqualifiedName = qname.getUnqualifiedName();

            if (Utils.isEmpty(unqualifiedName)) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.EMPTY_UNQUALIFIED_NAME,
                                                               NLS.bind(Messages.emptyUnqualifiedName, propertyName)));
            } else {
                final ValidationStatus nameStatus = validateLocalName(unqualifiedName, propertyName);

                if (!nameStatus.isOk()) {
                    status.add(nameStatus);
                }
            }
        }

        // make sure qname is not a duplicate
        if (!Utils.isEmpty(existingQNames) && existingQNames.contains(qname)) {
            final ValidationStatus newStatus = ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_QUALIFIED_NAME,
                                                                                   NLS.bind(Messages.duplicateQualifiedName,
                                                                                            propertyName,
                                                                                            qname));
            status.add(newStatus);
        }
    }

    /**
     * @param operator the query operator being validated (can be <code>null</code> or empty)
     * @param propertyDefinitionName the name of the property definition the query operator belongs to (cannot be
     *        <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static ValidationStatus validateQueryOperator( final String operator,
                                                          final String propertyDefinitionName ) {
        if (Utils.isEmpty(operator)) {
            return ValidationStatus.createErrorMessage(StatusCodes.EMPTY_QUERY_OPERATOR, Messages.emptyQueryOperator);
        }

        try {
            QueryOperator.find(operator);
        } catch (final Exception e) {
            return ValidationStatus.createErrorMessage(StatusCodes.INVALID_QUERY_OPERATOR,
                                                       NLS.bind(Messages.invalidQueryOperator, operator, propertyDefinitionName));
        }

        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param operator the query operator being validated (can be <code>null</code> or empty)
     * @param propertyDefinitionName the name of the property definition the query operator belongs to (cannot be
     *        <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateQueryOperator( final String operator,
                                              final String propertyDefinitionName,
                                              final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateQueryOperator(operator, propertyDefinitionName);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param childNodeDefinition the child node definition whose required types are being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateRequiredTypes( final ChildNodeDefinition childNodeDefinition,
                                                               final Collection<String> validNamespacePrefixes ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateRequiredTypes(childNodeDefinition, validNamespacePrefixes, status);
        return status;
    }

    /**
     * @param childNodeDefinition the child node definition whose required types are being validated (cannot be <code>null</code>)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateRequiredTypes( final ChildNodeDefinition childNodeDefinition,
                                              final Collection<String> validNamespacePrefixes,
                                              final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(childNodeDefinition, "childNodeDefinition"); //$NON-NLS-1$
        Utils.verifyIsNotNull(status, "status"); //$NON-NLS-1$

        final String[] requiredTypeNames = childNodeDefinition.getRequiredPrimaryTypeNames();
        String childNodeName = childNodeDefinition.getName();

        if (Utils.isEmpty(childNodeName)) {
            childNodeName = Messages.missingName;
        }

        if (Utils.isEmpty(requiredTypeNames)) {
            if (childNodeDefinition.getState(ChildNodeDefinition.PropertyName.REQUIRED_TYPES) == Value.IS) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.EMPTY_REQUIRED_TYPES,
                                                               NLS.bind(Messages.emptyRequiredTypes, childNodeName)));
            }
        } else {
            final Collection<QualifiedName> requiredTypes = new ArrayList<QualifiedName>(requiredTypeNames.length);

            for (final QualifiedName requiredType : childNodeDefinition.getRequiredTypes()) {
                // ERROR - Invalid required type name
                validateQualifiedName(requiredType, Messages.requiredTypeName, validNamespacePrefixes, null, status);

                // ERROR - Duplicate required type name
                if (requiredTypes.contains(requiredType)) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_REQUIRED_TYPE,
                                                                   NLS.bind(Messages.duplicateRequiredType,
                                                                            childNodeName,
                                                                            requiredType)));
                } else {
                    requiredTypes.add(requiredType);
                }
            }

            // ERROR - Cannot have explicit required types when required types is marked as a variant
            if (childNodeDefinition.getState(ChildNodeDefinition.PropertyName.REQUIRED_TYPES) != Value.IS) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.REQUIRED_TYPES_EXIST_BUT_MARKED_AS_VARIANT,
                                                               NLS.bind(Messages.requiredTypesExistButMarkedAsVariant,
                                                                        childNodeName)));
            }
        }
    }

    /**
     * @param nodeTypeDefinitionName the node type name whose supertypes are being checked (cannot be <code>null</code> or empty)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param superTypesState the supertypes property state (cannot be <code>null</code>)
     * @param superTypeNames the collection of a node type definition's supertype names to validate (can be <code>null</code> or
     *        empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateSuperTypes( final String nodeTypeDefinitionName,
                                                            final Collection<String> validNamespacePrefixes,
                                                            final Value superTypesState,
                                                            final Collection<QualifiedName> superTypeNames ) {
        Utils.verifyIsNotEmpty(nodeTypeDefinitionName, "nodeTypeDefinitionName"); //$NON-NLS-1$
        Utils.verifyIsNotNull(superTypesState, "superTypesState"); //$NON-NLS-1$

        /**
         * <pre>
         *     ERROR - Invalid super type name
         *     ERROR - Duplicate super type name
         *     ERROR - Cannot have explicit super types when super types is marked as a variant
         * </pre>
         */

        // OK to have none
        if (Utils.isEmpty(superTypeNames)) {
            return MultiValidationStatus.OK_STATUS;
        }

        final MultiValidationStatus status = new MultiValidationStatus();
        final Collection<QualifiedName> names = new ArrayList<QualifiedName>(superTypeNames.size());

        for (final QualifiedName superTypeName : superTypeNames) {
            // ERROR - Invalid super type name
            validateQualifiedName(superTypeName, Messages.superTypeName, validNamespacePrefixes, null, status);

            if (!Utils.isEmpty(superTypeName.get())) {
                // ERROR - Duplicate super type name
                if (names.contains(superTypeName)) {
                    status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_SUPER_TYPE,
                                                                   NLS.bind(Messages.duplicateSuperType,
                                                                            nodeTypeDefinitionName,
                                                                            superTypeName)));
                } else {
                    names.add(superTypeName);
                }
            }
        }

        // ERROR - Cannot have explicit super types when super types is marked as a variant
        if (superTypesState != Value.IS) {
            status.add(ValidationStatus.createErrorMessage(StatusCodes.SUPER_TYPES_EXIST_BUT_MARKED_AS_VARIANT,
                                                           NLS.bind(Messages.superTypesExistButMarkedAsVariant,
                                                                    nodeTypeDefinitionName)));
        }

        return status;
    }

    /**
     * @param nodeTypeDefinitionName the node type name whose supertypes are being checked (cannot be <code>null</code> or empty)
     * @param validNamespacePrefixes the valid namespace prefixes (can be <code>null</code> or empty)
     * @param superTypesState the supertypes property state (cannot be <code>null</code>)
     * @param superTypeNames the collection of a node type definition's supertype names to validate (can be <code>null</code> or
     *        empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateSuperTypes( final String nodeTypeDefinitionName,
                                           final Collection<String> validNamespacePrefixes,
                                           final Value superTypesState,
                                           final Collection<QualifiedName> superTypeNames,
                                           final MultiValidationStatus status ) {
        final MultiValidationStatus newStatus = validateSuperTypes(nodeTypeDefinitionName,
                                                                   validNamespacePrefixes,
                                                                   superTypesState,
                                                                   superTypeNames);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param uri the URI being checked (can be <code>null</code> or empty)
     * @param propertyName the name to use to identify the URI (cannot be <code>null</code> empty)
     * @return the status (never <code>null</code>)
     */
    public static ValidationStatus validateUri( final String uri,
                                                final String propertyName ) {
        Utils.verifyIsNotEmpty(propertyName, "propertyName"); //$NON-NLS-1$

        if (Utils.isEmpty(uri) || uri.contains(" ")) { //$NON-NLS-1$
            return ValidationStatus.createErrorMessage(StatusCodes.EMPTY_VALUE, NLS.bind(Messages.emptyValue, propertyName));
        }

        try {
            URI.create(uri);
        } catch (final Exception e) {
            return ValidationStatus.createErrorMessage(StatusCodes.INVALID_URI, NLS.bind(Messages.invalidUri, propertyName));
        }

        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param constraint the value constraint being validated (cannot be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static ValidationStatus validateValueConstraint( final String constraint ) {
        try {
            Utils.verifyIsNotEmpty(constraint, "constraint"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            return ValidationStatus.createErrorMessage(StatusCodes.EMPTY_VALUE_CONSTRAINT, Messages.emptyValueConstraint);
        }

        // TODO implement validateValueConstraint to make sure constraint is property syntax
        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param constraint the value constraint being validated (cannot be <code>null</code> or empty)
     * @param status the status to add the new status to (never <code>null</code>)
     */
    public static void validateValueConstraint( final String constraint,
                                                final MultiValidationStatus status ) {
        final ValidationStatus newStatus = validateValueConstraint(constraint);

        if (!newStatus.isOk()) {
            status.add(newStatus);
        }
    }

    /**
     * @param propertyDefinition the property definition whose value constraints are being validated (cannot be <code>null</code>)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateValueConstraints( final PropertyDefinition propertyDefinition ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateValueConstraints(propertyDefinition, status);
        return status;
    }

    /**
     * @param propertyDefinition the property definition whose value constraints are being validated (cannot be <code>null</code>)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateValueConstraints( final PropertyDefinition propertyDefinition,
                                                 final MultiValidationStatus status ) {
        Utils.verifyIsNotNull(propertyDefinition, "propertyDefinition"); //$NON-NLS-1$
        Utils.verifyIsNotNull(status, "status"); //$NON-NLS-1$

        String propertyName = propertyDefinition.getName();

        if (Utils.isEmpty(propertyName)) {
            propertyName = Messages.missingName;
        }

        final String[] valueConstraints = propertyDefinition.getValueConstraints();

        if (Utils.isEmpty(valueConstraints)) {
            if (propertyDefinition.getState(PropertyDefinition.PropertyName.VALUE_CONSTRAINTS) == Value.IS) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.EMPTY_VALUE_CONSTRAINTS,
                                                               NLS.bind(Messages.emptyValueConstraints, propertyName)));
            }
        } else {
            validateValueConstraints(propertyName, Arrays.asList(valueConstraints), status);

            // ERROR - Cannot have explicit value constraints when value constraints is marked as a variant
            if (propertyDefinition.getState(PropertyDefinition.PropertyName.VALUE_CONSTRAINTS) != Value.IS) {
                status.add(ValidationStatus.createErrorMessage(StatusCodes.VALUE_CONSTRAINTS_EXIST_BUT_MARKED_AS_VARIANT,
                                                               NLS.bind(Messages.valueConstraintsExistButMarkedAsVariant,
                                                                        propertyDefinition)));
            }
        }
    }

    /**
     * Checks for valid constraint and duplicates.
     *
     * @param propertyName the property definition name (can be <code>null</code> or empty)
     * @param valueConstraints the value constraints (can be <code>null</code> or empty)
     * @return the status (never <code>null</code>)
     */
    public static MultiValidationStatus validateValueConstraints( final String propertyName,
                                                                  final Collection<String> valueConstraints ) {
        final MultiValidationStatus status = new MultiValidationStatus();
        validateValueConstraints(propertyName, valueConstraints, status);
        return status;
    }

    /**
     * Checks for valid constraint and duplicates.
     *
     * @param propertyName the property definition name (can be <code>null</code> or empty)
     * @param valueConstraints the value constraints (can be <code>null</code> or empty)
     * @param status the status to add the new status to (cannot be <code>null</code>)
     */
    public static void validateValueConstraints( String propertyName,
                                                 final Collection<String> valueConstraints,
                                                 final MultiValidationStatus status ) {
        if (propertyName == null) {
            propertyName = Messages.missingName;
        }

        if (!Utils.isEmpty(valueConstraints)) {
            final Collection<String> constraints = new ArrayList<String>(valueConstraints.size());

            for (final String constraint : valueConstraints) {
                // ERROR - Invalid value constraint
                validateValueConstraint(constraint, status);

                if (!Utils.isEmpty(constraint)) {
                    // ERROR - Duplicate value constraint
                    if (constraints.contains(constraint)) {
                        status.add(ValidationStatus.createErrorMessage(StatusCodes.DUPLICATE_VALUE_CONSTRAINT,
                                                                       NLS.bind(Messages.duplicateValueConstraint,
                                                                                propertyName,
                                                                                constraint)));
                    } else {
                        constraints.add(constraint);
                    }
                }
            }
        }
    }

    /**
     * Don't allow construction.
     */
    private CndValidator() {
        // nothing to do
    }

    interface StatusCodes {
        int EMPTY_VALUE = 100;
        int INVALID_PROPERTY_VALUE_FOR_TYPE = 105;
        int ERROR_VALIDATING_PROPERTY_VALUE_FOR_TYPE = 110;
        int DUPLICATE_CHILD_NODE_DEFINITION_NAME = 115;
        int CND_HAS_NO_NAMESPACES_OR_NODE_TYPE_DEFINITIONS = 120;
        int DEFAULT_TYPE_DOES_NOT_MATCH_REQUIRED_TYPE = 125;
        int DEFAULT_TYPE_EXISTS_BUT_MARKED_AS_VARIANT = 130;
        int EMPTY_DEFAULT_VALUES = 135;
        int MULTIPLE_DEFAULT_VALUES_FOR_SINGLE_VALUED_PROPERTY = 140;
        int INVALID_QUALIFIER_FOR_DEFAULT_VALUE = 145;
        int DUPLICATE_DEFAULT_VALUE = 150;
        int DEFAULT_VALUES_EXIST_BUT_MARKED_AS_VARIANT = 155;
        int EMPTY_LOCAL_NAME = 160;
        int LOCAL_NAME_EQUAL_TO_SELF_OR_PARENT = 165;
        int LOCAL_NAME_HAS_INVALID_CHARACTERS = 170;
        int INVALID_URI_FOR_BUILT_IN_NAMESPACE_PREFIX = 175;
        int INVALID_PREFIX_FOR_BUILT_IN_NAMESPACE_URI = 180;
        int DUPLICATE_NAMESPACE_PREFIX = 185;
        int DUPLICATE_NAMESPACE_URI = 190;
        int EMPTY_SUPER_TYPES = 195;
        int PRIMARY_ITEM_EXISTS_BUT_MARKED_AS_VARIANT = 200;
        int DUPLICATE_NODE_TYPE_DEFINITION_NAME = 205;
        int EMPTY_QUERY_OPERATORS = 210;
        int DUPLICATE_QUERY_OPERATOR = 215;
        int QUERY_OPERATORS_EXIST_BUT_MARKED_AS_VARIANT = 220;
        int DUPLICATE_PROPERTY_DEFINITION_NAME = 225;
        int EMPTY_UNQUALIFIED_NAME = 230;
        int NAME_QUALIFIER_NOT_FOUND = 235;
        int DUPLICATE_QUALIFIED_NAME = 240;
        int EMPTY_QUERY_OPERATOR = 245;
        int INVALID_QUERY_OPERATOR = 250;
        int EMPTY_REQUIRED_TYPES = 255;
        int DUPLICATE_REQUIRED_TYPE = 260;
        int REQUIRED_TYPES_EXIST_BUT_MARKED_AS_VARIANT = 265;
        int DUPLICATE_SUPER_TYPE = 270;
        int SUPER_TYPES_EXIST_BUT_MARKED_AS_VARIANT = 275;
        int INVALID_URI = 280;
        int EMPTY_VALUE_CONSTRAINTS = 285;
        int DUPLICATE_VALUE_CONSTRAINT = 290;
        int VALUE_CONSTRAINTS_EXIST_BUT_MARKED_AS_VARIANT = 295;
        int EMPTY_VALUE_CONSTRAINT = 300;
    }

}
