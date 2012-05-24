/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;

/**
 * The <code>DelegateAction</code> class is an action that requires a delegate action. It can be used to override behavior of the
 * delegate. One use would be to have the delegate not to provide any text itself. The delegate action could be used for a toolbar
 * action which does not need text. At the same time, the delegate action could be used in this class. This class could provide text
 * so that the same action could be used as a menu item.
 */
public class DelegateAction extends Action {

    /**
     * The delegate action (never <code>null</code>).
     */
    private final IAction delegate;
    
    private final boolean styleOverridden;

    /**
     * @param delegate the delegate action (cannot be <code>null</code>)
     */
    public DelegateAction( final IAction delegate ) {
        this.delegate = delegate;
        this.styleOverridden = false;
    }

    /**
     * Overrides the delegate action's text.
     * 
     * @param text the action text (can be <code>null</code> or empty)
     * @param delegate the action delegate (cannot be <code>null</code>)
     */
    public DelegateAction( final String text,
                           final IAction delegate ) {
        super(text);
        this.delegate = delegate;
        this.styleOverridden = false;
    }

    /**
     * Overrides the delegate action's text and style.
     * 
     * @param text the action text (can be <code>null</code> or empty)
     * @param style the action style
     * @param delegate the action delegate (cannot be <code>null</code>)
     */
    public DelegateAction( final String text,
                           final int style,
                           final IAction delegate ) {
        super(text, style);
        this.delegate = delegate;
        this.styleOverridden = true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#addPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
     */
    @Override
    public void addPropertyChangeListener( final IPropertyChangeListener listener ) {
        this.delegate.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getAccelerator()
     */
    @Override
    public int getAccelerator() {
        if (super.getAccelerator() == 0) {
            return this.delegate.getAccelerator();
        }

        return super.getAccelerator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getActionDefinitionId()
     */
    @Override
    public String getActionDefinitionId() {
        if (super.getActionDefinitionId() == null) {
            return this.delegate.getActionDefinitionId();
        }

        return super.getActionDefinitionId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getDescription()
     */
    @Override
    public String getDescription() {
        if (super.getDescription() == null) {
            return this.delegate.getDescription();
        }

        return super.getDescription();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getDisabledImageDescriptor()
     */
    @Override
    public ImageDescriptor getDisabledImageDescriptor() {
        if (super.getDisabledImageDescriptor() == null) {
            return this.delegate.getDisabledImageDescriptor();
        }

        return super.getDisabledImageDescriptor();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getHelpListener()
     */
    @Override
    public HelpListener getHelpListener() {
        if (super.getHelpListener() == null) {
            return this.delegate.getHelpListener();
        }

        return super.getHelpListener();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getHoverImageDescriptor()
     */
    @Override
    public ImageDescriptor getHoverImageDescriptor() {
        if (super.getHoverImageDescriptor() == null) {
            return this.delegate.getHoverImageDescriptor();
        }

        return super.getHoverImageDescriptor();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getId()
     */
    @Override
    public String getId() {
        if (super.getId() == null) {
            return this.delegate.getId();
        }

        return super.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        if (super.getImageDescriptor() == null) {
            return this.delegate.getImageDescriptor();
        }

        return super.getImageDescriptor();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getMenuCreator()
     */
    @Override
    public IMenuCreator getMenuCreator() {
        if (super.getMenuCreator() == null) {
            return this.delegate.getMenuCreator();
        }

        return super.getMenuCreator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getStyle()
     */
    @Override
    public int getStyle() {
        if (this.styleOverridden) {
            return super.getStyle();
        }

        return this.delegate.getStyle();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#getToolTipText()
     */
    @Override
    public String getToolTipText() {
        if (super.getToolTipText() == null) {
            return this.delegate.getToolTipText();
        }

        return super.getToolTipText();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.action.Action#isChecked()
     */
    @Override
    public boolean isChecked() {
        if (this.styleOverridden) {
            return super.isChecked();
        }

        return this.delegate.isChecked();
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#removePropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
     */
    @Override
    public void removePropertyChangeListener( final IPropertyChangeListener listener ) {
        this.delegate.removePropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        this.delegate.run();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#runWithEvent(org.eclipse.swt.widgets.Event)
     */
    @Override
    public void runWithEvent( final Event event ) {
        this.delegate.runWithEvent(event);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.IAction#setEnabled(boolean)
     */
    @Override
    public void setEnabled( final boolean enabled ) {
        this.delegate.setEnabled(enabled);
    }
}
