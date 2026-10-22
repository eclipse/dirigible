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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import java.nio.charset.StandardCharsets;

import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.restassured.http.ContentType;

/**
 * The JavaScript handlers of a process, end to end over HTTP: the {@code ${JSTask}} and
 * {@code ${JSListener}} beans run their scripts through the JavaScript engine's bridge since the
 * engines no longer import it (#8006), and every shape a handler expression can take keeps working
 * - a whole module, an exported function, a method of an exported class - as does a
 * {@code BpmnError} thrown by a script, which the process routes through its boundary event.
 *
 * <p>
 * Each handler records a process variable; the process then waits on a user task whose creation ran
 * the task listener, and the test reads the variables back from the history.
 */
class JavascriptBpmnHandlersIT extends IntegrationTest {

    private static final String PROJECT = "js-bpmn-it";
    private static final String PROCESS_KEY = "js-bpmn-it-process";

    private static final String MODULE_LOCATION = "/" + PROJECT + "/module.mjs";
    private static final String HANDLERS_LOCATION = "/" + PROJECT + "/handlers.mjs";
    private static final String BPMN_LOCATION = "/" + PROJECT + "/process.bpmn";

    private static final String MODULE_REGISTRY_PATH = IRepositoryStructure.PATH_REGISTRY_PUBLIC + MODULE_LOCATION;
    private static final String HANDLERS_REGISTRY_PATH = IRepositoryStructure.PATH_REGISTRY_PUBLIC + HANDLERS_LOCATION;
    private static final String BPMN_REGISTRY_PATH = IRepositoryStructure.PATH_REGISTRY_PUBLIC + BPMN_LOCATION;

    private static final long ASSERTION_TIMEOUT_SECONDS = 60;

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Test
    void everyJavascriptHandlerShapeRunsAndAScriptBpmnErrorIsRouted() {
        write(MODULE_REGISTRY_PATH, moduleSource(), "application/javascript");
        write(HANDLERS_REGISTRY_PATH, handlersSource(), "application/javascript");
        write(BPMN_REGISTRY_PATH, bpmnSource(), "application/xml");
        synchronizationProcessor.forceProcessSynchronizers();

        String processInstanceId = startProcess();

        assertHistoricVariable(processInstanceId, "moduleRan", "yes");
        assertHistoricVariable(processInstanceId, "exportedFunctionRan", "yes");
        assertHistoricVariable(processInstanceId, "classMethodRan", "yes");
        assertHistoricVariable(processInstanceId, "bpmnErrorRouted", "yes");
        assertHistoricVariable(processInstanceId, "taskListenerRan", "yes");
    }

    @AfterEach
    void cleanup() {
        boolean removed = false;
        for (String path : new String[] {BPMN_REGISTRY_PATH, HANDLERS_REGISTRY_PATH, MODULE_REGISTRY_PATH}) {
            if (repository.hasResource(path)) {
                repository.removeResource(path);
                removed = true;
            }
        }
        if (removed) {
            synchronizationProcessor.forceProcessSynchronizers();
        }
    }

    private void write(String path, String content, String contentType) {
        repository.createResource(path, content.getBytes(StandardCharsets.UTF_8), false, contentType, true);
    }

    private String startProcess() {
        String body = "{\"processDefinitionKey\":\"" + PROCESS_KEY + "\",\"businessKey\":\"js-bpmn-it\",\"parameters\":\"{}\"}";
        return restAssuredExecutor.executeWithResult(() -> given().contentType(ContentType.JSON)
                                                                  .body(body)
                                                                  .when()
                                                                  .post("/services/bpm/bpm-processes/instance")
                                                                  .then()
                                                                  .statusCode(200)
                                                                  .extract()
                                                                  .asString());
    }

    private void assertHistoricVariable(String processInstanceId, String name, String expectedValue) {
        restAssuredExecutor.execute(() -> given().when()
                                                 .get("/services/bpm/bpm-processes/historic-instances/" + processInstanceId + "/variables")
                                                 .then()
                                                 .statusCode(200)
                                                 .body("variableName", hasItem(name))
                                                 .body("find { it.variableName == '" + name + "' }.value", equalTo(expectedValue)),
                ASSERTION_TIMEOUT_SECONDS);
    }

