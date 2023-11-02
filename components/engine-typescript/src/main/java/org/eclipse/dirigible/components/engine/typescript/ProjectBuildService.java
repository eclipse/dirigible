/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.typescript;

import org.apache.commons.exec.CommandLine;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.process.execution.ProcessExecutionOptions;
import org.eclipse.dirigible.commons.process.execution.ProcessExecutor;
import org.eclipse.dirigible.commons.process.execution.output.OutputsPair;
import org.eclipse.dirigible.commons.process.execution.output.ProcessResult;
import org.eclipse.dirigible.components.base.command.CommandDescriptor;
import org.eclipse.dirigible.components.base.project.ProjectAction;
import org.eclipse.dirigible.components.base.project.ProjectMetadata;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The Class ProjectBuildService.
 */
@Component
public class ProjectBuildService {

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBuildCallback.class);

    /**
     * The repository.
     */
    private final IRepository repository;

    /**
     * The type script service.
     */
    private final TypeScriptService typeScriptService;

    /**
     * Instantiates a new project build service.
     *
     * @param repository        the repository
     * @param typeScriptService the type script service
     */
    @Autowired
    public ProjectBuildService(IRepository repository, TypeScriptService typeScriptService) {
        this.repository = repository;
        this.typeScriptService = typeScriptService;
    }

    /**
     * Builds the.
     *
     * @param project the project
     */
    public void build(String project) {
        build(project, null);
    }

    /**
     * Builds the project.
     *
     * @param project          the project
     * @param projectEntryPath the project entry path
     */
    public void build(String project, String projectEntryPath) {
        try {
            getProjectJson(project)
                    .ifPresentOrElse(
                            projectMetadata ->getPublishCommand(projectMetadata).ifPresent(command -> buildWithCommand(project, command)),
                            () -> maybeBuildTypeScript(project, projectEntryPath)
                    );
        } catch (Exception e) {
            var errorMessage = "Failed to build project: " + project + " with entry path: " + projectEntryPath;
            LOGGER.error(errorMessage, e);
        }
    }

    private static Optional<CommandDescriptor> getPublishCommand(ProjectMetadata projectMetadata) {
        List<ProjectAction> actions = projectMetadata.getActions();
        if (actions == null) {
            return Optional.empty();
        }

        return actions
                .stream()
                .filter(ProjectAction::isPublish)
                .findFirst()
                .flatMap(action ->
                        action.getCommands()
                                .stream()
                                .filter(CommandDescriptor::isCompatibleWithCurrentOS)
                                .findFirst()
                );
    }

    /**
     * Maybe build type script.
     *
     * @param project          the project
     * @param projectEntryPath the project entry path
     */
    private void maybeBuildTypeScript(String project, String projectEntryPath) {
        if (typeScriptService.shouldCompileTypeScript(project, projectEntryPath)) {
            typeScriptService.compileTypeScript(project, projectEntryPath);
        }
    }

    /**
     * Builds the with command.
     *
     * @param project      the project
     * @param buildCommand the build command
     */
    private void buildWithCommand(String project, CommandDescriptor buildCommand) {
        var projectPath = getProjectPath(project).toString();
        var options = new ProcessExecutionOptions();
        options.setWorkingDirectory(projectPath);
        var env = Map.of("PATH", System.getenv("PATH"));
        try {
            var processExecutor = ProcessExecutor.create();
            Future<ProcessResult<OutputsPair>> outputFuture = processExecutor.executeProcess(buildCommand.getCommand(), env, options);
            String output = outputFuture.get().getProcessOutputs().getStandardOutput();
            LOGGER.info(output);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Could not run command: " + buildCommand, e);
        }
    }

    /**
     * Maybe project json.
     *
     * @param project the project
     * @return the project json
     */
    private Optional<ProjectMetadata> getProjectJson(String project) {
        Path projectJsonPath = getProjectPath(project).resolve("project.json");

        if (!projectJsonPath.toFile().exists()) {
            return Optional.empty();
        }

        try {
            String projectJson = Files.readString(projectJsonPath);
            ProjectMetadata projectMetadata = GsonHelper.fromJson(projectJson, ProjectMetadata.class);
            return Optional.of(projectMetadata);
        } catch (Exception e) {
            LOGGER.error("Malformed project file: " + projectJsonPath, e);
            return Optional.empty();
        }
    }

    /**
     * Gets the project path.
     *
     * @param project the project
     * @return the project path
     */
    private Path getProjectPath(String project) {
        var repositoryRelativePath = new RepositoryPath(IRepositoryStructure.PATH_REGISTRY_PUBLIC, project).toString();
        String absolutePath = repository.getInternalResourcePath(repositoryRelativePath);
        return Path.of(absolutePath);
    }
}
