/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.jboss.tools.modeshape.jcr.cnd.CndElement;
import org.jboss.tools.modeshape.jcr.cnd.CndElement.NotationType;
import org.jboss.tools.modeshape.jcr.cnd.CndImporter;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinition;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceConstants.CndPreference;
import org.jboss.tools.modeshape.jcr.preference.JcrPreferenceStore;
import org.jboss.tools.modeshape.jcr.ui.Activator;
import org.jboss.tools.modeshape.jcr.ui.JcrUiConstants;
import org.jboss.tools.modeshape.jcr.ui.JcrUiUtils;
import org.jboss.tools.modeshape.jcr.ui.cnd.CndMessages;

/**
 * The preference page that allows editing the settings of how CND files are formatted.
 */
public final class CndPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static CompactNodeTypeDefinition _previewCnd;

    /**
     * The editor used to choose the notation type for the CND format.
     */
    private Combo cbxNotationType;

    private String notationType;

    private Text txtPreview;

    /**
     * Constructs a CND preference page.
     */
    public CndPreferencePage() {
        if (_previewCnd == null) {
            _previewCnd = new CompactNodeTypeDefinition();

            final String content = "<abc = 'http://namespace.com/abc/1.0'>\n" //$NON-NLS-1$
                    + "<xyz = 'http://namespace.com/xyz/1.0'>\n" //$NON-NLS-1$
                    + "/* This is a comment for node type definition abc:NodeType. */\n" //$NON-NLS-1$
                    + "[abc:NodeType] > abc:ParentType1, abc:ParentType2 abstract orderable mixin noquery primaryitem abc:propertyABC\n" //$NON-NLS-1$
                    + "- abc:propertyABC (STRING) = 'default1', 'default2' mandatory autocreated protected multiple VERSION\n" //$NON-NLS-1$
                    + " queryops '=, <>, <, <=, >, >=, LIKE' nofulltext noqueryorder < 'constraint1', 'constraint2'" //$NON-NLS-1$
                    + "+ abc:node (abc:reqType1, abc:reqType2) = abc:defaultType mandatory autocreated protected sns version\n" //$NON-NLS-1$
                    + "[xyz:NodeTypeX]\n" //$NON-NLS-1$
                    + "/* This is a comment for property definition xyz:propertyX. */\n" //$NON-NLS-1$
                    + "- xyz:propertyX\n" //$NON-NLS-1$
                    + "[xyz:NodeTypeY]\n" //$NON-NLS-1$
                    + "- xyz:propertyY (LONG)\n" //$NON-NLS-1$
                    + "/* This is a comment for child node definition xyz:childNodeX. */\n" //$NON-NLS-1$
                    + "+ xyz:childNodeY\n"; //$NON-NLS-1$

            CndImporter importer = new CndImporter();
            Collection<Throwable> problems = new ArrayList<Throwable>();

            _previewCnd = importer.importFrom(content, problems, "string"); //$NON-NLS-1$

            if (_previewCnd == null) {
                for (Throwable e : problems) {
                    Activator.getSharedInstance().getLog().log(new Status(IStatus.ERROR, JcrUiConstants.PLUGIN_ID, null, e));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents( final Composite parent ) {
        final Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(2, false));
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        { // notation type editor
            final Label lbl = new Label(panel, SWT.NONE);
            lbl.setText(CndMessages.notationTypeLabel);

            this.cbxNotationType = new Combo(panel, SWT.NONE);
            this.cbxNotationType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            this.cbxNotationType.setItems(new String[] { CndElement.NotationType.LONG.toString(),
                    CndElement.NotationType.COMPRESSED.toString(), CndElement.NotationType.COMPACT.toString() });
            this.cbxNotationType.setToolTipText(CndMessages.notationTypeToolTip);

            // set current value
            this.notationType = JcrPreferenceStore.get().get(CndPreference.NOTATION_TYPE);

            if (this.notationType == null) {
                this.notationType = CndElement.NotationType.LONG.toString();
            }

            this.cbxNotationType.setText(this.notationType);

            // add selection listener
            this.cbxNotationType.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( final SelectionEvent e ) {
                    handleNotationTypeChanged();
                }
            });
        }

        { // preview text
            final Composite previewPanel = new Composite(parent, SWT.NONE);
            previewPanel.setLayout(new GridLayout());
            previewPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Label lblPreview = new Label(previewPanel, SWT.NONE);
            lblPreview.setText(CndMessages.previewLabel);

            this.txtPreview = new Text(previewPanel, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            this.txtPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            this.txtPreview.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
            ((GridData)this.txtPreview.getLayoutData()).widthHint = convertWidthInCharsToPixels(100);
            ((GridData)this.txtPreview.getLayoutData()).heightHint = convertHeightInCharsToPixels(20);
            refreshPreview();
        }

        // register with the help system
        IWorkbenchHelpSystem helpSystem = Activator.getSharedInstance().getWorkbench().getHelpSystem();
        helpSystem.setHelp(panel, JcrUiConstants.HelpContexts.CND_PREFERENCE_PAGE_HELP_CONTEXT);

        return panel;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
     */
    @Override
    public String getDescription() {
        return CndMessages.cndPrefPageDescription;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.DialogPage#getImage()
     */
    @Override
    public Image getImage() {
        return JcrUiUtils.getCndEditorImage();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.DialogPage#getMessage()
     */
    @Override
    public String getMessage() {
        return CndMessages.cndPrefPageMessage;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    @Override
    public IPreferenceStore getPreferenceStore() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
     */
    @Override
    public String getTitle() {
        return CndMessages.cndPrefPageTitle;
    }

    void handleNotationTypeChanged() {
        this.notationType = this.cbxNotationType.getText();
        refreshPreview();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init( final IWorkbench workbench ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        final JcrPreferenceStore prefStore = JcrPreferenceStore.get();

        { // notation type
            String defaultNotationType = prefStore.getDefault(CndPreference.NOTATION_TYPE);

            if (defaultNotationType == null) {
                defaultNotationType = CndElement.NotationType.LONG.toString();
            }

            this.notationType = defaultNotationType;
            this.cbxNotationType.setText(defaultNotationType);
        }

        super.performDefaults();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        JcrPreferenceStore.get().set(CndPreference.NOTATION_TYPE, this.notationType);
        return super.performOk();
    }

    private void refreshPreview() {
        if (_previewCnd == null) {
            this.txtPreview.setText(CndMessages.previewNotAvailableMessage);
        } else {
            this.txtPreview.setText(_previewCnd.toCndNotation(NotationType.valueOf(notationType)));
        }
    }
}
