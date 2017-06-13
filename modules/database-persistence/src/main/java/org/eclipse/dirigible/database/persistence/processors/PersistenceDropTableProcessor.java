package org.eclipse.dirigible.database.persistence.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.builders.DropTableBuilder;

public class PersistenceDropTableProcessor extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		DropTableBuilder dropTableBuilder = Squle.getNative(Squle.deriveDialect(connection))
				.drop()
				.table(tableModel.getTableName());
		
		String sql = dropTableBuilder.toString();
		return sql;
	}
	
	public Object drop(Connection connection, PersistenceTableModel tableModel, Object pojo) throws PersistenceException {
		int result = 0;
		PreparedStatement preparedStatement = null;
		try {
			String sql = generateScript(connection, tableModel);
			preparedStatement = connection.prepareStatement(sql);
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

	

	public Object drop(Connection connection, PersistenceTableModel tableModel, String json)
			throws PersistenceException {
		int result = 0;
		PreparedStatement preparedStatement = null;
		try {
			String sql = generateScript(connection, tableModel);
			preparedStatement = connection.prepareStatement(sql);
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
