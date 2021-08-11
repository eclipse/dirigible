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
package org.eclipse.dirigible.core.problems.service;

import org.eclipse.dirigible.core.problems.api.IProblemsCoreService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.model.ProblemsModel;
import org.eclipse.dirigible.core.problems.utils.ProblemsConstants;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProblemsCoreService implements IProblemsCoreService {

    private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
    private PersistenceManager<ProblemsModel> persistenceManager = new PersistenceManager<ProblemsModel>();

    @Override
    public ProblemsModel createProblem(ProblemsModel problemsModel) throws ProblemsException {
        problemsModel.setCreatedBy(UserFacade.getName());
        problemsModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
        problemsModel.setStatus(ProblemsConstants.ACTIVE);

        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.insert(connection, problemsModel);
            return problemsModel;
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public void save(ProblemsModel problemToPersist) throws ProblemsException {

        ProblemsModel problemsModel = getProblem(problemToPersist.getLocation(), problemToPersist.getType(),
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
        }
    }

    @Override
    public boolean existsProblem(String location, String type, String line, String column) throws ProblemsException {
        return getProblem(location, type, line, column) != null;
    }

    @Override
    public void updateProblem(ProblemsModel problemsModel) throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.update(connection, problemsModel);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public void updateProblemStatusById(Long id, String status) throws ProblemsException {
        ProblemsModel problemToUpdate = getProblemById(id);
        if (problemToUpdate != null) {
            problemToUpdate.setStatus(status);
            try (Connection connection = dataSource.getConnection()) {
                persistenceManager.update(connection, problemToUpdate);
            } catch (SQLException e) {
                throw new ProblemsException(e);
            }
        }
    }

    @Override
    public int updateStatusMultipleProblems(List<Long> ids, String status) throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder sql = new StringBuilder("UPDATE DIRIGIBLE_PROBLEMS SET PROBLEM_STATUS = ? WHERE PROBLEM_ID IN (");
            ids = new ArrayList<>(ids);
            ids.forEach(id -> sql.append("?,"));
            //delete the last ,
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
            return persistenceManager.execute(connection, sql.toString(), status, ids);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public ProblemsModel getProblem(String location, String type, String line, String column) throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_LOCATION = ? AND PROBLEM_TYPE = ? AND PROBLEM_LINE = ? AND PROBLEM_COLUMN = ?").toString();
            List<ProblemsModel> result = persistenceManager.query(connection, ProblemsModel.class,
                    sql, Arrays.asList(location, type, line, column));
            return result.isEmpty()? null : result.get(0);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public ProblemsModel getProblemById(Long id) throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            return persistenceManager.find(connection, ProblemsModel.class, id);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public List<ProblemsModel> getAllProblems() throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            return persistenceManager.findAll(connection, ProblemsModel.class);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public void deleteProblemById(Long id) throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.delete(connection, ProblemsModel.class, id);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public int deleteProblemsByStatus(String status) throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_STATUS = ?").toString();
            return persistenceManager.execute(connection, sql, Collections.singletonList(status));
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    @Override
    public void deleteAll() throws ProblemsException {
        try (Connection connection = dataSource.getConnection()) {
            persistenceManager.deleteAll(connection, ProblemsModel.class);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }
}
