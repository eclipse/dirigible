package org.eclipse.dirigible.database.persistence;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.parser.PersistenceAnnotationsParser;
import org.eclipse.dirigible.database.persistence.parser.PersistenceJsonParser;

public class PersistenceFactory {
	
	public static PersistenceTableModel createModel(Object pojo) throws PersistenceException {
		PersistenceAnnotationsParser parser = new PersistenceAnnotationsParser();
		PersistenceTableModel persistenceModel = parser.parsePojo(pojo);
		return persistenceModel;
	}
	
	public static PersistenceTableModel createModel(Class<? extends Object> clazz) throws PersistenceException {
		PersistenceAnnotationsParser parser = new PersistenceAnnotationsParser();
		PersistenceTableModel persistenceModel = parser.parsePojo(clazz);
		return persistenceModel;
	}
	
	public static PersistenceTableModel createModel(String json) throws PersistenceException {
		PersistenceJsonParser parser = new PersistenceJsonParser();
		PersistenceTableModel persistenceModel = parser.parseJson(json);
		return persistenceModel;
	}

}
