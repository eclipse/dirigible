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

import java.time.Duration;

import org.eclipse.dirigible.tests.base.ProjectDeployer;
import org.eclipse.dirigible.tests.base.UserInterfaceIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

/**
 * The IDE shell's deprecation banner of server-side JavaScript and TypeScript (#8049): absent while
 * no published project uses them, shown once one does, taking its room from the perspectives rather
 * than from the status bar, and gone for good in the browser once dismissed.
 */
public class JavaScriptDeprecationBannerIT extends UserInterfaceIntegrationTest {

    private static final String PROJECT = "deprecation-banner";

    private static final By BANNER = By.cssSelector("javascript-deprecation-banner .fd-message-strip");

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    /** How long the shell gets to fetch the notice before the banner is taken to be absent. */
    private static final long SETTLE_MILLIS = 3000;

    @Autowired
    private ProjectDeployer projectDeployer;

    @Test
    void theBannerShowsWhileAProjectUsesServerSideScriptsAndStaysDismissed() {
        ide.openIde();
        assertBannerAbsent();

        projectDeployer.deploy("JavaScriptDeprecationBannerIT/" + PROJECT);
        ide.reload();

        SelenideElement banner = Selenide.$(BANNER)
                                         .shouldBe(Condition.visible, TIMEOUT)
                                         .shouldHave(Condition.text("Server-side JavaScript and TypeScript are deprecated"))
                                         .shouldHave(Condition.text(PROJECT));
        Object statusBarInView = Selenide.executeJavaScript(
                "return document.querySelector('.statusbar').getBoundingClientRect().bottom <= window.innerHeight + 1;");
        Assertions.assertEquals(Boolean.TRUE, statusBarInView, "the banner pushed the status bar out of the window");

        banner.$(By.cssSelector("button[aria-label='Close']"))
              .click();
        Selenide.$(BANNER)
                .shouldNot(Condition.exist, TIMEOUT);

        ide.reload();
        assertBannerAbsent();
    }

    private void assertBannerAbsent() {
        Selenide.$(By.id("perspective-workbench"))
                .shouldBe(Condition.visible, TIMEOUT);
        Selenide.sleep(SETTLE_MILLIS);
        Selenide.$(BANNER)
                .shouldNot(Condition.exist);
    }
}
