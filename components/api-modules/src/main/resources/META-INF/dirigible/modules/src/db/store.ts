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

const DataStoreFacade = Java.type("org.eclipse.dirigible.components.api.db.DataStoreFacade");

export function save(name, entry) {
	DataStoreFacade.save(name, JSON.stringify(entry));
};

export function list(name) {
	let result = DataStoreFacade.list(name);
	return JSON.parse(result);
};

export function get(name, id) {
	let result = DataStoreFacade.get(name, id);
	return JSON.parse(result);
};

export function remove(name, id) {
	DataStoreFacade.deleteEntry(name, id);
};
