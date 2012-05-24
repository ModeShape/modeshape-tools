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
 * The <code>UnpublishHandler</code> controls the unpublishing (removing) of one or more
 * {@link org.eclipse.core.resources.IResource}s from a ModeShape repository.
 */
public final class UnpublishHandler extends BasePublishingHandler {

    /**
     * Constructs a handler for unpublishing (removing) of files from a ModeShape repository.
     */
    public UnpublishHandler() {
        super(Type.UNPUBLISH);
    }

}
