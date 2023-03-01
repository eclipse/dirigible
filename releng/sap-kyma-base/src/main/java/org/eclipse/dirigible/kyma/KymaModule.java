/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.kyma;

import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KymaModule extends AbstractDirigibleModule {

	private static final Logger logger = LoggerFactory.getLogger(KymaModule.class);

	private static final String MODULE_NAME = "Kyma Module";

	private static final String ENV_URL = "url";
	private static final String ENV_URI = "uri";
	private static final String ENV_CLIENT_ID = "clientid";
	private static final String ENV_CLIENT_SECRET = "clientsecret";
	private static final String ENV_VERIFICATION_KEY = "verificationkey";
	private static final String ENV_XS_APP_NAME = "xsappname";
	private static final String ENV_DIRIGIBLE_HOST = "DIRIGIBLE_HOST";
	private static final String ENV_TOKEN_SERVICE_URL = "token_service_url";

	private static final String OAUTH_AUTHORIZE = "/oauth/authorize";
	private static final String OAUTH_TOKEN = "/oauth/token";

	public static final String DIRIGIBLE_DESTINATION_PREFIX = "DIRIGIBLE_DESTINATION_PREFIX";
	public static final String DIRIGIBLE_DESTINATION_CLIENT_ID = "DIRIGIBLE_DESTINATION_CLIENT_ID";
	public static final String DIRIGIBLE_DESTINATION_CLIENT_SECRET = "DIRIGIBLE_DESTINATION_CLIENT_SECRET";
	public static final String DIRIGIBLE_DESTINATION_URL = "DIRIGIBLE_DESTINATION_URL";
	public static final String DIRIGIBLE_DESTINATION_URI = "DIRIGIBLE_DESTINATION_URI";

	public static final String DEFAULT_DESTINATION_PREFIX = "destination_";

	public static final String DIRIGIBLE_CONNECTIVITY_PREFIX = "DIRIGIBLE_CONNECTIVITY_PREFIX";
	public static final String DIRIGIBLE_CONNECTIVITY_CLIENT_ID = "DIRIGIBLE_CONNECTIVITY_CLIENT_ID";
	public static final String DIRIGIBLE_CONNECTIVITY_CLIENT_SECRET = "DIRIGIBLE_CONNECTIVITY_CLIENT_SECRET";
	public static final String DIRIGIBLE_CONNECTIVITY_URL = "DIRIGIBLE_CONNECTIVITY_URL";
	public static final String DIRIGIBLE_CONNECTIVITY_URI = "DIRIGIBLE_CONNECTIVITY_URI";
	public static final String DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_HOST = "DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_HOST";
	public static final String DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_HTTP_PORT = "DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_HTTP_PORT";
	public static final String DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_LDAP_PORT = "DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_LDAP_PORT";
	public static final String DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_PORT = "DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_PORT";
	public static final String DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_RFC_PORT = "DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_RFC_PORT";
	public static final String DIRIGIBLE_CONNECTIVITY_ONPREMISE_SOCKS5_PROXY_PORT = "DIRIGIBLE_CONNECTIVITY_ONPREMISE_SOCKS5_PROXY_PORT";

	public static final String DEFAULT_CONNECTIVITY_PREFIX = "connectivity_";

	private static final String ERROR_MESSAGE_NO_OAUTH_CONFIGURATION = "No OAuth configuration provided";
	private static final String WARN_MESSAGE_NO_DESTINATION_CONFIGURATION = "No Destination configuration provided";
	private static final String WARN_MESSAGE_NO_CONNECTIVITY_CONFIGURATION = "No Connectivity configuration provided";


	@Override
	public int getPriority() {
		// Set to higher priority, as this module will set security, database, etc. related configuration properties 
		return PRIORITY_CONFIGURATION;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.module.AbstractDirigibleModule#getName()
	 */
	@Override
	public void configure() {
		Configuration.loadModuleConfig("/dirigible-kyma.properties");
		configureOAuth();
		configureDestination();
		configureConnectivity();
	}

	private void configureOAuth() {
		String oauthPrefix = Configuration.get(OAuthService.DIRIGIBLE_OAUTH_PREFIX, "");

		String url = getEnvWithPrefix(oauthPrefix, ENV_URL);
		String authorizeUrl = url != null ? url + OAUTH_AUTHORIZE : null;
		String tokenUrl = url != null ? url + OAUTH_TOKEN : null;
		String clientId = getEnvWithPrefix(oauthPrefix, ENV_CLIENT_ID);
		String clientSecret = getEnvWithPrefix(oauthPrefix, ENV_CLIENT_SECRET);
		String verificationKey = getEnvWithPrefix(oauthPrefix, ENV_VERIFICATION_KEY);
		String applicationName = getEnvWithPrefix(oauthPrefix, ENV_XS_APP_NAME);
		String applicationHost = Configuration.get(ENV_DIRIGIBLE_HOST);

		if (url == null || clientId == null || clientSecret == null || verificationKey == null || applicationHost == null) {
			if (logger.isErrorEnabled()) {logger.error(ERROR_MESSAGE_NO_OAUTH_CONFIGURATION);}
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

	private void configureDestination() {
		String destinationPrefix = Configuration.get(DIRIGIBLE_DESTINATION_PREFIX, DEFAULT_DESTINATION_PREFIX);

		String clientId = getEnvWithPrefix(destinationPrefix, ENV_CLIENT_ID);
		String clientSecret = getEnvWithPrefix(destinationPrefix, ENV_CLIENT_SECRET);
		String url = getEnvWithPrefix(destinationPrefix, ENV_URL);
		String uri = getEnvWithPrefix(destinationPrefix, ENV_URI);

		if (clientId != null && clientSecret != null && url != null && uri != null) {
			Configuration.setIfNull(DIRIGIBLE_DESTINATION_CLIENT_ID, clientId);
			Configuration.setIfNull(DIRIGIBLE_DESTINATION_CLIENT_SECRET, clientSecret);
			Configuration.setIfNull(DIRIGIBLE_DESTINATION_URL, url);
			Configuration.setIfNull(DIRIGIBLE_DESTINATION_URI, uri);
		} else {
			if (logger.isWarnEnabled()) {logger.warn(WARN_MESSAGE_NO_DESTINATION_CONFIGURATION);}
		}

	}

	private void configureConnectivity() {
		String connectivityPrefix = Configuration.get(DIRIGIBLE_CONNECTIVITY_PREFIX, DEFAULT_CONNECTIVITY_PREFIX);

		String clientId = getEnvWithPrefix(connectivityPrefix, ENV_CLIENT_ID);
		String clientSecret = getEnvWithPrefix(connectivityPrefix, ENV_CLIENT_SECRET);
		String url = getEnvWithPrefix(connectivityPrefix, ENV_TOKEN_SERVICE_URL);

		if (clientId != null && clientSecret != null && url != null) {
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_CLIENT_ID, clientId);
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_CLIENT_SECRET, clientSecret);
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_URL, url);
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_HOST, "connectivity-proxy.kyma-system.svc.cluster.local");
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_HTTP_PORT, "20003");
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_LDAP_PORT, "20001");
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_PORT, "20003");
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_ONPREMISE_PROXY_RFC_PORT, "20001");
			Configuration.setIfNull(DIRIGIBLE_CONNECTIVITY_ONPREMISE_SOCKS5_PROXY_PORT, "20004");
		} else {
			if (logger.isWarnEnabled()) {logger.warn(WARN_MESSAGE_NO_CONNECTIVITY_CONFIGURATION);}
		}

	}

	private String getEnvWithPrefix(String prefix, String variableName) {
		return prefix.isEmpty() ? Configuration.get(variableName) : Configuration.get(String.format("%s%s", prefix, variableName));
	}

	@Override
	public String getName() {
		return MODULE_NAME;
	}

}
