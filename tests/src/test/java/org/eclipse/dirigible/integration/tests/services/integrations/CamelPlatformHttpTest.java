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
package org.eclipse.dirigible.integration.tests.services.integrations;

import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CamelPlatformHttpTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postToJsTest() throws Exception {
        String requestBody = "initial message";

        String actualResponseBody = mockMvc.perform(MockMvcRequestBuilders.post("/services/integrations/camelTest")
                                                                          .content(requestBody)
                                                                          .contentType(MediaType.APPLICATION_JSON)
                                                                          .accept(MediaType.APPLICATION_JSON))
                                           .andExpect(status().isOk())
                                           .andReturn()
                                           .getResponse()
                                           .getContentAsString();

        String expectedResponseBody = requestBody + " -> calledFromCamel.js handled this message";

        assertEquals("Unexpected response from camel platform http endpoint", expectedResponseBody, actualResponseBody);

    }

}
