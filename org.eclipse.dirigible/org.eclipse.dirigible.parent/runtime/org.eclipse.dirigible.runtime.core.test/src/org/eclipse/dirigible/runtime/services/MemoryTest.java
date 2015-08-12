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

package org.eclipse.dirigible.runtime.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.dirigible.runtime.memory.MemoryLogRecordDAO;
import org.junit.Test;

public class MemoryTest {
	
	@Test
	public void testMemory() {
		assertNotNull(MemoryLogRecordDAO.generateMemoryInfo());
	}
	
	@Test
	public void testMemoryLog() {
		try {
			MemoryLogRecordDAO.insert();
			String memoryLogs = MemoryLogRecordDAO.getMemoryLogRecords();
			assertNotNull(memoryLogs);
			System.out.println(ArrayUtils.toString(memoryLogs));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
