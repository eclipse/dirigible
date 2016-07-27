/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.common.dual;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;

/**
 * This class is supposed to be used in RCP environment only
 * A class with the same fully qualified name exist in *.rap plugin for use in Web environment
 */
public class DualParameters {

	public static final String RUNTIME_URL_DEFAULT = ""; //$NON-NLS-1$
	public static final String SERVICES_URL_DEFAULT = ""; //$NON-NLS-1$

	public static final String HC_LOCAL_HTTP_PORT = "HC_LOCAL_HTTP_PORT"; //$NON-NLS-1$
	public static final String HC_APPLICATION_URL = "HC_APPLICATION_URL"; //$NON-NLS-1$
	public static final String HC_APPLICATION = "HC_APPLICATION"; //$NON-NLS-1$
	public static final String HC_ACCOUNT = "HC_ACCOUNT"; //$NON-NLS-1$
	public static final String HC_REGION = "HC_REGION"; //$NON-NLS-1$
	public static final String HC_HOST = "HC_HOST"; //$NON-NLS-1$

	public static final String GUEST_USER = "local"; //$NON-NLS-1$

	private static final Map<String, Object> SESSION_MOCK = Collections.synchronizedMap(new HashMap<String, Object>());

	public static void initSystemParameters() {
		String parameterHC_HOST = System.getProperty(HC_HOST);
		SESSION_MOCK.put(HC_HOST, parameterHC_HOST);
		String parameterHC_REGION = System.getProperty(HC_REGION);
		SESSION_MOCK.put(HC_REGION, parameterHC_REGION);
		String parameterHC_ACCOUNT = System.getProperty(HC_ACCOUNT);
		SESSION_MOCK.put(HC_ACCOUNT, parameterHC_ACCOUNT);
		String parameterHC_APPLICATION = System.getProperty(HC_APPLICATION);
		SESSION_MOCK.put(HC_APPLICATION, parameterHC_APPLICATION);
		String parameterHC_APPLICATION_URL = System.getProperty(HC_APPLICATION_URL);
		SESSION_MOCK.put(HC_APPLICATION_URL, parameterHC_APPLICATION_URL);
		String parameterHC_LOCAL_HTTP_PORT = System.getProperty(HC_LOCAL_HTTP_PORT);
		SESSION_MOCK.put(HC_LOCAL_HTTP_PORT, parameterHC_LOCAL_HTTP_PORT);
	}

	public static String get(String name) {
		String parameter = (String) SESSION_MOCK.get(name);
		return parameter;
	}

	public static Object getObject(String name) {
		Object parameter = SESSION_MOCK.get(name);
		return parameter;
	}

	public static void set(String name, String value) {
		SESSION_MOCK.put(name, value);
	}

	public static void setObject(String name, Object value) {
		SESSION_MOCK.put(name, value);
	}

	public static String getRuntimeUrl() {
		String runtimeUrl = get(ICommonConstants.INIT_PARAM_RUNTIME_URL);
		if (runtimeUrl == null) {
			runtimeUrl = RUNTIME_URL_DEFAULT;
		}
		return runtimeUrl;
	}

	public static String getServicesUrl() {
		String runtimeUrl = get(ICommonConstants.INIT_PARAM_RUNTIME_URL);
		if (runtimeUrl == null) {
			runtimeUrl = RUNTIME_URL_DEFAULT;
		}
		String servicesUrl = get(ICommonConstants.INIT_PARAM_SERVICES_URL);
		if (servicesUrl == null) {
			servicesUrl = SERVICES_URL_DEFAULT;
		}

		return runtimeUrl + servicesUrl;
	}

	public static Object getService(Class clazz) {
		// return RWT.getClient().getService(clazz);
		return null;
	}

	public static String getContextPath() {
		// return RWT.getRequest().getContextPath();
		return null;
	}

	public static HttpServletRequest getRequest() {
		// return RWT.getRequest();
		return null;
	}

	public static String getUserName(HttpServletRequest request) {
		String user = null;
		// try {
		// user = RWT.getRequest().getRemoteUser();
		//
		// } catch (Throwable t) {
		// user = GUEST_USER;
		// }
		if (user == null) {
			user = GUEST_USER;
		}
		return user;
	}

	public static Boolean isRolesEnabled() {
		Boolean rolesEnabled = Boolean.parseBoolean(get(ICommonConstants.INIT_PARAM_ENABLE_ROLES));
		return rolesEnabled;
	}

	public static boolean isUserInRole(String role) {
		// if (isRolesEnabled()) {
		// return RWT.getRequest().isUserInRole(role);
		// } else {
		return true;
		// }
	}

	public static String getSessionId() {
		// String sessionId = RWT.getRequest().getSession(true).getId();
		// return sessionId;
		return "LOCAL";
	}

	public static final boolean isRAP() {
		return false;
	}

	public static final boolean isRCP() {
		return true;
	}
}
