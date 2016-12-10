package org.eclipse.dirigible.runtime.content;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class AnonymousUserServlet extends HttpServlet {

	private static final String INVALID_USER_NAME_S = "Invalid user name: %s"; //$NON-NLS-1$
	private static final String USER_PARAM = "user"; //$NON-NLS-1$
	private static final String INVALID_COOKIE = "Invalid cookie."; //$NON-NLS-1$
	private static final String USE_ANONYMOUS_USER_XXX = "Use .../anonymous?user=xxx"; //$NON-NLS-1$
	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final String REDIRECT_REGISTRY = "web/registry/index.html"; //$NON-NLS-1$
	private static final String REDIRECT_IDE = "index.html?perspective=workspace&git="; //$NON-NLS-1$
	private static final String GIT_PARAM = "git"; //$NON-NLS-1$
	private static final String REDIRECT_PARAM = "redirect"; //$NON-NLS-1$
	private static final long serialVersionUID = 3669759124907091014L;
	private static final Logger logger = Logger.getLogger(AnonymousUserServlet.class);
	private static final String REGEX_USER_NAME = "[A-Za-z0-9_]{3,64}"; //$NON-NLS-1$
	private static final Pattern PATTERN_USER_NAME = Pattern.compile(REGEX_USER_NAME);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cookieValue = null;
		String userName = req.getParameter(USER_PARAM);
		userName = StringEscapeUtils.escapeHtml(userName);
		userName = StringEscapeUtils.escapeJavaScript(userName);
		if ((null != userName) && !userName.isEmpty()) {
			userName = userName.trim();
			final Matcher userNameMatcher = PATTERN_USER_NAME.matcher(userName);
			if (!userNameMatcher.matches()) {
				logger.error(String.format("The provided username <%s> is invalid. It must match: %s", userName, REGEX_USER_NAME));
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, String.format(INVALID_USER_NAME_S, userName));
				return;
			}
			// if there is valid user name, then force the setting of the cookie
			setCookieUser(resp, userName);
			cookieValue = userName;
		} else {
			// parameter not present, so look up in the cookies
			Cookie[] cookies = req.getCookies();
			String cookieName = ICommonConstants.COOKIE_ANONYMOUS_USER;
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					cookieValue = cookie.getValue();
					// cookie exists, hence use it
					break;
				}
			}
			if (cookieValue == null) {
				// no cookie and no parameter, then fail
				logger.error("User has not been provided neither as cookie nor as parameter");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, USE_ANONYMOUS_USER_XXX);
				return;
			}

			final Matcher cookieUserNameMatcher = PATTERN_USER_NAME.matcher(cookieValue);
			if (!cookieUserNameMatcher.matches()) {
				logger.error("Invalid user name was provided as cookie: " + cookieValue);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_COOKIE);
				return;
			}
		}
		String redirect = req.getParameter(REDIRECT_PARAM);
		if ((redirect != null) && !"".equals(redirect.trim())) {
			String git = req.getParameter(GIT_PARAM);
			if ((git != null) && !"".equals(git.trim())) {
				git = StringEscapeUtils.escapeHtml(git);
				git = StringEscapeUtils.escapeJavaScript(git);
				resp.sendRedirect(REDIRECT_IDE + git);
			} else {
				resp.sendRedirect(REDIRECT_REGISTRY);
			}
		} else {
			cookieValue = StringEscapeUtils.escapeHtml(cookieValue);
			cookieValue = StringEscapeUtils.escapeJavaScript(cookieValue);
			resp.getWriter().println(cookieValue);
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private void setCookieUser(HttpServletResponse resp, String userName) {
		Cookie cookie = new Cookie(ICommonConstants.COOKIE_ANONYMOUS_USER, userName);
		cookie.setMaxAge(30 * 24 * 60 * 60);
		cookie.setPath(SLASH);
		resp.addCookie(cookie);
	}

}
