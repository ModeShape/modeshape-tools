/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinition;
import org.jboss.tools.modeshape.ui.forms.ErrorMessage;

/**
 * The CND editor's form page(s) base class.
 */
public abstract class CndEditorPage extends FormPage implements IPersistableEditor {

    /**
     * The memento used to restore editor state (can be <code>null</code>).
     */
    protected IMemento memento;

    /**
     * @param cndEditor the CND editor (cannot be <code>null</code>)
     * @param id the unique page identifier (cannot be <code>null</code>)
     * @param title the page part title (cannot be <code>null</code>)
     */
    protected CndEditorPage( final CndEditor cndEditor,
                             final String id,
                             final String title ) {
        super(cndEditor, id, title);
    }

    /**
     * @param body the parent UI control where all other controls should be added (never <code>null</code>)
     * @param toolkit the form toolkit to use when creating form controls (never <code>null</code>)
     */
    protected abstract void createBody( Composite body,
                                        FormToolkit toolkit );

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @Override
    protected final void createFormContent( final IManagedForm managedForm ) {
        final Composite body = managedForm.getForm().getBody();
        body.setLayout(new GridLayout());
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createBody(body, managedForm.getToolkit());
        setResourceReadOnly(isReadonly());
        updateAllMessages();
    }

    /**
     * @param c the form control that will be revealed in the MED editor client area (cannot be <code>null</code>)
     */
    protected void ensureVisible( final Control c ) {
        FormToolkit.ensureVisible(c);
    }

    /**
     * @return the CND being modified by the GUI (never <code>null</code>)
     */
    protected CompactNodeTypeDefinition getCnd() {
        return getCndEditor().getCnd();
    }

    /**
     * @return the CND editor that this page belongs to (never <code>null</code>)
     */
    protected CndEditor getCndEditor() {
        return (CndEditor)getEditor();
    }

    /**
     * @return the resource being edited (never <code>null</code>)
     */
    protected IFile getFile() {
        return getCndEditor().getFile();
    }

    /**
     * @return the part site's shell (never <code>null</code>)
     */
    protected Shell getShell() {
        return getSite().getShell();
    }

    /**
     * The CND was changed outside of the editor. Page must reload CND content.
     */
    public abstract void handleCndReloaded();

    /**
     * @param e the property change event being handled (never <code>null</code>)
     */
    protected abstract void handlePropertyChanged( PropertyChangeEvent e );

    /**
     * @return <code>true</code> if the editor is readonly
     */
    protected boolean isReadonly() {
        return getCndEditor().isReadOnly();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IPersistableEditor#restoreState(org.eclipse.ui.IMemento)
     */
    @Override
    public final void restoreState( IMemento memento ) {
        this.memento = memento;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState( IMemento memento ) {
        // nothing to do. subclassses can override.
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#setFocus()
     */
    @Override
    public void setFocus() {
        super.setFocus();

        // check for null as this method gets called before controls are constructed
        if (getManagedForm() != null) {
            getManagedForm().refresh();
        }
    }

    /**
     * @param readOnly the new readonly state of the editor
     */
    protected abstract void setResourceReadOnly( boolean readOnly );

    /**
     * Updates/refreshes all page header error messages.
     */
    protected abstract void updateAllMessages();

    /**
     * @param errorMessage the message being updated in the {@link IMessageManager message manager} (never <code>null</code>)
     */
    protected void updateMessage( final ErrorMessage errorMessage ) {
        assert (errorMessage != null) : "errorMessage is null"; //$NON-NLS-1$
        final IMessageManager msgMgr = ((CndEditor)getEditor()).getMessageManager();

        if (errorMessage.isOk()) {
            if (errorMessage.getControl() == null) {
                msgMgr.removeMessage(errorMessage.getKey());
            } else {
                msgMgr.removeMessage(errorMessage.getKey(), errorMessage.getControl());
            }
        } else {
            if (errorMessage.getControl() == null) {
                msgMgr.addMessage(errorMessage.getKey(), errorMessage.getMessage(), errorMessage.getData(),
                                  errorMessage.getMessageType());
            } else {
                msgMgr.addMessage(errorMessage.getKey(), errorMessage.getMessage(), errorMessage.getData(),
                                  errorMessage.getMessageType(), errorMessage.getControl());
            }
        }
    }
}
