/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.ext.utils.EnvUtils;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Theme Service
 */
public class ThemesServlet extends HttpServlet {

	private static final String NAME_PARAM = "name"; //$NON-NLS-1$
	private static final String DEFAULT_THEME = "default"; //$NON-NLS-1$
	private static final String INIT_PARAM_DEFAULT_THEME = "defaultTheme"; //$NON-NLS-1$
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final long serialVersionUID = 3669759124907091014L;

	private static final Logger logger = Logger.getLogger(ThemesServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String env = EnvUtils.getEnv(INIT_PARAM_DEFAULT_THEME);
		String cookieValue = (env == null) ? DEFAULT_THEME : env;
		String themеName = req.getParameter(NAME_PARAM);
		themеName = StringEscapeUtils.escapeHtml(themеName);
		themеName = StringEscapeUtils.escapeJavaScript(themеName);
		if ((null != themеName) && !themеName.isEmpty()) {
			themеName = themеName.trim();

			// if there is valid theme name, then force the setting of the cookie
			setCookieUser(resp, themеName);
			cookieValue = themеName;
		} else {
			// parameter not present, so look up in the cookies
			Cookie[] cookies = req.getCookies();
			String cookieName = ICommonConstants.COOKIE_THEME;
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					cookieValue = cookie.getValue();
					// cookie exists, hence use it
					break;
				}
			}
		}
		cookieValue = StringEscapeUtils.escapeHtml(cookieValue);
		cookieValue = StringEscapeUtils.escapeJavaScript(cookieValue);
		resp.getWriter().println(cookieValue.trim());
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	private void setCookieUser(HttpServletResponse resp, String themeName) {
		Cookie cookie = new Cookie(ICommonConstants.COOKIE_THEME, themeName);
		cookie.setMaxAge(30 * 24 * 60 * 60);
		cookie.setPath(SLASH);
		resp.addCookie(cookie);
	}

}
