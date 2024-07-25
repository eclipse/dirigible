/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.services.integrations;

import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CamelPlatformHttpTest extends IntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelPlatformHttpTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postToJsTest() throws Exception {
        String requestBody = "initial message";

        await().atMost(25, TimeUnit.SECONDS)
               .pollInterval(1, TimeUnit.SECONDS)
               .until(() -> {
                   try {
                       verifyCamelEndpoint(requestBody);
                       return true;
                   } catch (RuntimeException | AssertionError err) {
                       LOGGER.warn("Failed to verify camel endpoint. Will try again until the timeout is reached.", err);
                       return false;
                   }
               });
    }

    private void verifyCamelEndpoint(String requestBody) throws Exception {
        String actualResponseBody = mockMvc.perform(MockMvcRequestBuilders.post("/services/integrations/camelTest")
                                                                          .content(requestBody)
                                                                          .contentType(MediaType.APPLICATION_JSON)
                                                                          .accept(MediaType.APPLICATION_JSON))
                                           .andExpect(status().isOk())
                                           .andReturn()
                                           .getResponse()
                                           .getContentAsString();

        String expectedResponseBody = requestBody + " -> calledFromCamel.mjs handled this message";

        assertEquals("Unexpected response from camel platform http endpoint", expectedResponseBody, actualResponseBody);
    }

}
