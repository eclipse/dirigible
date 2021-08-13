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
import * as platform from '../../registry/common/platform.js';
import { Emitter } from '../../../base/common/event.js';
export var Extensions = {
    JSONContribution: 'base.contributions.json'
};
function normalizeId(id) {
    if (id.length > 0 && id.charAt(id.length - 1) === '#') {
        return id.substring(0, id.length - 1);
    }
    return id;
}
var JSONContributionRegistry = /** @class */ (function () {
    function JSONContributionRegistry() {
        this._onDidChangeSchema = new Emitter();
        this.schemasById = {};
    }
    JSONContributionRegistry.prototype.registerSchema = function (uri, unresolvedSchemaContent) {
        this.schemasById[normalizeId(uri)] = unresolvedSchemaContent;
        this._onDidChangeSchema.fire(uri);
    };
    JSONContributionRegistry.prototype.notifySchemaChanged = function (uri) {
        this._onDidChangeSchema.fire(uri);
    };
    return JSONContributionRegistry;
}());
var jsonContributionRegistry = new JSONContributionRegistry();
platform.Registry.add(Extensions.JSONContribution, jsonContributionRegistry);
