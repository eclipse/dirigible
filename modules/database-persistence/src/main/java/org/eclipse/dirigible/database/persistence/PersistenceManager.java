package org.eclipse.dirigible.database.persistence;

import java.sql.Connection;
import java.util.List;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.PersistenceCreateTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceDeleteProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceDropTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceInsertProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceQueryProcessor;
import org.eclipse.dirigible.database.persistence.processors.PersistenceUpdateProcessor;
import org.eclipse.dirigible.database.squle.Squle;

/**
 * PersistenceManager is a simple transport mechanism to store and retrieve
 * POJO object to/from underlying JDBC compliant database.
 * It reads a limited set of the standard JPA annotations from the POJO, such as Table, Id, Column, etc.
 * and generate a dialect dependent SQL script.
 * It works on flat POJOs and no lazy loading, associations, caches, etc. are supported.
 * It is the simplest possible persistence channel for POJOs and will stay at this level in the future
 * The POJO supported by this manager must have a single mandatory Id (PRIMARY KEY) field
 *
 * @param <T> type safety for a PersistenceManager instance
 */
public class PersistenceManager<T> {
	
	/**
	 * Create a table by a provided Class
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @return the result status of the create statement execution
	 */
	public int createTable(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceCreateTableProcessor createTableProcessor = new PersistenceCreateTableProcessor();
		return createTableProcessor.create(connection, tableModel);
	}
	
	/**
	 * Drop a table by a provided Class
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @return the result status of the drop statement execution
	 */
	public int dropTable(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceDropTableProcessor dropTableProcessor = new PersistenceDropTableProcessor();
		return dropTableProcessor.drop(connection, tableModel);
	}
	
	/**
	 * Check whether a table by a provided Class already exists
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @return true if exists and false otherwise
	 */
	public boolean existsTable(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		try {
			return Squle.getNative(connection)
				.exists(connection, tableModel.getTableName());
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}
	
	/**
	 * Insert a single record in the table representing the POJO instance 
	 * 
	 * @param connection the database connection
	 * @param pojo the POJO instance
	 * @return the result status of the insert statement execution
	 */
	public int insert(Connection connection, Object pojo) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(pojo);
		PersistenceInsertProcessor insertProcessor = new PersistenceInsertProcessor();
		return insertProcessor.insert(connection, tableModel, pojo);
	}
	
	/**
	 * Getter for the single POJO instance
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @param id the primary key field's value
	 * @return a POJO instance
	 */
	public T find(Connection connection, Class<T> clazz, Object id) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>();
		return queryProcessor.find(connection, tableModel, clazz, id);
	}
	
	/**
	 * Getter for all the POJO instances
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @return a list with the POJO instances
	 */
	public List<T> findAll(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>();
		return queryProcessor.findAll(connection, tableModel, clazz);
	}
	
	/**
	 * Custom query for narrow the search
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @param sql the custom SQL script
	 * @param values ordered parameters values
	 * @return a list with the POJO instances
	 */
	public List<T> query(Connection connection, Class<T> clazz, String sql, List<Object> values) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>();
		return queryProcessor.query(connection, tableModel, clazz, sql, values);
	}
	
	
	/**
	 * Delete a single record representing a single POJO instance
	 * 
	 * @param connection the database connection
	 * @param clazz the POJO's Class
	 * @param id the primary key field's value
	 * @return the result status of the delete statement execution
	 */
	public int delete(Connection connection, Class<T> clazz, Object id) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceDeleteProcessor<T> deleteProcessor = new PersistenceDeleteProcessor<T>();
		return deleteProcessor.delete(connection, tableModel, clazz, id);
	}
	
	/**
	 * 
	 * @param connection the database connection
	 * @param pojo the POJO instance
	 * @param id the primary key field's value
	 * @return the result status of the update statement execution
	 */
	public int update(Connection connection, Object pojo, Object id) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(pojo);
		PersistenceUpdateProcessor<T> updateProcessor = new PersistenceUpdateProcessor<T>();
		return updateProcessor.update(connection, tableModel, pojo, id);
	}

}
