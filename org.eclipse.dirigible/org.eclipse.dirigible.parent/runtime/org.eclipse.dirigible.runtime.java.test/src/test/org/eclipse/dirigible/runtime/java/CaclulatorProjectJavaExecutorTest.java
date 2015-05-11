/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.runtime.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CaclulatorProjectJavaExecutorTest extends AbstractJavaExecutorTest {

	@Test
	public void testCalculatorAndUtilsClassesWithCacheExecutionTime() throws Exception {
		createResource(RESOURCE_PATH_UTILS, SOURCЕ_UTILS);
		createResource(PATH_CALCULATOR_RESOURCE, SOURCЕ_CALCULATOR);
		long firstExecutionTime = getExecutionTime(MODULE_CALCULATOR);
		
		createResource(PATH_CALCULATOR_RESOURCE, SOURCE_CALCULATOR_UPDATED);
		long secondExecutionTime = getExecutionTime(MODULE_CALCULATOR);
		
		assertCacheExecutionTime(firstExecutionTime, secondExecutionTime);
	}
	
	@Test
	public void testCalculatorAndUtilsClassesWithCacheOutput() throws Exception {
		createResource(RESOURCE_PATH_UTILS, SOURCЕ_UTILS);
		createResource(PATH_CALCULATOR_RESOURCE, SOURCЕ_CALCULATOR);
		execute(MODULE_CALCULATOR);
		assertEquals("Sum of 3 + 5 = 8", getOutput());
		
		createResource(PATH_CALCULATOR_RESOURCE, SOURCE_CALCULATOR_UPDATED);
		execute(MODULE_CALCULATOR);
		assertEquals("Cache was not updated", "Sum of 5 + 5 = 10", getOutput());
	}
}
