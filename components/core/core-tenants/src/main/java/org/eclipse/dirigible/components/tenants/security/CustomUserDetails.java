package org.eclipse.dirigible.components.tenants.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {
    private final long userId;
    private final Long tenantId;

    public CustomUserDetails(String username, String password, long userId, Long tenantId,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.tenantId = tenantId;
    }

    public long getUserId() {
        return userId;
    }

    public Long getTenantId() {
        return tenantId;
    }
}
