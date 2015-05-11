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

import org.junit.Test;

import org.eclipse.dirigible.runtime.memory.MemoryLogRecordDAO;

public class MemoryTest {
	
	@Test
	public void testMemory() {
		assertNotNull(MemoryLogRecordDAO.generateMemoryInfo());
	}
}
