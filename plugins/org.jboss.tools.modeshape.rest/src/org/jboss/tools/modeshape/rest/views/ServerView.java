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
package org.jboss.tools.modeshape.rest.views;

import static org.jboss.tools.modeshape.rest.IUiConstants.COLLAPSE_ALL_IMAGE;
import static org.jboss.tools.modeshape.rest.IUiConstants.HelpContexts.SERVER_VIEW_HELP_CONTEXT;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.IServerRegistryListener;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.ServerRegistryEvent;
import org.jboss.tools.modeshape.rest.actions.AddPublishAreaAction;
import org.jboss.tools.modeshape.rest.actions.DeletePublishAreaAction;
import org.jboss.tools.modeshape.rest.actions.DeleteServerAction;
import org.jboss.tools.modeshape.rest.actions.EditServerAction;
import org.jboss.tools.modeshape.rest.actions.NewServerAction;
import org.jboss.tools.modeshape.rest.actions.ReconnectToServerAction;
import org.jboss.tools.modeshape.rest.domain.ModeShapeDomainObject;

/**
 * The <code>ServerView</code> shows all defined servers and their repositories.
 */
public final class ServerView extends ViewPart implements IServerRegistryListener {

    /**
     * Collapses all tree nodes.
     */
    private IAction collapseAllAction;

    /**
     * Adds a publish area to a workspace.
     */
    private BaseSelectionListenerAction addPublisAreaAction;

    /**
     * Deletes a server.
     */
    private BaseSelectionListenerAction deleteAction;

    /**
     * Deletes a publish area from a workspace.
     */
    private BaseSelectionListenerAction deletePublisAreaAction;

    /**
     * Edits a server's properties.
     */
    private BaseSelectionListenerAction editAction;

    /**
     * Creates a new server.
     */
    private Action newAction;

    /**
     * The viewer's content and label provider.
     */
    private ModeShapeContentProvider provider;

    /**
     * Refreshes the server connections.
     */
    private ReconnectToServerAction reconnectAction;

    private TreeViewer viewer;

