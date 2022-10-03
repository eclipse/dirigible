package org.eclipse.dirigible.services.spring.wrappers;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public WrapRequestFilter wrapRequestFilter() {
        return new WrapRequestFilter();
    }

    @Bean
    public FilterRegistrationBean wrapRequestFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(wrapRequestFilter());
        registrationBean.setName("wrapRequestFilter");
        registrationBean.setOrder(-1000001);
        return registrationBean;
    }
}
