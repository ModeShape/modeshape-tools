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
package org.jboss.tools.modeshape.rest.wizards;

import static org.jboss.tools.modeshape.rest.IUiConstants.WIZARD_BANNER_IMAGE;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.ServerManager;
import org.jboss.tools.modeshape.rest.domain.ModeShapeServer;
import org.modeshape.web.jcr.rest.client.Status;

/**
 * The <code>ServerWizard</code> is the wizard used to create and edit servers.
 */
public final class ServerWizard extends Wizard {

    /**
     * Non-<code>null</code> if the wizard is editing an existing server.
     */
    private ModeShapeServer existingServer;

    /**
     * The wizard page containing all the controls that allow editing of server properties.
     */
    private final ServerPage page;

    /**
     * The manager in charge of the server registry.
     */
    private final ServerManager serverManager;

    /**
     * Constructs a wizard that creates a new server.
     * 
     * @param serverManager the server manager in charge of the server registry (never <code>null</code>)
     */
    public ServerWizard( ServerManager serverManager ) {
        this.page = new ServerPage();
        this.serverManager = serverManager;

        setDefaultPageImageDescriptor(Activator.getDefault().getImageDescriptor(WIZARD_BANNER_IMAGE));
        setWindowTitle(RestClientI18n.serverWizardNewServerTitle);
    }

    /**
     * Constructs a wizard that edits an existing server.
     * 
     * @param serverManager the server manager in charge of the server registry (never <code>null</code>)
     * @param server the server whose properties are being edited (never <code>null</code>)
     */
    public ServerWizard( ServerManager serverManager,
                         ModeShapeServer server ) {
        this.page = new ServerPage(server);
        this.serverManager = serverManager;
        this.existingServer = server;

        setDefaultPageImageDescriptor(Activator.getDefault().getImageDescriptor(WIZARD_BANNER_IMAGE));
        setWindowTitle(RestClientI18n.serverWizardEditServerTitle);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(this.page);
    }

    /**
     * @return the server manager (never <code>null</code>)
     */
    protected ServerManager getServerManager() {
        return this.serverManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        Status status = Status.OK_STATUS;
        ModeShapeServer server = this.page.getServer();

        if (this.existingServer == null) {
            status = this.serverManager.addServer(server);

            if (status.isError()) {
                MessageDialog.openError(getShell(),
                                        RestClientI18n.errorDialogTitle,
                                        RestClientI18n.serverWizardEditServerErrorMsg);
            }
        } else if (!this.existingServer.equals(server)) {
            status = this.serverManager.updateServer(this.existingServer, server);

            if (status.isError()) {
                MessageDialog.openError(getShell(),
                                        RestClientI18n.errorDialogTitle,
                                        RestClientI18n.serverWizardNewServerErrorMsg);
            }
        }

        // log if necessary
        if (!status.isOk()) {
            Activator.getDefault().log(status);
        }

        return !status.isError();
    }

}
