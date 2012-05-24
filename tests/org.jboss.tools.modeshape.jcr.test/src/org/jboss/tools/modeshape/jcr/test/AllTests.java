/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.jboss.tools.modeshape.jcr.test;

import org.jboss.tools.modeshape.jcr.MultiValidationStatusTest;
import org.jboss.tools.modeshape.jcr.cnd.ChildNodeDefinitionTest;
import org.jboss.tools.modeshape.jcr.cnd.CndImporterTest;
import org.jboss.tools.modeshape.jcr.cnd.CndTokenizerTest;
import org.jboss.tools.modeshape.jcr.cnd.CndValidatorTest;
import org.jboss.tools.modeshape.jcr.cnd.CompactNodeTypeDefinitionTest;
import org.jboss.tools.modeshape.jcr.cnd.LocalNameTest;
import org.jboss.tools.modeshape.jcr.cnd.NamespaceMappingTest;
import org.jboss.tools.modeshape.jcr.cnd.NodeTypeDefinitionTest;
import org.jboss.tools.modeshape.jcr.cnd.PropertyDefinitionTest;
import org.jboss.tools.modeshape.jcr.cnd.QualifiedNameTest;
import org.jboss.tools.modeshape.jcr.cnd.WorkspaceRegistryTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.AbstractTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.AutocreatedTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.DefaultTypeTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.DefaultValuesTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.MandatoryTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.MixinTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.MultipleTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.NoFullTextTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.NoQueryOrderTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.NodeAttributesTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.NodeTypeAttributesTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.OnParentValueTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.OrderableTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.PrimaryItemTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.PropertyAttributesTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.ProtectedTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.QueryOperatorsTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.QueryableTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.RequiredTypesTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.SameNameSiblingsTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.SuperTypesTest;
import org.jboss.tools.modeshape.jcr.cnd.attributes.ValueConstraintsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
    ChildNodeDefinitionTest.class, 
    CndImporterTest.class, 
    CndTokenizerTest.class, 
    CndValidatorTest.class,
    CompactNodeTypeDefinitionTest.class, 
    LocalNameTest.class, 
    MultiValidationStatusTest.class,
    NamespaceMappingTest.class, 
    NodeTypeDefinitionTest.class,
    PropertyDefinitionTest.class,
    QualifiedNameTest.class, 
    WorkspaceRegistryTest.class,
    AbstractTest.class, 
    AutocreatedTest.class, 
    DefaultTypeTest.class, 
    DefaultValuesTest.class,
    MandatoryTest.class,
    MixinTest.class, 
    MultipleTest.class, 
    NodeAttributesTest.class, 
    NodeTypeAttributesTest.class,
    NoFullTextTest.class, 
    NoQueryOrderTest.class, 
    OnParentValueTest.class, 
    OrderableTest.class, 
    PrimaryItemTest.class,
    PropertyAttributesTest.class, 
    ProtectedTest.class, 
    QueryableTest.class, 
    QueryOperatorsTest.class, 
    RequiredTypesTest.class,
    SameNameSiblingsTest.class, 
    SuperTypesTest.class, 
    ValueConstraintsTest.class })
public class AllTests {
    // nothing to do
}
