/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.cli.apis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ImportProjectAPI {

	private static final Logger logger = Logger.getLogger(ImportProjectAPI.class.getCanonicalName());
	private static final String PART_NAME = "bin";

	public static void importProject(RequestConfig config, String url, InputStream in, String filename, Map<String, String> headers) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.addPart(PART_NAME, new InputStreamBody(in, filename));

		HttpPost postRequest = new HttpPost(url);
		postRequest.setEntity(entityBuilder.build());
		addHeaders(postRequest, headers);
		if (config != null) {
			postRequest.setConfig(config);
		}
		executeRequest(httpClient, postRequest);
	}

	private static void addHeaders(HttpPost postRequest, Map<String, String> headers) {
		for(Entry<String, String> header : headers.entrySet()) {
			postRequest.setHeader(header.getKey(), header.getValue());
		}
	}

	private static void executeRequest(CloseableHttpClient httpClient, HttpPost postRequest) throws IOException, ClientProtocolException {
		try {
			CloseableHttpResponse response = httpClient.execute(postRequest);
			try {
				logger.log(Level.INFO, "----------------------------------------");
				logger.log(Level.INFO, response.getStatusLine().toString());
				HttpEntity resultEntity = response.getEntity();
				if (resultEntity != null) {
					logger.log(Level.INFO, "Response content length: " + resultEntity.getContentLength());
				}
				EntityUtils.consume(resultEntity);
			} finally {
				response.close();
			}
		} finally {
			httpClient.close();
		}
	}
}
