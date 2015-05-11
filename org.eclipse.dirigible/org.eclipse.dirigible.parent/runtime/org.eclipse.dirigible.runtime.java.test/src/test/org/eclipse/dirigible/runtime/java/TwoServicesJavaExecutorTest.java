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

public class TwoServicesJavaExecutorTest extends AbstractJavaExecutorTest {
	private static final String EXPECTED_OUTPUT_FOR_SERVICE1 = "Hello from Service 1!"; //$NON-NLS-1$
	private static final String EXPECTED_OUTPUT_FOR_SERVICE2 = "Hello from Service 2!"; //$NON-NLS-1$
	private static final String EXPECTED_OUTPUT_FOR_UPDATED_SERVICE1 = "Service 1, updated!"; //$NON-NLS-1$
	private static final String EXPECTED_OUTPUT_FOR_UPDATED_SERVICE2 = "Service 2, updated!"; //$NON-NLS-1$

	@Test
	public void testExecuteTwoServicesFromOneProjectOutputs() throws Exception {
		createResource(RESOURCE_PATH_SERVICE1, SOURCЕ_SERVICE1);
		createResource(RESOURCE_PATH_SERVICE2, SOURCЕ_SERVICE2);
		
		execute(MODULE_SERVICE1);
		assertEquals(EXPECTED_OUTPUT_FOR_SERVICE1, getOutput());
		
		execute(MODULE_SERVICE2);
		assertEquals(EXPECTED_OUTPUT_FOR_SERVICE2, getOutput());
	}
	
	@Test
	public void testUpdateTwoServicesFromOneProjectOutputs() throws Exception {

		createResource(RESOURCE_PATH_SERVICE1, SOURCЕ_SERVICE1);
		createResource(RESOURCE_PATH_SERVICE2, SOURCЕ_SERVICE2);
		
		execute(MODULE_SERVICE1);
		assertEquals(EXPECTED_OUTPUT_FOR_SERVICE1, getOutput());
		execute(MODULE_SERVICE2);
		assertEquals(EXPECTED_OUTPUT_FOR_SERVICE2, getOutput());
		
		createResource(RESOURCE_PATH_SERVICE1, SOURCЕ_SERVICE1_UPDATED);

		execute(MODULE_SERVICE1);
		assertEquals(EXPECTED_OUTPUT_FOR_UPDATED_SERVICE1, getOutput());
		execute(MODULE_SERVICE2);
		assertEquals(EXPECTED_OUTPUT_FOR_SERVICE2, getOutput());
		
		createResource(RESOURCE_PATH_SERVICE2, SOURCЕ_SERVICE2_UPDATED);
		
		execute(MODULE_SERVICE1);
		assertEquals(EXPECTED_OUTPUT_FOR_UPDATED_SERVICE1, getOutput());
		execute(MODULE_SERVICE2);
		assertEquals(EXPECTED_OUTPUT_FOR_UPDATED_SERVICE2, getOutput());
	}
}
