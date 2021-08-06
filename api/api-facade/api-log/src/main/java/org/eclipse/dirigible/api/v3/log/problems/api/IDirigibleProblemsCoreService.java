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
package org.eclipse.dirigible.api.v3.log.problems.api;

import org.eclipse.dirigible.api.v3.log.problems.exceptions.DirigibleProblemsException;
import org.eclipse.dirigible.api.v3.log.problems.model.DirigibleProblemsModel;

import java.util.List;

public interface IDirigibleProblemsCoreService {

    /**
     * Creates the Problem.
     *
     * @param parserErrorsModel the model to insert
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public DirigibleProblemsModel createProblem(DirigibleProblemsModel parserErrorsModel)
            throws DirigibleProblemsException;

    /**
     * Checks if the Problem entry exists and either creates it or updates it.
     *
     * @param toPersist the model for persistence
     * @throws DirigibleProblemsException the parser errors exception
     */
    public void createOrUpdateProblem(DirigibleProblemsModel toPersist)
            throws DirigibleProblemsException;

    /**
     * Checks if Problem exists.
     *
     * @param location the location of the file with the error
     * @param type     the type of the error
     * @param line     the line of the error
     * @return true, if successful
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public boolean existsProblem(String location, String type, String line) throws DirigibleProblemsException;

    /**
     * Update Problem.
     *
     * @param parserErrorsModel the existing entry with the new field values
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public void updateProblem(DirigibleProblemsModel parserErrorsModel) throws DirigibleProblemsException;

    /**
     * Updates the status of a Problem by the id.
     *
     * @param id of the problem
     * @param status of the problem
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public void updateProblemStatusById(Long id, String status) throws DirigibleProblemsException;

    /**
     * Updates the status of multiple Problems by their id.
     *
     * @param ids list of problem ids
     * @param status of the problem
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public void updateStatusMultipleProblems(List<Long> ids, String status) throws DirigibleProblemsException;

    /**
     * Gets the Problem.
     *
     * @param location the location of the file with the error
     * @param type     the type of the error
     * @param line     the line of the error
     * @return the Problems model
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public DirigibleProblemsModel getProblem(String location, String type, String line) throws DirigibleProblemsException;

    /**
     * Gets the Problem by id.
     *
     * @param id of the problem
     * @return the Problem model
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public DirigibleProblemsModel getProblemById(Long id) throws DirigibleProblemsException;

    /**
     * Gets all Problem by a list of ids.
     *
     * @param ids list of problem ids
     * @return the Problem model
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public List<DirigibleProblemsModel> getAllProblemsById(List<Long> ids) throws DirigibleProblemsException;

    /**
     * Gets all Problems
     *
     * @return the Problem model list
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public List<DirigibleProblemsModel> getAllProblems() throws DirigibleProblemsException;

    /**
     * Deletes the Problem by id.
     *
     * @param id of the problem
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public void deleteProblemById(Long id) throws DirigibleProblemsException;

    /**
     * Deletes all Problems by their status.
     *
     * @param status of the problem
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public void deleteProblemsByStatus(String status) throws DirigibleProblemsException;

    /**
     * Deletes all Problems.
     *
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public void deleteAll() throws DirigibleProblemsException;
}
