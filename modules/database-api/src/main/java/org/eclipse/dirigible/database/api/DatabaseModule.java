/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;

/**
 * Module for managing Local Datasource instantiation and binding
 */
public class DatabaseModule extends AbstractDirigibleModule {
	
	private static final ServiceLoader<IDatabase> DATABASES = ServiceLoader.load(IDatabase.class);
	
	@Override
	protected void configure() {
		Configuration.load("/dirigible-database.properties");
		
		String databaseProvider = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_PROVIDER, IDatabase.DIRIGIBLE_DATABASE_PROVIDER_LOCAL);
		for (IDatabase database : DATABASES) {
			if (database.getType().equals(databaseProvider)) {
				bind(IDatabase.class).toInstance(database);
				bind(DataSource.class).toInstance(database.getDataSource());
			}
		}
	}
}