    /** A handler that is a whole module: its top-level code is the task. */
    private static String moduleSource() {
        return """
                import { process } from "@aerokit/sdk/bpm";

                process.getExecutionContext().setVariable("moduleRan", "yes");
                """;
    }

    private static String handlersSource() {
        return """
                import { process } from "@aerokit/sdk/bpm";

                export function exportedFunction() {
                    process.getExecutionContext().setVariable("exportedFunctionRan", "yes");
                }

                export class Handler {
                    classMethod() {
                        process.getExecutionContext().setVariable("classMethodRan", "yes");
                    }
                }

                export function failing() {
                    const BpmnError = Java.type("org.flowable.engine.delegate.BpmnError");
                    throw new BpmnError("JS_FAILED");
                }

                export function errorRouted() {
                    process.getExecutionContext().setVariable("bpmnErrorRouted", "yes");
                }

                export function taskCreated() {
                    process.getTaskContext().setVariable("taskListenerRan", "yes");
                }
                """;
    }

    private static String bpmnSource() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
                             xmlns:flowable="http://flowable.org/bpmn"
                             targetNamespace="http://www.flowable.org/processdef">
                  <error id="js-failed" errorCode="JS_FAILED"/>
                  <process id="%1$s" name="JavaScript BPMN handlers IT" isExecutable="true">
                    <startEvent id="start"/>
                    <sequenceFlow id="f1" sourceRef="start" targetRef="module"/>
                    <serviceTask id="module" flowable:delegateExpression="${JSTask}">
                      <extensionElements>
                        <flowable:field name="handler"><flowable:string><![CDATA[%2$s/module.mjs]]></flowable:string></flowable:field>
                      </extensionElements>
                    </serviceTask>
                    <sequenceFlow id="f2" sourceRef="module" targetRef="exported-function"/>
                    <serviceTask id="exported-function" flowable:delegateExpression="${JSTask}">
                      <extensionElements>
                        <flowable:field name="handler"><flowable:string><![CDATA[%2$s/handlers.mjs/exportedFunction]]></flowable:string></flowable:field>
                      </extensionElements>
                    </serviceTask>
                    <sequenceFlow id="f3" sourceRef="exported-function" targetRef="class-method"/>
                    <serviceTask id="class-method" flowable:delegateExpression="${JSTask}">
                      <extensionElements>
                        <flowable:field name="handler"><flowable:string><![CDATA[%2$s/handlers.mjs/Handler/classMethod]]></flowable:string></flowable:field>
                      </extensionElements>
                    </serviceTask>
                    <sequenceFlow id="f4" sourceRef="class-method" targetRef="failing"/>
                    <serviceTask id="failing" flowable:delegateExpression="${JSTask}">
                      <extensionElements>
                        <flowable:field name="handler"><flowable:string><![CDATA[%2$s/handlers.mjs/failing]]></flowable:string></flowable:field>
                      </extensionElements>
                    </serviceTask>
                    <sequenceFlow id="f5" sourceRef="failing" targetRef="not-routed"/>
                    <endEvent id="not-routed"/>
                    <boundaryEvent id="caught" attachedToRef="failing">
                      <errorEventDefinition errorRef="js-failed"/>
                    </boundaryEvent>
                    <sequenceFlow id="f6" sourceRef="caught" targetRef="error-routed"/>
                    <serviceTask id="error-routed" flowable:delegateExpression="${JSTask}">
                      <extensionElements>
                        <flowable:field name="handler"><flowable:string><![CDATA[%2$s/handlers.mjs/errorRouted]]></flowable:string></flowable:field>
                      </extensionElements>
                    </serviceTask>
                    <sequenceFlow id="f7" sourceRef="error-routed" targetRef="review"/>
                    <userTask id="review" name="Review">
                      <extensionElements>
                        <flowable:taskListener event="create" delegateExpression="${JSListener}">
                          <flowable:field name="handler"><flowable:string><![CDATA[%2$s/handlers.mjs/taskCreated]]></flowable:string></flowable:field>
                        </flowable:taskListener>
                      </extensionElements>
                    </userTask>
                    <sequenceFlow id="f8" sourceRef="review" targetRef="end"/>
                    <endEvent id="end"/>
                  </process>
                </definitions>
                """.formatted(
                PROCESS_KEY, PROJECT);
    }
}
