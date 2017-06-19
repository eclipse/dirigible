package org.eclipse.dirigible.database.persistence.parser;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;

import com.google.gson.Gson;

public class PersistenceJsonParser<T> {
	
	private static final Gson gson = new Gson();
	
	public PersistenceTableModel parseModel(String json) throws PersistenceException {
		PersistenceTableModel persistenceTableModel = gson.fromJson(json, PersistenceTableModel.class);
		return persistenceTableModel;
	}
	
	public String serializeModel(PersistenceTableModel persistenceTableModel) {
		return gson.toJson(persistenceTableModel);
	}

}
