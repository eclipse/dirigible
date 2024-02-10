package org.eclipse.dirigible.components.tenants.service;

import org.eclipse.dirigible.components.base.http.roles.Roles;
import org.eclipse.dirigible.components.tenants.domain.User;
import org.eclipse.dirigible.components.tenants.exceptions.TenantNotFoundException;
import org.eclipse.dirigible.components.tenants.repository.TenantRepository;
import org.eclipse.dirigible.components.tenants.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class UserService {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(TenantRepository tenantRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createNewUser(String email, String password, long tenantId, Roles role) {
        var tenant = tenantRepository.findById(tenantId)
                                     .orElseThrow(() -> new TenantNotFoundException("Tenant " + tenantId + " not found."));
        userRepository.save(new User(tenant, email, passwordEncoder.encode(password), role));
    }
}
