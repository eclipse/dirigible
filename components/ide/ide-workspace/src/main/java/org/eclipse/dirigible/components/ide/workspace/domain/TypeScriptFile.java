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
package org.eclipse.dirigible.components.ide.workspace.domain;

import org.eclipse.dirigible.components.api.security.UserFacade;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The Class TypeScriptFile.
 */
public class TypeScriptFile {

    /** The Constant IMPORT_STATEMENTS_PATTERN. */
    private static final Pattern IMPORT_STATEMENTS_PATTERN = Pattern.compile("(?:import|export)\\s+\\{.*\\}\\s+from\\s+['\"](.*)['\"];*");

    /** The repository. */
    private final IRepository repository;

    /** The workspace. */
    private final String workspace;

    /** The project. */
    private final String project;

    /** The file path. */
    private final String filePath;

    /** The source code. */
    private final String sourceCode;

    /** The imported files names. */
    private final List<String> importedFilesNames;

    /**
     * Instantiates a new type script file.
     *
     * @param repository the repository
     * @param workspace the workspace
     * @param project the project
     * @param filePath the file path
     */
    public TypeScriptFile(IRepository repository, String workspace, String project, String filePath) {
        var user = UserFacade.getName();
        this.repository = repository;
        this.workspace = workspace;
        this.project = project;
        this.filePath = filePath;
        this.sourceCode = getSourceCode(user, workspace, project, filePath);
        this.importedFilesNames = getImportedFilesNames(user, workspace, project, filePath, sourceCode);
    }

    /**
     * Gets the source code.
     *
     * @param user the user
     * @param workspace the workspace
     * @param project the project
     * @param filePath the file path
     * @return the source code
     */
    private String getSourceCode(String user, String workspace, String project, String filePath) {
        var repositoryPath = generateWorkspaceProjectFilePath(user, workspace, project, filePath);
        var resource = repository.getResource(repositoryPath);
        var content = new String(resource.getContent(), StandardCharsets.UTF_8);
        return content;
    }

    /**
     * Gets the imported files names.
     *
     * @param user the user
     * @param workspace the workspace
     * @param project the project
     * @param filePath the file path
     * @param sourceCode the source code
     * @return the imported files names
     */
    private List<String> getImportedFilesNames(String user, String workspace, String project, String filePath, String sourceCode) {
        var fileRepositoryPath =
                Path.of(repository.getInternalResourcePath(generateWorkspaceProjectFilePath(user, workspace, project, filePath)));
        var projectRepositoryPath = Path.of(repository.getInternalResourcePath(generateUserRepositoryPath(user)));
        var fileRepositoryPathParentDir = fileRepositoryPath.getParent();

        var allImports = new HashSet<String>();

        Matcher importStatementsMatcher = IMPORT_STATEMENTS_PATTERN.matcher(sourceCode);
        while (importStatementsMatcher.find()) {
            var fromStatement = importStatementsMatcher.group(1);
            allImports.add(fromStatement);
        }

        var relativeImports = allImports.stream()
                                        .filter(x -> x.startsWith("./") || x.startsWith("../"))
                                        .map(x -> {
                                            try {
                                                var importedModule = x.endsWith(".ts") ? x : x + ".ts";
                                                return fileRepositoryPathParentDir.resolve(Path.of(importedModule))
                                                                                  .toRealPath();
                                            } catch (Exception e) {
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)
                                        .map(x -> "/" + projectRepositoryPath.relativize(x))
                                        .collect(Collectors.toList());

        return relativeImports;
    }

    /**
     * Generate workspace project file path.
     *
     * @param user the user
     * @param workspace the workspace
     * @param project the project
     * @param path the path
     * @return the string
     */
    private String generateWorkspaceProjectFilePath(String user, String workspace, String project, String path) {
        return IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + user + IRepositoryStructure.SEPARATOR + workspace
                + IRepositoryStructure.SEPARATOR + project + IRepositoryStructure.SEPARATOR + path;
    }

    /**
     * Generate user repository path.
     *
     * @param user the user
     * @return the string
     */
    private String generateUserRepositoryPath(String user) {
        return IRepositoryStructure.PATH_USERS + IRepositoryStructure.SEPARATOR + user;
    }

    /**
     * Gets the source code.
     *
     * @return the source code
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Gets the imported files names.
     *
     * @return the imported files names
     */
    public List<String> getImportedFilesNames() {
        return importedFilesNames;
    }

    /**
     * Gets the workspace.
     *
     * @return the workspace
     */
    public String getWorkspace() {
        return workspace;
    }

    /**
     * Gets the project.
     *
     * @return the project
     */
    public String getProject() {
        return project;
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
}
