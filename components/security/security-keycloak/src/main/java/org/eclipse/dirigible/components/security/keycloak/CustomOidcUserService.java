package org.eclipse.dirigible.components.security.keycloak;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.tenants.service.UserService;
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
        if (isTrialDisabled()) {
            String username = oidcUser.getName();
            String tenantId = tenantContext.getCurrentTenant()
                                           .getId();
            userService.findUserByUsernameAndTenantId(username, tenantId)
                       .orElseThrow(() -> {
                           String message = "User with username [" + username + "] in tenant [" + tenantId + "] was not found";
                           return new OAuth2AuthenticationException(USER_NOT_IN_TENANT_ERR, message);
                       });

            return oidcUser;
        }

        return createTrialUser(userRequest, oidcUser);
    }

    private boolean isTrialDisabled() {
        String configValue = Configuration.get(Configuration.TRIAL_ENABLED, "false");
        return !Boolean.valueOf(configValue);
    }

    /**
     * for trial <br>
     * - assign all available roles to all users<br>
     * - skip checking whether the user is registered for the tenant
     *
     * @param userRequest
     * @param oidcUser
     * @return
     */
    private OidcUser createTrialUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        // for trial
        // - assign all available roles to all users
        // - skip checking whether the user is registered for the tenant
        List<GrantedAuthority> roleAuthorities = new ArrayList<>();
        for (Roles role : Roles.values()) {
            GrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());
            roleAuthorities.add(authority);
        }

        String userNameAttributeName = userRequest.getClientRegistration()
                                                  .getProviderDetails()
                                                  .getUserInfoEndpoint()
                                                  .getUserNameAttributeName();
        return StringUtils.hasText(userNameAttributeName) ? new CustomOidcUser(oidcUser, userNameAttributeName, roleAuthorities)
                : new CustomOidcUser(oidcUser, roleAuthorities);
    }

}
