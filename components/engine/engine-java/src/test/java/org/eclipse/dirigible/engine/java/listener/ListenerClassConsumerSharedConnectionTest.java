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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.jms.Connection;
import jakarta.jms.ExceptionListener;
import jakarta.jms.JMSException;
import jakarta.jms.JMSSecurityException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import jakarta.jms.TopicSubscriber;

/**
 * In the shared connection mode (#7875) the subscriptions of a scope - a tenant, or the global
 * destinations - ride ONE broker connection, each as a session on it. The connection count of an
 * instance is then the number of scopes, however many handlers a generated application declares,
 * and the connection is what a broker meters.
 *
 * <p>
 * Everything the per-listener mode guarantees has to hold on the shared connection too: a durable
 * subscription keeps a stable identity across reloads, a refusal is retried without ever opening a
 * second consumer on a destination, and a lost connection is re-established by the reconciliation
 * pass - now for the whole scope at once.
 */
class ListenerClassConsumerSharedConnectionTest {

    /** A typed listener on a tenant-scoped topic. */
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

    /** A typed listener on a tenant-scoped queue. */
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

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    /** One connection the factory handed out: its client id and the exception listener it carries. */
    private record Opened(Connection connection, ExceptionListener listener, String clientId) {
    }

    private final Map<String, Tenant> provisionedTenants = new LinkedHashMap<>();
    private final AtomicReference<String> currentTenantId = new AtomicReference<>();
    private final List<Opened> opened = new ArrayList<>();
    private final List<Session> sessions = new ArrayList<>();

    /** While set, the broker refuses every connection the way a starting broker does. */
    private final AtomicBoolean brokerRefusingConnections = new AtomicBoolean();

    /** While set, the connection is handed out and the session on it is refused. */
    private final AtomicBoolean brokerRefusingSessions = new AtomicBoolean();

    private ListenerClassConsumer consumer;
    private ListAppender<ILoggingEvent> appender;
    private Logger consumerLogger;

    @BeforeEach
    @SuppressWarnings("rawtypes")
    void setUp() throws Exception {
        addTenant(DEFAULT_TENANT_ID);
        addTenant("acme");

        ComponentContainer componentContainer = mock(ComponentContainer.class);
        when(componentContainer.instanceOf(EventsHandler.class)).thenReturn(Optional.of(new EventsHandler()));
        when(componentContainer.instanceOf(OrdersHandler.class)).thenReturn(Optional.of(new OrdersHandler()));
        when(componentContainer.instanceOf(GlobalEventsHandler.class)).thenReturn(Optional.of(new GlobalEventsHandler()));

        ActiveMQConnectionArtifactsFactory connectionFactory = mock(ActiveMQConnectionArtifactsFactory.class);
        when(connectionFactory.createConnection(any(), any())).thenAnswer(invocation -> {
            if (brokerRefusingConnections.get()) {
                throw new IllegalStateException("Failed to create connection to ActiveMQ");
            }
            Connection connection = mock(Connection.class);
            opened.add(new Opened(connection, invocation.getArgument(0), invocation.getArgument(1)));
            return connection;
        });
        when(connectionFactory.createSession(any())).thenAnswer(invocation -> {
            if (brokerRefusingSessions.get()) {
                throw new JMSSecurityException("Destination authorization refused");
            }
            Session session = mock(Session.class);
            when(session.createTopic(anyString())).thenAnswer(call -> {
                Topic topic = mock(Topic.class);
                when(topic.getTopicName()).thenReturn(call.getArgument(0));
                return topic;
            });
            when(session.createQueue(anyString())).thenReturn(mock(Queue.class));
            when(session.createDurableSubscriber(any(), anyString())).thenReturn(mock(TopicSubscriber.class));
            when(session.createConsumer(any())).thenReturn(mock(MessageConsumer.class));
            sessions.add(session);
            return session;
        });

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
                mock(Tenant.class), tenantConfigurationService, destinationNameManager, ListenerConnectionMode.SHARED);

