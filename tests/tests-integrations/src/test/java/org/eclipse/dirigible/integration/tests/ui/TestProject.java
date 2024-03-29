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
package org.eclipse.dirigible.integration.tests.ui;

import ch.qos.logback.classic.Level;
import io.restassured.http.ContentType;
import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.tests.DirigibleTestTenant;
import org.eclipse.dirigible.tests.awaitility.AwaitilityExecutor;
import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.BrowserFactory;
import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.eclipse.dirigible.tests.logging.LogsAsserter;
import org.eclipse.dirigible.tests.restassured.RestAssuredExecutor;
import org.eclipse.dirigible.tests.util.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Lazy
@Component
public class TestProject {

    public static final String UI_HOME_PATH = "/services/web/dirigible-test-project/gen/index.html";
    public static final String BOOKS_SERVICE_PATH = "/services/ts/dirigible-test-project/gen/api/Books/BookService.ts";
    public static final String EDM_FILE_NAME = "edm.edm";
    public static final String READERS_ODATA_ENTITY_PATH = "/odata/v2/Readers";
    public static final String READERS_VIEW_SERVICE_PATH = "/services/ts/dirigible-test-project/views/ReaderViewService.ts";
    public static final String PROJECT_ROOT_FOLDER = "dirigible-test-project";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestProject.class);
    private static final String PROJECT_RESOURCES_PATH = "dirigible-test-project";
    private static final String UI_PROJECT_TITLE = "Dirigible Test Project";
    private final IRepository dirigibleRepo;
    private final BrowserFactory browserFactory;
    private final Dirigible dirigible;
    private final EdmView edmView;

    private final RestAssuredExecutor restAssuredExecutor;

    private final LogsAsserter testJobLogsAsserter;

    private final LogsAsserter eventListenerLogsAsserter;

    TestProject(IRepository dirigibleRepo, BrowserFactory browserFactory, Dirigible dirigible, EdmView edmView,
            RestAssuredExecutor restAssuredExecutor) {
        this.dirigibleRepo = dirigibleRepo;
        this.browserFactory = browserFactory;
        this.dirigible = dirigible;
        this.edmView = edmView;
        this.restAssuredExecutor = restAssuredExecutor;

        this.testJobLogsAsserter = new LogsAsserter("app.test-job-handler.ts", Level.DEBUG);
        this.eventListenerLogsAsserter = new LogsAsserter("app.book-entity-events-handler.ts", Level.DEBUG);
    }

    public void publish() {
        copyToRepository();

        dirigible.openHomePage();

        DirigibleWorkbench workbench = dirigible.openWorkbench();
        workbench.expandProject(PROJECT_ROOT_FOLDER);
        workbench.openFile(EDM_FILE_NAME);

        edmView.regenerate();

        workbench.publishAll();
    }

    public void copyToRepository() {
        String repoBasePath = dirigibleRepo.getRepositoryPath();
        String userWorkspace = repoBasePath + File.separator + "users" + File.separator
                + DirigibleConfig.BASIC_ADMIN_USERNAME.getFromBase64Value() + File.separator + "workspace";

        URL projectResource = TestProject.class.getClassLoader()
                                               .getResource(PROJECT_RESOURCES_PATH);
        if (null == projectResource) {
            throw new IllegalStateException("Missing test project resource folder with path " + PROJECT_RESOURCES_PATH);
        }
        String destinationDir = userWorkspace + File.separator + PROJECT_RESOURCES_PATH;

        File sourceDirectory = new File(projectResource.getPath());
        File destinationDirectory = new File(destinationDir);

        try {
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to copy test project to Dirigible repository", ex);
        }
    }

    public void verify(DirigibleTestTenant tenant) {
        LOGGER.info("Verifying test project for tenant [{}]...", tenant);

        verifyHomePageAccessibleByTenant(tenant);
        verifyView(tenant);
        verifyOData(tenant);
        verifyEdmGeneratedResources(tenant);

        LOGGER.info("Test test project for tenant [{}] has been verified successfully!", tenant);
    }

    public void verifyHomePageAccessibleByTenant(DirigibleTestTenant tenant) {
        Browser browser = browserFactory.createByHost(tenant.getHost());
        browser.openPath(UI_HOME_PATH);

        Dirigible dirigible = new Dirigible(browser, tenant.getUsername(), tenant.getPassword());
        boolean forceLogin = !tenant.isDefaultTenant();
        dirigible.login(forceLogin);

        waitToLoadThePage();
        browser.assertElementExistsByTypeAndText(HtmlElementType.HEADER3, UI_PROJECT_TITLE);
    }

    /**
     * Verifies indirectly:<br>
     * - dirigible-test-project/views/readers.view is created and it is working<br>
     * - dirigible-test-project/csvim/data.csvim is imported <br>
     * - default DB datasource is resolved correctly
     */
    private void verifyView(DirigibleTestTenant tenant) {
        restAssuredExecutor.execute(tenant, //
                () -> given().when()
                             .get(READERS_VIEW_SERVICE_PATH)
                             .then()
                             .statusCode(200)
                             .body("$", hasSize(2))
                             .body("[0].READER_FIRST_NAME", equalTo("Ivan"))
                             .body("[0].READER_LAST_NAME", equalTo("Ivanov"))
                             .body("[1].READER_FIRST_NAME", equalTo("Maria"))
                             .body("[1].READER_LAST_NAME", equalTo("Petrova")));
    }

    /**
     * Verifies indirectly:<br>
     * - dirigible-test-project/tables/reader.table is created<br>
     * - dirigible-test-project/csvim/data.csvim is imported <br>
     * - dirigible-test-project/odata/readers.odata is configured <br>
     * - OData is working<br>
     * - default DB datasource is resolved correctly
     */
    private void verifyOData(DirigibleTestTenant tenant) {
        restAssuredExecutor.execute(tenant, () -> {
            verifyCSVIMIsImported();
            verifyAddingNewReader(tenant);
        });
    }

    /**
     * Verifies indirectly:<br>
     * - edm generated schema is created<br>
     * - generated REST is created and it works<br>
     * - topic listener works<br>
     * - job has been executed<br>
     * - default DB datasource is resolved correctly
     */
    private void verifyEdmGeneratedResources(DirigibleTestTenant tenant) {
        restAssuredExecutor.execute(tenant, () -> verifyBookREST(tenant));
        verifyJobExecuted(tenant);
        verifyListenerExecuted(tenant);
    }

    private void waitToLoadThePage() {
        SleepUtil.sleepSeconds(1);
    }

    private void verifyCSVIMIsImported() {
        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
               .when()
               .get(READERS_ODATA_ENTITY_PATH)
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

    private void verifyAddingNewReader(DirigibleTestTenant tenant) {
        String firstName = "FirstName[" + tenant.getName() + "]";
        String lastName = "LastName[" + tenant.getName() + "]";
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
               .post(READERS_ODATA_ENTITY_PATH)
               .then()
               .statusCode(201);

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
               .when()
               .get(READERS_ODATA_ENTITY_PATH)
               .then()
               .statusCode(200)
               .body("d.results", hasSize(3))
               .body("d.results[2].ReaderId", equalTo(3))
               .body("d.results[2].ReaderFirstName", equalTo(firstName))
               .body("d.results[2].ReaderLastName", equalTo(lastName));
    }

    private void verifyBookREST(DirigibleTestTenant tenant) {
        String title = "Title[" + tenant.getName() + "]";
        String author = "Author[" + tenant.getName() + "]";
        String jsonPayload = String.format("""
                {
                    "Title": "%s",
                    "Author": "%s"
                }
                """, title, author);

        given().contentType(ContentType.JSON)
               .body(jsonPayload)
               .when()
               .post(BOOKS_SERVICE_PATH)
               .then()
               .statusCode(201);

        given().when()
               .get(BOOKS_SERVICE_PATH)
               .then()
               .statusCode(200)
               .body("$", hasSize(1))
               .body("[0].Id", equalTo(1))
               .body("[0].Title", equalTo(title))
               .body("[0].Author", equalTo(author));
    }

    private void verifyJobExecuted(DirigibleTestTenant tenant) {
        String expectedMessage = "Job: found [1] books. Books: [[{\"Id\":1,\"Title\":\"Title[" + tenant.getName()
                + "]\",\"Author\":\"Author[" + tenant.getName() + "]\"}]]";
        verifyMessageLogged(expectedMessage, testJobLogsAsserter);
    }

    private void verifyListenerExecuted(DirigibleTestTenant tenant) {
        String expectedMessage = "Listener: found [1] books. Books: [[{\"Id\":1,\"Title\":\"Title[" + tenant.getName()
                + "]\",\"Author\":\"Author[" + tenant.getName() + "]\"}]]";
        verifyMessageLogged(expectedMessage, eventListenerLogsAsserter);
    }

    private void verifyMessageLogged(String expectedMessage, LogsAsserter logsAsserter) {
        String failMessage =
                "Couldn't find message [" + expectedMessage + "] in the logs. Logged messages: " + logsAsserter.getLoggedMessages();
        AwaitilityExecutor.execute(failMessage, () -> Awaitility.await()
                                                                .atMost(10, TimeUnit.SECONDS)
                                                                .until(() -> logsAsserter.containsMessage(expectedMessage, Level.INFO)));
    }
}
