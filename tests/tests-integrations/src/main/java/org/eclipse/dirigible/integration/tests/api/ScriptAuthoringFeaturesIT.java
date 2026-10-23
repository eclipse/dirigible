/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import org.eclipse.dirigible.components.base.script.ScriptEngineAvailability;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

/**
 * The IDE offers its JavaScript and TypeScript authoring features only where the JavaScript engine
 * is deployed (#8025), and New -> Scheduled Job, Message Listener and WebSocket create a Java
 * handler in either case.
 *
 * <p>
 * Where the engine is deployed - as in every IT - the branding script says so, and the debugger
 * view, the TypeScript and ESM file templates, the TypeScript route step handler, the descriptors
 * with a JavaScript handler, the extension and access constraint examples pointing at a JavaScript
 * file and the TypeScript mapping generator are offered as before. The spy on the availability then
 * answers as an instance without the engine does: the bootstrap and the template catalogue offer
 * none of them - New -> Extension and Access Constraints offer a JSON module and a Java controller
 * example in their place (#8047) - and the IDE's pages read {@code false} from the branding script,
 * the one answer they decide the editor typings, the Debug action and the script preview on.
 */
// One Dirigible boot for the whole class: the spy is reset after each method.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ScriptAuthoringFeaturesIT extends IntegrationTest {

    private static final String BRANDING = "/services/js/platform-branding/branding.js";

    private static final String VIEWS = "/services/js/platform-core/extension-services/views.js";

    private static final String MENUS = "/services/js/platform-core/extension-services/menus.js";

    private static final String TEMPLATES = "/services/js/service-template/api/templates.js";

    private static final String MENU_TEMPLATES = TEMPLATES + "/menu";

    private static final String DEBUGGER_VIEW = "debugger";

    /** The New menu items that create a JavaScript or TypeScript file. */
    private static final List<String> SCRIPT_MENU_TEMPLATES = List.of( //
            "menu-projects/templates/menu-ts.js", //
            "menu-projects/templates/menu-mjs.js", //
            "menu-integration/templates/integration-route-step-handler.js", //
            "menu-jobs/templates/menu-job-javascript.js", //
            "menu-listeners/templates/menu-listener-javascript.js", //
            "menu-websockets/templates/menu-websocket-javascript.js");

    /** The New menu items that create a Java handler, and the Java kind each creates. */
    private static final List<List<String>> JAVA_HANDLER_MENU_TEMPLATES = List.of( //
            List.of("menu-jobs/templates/menu-job.js", "Scheduled Job", "job"), //
            List.of("menu-listeners/templates/menu-listener.js", "Message Listener", "listener"), //
            List.of("menu-websockets/templates/menu-websocket.js", "WebSocket", "websocket"));

    /**
     * The New menu items whose example points at a JavaScript file, the alternative offered in their
     * place without the engine, the label both carry, and what the alternative's example points at.
     */
    private static final List<List<String>> SCRIPT_EXAMPLE_MENU_TEMPLATES = List.of( //
            List.of("menu-extensions/templates/menu-extension.js", "menu-extensions/templates/menu-extension-json.js", "Extension",
                    "\"/myproject/my-extension.json\""), //
            List.of("menu-security/templates/menu-access.js", "menu-security/templates/menu-access-java.js", "Access Constraints",
                    "\"/myproject/myfolder/MyController/**\""));

    private static final String TYPESCRIPT_MAPPING_TEMPLATE = "template-mapping-javascript/template/template.js";

    private static final String JAVA_MAPPING_TEMPLATE = "template-mapping-java/template/template.js";

    @MockitoSpyBean
    private ScriptEngineAvailability scriptEngine;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Test
    void offers_the_script_authoring_features_where_the_engine_is_deployed() {
        assertTrue(scriptEngine.isAvailable(), "the JavaScript engine is deployed in the integration tests");

        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(BRANDING)
                   .then()
                   .statusCode(200)
                   .body(containsString("top.PlatformScriptEngine = true;"));

            assertTrue(ids(VIEWS).contains(DEBUGGER_VIEW), "the JavaScript debugger is offered");
            assertTrue(windowMenuViews().contains(DEBUGGER_VIEW), "the Window menu lists the JavaScript debugger");
            assertTrue(ids(MENU_TEMPLATES).containsAll(SCRIPT_MENU_TEMPLATES), "the New menu offers the script files");
            assertScriptExampleItems(true);
            assertTrue(ids(TEMPLATES).contains(TYPESCRIPT_MAPPING_TEMPLATE), "the TypeScript mapping generator is offered");

            // The editor typings stay answered by the engine.
            given().when()
                   .get("/services/js/all-dts")
                   .then()
                   .statusCode(200);
        });
    }

    @Test
    void offers_none_of_them_without_the_engine() {
        doReturn(false).when(scriptEngine)
                       .isAvailable();

        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(BRANDING)
                   .then()
                   .statusCode(200)
                   .body(containsString("top.PlatformScriptEngine = false;"));

            List<String> views = ids(VIEWS);
            assertFalse(views.contains(DEBUGGER_VIEW), "the JavaScript debugger must not be offered: " + views);
            assertTrue(views.contains("projects"), "the other views stay: " + views);
            assertFalse(windowMenuViews().contains(DEBUGGER_VIEW), "the Window menu must not list the JavaScript debugger");

            List<String> menuTemplates = ids(MENU_TEMPLATES);
            for (String scriptTemplate : SCRIPT_MENU_TEMPLATES) {
                assertFalse(menuTemplates.contains(scriptTemplate), scriptTemplate + " must not be offered: " + menuTemplates);
            }
            assertTrue(menuTemplates.contains("menu-projects/templates/menu-java.js"), "the Java service stays: " + menuTemplates);
            assertScriptExampleItems(false);

            List<String> templates = ids(TEMPLATES);
            assertFalse(templates.contains(TYPESCRIPT_MAPPING_TEMPLATE), "the TypeScript mapping generator must not be offered");
            assertTrue(templates.contains(JAVA_MAPPING_TEMPLATE), "the Java mapping generator stays: " + templates);
        });
        assertJavaHandlerItems();
    }

    @Test
    void new_job_listener_and_websocket_create_a_java_handler() {
        assertJavaHandlerItems();
    }

    private void assertJavaHandlerItems() {
        restAssuredExecutor.execute(() -> {
            for (List<String> item : JAVA_HANDLER_MENU_TEMPLATES) {
                String template = "find { it.id == '" + item.get(0) + "' }";
                given().when()
                       .get(MENU_TEMPLATES)
                       .then()
                       .statusCode(200)
                       .body(template + ".label", equalTo(item.get(1)))
                       .body(template + ".javaKind", equalTo(item.get(2)))
                       // No file content of its own: the item creates the handler class the Java kind names.
                       .body(template + ".data", nullValue());
            }
        });
    }

    /**
     * Asserts that New -> Extension and Access Constraints offer the example pointing at a JavaScript
     * file where the engine is deployed, and the alternative under the same label where it is not.
     */
    private static void assertScriptExampleItems(boolean scriptEngine) {
        for (List<String> item : SCRIPT_EXAMPLE_MENU_TEMPLATES) {
            String offered = "find { it.id == '" + (scriptEngine ? item.get(0) : item.get(1)) + "' }";
            String withheld = "find { it.id == '" + (scriptEngine ? item.get(1) : item.get(0)) + "' }";
            given().when()
                   .get(MENU_TEMPLATES)
                   .then()
                   .statusCode(200)
                   .body(offered + ".label", equalTo(item.get(2)))
                   .body(offered + ".data",
                           scriptEngine ? containsString(".js\"") : allOf(containsString(item.get(3)), not(containsString(".js\""))))
                   .body(withheld, nullValue());
        }
    }

    private static List<String> ids(String path) {
        return given().when()
                      .get(path)
                      .then()
                      .statusCode(200)
                      .extract()
                      .jsonPath()
                      .getList("id", String.class);
    }

    /** The ids of the views the Window menu lists, in its Views group. */
    private static List<String> windowMenuViews() {
        List<String> views = given().when()
                                    .get(MENUS)
                                    .then()
                                    .statusCode(200)
                                    .extract()
                                    .jsonPath()
                                    .getList("find { it != null && it.id == 'window' }.menu.items.find { it.label == 'Views' }.items.id",
                                            String.class);
        assertFalse(views.isEmpty(), "the Window menu lists views");
        return views;
    }
}
