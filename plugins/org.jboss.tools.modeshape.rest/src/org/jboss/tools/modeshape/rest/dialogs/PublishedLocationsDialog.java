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
package org.jboss.tools.modeshape.rest.dialogs;

import static org.jboss.tools.modeshape.rest.IUiConstants.BLANK_IMAGE;
import static org.jboss.tools.modeshape.rest.IUiConstants.ModeShape_IMAGE_16x;
import static org.jboss.tools.modeshape.rest.IUiConstants.REPOSITORY_IMAGE;
import static org.jboss.tools.modeshape.rest.IUiConstants.SERVER_IMAGE;
import static org.jboss.tools.modeshape.rest.IUiConstants.WORKSPACE_IMAGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.PublishedResourceHelper.WorkspaceLocation;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.modeshape.common.util.CheckArg;

/**
 * The <code>PublishedLocationsDialog</code> class provides a UI for viewing a list of {@link ModeShapeServer servers} a selected file has
 * been published to.
 */
public final class PublishedLocationsDialog extends MessageDialog {

    /**
     * The column index of the server URL.
     */
    private static final int SERVER_URL_COL = 0;

    /**
     * The column index of the server user.
     */
    private static final int USER_COL = 1;

    /**
     * The column index of the repository name.
     */
    private static final int REPOSITORY_COL = 2;

    /**
     * The column index of the workspace name.
     */
    private static final int WORKSPACE_COL = 3;

    /**
     * The column index of the URL where the file was published.
     */
    private static final int FILE_URL_COL = 4;

    /**
     * The column indexes of all columns.
     */
    private static final int[] COLUMNS = {SERVER_URL_COL, USER_COL, REPOSITORY_COL, WORKSPACE_COL, FILE_URL_COL};

    /**
     * The column headers.
     */
    private static final String[] HEADERS = {RestClientI18n.publishedLocationsDialogServerUrlColumnHeader,
        RestClientI18n.publishedLocationsDialogUserColumnHeader,
        RestClientI18n.publishedLocationsDialogRepositoryColumnHeader,
        RestClientI18n.publishedLocationsDialogWorkspaceColumnHeader,
        RestClientI18n.publishedLocationsDialogFileUrlColumnHeader};

    /**
     * The button that copies the file URL to the clipboard.
     */
    private Button btnCopy;

    /**
     * The viewer of the table holding the published locations.
     */
    private TableViewer viewer;

    /**
     * Collection of workspace locations the selected file has been published to.
     */
    private final Collection<WorkspaceLocation> workspaceLocations;

    /**
     * @param parentShell the dialog parent
     * @param file the file whose workspaces it has been published on is being requested (never <code>null</code>)
     * @param workspaceLocations the workspace locations (never <code>null</code>)
     */
    public PublishedLocationsDialog( Shell parentShell,
                                     IFile file,
                                     Collection<WorkspaceLocation> workspaceLocations ) {
        super(parentShell, RestClientI18n.publishedLocationsDialogTitle,
              Activator.getDefault().getImage(ModeShape_IMAGE_16x),
              NLS.bind(RestClientI18n.publishedLocationsDialogMsg, file.getFullPath()), MessageDialog.INFORMATION,
              new String[] {IDialogConstants.OK_LABEL}, 0);

        CheckArg.isNotNull(workspaceLocations, "workspaceLocations"); //$NON-NLS-1$
        this.workspaceLocations = workspaceLocations;

        // make sure dialog is resizable
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        // layout consists of a panel that contains a table and a button
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(2, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        //
        // construct table
        //

        this.viewer = new TableViewer(panel, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        LocationsTableProvider provider = new LocationsTableProvider();
        this.viewer.setLabelProvider(provider);
        this.viewer.setContentProvider(provider);
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent e ) {
                handleTableSelection();
            }
        });

        // configure table
        Table table = this.viewer.getTable();
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        // set height of table
        ((GridData)table.getLayoutData()).minimumHeight = (table.getItemHeight() * 3);
        int locations = this.workspaceLocations.size() + 2;
        int numRows = (locations < 6) ? locations : Math.min(locations, 12);
        ((GridData)table.getLayoutData()).heightHint = (table.getItemHeight() * numRows);

