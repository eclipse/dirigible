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
package org.eclipse.dirigible.api.v4.git.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.api.v3.platform.WorkspaceFacade;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.api.v4.git.GitFacade;
import org.eclipse.dirigible.core.git.GitCommitInfo;
import org.eclipse.dirigible.core.git.GitConnectorException;
import org.eclipse.dirigible.core.git.project.ProjectMetadata;
import org.eclipse.dirigible.core.git.utils.GitFileUtils;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.core.workspace.api.IFile;
import org.eclipse.dirigible.core.workspace.api.IProject;
import org.eclipse.dirigible.core.workspace.api.IWorkspace;
import org.eclipse.dirigible.core.workspace.json.ProjectDescriptor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

/**
 * The Class GitFacadeTest.
 */
public class GitFacadeTest extends AbstractDirigibleTest {

    /** The username. */
    private String username = "dirigible";
    
    /** The email. */
    private String email = "dirigible@eclipse.com";
    
    /** The project name. */
    private String projectName = "project1";
    
    /** The workspace name. */
    private String workspaceName = "workspace";
    
    /** The repository. */
    private String repository = "project1-repo";
    
    /** The gson. */
    private final Gson gson = new Gson();

    /**
     * Test init repository and commit.
     *
     * @throws GitAPIException the git API exception
     * @throws GitConnectorException the git connector exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testInitRepositoryAndCommit() throws GitAPIException, GitConnectorException, IOException {
        String user = UserFacade.getName();
        File tempGitDirectory = GitFileUtils.getGitDirectory(user, workspaceName, repository);
        boolean isExistingGitRepository = tempGitDirectory != null;
        if (isExistingGitRepository) {
            GitFacade.deleteRepository(workspaceName, repository);
        }

        IWorkspace workspace = WorkspaceFacade.createWorkspace(workspaceName);
        IProject project = workspace.createProject(projectName);
        project.createFile("www/oldFile", "str".getBytes(StandardCharsets.UTF_8));
        GitFacade.initRepository(user, email, workspaceName, projectName, repository, "Initial commit");
        List<ProjectDescriptor> repos = GitFacade.getGitRepositories(workspaceName);
        assertTrue(repos.size() == 1);

        project.createFile("www/newFile", "str".getBytes(StandardCharsets.UTF_8));
        String message = "Second commit";
        GitFacade.commit(username, email, workspaceName, repository, message, true);
        List<GitCommitInfo> history = GitFacade.getHistory(repository, workspaceName, projectName);
        assertTrue(history.size() == 2);
        assertTrue(history.get(0).getMessage().equals(message));
        assertProjectJsonExists(project);

        GitFacade.deleteRepository(workspaceName, repository);
        assertTrue(GitFileUtils.getGitDirectory(user, workspaceName, repository) == null);

        FileUtils.deleteDirectory(new File("./target/.git"));
    }

    /**
     * Assert project json exists.
     *
     * @param project the project
     */
    private void assertProjectJsonExists(IProject project) {
        IFile maybeProjectJson = project.getFile("project.json");
        assertTrue("project.json does not exist", maybeProjectJson.exists());

        String projectJsonContent = new String(maybeProjectJson.getContent(), StandardCharsets.UTF_8);
        Map<String, String> parsedProjectJsonContent = gson.fromJson(
                projectJsonContent,
                new TypeToken<Map<String, String>>() {
                }.getType()
        );

        assertEquals("Unexpected project.json project id", project.getName(), parsedProjectJsonContent.get("guid"));
        assertEquals("Unexpected project.json properties cound", 1, parsedProjectJsonContent.size());
    }

}

