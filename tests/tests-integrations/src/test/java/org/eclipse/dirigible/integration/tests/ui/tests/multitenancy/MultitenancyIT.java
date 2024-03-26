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

import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.integration.tests.ui.DirigibleWorkbench;
import org.eclipse.dirigible.integration.tests.ui.EdmView;
import org.eclipse.dirigible.integration.tests.ui.tests.UserInterfaceIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MultitenancyIT extends UserInterfaceIntegrationTest {

    @Autowired
    private TestProject testProject;

    @Autowired
    private Dirigible dirigible;

    @Autowired
    private EdmView edmView;

    @Test
    void test() {
        prepareTestProject();
    }

    private void prepareTestProject() {
        testProject.copyToRepository();

        dirigible.openHomePage();

        DirigibleWorkbench workbench = dirigible.openWorkbench();
        workbench.expandProject(testProject.getRootFolderName());
        workbench.openFile(testProject.getEdmFileName());

        edmView.regenerate();

        workbench.publishAll();
    }
}
