/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.components.api.messaging.MessagingFacade;
import org.eclipse.dirigible.components.api.rabbitmq.RabbitMQFacade;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * A client-Java application consumes a RabbitMQ queue with a Java handler (#8017) - where the SDK
 * used to take only the registry path of a handler script, and so a script engine.
 *
 * <p>
 * The broker is a real RabbitMQ in a container. The client component starts consuming when the
 * container builds it and stops when its generation is torn down - the lifecycle a component owning
 * a consumer is meant to have - and hands every message to the platform's own broker, where the
 * test draws it from.
 */
// One Dirigible boot for the class: the broker outlives the individual methods.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class JavaRabbitMQConsumerIT extends IntegrationTest {

    private static final String PROJECT = "java-rabbitmq-consumer-it";

    private static final String ORDERS_QUEUE = "java-rabbitmq-consumer-it-orders";

    private static final String RECEIVED_QUEUE = "java-rabbitmq-consumer-it-received";

    private static final String SOURCE_PATH = IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/demo/OrdersConsumer.java";

    /** The image the RabbitMQ API's own container tests run against. */
    private static final String RABBITMQ_IMAGE = "rabbitmq:3.8.19-alpine";

    private static final int AMQP_PORT = 5672;

    /** Covers the compile and the consumer's start after a forced synchronization. */
    private static final int CONSUMPTION_TIMEOUT_SECONDS = 90;

    private static final long RECEIVE_TIMEOUT_MILLIS = 2000;

    private static final String CONSUMER_SOURCE = """
            package demo;

            import jakarta.annotation.PostConstruct;
            import jakarta.annotation.PreDestroy;

            import org.eclipse.dirigible.sdk.component.Component;
            import org.eclipse.dirigible.sdk.messaging.Producer;
            import org.eclipse.dirigible.sdk.rabbitmq.Consumer;
            import org.eclipse.dirigible.sdk.rabbitmq.MessageHandler;

            @Component
            public class OrdersConsumer implements MessageHandler {

                @PostConstruct
                public void start() {
                    Consumer.startListening("%1$s", this);
                }

                @PreDestroy
                public void stop() {
                    Consumer.stopListening("%1$s", this);
                }

                @Override
                public void onMessage(String message) {
                    Producer.sendToQueue("%2$s", "received:" + message);
                }
            }
            """.formatted(ORDERS_QUEUE, RECEIVED_QUEUE);

    private static GenericContainer<?> rabbitmq;

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    /** Starts the broker and points the platform at it - before the context exists. */
    @BeforeAll
    static void startTheBroker() {
        rabbitmq = new GenericContainer<>(DockerImageName.parse(RABBITMQ_IMAGE)).withExposedPorts(AMQP_PORT)
                                                                                .waitingFor(Wait.forLogMessage(
                                                                                        ".*Server startup complete.*", 1));
        rabbitmq.start();
        // cleared by IntegrationTest.reloadConfigurations() once the class is done
        Configuration.set("DIRIGIBLE_RABBITMQ_CLIENT_URI", rabbitmq.getHost() + ":" + rabbitmq.getMappedPort(AMQP_PORT));
    }

    @AfterAll
    static void stopTheBroker() {
        rabbitmq.stop();
    }

    @AfterEach
    void unpublishTheConsumer() {
        // the generation's teardown runs the component's @PreDestroy, which stops the consumer
        if (repository.hasResource(SOURCE_PATH)) {
            repository.removeResource(SOURCE_PATH);
            synchronizationProcessor.forceProcessSynchronizers();
        }
    }

    @Test
    void aClientJavaHandlerConsumesTheQueue() {
        repository.createResource(SOURCE_PATH, CONSUMER_SOURCE.getBytes(StandardCharsets.UTF_8), false, "text/x-java", true);
        synchronizationProcessor.forceProcessSynchronizers();

        // a message sent before the consumer subscribed waits in the queue for it
        RabbitMQFacade.send(ORDERS_QUEUE, "order-1");

        String[] received = new String[1];
        Awaitility.await()
                  .pollInterval(1, TimeUnit.SECONDS)
                  .atMost(CONSUMPTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                  .until(() -> {
                      try {
                          received[0] = MessagingFacade.receiveFromQueue(RECEIVED_QUEUE, RECEIVE_TIMEOUT_MILLIS);
                          return true;
                      } catch (RuntimeException notYet) {
                          return false;
                      }
                  });

        assertEquals("received:order-1", received[0], "the Java handler must receive the message the queue was sent");
    }
}
