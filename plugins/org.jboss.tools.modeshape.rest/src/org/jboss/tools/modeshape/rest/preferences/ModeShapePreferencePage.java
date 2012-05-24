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

import static org.jboss.tools.modeshape.rest.IUiConstants.ModeShape_IMAGE_16x;
import static org.jboss.tools.modeshape.rest.IUiConstants.HelpContexts.PREFERENCE_PAGE_HELP_CONTEXT;
import static org.jboss.tools.modeshape.rest.IUiConstants.Preferences.ENABLE_RESOURCE_VERSIONING;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferencePageDescription;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferencePageEnableVersioningEditor;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferencePageEnableVersioningEditorToolTip;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferencePageMessage;
import static org.jboss.tools.modeshape.rest.RestClientI18n.preferencePageTitle;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.jboss.tools.modeshape.rest.Activator;

/**
 * The <code>ModeShapePreferencePage</code> is the UI for managing general ModeShape-related preferences.
 */
public final class ModeShapePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /**
     * The editor used to enable and disable versioning in ModeShape.
     */
    private BooleanFieldEditor enableVersioningEditor;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( Composite parent ) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(2, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create the field editor
        this.enableVersioningEditor = new BooleanFieldEditor(ENABLE_RESOURCE_VERSIONING,
                                                             preferencePageEnableVersioningEditor,
                                                             panel);
        this.enableVersioningEditor.setPreferenceStore(getPreferenceStore());
        this.enableVersioningEditor.getDescriptionControl(panel).setToolTipText(preferencePageEnableVersioningEditorToolTip);

        // populate the editor with current preference value
        this.enableVersioningEditor.load();

        // register with the help system
        IWorkbenchHelpSystem helpSystem = Activator.getDefault().getWorkbench().getHelpSystem();
        helpSystem.setHelp(panel, PREFERENCE_PAGE_HELP_CONTEXT);

        return panel;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
     */
    @Override
    public String getDescription() {
        return preferencePageDescription;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getImage()
     */
    @Override
    public Image getImage() {
        return Activator.getDefault().getImage(ModeShape_IMAGE_16x);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getMessage()
     */
    @Override
    public String getMessage() {
        return preferencePageMessage;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    @Override
    public IPreferenceStore getPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
     */
    @Override
    public String getTitle() {
        return preferencePageTitle;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init( IWorkbench workbench ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        this.enableVersioningEditor.loadDefault();
        super.performDefaults();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        this.enableVersioningEditor.store();
        return super.performOk();
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);
        
        if (visible) {
            this.enableVersioningEditor.setFocus();
        }
    }

}
