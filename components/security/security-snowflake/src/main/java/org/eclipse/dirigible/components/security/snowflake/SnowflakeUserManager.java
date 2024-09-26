package org.eclipse.dirigible.components.security.snowflake;

import org.eclipse.dirigible.components.security.domain.Role;
import org.eclipse.dirigible.components.security.service.RoleService;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class SnowflakeUserManager {

    private final UserService userService;

    private final RoleService roleService;

    SnowflakeUserManager(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public Optional<User> findUserByUsernameAndTenantId(String username, String tenantId) {
        return userService.findUserByUsernameAndTenantId(toSnowflakeUsername(username), tenantId);
    }

    private String toSnowflakeUsername(String username) {
        // usernames are passed in uppercase to the applications via header Sf-Context-Current-User
        return username.toUpperCase();
    }

    public User createNewUser(String username, String tenantId) {
        String password = UUID.randomUUID()
                              .toString();// password not used in the Snowflake scenario
        return userService.createNewUser(toSnowflakeUsername(username), password, tenantId);
    }

    public void assignUserRoles(User user, String roleName) {
        Role role = roleService.findByName(roleName);
        userService.assignUserRoles(user, role);
    }
}
