package org.eclipse.dirigible.database.persistence;

import java.sql.Connection;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.PersistenceCreateTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceDropTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceInsertProcessor;

public class PersistenceManager {
	
	
	public Object insert(Connection connection, Object pojo) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(pojo);
		PersistenceInsertProcessor insertProcessor = new PersistenceInsertProcessor();
		return insertProcessor.insert(connection, tableModel, pojo);
	}
	
	public Object insert(Connection connection, String modelJson, String objectJson) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(modelJson);
		PersistenceInsertProcessor insertProcessor = new PersistenceInsertProcessor();
		return insertProcessor.insert(connection, tableModel, objectJson);
	}
	
	public Object createTable(Connection connection, Class<? extends Object> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceCreateTableProcessor createTableProcessor = new PersistenceCreateTableProcessor();
		return createTableProcessor.create(connection, tableModel, null);
	}
	
	public Object createTable(Connection connection, String modelJson) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(modelJson);
		PersistenceCreateTableProcessor createTableProcessor = new PersistenceCreateTableProcessor();
		return createTableProcessor.create(connection, tableModel, null);
	}
	
	public Object dropTable(Connection connection, Class<? extends Object> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceDropTableProcessor dropTableProcessor = new PersistenceDropTableProcessor();
		return dropTableProcessor.drop(connection, tableModel, null);
	}
	
	public Object dropTable(Connection connection, String modelJson) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(modelJson);
		PersistenceDropTableProcessor dropTableProcessor = new PersistenceDropTableProcessor();
		return dropTableProcessor.drop(connection, tableModel, null);
	}

}
