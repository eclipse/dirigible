/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.cf;

import org.eclipse.dirigible.cf.utils.CloudFoundryUtils;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.HanaDbEnv;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.PostgreDbEnv;
import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.core.scheduler.manager.SchedulerManager;
import org.eclipse.dirigible.database.api.IDatabase;

public class CloudFoundryModule extends AbstractDirigibleModule {

	private static final String DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE = "DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE";

	private static final String MODULE_NAME = "Cloud Foundry Module";

	private static final String DATABASE_POSTGRE = "POSTGRE";
	private static final String DATABASE_HANA = "HANA";
	private static final String DATABASE_POSTGRE_DRIVER = "org.postgresql.Driver";
	private static final String DATABASE_HANA_DRIVER = "com.sap.db.jdbc.Driver";

	@Override
	protected void configure() {
		boolean customPostgreDb = bindPostgreDb(CloudFoundryUtils.getPostgreDbEnv());
		boolean customHanaDb = bindHanaDb(CloudFoundryUtils.getHanaDbEnv());
		if (!customPostgreDb && !customHanaDb) {
			Configuration.set(IDatabase.DIRIGIBLE_DATABASE_PROVIDER, "local");
			Configuration.set(ICmsProvider.DIRIGIBLE_CMS_PROVIDER, "internal");
		}
	}

	private boolean bindPostgreDb(PostgreDbEnv env) {
		if (env == null) {
			return false;
		}

		String name = DATABASE_POSTGRE;
		String url = env.getCredentials().getUrl();
		String driver = DATABASE_POSTGRE_DRIVER;
		String username = env.getCredentials().getUsername();
		String password = env.getCredentials().getPassword();

		setDatabaseProperties(name, url, driver, username, password);

		Configuration.set(DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE, "false");
		Configuration.set(SchedulerManager.DIRIGIBLE_SCHEDULER_MEMORY_STORE, "true");
		return true;
	}

	private boolean bindHanaDb(HanaDbEnv env) {
		if (env == null) {
			return false;
		}

		String name = DATABASE_HANA;
		String url = env.getCredentials().getUrl();
		String driver = DATABASE_HANA_DRIVER;

		setDatabaseProperties(name, url, driver);

		String maxConnectionsCount = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT, "32");
		Configuration.set(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT, maxConnectionsCount);
		Configuration.set(DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE, "false");
		return true;
	}

	private void setDatabaseProperties(String name, String url, String driver) {
		setDatabaseProperties(name, url, driver, null, null);
	}

	private void setDatabaseProperties(String name, String url, String driver, String username, String password) {
		String customDatasources = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES, "");
		if (customDatasources != null && !customDatasources.equals("")) {
			customDatasources = customDatasources.concat(",");
		}
		customDatasources = customDatasources.concat(name);

		String datasourceNameDefault = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT);
		if (datasourceNameDefault == null || datasourceNameDefault.equals("")) {
			datasourceNameDefault = name;
		}

		String cmsDatabaseDatasourceName = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME);
		if (cmsDatabaseDatasourceName == null || cmsDatabaseDatasourceName.equals("")) {
			cmsDatabaseDatasourceName = name;
		}

		String cmsDatabaseDatasourceType = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE);
		if (cmsDatabaseDatasourceType == null || cmsDatabaseDatasourceType.equals("")) {
			cmsDatabaseDatasourceType = "custom";
		}

		// Database properties
		Configuration.set(IDatabase.DIRIGIBLE_DATABASE_PROVIDER, "custom");
		Configuration.set(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES, customDatasources);
		Configuration.set(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT, datasourceNameDefault);
		Configuration.set(name + "_URL", url);
		Configuration.set(name + "_DRIVER", driver);
		if (username != null) {
			Configuration.set(name + "_USERNAME", username);
		}
		if (password != null) {
			Configuration.set(name + "_PASSWORD", password);
		}

		// CMS properties
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_PROVIDER, "database");
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME, cmsDatabaseDatasourceName);
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE, cmsDatabaseDatasourceType);
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
