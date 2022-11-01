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

/**
 * The Class OAuthUtils.
 */
public class OAuthUtils {

	/** The Constant PARAM_CLIENT_ID. */
	public static final String PARAM_CLIENT_ID = "client_id";
	
	/** The Constant PARAM_REDIRECT_URI. */
	public static final String PARAM_REDIRECT_URI = "redirect_uri";
	
	/** The Constant PARAM_GRANT_TYPE. */
	public static final String PARAM_GRANT_TYPE = "grant_type";
	
	/** The Constant PARAM_RESPONSE_TYPE. */
	public static final String PARAM_RESPONSE_TYPE = "response_type";
	
	/** The Constant PARAM_CODE. */
	public static final String PARAM_CODE = "code";

	/** The Constant VALUE_RESPONSE_TYPE_CODE. */
	public static final String VALUE_RESPONSE_TYPE_CODE = "code";
	
	/** The Constant VALUE_GRANT_TYPE_AUTHORIZATION_CODE. */
	public static final String VALUE_GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	
	/** The Constant OAUTH_CALLBACK_PATH. */
	public static final String OAUTH_CALLBACK_PATH = "/services/v4/oauth/callback";

	/**
	 * Gets the authentication url.
	 *
	 * @return the authentication url
	 */
	public static String getAuthenticationUrl() {
		String redirectUri = getRedirectUri();
		StringBuilder url = new StringBuilder();
		url.append(getOAuthAuthorizeUrl());
		url.append("?").append(PARAM_RESPONSE_TYPE).append("=").append(VALUE_RESPONSE_TYPE_CODE);
		url.append("&").append(PARAM_CLIENT_ID).append("=").append(getOAuthClientId());
		url.append("&").append(PARAM_REDIRECT_URI).append("=").append(redirectUri);
		return url.toString();
	}

	/**
	 * Gets the token url.
	 *
	 * @param code the code
	 * @return the token url
	 */
	public static String getTokenUrl(String code) {
		StringBuilder tokenUrl = new StringBuilder();
		tokenUrl.append(getOAuthTokenUrl());
		tokenUrl.append("?").append(PARAM_GRANT_TYPE).append("=").append(VALUE_GRANT_TYPE_AUTHORIZATION_CODE);
		tokenUrl.append("&").append(PARAM_CODE).append("=").append(code);
		tokenUrl.append("&").append(PARAM_CLIENT_ID).append("=").append(getOAuthClientId());
		tokenUrl.append("&").append(PARAM_REDIRECT_URI).append("=").append(getRedirectUri());
		return tokenUrl.toString();
	}

	/**
	 * Gets the o auth authorization header.
	 *
	 * @return the o auth authorization header
	 */
	public static String getOAuthAuthorizationHeader() {
		return "Basic " + encodeBase64(getOAuthClientId() + ":" + getOAuthClientSecret());
	}

	/**
	 * Gets the o auth authorize url.
	 *
	 * @return the o auth authorize url
	 */
	public static String getOAuthAuthorizeUrl() {
		return Configuration.get(DIRIGIBLE_OAUTH_AUTHORIZE_URL);
	}

	/**
	 * Gets the o auth token url.
	 *
	 * @return the o auth token url
	 */
	public static String getOAuthTokenUrl() {
		return Configuration.get(DIRIGIBLE_OAUTH_TOKEN_URL);
	}

	/**
	 * Gets the o auth client id.
	 *
	 * @return the o auth client id
	 */
	public static String getOAuthClientId() {
		return Configuration.get(DIRIGIBLE_OAUTH_CLIENT_ID);
	}

	/**
	 * Gets the o auth client secret.
	 *
	 * @return the o auth client secret
	 */
	public static String getOAuthClientSecret() {
		return Configuration.get(DIRIGIBLE_OAUTH_CLIENT_SECRET);
	}

	/**
	 * Gets the o auth verification key.
	 *
	 * @return the o auth verification key
	 */
	public static String getOAuthVerificationKey() {
		return Configuration.get(DIRIGIBLE_OAUTH_VERIFICATION_KEY);
	}

	/**
	 * Gets the o auth application host.
	 *
	 * @return the o auth application host
	 */
	public static String getOAuthApplicationHost() {
		return Configuration.get(DIRIGIBLE_OAUTH_APPLICATION_HOST);
	}

	/**
	 * Gets the o auth application name.
	 *
	 * @return the o auth application name
	 */
	public static String getOAuthApplicationName() {
		return Configuration.get(DIRIGIBLE_OAUTH_APPLICATION_NAME, "");
	}

	/**
	 * Gets the o auth issuer.
	 *
	 * @return the o auth issuer
	 */
	public static String getOAuthIssuer() {
		return Configuration.get(DIRIGIBLE_OAUTH_ISSUER, "");
	}

	/**
	 * Gets the redirect uri.
	 *
	 * @return the redirect uri
	 */
	public static String getRedirectUri() {
		return getOAuthApplicationHost() + OAUTH_CALLBACK_PATH;
	};

	/**
	 * Encode base 64.
	 *
	 * @param value the value
	 * @return the string
	 */
	private static String encodeBase64(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

}