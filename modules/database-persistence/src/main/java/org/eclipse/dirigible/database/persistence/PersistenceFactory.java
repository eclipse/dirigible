/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

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
		PersistenceJsonParser<?> parser = new PersistenceJsonParser<>();
		PersistenceTableModel persistenceModel = parser.parseModel(json);
		return persistenceModel;
	}

}
