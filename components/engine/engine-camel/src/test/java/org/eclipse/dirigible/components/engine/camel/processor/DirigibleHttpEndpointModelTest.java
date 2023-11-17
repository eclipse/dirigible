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
package org.eclipse.dirigible.components.engine.camel.processor;

import org.apache.camel.Consumer;
import org.apache.camel.component.platform.http.HttpEndpointModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DirigibleHttpEndpointModelTest {

    @Test
    public void testCreateFromInputModel() {
        String uri = "test-uri";
        String verbs = "GET";
        Consumer consumer = mock(Consumer.class);
        HttpEndpointModel inputModel = new HttpEndpointModel(uri, verbs, consumer);

        var outputModel = DirigibleHttpEndpointModel.from(inputModel);
        assertEquals("/services/integrations/test-uri", outputModel.getUri(), "Unexpected URI");
        assertEquals(verbs, outputModel.getVerbs(), "Unexpected Verbs");
        assertEquals(consumer, outputModel.getConsumer(), "Unexpected Consumer");
    }
}
