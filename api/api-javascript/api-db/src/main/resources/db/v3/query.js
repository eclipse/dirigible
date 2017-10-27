/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

var java = require('core/v3/java');

exports.execute = function(sql, parameters, databaseType, datasourceName) {
	var resultset = [];
	if (parameters) {
		if (databaseType) {
			if (datasourceName) {
				resultset = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'query', [sql, parameters, databaseType, datasourceName]);
			} else {
				resultset = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'query', [sql, parameters, databaseType]);
			}
		} else {
			resultset = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'query', [sql, parameters]);
		}
	} else {
		resultset = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'query', [sql]);
	}
	if (resultset) {
		return JSON.parse(resultset);
	}
	return resultset;
};