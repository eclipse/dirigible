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

package org.eclipse.dirigible.runtime.filter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

	public XSSRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		String parameter = super.getParameter(name);
		return XSSUtils.stripXSS(parameter);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		return stripXSS(values);
	}

	@Override
	public String getQueryString() {
		String query = super.getQueryString();
		return XSSUtils.stripXSS(query);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = super.getParameterMap();
		return stripXSS(parameterMap);
	}

	@Override
	public String getHeader(String name) {
		String header = super.getHeader(name);
		return XSSUtils.stripXSS(header);
	}

	private String[] stripXSS(String[] values) {
		String encodedValues[] = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			encodedValues[i] = XSSUtils.stripXSS(values[i]);
		}
		return encodedValues;
	}

	private Map<String, String[]> stripXSS(Map<String, String[]> parameterMap) {
		Map<String, String[]> encodedMap = new TreeMap<String, String[]>();
		for (Entry<String, String[]> next : parameterMap.entrySet()) {
			String key = next.getKey();
			String[] values = next.getValue();
			encodedMap.put(XSSUtils.stripXSS(key), stripXSS(values));
		}
		return encodedMap;
	}
}
