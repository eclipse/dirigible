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
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;

public class PersistenceExecuteProcessor<T> extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		throw new PersistenceException("Generate Script method cannot be invoked in Query Processor");
	}

	public int execute(Connection connection, String sql, List<Object> values) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = openPreparedStatement(connection, sql);
			if (values != null) {
				int i = 1;
				for (Object value : values) {
					setValue(preparedStatement, i++, value);
				}
			}
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

}
