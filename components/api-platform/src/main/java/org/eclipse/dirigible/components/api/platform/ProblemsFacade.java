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
package org.eclipse.dirigible.components.api.platform;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.ide.problems.domain.Problem;
import org.eclipse.dirigible.components.ide.problems.service.ProblemService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ProblemsFacade.
 */
@Component
public class ProblemsFacade implements InitializingBean {
	
	/** The instance. */
	private static ProblemsFacade INSTANCE;
	
	/** The problem service. */
	private ProblemService problemService;
	
	/**
	 * Instantiates a new problem facade.
	 *
	 * @param problemService the problem service
	 */
	@Autowired
	public ProblemsFacade(ProblemService problemService) {
		this.problemService = problemService;
	}
	
	/**
	 * After properties set.
	 *
	 * @throws Exception the exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		INSTANCE = this;		
	}
	
	/**
	 * Gets the instance.
	 *
	 * @return the problem facade
	 */
	public static ProblemsFacade get() {
        return INSTANCE;
    }
	
	/**
	 * Gets the problem service.
	 *
	 * @return the problem service
	 */
	public ProblemService getProblemService() {
		return problemService;
	}

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
     * @throws Exception the exception
     */
    public static final void save(String location, String type, String line, String column, String cause, String expected,
                                  String category, String module, String source, String program) throws Exception {
    	Problem problem = new Problem(location, type, line, column, cause, expected, category, module, source, program);
    	ProblemsFacade.get().getProblemService().save(problem);
    }

    /**
     * Find problem.
     *
     * @param id the id
     * @return the string
     * @throws Exception the exception
     */
    public static final String findProblem(Long id) throws Exception {
        return GsonHelper.toJson(ProblemsFacade.get().getProblemService().findById(id));
    }

    /**
     * Fetch all problems.
     *
     * @return the string
     * @throws Exception the exception
     */
    public static final String fetchAllProblems() throws Exception {
        return GsonHelper.toJson(ProblemsFacade.get().getProblemService().getAll());
    }

    /**
     * Fetch problems batch.
     *
     * @param condition the condition
     * @param limit the limit
     * @return the string
     * @throws Exception the exception
     */
    public static final String fetchProblemsBatch(String condition, int limit) throws Exception {
        return GsonHelper.toJson(ProblemsFacade.get().getProblemService().fetchProblemsBatch(condition, limit));
    }

    /**
     * Delete problem.
     *
     * @param id the id
     * @throws Exception the exception
     */
    public static final void deleteProblem(Long id) throws Exception {
    	ProblemsFacade.get().getProblemService().deleteById(id);
    }

    /**
     * Delete multiple problems by id.
     *
     * @param ids the ids
     * @throws Exception the exception
     */
    public static final void deleteMultipleProblemsById(List<Long> ids) throws Exception {
    	ProblemsFacade.get().getProblemService().deleteAllByIds(ids);
    }

    /**
     * Delete all by status.
     *
     * @param status the status
     * @throws Exception the exception
     */
    public static final void deleteAllByStatus(String status) throws Exception {
    	ProblemsFacade.get().getProblemService().deleteAllByStatus(status);
    }

    /**
     * Clear all problems.
     *
     * @throws Exception the exception
     */
    public static final void clearAllProblems() throws Exception {
    	ProblemsFacade.get().getProblemService().deleteAll();
    }

    /**
     * Update status.
     *
     * @param id the id
     * @param status the status
     * @throws Exception the exception
     */
    public static final void updateStatus(Long id, String status) throws Exception {
    	ProblemsFacade.get().getProblemService().updateStatusById(id, status);
    }

    /**
     * Update status multiple.
     *
     * @param ids the ids
     * @param status the status
     * @throws Exception the exception
     */
    public static final void updateStatusMultiple(List<Long> ids, String status) throws Exception {
    	ProblemsFacade.get().getProblemService().updateStatusByIds(ids, status);
    }
}
