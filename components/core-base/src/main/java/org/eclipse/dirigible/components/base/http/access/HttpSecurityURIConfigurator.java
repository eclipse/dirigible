package org.eclipse.dirigible.components.base.http.access;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class HttpSecurityURIConfigurator {
	
	public static void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
	        .antMatchers("/").permitAll()
	        .antMatchers("/home").permitAll()
	        .antMatchers("/logout").permitAll()
	        .antMatchers("/index-busy.html").permitAll()
	        
	        .antMatchers("/stomp").permitAll()
	
	        .antMatchers("/error/**").permitAll()
	        .antMatchers("/error.html").permitAll()
	
	        // Public
	        .antMatchers("/favicon.ico").permitAll()
	        .antMatchers("/public/**").permitAll()
	        .antMatchers("/webjars/**").permitAll()
	        
	        .antMatchers("/services/core/theme/**").permitAll()
	        .antMatchers("/services/core/version/**").permitAll()
	        .antMatchers("/services/core/healthcheck/**").permitAll()
	        .antMatchers("/services/web/resources/**").permitAll()
	        .antMatchers("/services/web/resources-core/**").permitAll()
	        .antMatchers("/services/js/resources-core/**").permitAll()
	        
	        .antMatchers("/actuator/**").permitAll()
	
	        // Authenticated
	        .antMatchers("/services/**").authenticated()
	        .antMatchers("/websockets/**").authenticated()
	        .antMatchers("/odata/**").authenticated()
	
	        // "Developer" role required
	        .antMatchers("/services/ide/**").hasRole("Developer")
	        .antMatchers("/websockets/ide/**").hasRole("Developer")
	
	        // "Operator" role required
	//        .antMatchers("/services/ops/**").hasRole("Operator")
	//        .antMatchers("/services/transport/**").hasRole("Operator")
	//        .antMatchers("/websockets/ops/**").hasRole("Operator")
	
	        // Deny all other requests
	        .anyRequest().denyAll();
	}

}
