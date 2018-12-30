/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');

exports.execute = function(sql, parameters, databaseType, datasourceName) {
	var result = {};
	if (parameters) {
		if (databaseType) {
			if (datasourceName) {
				result = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'update', [sql, parameters, databaseType, datasourceName]);
			} else {
				result = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'update', [sql, parameters, databaseType]);
			}
		} else {
			result = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'update', [sql, parameters]);
		}
	} else {
		result = java.call('org.eclipse.dirigible.api.v3.db.DatabaseFacade', 'update', [sql]);
	}
	return result;
};
