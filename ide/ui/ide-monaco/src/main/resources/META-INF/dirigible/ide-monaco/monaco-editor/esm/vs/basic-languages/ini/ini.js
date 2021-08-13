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
export var conf = {
    comments: {
        lineComment: '#'
    },
    brackets: [
        ['{', '}'],
        ['[', ']'],
        ['(', ')'],
    ],
    autoClosingPairs: [
        { open: '{', close: '}' },
        { open: '[', close: ']' },
        { open: '(', close: ')' },
        { open: '"', close: '"' },
        { open: '\'', close: '\'' },
    ],
    surroundingPairs: [
        { open: '{', close: '}' },
        { open: '[', close: ']' },
        { open: '(', close: ')' },
        { open: '"', close: '"' },
        { open: '\'', close: '\'' },
    ]
};
export var language = {
    defaultToken: '',
    tokenPostfix: '.ini',
    // we include these common regular expressions
    escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
    // The main tokenizer for our languages
    tokenizer: {
        root: [
            // sections
            [/^\[[^\]]*\]/, 'metatag'],
            // keys
            [/(^\w+)(\s*)(\=)/, ['key', '', 'delimiter']],
            // whitespace
            { include: '@whitespace' },
            // numbers
            [/\d+/, 'number'],
            // strings: recover on non-terminated strings
            [/"([^"\\]|\\.)*$/, 'string.invalid'],
            [/'([^'\\]|\\.)*$/, 'string.invalid'],
            [/"/, 'string', '@string."'],
            [/'/, 'string', '@string.\''],
        ],
        whitespace: [
            [/[ \t\r\n]+/, ''],
            [/^\s*[#;].*$/, 'comment'],
        ],
        string: [
            [/[^\\"']+/, 'string'],
            [/@escapes/, 'string.escape'],
            [/\\./, 'string.escape.invalid'],
            [/["']/, {
                    cases: {
                        '$#==$S2': { token: 'string', next: '@pop' },
                        '@default': 'string'
                    }
                }]
        ],
    },
};
