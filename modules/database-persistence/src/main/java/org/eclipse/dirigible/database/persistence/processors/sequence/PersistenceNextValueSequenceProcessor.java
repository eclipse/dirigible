package org.eclipse.dirigible.database.persistence.processors.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.squle.ISquleKeywords;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;

public class PersistenceNextValueSequenceProcessor extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		NextValueSequenceBuilder nextValueBuilder = Squle.getNative(Squle.deriveDialect(connection))
				.nextval(tableModel.getTableName() + ISquleKeywords.UNDERSCROE + ISquleKeywords.KEYWORD_SEQUENCE);
		
		String sql = nextValueBuilder.toString();
		return sql;
	}
	
	public long nextval(Connection connection, PersistenceTableModel tableModel) throws PersistenceException {
		long result = -1;
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScript(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				result = resultSet.getLong(1);
				return result;
			}
			
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return result;
	}

}
