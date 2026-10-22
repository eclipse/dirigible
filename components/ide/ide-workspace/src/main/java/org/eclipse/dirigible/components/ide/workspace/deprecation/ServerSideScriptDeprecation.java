/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.ide.workspace.deprecation;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.LogSanitizer;
import org.eclipse.dirigible.components.base.publisher.PublisherHandler;
import org.eclipse.dirigible.components.base.script.ScriptEngineAvailability;
import org.eclipse.dirigible.components.ide.problems.domain.Problem;
import org.eclipse.dirigible.components.ide.problems.service.ProblemService;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Makes the deprecation of server-side JavaScript and TypeScript (removed in 16.0) visible where a
 * developer works: each publish reviews the projects it touched with
 * {@link ServerSideScriptDetector}, and a project that carries any gets one warning in the log and
 * one Problems view entry naming what was found and linking the migration guide. The entry is
 * replaced on every review and removed once the project carries none, or is unpublished.
 *
 * <p>
 * Where the JavaScript engine is not deployed, the same entry counts what the instance skips - the
 * project's script extension modules and its artefacts with a script handler - so dropping the
 * engine is never silent.
 *
 * <p>
 * Only a publish reviews a project, so the content the platform deploys to the registry itself is
 * never flagged. The signals are informational: nothing here changes how a script runs.
 */
@Component
class ServerSideScriptDeprecation implements PublisherHandler {

    /**
     * How to move server-side JavaScript and TypeScript to Java - the deprecation's umbrella issue,
     * until the migration guide (#8050) is published.
     */
    static final String MIGRATION_GUIDE = "https://github.com/eclipse-dirigible/dirigible/issues/8048";

    /** The Problems view category of the entries, one per project. */
    static final String PROBLEM_CATEGORY = "JavaScript deprecation";

    /** The Problems view type of the entries: each concerns a whole project. */
    static final String PROBLEM_TYPE = "project";

    private static final String PROBLEM_MODULE = "ide-workspace";

    private static final String PROBLEM_SOURCE = "ServerSideScriptDeprecation";

    private static final String PROBLEM_PROGRAM = "Eclipse Dirigible";

    /** The length of the Problems view cause column. */
    private static final int PROBLEM_CAUSE_MAX = 1024;

    private static final String PUBLISH_ALL = "*";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSideScriptDeprecation.class);

    private final IRepository repository;

    private final ProblemService problemService;

    private final ScriptEngineAvailability scriptEngineAvailability;

    /**
     * Instantiates the deprecation signals.
     *
     * @param repository the repository the projects are published in
     * @param problemService the Problems view's store
     * @param scriptEngineAvailability whether the JavaScript engine is deployed
     */
    ServerSideScriptDeprecation(IRepository repository, ProblemService problemService, ScriptEngineAvailability scriptEngineAvailability) {
        this.repository = repository;
        this.problemService = problemService;
        this.scriptEngineAvailability = scriptEngineAvailability;
    }

    /**
     * Reviews the published project, or each project of a published workspace - never the registry's
     * other content.
     */
    @Override
    public void afterPublish(String workspaceLocation, String registryLocation, AfterPublishMetadata metadata) {
        String project = metadata.projectName();
        if (project.isEmpty()) {
            repository.getCollection(workspaceLocation)
                      .getCollectionsNames()
                      .forEach(this::review);
        } else {
            review(project);
        }
    }

    /**
     * Reviews the project the unpublished location belongs to, or each flagged project when the
     * location names none: an entry outlives no project it describes.
     */
    @Override
    public void afterUnpublish(String location) {
        String[] segments = new RepositoryPath(location).getSegments();
        // /registry/public/<project>/...
        if (segments.length > 2 && !PUBLISH_ALL.equals(segments[2])) {
            review(segments[2]);
        } else {
            flaggedProjects().forEach(this::review);
        }
    }

    /**
     * The projects with an entry in the Problems view.
     *
     * @return the project names, sorted
     */
    List<String> flaggedProjects() {
        return problemService.findByCategory(PROBLEM_CATEGORY)
                             .stream()
                             .map(problem -> problem.getLocation()
                                                    .substring(1))
                             .distinct()
                             .sorted()
                             .toList();
    }

    /**
     * Replaces the project's entry with what its published content carries now. Synchronized, so two
     * publishes of one project cannot both find no entry and add one each.
     *
     * @param project the project name
     */
    synchronized void review(String project) {
        String location = IRepositoryStructure.SEPARATOR + project;
        ICollection published = repository.getCollection(IRepositoryStructure.PATH_REGISTRY_PUBLIC + location);
        ServerSideScripts scripts = published.exists() ? ServerSideScriptDetector.detect(published) : ServerSideScripts.NONE;
        problemService.deleteByLocationAndTypeAndCategory(location, PROBLEM_TYPE, PROBLEM_CATEGORY);
        if (scripts.isEmpty()) {
            return;
        }
        String message = message(LogSanitizer.sanitize(project), scripts);
        LOGGER.warn("{}", message);
        problemService.save(new Problem(location, PROBLEM_TYPE, "", "", truncate(message), "", PROBLEM_CATEGORY, PROBLEM_MODULE,
                PROBLEM_SOURCE, PROBLEM_PROGRAM));
    }

    private String message(String project, ServerSideScripts scripts) {
        String found = "Project [" + project + "] uses server-side JavaScript or TypeScript (" + scripts.describe() + ")";
        String consequence = scriptEngineAvailability.isAvailable() ? ", which is deprecated and will be removed in 16.0."
                : " and this instance has no JavaScript engine, so it does not run - JavaScript extension modules and artefacts skipped: "
                        + scripts.skippedWithoutEngine() + ".";
        return found + consequence + " Migration guide: " + MIGRATION_GUIDE;
    }

    private static String truncate(String cause) {
        return cause.length() > PROBLEM_CAUSE_MAX ? cause.substring(0, PROBLEM_CAUSE_MAX) : cause;
    }
}
