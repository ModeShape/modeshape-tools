/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.jboss.tools.modeshape.jcr.cnd.CndImporter;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants.Images;
import org.jboss.tools.modeshape.ui.UiMessages;
import org.jboss.tools.modeshape.ui.forms.MessageFormDialog;
import org.jboss.tools.modeshape.ui.forms.MessageSummaryDialog;

/**
 * An editor for JCR compact node type definition files.
 */
public final class CndEditor extends SharedHeaderFormEditor implements IPersistableEditor, IResourceChangeListener,
        PropertyChangeListener {

    private CompactNodeTypeDefinition cndBeingEdited;

    private boolean dirty = false;
    private final FileDocumentProvider documentProvider = new FileDocumentProvider();

    private final CndEditorPage formsPage;

    private IMemento memento;
    private long modificationStamp = 0;
    private CompactNodeTypeDefinition originalCnd;

    private boolean readOnly = false;
    private Listener refreshListener;

    private ScrolledForm scrolledForm;

    /**
     * Constructs a CND editor.
     */
    public CndEditor() {
        this.formsPage = new CndFormsEditorPage(this);
    }

    /**
     * Allow inner classes access to this instance.
     *
     * @return this instance
     */
    CndEditor accessThis() {
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    @Override
    protected void addPages() {
        try {
            addPage(0, this.formsPage); // Page 0: Forms Editor Page

            final TextEditor sourceEditor = new TextEditor() {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#isEditable()
                 */
                @Override
                public boolean isEditable() {
                    return false;
                }
            };

            // add text editor and set editor tab title
            addPage(1, sourceEditor, getEditorInput());
            setPageText((getPageCount() - 1), CndMessages.cndEditorSourcePageTitle);

            // handle when CND changed outside of this editor
            this.refreshListener = new Listener() {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
                 */
                @Override
                public void handleEvent( final Event event ) {
                    refreshCnd();
                }
            };

            // hook activation listener
            getContainer().addListener(SWT.Activate, this.refreshListener);

            // restore state
            int selectedPageNum = 0;

            if (this.memento != null) {
                final int value = this.memento.getInteger(MementoKeys.SELECTED_PAGE);

                if (value != -1) {
                    selectedPageNum = value;
                }
            }

            setActivePage(selectedPageNum);
        } catch (final PartInitException e) {
            // this will open a "Could not open editor" page with exception details
            throw new RuntimeException(CndMessages.errorOpeningCndEditor, e);
        }
    }

    //
    // private void contributeToMenu( IMenuManager menuMgr ) {
    // menuMgr.add(this.updateRegisteryAction);
    // menuMgr.add(this.showRegistryViewAction);
    // menuMgr.update(true);
    // }
    //
    // private void contributeToToolBar( IToolBarManager toolBarMgr ) {
    // toolBarMgr.add(this.updateRegisteryAction);
    // toolBarMgr.add(this.showRegistryViewAction);
    // toolBarMgr.update(true);
    // }
    //
    // private void createActions() {
    // this.updateRegisteryAction = new Action(CndMessages.updateMedInRegistryActionText, SWT.FLAT) {
    // @Override
    // public void run() {
    // IEditorInput editorInput = getEditorInput();
    //
    // if (editorInput instanceof IFileEditorInput) {
    // IFile medFile = ((IFileEditorInput)editorInput).getFile();
    // RegistryDeploymentValidator.deploy(medFile);
    // }
    // }
    // };
    // this.updateRegisteryAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));
    // this.updateRegisteryAction.setToolTipText(CndMessages.updateMedInRegistryActionToolTip);
    //
    // this.showRegistryViewAction = new ShowModelExtensionRegistryViewAction();
    // }

    private void createCnd() throws Exception {
        final CndImporter importer = new CndImporter();
        final List<Throwable> errors = new ArrayList<Throwable>();
        this.originalCnd = importer.importFrom(getFile().getContents(), errors, getFile().getName());

        // check for parse errors
        if (!errors.isEmpty()) {
            final Throwable t = errors.iterator().next();

            if (t.getCause() == null) {
                throw new RuntimeException(t);
            }

            throw new RuntimeException(t.getCause());
        }

        // unhook lstening to current CND being edited
        if (this.cndBeingEdited != null) {
            this.cndBeingEdited.removeListener(this);
        }

        // copy over CND
        this.cndBeingEdited = CompactNodeTypeDefinition.copy(this.originalCnd);
        this.cndBeingEdited.addListener(this);
        this.formsPage.handleCndReloaded();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#createHeaderContents(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected void createHeaderContents( final IManagedForm headerForm ) {
        this.scrolledForm = headerForm.getForm();
        this.scrolledForm.setImage(Activator.getSharedInstance().getImage(JcrUiConstants.Images.CND_EDITOR));
        this.scrolledForm.setText(CndMessages.cndEditorTitle);

        final Form form = this.scrolledForm.getForm();
        getToolkit().decorateFormHeading(form);
        form.addMessageHyperlinkListener(new HyperlinkAdapter() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
             */
            @Override
            public void linkActivated( final HyperlinkEvent e ) {
                handleDisplayMessageDialog((IMessage[])e.data);
            }
        });

        form.getToolBarManager().add(new ControlContribution(null) {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
             */
            @Override
            protected Control createControl( final Composite parent ) {
                final Hyperlink hlink = getToolkit().createHyperlink(parent, CndMessages.openCndEditorPreferencesHyperlink,
                                                                     SWT.NULL);
                hlink.addHyperlinkListener(new HyperlinkAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
                     */
                    @Override
                    public void linkActivated( final HyperlinkEvent e ) {
                        handleOpenCndPreferencesPage();
                    }
                });
                return hlink;
            }
        });
        form.getToolBarManager().update(true);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#dispose()
     */
    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        internalSave(monitor);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        final IProgressMonitor progressMonitor = getProgressMonitor();
        final SaveAsDialog dialog = new SaveAsDialog(getShell());
        dialog.setOriginalFile(getFile());
        dialog.create();

        // dialog was canceled
        if (dialog.open() == Window.CANCEL) {
            if (progressMonitor != null) {
                progressMonitor.setCanceled(true);
            }

            return;
        }

        // dialog OK'd
        IPath filePath = dialog.getResult();

        // make sure that file has the right extension
        if (!JcrUiConstants.FileExtensions.CND.equals(filePath.getFileExtension())) {
            filePath = filePath.addFileExtension(JcrUiConstants.FileExtensions.CND);
        }

        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IFile file = workspace.getRoot().getFile(filePath);

        try {
            // create set new editor input file
            final InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
            file.create(emptyStream, true, progressMonitor);
            setInput(new FileEditorInput(file));

            // save CND in new file
            internalSave(progressMonitor);
        } catch (final Exception e) {
            IStatus status = null;

            if (!(e instanceof CoreException)) {
                status = new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID, e.getLocalizedMessage());
            } else {
                status = ((CoreException)e).getStatus();
            }

            ErrorDialog.openError(getShell(), UiMessages.errorDialogTitle, CndMessages.cndEditorSaveError, status);
        }
    }

    /**
     * @return the CND being edited (never <code>null</code>)
     */
    CompactNodeTypeDefinition getCnd() {
        return this.cndBeingEdited;
    }

    /**
     * @return the *.cnd resource (never <code>null</code>)
     */
    protected IFile getFile() {
        return ((IFileEditorInput)getEditorInput()).getFile();
    }

    /**
     * @return the form editor's message manager (never <code>null</code>)
     */
    IMessageManager getMessageManager() {
        return this.scrolledForm.getMessageManager();
    }

    private NotationType getNotationTypePreference() {
        final String notationTypeString = JcrPreferenceStore.get().get(JcrPreferenceConstants.CndPreference.NOTATION_TYPE);
        return NotationType.valueOf(notationTypeString);
    }

    private IProgressMonitor getProgressMonitor() {
        final IStatusLineManager statusLineMgr = getEditorSite().getActionBars().getStatusLineManager();
        return ((statusLineMgr == null) ? null : statusLineMgr.getProgressMonitor());
    }

    /**
     * @return the editor's shell (never <code>null</code>)
     */
    Shell getShell() {
        return getEditorSite().getShell();
    }

    void handleDisplayMessageDialog( final IMessage[] data ) {
        // configure message and message type
        int numErrors = 0;
        int numWarnings = 0;
        int numInfos = 0;
        int messageType = IMessageProvider.ERROR;

        for (final IMessage message : data) {
            if (message.getMessageType() == IMessageProvider.ERROR) {
                ++numErrors;
            } else if (message.getMessageType() == IMessageProvider.WARNING) {
                ++numWarnings;
            } else if (message.getMessageType() == IMessageProvider.INFORMATION) {
                ++numInfos;
            }
        }

        if (numErrors == 0) {
            if (numWarnings != 0) {
                messageType = IMessageProvider.WARNING;
            } else if (numInfos != 0) {
                messageType = IMessageProvider.INFORMATION;
            } else {
                messageType = IMessageProvider.NONE;
            }
        }

        final String message = NLS.bind(CndMessages.cndMessageDialogMessageAreaMessage, new Object[] { getFile().getName(),
                numErrors, numWarnings, numInfos });

        // show dialog
        final FormDialog dialog = new MessageSummaryDialog(getShell(),
                                                           CndMessages.cndMessageDialogTitle,
                                                           CndMessages.cndMessageDialogMessageAreaTitle,
                                                           message,
                                                           messageType,
                                                           data);
        dialog.create();
        dialog.getShell().pack();
        dialog.open();
    }

    void handleOpenCndPreferencesPage() {
        PreferencesUtil.createPreferenceDialogOn(getShell(), JcrUiConstants.PreferenceIds.CND_PREFERENCE_PAGE,
                                                 new String[] { JcrUiConstants.PreferenceIds.CND_PREFERENCE_PAGE }, null).open();
    }

    /**
     * Registers an editor activation listener.
     */
    private void hookRefreshListener() {
        getContainer().addListener(SWT.Activate, this.refreshListener);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) throws PartInitException {
        super.init(site, input);
        assert (input instanceof IFileEditorInput) : "MED Editor input is not a file"; //$NON-NLS-1$

        try {
            createCnd();
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
            JcrPreferenceStore.get();
        } catch (final Exception e) {
            throw new PartInitException(CndMessages.errorOpeningCndEditor, e);
        }
    }

    private void internalSave( final IProgressMonitor progressMonitor ) {
        final IEditorInput input = getEditorInput();

        try {
            // create the new text document
            saveDocument(input, progressMonitor);

            // create new original CND that that will then be copied over to the CND being edited
            createCnd();
        } catch (final Exception e) {
            IStatus status = null;

            if (!(e instanceof CoreException)) {
                status = new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID, e.getLocalizedMessage());
            } else {
                status = ((CoreException)e).getStatus();
            }

            if ((status == null) || (status.getSeverity() != IStatus.CANCEL)) {
                ErrorDialog.openError(getShell(), UiMessages.errorDialogTitle, CndMessages.cndEditorSaveError, status);
            }
        } finally {
            this.documentProvider.changed(input);

            // update dirty flag
            refreshDirtyState();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#isDirty()
     */
    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * @return <code>true</code> if the file is readonly
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /**
     * @return <code>true</code> if editor is synchronized with file system
     */
    boolean isSynchronized() {
        final long currentModifiedStamp = this.documentProvider.getModificationStamp(getEditorInput());
        return (this.modificationStamp == currentModifiedStamp);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange( final PropertyChangeEvent e ) {
        refreshDirtyState();
        this.formsPage.handlePropertyChanged(e);
    }

    void refreshCnd() {
        if (!isSynchronized()) {
            unhookRefreshListener();

            if (MessageFormDialog.openQuestion(getShell(), CndMessages.cndChangedOnFileSystemDialogTitle,
                                               Activator.getSharedInstance().getImage(Images.CND_EDITOR),
                                               NLS.bind(CndMessages.cndChangedOnFileSystemDialogMsg, getFile().getName()))) {
                try {
                    getFile().refreshLocal(IResource.DEPTH_ONE, null);
                    this.modificationStamp = this.documentProvider.getModificationStamp(getEditorInput());

                    createCnd();
                } catch (final Exception e) {
                    Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID, null, e));
                    MessageFormDialog.openError(getShell(), CndMessages.cndEditorRefreshErrorTitle,
                                                Activator.getSharedInstance().getImage(Images.CND_EDITOR),
                                                CndMessages.cndEditorRefreshErrorMsg);
                }
            }

            hookRefreshListener();
        }

        refreshReadOnlyState();
    }

    /**
     * Refreshes the editor's dirty state by comparing the MED being edited with the original MED.
     */
    protected void refreshDirtyState() {
        final boolean newValue = !this.originalCnd.equals(this.cndBeingEdited);

        if (isDirty() != newValue) {
            this.dirty = newValue;
            getHeaderForm().dirtyStateChanged();
        }
    }

    /**
     * Checks the *.mxd file permissions and notifies the editor's pages if the permissions have changed.
     */
    private void refreshReadOnlyState() {
        final ResourceAttributes attributes = getFile().getResourceAttributes();
        final boolean newValue = ((attributes == null) ? true : attributes.isReadOnly());

        if (isReadOnly() != newValue) {
            this.readOnly = newValue;

            this.formsPage.setResourceReadOnly(this.readOnly);
            this.formsPage.getManagedForm().refresh();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    @Override
    public void resourceChanged( final IResourceChangeEvent event ) {
        final int type = event.getType();

        if (type == IResourceChangeEvent.POST_CHANGE) {
            final IResourceDelta delta = event.getDelta();

            if (delta == null) {
                return;
            }

            try {
                delta.accept(new IResourceDeltaVisitor() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
                     */
                    @Override
                    public boolean visit( final IResourceDelta delta ) {
                        if (delta.getResource().equals(getFile())) {
                            // MXD file has been deleted so close editor
                            if ((delta.getKind() & IResourceDelta.REMOVED) != 0) {
                                if (!getShell().isDisposed()) {
                                    getShell().getDisplay().asyncExec(new Runnable() {

                                        /**
                                         * {@inheritDoc}
                                         *
                                         * @see java.lang.Runnable#run()
                                         */
                                        @Override
                                        public void run() {
                                            getEditorSite().getPage().closeEditor(accessThis(), false);
                                        }
                                    });
                                }
                            } else if ((delta.getKind() == IResourceDelta.CHANGED)
                                    && ((delta.getFlags() & org.eclipse.core.resources.IResourceDelta.CONTENT) != 0)) {
                                if (!getShell().isDisposed()) {
                                    getShell().getDisplay().asyncExec(new Runnable() {

                                        /**
                                         * {@inheritDoc}
                                         *
                                         * @see java.lang.Runnable#run()
                                         */
                                        @Override
                                        public void run() {
                                            refreshCnd();
                                        }
                                    });
                                }
                            }

                            return false; // stop visiting
                        }

                        return true; // keep visiting
                    }
                });
            } catch (final Exception e) {
                Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID, null, e));
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.IPersistableEditor#restoreState(org.eclipse.ui.IMemento)
     */
    @Override
    public void restoreState( final IMemento memento ) {
        this.memento = memento;
        this.formsPage.restoreState(this.memento);
    }

    private void saveDocument( final IEditorInput input,
                               final IProgressMonitor progressMonitor ) throws Exception {
        final IDocument document = this.documentProvider.getDocument(input);
        document.set(getCnd().toCndNotation(getNotationTypePreference()));
        this.documentProvider.aboutToChange(input);
        this.documentProvider.saveDocument(progressMonitor, input, document, true);
        this.modificationStamp = this.documentProvider.getModificationStamp(input);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState( final IMemento memento ) {
        final int selectedPageNum = getActivePage();
        memento.putInteger(MementoKeys.SELECTED_PAGE, selectedPageNum);
        this.formsPage.saveState(memento);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.forms.editor.SharedHeaderFormEditor#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();
        refreshCnd();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    protected void setInput( final IEditorInput input ) {
        if (getEditorInput() != null) {
            // unhook previous document provider
            this.documentProvider.disconnect(getEditorInput());
        }

        if (input instanceof IFileEditorInput) {
            super.setInput(input);

            try {
                // hook new document provider
                this.documentProvider.connect(input);
                this.modificationStamp = this.documentProvider.getModificationStamp(input);
                final IAnnotationModel model = this.documentProvider.getAnnotationModel(input);
                model.connect(this.documentProvider.getDocument(input));

                // set editor tab text
                setPartName(getEditorInput().getName());
            } catch (final Exception e) {
                throw new RuntimeException(CndMessages.errorOpeningCndEditor, e);
            }
        } else {
            throw new RuntimeException(CndMessages.cndEditorInputNotAFile);
        }
    }

    /**
     * Unregisters the editor activation listener.
     */
    private void unhookRefreshListener() {
        if (!getContainer().isDisposed()) {
            getContainer().removeListener(SWT.Activate, this.refreshListener);
        }
    }

    /**
     * The memento keys for saving and restoring editor state.
     */
    private interface MementoKeys {
        String SELECTED_PAGE = "SELECTED_PAGE"; //$NON-NLS-1$
    }
}
