package org.eclipse.dirigible.components.security.keycloak;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Profile("keycloak")
@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOidcUserService.class);

    private static final OAuth2Error USER_NOT_IN_TENANT_ERR = new OAuth2Error("user_not_registered_in_tenant");

    private final UserService userService;
    private final TenantContext tenantContext;

    public CustomOidcUserService(UserService userService, TenantContext tenantContext) {
        this.userService = userService;
        this.tenantContext = tenantContext;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        User user = getTenantUser(oidcUser);
        Set<String> roleNames = getRoleNames(user);
        Set<GrantedAuthority> roleAuthorities = toAuthorities(roleNames);

        return createOidcUser(userRequest, oidcUser, roleAuthorities);

    }

    private User getTenantUser(OidcUser oidcUser) {
        if (isTrialEnabled()) {
            LOGGER.debug("Trial enabled - user [{}] will not be checked for the current tenant.", oidcUser.getName());
            return null;
        }
        String username = oidcUser.getName();
        String tenantId = tenantContext.getCurrentTenant()
                                       .getId();

        return userService.findUserByUsernameAndTenantId(username, tenantId)
                          .orElseThrow(() -> {
                              String message = "User [" + username + "] was not found in tenant [" + tenantId + "].";
                              return new OAuth2AuthenticationException(USER_NOT_IN_TENANT_ERR, message);
                          });
    }

    private Set<String> getRoleNames(User user) {
        if (isTrialEnabled()) {
            LOGGER.debug("Trial enabled - returning all available system roles for the current user.");
            return Arrays.stream(Roles.values())
                         .map(Roles::getRoleName)
                         .collect(Collectors.toSet());
        }
        return userService.getUserRoleNames(user);
    }

    private boolean isTrialEnabled() {
        String configValue = Configuration.get(Configuration.TRIAL_ENABLED, "false");
        return Boolean.valueOf(configValue);
    }

    private Set<GrantedAuthority> toAuthorities(Collection<String> roleNames) {
        return roleNames.stream()
                        .map(r -> new SimpleGrantedAuthority(r))
                        .collect(Collectors.toSet());

    }

    private OidcUser createOidcUser(OidcUserRequest userRequest, OidcUser oidcUser, Set<GrantedAuthority> additionalAuthorities) {
        String userNameAttributeName = userRequest.getClientRegistration()
                                                  .getProviderDetails()
                                                  .getUserInfoEndpoint()
                                                  .getUserNameAttributeName();
        return StringUtils.hasText(userNameAttributeName) ? new CustomOidcUser(oidcUser, userNameAttributeName, additionalAuthorities)
                : new CustomOidcUser(oidcUser, additionalAuthorities);
    }

}
