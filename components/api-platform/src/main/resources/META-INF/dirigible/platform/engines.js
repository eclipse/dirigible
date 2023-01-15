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
 * Get engine by type
 */
exports.getEngine = function(type) {
	return new Engine(type);
}

/**
 * Engine
 */
function Engine(type) {
	this.type = type;

	this.execute = function(projectName, projectFilePath, projectFilePathParam, parameters, debug) {
		const mapInstance = new java.util.HashMap();
		for (const property in parameters) {
				if (context.hasOwnProperty(property)) {
					mapInstance.put(property, context[property]);
				}
			}
		return org.eclipse.dirigible.components.api.platform.EnginesFacade
			.execute(projectName, projectFilePath, projectFilePathParam, mapInstance, debug);
	}

}

exports.getTypes = function() {
	const json = org.eclipse.dirigible.components.api.platform.EnginesFacade.getEngineTypes();
	return JSON.parse(json);
}