    private void constructActions() {
        // the collapse all action is always enabled
        this.collapseAllAction = new Action() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                getViewer().collapseAll();
            }
        };

        this.collapseAllAction.setToolTipText(RestClientI18n.collapseActionToolTip);
        this.collapseAllAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(COLLAPSE_ALL_IMAGE));

        // the reconnect action tries to ping a selected server
        this.reconnectAction = new ReconnectToServerAction(this.viewer);
        this.viewer.addSelectionChangedListener(this.reconnectAction);

        // the shell used for dialogs that the actions display
        Shell shell = this.getSite().getShell();

        // the delete action will delete one or more servers
        this.deleteAction = new DeleteServerAction(shell, getServerManager());
        this.viewer.addSelectionChangedListener(this.deleteAction);

        // the edit action is only enabled when one server is selected
        this.editAction = new EditServerAction(shell, getServerManager());
        this.viewer.addSelectionChangedListener(this.editAction);

        // the new server action is always enabled
        this.newAction = new NewServerAction(shell, getServerManager());

        // the add publish area action is only enabled when a workspace is selected
        this.addPublisAreaAction = new AddPublishAreaAction(shell, getServerManager(), this.viewer);
        this.viewer.addSelectionChangedListener(this.addPublisAreaAction);

        // the delete publish area action is only enabled when a workspace area is selected
        this.deletePublisAreaAction = new DeletePublishAreaAction(shell, getServerManager(), this.viewer);
        this.viewer.addSelectionChangedListener(this.deletePublisAreaAction);
    }

    private void constructContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.add(this.newAction);
        menuMgr.add(this.editAction);
        menuMgr.add(this.deleteAction);
        menuMgr.add(this.reconnectAction);
        menuMgr.add(new Separator());
        menuMgr.add(this.addPublisAreaAction);
        menuMgr.add(this.deletePublisAreaAction);

        Menu menu = menuMgr.createContextMenu(this.viewer.getTree());
        this.viewer.getTree().setMenu(menu);
        getSite().registerContextMenu(menuMgr, this.viewer);
    }

    private void constructToolBar() {
        IToolBarManager toolBar = getViewSite().getActionBars().getToolBarManager();
        toolBar.add(this.newAction);
        toolBar.add(this.editAction);
        toolBar.add(this.deleteAction);
        toolBar.add(this.reconnectAction);
        toolBar.add(new Separator());
        toolBar.add(this.addPublisAreaAction);
        toolBar.add(this.deletePublisAreaAction);
        toolBar.add(new Separator());
        toolBar.add(this.collapseAllAction);
    }

    /**
     * @param parent the viewer's parent
     */
    private void constructTreeViewer( Composite parent ) {
        this.viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
        this.viewer.setUseHashlookup(true);
        this.provider = new ModeShapeContentProvider();
        this.provider.setViewer(this.viewer);

        this.viewer.setContentProvider(this.provider);
        ILabelDecorator decorator = Activator.getDefault().getWorkbench().getDecoratorManager().getLabelDecorator();
        this.viewer.setLabelProvider(new DecoratingLabelProvider(this.provider, decorator));
        ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleSelectionChanged(event);
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
             */
            @Override
            public void doubleClick( DoubleClickEvent arg0 ) {
                handleDoubleClick();
            }
        });

        // need to call this to populate tree
        this.viewer.setInput(getServerManager());

        // need to do this so that properties page will work
        getSite().setSelectionProvider(this.viewer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        constructTreeViewer(parent);
        constructActions();
        constructToolBar();
        constructContextMenu();
        hookGlobalActions();

        setTitleToolTip(RestClientI18n.serverViewToolTip);

        // register to receive changes to the server registry
        getServerManager().addRegistryListener(this);
        getServerManager().addRegistryListener(this.provider);

        // register with the help system
        IWorkbenchHelpSystem helpSystem = Activator.getDefault().getWorkbench().getHelpSystem();
        helpSystem.setHelp(parent, SERVER_VIEW_HELP_CONTEXT);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        getServerManager().removeRegistryListener(this);

        if (this.provider != null) {
            getServerManager().removeRegistryListener(this.provider);
        }

        super.dispose();
    }

    IAction getDeleteAction() {
        return this.deleteAction;
    }

    /**
     * @return the server manager being used by this view
     */
    private ServerManager getServerManager() {
        return Activator.getDefault().getServerManager();
    }

    /**
     * @return the tree viewer (can be <code>null</code> if viewer was not constructed)
     */
    public TreeViewer getViewer() {
        return this.viewer;
    }

    /**
     * Opens a dialog to edit server properties.
     */
    void handleDoubleClick() {
        if (this.editAction.isEnabled()) {
            this.editAction.run();
        }
    }

    /**
     * @param event the event being processed
     */
    void handleSelectionChanged( SelectionChangedEvent event ) {
        assert (((IStructuredSelection)event.getSelection()).getFirstElement() instanceof ModeShapeDomainObject);
        updateStatusLine((IStructuredSelection)event.getSelection());
    }

    /**
     * Sets global action handlers.
     */
    private void hookGlobalActions() {
        IActionBars bars = getViewSite().getActionBars();

        // hook delete server action up
        bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), this.deleteAction);
        this.viewer.getControl().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent event ) {
                if ((event.character == SWT.DEL) && (event.stateMask == 0) && getDeleteAction().isEnabled()) {
                    getDeleteAction().run();
                }
            }
        });

        // don't want cut, copy, or paste actions so hook them up with a disabled action
        class NoOpAction extends Action {
            NoOpAction() {
                setEnabled(false);
            }
        }

        IAction noop = new NoOpAction();
        bars.setGlobalActionHandler(ActionFactory.CUT.getId(), noop);
        bars.setGlobalActionHandler(ActionFactory.COPY.getId(), noop);
        bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), noop);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.jboss.tools.modeshape.rest.IServerRegistryListener#serverRegistryChanged(org.jboss.tools.modeshape.rest.ServerRegistryEvent)
     */
    @Override
    public Exception[] serverRegistryChanged( ServerRegistryEvent event ) {
        if (event.isNew() || event.isUpdate()) {
            this.viewer.refresh();
        } else {
            this.viewer.remove(event.getServer());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if (!this.viewer.getControl().isDisposed()) {
            this.viewer.getControl().setFocus();
        }
    }

    /**
     * @param selection the current viewer selection (never <code>null</code>)
     */
    private void updateStatusLine( IStructuredSelection selection ) {
        String msg = (selection.size() != 1 ? "" : ((ModeShapeDomainObject)selection.getFirstElement()).getShortDescription()); //$NON-NLS-1$
        getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
    }

}
