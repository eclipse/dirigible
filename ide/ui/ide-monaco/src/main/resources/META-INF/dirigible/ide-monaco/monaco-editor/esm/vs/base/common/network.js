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
import { URI } from './uri.js';
import * as platform from './platform.js';
export var Schemas;
(function (Schemas) {
    /**
     * A schema that is used for models that exist in memory
     * only and that have no correspondence on a server or such.
     */
    Schemas.inMemory = 'inmemory';
    /**
     * A schema that is used for setting files
     */
    Schemas.vscode = 'vscode';
    /**
     * A schema that is used for internal private files
     */
    Schemas.internal = 'private';
    /**
     * A walk-through document.
     */
    Schemas.walkThrough = 'walkThrough';
    /**
     * An embedded code snippet.
     */
    Schemas.walkThroughSnippet = 'walkThroughSnippet';
    Schemas.http = 'http';
    Schemas.https = 'https';
    Schemas.file = 'file';
    Schemas.mailto = 'mailto';
    Schemas.untitled = 'untitled';
    Schemas.data = 'data';
    Schemas.command = 'command';
    Schemas.vscodeRemote = 'vscode-remote';
    Schemas.vscodeRemoteResource = 'vscode-remote-resource';
    Schemas.userData = 'vscode-userdata';
})(Schemas || (Schemas = {}));
var RemoteAuthoritiesImpl = /** @class */ (function () {
    function RemoteAuthoritiesImpl() {
        this._hosts = Object.create(null);
        this._ports = Object.create(null);
        this._connectionTokens = Object.create(null);
        this._preferredWebSchema = 'http';
        this._delegate = null;
    }
    RemoteAuthoritiesImpl.prototype.setPreferredWebSchema = function (schema) {
        this._preferredWebSchema = schema;
    };
    RemoteAuthoritiesImpl.prototype.rewrite = function (uri) {
        if (this._delegate) {
            return this._delegate(uri);
        }
        var authority = uri.authority;
        var host = this._hosts[authority];
        if (host && host.indexOf(':') !== -1) {
            host = "[" + host + "]";
        }
        var port = this._ports[authority];
        var connectionToken = this._connectionTokens[authority];
        var query = "path=" + encodeURIComponent(uri.path);
        if (typeof connectionToken === 'string') {
            query += "&tkn=" + encodeURIComponent(connectionToken);
        }
        return URI.from({
            scheme: platform.isWeb ? this._preferredWebSchema : Schemas.vscodeRemoteResource,
            authority: host + ":" + port,
            path: "/vscode-remote-resource",
            query: query
        });
    };
    return RemoteAuthoritiesImpl;
}());
export var RemoteAuthorities = new RemoteAuthoritiesImpl();
