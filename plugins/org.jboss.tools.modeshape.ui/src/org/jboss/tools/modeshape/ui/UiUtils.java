/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Common constants and methods used with JBoss Tool's ModeShape Tools project.
 */
public final class UiUtils {

    /**
     * An empty string constant.
     */
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * A delimiter used to join a collection of object string representations. Default value is "{@value} ".
     */
    public static final String DEFAULT_JOIN_DELIMITER = ","; //$NON-NLS-1$

    /**
     * The column will be packed using the header text.
     * 
     * @param viewerColumn the viewer column (cannot be <code>null</code>)
     * @param labelProvider the column label provider (cannot be <code>null</code>
     * @param headerText the header text (can be <code>null</code> or empty)
     * @param headerToolTip (can be <code>null</code> or empty)
     * @param moveable a flag indicating if the column can be moved
     * @param resizable a flag indicating if the column can be resized
     * @throws IllegalArgumentException if either the column or label provider is <code>null</code>
     */
    public static void configureColumn( final TableViewerColumn viewerColumn,
                                        final CellLabelProvider labelProvider,
                                        final String headerText,
                                        final String headerToolTip,
                                        final boolean moveable,
                                        final boolean resizable ) {
        verifyIsNotNull(viewerColumn, "viewerColumn"); //$NON-NLS-1$
        verifyIsNotNull(labelProvider, "labelProvider"); //$NON-NLS-1$

        viewerColumn.setLabelProvider(labelProvider);

        // configure column
        final TableColumn column = viewerColumn.getColumn();

        if (!isEmpty(headerText)) {
            column.setText(headerText);
        }

        column.setToolTipText(headerToolTip);
        column.setMoveable(false);
        column.setResizable(resizable);
        column.pack();
    }

    /**
     * @param control the control whose font will be used during calculation (cannot be <code>null</code>)
     * @param numChars the number of characters whose pixel width is being requested
     * @return the number of pixels corresponding to the width of the given number of characters
     */
    public static int convertWidthInCharsToPixels( Control control,
                                                   int numChars ) {
        verifyIsNotNull(control, "control"); //$NON-NLS-1$
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();
        return Dialog.convertWidthInCharsToPixels(fontMetrics, numChars);
    }

    /**
     * @param stringBeingChecked the string being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty( final String stringBeingChecked ) {
        return ((stringBeingChecked == null) || stringBeingChecked.isEmpty());
    }

    /**
     * An item within the items that are <code>null</code> are treated like an empty string. If a delimiter is not passed in the
     * {@link #DEFAULT_JOIN_DELIMITER default delimiter} is used.
     * 
     * @param items the items whose string representation are being joined (cannot be <code>null</code>)
     * @param delimiter the delimiter separating the items (can be <code>null</code> or empty)
     * @return the string representation of each item separated by the specified delimiter (never <code>null</code>)
     * @throws IllegalArgumentException if items is <code>null</code>
     */
    public static String join( Collection<?> items,
                               String delimiter ) {
        verifyIsNotNull(items, "items"); //$NON-NLS-1$

        delimiter = (((delimiter == null) || delimiter.isEmpty()) ? DEFAULT_JOIN_DELIMITER : delimiter);
        StringBuilder builder = new StringBuilder();

        for (Iterator<?> itr = items.iterator(); itr.hasNext();) {
            Object item = itr.next();

            if (item == null) {
                item = EMPTY_STRING;
            }

            builder.append(item.toString());

            // add delimiter
            if (itr.hasNext()) {
                builder.append(delimiter);
            }
        }

        return builder.toString();
    }

    /**
     * @param viewers the viewers whose columns will be packed (cannot be <code>null</code>)
     * @throws IllegalArgumentException if a viewer is <code>null</code>
     */
    public static void pack( final TableViewer... viewers ) {
        for (final TableViewer viewer : viewers) {
            verifyIsNotNull(viewer, "viewer"); //$NON-NLS-1$

            for (final TableColumn column : viewer.getTable().getColumns()) {
                column.pack();
            }
        }
    }

    /**
     * @param text the string being checked (can be <code>null</code> or empty)
     * @param name the name of the object to use in the error message (cannot be <code>null</code>)
     * @throws IllegalArgumentException if the text is <code>null</code> or empty
     */
    public static void verifyIsNotEmpty( final String text,
                                         String name ) {
        if (isEmpty(text)) {
            if ((name == null) || name.isEmpty()) {
                name = EMPTY_STRING;
            }

            throw new IllegalArgumentException(NLS.bind(UiMessages.stringIsEmpty, name));
        }
    }

    /**
     * @param object the object being checked (can be <code>null</code>)
     * @param name the name of the object to use in the error message (cannot be <code>null</code>)
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void verifyIsNotNull( final Object object,
                                        String name ) {
        if (object == null) {
            if ((name == null) || name.isEmpty()) {
                name = EMPTY_STRING;
            }

            throw new IllegalArgumentException(NLS.bind(UiMessages.objectIsNull, name));
        }
    }

    /**
     * Don't allow construction.
     */
    private UiUtils() {
        // nothing to do
    }
}
