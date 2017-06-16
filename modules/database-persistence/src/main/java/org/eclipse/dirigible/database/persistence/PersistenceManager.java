package org.eclipse.dirigible.database.persistence;

import java.sql.Connection;
import java.util.List;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.PersistenceCreateTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceDropTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceInsertProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceQueryProcessor;
import org.eclipse.dirigible.database.squle.Squle;

public class PersistenceManager<T> {
	
	
	public Object insert(Connection connection, Object pojo) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(pojo);
		PersistenceInsertProcessor insertProcessor = new PersistenceInsertProcessor();
		return insertProcessor.insert(connection, tableModel, pojo);
	}
	
	public int createTable(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceCreateTableProcessor createTableProcessor = new PersistenceCreateTableProcessor();
		return createTableProcessor.create(connection, tableModel);
	}
	
	public int dropTable(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceDropTableProcessor dropTableProcessor = new PersistenceDropTableProcessor();
		return dropTableProcessor.drop(connection, tableModel);
	}
	
	public T find(Connection connection, Class<T> clazz, Object id) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>();
		return queryProcessor.find(connection, tableModel, clazz, id);
	}
	
	public List<T> findAll(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>();
		return queryProcessor.findAll(connection, tableModel, clazz);
	}
	
	public List<T> query(Connection connection, Class<T> clazz, String sql, List<Object> values) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>();
		return queryProcessor.query(connection, tableModel, clazz, sql, values);
	}
	
	public boolean existsTable(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		try {
			return Squle.getNative(connection)
				.exists(connection, tableModel.getTableName());
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}
	
	

}
