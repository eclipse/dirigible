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
'use strict';
import { conf as tsConf, language as tsLanguage } from '../typescript/typescript.js';
// Allow for running under nodejs/requirejs in tests
var _monaco = (typeof monaco === 'undefined' ? self.monaco : monaco);
export var conf = tsConf;
export var language = {
    // Set defaultToken to invalid to see what you do not tokenize yet
    defaultToken: 'invalid',
    tokenPostfix: '.js',
    keywords: [
        'break', 'case', 'catch', 'class', 'continue', 'const',
        'constructor', 'debugger', 'default', 'delete', 'do', 'else',
        'export', 'extends', 'false', 'finally', 'for', 'from', 'function',
        'get', 'if', 'import', 'in', 'instanceof', 'let', 'new', 'null',
        'return', 'set', 'super', 'switch', 'symbol', 'this', 'throw', 'true',
        'try', 'typeof', 'undefined', 'var', 'void', 'while', 'with', 'yield',
        'async', 'await', 'of'
    ],
    typeKeywords: [],
    operators: tsLanguage.operators,
    symbols: tsLanguage.symbols,
    escapes: tsLanguage.escapes,
    digits: tsLanguage.digits,
    octaldigits: tsLanguage.octaldigits,
    binarydigits: tsLanguage.binarydigits,
    hexdigits: tsLanguage.hexdigits,
    regexpctl: tsLanguage.regexpctl,
    regexpesc: tsLanguage.regexpesc,
    tokenizer: tsLanguage.tokenizer,
};
