/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package test.org.eclipse.dirigible.repository.local;

import static org.junit.Assert.fail;

import org.eclipse.dirigible.repository.local.LocalRepository;
import org.junit.Before;

import test.org.eclipse.dirigible.repository.generic.GenericBigTextTest;

public class LocalBigTextTest extends GenericBigTextTest {

	@Before
	public void setUp() {
		try {
			repository = new LocalRepository("test");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
