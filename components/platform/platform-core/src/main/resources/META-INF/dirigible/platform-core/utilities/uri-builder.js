/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
const UriBuilder = function UriBuilder() {
    let pathSegments = [];
    this.path = function (_pathSegments) {
        if (!Array.isArray(_pathSegments))
            _pathSegments = [_pathSegments];
        _pathSegments = _pathSegments.filter(function (segment) {
            return segment;
        }).map(function (segment) {
            if (segment.length) {
                if (segment.charAt(segment.length - 1) === '/')
                    segment = segment.substring(0, segment.length - 2);
                segment = encodeURIComponent(segment);
            }
            return segment;
        });
        pathSegments = pathSegments.concat(_pathSegments);
        return this;
    };
    this.build = function (isBasePath = true) {
        if (isBasePath) return '/' + pathSegments.join('/');
        return pathSegments.join('/');
    };
    return this;
};