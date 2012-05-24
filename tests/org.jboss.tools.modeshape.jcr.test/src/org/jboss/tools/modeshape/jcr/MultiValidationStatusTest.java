/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class MultiValidationStatusTest {
    
    private static final int ERROR_CODE = 100;
    private static final String ERROR_MSG = "error messgae"; //$NON-NLS-1$
    private static final ValidationStatus ERROR_STATUS = ValidationStatus.createErrorMessage(ERROR_CODE, ERROR_MSG);
    
    private static final int WARNING_CODE = 200;
    private static final String WARNING_MSG = "warning messgae"; //$NON-NLS-1$
    private static final ValidationStatus WARNING_STATUS = ValidationStatus.createWarningMessage(WARNING_CODE, WARNING_MSG);
    
    private static final int INFO_CODE = 300;
    private static final String INFO_MSG = "warning messgae"; //$NON-NLS-1$
    private static final ValidationStatus INFO_STATUS = ValidationStatus.createInfoMessage(INFO_CODE, INFO_MSG);

    private MultiValidationStatus status;
    
    @Before
    public void beforeEach() {
        this.status = new MultiValidationStatus();
    }

    @Test
    public void shouldHaveOkStatusAfterConstruction() {
        assertTrue(this.status.isOk());
    }

    @Test
    public void shouldNotHaveErrorsAfterConstruction() {
        assertEquals(0, this.status.getAll().size());
    }

    @Test
    public void shouldHaveOkStatusCodeAfterConstruction() {
        assertEquals(ValidationStatus.OK_CODE, this.status.getCode());
    }

    @Test
    public void shouldHaveErrorStatusAfterAddingAnError() {
        this.status.add(ERROR_STATUS);
        assertTrue(this.status.isError());
        assertEquals(ERROR_CODE, this.status.getCode());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertEquals(ERROR_MSG, this.status.getMessage());
        assertEquals(1, this.status.getAll().size());
    }

    @Test
    public void shouldHaveWarningStatusAfterAddingAWarning() {
        this.status.add(WARNING_STATUS);
        assertTrue(this.status.isWarning());
        assertEquals(WARNING_CODE, this.status.getCode());
        assertTrue(this.status.containsCode(WARNING_CODE));
        assertEquals(WARNING_MSG, this.status.getMessage());
        assertEquals(1, this.status.getAll().size());
    }

    @Test
    public void shouldHaveInfoStatusAfterAddingAnInfo() {
        this.status.add(INFO_STATUS);
        assertTrue(this.status.isInfo());
        assertEquals(INFO_CODE, this.status.getCode());
        assertTrue(this.status.containsCode(INFO_CODE));
        assertEquals(INFO_MSG, this.status.getMessage());
        assertEquals(1, this.status.getAll().size());
    }

    @Test
    public void shouldChangeStatusToErrorFromInfoAfterAddingAnError() {
        this.status.add(INFO_STATUS);
        this.status.add(ERROR_STATUS);
        assertTrue(this.status.isError());
        assertEquals(2, this.status.getAll().size());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertTrue(this.status.containsCode(INFO_CODE));
    }

    @Test
    public void shouldChangeStatusToErrorFromWarningAfterAddingAnError() {
        this.status.add(WARNING_STATUS);
        this.status.add(ERROR_STATUS);
        assertTrue(this.status.isError());
        assertEquals(2, this.status.getAll().size());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertTrue(this.status.containsCode(WARNING_CODE));
    }

    @Test
    public void shouldNotChangeStatusFromErrorWhenAddingAnInfo() {
        this.status.add(ERROR_STATUS);
        this.status.add(INFO_STATUS);
        assertTrue(this.status.isError());
        assertEquals(2, this.status.getAll().size());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertTrue(this.status.containsCode(INFO_CODE));
    }

    @Test
    public void shouldNotChangeStatusFromErrorWhenAddingAWarning() {
        this.status.add(ERROR_STATUS);
        this.status.add(WARNING_STATUS);
        assertTrue(this.status.isError());
        assertEquals(2, this.status.getAll().size());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertTrue(this.status.containsCode(WARNING_CODE));
    }

    @Test
    public void shouldNotChangeStatusFromWarningWhenAddingAnInfo() {
        this.status.add(WARNING_STATUS);
        this.status.add(INFO_STATUS);
        assertTrue(this.status.isWarning());
        assertEquals(2, this.status.getAll().size());
        assertTrue(this.status.containsCode(WARNING_CODE));
        assertTrue(this.status.containsCode(INFO_CODE));
    }

    @Test
    public void shouldAddMultiStatusesCorrectly() {
        MultiValidationStatus mvs1 = new MultiValidationStatus(ERROR_STATUS);
        this.status.add(mvs1);
        
        MultiValidationStatus mvs2 = new MultiValidationStatus(WARNING_STATUS);
        this.status.add(mvs2);
        
        MultiValidationStatus mvs3 = new MultiValidationStatus(INFO_STATUS);
        this.status.add(mvs3);
        
        assertTrue(this.status.isError());
        assertEquals(ERROR_CODE, this.status.getCode());
        assertEquals(3, this.status.getAll().size());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertTrue(this.status.containsCode(WARNING_CODE));
        assertTrue(this.status.containsCode(INFO_CODE));
    }

    @Test
    public void shouldAddNestedMultiStatusesCorrectly() {
        MultiValidationStatus mvs1 = new MultiValidationStatus(ERROR_STATUS);
        MultiValidationStatus mvs2 = new MultiValidationStatus(WARNING_STATUS);        
        MultiValidationStatus mvs3 = new MultiValidationStatus(INFO_STATUS);

        mvs2.add(mvs1);
        mvs3.add(mvs2);
        this.status.add(mvs3);
        
        assertTrue(this.status.isError());
        assertEquals(ERROR_CODE, this.status.getCode());
        assertEquals(3, this.status.getAll().size());
        assertTrue(this.status.containsCode(ERROR_CODE));
        assertTrue(this.status.containsCode(WARNING_CODE));
        assertTrue(this.status.containsCode(INFO_CODE));
    }
}
