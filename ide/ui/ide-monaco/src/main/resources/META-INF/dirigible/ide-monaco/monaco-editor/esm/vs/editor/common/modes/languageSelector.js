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
import { match as matchGlobPattern } from '../../../base/common/glob.js';
export function score(selector, candidateUri, candidateLanguage, candidateIsSynchronized) {
    if (Array.isArray(selector)) {
        // array -> take max individual value
        var ret = 0;
        for (var _i = 0, selector_1 = selector; _i < selector_1.length; _i++) {
            var filter = selector_1[_i];
            var value = score(filter, candidateUri, candidateLanguage, candidateIsSynchronized);
            if (value === 10) {
                return value; // already at the highest
            }
            if (value > ret) {
                ret = value;
            }
        }
        return ret;
    }
    else if (typeof selector === 'string') {
        if (!candidateIsSynchronized) {
            return 0;
        }
        // short-hand notion, desugars to
        // 'fooLang' -> { language: 'fooLang'}
        // '*' -> { language: '*' }
        if (selector === '*') {
            return 5;
        }
        else if (selector === candidateLanguage) {
            return 10;
        }
        else {
            return 0;
        }
    }
    else if (selector) {
        // filter -> select accordingly, use defaults for scheme
        var language = selector.language, pattern = selector.pattern, scheme = selector.scheme, hasAccessToAllModels = selector.hasAccessToAllModels;
        if (!candidateIsSynchronized && !hasAccessToAllModels) {
            return 0;
        }
        var ret = 0;
        if (scheme) {
            if (scheme === candidateUri.scheme) {
                ret = 10;
            }
            else if (scheme === '*') {
                ret = 5;
            }
            else {
                return 0;
            }
        }
        if (language) {
            if (language === candidateLanguage) {
                ret = 10;
            }
            else if (language === '*') {
                ret = Math.max(ret, 5);
            }
            else {
                return 0;
            }
        }
        if (pattern) {
            if (pattern === candidateUri.fsPath || matchGlobPattern(pattern, candidateUri.fsPath)) {
                ret = 10;
            }
            else {
                return 0;
            }
        }
        return ret;
    }
    else {
        return 0;
    }
}
