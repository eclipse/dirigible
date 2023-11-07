/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.componenets.api.http;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@WebAppConfiguration
@ComponentScan(basePackages = {"org.eclipse.dirigible.components.*"})
public class HttpSuiteTest {

	@Autowired
	private JavascriptService javascriptService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext context;

	@Autowired
	private Filter springSecurityFilterChain;

	@Test
	public void executeClientTest() throws Exception {
		javascriptService.handleRequest("http-tests", "client-get.js", null, null, false);
		javascriptService.handleRequest("http-tests", "client-get-binary.js", null, null, false);
	}

	// @WithMockUser(username = "user", roles={"role1"})
	@Test
	public void executeRequestTest() throws Exception {
		mockMvc	.perform(get("/services/js/http-tests/request-get-attribute.js")
																				.header(HttpHeaders.AUTHORIZATION,
																						"Basic " + Base64Utils.encodeToString(
																								"user:password".getBytes()))
																				.requestAttr("attr1", "val1"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		// mockMvc.perform(get("/services/js/http-tests/request-get-auth-type.js")
		// .header(HttpHeaders.AUTHORIZATION,
		// "Basic " + Base64Utils.encodeToString("user:password".getBytes())))
		// .andDo(print())
		// .andExpect(status().is2xxSuccessful());
		mockMvc	.perform(get("/services/js/http-tests/request-get-header.js")
																				.header(HttpHeaders.AUTHORIZATION,
																						"Basic " + Base64Utils.encodeToString(
																								"user:password".getBytes()))
																				.header("header1", "header1")
																				.requestAttr("attr1", "val1"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		mockMvc	.perform(get("/services/js/http-tests/request-get-header-names.js")
																					.header(HttpHeaders.AUTHORIZATION,
																							"Basic " + Base64Utils.encodeToString(
																									"user:password".getBytes()))
																					.header("header1", "header1")
																					.header("header2", "header2")
																					.requestAttr("attr1", "val1"))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-get-method.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-get-path-info.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-get-path-translated.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-get-remote-user.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-get-server-name.js")
																					.header(HttpHeaders.AUTHORIZATION,
																							"Basic " + Base64Utils.encodeToString(
																									"user:password".getBytes()))
																					.with(new RequestPostProcessor() {
																						public MockHttpServletRequest postProcessRequest(
																								MockHttpServletRequest request) {
																							request.setServerName("server1");
																							return request;
																						}
																					}))
				.andDo(print())
				.andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-is-user-in-role.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/services/js/http-tests/request-is-valid.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void executeResponseTest() throws Exception {
		mockMvc.perform(get("/services/js/http-tests/response-get-header-names.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void executeSessionTest() throws Exception {
		mockMvc.perform(get("/services/js/http-tests/session-get-attribute-names.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void executeRSTest() throws Exception {
		mockMvc.perform(get("/services/js/http-tests/rs-define-request-handlers.js").header(HttpHeaders.AUTHORIZATION,
				"Basic " + Base64Utils.encodeToString("user:password".getBytes()))).andDo(print()).andExpect(status().is2xxSuccessful());
	}

	@Configuration
	static class Config {
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("user").password("password").roles("ROLE");
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return NoOpPasswordEncoder.getInstance();
		}

		@Autowired
		HttpResponseHeaderHandlerInterceptor httpResponsHeaderHandlerInterceptor;

		@Bean
		public WebMvcConfigurer contentNegotiatorConfigurer() {
			return new WebMvcConfigurerAdapter() {
				@Override
				public void addInterceptors(InterceptorRegistry registry) {
					registry.addInterceptor(httpResponsHeaderHandlerInterceptor);
				}
			};
		}
	}

	static class HttpResponseHeaderHandlerInterceptor extends HandlerInterceptorAdapter implements HandlerInterceptor {

		@Override
		public final boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
				throws Exception {

			this.assignHttpResponseHeaders(request, response, handler);

			return super.preHandle(request, response, handler);
		}

		protected final void assignHttpResponseHeaders(final HttpServletRequest request, final HttpServletResponse response,
				final Object handler) {

			response.setHeader("header1", "val1");
			response.setHeader("header2", "val2");
		}
	}


	@SpringBootApplication
	static class TestConfiguration {

		@Bean
		HttpResponseHeaderHandlerInterceptor getHttpResponseHeaderHandlerInterceptor() {
			return new HttpResponseHeaderHandlerInterceptor();
		}

	}
}
