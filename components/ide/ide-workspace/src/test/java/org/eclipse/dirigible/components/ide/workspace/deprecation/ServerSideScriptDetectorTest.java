/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.deprecation;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.eclipse.dirigible.components.ide.workspace.deprecation.ServerSideScripts.Kind;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.local.LocalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * The rule that tells server-side JavaScript and TypeScript from what a browser loads (#8049).
 */
class ServerSideScriptDetectorTest {

    private static final String PROJECT = "/registry/public/shop";

    @TempDir
    Path root;

    private IRepository repository;

    @BeforeEach
    void createRepository() {
        repository = new LocalRepository(root.toString(), true);
    }

    @Test
    void countsTypeScriptAndJavaScriptModulesButNotDeclarations() {
        write("/api/orders.ts", "export function list() { return []; }");
        write("/api/orders.d.ts", "export declare function list(): any[];");
        write("/api/legacy.mjs", "export default {};");

        ServerSideScripts scripts = detect();

        assertThat(scripts.count(Kind.TYPESCRIPT_FILE)).isEqualTo(1);
        assertThat(scripts.count(Kind.JAVASCRIPT_FILE)).isEqualTo(1);
        assertThat(scripts.describe()).isEqualTo("1 TypeScript file, 1 JavaScript file");
    }

    @Test
    void countsAJavaScriptFileThatImportsTheSdkButNotItsTranspiledTwin() {
        write("/api/customers.js", "import { query } from \"sdk/db\";\nexport const all = () => query.execute('SELECT 1');");
        write("/api/invoices.js", "const rs = require('@aerokit/sdk/http/rs');");
        write("/api/orders.ts", "import { response } from \"@aerokit/sdk/http\";");
        write("/api/orders.js", "import { response } from \"@aerokit/sdk/http\";");

        ServerSideScripts scripts = detect();

        assertThat(scripts.count(Kind.JAVASCRIPT_FILE)).isEqualTo(2);
        assertThat(scripts.count(Kind.TYPESCRIPT_FILE)).isEqualTo(1);
    }

    @Test
    void ignoresTheJavaScriptABrowserLoads() {
        write("/gen/ui/js/app.js", "angular.module('app', ['blimpKit']).controller('AppController', ($scope, $http) => {"
                + " $http.get('/services/java/shop/OrderController'); });");
        write("/gen/ui/js/stores/orders.js", "import { createStore } from './store.js';\nexport const orders = createStore('sdk/orders');");
        write("/ui/configs/perspective.json", "{\"id\": \"shop\"}");

        assertThat(detect().isEmpty()).isTrue();
    }

    @Test
    void ignoresNodeModulesAndHiddenFolders() {
        write("/node_modules/lib/index.ts", "export const x = 1;");
        write("/.settings/tool.ts", "export const y = 2;");

        assertThat(detect().isEmpty()).isTrue();
    }

    @Test
    void countsAnExtensionWhoseModuleIsAScript() {
        write("/extensions/menu.extension", "{\"module\": \"shop/ui/menu.js\", \"extensionPoint\": \"shop-menu\"}");
        write("/extensions/perspective.extension", "{\"module\": \"shop/ui/perspective.json\", \"extensionPoint\": \"perspectives\"}");
        write("/extensions/broken.extension", "not json");

        ServerSideScripts scripts = detect();

        assertThat(scripts.count(Kind.EXTENSION_MODULE)).isEqualTo(1);
        assertThat(scripts.skippedWithoutEngine()).isEqualTo(1);
    }

    @Test
    void countsTheArtefactsThatNameAScriptHandler() {
        write("/jobs/cleanup.job", "{\"expression\": \"0 0 * * * ?\", \"handler\": \"shop/jobs/cleanup.ts\"}");
        write("/jobs/report.job", "{\"expression\": \"0 0 * * * ?\", \"handler\": \"gen.jobs.shop.ReportJob\", \"engine\": \"java\"}");
        write("/events/orders.listener", "{\"name\": \"orders\", \"kind\": \"Q\", \"handler\": \"shop/events/orders.mjs\"}");
        write("/ws/chat.websocket", "{\"endpoint\": \"chat\", \"handler\": \"shop/ws/chat.js\"}");
        write("/process/approve.bpmn", """
                <serviceTask id="notify" flowable:delegateExpression="${JSTask}">
                  <extensionElements>
                    <flowable:field name="handler"><flowable:string><![CDATA[shop/process/notify.ts]]></flowable:string></flowable:field>
                  </extensionElements>
                </serviceTask>""");
        write("/process/ship.bpmn", """
                <serviceTask id="ship" flowable:class="gen.process.shop.ts.ShipDelegate"/>
                <userTask id="review" flowable:formKey="/services/web/shop/gen/forms/review/index.html"/>""");
        write("/routes/import.camel",
                "- route:\n    from:\n      uri: cron:tab\n      steps:\n        - to:\n            uri: dirigible-java-script\n"
                        + "            parameters:\n              javaScriptPath: shop/routes/import.ts\n");

        ServerSideScripts scripts = detect();

        assertThat(scripts.count(Kind.JOB)).isEqualTo(1);
        assertThat(scripts.count(Kind.LISTENER)).isEqualTo(1);
        assertThat(scripts.count(Kind.WEBSOCKET)).isEqualTo(1);
        assertThat(scripts.count(Kind.PROCESS)).isEqualTo(1);
        assertThat(scripts.count(Kind.ROUTE)).isEqualTo(1);
        assertThat(scripts.skippedWithoutEngine()).isEqualTo(5);
        assertThat(scripts.describe()).isEqualTo("1 job, 1 listener, 1 websocket, 1 process, 1 route");
    }

    @Test
    void findsNothingInAJavaOnlyProject() {
        write("/project.json", "{\"guid\": \"shop\"}");
        write("/gen/api/shop/OrderController.java", "package gen.api.shop; public class OrderController {}");
        write("/gen/ui/js/app.js", "angular.module('app', []);");
        write("/jobs/report.job", "{\"expression\": \"0 0 * * * ?\", \"handler\": \"gen.jobs.shop.ReportJob\", \"engine\": \"java\"}");
        write("/extensions/perspective.extension", "{\"module\": \"shop/gen/ui/perspective.json\", \"extensionPoint\": \"perspectives\"}");

        assertThat(detect().isEmpty()).isTrue();
    }

    private ServerSideScripts detect() {
        return ServerSideScriptDetector.detect(repository.getCollection(PROJECT));
    }

    private void write(String path, String content) {
        repository.createResource(PROJECT + path, content.getBytes(StandardCharsets.UTF_8));
    }
}
