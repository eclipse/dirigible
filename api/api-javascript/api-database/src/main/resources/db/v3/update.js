/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
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
