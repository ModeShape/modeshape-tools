/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
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
        assertThat(this.status.isOk(), is(true));
    }

    @Test
    public void shouldNotHaveErrorsAfterConstruction() {
        assertThat(this.status.getAll().size(), is(0));
    }

    @Test
    public void shouldHaveOkStatusCodeAfterConstruction() {
        assertThat(this.status.getCode(), is(ValidationStatus.OK_CODE));
    }

    @Test
    public void shouldHaveErrorStatusAfterAddingAnError() {
        this.status.add(ERROR_STATUS);
        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getCode(), is(ERROR_CODE));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.getMessage(), is(ERROR_MSG));
        assertThat(this.status.getAll().size(), is(1));
    }

    @Test
    public void shouldHaveWarningStatusAfterAddingAWarning() {
        this.status.add(WARNING_STATUS);
        assertThat(this.status.isWarning(), is(true));
        assertThat(this.status.getCode(), is(WARNING_CODE));
        assertThat(this.status.containsCode(WARNING_CODE), is(true));
        assertThat(this.status.getMessage(), is(WARNING_MSG));
        assertThat(this.status.getAll().size(), is(1));
    }

    @Test
    public void shouldHaveInfoStatusAfterAddingAnInfo() {
        this.status.add(INFO_STATUS);
        assertThat(this.status.isInfo(), is(true));
        assertThat(this.status.getCode(), is(INFO_CODE));
        assertThat(this.status.containsCode(INFO_CODE), is(true));
        assertThat(this.status.getMessage(), is(INFO_MSG));
        assertThat(this.status.getAll().size(), is(1));
    }

    @Test
    public void shouldChangeStatusToErrorFromInfoAfterAddingAnError() {
        this.status.add(INFO_STATUS);
        this.status.add(ERROR_STATUS);
        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getAll().size(), is(2));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.containsCode(INFO_CODE), is(true));
    }

    @Test
    public void shouldChangeStatusToErrorFromWarningAfterAddingAnError() {
        this.status.add(WARNING_STATUS);
        this.status.add(ERROR_STATUS);
        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getAll().size(), is(2));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.containsCode(WARNING_CODE), is(true));
    }

    @Test
    public void shouldNotChangeStatusFromErrorWhenAddingAnInfo() {
        this.status.add(ERROR_STATUS);
        this.status.add(INFO_STATUS);
        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getAll().size(), is(2));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.containsCode(INFO_CODE), is(true));
    }

    @Test
    public void shouldNotChangeStatusFromErrorWhenAddingAWarning() {
        this.status.add(ERROR_STATUS);
        this.status.add(WARNING_STATUS);
        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getAll().size(), is(2));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.containsCode(WARNING_CODE), is(true));
    }

    @Test
    public void shouldNotChangeStatusFromWarningWhenAddingAnInfo() {
        this.status.add(WARNING_STATUS);
        this.status.add(INFO_STATUS);
        assertThat(this.status.isWarning(), is(true));
        assertThat(this.status.getAll().size(), is(2));
        assertThat(this.status.containsCode(WARNING_CODE), is(true));
        assertThat(this.status.containsCode(INFO_CODE), is(true));
    }

    @Test
    public void shouldAddMultiStatusesCorrectly() {
        MultiValidationStatus mvs1 = new MultiValidationStatus(ERROR_STATUS);
        this.status.add(mvs1);

        MultiValidationStatus mvs2 = new MultiValidationStatus(WARNING_STATUS);
        this.status.add(mvs2);

        MultiValidationStatus mvs3 = new MultiValidationStatus(INFO_STATUS);
        this.status.add(mvs3);

        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getCode(), is(ERROR_CODE));
        assertThat(this.status.getAll().size(), is(3));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.containsCode(WARNING_CODE), is(true));
        assertThat(this.status.containsCode(INFO_CODE), is(true));
    }

    @Test
    public void shouldAddNestedMultiStatusesCorrectly() {
        MultiValidationStatus mvs1 = new MultiValidationStatus(ERROR_STATUS);
        MultiValidationStatus mvs2 = new MultiValidationStatus(WARNING_STATUS);
        MultiValidationStatus mvs3 = new MultiValidationStatus(INFO_STATUS);

        mvs2.add(mvs1);
        mvs3.add(mvs2);
        this.status.add(mvs3);

        assertThat(this.status.isError(), is(true));
        assertThat(this.status.getCode(), is(ERROR_CODE));
        assertThat(this.status.getAll().size(), is(3));
        assertThat(this.status.containsCode(ERROR_CODE), is(true));
        assertThat(this.status.containsCode(WARNING_CODE), is(true));
        assertThat(this.status.containsCode(INFO_CODE), is(true));
    }
}
