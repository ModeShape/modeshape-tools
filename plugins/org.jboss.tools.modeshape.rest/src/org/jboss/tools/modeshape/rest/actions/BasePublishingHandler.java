/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.jboss.tools.modeshape.rest.actions;

import static org.jboss.tools.modeshape.rest.IUiConstants.ModeShape_IMAGE_16x;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.jobs.PublishJob.Type;
import org.jboss.tools.modeshape.rest.wizards.PublishWizard;
import org.modeshape.web.jcr.rest.client.Status;
import org.modeshape.web.jcr.rest.client.Status.Severity;

/**
 * The <code>BasePublishingHandler</code> is a base class for the publishing and unpublishing handlers.
 */
abstract class BasePublishingHandler implements IHandler {

    /**
     * Indicates if this is a publishing or unpublishing handler.
     */
    private final Type type;

    /**
     * @param type indicates the type of handler
     */
    protected BasePublishingHandler( Type type ) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
     */
    @Override
    public void addHandlerListener( IHandlerListener handlerListener ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#dispose()
     */
    @Override
    public void dispose() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute( ExecutionEvent event ) {
        ISelection temp = HandlerUtil.getCurrentSelection(event);
        assert (temp != null);
        assert (!temp.isEmpty());
        assert (temp instanceof IStructuredSelection);

        IStructuredSelection selection = (IStructuredSelection)temp;
        List<IResource> resources;

        if (selection.size() == 1) {
            resources = Collections.singletonList((IResource)selection.getFirstElement());
        } else {
            resources = selection.toList();
        }

        // run wizard
        Shell shell = HandlerUtil.getActiveShell(event);

        try {
            WizardDialog dialog = new WizardDialog(shell, new PublishWizard(this.type, resources,
                                                                            Activator.getDefault().getServerManager())) {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
                 */
                @Override
                protected void initializeBounds() {
                    super.initializeBounds();
                    getShell().setImage(Activator.getDefault().getImage(ModeShape_IMAGE_16x));
                }
            };

            dialog.open();
        } catch (CoreException e) {
            String msg = null;

            if (this.type == Type.PUBLISH) {
                msg = RestClientI18n.basePublishingActionPublishingWizardErrorMsg;
            } else {
                msg = RestClientI18n.basePublishingActionUnpublishingWizardErrorMsg;
            }

            Activator.getDefault().log(new Status(Severity.ERROR, msg, e));
            MessageDialog.openError(shell, RestClientI18n.errorDialogTitle, msg);
        }

        // per javadoc must return null
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#isHandled()
     */
    @Override
    public boolean isHandled() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
     */
    @Override
    public void removeHandlerListener( IHandlerListener handlerListener ) {
        // nothing to do
    }

}
