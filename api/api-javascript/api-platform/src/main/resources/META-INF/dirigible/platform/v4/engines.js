/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * Get engine by type
 */
exports.getEngine = function (type) {
    return new Engine(type);
}

/**
 * Engine
 */
function Engine(type) {
    this.type = type;

    this.execute = function (module, context) {
        var mapInstance = new java.util.HashMap();
        for (var property in context) {
            if (context.hasOwnProperty(property)) {
                mapInstance.put(property, context[property]);
            }
        }
        return org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager
            .executeServiceModule(this.type, module, mapInstance);
    }

    this.executeCode = function (module, context) {
        var mapInstance = new java.util.HashMap();
        for (var property in context) {
            if (context.hasOwnProperty(property)) {
                mapInstance.put(property, context[property]);
            }
        }
        return org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager
            .executeServiceCode(this.type, module, mapInstance);
    }
}

exports.getTypes = function () {
    var json = org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager.getEngineTypesAsJson();
    return JSON.parse(json);
}