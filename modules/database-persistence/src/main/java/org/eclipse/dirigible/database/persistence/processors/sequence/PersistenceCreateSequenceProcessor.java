package org.eclipse.dirigible.database.persistence.processors.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.squle.ISquleKeywords;
import org.eclipse.dirigible.database.squle.Squle;
import org.eclipse.dirigible.database.squle.builders.sequence.CreateSequenceBuilder;

public class PersistenceCreateSequenceProcessor extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		CreateSequenceBuilder createSequenceBuilder = Squle.getNative(Squle.deriveDialect(connection))
				.create()
				.sequence(tableModel.getTableName() + ISquleKeywords.UNDERSCROE + ISquleKeywords.KEYWORD_SEQUENCE);
		
		String sql = createSequenceBuilder.toString();
		return sql;
	}
	
	public int create(Connection connection, PersistenceTableModel tableModel) throws PersistenceException {
		int result = 0;
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScript(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			result = preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return result;
	}

}
