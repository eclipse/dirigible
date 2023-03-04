package org.eclipse.dirigible.components.security.keycloak;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider  = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper()); // prefix = "ROLE_
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
        http.authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/home").permitAll()
            .antMatchers("/logout").permitAll()
            .antMatchers("/index-busy.html").permitAll()

            .antMatchers("/error/**").permitAll()
            .antMatchers("/error.html").permitAll()

            // Public
            .antMatchers("/public/**").permitAll()
            .antMatchers("/webjars/**").permitAll()
            .antMatchers("/services/core/theme/**").permitAll()
            .antMatchers("/services/web/resources/**").permitAll()
            .antMatchers("/services/web/resources-core/**").permitAll()
            .antMatchers("/services/js/resources-core/**").permitAll()

            // Authenticated
            .antMatchers("/services/web/**").authenticated()
            .antMatchers("/services/js/**").authenticated()
            .antMatchers("/services/wiki/**").authenticated()
            .antMatchers("/services/command/**").authenticated()
            .antMatchers("/odata/**").authenticated()

            // "Developer" role required
            .antMatchers("/services/ide/**").hasRole("Developer")
            .antMatchers("/websockets/ide/**").hasRole("Developer")

            // "Operator" role required
            .antMatchers("/services/ops/**").hasRole("Operator")
            .antMatchers("/services/transport/**").hasRole("Operator")
            .antMatchers("/websockets/ops/**").hasRole("Operator")

            // Deny all other requests
            .anyRequest().denyAll();
    }
}