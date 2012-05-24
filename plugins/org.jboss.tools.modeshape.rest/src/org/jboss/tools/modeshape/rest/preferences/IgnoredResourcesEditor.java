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
package org.jboss.tools.modeshape.rest.preferences;

import static org.jboss.tools.modeshape.rest.IUiConstants.Preferences.IGNORED_RESOURCES_PREFERENCE;
import static org.jboss.tools.modeshape.rest.RestClientI18n.ignoredResourcesPreferencePageLabel;
import static org.jboss.tools.modeshape.rest.RestClientI18n.newIgnoredResourceDialogLabel;
import static org.jboss.tools.modeshape.rest.RestClientI18n.newIgnoredResourceDialogTitle;

import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;

/**
 * The <code>IgnoredResourcesEditor</code> is an editor for managing a set of ignored resources.
 */
public final class IgnoredResourcesEditor extends FieldEditor {

    private Button btnAdd;

    private Button btnRemove;

    /**
     * The data model (never <code>null</code>).
     */
    private IgnoredResourcesModel model;

    private TableViewer viewer;

    /**
     * @param parent the parent control
     */
    public IgnoredResourcesEditor( Composite parent ) {
        super(IGNORED_RESOURCES_PREFERENCE, ignoredResourcesPreferencePageLabel, parent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
     */
    @Override
    protected void adjustForNumColumns( int numColumns ) {
        Control control = getLabelControl();
        ((GridData)control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData)this.viewer.getControl().getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Creates a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
     * @return the button (never <code>null</code>)
     */
    private Button createPushButton( Composite parent,
                                     String key ) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(JFaceResources.getString(key));
        button.setFont(parent.getFont());
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
        data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        button.setLayoutData(data);
        return button;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
     */
    @Override
    protected void doFillIntoGrid( Composite parent,
                                   int numColumns ) {
        // create table
        this.viewer = new TableViewer(parent, (SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER));

        this.viewer.setContentProvider(new IStructuredContentProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                return getPatterns().toArray();
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
        });

        // sort the table rows by resource name pattern
        this.viewer.setComparator(new ViewerComparator() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public int compare( Viewer viewer,
                                Object e1,
                                Object e2 ) {
                ResourcePattern pattern1 = (ResourcePattern)e1;
                ResourcePattern pattern2 = (ResourcePattern)e2;

                return pattern1.getPattern().compareTo(pattern2.getPattern());
            }
        });

        this.viewer.setLabelProvider(new LabelProvider() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            @Override
            public String getText( Object element ) {
                return ((ResourcePattern)element).getPattern();
            }
        });

        Table table = this.viewer.getTable();
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        table.addListener(SWT.Selection, new Listener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
             */
            @Override
            public void handleEvent( Event event ) {
                if (event.detail == SWT.CHECK) {
                    handlePatternChecked((TableItem)event.item);
                } else {
                    handlePatternSelected();
                }
            }
        });

