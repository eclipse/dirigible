package org.eclipse.dirigible.integration.tests.ui.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Ordered Test Suite")
@SelectClasses({BPMStarterTemplateIT.class, MultitenancyIT.class})
//@SelectClasses({MultitenancyIT.class, BPMStarterTemplateIT.class,})
public class OrderedTestSuite {
}
