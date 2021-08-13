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
var InternalEditorAction = /** @class */ (function () {
    function InternalEditorAction(id, label, alias, precondition, run, contextKeyService) {
        this.id = id;
        this.label = label;
        this.alias = alias;
        this._precondition = precondition;
        this._run = run;
        this._contextKeyService = contextKeyService;
    }
    InternalEditorAction.prototype.isSupported = function () {
        return this._contextKeyService.contextMatchesRules(this._precondition);
    };
    InternalEditorAction.prototype.run = function () {
        if (!this.isSupported()) {
            return Promise.resolve(undefined);
        }
        var r = this._run();
        return r ? r : Promise.resolve(undefined);
    };
    return InternalEditorAction;
}());
export { InternalEditorAction };
