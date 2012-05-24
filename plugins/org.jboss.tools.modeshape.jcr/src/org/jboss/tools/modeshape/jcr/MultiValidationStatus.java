/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import java.util.ArrayList;
import java.util.List;

/**
 * A validation status that can contain other statuses.
 */
public class MultiValidationStatus extends ValidationStatus {

    /**
     * An OK multi-validation status with a standard, localized message.
     */
    public static MultiValidationStatus OK_STATUS = new MultiValidationStatus();

    /**
     * A collection of statuses (never <code>null</code>)
     */
    private final List<ValidationStatus> errors = new ArrayList<ValidationStatus>();

    private ValidationStatus primary = null;

    /**
     * Constructs an OK status.
     */
    public MultiValidationStatus() {
        super(Severity.OK, ValidationStatus.OK_CODE, ValidationStatus.OK_STATUS.getMessage());
    }

    /**
     * @param status the status used to construct (cannot be <code>null</code>)
     */
    public MultiValidationStatus( final ValidationStatus status ) {
        super(status.getSeverity(), status.getCode(), status.getMessage());

        if (!status.isOk()) {
            add(status);
        }
    }

    /**
     * @param statusBeingAdded the status being added (cannot be <code>null</code>)
     */
    public void add( final ValidationStatus statusBeingAdded ) {
        this.errors.add(statusBeingAdded);

        if ((this.primary == null) || statusBeingAdded.getSeverity().isMoreSevere(getSeverity())) {
            this.primary = statusBeingAdded;
            this.severity = this.primary.getSeverity();
            this.message = this.primary.getMessage();
            this.code = this.primary.getCode();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.tools.modeshape.jcr.ValidationStatus#containsCode(int)
     */
    @Override
    public boolean containsCode( int code ) {
        if (code == getCode()) {
            return true;
        }

        for (ValidationStatus status : getAll()) {
            if (status instanceof MultiValidationStatus) {
                if (((MultiValidationStatus)status).containsCode(code)) {
                    return true;
                }
            } else if (code == status.getCode()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return a collection of all contained statuses (never <code>null</code>)
     */
    public List<ValidationStatus> getAll() {
        List<ValidationStatus> all = new ArrayList<ValidationStatus>();

        for (ValidationStatus status : this.errors) {
            if (status instanceof MultiValidationStatus) {
                all.addAll(((MultiValidationStatus)status).getAll());
            } else {
                all.add(status);
            }
        }

        return all;
    }
}
