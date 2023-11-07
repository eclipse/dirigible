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
package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.RoutesLoader;

import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.apache.camel.support.ResourceHelper;
import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CamelProcessor {
	private final SpringBootCamelContext context;

	private final RequestMappingHandlerMapping camelMapping;

	private final RoutesLoader loader;

	private final Map<Long, Resource> camels = new HashMap<>();

	@Autowired
	public CamelProcessor(CamelContext context, @Qualifier("platformHttpEngineRequestMapping") RequestMappingHandlerMapping camelMapping) {
		this.context = context.adapt(SpringBootCamelContext.class);
		this.camelMapping = camelMapping;
		loader = this.context.getRoutesLoader();
	}

	public void onCreateOrUpdate(Camel camel) {
		Resource resource = ResourceHelper.fromBytes("any.yaml", camel.getContent());
		camels.put(camel.getId(), resource);
		removeAllRoutes();
		addAllRoutes();
	}

	public void onRemove(Camel camel) {
		camels.remove(camel.getId());
		removeAllRoutes();
		addAllRoutes();
	}

	private void addAllRoutes() {
		camels.values().forEach(routesResource -> {
			try {
				loader.loadRoutes(routesResource);
			} catch (Exception e) {
				throw new CamelProcessorException(e);
			}
		});
	}

	private void removeAllRoutes() {
		try {
			context.stopAllRoutes();
			context.removeAllRoutes();
			unregisterEndpoints();

		} catch (Exception e) {
			throw new CamelProcessorException(e);
		}
	}

	private void unregisterEndpoints() {
		List<RequestMappingInfo> mappingsToRemove = new ArrayList<>();
		camelMapping.getHandlerMethods().forEach((info, method) -> mappingsToRemove.add(info));
		mappingsToRemove.forEach(info -> camelMapping.unregisterMapping(info));
	}

	public Object invokeRoute(String routeId, Object payload, Map<String, Object> headers) {
		try (FluentProducerTemplate producer = context.createFluentProducerTemplate();) {
			return producer.withHeaders(headers).withBody(payload).to(routeId).request();
		} catch (IOException e) {
			throw new CamelProcessorException("Could not invoke route: " + routeId, e);
		}
	}
}
