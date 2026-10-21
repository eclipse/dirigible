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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleEvaluator;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleExport;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;

/**
 * A Harmonia shell bootstraps without running a script (#7964).
 *
 * <p>
 * The shells' bootstrap endpoints - the aggregated extensions, the user name, the languages, the
 * country and the branding - are answered by Java, and the extension modules the platform and the
 * current application template contribute are JSON. A counting double wraps the JavaScript engine's
 * {@link ExtensionModuleEvaluator}: across the whole bootstrap of every runtime shell it must not
 * be called once. A legacy script module keeps loading beside the JSON ones, through the evaluator.
 */
@Import(ShellBootstrapWithoutScriptsIT.CountingEvaluatorConfiguration.class)
// One Dirigible boot for the whole class: each method cleans up after itself.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ShellBootstrapWithoutScriptsIT extends IntegrationTest {

    private static final String PROJECT = "/shell-bootstrap-it";

    private static final String POINT = "application-perspectives";

    private static final String GROUP_ID = "shell-bootstrap-it-group";

    private static final String JSON_PERSPECTIVE_ID = "shell-bootstrap-it-Orders";

    private static final String LEGACY_PERSPECTIVE_ID = "shell-bootstrap-it-Legacy";

    private static final String LEGACY_MODULE = PROJECT.substring(1) + "/legacy/perspective.js";

    private static final long ASSERTION_TIMEOUT_SECONDS = 30;

    /** What every runtime shell requests while it bootstraps, at the script URLs the pages ask. */
    private static final List<String> BOOTSTRAP_URLS = List.of( //
            "/services/js/platform-branding/branding.js", //
            "/services/js/platform-core/extension-services/locales.js", //
            "/services/js/platform-core/extension-services/locales.js?langs=en-US&namespaces=application-core", //
            "/services/js/platform-core/extension-services/shells.js", //
            "/services/js/platform-core/extension-services/shells.js?extensionPoints=platform-shells", //
            "/services/js/platform-core/extension-services/perspectives.js?extensionPoints=application-perspectives", //
            "/services/js/platform-core/extension-services/perspectives.js?extensionPoints=application-personal-perspectives", //
            "/services/js/platform-core/extension-services/perspectives.js?extensionPoints=application-partner-perspectives", //
            "/services/js/platform-core/extension-services/perspectives.js?extensionPoints=application-admin-perspectives", //
            "/services/js/platform-core/extension-services/projections.js?extensionPoints=application-projections", //
            "/services/js/platform-core/services/user-name.js", //
            "/services/js/platform-core/services/application-languages.js", //
            "/services/js/platform-core/services/application-country.js");

    /** The canonical paths of the same answers. */
    private static final List<String> CANONICAL_URLS = List.of( //
            "/services/platform/branding.js", //
            "/services/platform/extensions/locales", //
            "/services/platform/extensions/shells", //
            "/services/platform/extensions/perspectives?extensionPoints=application-perspectives", //
            "/services/platform/extensions/projections", //
            "/services/platform/user-name", //
            "/services/platform/languages", //
            "/services/platform/country", //
            "/services/platform/clear-cache");

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private CountingExtensionModuleEvaluator evaluator;

    /** The registry paths this test published, in creation order, for the teardown. */
    private final Map<String, String> published = new LinkedHashMap<>();

    @Test
    void bootstraps_every_runtime_shell_without_running_a_script() {
        publishGroup();
        publishJsonPerspective();
        synchronizationProcessor.forceProcessSynchronizers();
        restAssuredExecutor.execute(() -> given().when()
                                                 .get("/services/platform/extensions/perspectives?extensionPoints=" + POINT)
                                                 .then()
                                                 .body(inGroup(GROUP_ID), hasItem(JSON_PERSPECTIVE_ID)),
                ASSERTION_TIMEOUT_SECONDS);
        evaluator.reset();

        List<String> urls = new ArrayList<>(BOOTSTRAP_URLS);
        urls.addAll(CANONICAL_URLS);
        for (String url : urls) {
            given().when()
                   .get(url)
                   .then()
                   .statusCode(200)
                   .header("Cache-Control", equalTo("private, no-cache"));
        }

        assertEquals(List.of(), evaluator.evaluatedModules(), "no extension module may be evaluated as a script during a shell bootstrap");
        assertEquals(0, evaluator.sessions(), "no script engine may be started during a shell bootstrap");
    }

    @Test
    void answers_the_old_script_urls_although_the_scripts_are_gone() {
        assertFalse(repository.hasResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/platform-core/extension-services/perspectives.js"),
                "the aggregation script must be gone from the registry");

        given().when()
               .get("/services/js/platform-core/extension-services/perspectives.js?extensionPoints=application-personal-perspectives")
               .then()
               .statusCode(200)
               .body("perspectives.id", hasItem("personal"));
        given().when()
               .get("/services/js/platform-branding/branding.js")
               .then()
               .statusCode(200)
               .body(containsString("top.PlatformBranding"));
    }

    @Test
    void evaluates_a_legacy_script_module_beside_the_json_ones() {
        publishGroup();
        publishJsonPerspective();
        createResource("/" + LEGACY_MODULE, """
                const perspectiveData = {
                    id: '%s',
                    label: 'Legacy',
                    path: '/services/web%s/legacy/index.html',
                    groupId: '%s',
                    order: 2
                };
                if (typeof exports !== 'undefined') {
                    exports.getPerspective = () => perspectiveData;
                }
                """.formatted(LEGACY_PERSPECTIVE_ID, PROJECT, GROUP_ID), "application/javascript");
        createResource(PROJECT + "/legacy/perspective.extension", extension(LEGACY_MODULE), "application/json");
        synchronizationProcessor.forceProcessSynchronizers();

        restAssuredExecutor.execute(() -> {
            evaluator.reset();
            given().when()
                   .get("/services/js/platform-core/extension-services/perspectives.js?extensionPoints=" + POINT)
                   .then()
                   .statusCode(200)
                   .body(inGroup(GROUP_ID), equalTo(List.of(JSON_PERSPECTIVE_ID, LEGACY_PERSPECTIVE_ID)));
            assertEquals(List.of(LEGACY_MODULE), evaluator.evaluatedModules(), "only the script module may go through the evaluator");
        }, ASSERTION_TIMEOUT_SECONDS);
    }

    /** A navigation group, as a module contributing one is now written. */
    private void publishGroup() {
        createResource(PROJECT + "/group.json", """
                {
                    "id": "%s",
                    "label": "Shell bootstrap IT",
                    "order": 9990,
                    "items": []
                }
                """.formatted(GROUP_ID), "application/json");
        createResource(PROJECT + "/group.extension", extension(PROJECT.substring(1) + "/group.json"), "application/json");
    }

    /** A perspective, as the Harmonia application template now generates it. */
    private void publishJsonPerspective() {
        String module = PROJECT.substring(1) + "/gen/app/perspectives/Orders/perspective.json";
        createResource("/" + module, """
                {
                    "id": "%s",
                    "label": "Orders",
                    "tkey": "shell-bootstrap-it:app.t.Orders_plural",
                    "path": "/services/web%s/gen/app/index.html?embedded=true#/Orders",
                    "kind": "LIST",
                    "groupId": "%s",
                    "order": 1,
                    "icon": "shopping-cart"
                }
                """.formatted(JSON_PERSPECTIVE_ID, PROJECT, GROUP_ID), "application/json");
        createResource(PROJECT + "/gen/app/perspectives/Orders/perspective.extension", extension(module), "application/json");
    }

    private static String extension(String module) {
        return """
                {
                    "module": "%s",
                    "extensionPoint": "%s",
                    "description": "Shell bootstrap IT - %s"
                }
                """.formatted(module, POINT, module);
    }

    private void createResource(String location, String content, String contentType) {
        String path = IRepositoryStructure.PATH_REGISTRY_PUBLIC + location;
        repository.createResource(path, content.getBytes(StandardCharsets.UTF_8), false, contentType, true);
        published.put(location, path);
    }

    /** A JsonPath expression selecting the ids of the perspectives inside the given group. */
    private static String inGroup(String groupId) {
        return "perspectives.find { it.id == '" + groupId + "' }.items.id";
    }

    @AfterEach
    void removePublishedArtefacts() {
        published.values()
                 .stream()
                 .filter(repository::hasResource)
                 .forEach(repository::removeResource);
        published.clear();
        synchronizationProcessor.forceProcessSynchronizers();
    }

    /**
     * Puts the counting double in front of the JavaScript engine's evaluator.
     */
    @TestConfiguration
    static class CountingEvaluatorConfiguration {

        @Bean
        @Primary
        CountingExtensionModuleEvaluator countingExtensionModuleEvaluator(
                @Qualifier("javascriptExtensionModuleEvaluator") ExtensionModuleEvaluator javascript) {
            return new CountingExtensionModuleEvaluator(javascript);
        }
    }

    /**
     * Records every session opened and every module evaluated, delegating to the real evaluator.
     */
    static final class CountingExtensionModuleEvaluator implements ExtensionModuleEvaluator {

        private final ExtensionModuleEvaluator delegate;

        private final AtomicInteger sessions = new AtomicInteger();

        private final List<String> evaluatedModules = Collections.synchronizedList(new ArrayList<>());

        CountingExtensionModuleEvaluator(ExtensionModuleEvaluator delegate) {
            this.delegate = delegate;
        }

        @Override
        public Session openSession() {
            sessions.incrementAndGet();
            Session session = delegate.openSession();
            return new Session() {

                @Override
                public Optional<ExtensionModuleExport> evaluate(String module, List<String> exportNames) {
                    evaluatedModules.add(module);
                    return session.evaluate(module, exportNames);
                }

                @Override
                public void close() {
                    session.close();
                }
            };
        }

        int sessions() {
            return sessions.get();
        }

        List<String> evaluatedModules() {
            return List.copyOf(evaluatedModules);
        }

        void reset() {
            sessions.set(0);
            evaluatedModules.clear();
        }
    }
}
