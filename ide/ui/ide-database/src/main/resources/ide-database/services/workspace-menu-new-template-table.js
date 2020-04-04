/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
exports.getTemplate = function() {
	var view = {
			"name":"database-table",
			"label":"Database Table",
			"extension":"table",
			"data":'{"name":"MYTABLE","type":"TABLE","columns":[{"name":"ID","type":"INTEGER","length":"0","nullable":"false","primaryKey":"true","defaultValue":""}]}'
	};
	return view;
};
