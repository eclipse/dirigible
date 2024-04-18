/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.tracing;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Configuration
public class OpenTelemetryConfiguration {
  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
  public DisposableBean runJaeger() {
    var portBindings = List.of(
        "6831:6831/udp",
        "6832:6832/udp",
        "5778:5778",
        "16686:16686",
        "4317:4317",
        "4318:4318",
        "14250:14250",
        "14268:14268",
        "14269:14269",
        "9411:9411"
    );

    @SuppressWarnings("resource")
    var jaeger = new GenericContainer<>(DockerImageName.parse("jaegertracing/all-in-one:1.56"));
    jaeger.setPortBindings(portBindings);
    jaeger.addEnv("COLLECTOR_ZIPKIN_HOST_PORT", ":9411");
    jaeger.waitingFor(new HttpWaitStrategy().forPort(16686));
    jaeger.start();

    return () -> {
      jaeger.stop();
      jaeger.close();
    };
  }
}
