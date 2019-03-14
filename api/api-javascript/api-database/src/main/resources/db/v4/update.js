/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * API v4 Update
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.execute = function(sql, parameters, databaseType, datasourceName) {
	var result = {};
	if (parameters) {
		var params = JSON.stringify(parameters);
		if (databaseType) {
			if (datasourceName) {
				result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql, params, databaseType, datasourceName);
			} else {
				result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql, params, databaseType);
			}
		} else {
			result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql, params);
		}
	} else {
		result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql);
	}
	return result;
};
