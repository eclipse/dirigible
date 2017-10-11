/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.persistence.processors.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.UpdateBuilder;

public class PersistenceUpdateProcessor<T> extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		UpdateBuilder updateBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection)).update().table(tableModel.getTableName());

		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (!columnModel.isPrimaryKey()) {
				updateBuilder.set(columnModel.getName(), ISqlKeywords.QUESTION);
			}
		}

		updateBuilder.where(new StringBuilder() //
				.append(getPrimaryKey(tableModel)) //
				.append(ISqlKeywords.SPACE) //
				.append(ISqlKeywords.EQUALS) //
				.append(ISqlKeywords.SPACE) //
				.append(ISqlKeywords.QUESTION) //
				.toString());

		String sql = updateBuilder.toString();
		return sql;
	}

	public int update(Connection connection, PersistenceTableModel tableModel, Object pojo, Object id) throws PersistenceException {
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScript(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			setValuesFromPojo(tableModel, pojo, preparedStatement);
			setValue(preparedStatement, tableModel.getColumns().size(), id);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

	@Override
	protected boolean shouldSetColumnValue(PersistenceTableColumnModel columnModel) {
		return !columnModel.isPrimaryKey();
	}
}
