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

package org.eclipse.dirigible.cli.utils;

import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;

public class Utils {
	private static final String SPLITTER = "=";

	public static boolean isEmpty(String property) {
		return property == null || property.isEmpty();
	}
	
	public static Properties loadCLIProperties(String[] args) {
		Properties propeties = new Properties();
		for (String next : args) {
			String[] properties = next.split(SPLITTER);
			propeties.put(properties[0], properties[1]);
		}
		return propeties;
	}

	public static RequestConfig createRequestProxy(String host, int port, String scheme) {
		Builder resultConfigBuilder = RequestConfig.custom();
		resultConfigBuilder.setProxy(new HttpHost(host, port, scheme));
		RequestConfig config = resultConfigBuilder.build();
		return config;
	}
}
