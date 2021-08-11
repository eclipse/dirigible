/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.problems;

import org.eclipse.dirigible.core.problems.api.IProblemsCoreService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.model.ProblemsModel;
import org.eclipse.dirigible.core.problems.service.ProblemsCoreService;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProblemsFacade implements IScriptingFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemsFacade.class);

    public static final void save(String location, String type, String line, String column,
                                            String category, String module, String source, String program) throws ProblemsException {

        IProblemsCoreService dirigibleProblemsCoreService = new ProblemsCoreService();
        ProblemsModel problemsModel = new ProblemsModel(location, type, line, column, category, module, source, program);
        dirigibleProblemsCoreService.save(problemsModel);
        LOGGER.error(problemsModel.toJson());
    }

    public static final String findProblem(Long id) throws ProblemsException {
        return new ProblemsCoreService().getProblemById(id).toJson();
    }

    public static final List<String> fetchAllProblems() throws ProblemsException {
        List<ProblemsModel> problemsList = new ProblemsCoreService().getAllProblems();

        return problemsList.stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
    }

    public static final void deleteProblem(Long id) throws ProblemsException {
        new ProblemsCoreService().deleteProblemById(id);
    }

    public static final void deleteAllByStatus(String status) throws ProblemsException {
        new ProblemsCoreService().deleteProblemsByStatus(status);
    }

    public static final void clearAllProblems() throws ProblemsException {
        new ProblemsCoreService().deleteAll();
    }

    public static final void updateStatus(Long id, String status) throws ProblemsException {
        new ProblemsCoreService().updateProblemStatusById(id, status);
    }

    public static final void updateStatusMultiple(List<Long> ids, String status) throws ProblemsException {
        new ProblemsCoreService().updateStatusMultipleProblems(ids, status);
    }
}
