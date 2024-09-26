package org.eclipse.dirigible.components.security.snowflake;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
class SnowflakeLogoutHandler implements LogoutHandler {

    private static final String SNOWFLAKE_AUTH_COOKIE_PREFIX = "sfc-ss-ingress-auth-v1-";
    private static final String SNOWFLAKE_CSRF_TOKENCOOKIE_PREFIX = "sfc-ss-csrf-token-v1-";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String hostHeader = request.getHeader(HttpHeaders.HOST);
        String subdomain = extractSubdomain(hostHeader);

        Cookie authCookie = createInvalidatedAuthCookie(subdomain);
        response.addCookie(authCookie);

        Cookie csrfTokenCookie = createInvalidatedCsrfTokenCookie(subdomain);
        response.addCookie(csrfTokenCookie);
    }

    private String extractSubdomain(String host) {
        int dotIdx = host.indexOf(".");
        return dotIdx == -1 ? host : host.substring(0, dotIdx);
    }

    private Cookie createInvalidatedAuthCookie(String subdomain) {
        String cookieName = SNOWFLAKE_AUTH_COOKIE_PREFIX + subdomain;

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        cookie.setMaxAge(10 * 60);
        return cookie;
    }

    private Cookie createInvalidatedCsrfTokenCookie(String subdomain) {
        String cookieName = SNOWFLAKE_CSRF_TOKENCOOKIE_PREFIX + subdomain;

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        cookie.setMaxAge(10 * 60);
        return cookie;
    }

}
