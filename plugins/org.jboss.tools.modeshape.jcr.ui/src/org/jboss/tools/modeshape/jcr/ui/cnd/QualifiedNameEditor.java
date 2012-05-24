/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jboss.tools.modeshape.jcr.ItemDefinition;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.jboss.tools.modeshape.jcr.ValidationStatus;
import org.jboss.tools.modeshape.jcr.cnd.CndValidator;
import org.jboss.tools.modeshape.ui.forms.FormUtils.Styles;

/**
 * A UI container that can be used to edit or create a qualified name. To receive notification of when a change was made in the
 * editor, register using {@link #addListener(int, org.eclipse.swt.widgets.Listener)} using {@link SWT#Modify} as the event type.
 * After notification, a call to {@link #getStatus()} will have the latest validation results.
 */
final class QualifiedNameEditor extends Composite {

    private CCombo cbxQualifiers;

    /**
     * An optional collection of existing qualified names. When this is non-empty, it is checked to make sure the qualified name
     * being edited is not a duplicate.
     */
    private Collection<QualifiedName> existingQNames;

    private Label lblName;

    private Label lblQualifier;

    /**
     * The qualified name being edited or <code>null</code> when creating a qualified name.
     */
    private QualifiedName qnameBeingEdited;

    private final String qualifiedNameType;

    private String qualifier;

    private ContentProposalAdapter proposalAdapter;

    private QualifiedNameProposalProvider proposalProvider;

    private boolean residualNameAllowed = false;

    private ValidationStatus status;

    private final FormToolkit toolkit;

    private Text txtName;

    private String unqualifiedName;

    /**
     * A collection of known qualifiers/namespace prefixes to the CND (never <code>null</code>).
     */
    private List<String> validQualifiers;

    /**
     * Used to edit an existing or create a new qualified name.
     * 
     * @param parent the parent container (cannot be <code>null</code>)
     * @param style the composite style
     * @param toolkit the toolkit (cannot be <code>null</code>)
     * @param qualifiedNameType a word describing what the qualified name represents (cannot be <code>null</code> or empty)
     * @param existingQualifiers the existing qualifies (can be <code>null</code> or empty)
     * @param qnameBeingEdited the qualified name being edited or <code>null</code> if creating a new name
     */
    public QualifiedNameEditor( final Composite parent,
                                final int style,
                                final FormToolkit toolkit,
                                final String qualifiedNameType,
                                final Collection<String> existingQualifiers,
                                final QualifiedName qnameBeingEdited ) {
        super(parent, style);
        Utils.verifyIsNotNull(qualifiedNameType, "qualifiedNameType"); //$NON-NLS-1$
        Utils.verifyIsNotNull(toolkit, "toolkit"); //$NON-NLS-1$

        this.qualifiedNameType = qualifiedNameType;
        this.toolkit = toolkit;
        this.toolkit.adapt(this);

        this.toolkit.adapt(this);
        this.toolkit.paintBordersFor(this);

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        constructEditor();
        setValidQualifiers(existingQualifiers);
        setNameBeingEdited(qnameBeingEdited);
    }

