package org.eclipse.dirigible.tests.util;

import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.service.RoleService;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserService userService;
    private final TenantService tenantService;
    private final Tenant defaultTenant;
    private final RoleService roleService;

    SecurityUtil(UserService userService, TenantService tenantService, @DefaultTenant Tenant defaultTenant, RoleService roleService) {
        this.userService = userService;
        this.tenantService = tenantService;
        this.defaultTenant = defaultTenant;
        this.roleService = roleService;
    }

    public void createUser(String username, String password, String roleName) {
        String defaultTenantId = tenantService.findBySubdomain(defaultTenant.getSubdomain())
                                              .get()
                                              .getId();
        User user = userService.createNewUser(username, password, defaultTenantId);

        Role role = roleService.findByName(roleName);
        userService.assignUserRoles(user, role);
    }
}
