/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tools.modeshape.rest.dialogs;

import static org.jboss.tools.modeshape.rest.IUiConstants.ModeShape_IMAGE_16x;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.modeshape.rest.Activator;
import org.jboss.tools.modeshape.rest.RestClientI18n;
import org.jboss.tools.modeshape.rest.domain.ModeShapeWorkspace;
import org.modeshape.common.util.CheckArg;
import org.modeshape.common.util.StringUtil;

/**
 * A dialog that is used to add a publish area to a workspace.
 */
public final class AddPublishAreaDialog extends MessageDialog {

    private Button btnOk;

    private String publishArea;

    private final ModeShapeWorkspace workspace;

    public AddPublishAreaDialog( final Shell parentShell,
                                 final ModeShapeWorkspace workspace ) {
        super(parentShell, RestClientI18n.addPublishAreaDialogTitle, Activator.getDefault().getImage(ModeShape_IMAGE_16x),
              RestClientI18n.addPublishAreaDialogMessage, MessageDialog.QUESTION, new String[] {IDialogConstants.OK_LABEL,
                  IDialogConstants.CANCEL_LABEL}, 0);

        CheckArg.isNotNull(workspace, "workspace");
        this.workspace = workspace;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String,
     *      boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        final Button btn = super.createButton(parent, id, label, defaultButton);

        if (IDialogConstants.OK_ID == id) {
            this.btnOk = btn;
            this.btnOk.setEnabled(false);
        }

        return btn;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        final Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(2, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Label lbl = new Label(panel, SWT.NONE);
        lbl.setText(NLS.bind(RestClientI18n.workspacePath, this.workspace.getRepository().getName(), this.workspace.getName()));

        final Text txt = new Text(panel, SWT.BORDER);
        txt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        txt.addModifyListener(new ModifyListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            @Override
            public void modifyText( ModifyEvent e ) {
                handlePublishAreaChanged(txt.getText());
            }
        });

        return panel;
    }

    void handlePublishAreaChanged( final String newPublishArea ) {
        this.publishArea = newPublishArea;
        final boolean enable = !StringUtil.isBlank(newPublishArea);

        if (this.btnOk.getEnabled() != enable) {
            this.btnOk.setEnabled(enable);
        }
    }

    /**
     * @return the publish area to create (never <code>null</code>)
     */
    public String publishArea() {
        return this.publishArea;
    }

}
