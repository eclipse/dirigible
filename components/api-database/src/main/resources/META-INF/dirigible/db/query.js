/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API Query
 *
 */

exports.execute = function(sql, parameters, datasourceName) {
	let resultset = [];
	if (parameters) {
		const params = JSON.stringify(parameters);
		resultset = org.eclipse.dirigible.components.api.db.DatabaseFacade.query(sql,params,datasourceName);
	} else {
		resultset = org.eclipse.dirigible.components.api.db.DatabaseFacade.query(sql,null,datasourceName);
	}
	if (resultset) {
		return JSON.parse(resultset);
	}
	return resultset;
};
