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

import ch.qos.logback.classic.Level;
import org.eclipse.dirigible.integration.tests.ui.tests.UserInterfaceIntegrationTest;
import org.eclipse.dirigible.tests.IDE;
import org.eclipse.dirigible.tests.logging.LogsAsserter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class CamelDirigibleTwoStepsJSInvokerIT extends UserInterfaceIntegrationTest {

    private static final String TEST_PROJECT_FOLDER_PATH = "CamelDirigibleTwoStepsJSInvokerIT";
    private static final String TEST_PROJECT_NAME = "call-dirigible-js-two-steps";

    @Autowired
    private IDE ide;

    private LogsAsserter logsAsserter;

    @BeforeEach
    void setUp() {
        this.logsAsserter = new LogsAsserter("TwoStepsLogger", Level.INFO);
    }

    @Test
    void testInvokeJSWithCronRoute() {
        ide.createAndPublishProjectFromResources(TEST_PROJECT_NAME, TEST_PROJECT_FOLDER_PATH, Map.of("<project_name>", TEST_PROJECT_NAME));

        assertBodyIsPassedAndHandledProperlyByJSHandler();
    }

    private void assertBodyIsPassedAndHandledProperlyByJSHandler() {
        // this log message is expected to be logged by the final camel log step
        await().atMost(20, TimeUnit.SECONDS)
               .until(() -> logsAsserter.containsMessage("Completed execution. Body: [THIS IS A TEST BODY]", Level.INFO));
    }

    @Disabled("Disabled since there is a bug.")
    @Test
    void testInvokeJSWithEndpointRoute() {
        // TODO to be implemented once https://github.com/eclipse/dirigible/issues/4392 is fixed
    }

}
