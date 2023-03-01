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
package org.eclipse.dirigible.core.problems.test;

import org.eclipse.dirigible.core.problems.api.IProblemsCoreService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.model.ProblemsModel;
import org.eclipse.dirigible.core.problems.service.ProblemsCoreService;
import org.eclipse.dirigible.core.problems.utils.ProblemsConstants;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * The Class ProblemsCoreServiceTest.
 */
public class ProblemsCoreServiceTest extends AbstractDirigibleTest {

    /** The problems core service. */
    private IProblemsCoreService problemsCoreService;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        this.problemsCoreService = new ProblemsCoreService();
    }

    /**
     * Save problem test.
     *
     * @throws ProblemsException the problems exception
     */
    @Test
    public void saveProblemTest() throws ProblemsException {
        problemsCoreService.deleteAll();
        ProblemsModel problemsModel = new ProblemsModel("Test/test.xsjs", "Runtime", "1", "2",
                ";", ")", "JUnit", "API", "JUnit Test", "Dirigible");

        problemsCoreService.save(problemsModel.getLocation(), problemsModel.getType(), problemsModel.getLine(),
                problemsModel.getColumn(), problemsModel.getCause(), problemsModel.getExpected(),
                problemsModel.getCategory(), problemsModel.getModule(), problemsModel.getSource(), problemsModel.getProgram());

        ProblemsModel result = problemsCoreService.getProblem(problemsModel.getLocation(), problemsModel.getType(),
                problemsModel.getLine(), problemsModel.getColumn());
        assertEquals(problemsModel, result);

        // test if the problem gets updated
        problemsModel.setSource("Manual Update");
        problemsCoreService.save(problemsModel.getLocation(), problemsModel.getType(), problemsModel.getLine(),
                problemsModel.getColumn(), problemsModel.getCause(), problemsModel.getExpected(),
                problemsModel.getCategory(), problemsModel.getModule(), problemsModel.getSource(), problemsModel.getProgram());
        ProblemsModel resultUpdated = problemsCoreService.getProblem(problemsModel.getLocation(), problemsModel.getType(),
                problemsModel.getLine(), problemsModel.getColumn());
        assertEquals(problemsModel, resultUpdated);
        problemsCoreService.deleteAll();
    }

    /**
     * Update problem status test.
     *
     * @throws ProblemsException the problems exception
     */
    @Test
    public void updateProblemStatusTest() throws ProblemsException {
        problemsCoreService.deleteAll();
        ProblemsModel problemsModel = new ProblemsModel("Test/test.xsjs", "Runtime", "1", "2",
                ";", ")", "JUnit", "API", "JUnit Test", "Dirigible");
        List<Long> ids = new ArrayList<>();
        // create problems and save their ids
        ids.add(problemsCoreService.createProblem(problemsModel).getId());
        // create a second problem
        problemsModel.setLocation("Demo/demo.xsjs");
        ids.add(problemsCoreService.createProblem(problemsModel).getId());

        problemsCoreService.updateStatusMultipleProblems(ids, ProblemsConstants.IGNORED);
        problemsCoreService.getAllProblems().forEach(element -> {
            assertEquals(ProblemsConstants.IGNORED, element.getStatus());
        });

        // test the status update of a single problem
        problemsCoreService.updateProblemStatusById(ids.get(0), ProblemsConstants.SOLVED);
        assertEquals(ProblemsConstants.SOLVED, problemsCoreService.getProblemById(ids.get(0)).getStatus());
        problemsCoreService.deleteAll();
    }

    /**
     * Delete problems by status test.
     *
     * @throws ProblemsException the problems exception
     */
    @Test
    public void deleteProblemsByStatusTest() throws ProblemsException {
        problemsCoreService.deleteAll();
        ProblemsModel problemsModel = new ProblemsModel("Test/test.xsjs", "Runtime", "1", "2",
                ";", ")", "JUnit", "API", "JUnit Test", "Dirigible");
        problemsCoreService.createProblem(problemsModel);
        // create a second problem
        problemsModel.setLocation("Demo/demo.xsjs");
        problemsCoreService.createProblem(problemsModel);
        problemsCoreService.deleteProblemsByStatus(ProblemsConstants.ACTIVE);
        assertTrue(problemsCoreService.getAllProblems().isEmpty());

        // test deletion by id
        Long idToDelete = problemsCoreService.createProblem(problemsModel).getId();
        problemsCoreService.deleteProblemById(idToDelete);
        assertNull(problemsCoreService.getProblemById(idToDelete));
    }
}