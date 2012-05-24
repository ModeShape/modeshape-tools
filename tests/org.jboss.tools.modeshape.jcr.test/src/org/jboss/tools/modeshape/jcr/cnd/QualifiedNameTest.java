/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.cnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.tools.modeshape.jcr.QualifiedName;
import org.jboss.tools.modeshape.jcr.Utils;
import org.junit.Before;
import org.junit.Test;

public class QualifiedNameTest {

    private QualifiedName qualifiedName;

    @Before
    public void beforeEach() {
        this.qualifiedName = new QualifiedName();
    }

    @Test
    public void noArgConstructorShouldNotHaveQualifier() {
        assertTrue(Utils.isEmpty(this.qualifiedName.getQualifier()));
    }

    @Test
    public void noArgConstructorShouldNotHaveUnqualifiedName() {
        assertTrue(Utils.isEmpty(this.qualifiedName.getUnqualifiedName()));
    }

    @Test
    public void shouldAllowNullValuesAtConstructor() {
        new QualifiedName(null, null);
    }

    @Test
    public void shouldNotHaveQualifierWhenSettingWithJustUnqualifiedName() {
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        assertTrue(this.qualifiedName.set(NAME));
        assertTrue(Utils.isEmpty(this.qualifiedName.getQualifier()));
        assertEquals(NAME, this.qualifiedName.getUnqualifiedName());
    }

    @Test
    public void shouldNotHaveUnqualifiedNameWhenSettingWithJustQualifier() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        assertTrue(this.qualifiedName.set(QUALIFIER + QualifiedName.DELIM));
        assertTrue(Utils.isEmpty(this.qualifiedName.getUnqualifiedName()));
        assertEquals(QUALIFIER, this.qualifiedName.getQualifier());
    }

    @Test
    public void shouldNotSetQualifierToSameValue() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        assertTrue(this.qualifiedName.setQualifier(QUALIFIER));
        assertFalse(this.qualifiedName.setQualifier(QUALIFIER));
    }

    @Test
    public void shouldNotSetUnqualifiedNameToSameValue() {
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        assertTrue(this.qualifiedName.setUnqualifiedName(NAME));
        assertFalse(this.qualifiedName.setUnqualifiedName(NAME));
    }

    @Test
    public void shouldParseQualifiedName() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        final String QUALIFIED_NAME = QUALIFIER + QualifiedName.DELIM + NAME;
        final QualifiedName qualifiedName = QualifiedName.parse(QUALIFIED_NAME);
        assertEquals(QUALIFIER, qualifiedName.getQualifier());
        assertEquals(NAME, qualifiedName.getUnqualifiedName());
    }

    @Test
    public void shouldSetQualifier() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        assertTrue(this.qualifiedName.setQualifier(QUALIFIER));
        assertEquals(QUALIFIER, this.qualifiedName.getQualifier());
    }

    @Test
    public void shouldSetQualifierAtConstructor() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        this.qualifiedName = new QualifiedName(QUALIFIER, null);
        assertEquals(QUALIFIER, this.qualifiedName.getQualifier());
    }

    @Test
    public void shouldSetUnqualifiedName() {
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        assertTrue(this.qualifiedName.setUnqualifiedName(NAME));
        assertEquals(NAME, this.qualifiedName.getUnqualifiedName());
    }

    @Test
    public void shouldSetUnqualifiedNameAtConstructor() {
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        this.qualifiedName = new QualifiedName(null, NAME);
        assertEquals(NAME, this.qualifiedName.getUnqualifiedName());
    }

    @Test
    public void shouldSetWithQualifiedName() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        final String QUALIFIED_NAME = QUALIFIER + QualifiedName.DELIM + NAME;
        assertTrue(this.qualifiedName.set(QUALIFIED_NAME));
        assertEquals(QUALIFIER, this.qualifiedName.getQualifier());
        assertEquals(NAME, this.qualifiedName.getUnqualifiedName());
    }

    @Test
    public void twoQualifiedNamesWithDifferentQualifiersAndSameNameShouldNotBeEqual() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        final QualifiedName qn1 = new QualifiedName(QUALIFIER, NAME);
        final QualifiedName qn2 = new QualifiedName(QUALIFIER + "Changed", NAME); //$NON-NLS-1$
        assertFalse(qn1.equals(qn2));
    }

    @Test
    public void twoQualifiedNamesWithSameQualifierAndDifferentNamesShouldNotBeEqual() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        final QualifiedName qn1 = new QualifiedName(QUALIFIER, NAME);
        final QualifiedName qn2 = new QualifiedName(QUALIFIER, NAME + "Changed"); //$NON-NLS-1$
        assertFalse(qn1.equals(qn2));
    }

    @Test
    public void twoQualifiedNamesWithSameQualifierAndNameShouldBeEqual() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        final QualifiedName qn1 = new QualifiedName(QUALIFIER, NAME);
        final QualifiedName qn2 = new QualifiedName(QUALIFIER, NAME);
        assertEquals(qn1, qn2);
    }

    @Test
    public void twoQualifiedNamesWithSameQualifierAndNameShouldHaveSameHashCode() {
        final String QUALIFIER = "qualifier"; //$NON-NLS-1$
        final String NAME = "unqualifiedName"; //$NON-NLS-1$
        final QualifiedName qn1 = new QualifiedName(QUALIFIER, NAME);
        final QualifiedName qn2 = new QualifiedName(QUALIFIER, NAME);
        assertEquals(qn1.hashCode(), qn2.hashCode());
    }

}
