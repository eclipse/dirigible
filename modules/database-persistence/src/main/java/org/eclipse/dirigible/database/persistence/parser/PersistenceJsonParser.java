/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

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
