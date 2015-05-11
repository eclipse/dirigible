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

package org.eclipse.dirigible.repository.db;

import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryProvider;

public class DBRepositoryProvider implements IRepositoryProvider {

	private static final String PARAM_DATASOURCE = "datasource";
	private static final String PARAM_USER = "user";
	private static final String PARAM_RECREATE = "recreate";

	@Override
	public IRepository createRepository(Map<String, Object> parameters) {
		DataSource dataSource = (DataSource) parameters.get(PARAM_DATASOURCE);
		String user = (String) parameters.get(PARAM_USER);
		Boolean forceRecreate = (Boolean) parameters.get(PARAM_RECREATE);
		return new DBRepository(dataSource, user, forceRecreate);
	}

}
