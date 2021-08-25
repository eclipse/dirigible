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
package org.eclipse.dirigible.core.problems.api;

import org.eclipse.dirigible.commons.api.service.ICoreService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.model.ProblemsModel;

import java.util.List;

public interface IProblemsCoreService extends ICoreService {

    /**
     * Create Problem.
     * @param parserErrorsModel the model to insert
     * @return the Problem model
     * @throws ProblemsException the dirigible problem exception
     */
    public ProblemsModel createProblem(ProblemsModel parserErrorsModel)
            throws ProblemsException;

    /**
     * Checks if the Problem entry exists and either creates it or updates it.
     * Location, type, line and column are unique for each problem. Status is updated separately.
     *
     * @param location   the location of the file with the error
     * @param type       the type of the error
     * @param line       the line of the error
     * @param column     the column of the error
     * @param symbol     the problematic symbol
     * @param expected   the expected symbol
     * @param category   the category of the error, e.g. the submodule
     * @param module     the module within the program, in which the error was found
     * @param source     the action that produced the error
     * @param program    the program, in which the error was found
     * @throws ProblemsException the parser errors exception
     */
    public void save(String location, String type, String line, String column, String symbol, String expected,
                     String category, String module, String source, String program)
            throws ProblemsException;

    /**
     * Checks if Problem exists.
     *
     * @param location the location of the file with the error
     * @param type     the type of the error
     * @param line     the line of the error
     * @param column   the column of the error
     * @return true, if successful
     * @throws ProblemsException the dirigible problem exception
     */
    public boolean existsProblem(String location, String type, String line, String column) throws ProblemsException;

    /**
     * Update Problem.
     *
     * @param parserErrorsModel the existing entry with the new field values
     * @throws ProblemsException the dirigible problem exception
     */
    public void updateProblem(ProblemsModel parserErrorsModel) throws ProblemsException;

    /**
     * Updates the status of a Problem by the id.
     *
     * @param id     of the problem
     * @param status of the problem
     * @throws ProblemsException the dirigible problem exception
     */
    public void updateProblemStatusById(Long id, String status) throws ProblemsException;

    /**
     * Updates the status of multiple Problems by their id.
     *
     * @param ids    list of problem ids
     * @param status of the problem
     * @return the result status of the update statement execution
     * @throws ProblemsException the dirigible problem exception
     */
    public int updateStatusMultipleProblems(List<Long> ids, String status) throws ProblemsException;

    /**
     * Gets the Problem.
     *
     * @param location the location of the file with the error
     * @param type     the type of the error
     * @param line     the line of the error
     * @param column   the column of the error
     * @return the Problems model
     * @throws ProblemsException the dirigible problem exception
     */
    public ProblemsModel getProblem(String location, String type, String line, String column) throws ProblemsException;

    /**
     * Gets the Problem by id.
     *
     * @param id of the problem
     * @return the Problem model
     * @throws ProblemsException the dirigible problem exception
     */
    public ProblemsModel getProblemById(Long id) throws ProblemsException;

    /**
     * Gets all Problems
     *
     * @return Problem model list
     * @throws ProblemsException the dirigible problem exception
     */
    public List<ProblemsModel> getAllProblems() throws ProblemsException;

    /**
     * Deletes the Problem by id.
     *
     * @param id of the problem
     * @throws ProblemsException the dirigible problem exception
     */
    public void deleteProblemById(Long id) throws ProblemsException;

    /**
     * Deletes all Problems by their ids.
     *
     * @param ids list of problem ids
     * @return the result status of the delete statement execution
     * @throws ProblemsException the dirigible problem exception
     */
    public int deleteMultipleProblemsById(List<Long> ids) throws ProblemsException;

    /**
     * Deletes all Problems by their status.
     *
     * @param status of the problem
     * @return the result status of the delete statement execution
     * @throws ProblemsException the dirigible problem exception
     */
    public int deleteProblemsByStatus(String status) throws ProblemsException;

    /**
     * Deletes all Problems.
     *
     * @throws ProblemsException the dirigible problem exception
     */
    public void deleteAll() throws ProblemsException;
}
