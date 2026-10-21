/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.aggregation;

import static org.eclipse.dirigible.components.extensions.aggregation.Modules.object;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

/**
 * The {@code order} is read as JavaScript's {@code parseInt} read it.
 */
class ExtensionOrderingTest {

    @Test
    void reads_the_order_as_parseInt_does() {
        assertEquals(OptionalInt.of(10), ExtensionOrdering.order(object("{'order': 10}")));
        assertEquals(OptionalInt.of(10), ExtensionOrdering.order(object("{'order': 10.7}")));
        assertEquals(OptionalInt.of(-2), ExtensionOrdering.order(object("{'order': -2}")));
        assertEquals(OptionalInt.of(15), ExtensionOrdering.order(object("{'order': ' 15px'}")));
    }

    @Test
    void treats_an_order_without_an_integer_as_absent() {
        assertEquals(OptionalInt.empty(), ExtensionOrdering.order(object("{}")));
        assertEquals(OptionalInt.empty(), ExtensionOrdering.order(object("{'order': null}")));
        assertEquals(OptionalInt.empty(), ExtensionOrdering.order(object("{'order': 'first'}")));
    }
}