        // create columns
        for (int numCols = COLUMNS.length, i = 0; i < numCols; ++i) {
            TableColumn column = new TableColumn(table, SWT.LEFT);
            column.setText(HEADERS[i]);

            // set image
            Image image = null;

            if ((i == SERVER_URL_COL) || (i == USER_COL)) {
                image = Activator.getDefault().getImage(SERVER_IMAGE);
            } else if (i == REPOSITORY_COL) {
                image = Activator.getDefault().getImage(REPOSITORY_IMAGE);
            } else if (i == WORKSPACE_COL) {
                image = Activator.getDefault().getImage(WORKSPACE_IMAGE);
            } else {
                image = Activator.getDefault().getImage(BLANK_IMAGE);
            }

            column.setImage(image);
        }

        // populate the table
        this.viewer.setInput(this);

        // size columns
        for (TableColumn column : table.getColumns()) {
            column.pack();
            column.setWidth(column.getWidth() + 10);
        }

        //
        // construct button
        //

        this.btnCopy = new Button(panel, SWT.PUSH);
        this.btnCopy.setText(RestClientI18n.publishedLocationsDialogCopyUrlButton);
        this.btnCopy.setToolTipText(RestClientI18n.publishedLocationsDialogCopyUrlButtonToolTip);
        this.btnCopy.setEnabled(false);
        this.btnCopy.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleCopyUrl();
            }
        });

        return panel;
    }

    /**
     * @param workspaceLocation the workspace location where the file was published
     * @return the URL where the file was published
     */
    String getPublishedAtUrl( WorkspaceLocation workspaceLocation ) {
        return workspaceLocation.getUrl();
    }

    /**
     * @return the workspaces the file has been published to (never <code>null</code>)
     */
    Object[] getWorkspaceLocations() {
        return this.workspaceLocations.toArray();
    }

    /**
     * Handler for when the copy URL button is clicked.
     */
    void handleCopyUrl() {
        WorkspaceLocation workspaceLocation = (WorkspaceLocation)((IStructuredSelection)this.viewer.getSelection()).getFirstElement();
        String url = getPublishedAtUrl(workspaceLocation);
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        clipboard.setContents(new Object[] {url}, new Transfer[] {TextTransfer.getInstance()});
    }

    /**
     * Handler for when a table row is selected.
     */
    void handleTableSelection() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        boolean enable = (selection.size() == 1);

        if (this.btnCopy.getEnabled() != enable) {
            this.btnCopy.setEnabled(enable);
        }
    }

    /**
     * The <code>LocationsTableProvider</code> provides content, labels, and images for the table.
     */
    class LocationsTableProvider implements IStructuredContentProvider, ITableLabelProvider {
        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void addListener( ILabelProviderListener listener ) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        @Override
        public void dispose() {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        @Override
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        @Override
        public String getColumnText( Object element,
                                     int columnIndex ) {
            WorkspaceLocation workspaceLocation = (WorkspaceLocation)element;

            if (columnIndex == SERVER_URL_COL) {
                return workspaceLocation.getServer().getUrl();
            }

            if (columnIndex == USER_COL) {
                return workspaceLocation.getServer().getUser();
            }

            if (columnIndex == REPOSITORY_COL) {
                return workspaceLocation.getRepository().getName();
            }

            if (columnIndex == WORKSPACE_COL) {
                return workspaceLocation.getWorkspace().getName();
            }

            if (columnIndex == FILE_URL_COL) {
                return getPublishedAtUrl(workspaceLocation);
            }

            // should never get here
            assert false;
            return ""; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        @Override
        public Object[] getElements( Object inputElement ) {
            return getWorkspaceLocations();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        @Override
        public void inputChanged( Viewer viewer,
                                  Object oldInput,
                                  Object newInput ) {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
         */
        @Override
        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        @Override
        public void removeListener( ILabelProviderListener listener ) {
            // nothing to do
        }
    }

}
