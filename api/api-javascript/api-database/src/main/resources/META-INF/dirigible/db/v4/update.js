/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Update
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.execute = function(sql, parameters, databaseType, datasourceName) {
	let result = {};
	if (parameters) {
		const params = JSON.stringify(parameters);
		result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql,params,databaseType,datasourceName)
	} else {
		result = org.eclipse.dirigible.api.v3.db.DatabaseFacade.update(sql,null,databaseType,datasourceName);
	}
	return result;
};
