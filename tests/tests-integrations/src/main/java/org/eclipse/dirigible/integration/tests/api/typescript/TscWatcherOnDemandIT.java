/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api.typescript;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.List;

import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.base.ProjectDeployer;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The tsc watcher runs only once the registry holds TypeScript sources (#8004): the platform's own
 * content has none, so a booted instance runs no Node process, and publishing a TypeScript project
 * starts the watcher that transpiles it for {@code /services/ts}.
 */
class TscWatcherOnDemandIT extends IntegrationTest {

    private static final String PROJECT = "TscWatcherOnDemandIT";

    /** A watcher left by the previous test class's context is killed when that context closes. */
    private static final Duration PREVIOUS_CONTEXT_CLEANUP = Duration.ofSeconds(10);

    @Autowired
    private ProjectDeployer projectDeployer;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Test
    void startsTheWatcherOnlyOnceTypeScriptIsPublished() {
        await().atMost(PREVIOUS_CONTEXT_CLEANUP)
               .until(() -> tscWatchProcesses().isEmpty());

        projectDeployer.deploy(PROJECT);

        restAssuredExecutor.execute( //
                () -> given().when()
                             .get("/services/ts/" + PROJECT + "/hello.ts")
                             .then()
                             .statusCode(200)
                             .body(containsString("Hello from TypeScript")),
                60);
        assertFalse(tscWatchProcesses().isEmpty(), "the published TypeScript project must have started the tsc watcher");
    }

    private static List<ProcessHandle> tscWatchProcesses() {
        return ProcessHandle.current()
                            .descendants()
                            .filter(ProcessHandle::isAlive)
                            .filter(process -> process.info()
                                                      .commandLine()
                                                      .filter(commandLine -> commandLine.contains("tsc") && commandLine.contains("--watch"))
                                                      .isPresent())
                            .toList();
    }
}
