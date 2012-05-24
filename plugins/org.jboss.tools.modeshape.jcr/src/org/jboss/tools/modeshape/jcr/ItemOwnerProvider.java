/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

/**
 * Provides information about an item's owner.
 */
public interface ItemOwnerProvider {

    /**
     * @return the qualified name of the owner (never <code>null</code>)
     */
    QualifiedName getOwnerQualifiedName();
}
