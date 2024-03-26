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

import org.awaitility.Awaitility;
import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.DirigibleWorkbench;
import org.eclipse.dirigible.integration.tests.ui.EdmView;
import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.BrowserImpl;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

// TODO to be deleted once the MultitenancyIT is implemented
class UITest {

    @Test
    void test() throws InterruptedException {
        TestProject testProject = new TestProject(null);
        Browser browser = new BrowserImpl(8080);
        Dirigible dirigible = new Dirigible(browser);

        dirigible.openHomePage();

        DirigibleWorkbench workbench = dirigible.openWorkbench();
        workbench.expandProject(testProject.getRootFolderName());
        workbench.openFile(testProject.getEdmFileName());

        EdmView edmView = new EdmView(browser);
        edmView.regenerate();

        workbench.publishAll();

        Awaitility.await()
                  .atMost(120, TimeUnit.SECONDS)
                  .until(() -> false);
    }
}
