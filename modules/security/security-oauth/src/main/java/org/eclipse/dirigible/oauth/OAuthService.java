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
package org.eclipse.dirigible.oauth;


import static org.eclipse.dirigible.oauth.filters.AbstractOAuthFilter.INITIAL_REQUEST_PATH_COOKIE;
import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.dirigible.api.v3.http.HttpClientFacade;
import org.eclipse.dirigible.api.v3.http.client.HttpClientHeader;
import org.eclipse.dirigible.api.v3.http.client.HttpClientParam;
import org.eclipse.dirigible.api.v3.http.client.HttpClientProxyUtils;
import org.eclipse.dirigible.api.v3.http.client.HttpClientRequestOptions;
import org.eclipse.dirigible.api.v3.http.client.HttpClientResponse;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.oauth.utils.JwtUtils;
import org.eclipse.dirigible.oauth.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;

/**
 * Front facing REST service serving the Git commands.
 */
@Path("/oauth")
@Api(value = "OAuth")
public class OAuthService extends AbstractRestService implements IRestService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(OAuthService.class);

	/** The Constant AUTHORIZATION_HEADER. */
	private static final String AUTHORIZATION_HEADER = "Authorization";

	/** The request. */
	@Context
	private HttpServletRequest request;

	/** The response. */
	@Context
	private HttpServletResponse response;

	/** The Constant DIRIGIBLE_OAUTH_ENABLED. */
	public static final String DIRIGIBLE_OAUTH_ENABLED = "DIRIGIBLE_OAUTH_ENABLED";
	
	/** The Constant DIRIGIBLE_OAUTH_PREFIX. */
	public static final String DIRIGIBLE_OAUTH_PREFIX = "DIRIGIBLE_OAUTH_PREFIX";
	
	/** The Constant DIRIGIBLE_OAUTH_AUTHORIZE_URL. */
	public static final String DIRIGIBLE_OAUTH_AUTHORIZE_URL = "DIRIGIBLE_OAUTH_AUTHORIZE_URL";
	
	/** The Constant DIRIGIBLE_OAUTH_TOKEN_URL. */
	public static final String DIRIGIBLE_OAUTH_TOKEN_URL = "DIRIGIBLE_OAUTH_TOKEN_URL";
	
	/** The Constant DIRIGIBLE_OAUTH_CLIENT_ID. */
	public static final String DIRIGIBLE_OAUTH_CLIENT_ID = "DIRIGIBLE_OAUTH_CLIENT_ID";
	
	/** The Constant DIRIGIBLE_OAUTH_CLIENT_SECRET. */
	public static final String DIRIGIBLE_OAUTH_CLIENT_SECRET = "DIRIGIBLE_OAUTH_CLIENT_SECRET";
	
	/** The Constant DIRIGIBLE_OAUTH_VERIFICATION_KEY. */
	public static final String DIRIGIBLE_OAUTH_VERIFICATION_KEY = "DIRIGIBLE_OAUTH_VERIFICATION_KEY";

	/** The Constant DIRIGIBLE_OAUTH_VERIFICATION_KEY_EXPONENT. */
	public static final String DIRIGIBLE_OAUTH_VERIFICATION_KEY_EXPONENT = "DIRIGIBLE_OAUTH_VERIFICATION_KEY_EXPONENT";
	
	/** The Constant DIRIGIBLE_OAUTH_APPLICATION_NAME. */
	public static final String DIRIGIBLE_OAUTH_APPLICATION_NAME = "DIRIGIBLE_OAUTH_APPLICATION_NAME";
	
	/** The Constant DIRIGIBLE_OAUTH_APPLICATION_HOST. */
	public static final String DIRIGIBLE_OAUTH_APPLICATION_HOST = "DIRIGIBLE_OAUTH_APPLICATION_HOST";
	
	/** The Constant DIRIGIBLE_OAUTH_ISSUER. */
	public static final String DIRIGIBLE_OAUTH_ISSUER = "DIRIGIBLE_OAUTH_ISSUER";
	
	/** The Constant DIRIGIBLE_OAUTH_CHECK_ISSUER_ENABLED. */
	public static final String DIRIGIBLE_OAUTH_CHECK_ISSUER_ENABLED = "DIRIGIBLE_OAUTH_CHECK_ISSUER_ENABLED";

	/** The Constant DIRIGIBLE_OAUTH_CHECK_AUDIENCE_ENABLED. */
	public static final String DIRIGIBLE_OAUTH_CHECK_AUDIENCE_ENABLED = "DIRIGIBLE_OAUTH_CHECK_AUDIENCE_ENABLED";

	/** The Constant DIRIGIBLE_OAUTH_TOKEN_REQUEST_METHOD. */
	public static final String DIRIGIBLE_OAUTH_TOKEN_REQUEST_METHOD = "DIRIGIBLE_OAUTH_TOKEN_REQUEST_METHOD";

	/**
	 * Clone repository.
	 *
	 * @param code the code
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/callback")
	public void callback(@QueryParam("code") String code) throws ClientProtocolException, IOException{
		AccessToken accessToken = getAccessToken(code);
		JwtUtils.setJwt(response, accessToken.getAccessToken());
		String redirectPath = getRedirectPath();
		response.sendRedirect(redirectPath);
	}

	/**
	 * Gets the access token.
	 *
	 * @param code the code
	 * @return the access token
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClientProtocolException the client protocol exception
	 */
	private AccessToken getAccessToken(String code) throws IOException, ClientProtocolException {
		HttpUriRequest request = null;
		HttpClientRequestOptions options = new HttpClientRequestOptions();
		options.getHeaders().add(new HttpClientHeader(AUTHORIZATION_HEADER, OAuthUtils.getOAuthAuthorizationHeader()));

		switch (Configuration.get(DIRIGIBLE_OAUTH_TOKEN_REQUEST_METHOD, "GET").toUpperCase()) {
		case "GET":
			request = HttpClientFacade.createGetRequest(OAuthUtils.getTokenUrl(code), options);
			break;
		case "POST":
			options.getParams().add(new HttpClientParam(OAuthUtils.PARAM_GRANT_TYPE, OAuthUtils.VALUE_GRANT_TYPE_AUTHORIZATION_CODE));
			options.getParams().add(new HttpClientParam(OAuthUtils.PARAM_CODE, code));
			options.getParams().add(new HttpClientParam(OAuthUtils.PARAM_REDIRECT_URI, OAuthUtils.getRedirectUri()));
			request = HttpClientFacade.createPostRequest(OAuthUtils.getOAuthTokenUrl(), options);
			break;
		default:
			throw new IllegalArgumentException("Unsupported OAuth Token Request Method");
		}

		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(false);
		CloseableHttpResponse httpClientResponse = httpClient.execute(request);
		HttpClientResponse clientResponse = HttpClientFacade.processHttpClientResponse(httpClientResponse, false);

		return GsonHelper.GSON.fromJson(clientResponse.getText(), AccessToken.class);
	}

	private String getRedirectPath() {
		String redirectPath = "/";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie: cookies) {
				if (cookie.getName().equals(INITIAL_REQUEST_PATH_COOKIE) && cookie.getValue() != null && !cookie.getValue().equals("")) {
					redirectPath = cookie.getValue();
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}

			}
		}
		return redirectPath;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return OAuthService.class;
	}
	
	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
