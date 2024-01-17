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
 * API Extensions
 *
 */

const ExtensionsFacade = Java.type("org.eclipse.dirigible.components.api.extensions.ExtensionsFacade");

export class Extensions {

	public static getExtensions(extensionPoint: string): string[] {
		const extensions = ExtensionsFacade.getExtensions(extensionPoint);
		return JSON.parse(JSON.stringify(extensions));
	}

	public static getExtensionPoints(): string[] {
		const extensionPoints = ExtensionsFacade.getExtensionPoints();
		return JSON.parse(JSON.stringify(extensionPoints));
	}
}

// @ts-ignore
if (typeof module !== 'undefined') {
	// @ts-ignore
	module.exports = Extensions;
}
