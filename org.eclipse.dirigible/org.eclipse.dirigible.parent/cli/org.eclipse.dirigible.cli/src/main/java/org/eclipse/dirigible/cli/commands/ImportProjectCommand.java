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

package org.eclipse.dirigible.cli.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.config.RequestConfig;

import org.eclipse.dirigible.cli.apis.ImportProjectAPI;
import org.eclipse.dirigible.cli.utils.CommonProperties;
import org.eclipse.dirigible.cli.utils.Utils;

public class ImportProjectCommand implements ICommand, CommonProperties.ImportProjectCommand {

	public void execute(Properties propeties) throws IOException {
		String url = propeties.getProperty(PROPERTY_URL);
		File file = new File(propeties.getProperty(PROPERTY_ARCHIVE));
		InputStream in = new FileInputStream(file);
		RequestConfig config = getRequestConfig(propeties);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(PROPERTY_OVERRIDE, propeties.getProperty(PROPERTY_OVERRIDE));
		ImportProjectAPI.importProject(config, url, in, file.getName(), headers);
	}

	private RequestConfig getRequestConfig(Properties properties) {
		String host = properties.getProperty(PROPERTY_PROXY_HOST);
		String port = properties.getProperty(PROPERTY_PROXY_PORT);
		String scheme = properties.getProperty(PROPERTY_PROXY_SCHEME);
		
		RequestConfig config = null;
		if(!Utils.isEmpty(host) && !Utils.isEmpty(port) && !Utils.isEmpty(scheme)) {
			config = Utils.createRequestProxy(host, Integer.valueOf(port), scheme);
		}
		return config;
	}
}