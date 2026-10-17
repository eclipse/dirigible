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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.awaitility.Awaitility;
import org.eclipse.dirigible.components.api.messaging.MessagingFacade;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.tenant.DirigibleTestTenant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * An instance's broker connections grow with its tenants, not with its listeners (#7875).
 *
 * <p>
 * Before, every subscription opened a connection of its own in every tenant: a generated
 * application suite with 130 handlers needed 260 connections for one tenant beside the default,
 * against a 300-connection quota on the smaller Amazon MQ classes. In the default (shared) mode the
 * client-Java listeners of a tenant ride one connection and the {@code .listener} artefacts
 * another, so this test deploys several of each, provisions a tenant at runtime, and asserts the
 * broker's own count - then round-trips a message in both tenants, because a connection that
 * carries no working subscription would make the count trivially small.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ListenerConnectionSharingIT extends IntegrationTest {

    private static final String PROJECT = "listener-connection-sharing-it";

    /** Client-Java handlers, every one a durable topic subscription per tenant. */
    private static final int JAVA_HANDLERS = 4;

    /** {@code .listener} artefacts, every one a topic subscription per tenant. */
    private static final int ARTEFACT_LISTENERS = 3;

    /**
     * What the deployment may add on top of the baseline with two tenants: one shared connection per
     * tenant for the client-Java handlers, one for every {@code .listener} artefact. Each subscription
     * on its own connection would add {@code (4 + 3) x 2 = 14}.
     */
    private static final int EXPECTED_ADDED_CONNECTIONS = 3;

    private static final int ROUND_TRIP_TIMEOUT_SECONDS = 90;
    private static final long RECEIVE_TIMEOUT_MILLIS = 2000;

    @Autowired
    private BrokerService broker;

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private TenantContext tenantContext;

    @Test
    void theConnectionsGrowWithTheTenantsNotWithTheListeners() throws Exception {
        int baseline = connections();

        deployJavaHandlers();
        deployArtefactListeners();
        synchronizationProcessor.forceProcessSynchronizers();
        assertRoundTrip(null, "default");

        DirigibleTestTenant tenant = new DirigibleTestTenant(PROJECT);
        createTenants(tenant);
        waitForTenantProvisioning(tenant);
        // The post-provisioning step subscribes the handlers in the new tenant, the synchronizer the
        // artefacts; the round trip proves both landed on a working connection.
        assertRoundTrip(tenant.getId(), "tenant");

        int added = connections() - baseline;
        assertThat(added).as("connections added for %d handlers and %d artefacts in two tenants", JAVA_HANDLERS, ARTEFACT_LISTENERS)
                         .isBetween(1, EXPECTED_ADDED_CONNECTIONS);

        // A further pass with nothing new to subscribe must not open anything.
        synchronizationProcessor.forceProcessSynchronizers();
        assertEquals(added, connections() - baseline, "an idle pass opens no connection");
    }

    /** What the broker itself counts - every client attached to it, over any transport. */
    private int connections() throws Exception {
        return broker.getBroker()
                     .getClients().length;
    }

    /**
     * Publish into the first handler's topic and draw its echo back off its queue, inside the given
     * tenant (the default one for {@code null}), retrying the whole exchange until the subscription is
     * open.
     */
    private void assertRoundTrip(String tenantId, String probe) {
        String topic = PROJECT + "-in-0";
        String queue = PROJECT + "-out-0";
        String message = "ping-" + probe + "-" + System.currentTimeMillis();
        String[] received = new String[1];
        Awaitility.await()
                  .pollInterval(1, TimeUnit.SECONDS)
                  .atMost(ROUND_TRIP_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                  .until(() -> exchange(tenantId, topic, queue, message, received));
        assertEquals("echo:" + message, received[0], "the handler must answer on the shared connection in " + probe);
    }

    private boolean exchange(String tenantId, String topic, String queue, String message, String[] received) throws Exception {
        if (tenantId == null) {
            return attempt(topic, queue, message, received);
        }
        return tenantContext.execute(tenantId, () -> attempt(topic, queue, message, received));
    }

    private static boolean attempt(String topic, String queue, String message, String[] received) {
        MessagingFacade.sendToTopic(topic, message);
        try {
            received[0] = MessagingFacade.receiveFromQueue(queue, RECEIVE_TIMEOUT_MILLIS);
            return true;
        } catch (RuntimeException notYet) {
            return false;
        }
    }

    /** Four client listeners, each echoing its topic onto its queue. */
    private void deployJavaHandlers() {
        for (int i = 0; i < JAVA_HANDLERS; i++) {
            String className = "Echo" + i;
            String source = """
                    package demo;
                    import org.eclipse.dirigible.sdk.component.Component;
                    import org.eclipse.dirigible.sdk.messaging.ListenerKind;
                    import org.eclipse.dirigible.sdk.messaging.MessageHandler;
                    import org.eclipse.dirigible.sdk.messaging.Producer;
                    @Component
                    public class %s implements MessageHandler {
                        @Override
                        public String destination() {
                            return "%s-in-%d";
                        }
                        @Override
                        public ListenerKind kind() {
                            return ListenerKind.TOPIC;
                        }
                        @Override
                        public void onMessage(String message) {
                            Producer.sendToQueue("%s-out-%d", "echo:" + message);
                        }
                    }
                    """.formatted(className, PROJECT, i, PROJECT, i);
            repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/demo/" + className + ".java",
                    source.getBytes(StandardCharsets.UTF_8), false, "text/x-java", true);
        }
    }

    /** Three {@code .listener} artefacts on topics of their own, handled in JavaScript. */
    private void deployArtefactListeners() {
        String handler = """
                export function onMessage(message) {
                }
                export function onError(error) {
                }
                """;
        repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/handler.js",
                handler.getBytes(StandardCharsets.UTF_8), false, "text/javascript", true);
        for (int i = 0; i < ARTEFACT_LISTENERS; i++) {
            String listener = """
                    {
                        "name": "%s-artefact-%d",
                        "kind": "T",
                        "handler": "%s/handler.js",
                        "description": "shares the artefacts' connection"
                    }
                    """.formatted(PROJECT, i, PROJECT);
            repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/artefact-" + i + ".listener",
                    listener.getBytes(StandardCharsets.UTF_8), false, "application/json", true);
        }
    }
}
