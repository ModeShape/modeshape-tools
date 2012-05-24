/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.ui.forms;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessage;
import org.jboss.tools.modeshape.ui.UiUtils;

/**
 * An error message that can be used by forms with header message areas.
 */
public class ErrorMessage implements IMessage {

    /**
     * Used to hold additional information (can be <code>null</code>).
     */
    private Object data;

    /**
     * The error message (can be <code>null</code> or empty)
     */
    private String message;

    /**
     * The message type.
     */
    private int messageType = IMessageProvider.NONE;

    /**
     * The UI control where the error can be fixed.
     */
    private Control widget;

    /**
     * Clears the error message.
     */
    public void clearMessage() {
        setErrorMessage(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getControl()
     */
    @Override
    public Control getControl() {
        return this.widget;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getData()
     */
    @Override
    public Object getData() {
        return this.data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getKey()
     */
    @Override
    public Object getKey() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IMessageProvider#getMessage()
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.IMessageProvider#getMessageType()
     */
    @Override
    public int getMessageType() {
        return this.messageType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.IMessage#getPrefix()
     */
    @Override
    public String getPrefix() {
        return null;
    }

    /**
     * @return <code>true</code> if the validation status has an error severity
     */
    public boolean isError() {
        return (this.messageType == IMessageProvider.ERROR);
    }

    /**
     * @return <code>true</code> if the validation status has an information severity
     */
    public boolean isInfo() {
        return (this.messageType == IMessageProvider.INFORMATION);
    }

    /**
     * @return <code>true</code> if the validation status has an OK severity
     */
    public boolean isOk() {
        return (this.messageType == IMessageProvider.NONE);
    }

    /**
     * @return <code>true</code> if the validation status has a warning severity
     */
    public boolean isWarning() {
        return (this.messageType == IMessageProvider.WARNING);
    }

    /**
     * @param newControl the new control (can be <code>null</code>)
     */
    public void setControl( final Control newControl ) {
        this.widget = newControl;
    }

    /**
     * @param data any additional information needed by the error message (can be <code>null</code>)
     */
    public void setData( Object data ) {
        this.data = data;
    }

    /**
     * Also sets the message severity to an error.
     * 
     * @param newMessage the new message (can be <code>null</code> or empty)
     */
    public void setErrorMessage( final String newMessage ) {
        this.message = newMessage;
        this.messageType = (UiUtils.isEmpty(this.message) ? IMessageProvider.NONE : IMessageProvider.ERROR);
    }

    /**
     * Also sets the message severity to an info.
     * 
     * @param newMessage the new message (can be <code>null</code> or empty)
     */
    public void setInformationMessage( final String newMessage ) {
        this.message = newMessage;
        this.messageType = (UiUtils.isEmpty(this.message) ? IMessageProvider.NONE : IMessageProvider.INFORMATION);
    }

    /**
     * Also sets the message severity to an OK.
     * 
     * @param newMessage the new message (can be <code>null</code> or empty)
     */
    public void setOkMessage( final String newMessage ) {
        this.message = newMessage;
        this.messageType = (UiUtils.isEmpty(this.message) ? IMessageProvider.NONE : IMessageProvider.NONE);
    }

    /**
     * Also sets the message severity to warning.
     * 
     * @param newMessage the new message (can be <code>null</code> or empty)
     */
    public void setWarningMessage( final String newMessage ) {
        this.message = newMessage;
        this.messageType = (UiUtils.isEmpty(this.message) ? IMessageProvider.NONE : IMessageProvider.WARNING);
    }
}
