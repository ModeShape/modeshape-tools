/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.jboss.tools.modeshape.rest.actions;

import org.jboss.tools.modeshape.rest.jobs.PublishJob.Type;

/**
 * The <code>PublishHandler</code> controls the publishing (uploading) of one or more {@link org.eclipse.core.resources.IResource}
 * s to a ModeShape repository.
 */
public final class PublishHandler extends BasePublishingHandler {

    /**
     * Constructs a handler for publishing (uploading) of files to a ModeShape repository.
     */
    public PublishHandler() {
        super(Type.PUBLISH);
    }
}
