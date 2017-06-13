package org.eclipse.dirigible.database.persistence.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.ISquleKeywords;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.builders.CreateTableBuilder;

public class PersistenceCreateTableProcessor extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		CreateTableBuilder createTableBuilder = Squle.getNative(Squle.deriveDialect(connection))
				.create()
				.table(tableModel.getTableName());
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			DataType dataType = DataType.valueOf(columnModel.getType());
			switch (dataType) {
				case VARCHAR: createTableBuilder.columnVarchar(columnModel.getName(), columnModel.getLength(), columnModel.isPrimaryKey()); break;
				case CHAR: createTableBuilder.columnVarchar(columnModel.getName(), columnModel.getLength(), columnModel.isPrimaryKey()); break;
				case DATE: createTableBuilder.columnDate(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case TIME: createTableBuilder.columnTime(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case TIMESTAMP: createTableBuilder.columnTimestamp(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case INTEGER: createTableBuilder.columnInteger(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case BIGINT: createTableBuilder.columnBigint(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case REAL: createTableBuilder.columnReal(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case DOUBLE: createTableBuilder.columnDouble(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case BOOLEAN: createTableBuilder.columnBoolean(columnModel.getName(), columnModel.isPrimaryKey()); break;
				case BLOB: createTableBuilder.columnBlob(columnModel.getName()); break;
			}
		}
		String sql = createTableBuilder.toString();
		return sql;
	}
	
	public Object create(Connection connection, PersistenceTableModel tableModel, Object pojo) throws PersistenceException {
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

	

	public Object create(Connection connection, PersistenceTableModel tableModel, String json)
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
