/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.java.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.dirigible.components.base.callable.CallableResultAndException;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.eclipse.dirigible.components.configurations.tenant.TenantConfigurationService;
import org.eclipse.dirigible.components.listeners.config.ActiveMQConnectionArtifactsFactory;
import org.eclipse.dirigible.components.listeners.config.ListenerConnectionMode;
import org.eclipse.dirigible.components.listeners.service.DestinationNameManager;
import org.eclipse.dirigible.components.listeners.service.TenantPropertyManager;
import org.eclipse.dirigible.engine.java.component.ComponentContainer;
import org.eclipse.dirigible.engine.java.spi.LoadedClass;
import org.eclipse.dirigible.sdk.messaging.ListenerKind;
import org.eclipse.dirigible.sdk.messaging.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.jms.Connection;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.TopicSubscriber;

/**
 * A topic subscription whose broker connection is gone is subscribed again by the next
 * reconciliation pass (#7851).
 *
 * <p>
 * Its connection carries a client id, so the transport does not reconnect it: over a
 * {@code failover:} URL that reconnect replayed the client id while the broker still held the
 * previous socket, was refused, and looped. The connection's exception listener is therefore the
 * only signal that it is gone, and these tests deliver that signal the way the factory does - by
 * invoking the listener it was given.
 *
 * <p>
 * Pinned in the per-listener connection mode, where a loss is one subscription's;
 * {@link ListenerClassConsumerSharedConnectionTest} covers the loss of a scope's shared connection.
 */
class ListenerClassConsumerConnectionLossTest {

    /** A typed listener on a tenant-scoped topic - durable, so its connection carries a client id. */
    static class EventsHandler implements MessageHandler {

        @Override
        public String destination() {
            return "events";
        }

        @Override
        public ListenerKind kind() {
            return ListenerKind.TOPIC;
        }

        @Override
        public void onMessage(String message) {
            // The subscription, not the dispatch, is what this test is about.
        }
    }

    /** A typed listener on a topic shared with another deployment. */
    static class GlobalEventsHandler implements MessageHandler {

        @Override
        public String destination() {
            return DestinationNameManager.GLOBAL_MARKER + "codbex.events";
        }

        @Override
        public ListenerKind kind() {
            return ListenerKind.TOPIC;
        }

        @Override
        public void onMessage(String message) {
            // The subscription, not the dispatch, is what this test is about.
        }
    }

    /** A queue listener: no client id, so the transport keeps reconnecting it by itself. */
    static class OrdersHandler implements MessageHandler {

        @Override
        public String destination() {
            return "orders";
        }

        @Override
        public void onMessage(String message) {
            // The subscription, not the dispatch, is what this test is about.
        }
    }

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    /** One connection the factory handed out, and the exception listener it was handed with it. */
    private record Opened(Connection connection, ExceptionListener listener, String tenantId) {
    }

    private final Map<String, Tenant> provisionedTenants = new LinkedHashMap<>();

    private final AtomicReference<String> currentTenantId = new AtomicReference<>();

    private final List<Opened> opened = new ArrayList<>();

    private Session session;
    private ListenerClassConsumer consumer;

    @BeforeEach
    @SuppressWarnings("rawtypes")
    void setUp() throws Exception {
        addTenant(DEFAULT_TENANT_ID);
        addTenant("acme");

        ComponentContainer componentContainer = mock(ComponentContainer.class);
        when(componentContainer.instanceOf(EventsHandler.class)).thenReturn(Optional.of(new EventsHandler()));
        when(componentContainer.instanceOf(GlobalEventsHandler.class)).thenReturn(Optional.of(new GlobalEventsHandler()));
        when(componentContainer.instanceOf(OrdersHandler.class)).thenReturn(Optional.of(new OrdersHandler()));

        session = mock(Session.class);
        ActiveMQConnectionArtifactsFactory connectionFactory = mock(ActiveMQConnectionArtifactsFactory.class);
        when(connectionFactory.createConnection(any(), any())).thenAnswer(invocation -> {
            Connection connection = mock(Connection.class);
            opened.add(new Opened(connection, invocation.getArgument(0), currentTenantId.get()));
            return connection;
        });
        when(connectionFactory.createSession(any())).thenReturn(session);
        when(session.createTopic(anyString())).thenReturn(mock(Topic.class));
        when(session.createQueue(anyString())).thenReturn(mock(Queue.class));
        when(session.createDurableSubscriber(any(), anyString())).thenReturn(mock(TopicSubscriber.class));
        when(session.createConsumer(any())).thenReturn(mock(MessageConsumer.class));

        TenantContext tenantContext = mock(TenantContext.class);
        when(tenantContext.getCurrentTenant()).thenAnswer(invocation -> provisionedTenants.get(currentTenantId.get()));
        when(tenantContext.executeForEachTenant(any())).thenAnswer(invocation -> {
            for (String tenantId : new ArrayList<>(provisionedTenants.keySet())) {
                currentTenantId.set(tenantId);
                ((CallableResultAndException) invocation.getArgument(0)).call();
            }
            currentTenantId.set(null);
            return List.of();
        });

        DestinationNameManager destinationNameManager = mock(DestinationNameManager.class);
        when(destinationNameManager.toTenantName(anyString())).thenAnswer(invocation -> {
            String logicalName = invocation.getArgument(0);
            if (DestinationNameManager.isGlobal(logicalName)) {
                return logicalName.substring(DestinationNameManager.GLOBAL_MARKER.length());
            }
            String tenantId = currentTenantId.get();
            return DEFAULT_TENANT_ID.equals(tenantId) ? logicalName : tenantId + "###" + logicalName;
        });

        TenantConfigurationService tenantConfigurationService = mock(TenantConfigurationService.class);
        when(tenantConfigurationService.resolveInjectableForCurrentTenant()).thenReturn(new HashMap<>());

        consumer = new ListenerClassConsumer(componentContainer, connectionFactory, tenantContext, mock(TenantPropertyManager.class),
                mock(Tenant.class), tenantConfigurationService, destinationNameManager, ListenerConnectionMode.PER_LISTENER);
    }

