/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
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