    private void constructEditor() {
        { // qualifier
            this.lblQualifier = this.toolkit.createLabel(this, CndMessages.qualifierLabel, SWT.NONE);
            this.lblQualifier.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

            this.cbxQualifiers = new CCombo(this, Styles.COMBO_STYLE);
            this.toolkit.adapt(this.cbxQualifiers, true, false);
            this.cbxQualifiers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
            ((GridData)this.cbxQualifiers.getLayoutData()).heightHint = this.cbxQualifiers.getItemHeight() + 4;
            this.cbxQualifiers.setToolTipText(CndMessages.validQualifiersToolTip);

            this.cbxQualifiers.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( final ModifyEvent e ) {
                    final String newQualifier = ((CCombo)e.widget).getText();
                    handleQualifierChanged(newQualifier);
                }
            });
        }

        { // unqualified name
            this.lblName = this.toolkit.createLabel(this, CndMessages.nameLabel, SWT.NONE);
            this.lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

            this.txtName = this.toolkit.createText(this, null, Styles.TEXT_STYLE);
            this.txtName.setToolTipText(CndMessages.unqualifiedNameToolTip);
            this.txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            this.txtName.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( final ModifyEvent e ) {
                    handleNameChanged(((Text)e.widget).getText());
                }
            });

            this.proposalAdapter = new ContentProposalAdapter(this.txtName,
                                                              new TextContentAdapter(),
                                                              new SimpleContentProposalProvider(new String[0]),
                                                              KeyStroke.getInstance(SWT.CTRL, ' '),
                                                              CndValidator.LOCAL_NAME_VALID_CHARS.toCharArray());
            this.proposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

            this.txtName.setFocus();
        }
    }

    /**
     * @return the new or edited qualified name (never <code>null</code>)
     */
    public QualifiedName getQualifiedName() {
        return new QualifiedName(this.qualifier, this.unqualifiedName);
    }

    /**
     * @return the latest validation results (never <code>null</code>)
     */
    public ValidationStatus getStatus() {
        return this.status;
    }

    void handleNameChanged( final String newName ) {
        this.unqualifiedName = newName;
        validate();
    }

    void handleQualifierChanged( final String newQualifier ) {
        if (CndMessages.noNameQualifierChoice.equals(newQualifier)) {
            this.qualifier = Utils.EMPTY_STRING;
        } else {
            this.qualifier = newQualifier;
        }

        // let the proposal provider know
        if (this.proposalProvider != null) {
            this.proposalProvider.setQualifier(this.qualifier);
        }

        validate();
    }

    private boolean isEditMode() {
        return (this.qnameBeingEdited != null);
    }

    /**
     * @param newValue indicates if and unqualified name equal to {@link ItemDefinition#RESIDUAL_NAME} is allowed.
     */
    public void setAllowsResidualName( final boolean newValue ) {
        this.residualNameAllowed = newValue;
        validate();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
     */
    @Override
    public void setEnabled( final boolean enable ) {
        super.setEnabled(enable);

        // all controls have the same enablement
        if (this.txtName.getEnabled() != enable) {
            this.lblQualifier.setEnabled(enable);
            this.cbxQualifiers.setEnabled(enable);
            this.lblName.setEnabled(enable);
            this.txtName.setEnabled(enable);
        }
    }

    /**
     * @param existingQNames used to check against for duplicate qualified names (can be <code>null</code> or empty)
     */
    void setExistingQNames( final Collection<QualifiedName> existingQNames ) {
        if (Utils.isEmpty(existingQNames)) {
            this.existingQNames = null;
        } else {
            this.existingQNames = new ArrayList<QualifiedName>(existingQNames);

            // so that validating won't show it as a duplicate
            if (isEditMode()) {
                this.existingQNames.remove(this.qnameBeingEdited);
            }
        }
    }

    public void setNameBeingEdited( final QualifiedName qnameBeingEdited ) {
        this.qnameBeingEdited = qnameBeingEdited;

        if (this.qnameBeingEdited == null) {
            this.qualifier = null;
            this.unqualifiedName = null;
        } else {
            this.qualifier = this.qnameBeingEdited.getQualifier();
            this.unqualifiedName = this.qnameBeingEdited.getUnqualifiedName();
        }

        updateUi();
        validate();
    }

    /**
     * @param proposalProvider the proposal provider (can be <code>null</code>)
     */
    public void setProposalProvider( final QualifiedNameProposalProvider proposalProvider ) {
        this.proposalProvider = proposalProvider;
        this.proposalAdapter.setContentProposalProvider(proposalProvider);
    }

    /**
     * @param validQualifiers the valid qualifiers (can be <code>null</code> or empty)
     */
    void setValidQualifiers( final Collection<String> validQualifiers ) {
        this.validQualifiers = ((validQualifiers == null) ? new ArrayList<String>(1) : new ArrayList<String>(validQualifiers));

        if (!this.validQualifiers.contains(CndMessages.noNameQualifierChoice)) {
            this.validQualifiers.add(0, CndMessages.noNameQualifierChoice); // include empty qualifier at index 0
        }

        updateUi();
    }

    private void updateUi() {
        // set qualifier choices if they have changed
        final String[] currentItems = this.cbxQualifiers.getItems();
        final List<String> allQualifiers = new ArrayList<String>(this.validQualifiers);
        String currentQualifier = null;
        
        if (isEditMode()) {
            currentQualifier = this.qnameBeingEdited.getQualifier();
            
            if (!Utils.isEmpty(currentQualifier) && !this.validQualifiers.contains(currentQualifier)) {
                allQualifiers.add(currentQualifier);
            }                
        }

        // only reload qualifiers if different
        if ((allQualifiers.size() != currentItems.length) || !allQualifiers.containsAll(Arrays.asList(currentItems))) {
            final String[] newQualifiers = allQualifiers.toArray(new String[allQualifiers.size()]);
            Arrays.sort(newQualifiers);
            this.cbxQualifiers.setItems(newQualifiers);
        }

        // select the current qualifier and set the name
        if (isEditMode()) {
            if (Utils.isEmpty(currentQualifier)) {
                this.cbxQualifiers.select(0);
            } else {
                final int index = this.cbxQualifiers.indexOf(currentQualifier);
                // qualifier should already be a valid selection
                assert (index != -1) : "qualifier not found=" + currentQualifier; //$NON-NLS-1$
                this.cbxQualifiers.select(index);
            }

            final String name = this.qnameBeingEdited.getUnqualifiedName();
            this.txtName.setText(Utils.isEmpty(name) ? Utils.EMPTY_STRING : name);
        } else {
            this.cbxQualifiers.select(0);
            this.txtName.setText(Utils.EMPTY_STRING);
        }
    }

    private void validate() {
        if (ItemDefinition.RESIDUAL_NAME.equals(this.unqualifiedName) && this.residualNameAllowed) {
            this.status = ValidationStatus.OK_STATUS;
        } else {
            final QualifiedName currentQName = new QualifiedName(this.qualifier, this.unqualifiedName);
            this.status = CndValidator.validateQualifiedName(currentQName, this.qualifiedNameType, this.validQualifiers,
                                                             this.existingQNames);
        }

        final Event e = new Event();
        e.widget = this;
        e.type = SWT.Modify;
        e.text = getQualifiedName().get();
        notifyListeners(SWT.Modify, e);
    }
}
