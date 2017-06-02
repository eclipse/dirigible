/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.master.jar;

import org.eclipse.dirigible.repository.local.LocalRepositoryException;
import org.eclipse.dirigible.repository.master.IMasterRepository;

public class JarMasterRepository extends JarRepository implements IMasterRepository {

	public static final String TYPE = "jar";
	public static final String DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH = "DIRIGIBLE_MASTER_REPOSITORY_JAR_PATH";

	public JarMasterRepository(String zip) throws LocalRepositoryException {
		super(zip);
	}

}
