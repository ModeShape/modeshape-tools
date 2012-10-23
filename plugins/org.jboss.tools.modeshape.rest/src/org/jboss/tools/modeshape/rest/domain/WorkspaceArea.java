package org.jboss.tools.modeshape.rest.domain;

import org.modeshape.common.annotation.Immutable;
import org.modeshape.common.util.CheckArg;

/**
 * The <code>WorkspaceArea</code> represents a known area in a ModeShape repository where sequencing of resources occurs.
 */
@Immutable
public final class WorkspaceArea implements ModeShapeDomainObject {

    /**
     * The path within the workspace where this area is found (never <code>null</code>).
     */
    private final String path;

    /**
     * An optional workspace area title (can be <code>null</code> or empty).
     */
    private final String title;

    /**
     * The workspace where this area is found (never <code>null</code>).
     */
    private final ModeShapeWorkspace workspace;

    /**
     * @param workspace the workspace where this area is found (never <code>null</code>)
     * @param path the workspace path where this area is found (never <code>null</code>)
     * @param title the workspace area title (can be <code>null</code> or empty)
     */
    public WorkspaceArea( ModeShapeWorkspace workspace,
                          String path,
                          String title ) {
        CheckArg.isNotNull(workspace, "workspace"); //$NON-NLS-1$
        CheckArg.isNotEmpty(path, "path"); //$NON-NLS-1$

        this.workspace = workspace;
        this.path = path;
        this.title = title;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject#getName()
     */
    @Override
    public String getName() {
        return getPath();
    }

    /**
     * @return path the workspace path where this area is found (never <code>null</code>)
     */
    public String getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject#getShortDescription()
     */
    @Override
    public String getShortDescription() {
        return getPath();
    }

    /**
     * @return title the workspace area title (can be <code>null</code> or empty)
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @return workspace the workspace where this area is found (never <code>null</code>)
     */
    public ModeShapeWorkspace getWorkspace() {
        return this.workspace;
    }

}
