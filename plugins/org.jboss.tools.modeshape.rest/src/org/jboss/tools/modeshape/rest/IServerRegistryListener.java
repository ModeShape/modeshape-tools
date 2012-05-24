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
package org.jboss.tools.modeshape.rest;

/**
 * The <code>IServerRegistryListener</code> interface defines the API for a server registry listener.
 */
public interface IServerRegistryListener {

    /**
     * @param event the event being processed (never <code>null</code>)
     * @return any errors caught during the processing or <code>null</code>
     */
    Exception[] serverRegistryChanged( ServerRegistryEvent event );

}
