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
 * API Globals
 */

const GlobalsFacade = Java.type("org.eclipse.dirigible.components.api.core.GlobalsFacade");

export class Globals{

	public static get(name: string): string {
		return GlobalsFacade.get(name);
	};

	public static set(name: string, value: string): void {
		GlobalsFacade.set(name, value);
	};

	public static list(): string {
		return GlobalsFacade.list();
	};
}
