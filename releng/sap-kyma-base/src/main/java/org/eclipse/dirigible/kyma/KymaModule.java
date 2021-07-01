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
package org.eclipse.dirigible.kyma;

import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.database.api.IDatabase;
import org.eclipse.dirigible.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KymaModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(KymaModule.class);

	private static final String MODULE_NAME = "Kyma Module";

	private static final String ENV_URL = "url";
	private static final String ENV_CLIENT_ID = "clientid";
	private static final String ENV_CLIENT_SECRET = "clientsecret";
	private static final String ENV_VERIFICATION_KEY = "verificationkey";
	private static final String ENV_XS_APP_NAME = "xsappname";
	private static final String ENV_DIRIGIBLE_HOST = "DIRIGIBLE_HOST";

	private static final String OAUTH_AUTHORIZE = "/oauth/authorize";
	private static final String OAUTH_TOKEN = "/oauth/token";

	private static final String ERROR_MESSAGE_NO_OAUTH_CONFIGURATION = "No OAuth configuration provided";

	@Override
	public int getPriority() {
		// Set to higher priority, as this module will set security, database, etc. related configuration properties 
		return HIGH_PRIORITY;
	}

	@Override
	protected void configure() {
		Configuration.loadModuleConfig("/dirigible-kyma.properties");
		configureOAuth();
	}

	private void configureOAuth() {
		String url = Configuration.get(ENV_URL);
		String authorizeUrl = url != null ? url + OAUTH_AUTHORIZE : null;
		String tokenUrl = url != null ? url + OAUTH_TOKEN : null;
		String clientId = Configuration.get(ENV_CLIENT_ID);
		String clientSecret = Configuration.get(ENV_CLIENT_SECRET);
		String verificationKey = Configuration.get(ENV_VERIFICATION_KEY);
		String applicationName = Configuration.get(ENV_XS_APP_NAME);
		String applicationHost = Configuration.get(ENV_DIRIGIBLE_HOST);

		if (url == null || clientId == null || clientSecret == null || verificationKey == null || applicationHost == null) {
			logger.error(ERROR_MESSAGE_NO_OAUTH_CONFIGURATION);
			throw new InvalidStateException(ERROR_MESSAGE_NO_OAUTH_CONFIGURATION);
		}

		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_AUTHORIZE_URL, authorizeUrl);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_TOKEN_URL, tokenUrl);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_CLIENT_ID, clientId);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_CLIENT_SECRET, clientSecret);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_VERIFICATION_KEY, verificationKey);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_APPLICATION_NAME, applicationName);
		Configuration.setIfNull(OAuthService.DIRIGIBLE_OAUTH_APPLICATION_HOST, applicationHost);
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
