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
const rs = require('http/v4/rs');

const router = rs.service();
let instance = null;

exports.Controller = function (ctr) {
    instance = new ctr();
    router.execute();
}

function registerRequestHandler(
    descriptor,
    path,
    method,
) {
    router[method](
        path,
        (ctx, req, res) => {
            handleRequest(req, res, ctx, descriptor);
        }
    );
}

function handleRequest(req, res, ctx, handler) {
    const body = req.json();
    const maybeResponseBody = handler.apply(instance || {}, [body, ctx, req, res]);
    if (maybeResponseBody) {
        res.json(maybeResponseBody);
    }
}

exports.Get = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "get");
    };
}

exports.Post = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "post");
    };
}

exports.Put = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "put");
    };
}

exports.Patch = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "patch");
    };
}

exports.Delete = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "delete");
    };
}

exports.Head = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "head");
    };
}

exports.Options = function (path) {
    return function (target) {
        return registerRequestHandler(target, path, "options");
    };
}