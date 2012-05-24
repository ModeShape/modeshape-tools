/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.viewers;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * Draws a native-looking checkbox.
 */
public abstract class CheckBoxLabelProvider extends ColumnLabelProvider {

    private static final String CHECKED_KEY = "CHECKED"; //$NON-NLS-1$

    private static final String UNCHECK_KEY = "UNCHECKED"; //$NON-NLS-1$

    /**
     * @param viewer the viewer installing this label provider (cannot be <code>null</code>)
     */
    public CheckBoxLabelProvider( final ColumnViewer viewer ) {
        UiUtils.verifyIsNotNull(viewer, "viewer"); //$NON-NLS-1$

        if (JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY) == null) {
            JFaceResources.getImageRegistry().put(UNCHECK_KEY, createImage(viewer.getControl().getShell(), false));
            JFaceResources.getImageRegistry().put(CHECKED_KEY, createImage(viewer.getControl().getShell(), true));
        }
    }

    private Image createImage( final Shell shell,
                               final boolean type ) {
        final Shell s = new Shell(shell, SWT.NO_TRIM);
        final Button b = new Button(s, SWT.CHECK);
        b.setSelection(type);

        final Point bsize = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        b.setSize(bsize);
        b.setLocation(0, 0);
        s.setSize(bsize);
        s.open();

        final GC gc = new GC(b);
        final Image image = new Image(shell.getDisplay(), bsize.x, bsize.y);
        gc.copyArea(image, 0, 0);
        gc.dispose();

        s.close();

        return image;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage( final Object element ) {
        if (isChecked(element)) {
            return JFaceResources.getImageRegistry().getDescriptor(CHECKED_KEY).createImage();
        }

        return JFaceResources.getImageRegistry().getDescriptor(UNCHECK_KEY).createImage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText( Object element ) {
        return UiUtils.EMPTY_STRING;
    }
    
    /**
     * @param element the element being displayed in the viewer (cannot be <code>null</code>)
     * @return <code>true</code> if a checked checkbox image should be returned
     */
    protected abstract boolean isChecked( Object element );
}
