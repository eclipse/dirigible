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
package org.eclipse.dirigible.kyma.oauth;


import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.eclipse.dirigible.api.v3.http.HttpClientFacade;
import org.eclipse.dirigible.api.v3.http.client.HttpClientHeader;
import org.eclipse.dirigible.api.v3.http.client.HttpClientProxyUtils;
import org.eclipse.dirigible.api.v3.http.client.HttpClientRequestOptions;
import org.eclipse.dirigible.api.v3.http.client.HttpClientResponse;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.kyma.utils.JwtUtils;
import org.eclipse.dirigible.kyma.utils.KymaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;

/**
 * Front facing REST service serving the Git commands.
 */
@Singleton
@Path("/oauth")
@Api(value = "OAuth")
public class OAuthService extends AbstractRestService implements IRestService {
	
	private static final Logger logger = LoggerFactory.getLogger(OAuthService.class);

	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Context
	private HttpServletResponse response;

	/**
	 * Clone repository.
	 *
	 * @param code the code
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@GET
	@Path("/callback")
	public void callback(@QueryParam("code") String code) throws ClientProtocolException, IOException{
		XsuaaToken xsuaaToken = getXsuaaToken(code);
		JwtUtils.setJwt(response, xsuaaToken.getAccessToken());
		response.sendRedirect("/");
	}

	private XsuaaToken getXsuaaToken(String code) throws IOException, ClientProtocolException {
		String tokenUrl = KymaUtils.getOAuthTokenUrl(code);

		HttpClientRequestOptions options = new HttpClientRequestOptions();
		options.getHeaders().add(new HttpClientHeader(AUTHORIZATION_HEADER, KymaUtils.getOAuthAuthorizationHeader()));

		HttpGet httpGet = HttpClientFacade.createGetRequest(tokenUrl, options);
		CloseableHttpClient httpClient = HttpClientProxyUtils.getHttpClient(false);
		CloseableHttpResponse httpClientResponse = httpClient.execute(httpGet);
		HttpClientResponse clientResponse = HttpClientFacade.processHttpClientResponse(httpClientResponse, false);

		return GsonHelper.GSON.fromJson(clientResponse.getText(), XsuaaToken.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.IRestService#getType()
	 */
	@Override
	public Class<? extends IRestService> getType() {
		return OAuthService.class;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
