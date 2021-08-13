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
/*---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*--------------------------------------------------------------------------------------------*/
export function stringifyObject(obj, indent, stringifyLiteral) {
    if (obj !== null && typeof obj === 'object') {
        var newIndent = indent + '\t';
        if (Array.isArray(obj)) {
            if (obj.length === 0) {
                return '[]';
            }
            var result = '[\n';
            for (var i = 0; i < obj.length; i++) {
                result += newIndent + stringifyObject(obj[i], newIndent, stringifyLiteral);
                if (i < obj.length - 1) {
                    result += ',';
                }
                result += '\n';
            }
            result += indent + ']';
            return result;
        }
        else {
            var keys = Object.keys(obj);
            if (keys.length === 0) {
                return '{}';
            }
            var result = '{\n';
            for (var i = 0; i < keys.length; i++) {
                var key = keys[i];
                result += newIndent + JSON.stringify(key) + ': ' + stringifyObject(obj[key], newIndent, stringifyLiteral);
                if (i < keys.length - 1) {
                    result += ',';
                }
                result += '\n';
            }
            result += indent + '}';
            return result;
        }
    }
    return stringifyLiteral(obj);
}
