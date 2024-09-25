package org.eclipse.dirigible.components.security.snowflake;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
class SnowflakeLogoutHandler implements LogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeLogoutHandler.class);

    private static final String SNOWFLAKE_AUTH_COOKIE_PREFIX = "sfc-ss-ingress-auth";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        LOGGER.info("Invalidating cookies...");
        for (Cookie cookie : cookies) {
            if (cookie.getName()
                      .startsWith(SNOWFLAKE_AUTH_COOKIE_PREFIX)) {
                // invalidate the cookie
                LOGGER.info("Invalidating cookie with name [{}]", cookie.getName());
                cookie.setValue(null);
                cookie.setMaxAge(0);
                cookie.setPath("/");

                response.addCookie(cookie); // Add the invalidated cookie to the response
            }
        }
    }
}
