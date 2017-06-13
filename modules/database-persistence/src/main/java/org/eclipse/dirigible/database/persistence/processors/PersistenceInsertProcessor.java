package org.eclipse.dirigible.database.persistence.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.builders.InsertBuilder;

public class PersistenceInsertProcessor extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		InsertBuilder insertBuilder = Squle.getNative(Squle.deriveDialect(connection))
				.insert()
				.into(tableModel.getTableName());
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			insertBuilder.column(columnModel.getName());
		}
		String sql = insertBuilder.toString();
		return sql;
	}
	
	public Object insert(Connection connection, PersistenceTableModel tableModel, Object pojo) throws PersistenceException {
		int result = 0;
		PreparedStatement preparedStatement = null;
		try {
			String sql = generateScript(connection, tableModel);
			preparedStatement = connection.prepareStatement(sql);
			setValuesFromPojo(tableModel, pojo, preparedStatement);
			result = preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				throw new PersistenceException(e);
			}
		}
		return result;
	}

	

	public Object insert(Connection connection, PersistenceTableModel tableModel, String json)
			throws PersistenceException {
		int result = 0;
		PreparedStatement preparedStatement = null;
		try {
			String sql = generateScript(connection, tableModel);
			preparedStatement = connection.prepareStatement(sql);
			setValuesFromJson(tableModel, json, preparedStatement);
			result = preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				throw new PersistenceException(e);
			}
		}
		return result;
	}

}
