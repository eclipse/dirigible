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


public class HelloWorldProjectJavaExecutorTest extends AbstractJavaExecutorTest {
	
	@Test
	public void testHelloWorldClassWithCacheExecutionTime() throws Exception {
		createResource(RESOURCE_PATH_HELLO_WORLD, SOURCЕ_HELLO_WORLD);
		
		long firstExecutionTime = getExecutionTime(MODULE_HELLO_WORLD);
		long secondExecutionTime = getExecutionTime(MODULE_HELLO_WORLD);
		
		assertCacheExecutionTime(firstExecutionTime, secondExecutionTime);
	}

	@Test
	public void testHelloWorldClassWithCacheOutput() throws Exception {
		createResource(RESOURCE_PATH_HELLO_WORLD, SOURCЕ_HELLO_WORLD);
		execute(MODULE_HELLO_WORLD);
		assertEquals("Hello World!", getOutput());
		
		createResource(RESOURCE_PATH_HELLO_WORLD, SOURCE_HELLO_WORLD_UPDATED);
		execute(MODULE_HELLO_WORLD);
		assertEquals("Hello World Updated!", getOutput());
	}
}
