/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.persistence.processors.identity;

import java.sql.Connection;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;

public class PersistenceNextValueIdentityProcessor extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		return null;
	}

	public long nextval(Connection connection, PersistenceTableModel tableModel) throws PersistenceException {
		PersistenceManager<Identity> persistenceManager = new PersistenceManager<Identity>();
		if (!persistenceManager.tableExists(connection, Identity.class)) {
			persistenceManager.tableCreate(connection, Identity.class);
		}

		Identity identity = persistenceManager.find(connection, Identity.class, tableModel.getTableName());
		if (identity == null) {
			identity = new Identity();
			identity.setTable(tableModel.getTableName());
			identity.setValue(1);
			persistenceManager.insert(connection, identity);
			return 1;
		}
		identity = persistenceManager.lock(connection, Identity.class, tableModel.getTableName());
		identity.setValue(identity.getValue() + 1);
		persistenceManager.update(connection, identity, tableModel.getTableName());
		return identity.getValue();
	}

}
