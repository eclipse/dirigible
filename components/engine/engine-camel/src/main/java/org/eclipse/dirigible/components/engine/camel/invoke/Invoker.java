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
package org.eclipse.dirigible.components.engine.camel.invoke;

import org.apache.camel.Message;
import org.eclipse.dirigible.components.engine.camel.processor.CamelProcessor;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class Invoker {
  private final JavascriptService javascriptService;

  private final CamelProcessor processor;

  @Autowired
  public Invoker(JavascriptService javascriptService, CamelProcessor processor) {
    this.javascriptService = javascriptService;
    this.processor = processor;
  }

  public void invoke(Message camelMessage) {
    String resourcePath = (String) camelMessage.getExchange()
                                               .getProperty("resource");
    RepositoryPath path = new RepositoryPath(resourcePath);

    String messageBody = camelMessage.getBody(String.class);

    Map<Object, Object> context = new HashMap<>();
    context.put("camelMessage", messageBody);

    javascriptService.handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
  }

  public Object invokeRoute(String routeId, Object payload, Map<String, Object> headers) {
    return processor.invokeRoute(routeId, payload, headers);
  }
}
