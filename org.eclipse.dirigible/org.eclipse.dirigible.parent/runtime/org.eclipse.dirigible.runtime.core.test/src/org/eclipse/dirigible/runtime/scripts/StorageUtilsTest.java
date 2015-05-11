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

package org.eclipse.dirigible.runtime.scripts;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;

import org.junit.Before;
import org.junit.Test;

import org.eclipse.dirigible.runtime.scripting.AbstractStorageUtils;
import org.eclipse.dirigible.runtime.scripting.utils.StorageUtils;
import org.eclipse.dirigible.runtime.utils.DataSourceUtils;

public class StorageUtilsTest {

	private static final String PATH = "/a/b/c";
	private static final byte[] DATA = "Some data".getBytes();
	private static final byte[] OTHER_DATA = "Other data".getBytes();
	private static final byte[] TOO_BIG_DATA = new byte[AbstractStorageUtils.MAX_STORAGE_FILE_SIZE_IN_BYTES + 1];

	private StorageUtils storage;

	@Before
	public void setUp() throws Exception {
		storage = new StorageUtils(DataSourceUtils.createLocal());
	}

	@Test
	public void testPut() throws Exception {
		storage.put(PATH, DATA);
		assertArrayEquals(DATA, storage.get(PATH));
	}

	@Test
	public void testPutTooBigData() throws Exception {
		try {
			storage.put(PATH, TOO_BIG_DATA);
			fail("Test should fail, because " + AbstractStorageUtils.TOO_BIG_DATA_MESSAGE);
		} catch (InvalidParameterException e) {
			assertEquals(AbstractStorageUtils.TOO_BIG_DATA_MESSAGE, e.getMessage());
		}
	}

	@Test
	public void testClear() throws Exception {
		storage.put(PATH, DATA);
		storage.clear();
		assertNull(storage.get(PATH));
	}

	@Test
	public void testDelete() throws Exception {
		storage.put(PATH, DATA);
		storage.delete(PATH);
		assertNull(storage.get(PATH));
	}

	@Test
	public void testSet() throws Exception {
		storage.put(PATH, DATA);
		storage.put(PATH, OTHER_DATA);
		assertArrayEquals(OTHER_DATA, storage.get(PATH));
	}

}
