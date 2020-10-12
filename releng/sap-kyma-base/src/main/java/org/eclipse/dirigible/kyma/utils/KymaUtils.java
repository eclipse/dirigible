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
package org.eclipse.dirigible.kyma.utils;

import org.eclipse.dirigible.api.v3.core.EnvFacade;
import org.eclipse.dirigible.api.v3.utils.Base64Facade;

public class KymaUtils {

	private static final String PARAM_CLIENT_ID = "client_id";
	private static final String PARAM_REDIRECT_URI = "redirect_uri";
	private static final String PARAM_GRANT_TYPE = "grant_type";
	private static final String PARAM_RESPONSE_TYPE = "response_type";
	private static final String PARAM_CODE = "code";

	private static final String RESPONSE_TYPE_COE = "code";
	private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	
	private static final String OAUTH_AUTHORIZE = "/oauth/authorize";
	private static final String OAUTH_TOKEN = "/oauth/token";
	private static final String OAUTH_CALLBACK_PATH = "/services/v4/oauth/callback";
	
	private static final String OAUTH_URL = "url";
	private static final String OAUTH_CLIENT_ID = "clientid";
	private static final String OAUTH_CLIENT_SECRET = "clientsecret";
	private static final String OAUTH_VERIFICATION_KEY = "verificationkey";

	private static final String XS_APP_NAME = "xsappname";
	private static final String DIRIGIBLE_HOST = "DIRIGIBLE_HOST";

	public static String getOAuthTokenUrl(String code) {
		StringBuilder tokenUrl = new StringBuilder();
		tokenUrl.append(getOAuthUrl()).append(OAUTH_TOKEN);
		tokenUrl.append("?").append(PARAM_GRANT_TYPE).append("=").append(GRANT_TYPE_AUTHORIZATION_CODE);
		tokenUrl.append("&").append(PARAM_CODE).append("=").append(code);
		tokenUrl.append("&").append(PARAM_CLIENT_ID).append("=").append(getOAuthClientId());
		tokenUrl.append("&").append(PARAM_REDIRECT_URI).append("=").append(getRedirectUri());
		return tokenUrl.toString();
	}

	public static String getAuthenticationUrl() {
		String redirectUri = getRedirectUri();
		StringBuilder url = new StringBuilder();
		url.append(getOAuthUrl());
		url.append(OAUTH_AUTHORIZE);
		url.append("?").append(PARAM_RESPONSE_TYPE).append("=").append(RESPONSE_TYPE_COE);
		url.append("&").append(PARAM_CLIENT_ID).append("=").append(getOAuthClientId());
		url.append("&").append(PARAM_REDIRECT_URI).append("=").append(redirectUri);
		return url.toString();
	}

	public static String getOAuthAuthorizationHeader() {
		return "Basic " + Base64Facade.encode((getOAuthClientId() + ":" + getOAuthClientSecret()).getBytes());
	}

	public static String getXsAppName() {
		return EnvFacade.get(XS_APP_NAME);
	}

	public static String getOAuthUrl() {
		return EnvFacade.get(OAUTH_URL);
	}

	public static String getOAuthClientId() {
		return EnvFacade.get(OAUTH_CLIENT_ID);
	}

	public static String getOAuthClientSecret() {
		return EnvFacade.get(OAUTH_CLIENT_SECRET);
	}

	public static String getVerificationKey() {
		return EnvFacade.get(OAUTH_VERIFICATION_KEY);
	}

	public static String getDirigibleHost() {
		return EnvFacade.get(DIRIGIBLE_HOST);
	}

	public static String getRedirectUri() {
		return getDirigibleHost() + OAUTH_CALLBACK_PATH;
	};

}