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

const EnginesFacade = Java.type("org.eclipse.dirigible.components.api.platform.EnginesFacade");
const HashMap = Java.type("java.util.HashMap");

/**
 * Get engine by type
 */
export function getEngine(type) {
	return new Engine(type);
}

/**
 * Engine
 */
class Engine {
	constructor(private type) { }

	execute(projectName, projectFilePath, projectFilePathParam, parameters, debug) {
		const mapInstance = new HashMap();
		for (const property in parameters) {
			if (context.hasOwnProperty(property)) {
				mapInstance.put(property, context[property]);
			}
		}
		return EnginesFacade.execute(projectName, projectFilePath, projectFilePathParam, mapInstance, debug);
	}

}

export function getTypes() {
	const json = EnginesFacade.getEngineTypes();
	return JSON.parse(json);
}
