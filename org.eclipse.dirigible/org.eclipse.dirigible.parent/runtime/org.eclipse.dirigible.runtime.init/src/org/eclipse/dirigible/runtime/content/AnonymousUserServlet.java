package org.eclipse.dirigible.runtime.content;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.logging.Logger;

public class AnonymousUserServlet extends HttpServlet {
	private static final long serialVersionUID = 3669759124907091014L;
	private static final Logger logger = Logger.getLogger(AnonymousUserServlet.class);
	private static final String REGEX_USER_NAME = "[A-Za-z0-9_]{3,64}";
	private static final Pattern PATTERN_USER_NAME = Pattern.compile(REGEX_USER_NAME);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cookieValue = null;
		String userName = req.getParameter("user");
		if ((null != userName) && !userName.isEmpty()) {
			userName = userName.trim();
			final Matcher userNameMatcher = PATTERN_USER_NAME.matcher(userName);
			if (!userNameMatcher.matches()) {
				logger.error("The provided username <" + userName + "> is invalid. It must match: " + REGEX_USER_NAME);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user name: " + userName);
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
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Use .../anonymous?user=xxx");
				return;
			}

			final Matcher cookieUserNameMatcher = PATTERN_USER_NAME.matcher(cookieValue);
			if (!cookieUserNameMatcher.matches()) {
				logger.error("Invalid user name was provided as cookie: " + cookieValue);
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid cookie.");
				return;
			}
		}
		String redirect = req.getParameter("redirect");
		if ((redirect != null) && !"".equals(redirect.trim())) {
			String git = req.getParameter("git");
			if ((git != null) && !"".equals(git.trim())) {
				resp.sendRedirect("index.html?perspective=workspace&git=" + git);
			} else {
				resp.sendRedirect("ui/index.html");
			}
		} else {
			resp.getWriter().println(cookieValue);
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private void setCookieUser(HttpServletResponse resp, String userName) {
		Cookie cookie = new Cookie(ICommonConstants.COOKIE_ANONYMOUS_USER, userName);
		cookie.setMaxAge(30 * 24 * 60 * 60);
		cookie.setPath("/");
		resp.addCookie(cookie);
	}

}
