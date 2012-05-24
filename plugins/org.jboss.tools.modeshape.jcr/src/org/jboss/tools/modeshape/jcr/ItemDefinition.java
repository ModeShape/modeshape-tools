/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import org.jboss.tools.modeshape.jcr.cnd.CommentedCndElement;

/**
 * An interface for property definitions and child node definitions.
 */
public interface ItemDefinition extends CommentedCndElement, Comparable<ItemDefinition> {

    /**
     * Defines a residual set of child items.
     */
    String RESIDUAL_NAME = "*"; //$NON-NLS-1$

    /**
     * @return the name of the node type definition where this item definition is defined (never <code>null</code>)
     */
    QualifiedName getDeclaringNodeTypeDefinitionName();

    /**
     * @return the qualified name (never <code>null</code>)
     */
    QualifiedName getQualifiedName();
}
