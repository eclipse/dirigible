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

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.integration.tests.ui.Dirigible;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.tests.framework.Browser;
import org.eclipse.dirigible.tests.framework.BrowserFactory;
import org.eclipse.dirigible.tests.framework.DirigibleTestTenant;
import org.eclipse.dirigible.tests.framework.HtmlElementType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Lazy
@Component
class TestProject {

    public static final String UI_HOME_PATH = "/services/web/dirigible-test-project/gen/index.html";
    private static final String PROJECT_RESOURCES_PATH = "dirigible-test-project";
    private static final String ADMIN_USERNAME = new String(Base64.getDecoder()
                                                                  .decode(Configuration.get(Configuration.BASIC_USERNAME, "YWRtaW4=")));
    private static final String UI_PROJECT_TITLE = "Dirigible Test Project";

    private final IRepository dirigibleRepo;
    private final BrowserFactory browserFactory;

    TestProject(IRepository dirigibleRepo, BrowserFactory browserFactory) {
        this.dirigibleRepo = dirigibleRepo;
        this.browserFactory = browserFactory;
    }

    void copyToRepository() {
        String repoBasePath = dirigibleRepo.getRepositoryPath();
        String userWorkspace = repoBasePath + File.separator + "users" + File.separator + ADMIN_USERNAME + File.separator + "workspace";

        URL projectResource = TestProject.class.getClassLoader()
                                               .getResource(PROJECT_RESOURCES_PATH);
        if (null == projectResource) {
            throw new IllegalStateException("Missing test project resource folder with path " + PROJECT_RESOURCES_PATH);
        }
        String destinationDir = userWorkspace + File.separator + PROJECT_RESOURCES_PATH;

        String projectResourcesPath = projectResource.getPath();
        File sourceDirectory = new File(projectResource.getPath());
        File destinationDirectory = new File(destinationDir);
        if (destinationDirectory.exists()) {
            FileSystemUtils.deleteRecursively(destinationDirectory);
            destinationDirectory.delete();
        }
        try {
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to copy test project to Dirigible repository", ex);
        }
    }

    public String getRootFolderName() {
        return "dirigible-test-project";
    }

    public String getEdmFileName() {
        return "edm.edm";
    }

    public void assertHomePageAccessibleByTenant(DirigibleTestTenant tenant) {
        Browser browser = browserFactory.createByTenantSubdomain(tenant.getSubdomain());
        browser.openPath(UI_HOME_PATH);

        Dirigible dirigible = new Dirigible(browser, tenant.getUsername(), tenant.getPassword());
        dirigible.login();

        waitToLoadThePage();
        browser.assertElementExistsByTypeAndText(HtmlElementType.HEADER3, UI_PROJECT_TITLE);
    }

    private void waitToLoadThePage() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Failed to fall asleep", e);
        }
    }

    public String getReadersODataEntityPath() {
        return "/odata/v2/Readers";
    }

    public String getReadersViewServicePath() {
        return "/services/ts/dirigible-test-project/views/ReaderViewService.ts";
    }
}
