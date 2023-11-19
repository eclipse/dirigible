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

import org.eclipse.dirigible.integration.tests.IntegrationTest;
import org.eclipse.dirigible.integration.tests.ui.framework.Browser;
import org.eclipse.dirigible.integration.tests.ui.framework.BrowserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;

abstract class UserInterfaceIntegrationTest extends IntegrationTest {

    @LocalServerPort
    private int localServerPort;

    protected Browser browser;

    @BeforeEach
    final void initBrowser() {
        this.browser = new BrowserImpl(localServerPort);
    }

}
