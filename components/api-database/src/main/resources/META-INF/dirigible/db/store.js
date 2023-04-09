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
 * API Data Store
 *
 */

exports.save = function(name, entry) {
	org.eclipse.dirigible.components.api.db.DataStoreFacade.save(name, JSON.stringify(entry));
};

exports.list = function(name) {
	let result = org.eclipse.dirigible.components.api.db.DataStoreFacade.list(name);
	return JSON.parse(result);
};

exports.get = function(name, id) {
	let result = org.eclipse.dirigible.components.api.db.DataStoreFacade.get(name, id);
	return JSON.parse(result);
};

exports.delete = function(name, id) {
	org.eclipse.dirigible.components.api.db.DataStoreFacade.delete(name, id);
};
