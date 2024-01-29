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
package org.eclipse.dirigible.integration.tests.ui.tests;

import java.util.concurrent.TimeUnit;
import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.framework.HtmlElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirigibleNewProjectIT extends UserInterfaceIntegrationTest {

    private Dirigible dirigible;

    @BeforeEach
    void setUp() {
        this.dirigible = new Dirigible(browser);
    }

    @Test
    void testOpenHomepage() throws InterruptedException {
        dirigible.openHomePage();

        browser.clickElementByTypeAndText(HtmlElementType.BUTTON, "File");
        browser.clickElementByTypeAndText(HtmlElementType.SPAN, "New");
        browser.clickElementByTypeAndText(HtmlElementType.SPAN, "Project");

        TimeUnit.SECONDS.sleep(10);
    }
}
