/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.camel.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import org.apache.camel.CamelContext;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.component.platform.http.springboot.CamelRequestHandlerMapping;
import org.apache.camel.impl.engine.DefaultRoutesLoader;
import org.apache.camel.impl.event.ExchangeCompletedEvent;
import org.apache.camel.observation.MicrometerObservationTracer;
import org.apache.camel.spi.*;
import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.ExtendedExchangeExtension;
import org.apache.camel.support.ResourceHelper;
import org.apache.camel.tracing.SpanDecorator;
import org.eclipse.dirigible.components.engine.camel.domain.Camel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class CamelProcessor.
 */
@Component
public class CamelProcessor {

  /**
   * The context.
   */
  private final SpringBootCamelContext context;

  /**
   * The camel request handler mapping.
   */
  private final CamelRequestHandlerMapping camelRequestHandlerMapping;

  /**
   * The loader.
   */
  private final RoutesLoader loader;

  /**
   * The camels.
   */
  private final Map<Long, Resource> camels = new HashMap<>();

  /**
   * Instantiates a new camel processor.
   *
   * @param context                    the context
   * @param camelRequestHandlerMapping the camel request handler mapping
   */
  @Autowired
  public CamelProcessor(
      SpringBootCamelContext context,
      CamelRequestHandlerMapping camelRequestHandlerMapping,
      ObservationRegistry observationRegistry,
      Tracer tracer
  ) {
    this.context = context;
    this.camelRequestHandlerMapping = camelRequestHandlerMapping;
    loader = new DefaultRoutesLoader(context);

    MicrometerObservationTracer micrometerObservationTracer = new MicrometerObservationTracer(); // TODO: should we close?
    micrometerObservationTracer.setObservationRegistry(observationRegistry);
    micrometerObservationTracer.setTracer(tracer);
    micrometerObservationTracer.init(context);

    context.getTracer().setEnabled(true);
    context.setTracing(true);
    context.getManagementStrategy().addEventNotifier(new EventNotifier() {
      @Override
      public void notify(CamelEvent event) throws Exception {
        if (event.getSource() instanceof DefaultExchange exchange) {
          ExtendedExchangeExtension ext = exchange.getExchangeExtension();
          String stepId = ext.getHistoryNodeId();
          tracer.nextSpan()
              .name(stepId)
              .tag("Label", ext.getHistoryNodeLabel())
              .tag("Source", ext.getHistoryNodeSource())
              .start()
              .end();
        }
      }

      @Override
      public boolean isEnabled(CamelEvent event) {
        return true;
      }

      @Override
      public boolean isDisabled() {
        return false;
      }

      @Override
      public boolean isIgnoreCamelContextInitEvents() {
        return true;
      }

      @Override
      public void setIgnoreCamelContextInitEvents(boolean ignoreCamelContextInitEvents) {

      }

      @Override
      public boolean isIgnoreCamelContextEvents() {
        return true;
      }

      @Override
      public void setIgnoreCamelContextEvents(boolean ignoreCamelContextEvents) {

      }

      @Override
      public boolean isIgnoreRouteEvents() {
        return false;
      }

      @Override
      public void setIgnoreRouteEvents(boolean ignoreRouteEvents) {

      }

      @Override
      public boolean isIgnoreServiceEvents() {
        return false;
      }

      @Override
      public void setIgnoreServiceEvents(boolean ignoreServiceEvents) {

      }

      @Override
      public boolean isIgnoreExchangeEvents() {
        return false;
      }

      @Override
      public void setIgnoreExchangeEvents(boolean ignoreExchangeEvents) {

      }

      @Override
      public boolean isIgnoreExchangeCreatedEvent() {
        return false;
      }

      @Override
      public void setIgnoreExchangeCreatedEvent(boolean ignoreExchangeCreatedEvent) {

      }

      @Override
      public boolean isIgnoreExchangeCompletedEvent() {
        return false;
      }

      @Override
      public void setIgnoreExchangeCompletedEvent(boolean ignoreExchangeCompletedEvent) {

      }

      @Override
      public boolean isIgnoreExchangeFailedEvents() {
        return false;
      }

      @Override
      public void setIgnoreExchangeFailedEvents(boolean ignoreExchangeFailureEvents) {

      }

      @Override
      public boolean isIgnoreExchangeRedeliveryEvents() {
        return false;
      }

      @Override
      public void setIgnoreExchangeRedeliveryEvents(boolean ignoreExchangeRedeliveryEvents) {

      }

      @Override
      public boolean isIgnoreExchangeSentEvents() {
        return false;
      }

      @Override
      public void setIgnoreExchangeSentEvents(boolean ignoreExchangeSentEvents) {

      }

      @Override
      public boolean isIgnoreExchangeSendingEvents() {
        return false;
      }

      @Override
      public void setIgnoreExchangeSendingEvents(boolean ignoreExchangeSendingEvents) {

      }

      @Override
      public boolean isIgnoreStepEvents() {
        return false;
      }

      @Override
      public void setIgnoreStepEvents(boolean ignoreStepEvents) {

      }

      @Override
      public void setIgnoreExchangeAsyncProcessingStartedEvents(boolean ignoreExchangeAsyncProcessingStartedEvents) {

      }

      @Override
      public boolean isIgnoreExchangeAsyncProcessingStartedEvents() {
        return false;
      }
    });
  }

  /**
   * On create or update.
   *
   * @param camel the camel
   */
  public void onCreateOrUpdate(Camel camel) {
    Resource resource = ResourceHelper.fromBytes("any.yaml", camel.getContent());
    camels.put(camel.getId(), resource);
    removeAllRoutes();
    addAllRoutes();
  }

  /**
   * On remove.
   *
   * @param camel the camel
   */
  public void onRemove(Camel camel) {
    camels.remove(camel.getId());
    removeAllRoutes();
    addAllRoutes();
  }

  /**
   * Adds the all routes.
   */
  private void addAllRoutes() {
    camels.values()
        .forEach(routesResource -> {
          try {
            loader.loadRoutes(routesResource);
          } catch (Exception e) {
            throw new CamelProcessorException(e);
          }
        });
  }

  /**
   * Removes the all routes.
   */
  private void removeAllRoutes() {
    try {
      context.stopAllRoutes();
      context.removeAllRoutes();
      camelRequestHandlerMapping.getHandlerMethods()
          .forEach((info, method) -> camelRequestHandlerMapping.unregisterMapping(info));
    } catch (Exception e) {
      throw new CamelProcessorException(e);
    }
  }

  /**
   * Invoke route.
   *
   * @param routeId the route id
   * @param payload the payload
   * @param headers the headers
   * @return the object
   */
  public Object invokeRoute(String routeId, Object payload, Map<String, Object> headers) {
    try (FluentProducerTemplate producer = context.createFluentProducerTemplate()) {
      return producer.withHeaders(headers)
          .withBody(payload)
          .to(routeId)
          .request();
    } catch (IOException e) {
      throw new CamelProcessorException("Could not invoke route: " + routeId, e);
    }
  }
}
