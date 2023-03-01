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
package org.eclipse.dirigible.api.v3.problems;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.service.ProblemsCoreService;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import java.util.List;

/**
 * The Class ProblemsFacade.
 */
public class ProblemsFacade implements IScriptingFacade {

    /**
     * Save.
     *
     * @param location the location
     * @param type the type
     * @param line the line
     * @param column the column
     * @param cause the cause
     * @param expected the expected
     * @param category the category
     * @param module the module
     * @param source the source
     * @param program the program
     * @throws ProblemsException the problems exception
     */
    public static final void save(String location, String type, String line, String column, String cause, String expected,
                                  String category, String module, String source, String program) throws ProblemsException {

        new ProblemsCoreService().save(location, type, line, column, cause, expected, category, module, source, program);
    }

    /**
     * Find problem.
     *
     * @param id the id
     * @return the string
     * @throws ProblemsException the problems exception
     */
    public static final String findProblem(Long id) throws ProblemsException {
        return new ProblemsCoreService().getProblemById(id).toJson();
    }

    /**
     * Fetch all problems.
     *
     * @return the string
     * @throws ProblemsException the problems exception
     */
    public static final String fetchAllProblems() throws ProblemsException {
        return GsonHelper.toJson(new ProblemsCoreService().getAllProblems());
    }

    /**
     * Fetch problems batch.
     *
     * @param condition the condition
     * @param limit the limit
     * @return the string
     * @throws ProblemsException the problems exception
     */
    public static final String fetchProblemsBatch(String condition, int limit) throws ProblemsException {
        return GsonHelper.toJson(new ProblemsCoreService().fetchProblemsBatch(condition, limit));
    }

    /**
     * Delete problem.
     *
     * @param id the id
     * @throws ProblemsException the problems exception
     */
    public static final void deleteProblem(Long id) throws ProblemsException {
        new ProblemsCoreService().deleteProblemById(id);
    }

    /**
     * Delete multiple problems by id.
     *
     * @param id the id
     * @throws ProblemsException the problems exception
     */
    public static final void deleteMultipleProblemsById(List<Long> id) throws ProblemsException {
        new ProblemsCoreService().deleteMultipleProblemsById(id);
    }

    /**
     * Delete all by status.
     *
     * @param status the status
     * @throws ProblemsException the problems exception
     */
    public static final void deleteAllByStatus(String status) throws ProblemsException {
        new ProblemsCoreService().deleteProblemsByStatus(status);
    }

    /**
     * Clear all problems.
     *
     * @throws ProblemsException the problems exception
     */
    public static final void clearAllProblems() throws ProblemsException {
        new ProblemsCoreService().deleteAll();
    }

    /**
     * Update status.
     *
     * @param id the id
     * @param status the status
     * @throws ProblemsException the problems exception
     */
    public static final void updateStatus(Long id, String status) throws ProblemsException {
        new ProblemsCoreService().updateProblemStatusById(id, status);
    }

    /**
     * Update status multiple.
     *
     * @param ids the ids
     * @param status the status
     * @throws ProblemsException the problems exception
     */
    public static final void updateStatusMultiple(List<Long> ids, String status) throws ProblemsException {
        new ProblemsCoreService().updateStatusMultipleProblems(ids, status);
    }
}
