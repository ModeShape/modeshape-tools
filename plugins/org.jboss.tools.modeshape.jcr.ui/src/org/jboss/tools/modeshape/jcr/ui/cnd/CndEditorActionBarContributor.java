package org.jboss.tools.modeshape.jcr.ui.cnd;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.jboss.tools.modeshape.jcr.ChildNodeDefinition;
import org.jboss.tools.modeshape.jcr.NodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.PropertyDefinition;
import org.jboss.tools.modeshape.ui.UiUtils;
import org.jboss.tools.modeshape.ui.actions.DisabledAction;

/**
 * Action contributor for the CND editor.
 */
public class CndEditorActionBarContributor extends EditorActionBarContributor {

    private final StatusField childNodeStatus;
    private final StatusField nodeTypeStatus;
    private final StatusField propertyStatus;
    private final StatusField readOnlyStatus;

    /**
     * Constructs the CND editor action contributor.
     */
    public CndEditorActionBarContributor() {
        this.readOnlyStatus = new StatusField(10, null, null);
        this.nodeTypeStatus = new StatusField(-1, null, CndMessages.statusBarNodeTypeToolTip);
        this.propertyStatus = new StatusField(-1, null, CndMessages.statusBarPropertyToolTip);
        this.childNodeStatus = new StatusField(-1, null, CndMessages.statusBarChildNodeToolTip);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToStatusLine(org.eclipse.jface.action.IStatusLineManager)
     */
    @Override
    public void contributeToStatusLine( final IStatusLineManager statusLineManager ) {
        statusLineManager.add(this.readOnlyStatus);
        statusLineManager.add(this.nodeTypeStatus);
        statusLineManager.add(this.propertyStatus);
        statusLineManager.add(this.childNodeStatus);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.part.EditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
     */
    @Override
    public void setActiveEditor( IEditorPart targetEditor ) {
        final IActionBars actionBars = getActionBars();

        // disable global actions
        if (actionBars != null) {
            actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), DisabledAction.get());
            actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), DisabledAction.get());
            actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), DisabledAction.get());
            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), DisabledAction.get());
            actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), DisabledAction.get());
        }
    }

    /**
     * @param selectedChildNode the selected child node definition or <code>null</code> of none selected
     */
    public void setChildNode( final ChildNodeDefinition selectedChildNode ) {
        this.childNodeStatus.setMessage((selectedChildNode == null) ? null : selectedChildNode.getName());
        getActionBars().getStatusLineManager().update(true);
    }

    /**
     * @param newDoubleClickAction the new double-click action for the selected child node definition status bar contribution menu
     *        (can be <code>null</code>)
     */
    public void setChildNodeStatusDoubleClickAction( final IAction newDoubleClickAction ) {
        this.childNodeStatus.setDoubleClickAction(newDoubleClickAction);
    }

    /**
     * @param newMenuManager the new menu manager to use to create the selected child node definition status bar contribution menu
     *        (can be <code>null</code>)
     */
    public void setChildNodeStatusMenuManager( final MenuManager newMenuManager ) {
        this.childNodeStatus.setMenuManager(newMenuManager);
    }

    /**
     * @param newEditableState <code>true</code> if editor is editable
     */
    public void setEditableStatus( final boolean newEditableState ) {
        final String newMessage = (newEditableState ? "Writable" : "Read-Only");
        this.readOnlyStatus.setMessage(newMessage);
        getActionBars().getStatusLineManager().update(true);
    }

    /**
     * @param selectedNodeType the selected node type definition or <code>null</code> of none selected
     */
    public void setNodeType( final NodeTypeDefinition selectedNodeType ) {
        this.nodeTypeStatus.setMessage((selectedNodeType == null) ? null : selectedNodeType.getName());
        getActionBars().getStatusLineManager().update(true);
    }

    /**
     * @param newDoubleClickAction the new double-click action for the selected node type definition status bar contribution menu
     *        (can be <code>null</code>)
     */
    public void setNodeTypeStatusDoubleClickAction( final IAction newDoubleClickAction ) {
        this.nodeTypeStatus.setDoubleClickAction(newDoubleClickAction);
    }

    /**
     * @param newMenuManager the new menu manager to use to create the selected node type definition status bar contribution menu
     *        (can be <code>null</code>)
     */
    public void setNodeTypeStatusMenuManager( final MenuManager newMenuManager ) {
        this.nodeTypeStatus.setMenuManager(newMenuManager);
    }

    /**
     * @param selectedProperty the selected property definition or <code>null</code> of none selected
     */
    public void setProperty( final PropertyDefinition selectedProperty ) {
        this.propertyStatus.setMessage((selectedProperty == null) ? null : selectedProperty.getName());
        getActionBars().getStatusLineManager().update(true);
    }

    /**
     * @param newDoubleClickAction the new double-click action for the selected property definition status bar contribution menu
     *        (can be <code>null</code>)
     */
    public void setPropertyStatusDoubleClickAction( final IAction newDoubleClickAction ) {
        this.propertyStatus.setDoubleClickAction(newDoubleClickAction);
    }

    /**
     * @param newMenuManager the new menu manager to use to create the selected property definition status bar contribution menu
     *        (can be <code>null</code>)
     */
    public void setPropertyStatusMenuManager( final MenuManager newMenuManager ) {
        this.propertyStatus.setMenuManager(newMenuManager);
    }

    /**
     * @param newMenuManager the new menu manager used to create the read-only status bar contribution menu (can be
     *        <code>null</code>)
     */
    public void setReadOnlyStatusMenuManager( final MenuManager newMenuManager ) {
        this.readOnlyStatus.setMenuManager(newMenuManager);
    }

    static class StatusField extends ContributionItem {

        private static final int DEFAULT_WIDTH = 25;

        private IAction doubleClickAction;
        private CLabel label;
        private MenuManager menuManager;
        private String message;
        private final String toolTip;
        private final int widthInChars;

        StatusField( final int widthInChars,
                     final String message,
                     final String toolTip ) {
            this.widthInChars = ((widthInChars < 1) ? DEFAULT_WIDTH : widthInChars);
            this.message = message;
            this.toolTip = toolTip;
        }

        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
         */
        @Override
        public void fill( final Composite parent ) {
            final GC gc = new GC(parent);
            gc.setFont(parent.getFont());

            final Label sep = new Label(parent, SWT.SEPARATOR);
            sep.setLayoutData(new StatusLineLayoutData());
            ((StatusLineLayoutData)sep.getLayoutData()).heightHint = gc.getFontMetrics().getHeight();

            this.label = new CLabel(parent, SWT.SHADOW_NONE);
            this.label.setLayoutData(new StatusLineLayoutData());
            ((StatusLineLayoutData)this.label.getLayoutData()).widthHint = ((gc.getFontMetrics().getAverageCharWidth() * this.widthInChars));

            gc.dispose();

            // create menu (cannot use same menu manager or menu)
            if (this.menuManager != null) {
                this.label.setMenu(this.menuManager.createContextMenu(this.label));
            }

            // add double-click listener
            if (this.doubleClickAction != null) {
                this.label.addMouseListener(new MouseAdapter() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
                     */
                    @Override
                    public void mouseDoubleClick( final MouseEvent e ) {
                        handleDoubleClick();
                    }
                });
            }

            // set current message and tooltip
            updateLabel();
        }

        void handleDoubleClick() {
            assert (this.doubleClickAction != null) : "double click action is null and action handler was called";
            this.doubleClickAction.run();
        }

        void setDoubleClickAction( final IAction doubleClickAction ) {
            this.doubleClickAction = doubleClickAction;
        }

        void setMenuManager( final MenuManager newMenuManager ) {
            this.menuManager = newMenuManager;
        }

        /**
         * @param newMessage the new message (can be <code>null</code> or empty)
         */
        void setMessage( final String newMessage ) {
            this.message = newMessage;
            updateLabel();
        }

        private void updateLabel() {
            if ((this.label != null) && !this.label.isDisposed()) {
                { // update label text
                    final String newText = (UiUtils.isEmpty(this.message) ? CndMessages.statusBarNoSelection : this.message);

                    if (!newText.equals(this.label.getText())) {
                        this.label.setText(newText);
                    }
                }

                { // update label tooltip
                    final String newToolTip = UiUtils.ensureNotNull(this.toolTip);

                    if (!newToolTip.equals(this.label.getToolTipText())) {
                        this.label.setToolTipText(newToolTip);
                    }
                }
            }
        }
    }
}
