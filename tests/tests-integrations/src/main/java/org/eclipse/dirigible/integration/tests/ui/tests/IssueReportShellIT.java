/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.ui.tests;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;

import org.eclipse.dirigible.tests.base.UserInterfaceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.codeborne.selenide.Selenide;

/**
 * "Report an issue" in a real browser (#7948): a failed request's id reaches the report dialog of
 * the application shell, the report is filed from it, the reporter follows it on the shell's Issues
 * page, and the instance's operator finds it on the monitoring shell's Issues page.
 * <p>
 * Only a browser catches the failure mode of the shared fragments: a binding that throws inside the
 * dialog mounted with x-html aborts Alpine's walk with no server-side symptom.
 */
class IssueReportShellIT extends UserInterfaceIntegrationTest {

    private static final String APPLICATION_SHELL = "/services/web/application/index.html";

    private static final String MONITORING_SHELL = "/services/web/monitoring/index.html";

    private static final String SUBJECT = "Saving fails with a reference";

    private static final Duration RENDER = Duration.ofSeconds(30);

    @Test
    void aFailedRequestIsReportedFromTheUserMenuAndFollowedByReporterAndOperator() {
        ide.openPath(APPLICATION_SHELL);
        $(By.xpath("//span[normalize-space(text())='Dashboard']")).shouldBe(visible, RENDER);

        // a request the shared fetch client sees fail - it records the failure with its request id
        Selenide.executeJavaScript("App.services.api.get('/services/issues/987654321', { baseUrl: '' }).catch(() => {});");
        String requestId = awaitRecordedRequestId();

        browser.clickOnElementById("user-menu-trigger");
        browser.clickOnElementById("user-menu-report-issue");

        $(By.id("issues-report-dialog")).shouldBe(visible, RENDER);
        $(By.id("issues-report-reference")).shouldHave(text(requestId), RENDER);
        $(By.id("issues-report-subject")).setValue(SUBJECT);
        $(By.id("issues-report-bundle-toggle")).click();
        $(By.id("issues-report-bundle")).shouldHave(text(requestId), RENDER);
        $(By.id("issues-report-submit")).click();
        $(By.id("issues-report-dialog")).shouldNotBe(visible, RENDER);

        // the reporter's Issues page lists it, and opens its conversation
        Selenide.executeJavaScript("window.location.hash = '#/issues';");
        $(By.xpath("//*[@id='issues-page']//span[normalize-space(text())='" + SUBJECT + "']")).shouldBe(visible, RENDER)
                                                                                              .click();
        $(By.id("issues-page-detail")).shouldHave(text(SUBJECT), RENDER);

        // the operator of the instance finds it on the support desk
        ide.openPath(MONITORING_SHELL);
        browser.clickOnElementById("monitoring-nav-issues");
        $(By.xpath("//*[@id='monitoring-issues-page']//span[normalize-space(text())='" + SUBJECT + "']")).shouldBe(visible, RENDER)
                                                                                                         .click();
        $(By.id("monitoring-issues-detail")).shouldHave(text(requestId), RENDER);
    }

    private static String awaitRecordedRequestId() {
        long deadline = System.currentTimeMillis() + RENDER.toMillis();
        while (System.currentTimeMillis() < deadline) {
            String requestId = Selenide.executeJavaScript(
                    "const items = App.services.recentFailures.items; return items.length ? items[0].requestId : null;");
            if (requestId != null) {
                return requestId;
            }
            Selenide.sleep(250);
        }
        return fail("the failed request was never recorded with its request id");
    }
}
