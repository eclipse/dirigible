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
export function polyfill() {
    // Object.assign, for IE11
    if (typeof Object['assign'] != 'function') {
        Object.defineProperty(Object, "assign", {
            value: function assign(destination, sources) {
                'use strict';
                if (destination !== null) {
                    for (var i = 1; i < arguments.length; i++) {
                        var source = arguments[i];
                        if (source) {
                            for (var key in source) {
                                if (Object.prototype.hasOwnProperty.call(source, key)) {
                                    destination[key] = source[key];
                                }
                            }
                        }
                    }
                    ;
                }
                return destination;
            },
            writable: true,
            configurable: true
        });
    }
}
