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
package org.eclipse.dirigible.core.problems.service;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.core.problems.api.IProblemsCoreService;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.problems.model.ProblemsModel;
import org.eclipse.dirigible.core.problems.model.response.ResponseModel;
import org.eclipse.dirigible.core.problems.utils.DateValidator;
import org.eclipse.dirigible.core.problems.utils.ProblemsConstants;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.database.persistence.PersistenceFactory;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Class ProblemsCoreService.
 */
public class ProblemsCoreService implements IProblemsCoreService {

    /** The Constant LOGGER. */
    private static final Logger logger = LoggerFactory.getLogger(ProblemsCoreService.class);
    
    /** The data source. */
    private DataSource dataSource = null;
    
    /** The persistence manager. */
    private PersistenceManager<ProblemsModel> persistenceManager = new PersistenceManager<ProblemsModel>();

    /** The Constant PERCENT. */
    private static final String PERCENT = "%";
    
    /**
     * Gets the data source.
     *
     * @return the data source
     */
    protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.SYSTEM_DATASOURCE);
		}
		return dataSource;
	}

    /**
     * Creates the problem.
     *
     * @param problemsModel the problems model
     * @return the problems model
     * @throws ProblemsException the problems exception
     */
    @Override
    public ProblemsModel createProblem(ProblemsModel problemsModel) throws ProblemsException {
        problemsModel.setCreatedBy(UserFacade.getName());
        problemsModel.setCreatedAt(new Timestamp(new java.util.Date().getTime()));
        problemsModel.setStatus(ProblemsConstants.ACTIVE);

        try (Connection connection = getDataSource().getConnection()) {
            persistenceManager.insert(connection, problemsModel);
            return problemsModel;
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
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
     * @throws ProblemsException the problems exception
     */
    @Override
    public void save(String location, String type, String line, String column, String cause, String expected,
                     String category, String module, String source, String program) throws ProblemsException {

        ProblemsModel problemToPersist = new ProblemsModel(location, type, line, column, cause, expected, category,
                module, source, program);
        ProblemsModel problemsModel = getProblem(location, type, line, column);
        if (problemsModel == null) {
            createProblem(problemToPersist);
            if (logger.isErrorEnabled()) {logger.error("Created a new Problem: " +problemToPersist.toJson());}
        } else {
            if (!problemsModel.equals(problemToPersist)) {
                problemsModel.setCategory(category);
                problemsModel.setModule(module);
                problemsModel.setSource(source);
                problemsModel.setProgram(program);
                updateProblem(problemsModel);
                if (logger.isErrorEnabled()) {logger.error("Updated an existing Problem: " +problemsModel.toJson());}
            }
        }
    }

    /**
     * Exists problem.
     *
     * @param location the location
     * @param type the type
     * @param line the line
     * @param column the column
     * @return true, if successful
     * @throws ProblemsException the problems exception
     */
    @Override
    public boolean existsProblem(String location, String type, String line, String column) throws ProblemsException {
        return getProblem(location, type, line, column) != null;
    }

    /**
     * Update problem.
     *
     * @param problemsModel the problems model
     * @throws ProblemsException the problems exception
     */
    @Override
    public void updateProblem(ProblemsModel problemsModel) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            persistenceManager.update(connection, problemsModel);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Update problem status by id.
     *
     * @param id the id
     * @param status the status
     * @throws ProblemsException the problems exception
     */
    @Override
    public void updateProblemStatusById(Long id, String status) throws ProblemsException {
        ProblemsModel problemToUpdate = getProblemById(id);
        if (problemToUpdate != null) {
            problemToUpdate.setStatus(status);
            try (Connection connection = getDataSource().getConnection()) {
                persistenceManager.update(connection, problemToUpdate);
            } catch (SQLException e) {
                throw new ProblemsException(e);
            }
        }
    }

    /**
     * Update status multiple problems.
     *
     * @param ids the ids
     * @param status the status
     * @return the int
     * @throws ProblemsException the problems exception
     */
    @Override
    public int updateStatusMultipleProblems(List<Long> ids, String status) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
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

    /**
     * Gets the problem.
     *
     * @param location the location
     * @param type the type
     * @param line the line
     * @param column the column
     * @return the problem
     * @throws ProblemsException the problems exception
     */
    @Override
    public ProblemsModel getProblem(String location, String type, String line, String column) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            String sql = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_LOCATION = ? AND PROBLEM_TYPE = ? AND PROBLEM_LINE = ? AND PROBLEM_COLUMN = ?").toString();
            List<ProblemsModel> result = persistenceManager.query(connection, ProblemsModel.class,
                    sql, Arrays.asList(location, type, line, column));
            return result.isEmpty()? null : result.get(0);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Gets the problem by id.
     *
     * @param id the id
     * @return the problem by id
     * @throws ProblemsException the problems exception
     */
    @Override
    public ProblemsModel getProblemById(Long id) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            return persistenceManager.find(connection, ProblemsModel.class, id);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Gets the all problems.
     *
     * @return the all problems
     * @throws ProblemsException the problems exception
     */
    @Override
    public List<ProblemsModel> getAllProblems() throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            return persistenceManager.findAll(connection, ProblemsModel.class);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Fetch problems batch.
     *
     * @param condition the condition
     * @param limit the limit
     * @return the response model
     * @throws ProblemsException the problems exception
     */
    @Override
    public ResponseModel fetchProblemsBatch(String condition, int limit) throws ProblemsException {
        List<ProblemsModel> result = searchProblemsLimited(condition, limit);
        return new ResponseModel(result, result.size(), countProblems());
    }

    /**
     * Search problems limited.
     *
     * @param condition the condition
     * @param limit the limit
     * @return the list
     * @throws ProblemsException the problems exception
     */
    @Override
    public List<ProblemsModel> searchProblemsLimited(String condition, int limit) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            SelectBuilder sqlBuilder = SqlFactory.getNative(connection).select().column("*").from("DIRIGIBLE_PROBLEMS").limit(limit);
            List<Object> values = null;

            if (new DateValidator(DateTimeFormatter.ISO_LOCAL_DATE).isValid(condition)) {
                sqlBuilder.where("FORMATDATETIME(PROBLEM_CREATED_AT,'yyyy-MM-dd') = ?");
                values = Collections.singletonList(condition);
            } else if (!StringUtils.isEmpty(condition)) {
                sqlBuilder.where("PROBLEM_LOCATION LIKE ? " +
                                 "OR PROBLEM_TYPE LIKE ? " +
                                 "OR PROBLEM_LINE LIKE ? " +
                                 "OR PROBLEM_COLUMN LIKE ? " +
                                 "OR PROBLEM_CAUSE LIKE ? " +
                                 "OR PROBLEM_CREATED_BY LIKE ? " +
                                 "OR PROBLEM_CATEGORY LIKE ? " +
                                 "OR PROBLEM_MODULE LIKE ? " +
                                 "OR PROBLEM_SOURCE LIKE ? " +
                                 "OR PROBLEM_PROGRAM LIKE ? " +
                                 "OR PROBLEM_STATUS LIKE ?");

                values = Collections.nCopies(11, PERCENT + condition + PERCENT);
            }

            return persistenceManager.query(connection, ProblemsModel.class, sqlBuilder.toString(), values);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Count problems.
     *
     * @return the int
     * @throws ProblemsException the problems exception
     */
    @Override
    public int countProblems() throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            PersistenceTableModel tableModel = PersistenceFactory.createModel(ProblemsModel.class);
            return SqlFactory.getNative(connection).count(connection, tableModel.getTableName());
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Delete problem by id.
     *
     * @param id the id
     * @throws ProblemsException the problems exception
     */
    @Override
    public void deleteProblemById(Long id) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            persistenceManager.delete(connection, ProblemsModel.class, id);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Delete multiple problems by id.
     *
     * @param ids the ids
     * @return the int
     * @throws ProblemsException the problems exception
     */
    @Override
    public int deleteMultipleProblemsById(List<Long> ids) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            StringBuilder sql = new StringBuilder("DELETE FROM DIRIGIBLE_PROBLEMS WHERE PROBLEM_ID IN (");
            ids = new ArrayList<>(ids);
            ids.forEach(id -> sql.append("?,"));
            //delete the last ,
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
            return persistenceManager.execute(connection, sql.toString(), ids);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Delete problems by status.
     *
     * @param status the status
     * @return the int
     * @throws ProblemsException the problems exception
     */
    @Override
    public int deleteProblemsByStatus(String status) throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            String sql = SqlFactory.getNative(connection).delete().from("DIRIGIBLE_PROBLEMS")
                    .where("PROBLEM_STATUS = ?").toString();
            return persistenceManager.execute(connection, sql, Collections.singletonList(status));
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }

    /**
     * Delete all.
     *
     * @throws ProblemsException the problems exception
     */
    @Override
    public void deleteAll() throws ProblemsException {
        try (Connection connection = getDataSource().getConnection()) {
            persistenceManager.deleteAll(connection, ProblemsModel.class);
        } catch (SQLException e) {
            throw new ProblemsException(e);
        }
    }
}