    @Test
    void aLostTopicConnectionIsClosedAndItsTenantSubscribedAgainByTheNextPass() throws Exception {
        load(EventsHandler.class);
        Opened acme = openedIn("acme");

        lose(acme);
        consumer.reconcile();

        verify(acme.connection()).close();
        verify(session, times(3)).createDurableSubscriber(any(), anyString());
        verify(session, times(2)).createTopic("acme###events");
        // The other tenant kept its subscription: losing one scope does not tear down the rest.
        verify(session, times(1)).createTopic("events");
        verify(openedIn(DEFAULT_TENANT_ID).connection(), never()).close();
    }

    /**
     * Closed by the pass, not by the exception listener: that thread belongs to the dying connection.
     */
    @Test
    void theLossIsClosedByThePassNotOnTheExceptionThread() throws Exception {
        load(EventsHandler.class);
        Opened acme = openedIn("acme");

        lose(acme);

        verify(acme.connection(), never()).close();
    }

    @Test
    void aLostGlobalTopicConnectionIsSubscribedAgainByTheNextPass() throws Exception {
        load(GlobalEventsHandler.class);
        assertEquals(1, opened.size(), "a global destination is subscribed once for the deployment");
        Opened global = opened.get(0);

        lose(global);
        consumer.reconcile();

        verify(global.connection()).close();
        verify(session, times(2)).createTopic("codbex.events");
    }

    /** Every connection of a scope may report the same outage; only the first one may act on it. */
    @Test
    void aLossReportedTwiceIsHandledOnce() throws Exception {
        load(EventsHandler.class);
        Opened acme = openedIn("acme");

        lose(acme);
        lose(acme);
        consumer.reconcile();

        verify(acme.connection(), times(1)).close();
        verify(session, times(2)).createTopic("acme###events");
    }

    /** A queue connection has no client id, and its transport still reconnects it by itself. */
    @Test
    void aQueueConnectionErrorDoesNotReSubscribe() throws Exception {
        load(OrdersHandler.class);
        Opened acme = openedIn("acme");

        lose(acme);
        consumer.reconcile();

        verify(acme.connection(), never()).close();
        verify(session, times(1)).createQueue("acme###orders");
    }

    /** A loss reported for a registration a republish replaced must not touch the replacement. */
    @Test
    void aLossOfASupersededRegistrationIsIgnored() throws Exception {
        load(EventsHandler.class);
        Opened stale = openedIn("acme");
        load(EventsHandler.class);
        int afterRepublish = opened.size();

        lose(stale);
        consumer.reconcile();

        assertEquals(afterRepublish, opened.size(), "nothing was subscribed again");
    }

    private Opened openedIn(String tenantId) {
        return opened.stream()
                     .filter(candidate -> tenantId.equals(candidate.tenantId()))
                     .reduce((first, second) -> second)
                     .orElseThrow();
    }

    private static void lose(Opened opened) {
        opened.listener()
              .onException(new JMSException("Reconnect attempts of 0 exceeded"));
    }

    private void addTenant(String tenantId) {
        Tenant tenant = mock(Tenant.class);
        when(tenant.getId()).thenReturn(tenantId);
        when(tenant.isDefault()).thenReturn(DEFAULT_TENANT_ID.equals(tenantId));
        provisionedTenants.put(tenantId, tenant);
    }

    private void load(Class<?> handler) {
        consumer.onClassLoaded(new LoadedClass("sample", handler.getName(), handler, handler.getClassLoader()));
    }
}
