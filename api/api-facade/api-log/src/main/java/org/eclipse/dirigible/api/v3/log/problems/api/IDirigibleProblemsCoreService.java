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
     * @param id the location of the file with the error
     * @return the Problem model
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public DirigibleProblemsModel getProblemById(Long id) throws DirigibleProblemsException;

    /**
     * Gets all Problems
     *
     * @return the Problem model list
     * @throws DirigibleProblemsException the dirigible problem exception
     */
    public List<DirigibleProblemsModel> getAllProblems() throws DirigibleProblemsException;
}
