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

export class Store {
	public static save(name: string, entry: Object): void {
		DataStoreFacade.save(name, JSON.stringify(entry));
	};

	public static list(name: string): Object | Object[] {
		let result = DataStoreFacade.list(name);
		return JSON.parse(result);
	};

	public static get(name: string, id: string): Object | Object[] {
		let result = DataStoreFacade.get(name, id);
		return JSON.parse(result);
	};

	public static remove(name: string, id: string): void {
		DataStoreFacade.deleteEntry(name, id);
	};
}
