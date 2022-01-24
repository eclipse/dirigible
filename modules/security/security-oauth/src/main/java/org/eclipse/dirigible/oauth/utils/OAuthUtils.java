/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.oauth.utils;

import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_APPLICATION_HOST;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_APPLICATION_NAME;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_AUTHORIZE_URL;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_CLIENT_ID;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_CLIENT_SECRET;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_ISSUER;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_TOKEN_URL;
import static org.eclipse.dirigible.oauth.OAuthService.DIRIGIBLE_OAUTH_VERIFICATION_KEY;

import java.util.Base64;

import org.eclipse.dirigible.commons.config.Configuration;

public class OAuthUtils {

	private static final String PARAM_CLIENT_ID = "client_id";
	private static final String PARAM_REDIRECT_URI = "redirect_uri";
	private static final String PARAM_GRANT_TYPE = "grant_type";
	private static final String PARAM_RESPONSE_TYPE = "response_type";
	private static final String PARAM_CODE = "code";

	private static final String VALUE_RESPONSE_TYPE_CODE = "code";
	private static final String VALUE_GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	
	private static final String OAUTH_CALLBACK_PATH = "/services/v4/oauth/callback";

	public static String getAuthenticationUrl() {
		String redirectUri = getRedirectUri();
		StringBuilder url = new StringBuilder();
		url.append(getOAuthAuthorizeUrl());
		url.append("?").append(PARAM_RESPONSE_TYPE).append("=").append(VALUE_RESPONSE_TYPE_CODE);
		url.append("&").append(PARAM_CLIENT_ID).append("=").append(getOAuthClientId());
		url.append("&").append(PARAM_REDIRECT_URI).append("=").append(redirectUri);
		return url.toString();
	}

	public static String getTokenUrl(String code) {
		StringBuilder tokenUrl = new StringBuilder();
		tokenUrl.append(getOAuthTokenUrl());
		tokenUrl.append("?").append(PARAM_GRANT_TYPE).append("=").append(VALUE_GRANT_TYPE_AUTHORIZATION_CODE);
		tokenUrl.append("&").append(PARAM_CODE).append("=").append(code);
		tokenUrl.append("&").append(PARAM_CLIENT_ID).append("=").append(getOAuthClientId());
		tokenUrl.append("&").append(PARAM_REDIRECT_URI).append("=").append(getRedirectUri());
		return tokenUrl.toString();
	}

	public static String getOAuthAuthorizationHeader() {
		return "Basic " + encodeBase64(getOAuthClientId() + ":" + getOAuthClientSecret());
	}

	public static String getOAuthAuthorizeUrl() {
		return Configuration.get(DIRIGIBLE_OAUTH_AUTHORIZE_URL);
	}

	public static String getOAuthTokenUrl() {
		return Configuration.get(DIRIGIBLE_OAUTH_TOKEN_URL);
	}

	public static String getOAuthClientId() {
		return Configuration.get(DIRIGIBLE_OAUTH_CLIENT_ID);
	}

	public static String getOAuthClientSecret() {
		return Configuration.get(DIRIGIBLE_OAUTH_CLIENT_SECRET);
	}

	public static String getOAuthVerificationKey() {
		return Configuration.get(DIRIGIBLE_OAUTH_VERIFICATION_KEY);
	}

	public static String getOAuthApplicationHost() {
		return Configuration.get(DIRIGIBLE_OAUTH_APPLICATION_HOST);
	}

	public static String getOAuthApplicationName() {
		return Configuration.get(DIRIGIBLE_OAUTH_APPLICATION_NAME, "");
	}

	public static String getOAuthIssuer() {
		return Configuration.get(DIRIGIBLE_OAUTH_ISSUER, "");
	}

	private static String getRedirectUri() {
		return getOAuthApplicationHost() + OAUTH_CALLBACK_PATH;
	};

	private static String encodeBase64(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

}