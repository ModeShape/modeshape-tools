/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.ui.cnd;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;

/**
 * Provides proposals based on the qualifier part of a qualified name.
 */
abstract class QualifiedNameProposalProvider implements IContentProposalProvider {

    public static final QualifiedNameProposalProvider NO_PROPOSALS_PROVIDER = new QualifiedNameProposalProvider() {

        /**
         * {@inheritDoc}
         * 
         * @see org.jboss.tools.modeshape.jcr.ui.cnd.QualifiedNameProposalProvider#qnameStartsWith(java.lang.String,
         *      java.lang.String)
         */
        @Override
        protected List<QualifiedName> qnameStartsWith( final String qualifier,
                                                       final String namePattern ) {
            return Collections.emptyList();
        }
    };

    private String qualifier;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.fieldassist.IContentProposalProvider#getProposals(java.lang.String, int)
     */
    @Override
    public final IContentProposal[] getProposals( final String contents,
                                                  final int position ) {
        final List<QualifiedName> matches = qnameStartsWith(this.qualifier, contents);

        if (Utils.isEmpty(matches)) {
            return new IContentProposal[0];
        }

        final IContentProposal[] proposals = new IContentProposal[matches.size()];
        int i = 0;

        for (final QualifiedName qname : matches) {
            proposals[i++] = new ContentProposal(qname.getUnqualifiedName());
        }

        return proposals;
    }

    /**
     * @param qualifier the qualifier to match (can be <code>null</code> or empty)
     * @param namePattern the text to match (can be <code>null</code> or empty)
     * @return a collection of <code>QualifiedName</code>s whose qualifier matches and whose unqualified name starts with the
     *         specified name pattern (never <code>null</code> but can be empty)
     */
    protected abstract List<QualifiedName> qnameStartsWith( String qualifier,
                                                            String namePattern );

    /**
     * @param qualifier the qualifier the proposals should be based on (can be <code>null</code> or empty)
     */
    public void setQualifier( final String qualifier ) {
        this.qualifier = qualifier;
    }
}
