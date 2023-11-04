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
exports.getTemplate = function () {
	let template = {
		"name": "database-table",
		"label": "Database Table",
		"extension": "table",
		"data": '{"name":"MYTABLE","type":"TABLE","columns":[{"name":"ID","type":"INTEGER","length":"0","nullable":"false","primaryKey":"true","defaultValue":""}]}'
	};
	return template;
};