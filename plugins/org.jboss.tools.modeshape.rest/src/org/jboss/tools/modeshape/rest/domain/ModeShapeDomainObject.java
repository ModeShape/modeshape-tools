/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.rest.domain;

/**
 * The <code>ModeShapeDomainObject</code> class is used to wrap object types coming from the ModeShape server. This is done
 * to add more functionality to each type. One example is that these wrapper objects know how to display their properties
 * in Eclipse.
 */
public interface ModeShapeDomainObject {

    /**
     * @return the name of the object (never <code>null</code>)
     */
    String getName();

    /**
     * @return a short description of the object that is typically used as a tooltip (can be <code>null</code> or empty)
     */
    String getShortDescription();

}
