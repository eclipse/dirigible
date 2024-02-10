package org.eclipse.dirigible.components.tenants.security;

import static org.eclipse.dirigible.components.base.http.roles.Roles.ADMINISTRATOR;

import java.util.ArrayList;

import org.eclipse.dirigible.components.tenants.repository.UserRepository;
import org.eclipse.dirigible.components.tenants.tenant.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "tenants.enabled", havingValue = "true")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var tenant = TenantContext.getCurrentTenant();

        if (tenant != null) {
            return loadUser(email, tenant);
        } else {
            return loadGeneralAdmin(email);
        }
    }

    private UserDetails loadUser(String email, String tenant) {
        var user = userRepository.findUser(email, tenant)
                                 .orElseThrow(() -> new UsernameNotFoundException("'" + email + "' / '" + tenant + "' was not found."));

        var auths = new ArrayList<GrantedAuthority>();
        auths.add(new SimpleGrantedAuthority(user.getRole()
                                                 .getRoleName()));
        return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getId(), user.getTenant()
                                                                                            .getId(),
                auths);
    }

    private UserDetails loadGeneralAdmin(String email) {
        var admin = userRepository.findGeneralAdmin(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("'" + email + "' was not found as a general admin."));
        var auths = new ArrayList<GrantedAuthority>();
        auths.add(new SimpleGrantedAuthority(ADMINISTRATOR.getRoleName()));
        return new CustomUserDetails(admin.getEmail(), admin.getPassword(), admin.getId(), null, auths);
    }
}
