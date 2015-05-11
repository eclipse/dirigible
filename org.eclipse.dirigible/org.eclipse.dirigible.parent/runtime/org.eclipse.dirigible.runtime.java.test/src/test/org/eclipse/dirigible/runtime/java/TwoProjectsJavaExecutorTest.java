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

public class TwoProjectsJavaExecutorTest extends AbstractJavaExecutorTest {

	private static final String EXPECTED_OUTPUT_FOR_HELLO_WORLD = "Hello World!";
	private static final String EXPECTED_OUTPUT_FOR_HELLO_WORLD_UPDATED = "Hello World Updated!";
	private static final String EXPECTED_OUTPUT_FOR_CALCULATOR = "Sum of 3 + 5 = 8";
	private static final String EXPECTED_OUTPUT_FOR_CALCULATOR_UPDATED = "Sum of 5 + 5 = 10";

	@Test
	public void testExecuteTwoServicesFromTwoProjectsOutputs() throws Exception {
		createResource(RESOURCE_PATH_HELLO_WORLD, SOURCЕ_HELLO_WORLD);
		createResource(RESOURCE_PATH_UTILS, SOURCЕ_UTILS);
		createResource(PATH_CALCULATOR_RESOURCE, SOURCЕ_CALCULATOR);
		
		execute(MODULE_HELLO_WORLD);
		assertEquals(EXPECTED_OUTPUT_FOR_HELLO_WORLD,  getOutput());

		execute(MODULE_CALCULATOR);
		assertEquals(EXPECTED_OUTPUT_FOR_CALCULATOR, getOutput());
	}
	
	@Test
	public void testUpdateTwoServicesFromTwoProjectsOutputs() throws Exception {
		createResource(RESOURCE_PATH_HELLO_WORLD, SOURCЕ_HELLO_WORLD);
		createResource(RESOURCE_PATH_UTILS, SOURCЕ_UTILS);
		createResource(PATH_CALCULATOR_RESOURCE, SOURCЕ_CALCULATOR);
		
		execute(MODULE_HELLO_WORLD);
		assertEquals(EXPECTED_OUTPUT_FOR_HELLO_WORLD,  getOutput());
		execute(MODULE_CALCULATOR);
		assertEquals(EXPECTED_OUTPUT_FOR_CALCULATOR, getOutput());
		
		createResource(RESOURCE_PATH_HELLO_WORLD, SOURCE_HELLO_WORLD_UPDATED);
		execute(MODULE_HELLO_WORLD);
		assertEquals(EXPECTED_OUTPUT_FOR_HELLO_WORLD_UPDATED, getOutput());
		
		createResource(PATH_CALCULATOR_RESOURCE, SOURCE_CALCULATOR_UPDATED);
		execute(MODULE_CALCULATOR);
		assertEquals(EXPECTED_OUTPUT_FOR_CALCULATOR_UPDATED, getOutput());
	}
}
