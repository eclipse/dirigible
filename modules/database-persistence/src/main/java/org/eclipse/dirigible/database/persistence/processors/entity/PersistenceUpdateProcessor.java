package org.eclipse.dirigible.database.persistence.processors.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.squle.ISquleKeywords;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;

public class PersistenceUpdateProcessor<T> extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		UpdateBuilder updateBuilder = Squle.getNative(Squle.deriveDialect(connection))
				.update()
				.table(tableModel.getTableName());
				
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			updateBuilder.set(columnModel.getName(), ISquleKeywords.QUESTION);
		}
		
		updateBuilder.where(getPrimaryKey(tableModel) + new StringBuilder()
				.append(ISquleKeywords.SPACE)
				.append(ISquleKeywords.EQUALS)
				.append(ISquleKeywords.SPACE)
				.append(ISquleKeywords.QUESTION).toString());
		
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
			setValue(preparedStatement, tableModel.getColumns().size() + 1, id);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

}
