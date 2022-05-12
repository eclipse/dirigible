/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.services.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.catalina.filters.CorsFilter;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;
import org.apache.olingo.odata2.core.servlet.ODataServlet;
import org.eclipse.dirigible.runtime.core.embed.EmbeddedDirigible;
import org.eclipse.dirigible.runtime.core.filter.HealthCheckFilter;
import org.eclipse.dirigible.runtime.core.filter.HttpContextFilter;
import org.eclipse.dirigible.runtime.core.initializer.DirigibleInitializer;
import org.eclipse.dirigible.runtime.core.services.HomeRedirectServlet;
import org.eclipse.dirigible.runtime.core.services.LogoutServlet;
import org.eclipse.dirigible.runtime.core.version.Version;
import org.eclipse.dirigible.runtime.core.version.VersionProcessor;
//import org.eclipse.dirigible.runtime.security.filter.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DirigibleSpringBootConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DirigibleSpringBootConfiguration.class);

	@Autowired
	private Bus bus;

	@Bean
	public Server rsServer() {
		EmbeddedDirigible dirigible = new EmbeddedDirigible();
		DirigibleInitializer initializer = dirigible.initialize();
		ArrayList<Object> servicesAndProviders = new ArrayList<Object>(initializer.getServices());

		JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
		endpoint.setBus(bus);
		endpoint.setServiceBeans(servicesAndProviders);
		endpoint.setProviders(servicesAndProviders);
		endpoint.setAddress("/");
		endpoint.setFeatures(Arrays.asList(createOpenApiFeature()));

		return endpoint.create();
	}

	@Bean
	public FilterRegistrationBean<HttpContextFilter> contextFilter() {
		FilterRegistrationBean<HttpContextFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new HttpContextFilter());
		registrationBean.addUrlPatterns(
				"/services/v3/*", //
				"/public/v3/*", //
				"/services/v4/*", //
				"/public/v4/*" //
		);

		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<HealthCheckFilter> healthcheckFilter() {
		FilterRegistrationBean<HealthCheckFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new HealthCheckFilter());
		registrationBean.addUrlPatterns(
				"/services/v3/*", //
				"/public/v3/*", //
				"/services/v4/*", //
				"/public/v4/*" //
		);

		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new CorsFilter());
		registrationBean.addInitParameter("cors.allowed.origins", "*");
		registrationBean.addInitParameter("cors.allowed.methods", "GET,PUT,PATCH,POST,DELETE,HEAD,OPTIONS,CONNECT,TRACE");
		registrationBean.addUrlPatterns("/*");

		return registrationBean;
	}

//    @Bean
//    public FilterRegistrationBean<SecurityFilter> securityFilter(){
//        FilterRegistrationBean<SecurityFilter> registrationBean 
//          = new FilterRegistrationBean<>();
//            
//        registrationBean.setFilter(new SecurityFilter());
//        registrationBean.addUrlPatterns("/services/v3/js/*",
//        		"/services/v3/rhino/*",
//        		"/services/v3/nashorn/*",
//        		"/services/v3/v8/*",
//        		"/services/v3/public/*",
//        		"/services/v3/web/*",
//        		"/services/v3/wiki/*",
//        		"/services/v3/command/*",
//        		
//        		"/public/v3/js/*",
//        		"/public/v3/rhino/*",
//        		"/public/v3/nashorn/*",
//        		"/public/v3/v8/*",
//        		"/public/v3/public/*",
//        		"/public/v3/web/*",
//        		"/public/v3/wiki/*",
//        		"/public/v3/command/*",
//        		
//        		"/services/v4/js/*",
//        		"/services/v4/rhino/*",
//        		"/services/v4/nashorn/*",
//        		"/services/v4/v8/*",
//        		"/services/v4/public/*",
//        		"/services/v4/web/*",
//        		"/services/v4/wiki/*",
//        		"/services/v4/command/*",
//        		
//        		"/public/v4/js/*",
//        		"/public/v4/rhino/*",
//        		"/public/v4/nashorn/*",
//        		"/public/v4/v8/*",
//        		"/public/v4/public/*",
//        		"/public/v4/web/*",
//        		"/public/v4/wiki/*",
//        		"/public/v4/command/*",
//        		
//        		"/odata/v2/*");
//            
//        return registrationBean;    
//    }

	@Bean
	public ServletRegistrationBean<HomeRedirectServlet> delegateHomeRedirectServlet() {
		return new ServletRegistrationBean<HomeRedirectServlet>(new HomeRedirectServlet(), "/home");
	}

	@Bean
	public ServletRegistrationBean<LogoutServlet> delegateLogoutServlet() {
		return new ServletRegistrationBean<LogoutServlet>(new LogoutServlet(), "/logout");
	}

	@Bean
	public ServletRegistrationBean<ODataServlet> olingoServlet() {
		ServletRegistrationBean<ODataServlet> bean = new ServletRegistrationBean<ODataServlet>(new ODataServlet(), "/odata/v2/*");
		bean.addInitParameter("javax.ws.rs.Application", "org.apache.olingo.odata2.core.rest.app.ODataApplication");
		bean.addInitParameter("org.apache.olingo.odata2.service.factory", "org.eclipse.dirigible.engine.odata2.factory.DirigibleODataServiceFactory");
		bean.setLoadOnStartup(1);
		return bean;
	}

	@Bean
	public OpenApiFeature createOpenApiFeature() {
		final OpenApiFeature openApiFeature = new OpenApiFeature();
		openApiFeature.setPrettyPrint(true);
		openApiFeature.setTitle(getOpenApiTitle());
		openApiFeature.setContactName(getOpenApiContactName());
		openApiFeature.setContactEmail(getOpenApiContactEmail());
		openApiFeature.setLicense(getOpenApiLicense());
		openApiFeature.setLicenseUrl(getOpenApiLicenseUrl());
		openApiFeature.setDescription(getOpenApiDescription());
		try {
			Version version = new VersionProcessor().getVersion();
			openApiFeature.setVersion(version.getProductVersion());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			openApiFeature.setVersion("3.0.0");
		}
		openApiFeature.setSwaggerUiConfig(new SwaggerUiConfig().url("/a/services/v4/openapi.json"));
		return openApiFeature;
	}

	protected String getOpenApiTitle() {
		return "Eclipse Dirigible - RESTful Services API";
	}

	protected String getOpenApiContactName() {
		return "Eclipse Dirigible";
	}

	protected String getOpenApiContactEmail() {
		return "dirigible-dev@eclipse.org";
	}

	protected String getOpenApiLicense() {
		return "Eclipse Public License - v 2.0";
	}

	protected String getOpenApiLicenseUrl() {
		return "https://www.eclipse.org/legal/epl-v20.html";
	}

	protected String getOpenApiDescription() {
		return "Eclipse Dirigible API of the core RESTful services provided by the application development platform itself";
	}
}