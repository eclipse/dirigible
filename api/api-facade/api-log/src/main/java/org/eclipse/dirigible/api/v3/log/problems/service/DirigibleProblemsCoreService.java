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
package org.eclipse.dirigible.api.v3.log.problems.service;

import org.eclipse.dirigible.api.v3.log.problems.api.IDirigibleProblemsCoreService;
import org.eclipse.dirigible.api.v3.log.problems.exceptions.DirigibleProblemsException;
import org.eclipse.dirigible.api.v3.log.problems.model.DirigibleProblemsModel;
import org.eclipse.dirigible.api.v3.log.problems.utils.ProblemsConstants;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DirigibleProblemsCoreService implements IDirigibleProblemsCoreService {

    private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);

    private PersistenceManager<DirigibleProblemsModel> persistenceManager = new PersistenceManager<DirigibleProblemsModel>();

    @Override
    public DirigibleProblemsModel createProblem(DirigibleProblemsModel problemsModel) throws DirigibleProblemsException {
        problemsModel.setCreatedBy(UserFacade.getName());
        problemsModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
        problemsModel.setStatus(ProblemsConstants.ACTIVE);

        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.insert(connection, problemsModel);
            return problemsModel;
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public void createOrUpdateProblem(DirigibleProblemsModel problemToPersist) throws DirigibleProblemsException {

        DirigibleProblemsModel problemsModel = getProblem(problemToPersist.getLocation(), problemToPersist.getType(),
                problemToPersist.getLine(), problemToPersist.getColumn());
        if (problemsModel == null) {
            createProblem(problemToPersist);
        } else {
            if (!problemsModel.equals(problemToPersist)) {
                problemsModel.setLocation(problemToPersist.getLocation());
                problemsModel.setType(problemToPersist.getType());
                problemsModel.setLine(problemToPersist.getLine());
                problemsModel.setSource(problemToPersist.getSource());
                updateProblem(problemsModel);
            }
        }    }

    @Override
    public boolean existsProblem(String location, String type, String line, String column) throws DirigibleProblemsException {
        return getProblem(location, type, line, column) != null;
    }

    @Override
    public void updateProblem(DirigibleProblemsModel problemsModel) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.update(connection, problemsModel);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public void updateProblemStatusById(Long id, String status) throws DirigibleProblemsException {
        DirigibleProblemsModel problemToUpdate = getProblemById(id);
        if (problemToUpdate != null) {
            problemToUpdate.setStatus(status);
            try (Connection connection = dataSource.getConnection()) {
                persistenceManager.update(connection, problemToUpdate);
            } catch (SQLException e) {
                throw new DirigibleProblemsException(e);
            }
        }
    }

    @Override
    public void updateStatusMultipleProblems(List<Long> ids, String status) throws DirigibleProblemsException {
        List<DirigibleProblemsModel> problemsToUpdate = getAllProblemsById(ids);
        if (problemsToUpdate != null && !problemsToUpdate.isEmpty()) {
            try (Connection connection = dataSource.getConnection()) {
                problemsToUpdate.forEach(problemsModel -> {
                    problemsModel.setStatus(status);
                    persistenceManager.update(connection, problemsModel);
                });
            } catch (SQLException e) {
                throw new DirigibleProblemsException(e);
            }
        }
    }

    @Override
    public DirigibleProblemsModel getProblem(String location, String type, String line, String column) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_LOCATION = ? AND PROBLEM_TYPE = ? AND PROBLEM_LINE = ? AND PROBLEM_COLUMN = ?").toString();
            List<DirigibleProblemsModel> result = persistenceManager.query(connection, DirigibleProblemsModel.class,
                    sql, Arrays.asList(location, type, line, column));
            return result.isEmpty()? null : result.get(0);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public DirigibleProblemsModel getProblemById(Long id) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            return persistenceManager.find(connection, DirigibleProblemsModel.class, id);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public List<DirigibleProblemsModel> getAllProblemsById(List<Long> ids) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_ID").toString();
            StringBuilder query = new StringBuilder(sql + " IN (");
            ids.forEach(id -> query.append("?,"));
            //delete the last ,
            query.deleteCharAt(query.length() - 1);
            query.append(")");
            return persistenceManager.query(connection, DirigibleProblemsModel.class, query.toString(), Collections.singletonList(ids));
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public List<DirigibleProblemsModel> getAllProblems() throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            return persistenceManager.findAll(connection, DirigibleProblemsModel.class);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public void deleteProblemById(Long id) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.delete(connection, DirigibleProblemsModel.class, id);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public int deleteProblemsByStatus(String status) throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_STATUS = ?").toString();
            return persistenceManager.execute(connection, sql, Collections.singletonList(status));
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }

    @Override
    public void deleteAll() throws DirigibleProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.deleteAll(connection, DirigibleProblemsModel.class);
        } catch (SQLException e) {
            throw new DirigibleProblemsException(e);
        }
    }
}
