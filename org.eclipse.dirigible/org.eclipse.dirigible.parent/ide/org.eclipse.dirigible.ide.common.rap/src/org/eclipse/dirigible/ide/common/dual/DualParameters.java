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

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.ext.utils.EnvUtils;
import org.eclipse.dirigible.repository.ext.utils.RequestUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientService;

/**
 * This class is supposed to be used in RAP environment only
 * A class with the same fully qualified name exist in *.rcp plugin for standalone use
 */
public class DualParameters {

	private static final Logger logger = Logger.getLogger(DualParameters.class);

	public static final String SET_AUTO_ACTIVATE = "autoActivateEnabled"; //$NON-NLS-1$
	public static final String SET_AUTO_PUBLISH = "autoPublishEnabled"; //$NON-NLS-1$

	public static final String SANDBOX_ENABLED = "enableSandbox"; //$NON-NLS-1$

	public static final String RUNTIME_URL_DEFAULT = ""; //$NON-NLS-1$
	public static final String SERVICES_URL_DEFAULT = ""; //$NON-NLS-1$

	public static final String HC_LOCAL_HTTP_PORT = "HC_LOCAL_HTTP_PORT"; //$NON-NLS-1$
	public static final String HC_APPLICATION_URL = "HC_APPLICATION_URL"; //$NON-NLS-1$
	public static final String HC_APPLICATION = "HC_APPLICATION"; //$NON-NLS-1$
	public static final String HC_ACCOUNT = "HC_ACCOUNT"; //$NON-NLS-1$
	public static final String HC_REGION = "HC_REGION"; //$NON-NLS-1$
	public static final String HC_HOST = "HC_HOST"; //$NON-NLS-1$

	public static final String GUEST_USER = ICommonConstants.GUEST;

	private static final boolean SET_SANDBOX_ENABLED_DEFAULT = false;

	private static final boolean SET_AUTO_ACTIVATE_DEFAULT = true;

	private static final boolean SET_AUTO_PUBLIUSH_DEFAULT = true;

	public static void initSystemParameters() {
		HttpServletRequest req = RWT.getRequest();
		String parameterHC_HOST = System.getProperty(HC_HOST);
		req.getSession().setAttribute(HC_HOST, parameterHC_HOST);
		String parameterHC_REGION = System.getProperty(HC_REGION);
		req.getSession().setAttribute(HC_REGION, parameterHC_REGION);
		String parameterHC_ACCOUNT = System.getProperty(HC_ACCOUNT);
		req.getSession().setAttribute(HC_ACCOUNT, parameterHC_ACCOUNT);
		String parameterHC_APPLICATION = System.getProperty(HC_APPLICATION);
		req.getSession().setAttribute(HC_APPLICATION, parameterHC_APPLICATION);
		String parameterHC_APPLICATION_URL = System.getProperty(HC_APPLICATION_URL);
		req.getSession().setAttribute(HC_APPLICATION_URL, parameterHC_APPLICATION_URL);
		String parameterHC_LOCAL_HTTP_PORT = System.getProperty(HC_LOCAL_HTTP_PORT);
		req.getSession().setAttribute(HC_LOCAL_HTTP_PORT, parameterHC_LOCAL_HTTP_PORT);
	}

	public static String get(String name) {
		try {
			String parameter = (String) RWT.getRequest().getSession().getAttribute(name);
			return parameter;
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

	public static Object getObject(String name) {
		return getObject(name, null);
	}

	public static Object getObject(String name, HttpServletRequest request) {
		try {
			if (request == null) {
				request = RWT.getRequest();
			}
			Object parameter = request.getSession().getAttribute(name);
			return parameter;
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return null;
	}

	public static void set(String name, String value) {
		try {
			RWT.getRequest().getSession().setAttribute(name, value);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public static void set(String name, Object value) {
		try {
			RWT.getRequest().getSession().setAttribute(name, value);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public static void setObject(String name, Object value) {
		try {
			RWT.getRequest().getSession().setAttribute(name, value);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	public static String getRuntimeUrl() {
		String runtimeUrl = EnvUtils.getEnv(ICommonConstants.INIT_PARAM_RUNTIME_URL);
		if (runtimeUrl == null) {
			runtimeUrl = RUNTIME_URL_DEFAULT;
		}
		return runtimeUrl;
	}

	public static boolean isAutoPublishEnabled() {
		String autoPublishEnabled = EnvUtils.getEnv(SET_AUTO_PUBLISH);
		boolean result = SET_AUTO_PUBLIUSH_DEFAULT;
		if (autoPublishEnabled != null) {
			result = Boolean.parseBoolean(autoPublishEnabled);
		}
		return result;
	}

	public static boolean isAutoActivateEnabled() {
		String autoPublishEnabled = EnvUtils.getEnv(SET_AUTO_ACTIVATE);
		boolean result = SET_AUTO_ACTIVATE_DEFAULT;
		if (autoPublishEnabled != null) {
			result = Boolean.parseBoolean(autoPublishEnabled);
		}
		return result;
	}

	public static boolean isSandboxEnabled() {
		String sandboxEnabledStr = EnvUtils.getEnv(SANDBOX_ENABLED);
		boolean result = SET_SANDBOX_ENABLED_DEFAULT;
		if (sandboxEnabledStr != null) {
			result = Boolean.parseBoolean(sandboxEnabledStr);
		}
		return result;
	}

	public static String getServicesUrl() {
		String runtimeUrl = EnvUtils.getEnv(ICommonConstants.INIT_PARAM_RUNTIME_URL);
		if ((runtimeUrl == null) || "".equals(runtimeUrl)) {
			runtimeUrl = RWT.getRequest().getContextPath();
		}
		String servicesUrl = EnvUtils.getEnv(ICommonConstants.INIT_PARAM_SERVICES_URL);
		if (servicesUrl == null) {
			servicesUrl = SERVICES_URL_DEFAULT;
		}

		if (runtimeUrl.endsWith(IRepository.SEPARATOR) && servicesUrl.startsWith(IRepository.SEPARATOR)) {
			servicesUrl = servicesUrl.substring(1);
		}
		return runtimeUrl + servicesUrl;

	}

	public static <T extends ClientService> T getService(Class<T> clazz) {
		return RWT.getClient().getService(clazz);
	}

	public static String getContextPath() {
		return RWT.getRequest().getContextPath();
	}

	public static HttpServletRequest getRequest() {
		return RWT.getRequest();
	}

	public static String getUserName(HttpServletRequest request) {
		String user = GUEST_USER;
		try {
			if (request == null) {
				request = RWT.getRequest();
			}
			user = request.getRemoteUser();
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		if (user == null) {
			if (!isRolesEnabled()) {
				user = RequestUtils.getCookieValue(request, ICommonConstants.COOKIE_ANONYMOUS_USER);
			}
		}
		if (user == null) {
			user = GUEST_USER;
		}
		return user;
	}

	public static Boolean isRolesEnabled() {
		Boolean rolesEnabled = Boolean.parseBoolean(EnvUtils.getEnv(ICommonConstants.INIT_PARAM_ENABLE_ROLES));
		return rolesEnabled;
	}

	public static boolean isUserInRole(String role) {
		if (isRolesEnabled()) {
			return RWT.getRequest().isUserInRole(role);
		} else {
			return true;
		}
	}

	public static String getSessionId() {
		String sessionId = RWT.getRequest().getSession(true).getId();
		return sessionId;
	}

	public static final boolean isRAP() {
		return true;
	}

	public static final boolean isRCP() {
		return false;
	}
}