        // create buttons
        Composite pnlButtons = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        pnlButtons.setLayout(layout);
        pnlButtons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        this.btnAdd = createPushButton(pnlButtons, "ListEditor.add");//$NON-NLS-1$
        this.btnAdd.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAddPressed();
            }
        });

        this.btnRemove = createPushButton(pnlButtons, "ListEditor.remove");//$NON-NLS-1$
        this.btnRemove.setEnabled(false);
        this.btnRemove.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRemovePressed();
            }
        });

        this.model = new IgnoredResourcesModel(); // model must be created before setInput is called
        this.viewer.setInput(this); // doesn't matter what you pass in
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doLoad()
     */
    @Override
    protected void doLoad() {
        // load model from current preference value and refresh UI
        String prefValue = getPreferenceStore().getString(getPreferenceName());
        this.model.load(prefValue);
        refreshUi();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
     */
    @Override
    protected void doLoadDefault() {
        // load model from default preference value and refresh UI
        String prefValue = getPreferenceStore().getDefaultString(getPreferenceName());
        this.model.load(prefValue);
        refreshUi();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#doStore()
     */
    @Override
    protected void doStore() {
        getPreferenceStore().setValue(getPreferenceName(), this.model.createList());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
     */
    @Override
    public int getNumberOfControls() {
        return 2;
    }

    Collection<ResourcePattern> getPatterns() {
        return this.model.getPatterns();
    }

    /**
     * Handler for when add button is pressed.
     */
    void handleAddPressed() {
        // display dialog to get new pattern from user
        NewPatternDialog dialog = new NewPatternDialog(this.btnAdd.getShell(), getPatterns());

        if (dialog.open() == Window.OK) {
            // update model and UI
            this.model.addPattern(new ResourcePattern(dialog.getNewPattern(), true));
            refreshUi();
        }
    }

    /**
     * @param item the item whose checked state has been changed (may not be <code>null</code>)
     */
    void handlePatternChecked( TableItem item ) {
        ResourcePattern pattern = (ResourcePattern)item.getData();
        pattern.setEnabled(item.getChecked());
    }

    /**
     * Handler for when pattern is selected.
     */
    void handlePatternSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        boolean enable = !selection.isEmpty();

        if (this.btnRemove.getEnabled() != enable) {
            this.btnRemove.setEnabled(enable);
        }
    }

    /**
     * Handler for when the remove button is pressed.
     */
    void handleRemovePressed() {
        assert (!this.viewer.getSelection().isEmpty());
        Object pattern = ((IStructuredSelection)this.viewer.getSelection()).getFirstElement();
        this.model.removePattern((ResourcePattern)pattern);
        refreshUi();
    }

    /**
     * Updates the check state of each table item to reflect the appropriate value in the model.
     */
    private void initializeCheckBoxStates() {
        for (TableItem item : this.viewer.getTable().getItems()) {
            ResourcePattern pattern = (ResourcePattern)item.getData();

            if (pattern.isEnabled()) {
                item.setChecked(true);
            }
        }
    }

    /**
     * Refreshes the viewer.
     */
    private void refreshUi() {
        this.viewer.refresh();
        initializeCheckBoxStates();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.FieldEditor#setFocus()
     */
    @Override
    public void setFocus() {
        this.viewer.getControl().setFocus();
    }

    /**
     * A <code>NewPatternDialog</code> allows the user to enter a pattern.
     */
    class NewPatternDialog extends Dialog implements ModifyListener {

        /**
         * The existing ignored resource patterns.
         */
        private final Collection<ResourcePattern> existingPatterns;

        /**
         * A message for the user.
         */
        private CLabel lblMessage;

        /**
         * The contents of the new pattern text field.
         */
        private String newPattern;

        /**
         * @param parentShell the parent shell (can be <code>null</code>)
         * @param existingPatterns the existing patterns (can be <code>null</code> or empty)
         */
        public NewPatternDialog( Shell parentShell,
                                 Collection<ResourcePattern> existingPatterns ) {
            super(parentShell);
            setShellStyle(getShellStyle() | SWT.RESIZE);
            this.existingPatterns = existingPatterns;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
         */
        @Override
        protected void configureShell( Shell newShell ) {
            newShell.setText(newIgnoredResourceDialogTitle);
            super.configureShell(newShell);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
         */
        @Override
        protected Button createButton( Composite parent,
                                       int id,
                                       String label,
                                       boolean defaultButton ) {
            Button button = super.createButton(parent, id, label, defaultButton);

            // disable OK button initially
            if (id == OK) {
                button.setEnabled(false);
            }

            return button;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite panel = (Composite)super.createDialogArea(parent);
            Composite pnlEditor = new Composite(panel, SWT.NONE);
            pnlEditor.setLayout(new GridLayout());
            pnlEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Label label = new Label(pnlEditor, SWT.NONE);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
            label.setText(newIgnoredResourceDialogLabel);

            Text textField = new Text(pnlEditor, SWT.BORDER);
            textField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            textField.addModifyListener(this);
            textField.addVerifyListener(new VerifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
                 */
                @Override
                public void verifyText( VerifyEvent e ) {
                    // don't allow slashes as input
                    if (e.text.contains("\\") || e.text.contains("/")) { //$NON-NLS-1$ //$NON-NLS-2$
                        e.doit = false;
                    }
                }
            });

            // add image and message labels
            this.lblMessage = new CLabel(pnlEditor, SWT.NONE);
            this.lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            ((GridData)this.lblMessage.getLayoutData()).horizontalSpan = 2;

            return panel;
        }

        /**
         * @return the new pattern name or <code>null</code> if the dialog was canceled
         */
        public String getNewPattern() {
            if (getReturnCode() == OK) {
                return this.newPattern;
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
         */
        @Override
        protected void initializeBounds() {
            super.initializeBounds();

            // resize shell to be twice the width needed for the title (without this the title maybe cropped)
            int width = (4 * convertWidthInCharsToPixels(newIgnoredResourceDialogTitle.length()));
            Rectangle rectangle = getShell().getBounds();
            getShell().setBounds(rectangle.x, rectangle.y, width, rectangle.height);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         */
        @Override
        public void modifyText( ModifyEvent event ) {
            // clear message
            this.lblMessage.setImage(null);
            this.lblMessage.setText(""); //$NON-NLS-1$

            // enable/disable OK button
            this.newPattern = ((Text)event.widget).getText();

            // make sure at least one character entered
            boolean enable = (this.newPattern.length() != 0);

            // make sure value is not a disallowed value
            if (enable && (this.existingPatterns != null)) {
                for (ResourcePattern pattern : this.existingPatterns) {
                    if (this.newPattern.equals(pattern.getPattern())) {
                        enable = false;
                        this.lblMessage.setImage(Activator.getDefault().getSharedImage(ISharedImages.IMG_OBJS_INFO_TSK));
                        this.lblMessage.setText(RestClientI18n.newItemDialogValueExists);
                        break;
                    }
                }
            }

            // set enabled state if different than current state
            if (getButton(OK).getEnabled() != enable) {
                getButton(OK).setEnabled(enable);
            }
        }

    }

}