        appender = new ListAppender<>();
        appender.start();
        consumerLogger = (Logger) LoggerFactory.getLogger(ListenerClassConsumer.class);
        consumerLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        consumerLogger.detachAppender(appender);
    }

    @Test
    void theSubscriptionsOfATenantShareOneConnection() {
        load(EventsHandler.class);
        load(OrdersHandler.class);

        assertEquals(List.of("dirigible-java-tenant-default-tenant", "dirigible-java-tenant-acme"), clientIds(),
                "two handlers in two tenants open one connection per tenant, not one per subscription");
        assertEquals(4, sessions.size(), "every subscription is a session on its tenant's connection");
    }

    @Test
    void theGlobalDestinationsRideAConnectionOfTheirOwn() {
        load(GlobalEventsHandler.class);

        assertEquals(List.of("dirigible-java-global"), clientIds());
    }

    /**
     * On a shared connection the client id is the scope's, so what tells the durable subscriptions of a
     * tenant's handlers apart is the subscription name - still the handler plus the physical
     * destination, and still the same on every reload.
     */
    @Test
    void aDurableSubscriptionIsNamedAfterTheHandlerAndItsDestinationAndKeepsThatNameAcrossReloads() throws Exception {
        load(EventsHandler.class);
        load(EventsHandler.class);

        ArgumentCaptor<String> names = ArgumentCaptor.forClass(String.class);
        verify(sessions.get(1)).createDurableSubscriber(any(), names.capture());
        verify(sessions.get(3)).createDurableSubscriber(any(), names.capture());

        String first = names.getAllValues()
                            .get(0);
        assertTrue(first.contains(EventsHandler.class.getName()
                                                     .replace('$', '_')),
                "the name must identify the handler, got: " + first);
        assertTrue(first.contains("acme___events"),
                "the name must identify the tenant-resolved destination (sanitised for the broker), got: " + first);
        assertEquals(first, names.getAllValues()
                                 .get(1),
                "a reload must resume the same durable subscription");
    }

    @Test
    void aReloadReusesTheConnectionAndClosesOnlyTheOldSessions() throws Exception {
        load(EventsHandler.class);
        load(EventsHandler.class);

        assertEquals(2, opened.size(), "a reload opens no connection - the tenants' are already up");
        verify(sessions.get(0)).close();
        verify(sessions.get(1)).close();
        verify(sessions.get(2), never()).close();
        verify(sessions.get(3), never()).close();
        verify(opened.get(0)
                     .connection(),
                never()).close();
    }

    @Test
    void anUnloadClosesTheSessionsAndKeepsTheConnectionForTheOtherHandlers() throws Exception {
        load(EventsHandler.class);
        load(OrdersHandler.class);

        consumer.onClassUnloaded(loaded(EventsHandler.class));

        verify(sessions.get(0)).close();
        verify(sessions.get(1)).close();
        verify(sessions.get(2), never()).close();
        verify(opened.get(0)
                     .connection(),
                never()).close();
    }

    @Test
    void aTenantProvisionedLaterGetsAConnectionOfItsOwn() {
        load(EventsHandler.class);

        addTenant("beta");
        consumer.execute();

        assertEquals(List.of("dirigible-java-tenant-default-tenant", "dirigible-java-tenant-acme", "dirigible-java-tenant-beta"),
                clientIds());
        assertEquals(3, sessions.size());
    }

    /**
     * While the broker refuses the scope's connection there is nothing to attempt per handler, and a
     * suite of a hundred handlers must not report a hundred refusals of the same connection.
     */
    @Test
    void aRefusedSharedConnectionIsReportedOncePerScopeAndRetriedByTheNextPass() {
        brokerRefusingConnections.set(true);
        load(EventsHandler.class);
        load(OrdersHandler.class);

        assertEquals(0, sessions.size(), "no subscription is attempted without the scope's connection");
        assertEquals(2, warnings("Failed to open the shared broker connection").size(),
                "one report per scope, not one per handler and pass");

        brokerRefusingConnections.set(false);
        consumer.reconcile();

        assertEquals(2, opened.size());
        assertEquals(4, sessions.size(), "the next pass subscribes every handler of both tenants");
    }

    @Test
    void aRefusedSessionClosesTheSessionAndKeepsTheSharedConnection() throws Exception {
        brokerRefusingSessions.set(true);
        load(EventsHandler.class);

        assertEquals(2, opened.size(), "the scope's connection was opened");
        verify(opened.get(0)
                     .connection(),
                never()).close();

        brokerRefusingSessions.set(false);
        consumer.reconcile();

        assertEquals(2, opened.size(), "the retry reuses the connection");
        assertEquals(2, sessions.size(), "the retry opens exactly one session per tenant");
    }

    /**
     * The loss of a tenant's connection is the loss of every handler's subscription in that tenant: the
     * pass closes the dead connection, opens a new one, and subscribes them all again - the other
     * tenant untouched.
     */
    @Test
    void aLostSharedConnectionIsClosedByThePassAndEveryHandlerOfTheTenantIsSubscribedAgain() throws Exception {
        load(EventsHandler.class);
        load(OrdersHandler.class);
        Opened acme = openedFor("dirigible-java-tenant-acme");

        lose(acme);
        verify(acme.connection(), never()).close();

        consumer.reconcile();

        verify(acme.connection()).close();
        assertEquals(List.of("dirigible-java-tenant-default-tenant", "dirigible-java-tenant-acme", "dirigible-java-tenant-acme"),
                clientIds(), "a new connection for the lost tenant, none for the other");
        assertEquals(6, sessions.size(), "both handlers subscribed again in the lost tenant");
        verify(openedFor("dirigible-java-tenant-default-tenant").connection(), never()).close();
    }

    @Test
    void aLossReportedTwiceIsHandledOnce() throws Exception {
        load(EventsHandler.class);
        Opened acme = openedFor("dirigible-java-tenant-acme");

        lose(acme);
        lose(acme);
        consumer.reconcile();

        verify(acme.connection(), times(1)).close();
        assertEquals(3, opened.size());
    }

    @Test
    void shutdownClosesEverySessionAndEverySharedConnection() throws Exception {
        load(EventsHandler.class);
        load(GlobalEventsHandler.class);

        consumer.shutdown();

        for (Session session : sessions) {
            verify(session).close();
        }
        for (Opened connection : opened) {
            verify(connection.connection()).close();
        }
    }

    private List<String> clientIds() {
        return opened.stream()
                     .map(Opened::clientId)
                     .toList();
    }

    private Opened openedFor(String clientId) {
        return opened.stream()
                     .filter(candidate -> clientId.equals(candidate.clientId()))
                     .reduce((first, second) -> second)
                     .orElseThrow();
    }

    private List<ILoggingEvent> warnings(String containing) {
        return appender.list.stream()
                            .filter(event -> event.getLevel() == Level.WARN && event.getFormattedMessage()
                                                                                    .contains(containing))
                            .toList();
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
        consumer.onClassLoaded(loaded(handler));
    }

    private static LoadedClass loaded(Class<?> handler) {
        return new LoadedClass("sample", handler.getName(), handler, handler.getClassLoader());
    }
}
