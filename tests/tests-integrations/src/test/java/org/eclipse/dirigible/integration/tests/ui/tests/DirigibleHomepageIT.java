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

import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirigibleHomepageIT extends UserInterfaceIntegrationTest {

    private static final String ECLIPSE_DIRIGIBLE_HEADER = "Eclipse Dirigible";

    private Dirigible dirigible;

    @BeforeEach
    void setUp() {
        this.dirigible = new Dirigible(browser);
    }

    @Test
    void testOpenHomepage() {
        dirigible.openHomePage();

        browser.assertElementExistsByTypeAndText(HtmlElementType.HEADER5, ECLIPSE_DIRIGIBLE_HEADER);
    }
}
