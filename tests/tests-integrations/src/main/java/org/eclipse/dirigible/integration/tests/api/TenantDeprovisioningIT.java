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
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.data.sources.manager.DataSourcesManager;
import org.eclipse.dirigible.components.data.sources.service.DataSourceService;
import org.eclipse.dirigible.components.initializers.synchronizer.SynchronizationProcessor;
import org.eclipse.dirigible.components.tenants.domain.Tenant;
import org.eclipse.dirigible.components.tenants.domain.TenantStatus;
import org.eclipse.dirigible.components.tenants.service.TenantService;
import org.eclipse.dirigible.components.tenants.service.UserService;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.tests.base.IntegrationTest;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.eclipse.dirigible.tests.framework.security.SecurityUtil;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.http.ContentType;

/**
 * Deprovisioning a tenant through the tenant provisioning API removes everything the application
 * holds of it - its system database rows, its data source, its scheduled jobs and its processes -
 * and leaves a second tenant beside it untouched.
 *
 * <p>
 * The test plays the external provisioner, as {@code TenantScopedInitializationIT} does: it creates
 * each tenant's database user and schema, registers the tenant and its data source, and activates
 * it, against a project carrying a per-tenant table, a scheduled job and a process definition.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("slow")
class TenantDeprovisioningIT extends IntegrationTest {

    private static final String TENANTS_PATH = "/services/tenant-provisioning/tenants/";

    private static final String PROJECT = "tenant-deprovisioning-it";
    private static final String TABLE = "TENANT_DEPROVISIONING_ORDERS";
    private static final String PROCESS = "tenant-deprovisioning-it-process";

    /** The tenant that is deprovisioned. */
    private static final ExternalTenant ALPHA = new ExternalTenant("deprov-alpha", "DEPROVALPHA", "U_DEPROVALPHA");

    /** The tenant beside it, which must keep everything. */
    private static final ExternalTenant BETA = new ExternalTenant("deprov-beta", "DEPROVBETA", "U_DEPROVBETA");

    private static final String DB_PASSWORD = "tenant-deprovisioning-it-password";
    private static final String PLAIN_USER = "tenant-deprovisioning-it-plain";
    private static final String PASSWORD = "tenant-deprovisioning-it-password";
    private static final String AT = "2026-10-19T10:00:00Z";

    private static final long INITIALIZATION_TIMEOUT_SECONDS = 300;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private IRepository repository;

    @Autowired
    private SynchronizationProcessor synchronizationProcessor;

    @Autowired
    private DataSourcesManager dataSourcesManager;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserService userService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ProcessEngine processEngine;

    @BeforeAll
    static void enableTheApi() {
        DirigibleConfig.TENANT_PROVISIONING_API_ENABLED.setBooleanValue(true);
    }

    @AfterAll
    static void disableTheApi() {
        Configuration.remove(DirigibleConfig.TENANT_PROVISIONING_API_ENABLED.getKey());
    }

    @Test
    @Order(1)
    void aProvisionedTenantIsRemovedAndTheTenantBesideItIsUntouched() throws Exception {
        publishProject();
        activateAndAwait(ALPHA);
        activateAndAwait(BETA);
        for (ExternalTenant tenant : List.of(ALPHA, BETA)) {
            securityUtil.createUser(tenant.id(), "user-of-" + tenant.id(), PASSWORD);
            syncOneUser(tenant);
            completeOneProcess(tenant);
            processEngine.getRuntimeService()
                         .startProcessInstanceByKeyAndTenantId(PROCESS, tenant.id());
        }
        assertTrue(jobCount(ALPHA) > 0, "the job is scheduled in the tenant before its deprovisioning");
        assertEquals(1, runningProcesses(ALPHA), "a process runs in the tenant before its deprovisioning");
        assertEquals(1, rows("DIRIGIBLE_TENANT_USERS", "TENUSER_TENANT_ID", ALPHA), "the replica holds the tenant's user");

        // an earlier deprovisioning that stopped half way: the tenant stays DEPROVISIONING ...
        Tenant alpha = tenantService.findById(ALPHA.id())
                                    .orElseThrow();
        alpha.setStatus(TenantStatus.DEPROVISIONING);
        tenantService.save(alpha);
        // ... its replica can no longer be written ...
        restAssuredExecutor.execute(() -> given().contentType(ContentType.JSON)
                                                 .body(snapshot())
                                                 .when()
                                                 .put(TENANTS_PATH + ALPHA.id() + "/users")
                                                 .then()
                                                 .statusCode(409));
        // ... and the repeated call resumes it
        restAssuredExecutor.execute(() -> given().when()
                                                 .delete(TENANTS_PATH + ALPHA.id())
                                                 .then()
                                                 .statusCode(204));

        assertTrue(tenantService.findById(ALPHA.id())
                                .isEmpty(),
                "the tenant row");
        assertTrue(dataSourceService.findOptionalByName(ALPHA.id() + "_DefaultDB")
                                    .isEmpty(),
                "the tenant's data source");
        assertTrue(userService.findUsersByTenantId(ALPHA.id())
                              .isEmpty(),
                "the tenant's platform users");
        assertEquals(0, rows("DIRIGIBLE_TENANT_INITIALIZATIONS", "TENANT_INITIALIZATION_TENANT_ID", ALPHA), "the initialization record");
        assertEquals(0, rows("DIRIGIBLE_TENANT_ARTEFACTS", "TENANT_ARTEFACT_TENANT_ID", ALPHA), "the artefact ledger rows");
        assertEquals(0, rows("DIRIGIBLE_TENANT_USERS", "TENUSER_TENANT_ID", ALPHA), "the user replica");
        assertEquals(0, jobCount(ALPHA), "no scheduled job of the tenant is left");
        assertEquals(0, runningProcesses(ALPHA), "no process of the tenant runs");
        assertEquals(0, historicProcesses(ALPHA), "no process history of the tenant is left");

        restAssuredExecutor.execute(() -> {
            given().when()
                   .get(TENANTS_PATH + ALPHA.id())
                   .then()
                   .statusCode(404);
            given().when()
                   .delete(TENANTS_PATH + ALPHA.id())
                   .then()
                   .statusCode(204);
            given().when()
                   .get(TENANTS_PATH + BETA.id())
                   .then()
                   .statusCode(200)
                   .body("status", equalTo(TenantStatus.PROVISIONED.name()));
        });
        assertTrue(dataSourceService.findOptionalByName(BETA.id() + "_DefaultDB")
                                    .isPresent(),
                "the other tenant's data source");
        assertTrue(tenantDataSourceConnects(BETA), "the other tenant's data source still connects");
        assertTrue(jobCount(BETA) > 0, "the other tenant's job is still scheduled");
        assertEquals(1, runningProcesses(BETA), "the other tenant's process still runs");
        assertEquals(2, historicProcesses(BETA), "the other tenant's process history is intact");
        assertEquals(1, rows("DIRIGIBLE_TENANT_USERS", "TENUSER_TENANT_ID", BETA), "the other tenant's replica");
        assertFalse(userService.findUsersByTenantId(BETA.id())
                               .isEmpty(),
                "the other tenant's platform users");

        // nothing comes back with the next top-up of the per-tenant artefacts
        synchronizationProcessor.forceProcessSynchronizers();
        assertEquals(0, jobCount(ALPHA), "the top-up must not schedule the removed tenant's job again");
        assertTrue(tenantService.findById(ALPHA.id())
                                .isEmpty(),
                "nor bring the tenant back");
    }

    @Test
    @Order(2)
    void aTenantThatDoesNotExistIsAlreadyGone() {
        restAssuredExecutor.execute(() -> given().when()
                                                 .delete(TENANTS_PATH + "deprov-never-registered")
                                                 .then()
                                                 .statusCode(204));
    }

    @Test
    @Order(3)
    void theDefaultTenantAndAnInvalidIdAreRefused() {
        restAssuredExecutor.execute(() -> {
            given().when()
                   .delete(TENANTS_PATH + "default-tenant")
                   .then()
                   .statusCode(400)
                   .body("status", equalTo(400));
            given().when()
                   .delete(TENANTS_PATH + "not_a.tenant")
                   .then()
                   .statusCode(400);
        });
        assertTrue(tenantService.findById("default-tenant")
                                .isPresent(),
                "the default tenant is untouched");
    }

    @Test
    @Order(4)
    void anAuthenticatedUserWithoutARoleIsRefused() {
        securityUtil.ensureUserInDefaultTenant(PLAIN_USER, PASSWORD);

        restAssuredExecutor.execute(() -> given().when()
                                                 .delete(TENANTS_PATH + BETA.id())
                                                 .then()
                                                 .statusCode(403),
                PLAIN_USER, PASSWORD);
        assertTrue(tenantService.findById(BETA.id())
                                .isPresent(),
                "the refused call removes nothing");
    }

    /** A per-tenant table, a scheduled job that never fires during the test, and a process. */
    private void publishProject() {
        write(TABLE + ".table", """
                {
                    "name": "%s",
                    "type": "TABLE",
                    "columns": [
                        { "name": "ORDER_ID", "type": "INTEGER", "length": "0", "nullable": "false", "primaryKey": "true" }
                    ]
                }
                """.formatted(TABLE));
        write("heartbeat.job", """
                {
                    "expression": "0 0 0 1 1 ? 2099",
                    "group": "defined",
                    "handler": "%s/heartbeat-handler.ts",
                    "description": "Never fires during the test",
                    "parameters": []
                }
                """.formatted(PROJECT));
        write("heartbeat-handler.ts", "export {};\n");
        write("wait.bpmn", """
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" targetNamespace="http://www.flowable.org/processdef">
                  <process id="%s" name="Tenant deprovisioning IT" isExecutable="true">
                    <startEvent id="start"/>
                    <sequenceFlow id="toWait" sourceRef="start" targetRef="wait"/>
                    <userTask id="wait" name="Wait"/>
                    <sequenceFlow id="toEnd" sourceRef="wait" targetRef="end"/>
                    <endEvent id="end"/>
                  </process>
                </definitions>
                """.formatted(PROCESS));
        synchronizationProcessor.forceProcessSynchronizers();
    }

    private void write(String name, String content) {
        repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + PROJECT + "/" + name,
                content.getBytes(StandardCharsets.UTF_8), false, "application/octet-stream", true);
    }

    private void activateAndAwait(ExternalTenant tenant) throws SQLException {
        createDatabaseUserAndSchema(tenant);
        restAssuredExecutor.execute(() -> {
            given().contentType(ContentType.JSON)
                   .body(Map.of("name", tenant.id()))
                   .when()
                   .put(TENANTS_PATH + tenant.id())
                   .then()
                   .statusCode(anyOf(is(200), is(201)));
            given().contentType(ContentType.JSON)
                   .body(Map.of("username", tenant.user(), "password", DB_PASSWORD, "schema", tenant.schema()))
                   .when()
                   .put(TENANTS_PATH + tenant.id() + "/datasources/default")
                   .then()
                   .statusCode(anyOf(is(200), is(201)));
            given().when()
                   .post(TENANTS_PATH + tenant.id() + "/activation")
                   .then()
                   .statusCode(202);
        });
        Awaitility.await()
                  .atMost(INITIALIZATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                  .pollInterval(1, TimeUnit.SECONDS)
                  .until(() -> !"IN_PROGRESS".equals(initializationStatus(tenant)));
        assertEquals("COMPLETED", initializationStatus(tenant), "the initialization status of " + tenant.id());
    }

    private String initializationStatus(ExternalTenant tenant) {
        return restAssuredExecutor.executeWithResult(() -> given().when()
                                                                  .get(TENANTS_PATH + tenant.id() + "/activation")
                                                                  .then()
                                                                  .statusCode(200)
                                                                  .extract()
                                                                  .path("status"));
    }

    private void syncOneUser(ExternalTenant tenant) {
        restAssuredExecutor.execute(() -> given().contentType(ContentType.JSON)
                                                 .body(snapshot())
                                                 .when()
                                                 .put(TENANTS_PATH + tenant.id() + "/users")
                                                 .then()
                                                 .statusCode(200));
    }

    private static Map<String, Object> snapshot() {
        Map<String, Object> role = new LinkedHashMap<>();
        role.put("role", "Owner");
        role.put("state", "GRANTED");
        role.put("grantedBy", "owner@example.test");
        role.put("grantedAt", AT);
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("email", "owner@example.test");
        user.put("revision", 1);
        user.put("status", "ASSIGNED");
        user.put("roles", new ArrayList<>(List.of(role)));
        user.put("invitedBy", "owner@example.test");
        user.put("invitedAt", AT);
        user.put("lastChangedBy", "owner@example.test");
        user.put("lastChangedAt", AT);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("complete", false);
        body.put("revision", 1);
        body.put("users", List.of(user));
        return body;
    }

    /** One instance started and completed, so the tenant has process history. */
    private void completeOneProcess(ExternalTenant tenant) {
        ProcessInstance instance = processEngine.getRuntimeService()
                                                .startProcessInstanceByKeyAndTenantId(PROCESS, tenant.id());
        processEngine.getTaskService()
                     .createTaskQuery()
                     .processInstanceId(instance.getId())
                     .list()
                     .forEach(task -> processEngine.getTaskService()
                                                   .complete(task.getId()));
    }

    private long runningProcesses(ExternalTenant tenant) {
        return processEngine.getRuntimeService()
                            .createProcessInstanceQuery()
                            .processInstanceTenantId(tenant.id())
                            .count();
    }

    private long historicProcesses(ExternalTenant tenant) {
        return processEngine.getHistoryService()
                            .createHistoricProcessInstanceQuery()
                            .processInstanceTenantId(tenant.id())
                            .count();
    }

    private long jobCount(ExternalTenant tenant) throws SchedulerException {
        String prefix = tenant.id() + "###";
        long count = 0;
        for (JobKey key : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("defined"))) {
            if (key.getName()
                   .startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    private int rows(String table, String tenantColumn, ExternalTenant tenant) throws SQLException {
        try (Connection connection = dataSourcesManager.getSystemDataSource()
                                                       .getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + tenantColumn + " = ?")) {
            statement.setString(1, tenant.id());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private boolean tenantDataSourceConnects(ExternalTenant tenant) throws SQLException {
        try (Connection connection = dataSourcesManager.getDataSource(tenant.id() + "_DefaultDB")
                                                       .getConnection()) {
            return connection.isValid(5);
        }
    }

    private boolean schemaExists(ExternalTenant tenant) throws SQLException {
        try (Connection connection = dataSourcesManager.getDefaultDataSource()
                                                       .getConnection();
                ResultSet schemas = connection.getMetaData()
                                              .getSchemas(null, tenant.schema())) {
            return schemas.next();
        }
    }

    /** What the external provisioner does before it calls the API. */
    private void createDatabaseUserAndSchema(ExternalTenant tenant) throws SQLException {
        if (schemaExists(tenant)) {
            return;
        }
        try (Connection connection = dataSourcesManager.getDefaultDataSource()
                                                       .getConnection()) {
            execute(connection, SqlFactory.getNative(connection)
                                          .create()
                                          .user(tenant.user(), DB_PASSWORD)
                                          .build());
            execute(connection, SqlFactory.getNative(connection)
                                          .create()
                                          .schema(tenant.schema())
                                          .authorization(tenant.user())
                                          .build());
        }
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    /**
     * A tenant whose database user and schema exist outside the platform.
     *
     * @param id the tenant id
     * @param schema the schema the provisioner created
     * @param user the database user the provisioner created
     */
    private record ExternalTenant(String id, String schema, String user) {
    }
}
