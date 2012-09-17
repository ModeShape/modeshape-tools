/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.forms;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Helper methods for constructing form widgets.
 */
public final class FormUtils {

    /**
     * @param managedForm the managed form that will contain the new section (cannot be <code>null</code>)
     * @param toolkit the toolkit used to create the form objects (cannot be <code>null</code>)
     * @param parent the parent container for the new section (cannot be <code>null</code>)
     * @param title the section title (cannot be <code>null</code>)
     * @param description the section description (cannot be <code>null</code> if the {@link Section description style} is used)
     * @param style the section style
     * @param shouldGiveUpVerticalSpaceWhenFolded indicates if vertical space should be lost when section is folded
     * @return the new section (never <code>null</code>)
     */
    public static Section createSection( final IManagedForm managedForm,
                                         final FormToolkit toolkit,
                                         final Composite parent,
                                         final String title,
                                         final String description,
                                         final int style,
                                         final boolean shouldGiveUpVerticalSpaceWhenFolded ) {
        final Section section = toolkit.createSection(parent, style);
        section.setText(title);

        if ((style & Section.DESCRIPTION) != 0) {
            section.setDescription(description);
            section.getDescriptionControl().setFont(JFaceResources.getBannerFont());
        }

        section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                                           ((style & ExpandableComposite.EXPANDED) == ExpandableComposite.EXPANDED)));

        managedForm.addPart(new SectionPart(section));

        if (shouldGiveUpVerticalSpaceWhenFolded) {
            final ExpansionAdapter handler = new ExpansionAdapter() {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.ui.forms.events.ExpansionAdapter#expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
                 */
                @Override
                public void expansionStateChanged( final ExpansionEvent e ) {
                    final GridData gridData = (GridData)section.getLayoutData();
                    gridData.grabExcessVerticalSpace = e.getState();
                    managedForm.reflow(true);
                }
            };
            section.addExpansionListener(handler);
        }

        return section;
    }

    /**
     * @param section the section whose toolbar is being created (cannot <code>null</code>)
     * @param toolkit the toolkit used to create the form objects (cannot be <code>null</code>)
     * @param actions the actions used to create the toolbar buttons from (cannot be <code>null</code> or empty)
     * @return the toolbar manager of the new toolbar (never <code>null</code>)
     */
    public static IToolBarManager createSectionToolBar( final Section section,
                                                        final FormToolkit toolkit,
                                                        final IAction[] actions ) {
        final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        final ToolBar toolBar = toolBarManager.createControl(section);
        toolkit.adapt(toolBar);
        final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
        toolBar.setCursor(handCursor);
        section.setTextClient(toolBar);

        // Cursor needs to be explicitly disposed
        toolBar.addDisposeListener(new DisposeListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            @Override
            public void widgetDisposed( final DisposeEvent e ) {
                if (!handCursor.isDisposed()) {
                    handCursor.dispose();
                }
            }
        });

        for (final IAction action : actions) {
            toolBarManager.add(action);
        }

        toolBarManager.update(true);
        return toolBarManager;
    }

    /**
     * Table is created with header and lines visible using the default {@link Styles#VIEWER_STYLE style}.
     *
     * @param toolkit the toolkit used to create the form objects (cannot be <code>null</code>)
     * @param parent the parent container for the new section (cannot be <code>null</code>)
     * @return the table created by the toolkit (never <code>null</code>)
     */
    public static Table createTable( final FormToolkit toolkit,
                                     final Composite parent ) {
        final Table table = toolkit.createTable(parent, Styles.VIEWER_STYLE);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return table;
    }

    /**
     * @param parent the parent whose toolbar is being created (cannot <code>null</code>)
     * @param toolkit the toolkit used to create the form objects (cannot be <code>null</code>)
     * @param actions the actions used to create the toolbar buttons from (cannot be <code>null</code> or empty)
     * @param separatorIndexes the toolbar indexes to put separators (can be <code>null</code> or empty)
     * @return the toolbar manager of the new toolbar (never <code>null</code>)
     */
    public static IToolBarManager createToolBar( final Composite parent,
                                                 final FormToolkit toolkit,
                                                 final IAction[] actions,
                                                 final int... separatorIndexes ) {
        final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
        final ToolBar toolBar = toolBarManager.createControl(parent);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
        toolkit.adapt(toolBar);
        final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
        toolBar.setCursor(handCursor);

        // Cursor needs to be explicitly disposed
        toolBar.addDisposeListener(new DisposeListener() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            @Override
            public void widgetDisposed( final DisposeEvent e ) {
                if (!handCursor.isDisposed()) {
                    handCursor.dispose();
                }
            }
        });

        final boolean hasSeparators = ((separatorIndexes != null) && (separatorIndexes.length != 0));
        int i = 0;

        for (final IAction action : actions) {
            if (hasSeparators) {
                final int[] indexes = separatorIndexes; // gets rid of possible null pointer compiler warning

                for (final int separatorIndex : indexes) {
                    if (separatorIndex == i) {
                        toolBarManager.add(new Separator());
                        ++i;
                        break;
                    }
                }
            }

            toolBarManager.add(action);
            ++i;
        }

        toolBarManager.update(true);
        return toolBarManager;
    }

    /**
     * Don't allow construction.
     */
    private FormUtils() {
        // nothing to do
    }

    /**
     * Default widget styles.
     */
    public interface Styles {

        /**
         * A default style for combo boxes.
         */
        int COMBO_STYLE = SWT.FLAT | SWT.READ_ONLY | SWT.BORDER;

        /**
         * A default style for sections.
         */
        int SECTION_STYLE = Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
                            | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED;

        /**
         * A default style for text fields.
         */
        int TEXT_STYLE = SWT.BORDER;

        /**
         * A default style for viewers.
         */
        int VIEWER_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER;
    }
}
