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

import static org.eclipse.dirigible.components.extensions.aggregation.Modules.json;
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.stub;
import static org.eclipse.dirigible.components.extensions.aggregation.Modules.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.components.base.context.ThreadContextFacade;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The per-user role check and the de-duplication of projections.
 */
@ExtendWith(MockitoExtension.class)
class ProjectionsAggregatorTest {

    private static final String POINT = "test-projections";

    @Mock
    private ExtensionModuleReader reader;

    @BeforeEach
    void signInAsSalesUser() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        lenient().when(request.isUserInRole("Sales"))
                 .thenReturn(true);
        ThreadContextFacade.setUp();
        ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), request);
    }

    @AfterEach
    void signOut() {
        ThreadContextFacade.tearDown();
    }

    @Test
    void offers_a_projection_only_to_a_user_holding_one_of_its_roles() {
        stub(reader, POINT, json("{'id': 'everything', 'label': 'Everything', 'all': true}"),
                json("{'id': 'sales', 'label': 'Sales', 'roles': ['Sales', 'Manager']}"),
                json("{'id': 'finance', 'label': 'Finance', 'roles': ['Finance']}"), json("{'id': 'open', 'label': 'Open', 'roles': []}"));

        assertEquals(List.of("everything", "open", "sales"), values(aggregate(), "id"));
    }

    @Test
    void keeps_the_first_of_two_projections_with_one_id() {
        stub(reader, POINT, json("{'id': 'p', 'label': 'First'}"), json("{'id': 'p', 'label': 'Second'}"));

        assertEquals(List.of("First"), values(aggregate(), "label"));
    }

    private List<Map<String, Object>> aggregate() {
        return new ProjectionsAggregator(reader).aggregate(List.of(POINT));
    }
}
