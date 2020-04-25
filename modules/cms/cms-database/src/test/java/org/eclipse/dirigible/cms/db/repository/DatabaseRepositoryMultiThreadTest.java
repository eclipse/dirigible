/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cms.db.repository;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.generic.RepositoryGenericMultiThreadTest;

/**
 * The Class DatabaseRepositoryMultiThreadTest.
 */
public class DatabaseRepositoryMultiThreadTest extends RepositoryGenericMultiThreadTest {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.repository.generic.RepositoryGenericMultiThreadTest#getNewRepository(java.lang.String)
	 */
	@Override
	protected IRepository getNewRepository(String user) {
		// TODO uncomment only for manual tests
		// return new LocalRepository(user);
		return null;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		DatabaseRepositoryMultiThreadTest localMultiThreadTest = new DatabaseRepositoryMultiThreadTest();
		localMultiThreadTest.multi();
	}

}
