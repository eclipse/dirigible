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
// ------ internal util
export var _util;
(function (_util) {
    _util.serviceIds = new Map();
    _util.DI_TARGET = '$di$target';
    _util.DI_DEPENDENCIES = '$di$dependencies';
    function getServiceDependencies(ctor) {
        return ctor[_util.DI_DEPENDENCIES] || [];
    }
    _util.getServiceDependencies = getServiceDependencies;
})(_util || (_util = {}));
export var IInstantiationService = createDecorator('instantiationService');
function storeServiceDependency(id, target, index, optional) {
    if (target[_util.DI_TARGET] === target) {
        target[_util.DI_DEPENDENCIES].push({ id: id, index: index, optional: optional });
    }
    else {
        target[_util.DI_DEPENDENCIES] = [{ id: id, index: index, optional: optional }];
        target[_util.DI_TARGET] = target;
    }
}
/**
 * A *only* valid way to create a {{ServiceIdentifier}}.
 */
export function createDecorator(serviceId) {
    if (_util.serviceIds.has(serviceId)) {
        return _util.serviceIds.get(serviceId);
    }
    var id = function (target, key, index) {
        if (arguments.length !== 3) {
            throw new Error('@IServiceName-decorator can only be used to decorate a parameter');
        }
        storeServiceDependency(id, target, index, false);
    };
    id.toString = function () { return serviceId; };
    _util.serviceIds.set(serviceId, id);
    return id;
}
/**
 * Mark a service dependency as optional.
 */
export function optional(serviceIdentifier) {
    return function (target, key, index) {
        if (arguments.length !== 3) {
            throw new Error('@optional-decorator can only be used to decorate a parameter');
        }
        storeServiceDependency(serviceIdentifier, target, index, true);
    };
}
