package org.eclipse.dirigible.runtime.content;

import java.io.IOException;

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
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String userName = req.getParameter("user");
		
		String cookieValue = null;
		if (userName != null 
				&& !"".equals(userName.trim())) {
			// if parameter is present, then force the setting of the cookie
			setCookieUser(resp, userName);
			cookieValue = userName;
		} else {
			// parameter not present, so look up in the cookies
			Cookie[] cookies = req.getCookies();
			String cookieName = ICommonConstants.COOKIE_ANONYMOUS_USER;
			for ( int i=0; i<cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName())) {
					cookieValue = cookie.getValue();
					// cookie exists, hence use it
					break;
				}
			}
			if (cookieValue == null) {
				// no cookie and no parameter, then fail
				logger.error("User has not been provider neither as cookie nor as parameter");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Use .../anonymous?user=xxx");
				return;
			}
		}
		String redirect = req.getParameter("redirect");
		if (redirect != null
				&& !"".equals(redirect.trim())) {
			resp.sendRedirect("ui/index.html");
		} else {
			resp.getWriter().println(cookieValue);
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	private void setCookieUser(HttpServletResponse resp, String userName) {
		Cookie cookie = new Cookie(ICommonConstants.COOKIE_ANONYMOUS_USER, userName);
		cookie.setMaxAge(30*24*60*60);
		resp.addCookie(cookie);
	}

}
