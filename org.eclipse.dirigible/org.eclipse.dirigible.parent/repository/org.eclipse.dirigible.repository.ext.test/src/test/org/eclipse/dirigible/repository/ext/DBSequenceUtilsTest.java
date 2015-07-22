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

package test.org.eclipse.dirigible.repository.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.ext.db.DBSequenceUtils;
import org.junit.Before;
import org.junit.Test;


public class DBSequenceUtilsTest {

	private DataSource dataSource;
	private DBSequenceUtils dbSequenceUtils;

	@Before
	public void setUp() {
		dataSource = DataSourceUtils.createLocal();
		dbSequenceUtils = new DBSequenceUtils(dataSource);
	}

	@Test
	public void testGetNext() {
		try {
			dbSequenceUtils.dropSequence("DGB_TEST_SEQ"); //$NON-NLS-1$
			int value = dbSequenceUtils.getNext("DGB_TEST_SEQ"); //$NON-NLS-1$
			assertEquals(1, value);
			value = dbSequenceUtils.getNext("DGB_TEST_SEQ"); //$NON-NLS-1$
			assertEquals(2, value);
			value = dbSequenceUtils.getNext("DGB_TEST_SEQ"); //$NON-NLS-1$
			assertEquals(3, value);
		} catch (SQLException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateSequence() {
		try {
			dbSequenceUtils.createSequence("DGB_TEST_SEQ1", 0); //$NON-NLS-1$
			dbSequenceUtils.dropSequence("DGB_TEST_SEQ1"); //$NON-NLS-1$
		} catch (SQLException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testDropSequence() {
		try {
			dbSequenceUtils.dropSequence("DGB_TEST_SEQ2"); //$NON-NLS-1$
			int value = dbSequenceUtils.getNext("DGB_TEST_SEQ2"); //$NON-NLS-1$
			assertEquals(1, value);
			dbSequenceUtils.dropSequence("DGB_TEST_SEQ2"); //$NON-NLS-1$
			value = dbSequenceUtils.getNext("DGB_TEST_SEQ2"); //$NON-NLS-1$
			assertEquals(1, value);
		} catch (SQLException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

	@Test
	public void testExistSequence() {
		try {
			dbSequenceUtils.dropSequence("DGB_TEST_SEQ3"); //$NON-NLS-1$
			boolean value = dbSequenceUtils.existSequence("DGB_TEST_SEQ3"); //$NON-NLS-1$
			assertEquals(false, value);
			dbSequenceUtils.createSequence("DGB_TEST_SEQ3", 0); //$NON-NLS-1$
			value = dbSequenceUtils.existSequence("DGB_TEST_SEQ3"); //$NON-NLS-1$
			assertEquals(true, value);
		} catch (SQLException e) {
			assertTrue(e.getMessage(), false);
			e.printStackTrace();
		}
	}

}
