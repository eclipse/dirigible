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

import org.eclipse.dirigible.tests.base.KeycloakUserInterfaceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;

/**
 * A person who holds no tenant of the application signs in through a real identity provider. A
 * group that only carries no tenant - here the one Cognito puts every federated user in - no longer
 * serves them the default tenant's shell: they are told on the picker that they have no access. A
 * platform role still lets staff into the default tenant.
 */
class KeycloakNoTenantSignInIT extends KeycloakUserInterfaceIntegrationTest {

    private static final String APPLICATION_SHELL = "/services/web/application/index.html";

    private static final String TENANT_CHIP = "tenant-menu-trigger";

    @Test
    void aUserWithOnlyAnUnrelatedGroupIsToldTheyHaveNoAccess() {
        KEYCLOAK.ensureUser("unrelated.signin@example.com", PASSWORD, "eu-central-1_AbCdEf123_Google");

        signInAndOpen(APPLICATION_SHELL, "unrelated.signin@example.com");

        Selenide.webdriver()
                .shouldHave(WebDriverConditions.urlContaining("/tenant-selection.html"), Duration.ofSeconds(60));
        Selenide.$("h1")
                .shouldHave(Condition.exactText("No access to this application"), Duration.ofSeconds(30));
        Selenide.$(By.tagName("body"))
                .shouldHave(Condition.text("not assigned to any tenant of this application"));
        Selenide.$(By.id(TENANT_CHIP))
                .shouldNot(Condition.exist);
    }

    @Test
    void aDeveloperWithoutATenantStillEntersTheDefaultTenant() {
        KEYCLOAK.ensureUser("developer.signin@example.com", PASSWORD, "DEVELOPER");

        signInAndOpen(APPLICATION_SHELL, "developer.signin@example.com");

        Selenide.$(By.id(TENANT_CHIP))
                .shouldBe(Condition.visible, Duration.ofSeconds(60))
                .shouldHave(Condition.text("default-tenant"));
    }
}
