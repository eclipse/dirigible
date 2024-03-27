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
package org.eclipse.dirigible.integration.tests.ui.tests.multitenancy;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Awaitility;
import org.eclipse.dirigible.components.base.tenant.DefaultTenant;
import org.eclipse.dirigible.components.base.tenant.Tenant;
import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.DirigibleWorkbench;
import org.eclipse.dirigible.integration.tests.ui.EdmView;
import org.eclipse.dirigible.integration.tests.ui.tests.UserInterfaceIntegrationTest;
import org.eclipse.dirigible.tests.framework.DirigibleTestTenant;
import org.eclipse.dirigible.tests.framework.restassured.RestAssuredExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

class MultitenancyIT extends UserInterfaceIntegrationTest {

    private DirigibleTestTenant defaultTenant;
    private DirigibleTestTenant testTenant1;
    private DirigibleTestTenant testTenant2;
    private List<DirigibleTestTenant> testTenants;

    @Autowired
    private TenantCreator tenantCreator;
    @Autowired
    private TestProject testProject;

    @Autowired
    private Dirigible dirigible;

    @Autowired
    private EdmView edmView;

    @Autowired
    @DefaultTenant
    private Tenant defTenant;

    @Autowired
    private RestAssuredExecutor restAssuredExecutor;

    @BeforeEach
    void initTestTenants() {
        defaultTenant = fromTenantEntity(defTenant);
        testTenant1 = new DirigibleTestTenant("test-tenant-1");
        testTenant2 = new DirigibleTestTenant("test-tenant-2");
        testTenants = List.of(defaultTenant, testTenant1, testTenant2);
    }

    private DirigibleTestTenant fromTenantEntity(Tenant tenant) {
        return new DirigibleTestTenant(tenant.isDefault(), //
                tenant.getName(), //
                tenant.getId(), //
                tenant.getSubdomain(), //
                UUID.randomUUID()
                    .toString(), //
                UUID.randomUUID()
                    .toString());
    }

    @Test
    void test() {
        createTestTenants();
        prepareTestProject();

        waitForTenantsProvisioning();
        verifyTestProjectAccessibleByTenants();
        verifyView();
        verifyOData();
    }

    private void createTestTenants() {
        testTenants.stream()
                   .forEach(t -> tenantCreator.createTenant(t));
    }

    private void prepareTestProject() {
        testProject.copyToRepository();

        dirigible.openHomePage();

        DirigibleWorkbench workbench = dirigible.openWorkbench();
        workbench.expandProject(testProject.getRootFolderName());
        workbench.openFile(testProject.getEdmFileName());

        edmView.regenerate();

        workbench.refresh();
        workbench.publishAll();
    }

    private void waitForTenantsProvisioning() {
        testTenants.stream()
                   .forEach(t -> waitForTenantProvisioning(t, 30));
    }

    private void verifyTestProjectAccessibleByTenants() {
        testTenants.stream()
                   .forEach(t -> testProject.assertHomePageAccessibleByTenant(t));
    }

    /**
     * Verifies indirectly:<br>
     * - dirigible-test-project/views/readers.view is created and it is working<br>
     * - dirigible-test-project/csvim/data.csvim is imported <br>
     * - DefaultDB datasource is resolved correctly
     */
    private void verifyView() {
        testTenants.forEach(t -> restAssuredExecutor.execute(t, () -> verifyView(t)));
    }

    /**
     * Verifies indirectly:<br>
     * - dirigible-test-project/tables/reader.table is created<br>
     * - dirigible-test-project/csvim/data.csvim is imported <br>
     * - dirigible-test-project/odata/readers.odata is configured <br>
     * - OData is working<br>
     * - DefaultDB datasource is resolved correctly
     */
    private void verifyOData() {
        testTenants.forEach(t -> restAssuredExecutor.execute(t, () -> {
            verifyCSVIMIsImported();
            verifyAddingNewReader();
        }));
    }

    private void waitForTenantProvisioning(DirigibleTestTenant tenant, int waitSeconds) {
        Awaitility.await()
                  .atMost(waitSeconds, TimeUnit.SECONDS)
                  .until(() -> tenantCreator.isTenantProvisioned(tenant));
    }

    private void verifyView(DirigibleTestTenant tenant) {
        restAssuredExecutor.execute(tenant, //
                () -> given().when()
                             .get(testProject.getReadersViewServicePath())
                             .then()
                             .statusCode(200)
                             .body("$", hasSize(2))
                             .body("[0].READER_FIRST_NAME", equalTo("Ivan"))
                             .body("[0].READER_LAST_NAME", equalTo("Ivanov"))
                             .body("[1].READER_FIRST_NAME", equalTo("Maria"))
                             .body("[1].READER_LAST_NAME", equalTo("Petrova")));
    }

    private void verifyCSVIMIsImported() {
        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
               .when()
               .get(testProject.getReadersODataEntityPath())
               .then()
               .statusCode(200)
               .body("d.results", hasSize(2))
               .body("d.results[0].ReaderId", equalTo(1))
               .body("d.results[0].ReaderFirstName", equalTo("Ivan"))
               .body("d.results[0].ReaderLastName", equalTo("Ivanov"))
               .body("d.results[1].ReaderId", equalTo(2))
               .body("d.results[1].ReaderFirstName", equalTo("Maria"))
               .body("d.results[1].ReaderLastName", equalTo("Petrova"));
    }

    private void verifyAddingNewReader() {
        String firstName = "FirstName_" + RandomStringUtils.randomAlphabetic(5);
        String lastName = "LastName_" + RandomStringUtils.randomAlphabetic(5);
        String jsonPayload = String.format("""
                {
                    "ReaderId": 3,
                    "ReaderFirstName": "%s",
                    "ReaderLastName": "%s"
                }
                """, firstName, lastName);

        given().contentType(ContentType.JSON)
               .body(jsonPayload)
               .when()
               .post(testProject.getReadersODataEntityPath())
               .then()
               .statusCode(201);

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
               .when()
               .get(testProject.getReadersODataEntityPath())
               .then()
               .statusCode(200)
               .body("d.results", hasSize(3))
               .body("d.results[2].ReaderId", equalTo(3))
               .body("d.results[2].ReaderFirstName", equalTo(firstName))
               .body("d.results[2].ReaderLastName", equalTo(lastName));
    }

}
