/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.data.store.java.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.dirigible.sdk.db.Entity;
import org.eclipse.dirigible.sdk.db.Id;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

/**
 * {@link JavaEntityManager#beginBatch()} / {@link JavaEntityManager#endBatch()} (dirigible #7963):
 * a generation install rebuilds the {@link SessionFactory} once, not once per entity, and an entity
 * that breaks the batched rebuild is dropped without taking the others down.
 */
class JavaEntityManagerBatchTest {

    private static final String ORDER = "proj::" + Order.class.getName();
    private static final String CUSTOMER = "proj::" + Customer.class.getName();
    private static final String BROKEN = "proj::" + Broken.class.getName();

    @Test
    void aBatchRebuildsOnceOverAllItsChanges() {
        CountingManager manager = new CountingManager();
        manager.register(ORDER, Order.class);
        manager.builds.clear();

        manager.beginBatch();
        manager.unregister(ORDER);
        manager.register(ORDER, Order.class);
        manager.register(CUSTOMER, Customer.class);
        assertTrue(manager.builds.isEmpty(), "no rebuild inside a batch");
        manager.endBatch();

        assertEquals(List.of(List.of("Order", "Customer")), manager.builds);
        assertSame(manager.lastFactory(), manager.getSessionFactory());
    }

    @Test
    void nestedBatchesRebuildOnlyWhenTheOutermostEnds() {
        CountingManager manager = new CountingManager();

        manager.beginBatch();
        manager.beginBatch();
        manager.register(ORDER, Order.class);
        manager.endBatch();
        assertTrue(manager.builds.isEmpty(), "the inner end does not rebuild");
        manager.endBatch();

        assertEquals(1, manager.builds.size());
    }

    @Test
    void aBatchWithoutChangesDoesNotRebuild() {
        CountingManager manager = new CountingManager();

        manager.beginBatch();
        manager.unregister(ORDER);
        manager.endBatch();

        assertTrue(manager.builds.isEmpty());
    }

    @Test
    void outsideABatchEveryChangeRebuilds() {
        CountingManager manager = new CountingManager();

        manager.register(ORDER, Order.class);
        manager.register(CUSTOMER, Customer.class);
        manager.unregister(ORDER);

        assertEquals(List.of(List.of("Order"), List.of("Order", "Customer"), List.of("Customer")), manager.builds);
    }

    @Test
    void aFailedRegisterOutsideABatchLeavesNoRegistrationBehind() {
        CountingManager manager = new CountingManager();
        manager.poisoned.add("Broken");
        manager.register(ORDER, Order.class);

        assertThrows(IllegalStateException.class, () -> manager.register(BROKEN, Broken.class));

        assertTrue(manager.find(BROKEN)
                          .isEmpty(),
                "a registration whose rebuild failed must not break every later rebuild");
        manager.register(CUSTOMER, Customer.class);
        assertEquals(List.of("Order", "Customer"), manager.builds.get(manager.builds.size() - 1));
    }

    @Test
    void anEntityThatBreaksTheBatchIsDroppedAndTheOthersAreInstalled() {
        CountingManager manager = new CountingManager();
        manager.poisoned.add("Broken");
        manager.register(ORDER, Order.class);
        SessionFactory previous = manager.getSessionFactory();

        manager.beginBatch();
        manager.register(BROKEN, Broken.class);
        manager.register(CUSTOMER, Customer.class);
        manager.endBatch();

        assertTrue(manager.find(BROKEN)
                          .isEmpty());
        assertEquals(2, manager.size());
        assertEquals(List.of("Order", "Customer"), manager.builds.get(manager.builds.size() - 1));
        assertSame(manager.lastFactory(), manager.getSessionFactory());
        verify(previous).close();
        manager.factories.subList(0, manager.factories.size() - 1)
                         .forEach(factory -> verify(factory).close());
        verify(manager.lastFactory(), never()).close();
    }

    @Test
    void aDatabaseFailureKeepsTheRegistrationsAndThePreviousFactory() {
        CountingManager manager = new CountingManager();
        manager.register(ORDER, Order.class);
        SessionFactory previous = manager.getSessionFactory();
        manager.databaseDown = true;

        manager.beginBatch();
        manager.register(CUSTOMER, Customer.class);
        assertThrows(IllegalStateException.class, manager::endBatch);

        assertEquals(2, manager.size(), "no entity is to blame, so none is dropped");
        assertSame(previous, manager.getSessionFactory());
        verify(previous, never()).close();
    }

    @Test
    void endingABatchThatWasNeverBegunIsRefused() {
        assertThrows(IllegalStateException.class, () -> new CountingManager().endBatch());
    }

    /** Records each build's entity names; fails a build mapping a poisoned entity, or every build. */
    private static final class CountingManager extends JavaEntityManager {

        final List<List<String>> builds = new ArrayList<>();
        final List<SessionFactory> factories = new ArrayList<>();
        final Set<String> poisoned = new HashSet<>();
        boolean databaseDown;

        CountingManager() {
            super(null, null, null);
        }

        @Override
        SessionFactory buildSessionFactory(List<RegisteredEntity> entities) {
            if (databaseDown) {
                throw new IllegalStateException("database unavailable");
            }
            List<String> names = entities.stream()
                                         .map(RegisteredEntity::entityName)
                                         .toList();
            if (names.stream()
                     .anyMatch(poisoned::contains)) {
                throw new IllegalStateException("broken mapping");
            }
            builds.add(names);
            SessionFactory factory = mock(SessionFactory.class);
            factories.add(factory);
            return factory;
        }

        SessionFactory lastFactory() {
            return factories.get(factories.size() - 1);
        }
    }

    @Entity
    public static class Order {
        @Id
        public Integer Id;
    }

    @Entity
    public static class Customer {
        @Id
        public Integer Id;
    }

    @Entity
    public static class Broken {
        @Id
        public Integer Id;
    }
}
