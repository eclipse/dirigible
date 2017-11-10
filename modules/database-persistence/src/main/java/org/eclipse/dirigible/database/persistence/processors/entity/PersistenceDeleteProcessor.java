/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.DataTypeUtils;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;

public class PersistenceDeleteProcessor<T> extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		throw new PersistenceException("Generate Script method cannot be invoked in Delete Processor");
	}
	
	protected String generateScriptDelete(Connection connection, PersistenceTableModel tableModel) {
		DeleteBuilder deleteBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection))
				.delete()
				.from(tableModel.getTableName())
				.where(getPrimaryKey(tableModel) + new StringBuilder()
						.append(ISqlKeywords.SPACE)
						.append(ISqlKeywords.EQUALS)
						.append(ISqlKeywords.SPACE)
						.append(ISqlKeywords.QUESTION).toString());
		String sql = deleteBuilder.toString();
		return sql;
	}
	
	protected String generateScriptDeleteAll(Connection connection, PersistenceTableModel tableModel) {
		DeleteBuilder deleteBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection))
				.delete()
				.from(tableModel.getTableName());
		String sql = deleteBuilder.toString();
		return sql;
	}
	
	public int delete(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id) throws PersistenceException {
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScriptDelete(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			setValue(preparedStatement, 1, id);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}
	
	public int deleteAll(Connection connection, PersistenceTableModel tableModel, Class<T> clazz) throws PersistenceException {
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScriptDeleteAll(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

}
