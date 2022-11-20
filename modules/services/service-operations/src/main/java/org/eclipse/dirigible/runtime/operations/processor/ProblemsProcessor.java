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
package org.eclipse.dirigible.runtime.operations.processor;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.service.ProblemsCoreService;

import java.util.List;

/**
 * The Class ProblemsProcessor.
 */
public class ProblemsProcessor {

    /** The problems core service. */
    private ProblemsCoreService problemsCoreService = new ProblemsCoreService();

    /**
     * List.
     *
     * @return the string
     * @throws ProblemsException the problems exception
     */
    public String list() throws ProblemsException {
        return GsonHelper.toJson(problemsCoreService.getAllProblems());
    }

    /**
     * Fetch problems batch.
     *
     * @param condition the condition
     * @param limit the limit
     * @return the string
     * @throws ProblemsException the problems exception
     */
    public String fetchProblemsBatch(String condition, int limit) throws ProblemsException {
        return GsonHelper.toJson(new ProblemsCoreService().fetchProblemsBatch(condition, limit));
    }

    /**
     * Update status.
     *
     * @param ids the ids
     * @param status the status
     * @throws ProblemsException the problems exception
     */
    public void updateStatus(List<Long> ids, String status) throws ProblemsException {
        problemsCoreService.updateStatusMultipleProblems(ids, status);
    }

    /**
     * Delete problems by status.
     *
     * @param status the status
     * @throws ProblemsException the problems exception
     */
    public void deleteProblemsByStatus(String status) throws ProblemsException {
        problemsCoreService.deleteProblemsByStatus(status);
    }

    /**
     * Clear.
     *
     * @throws ProblemsException the problems exception
     */
    public void clear() throws ProblemsException {
        problemsCoreService.deleteAll();
    }

    /**
     * Delete multiple problems by id.
     *
     * @param ids the ids
     * @throws ProblemsException the problems exception
     */
    public void deleteMultipleProblemsById(List<Long> ids) throws ProblemsException {
        problemsCoreService.deleteMultipleProblemsById(ids);
    }
}
