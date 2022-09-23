/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.core.git.test;

import java.io.File;
import java.io.IOException;

import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.command.InitCommand;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class InitCommandTest.
 */
public class InitCommandTest extends AbstractDirigibleTest {

    /** The init command. */
    private InitCommand initCommand;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.initCommand = new InitCommand();
    }

    /**
     * Inits the repository test.
     *
     * @throws GitConnectorException the git connector exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void initRepositoryTest() throws GitConnectorException, IOException {
        String gitEnabled = System.getenv(GitConnectorTest.DIRIGIBLE_TEST_GIT_ENABLED);
        if (gitEnabled != null) {
            String user = UserFacade.getName();
            if (GitFileUtils.getGitDirectory(user, "workspace1").exists()) {
                GitFileUtils.deleteGitDirectory(user, "workspace1", "workspace-repo");
            }
            File gitRepo = GitFileUtils.createGitDirectory(user, "workspace1", "workspace-repo");
            initCommand.execute(gitRepo.getCanonicalPath(), false);
        }
    }


}
