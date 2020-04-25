/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * API v4 Query
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.execute = function(sql, parameters, databaseType, datasourceName) {
	var resultset = [];
	if (parameters) {
		var params = JSON.stringify(parameters);
		if (databaseType) {
			if (datasourceName) {
				resultset = org.eclipse.dirigible.api.v3.db.DatabaseFacade.query(sql, params, databaseType, datasourceName);
			} else {
				resultset = org.eclipse.dirigible.api.v3.db.DatabaseFacade.query(sql, params, databaseType);
			}
		} else {
			resultset = org.eclipse.dirigible.api.v3.db.DatabaseFacade.query(sql, params);
		}
	} else {
		resultset = org.eclipse.dirigible.api.v3.db.DatabaseFacade.query(sql);
	}
	if (resultset) {
		return JSON.parse(resultset);
	}
	return resultset;
};
