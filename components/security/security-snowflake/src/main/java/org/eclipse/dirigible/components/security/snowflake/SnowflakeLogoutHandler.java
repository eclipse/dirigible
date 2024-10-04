package org.eclipse.dirigible.components.security.snowflake;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
class SnowflakeLogoutHandler implements LogoutHandler {

    private static final String SNOWFLAKE_AUTH_COOKIE_PREFIX = "sfc-ss-ingress-auth-v1-";
    private static final String SNOWFLAKE_AUTH_COOKIE_INVALIDATED_VALUE_PATTERN =
            SNOWFLAKE_AUTH_COOKIE_PREFIX + "%s=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/; domain=.%s; Secure; HttpOnly";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String hostHeader = request.getHeader(HttpHeaders.HOST);
        String subdomain = extractSubdomain(hostHeader);

        String invalidatedAuthCookie = String.format(SNOWFLAKE_AUTH_COOKIE_INVALIDATED_VALUE_PATTERN, subdomain, hostHeader);

        // set the cookie as header since addCookie cannot be used
        // due to an RFC restriction
        response.addHeader(HttpHeaders.SET_COOKIE, invalidatedAuthCookie);
    }

    private static String extractSubdomain(String host) {
        int dotIdx = host.indexOf(".");
        return dotIdx == -1 ? host : host.substring(0, dotIdx);
    }

}
