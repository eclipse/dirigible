/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.cf;

import java.text.MessageFormat;

import org.eclipse.dirigible.cf.utils.CloudFoundryUtils;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.HanaDbEnv;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.HanaSchemaEnv;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.PostgreDbEnv;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.PostgreHyperscalerDbEnv;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.XsuaaEnv;
import org.eclipse.dirigible.cf.utils.CloudFoundryUtils.XsuaaEnv.XsuaaCredentialsEnv;
import org.eclipse.dirigible.cms.api.ICmsProvider;
import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFoundryModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(CloudFoundryModule.class);

	private static final String MODULE_NAME = "Cloud Foundry Module";

	private static final String ERROR_MESSAGE_NO_XSUAA = "No XSUAA service instance is bound";

	private static final String DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE = "DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE";

	private static final String DATABASE_POSTGRE = "POSTGRE";
	private static final String DATABASE_HANA = "HANA";
	private static final String DATABASE_POSTGRE_DRIVER = "org.postgresql.Driver";
	private static final String DATABASE_HANA_DRIVER = "com.sap.db.jdbc.Driver";

	private static final String OAUTH_AUTHORIZE = "/oauth/authorize";
	private static final String OAUTH_TOKEN = "/oauth/token";
	private static final String ISSUER_PATTERN = "http://{0}.localhost:8080/uaa/oauth/token";

	@Override
	public int getPriority() {
		// Set to higher priority, as this module will set security, database, etc. related configuration properties 
		return HIGH_PRIORITY;
	}

	@Override
	protected void configure() {
		Configuration.loadModuleConfig("/dirigible-cloud-foundry.properties");
		configureOAuth();
		configureDatasource();
	}

	private void configureOAuth() {
		XsuaaEnv xsuaaEnv = CloudFoundryUtils.getXsuaaEnv();
		if (xsuaaEnv == null || xsuaaEnv.getCredentials() == null) {
			logger.error(ERROR_MESSAGE_NO_XSUAA);
			throw new InvalidStateException(ERROR_MESSAGE_NO_XSUAA);
		}
		XsuaaCredentialsEnv xsuaaCredentials = xsuaaEnv.getCredentials();
		
		String url = xsuaaCredentials.getUrl();
		String authorizeUrl = url + OAUTH_AUTHORIZE;
		String tokenUrl = url + OAUTH_TOKEN;
		String clientId = xsuaaCredentials.getClientId();
		String clientSecret = xsuaaCredentials.getClientSecret();
		String verificationKey = xsuaaCredentials.getVerificationKey();
		String applicationName = xsuaaCredentials.getApplicationName();
		String applicationHost = CloudFoundryUtils.getApplicationHost();
		String issuer = MessageFormat.format(ISSUER_PATTERN, xsuaaCredentials.getIdentityZone());
		
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_AUTHORIZE_URL, authorizeUrl);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_TOKEN_URL, tokenUrl);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_CLIENT_ID, clientId);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_CLIENT_SECRET, clientSecret);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_VERIFICATION_KEY, verificationKey);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_APPLICATION_NAME, applicationName);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_APPLICATION_HOST, applicationHost);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_ISSUER, issuer);
	}

	private void configureDatasource() {
		bindPostgreDb(CloudFoundryUtils.getPostgreDbEnv());
		bindPostgreHyperscalerDb(CloudFoundryUtils.getPostgreHyperscalerDbEnv());
		bindHanaDb(CloudFoundryUtils.getHanaDbEnv());
		bindHanaSchema(CloudFoundryUtils.getHanaSchemaEnv());
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
		Configuration.set(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "true");
		return true;
	}

	private boolean bindPostgreHyperscalerDb(PostgreHyperscalerDbEnv env) {
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
		Configuration.set(IDataStructureModel.DIRIGIBLE_DATABASE_NAMES_CASE_SENSITIVE, "true");

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

		// CMS properties
		String cmsDatabaseDatasourceName = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME);
		if (cmsDatabaseDatasourceName == null || cmsDatabaseDatasourceName.equals("")) {
			cmsDatabaseDatasourceName = name;
		}

		String cmsDatabaseDatasourceType = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE);
		if (cmsDatabaseDatasourceType == null || cmsDatabaseDatasourceType.equals("")) {
			cmsDatabaseDatasourceType = "custom";
		}

		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_PROVIDER, "database");
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME, cmsDatabaseDatasourceName);
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE, cmsDatabaseDatasourceType);
		return true;
	}

	private boolean bindHanaSchema(HanaSchemaEnv env) {
		if (env == null) {
			return false;
		}

		String name = DATABASE_HANA;
		String url = env.getCredentials().getUrl();
		String username = env.getCredentials().getUsername();
		String password = env.getCredentials().getPassword();
		String driver = DATABASE_HANA_DRIVER;

		setDatabaseProperties(name, url, driver, username, password);

		String maxConnectionsCount = Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT, "32");
		Configuration.set(IDatabase.DIRIGIBLE_DATABASE_DEFAULT_MAX_CONNECTIONS_COUNT, maxConnectionsCount);
		Configuration.set(DIRIGIBLE_MESSAGING_USE_DEFAULT_DATABASE, "false");

		// CMS properties
		String cmsDatabaseDatasourceName = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME);
		if (cmsDatabaseDatasourceName == null || cmsDatabaseDatasourceName.equals("")) {
			cmsDatabaseDatasourceName = name;
		}

		String cmsDatabaseDatasourceType = Configuration.get(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE);
		if (cmsDatabaseDatasourceType == null || cmsDatabaseDatasourceType.equals("")) {
			cmsDatabaseDatasourceType = "custom";
		}

		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_PROVIDER, "database");
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_NAME, cmsDatabaseDatasourceName);
		Configuration.set(ICmsProvider.DIRIGIBLE_CMS_DATABASE_DATASOURCE_TYPE, cmsDatabaseDatasourceType);
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
	}

	private boolean haveCustomDatasourceConfiguration() {
		return Configuration.get(IDatabase.DIRIGIBLE_DATABASE_PROVIDER, "").equals("custom")
				&& !Configuration.get(IDatabase.DIRIGIBLE_DATABASE_CUSTOM_DATASOURCES, "").equals("")
				&& !Configuration.get(IDatabase.DIRIGIBLE_DATABASE_DATASOURCE_NAME_DEFAULT, "").equals("");
	}
	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
