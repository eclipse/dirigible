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
define(["require", "exports"], function (require, exports) {
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    exports.conf = {
        comments: {
            lineComment: ';',
            blockComment: ['#|', '|#'],
        },
        brackets: [['(', ')'], ['{', '}'], ['[', ']']],
        autoClosingPairs: [
            { open: '{', close: '}' },
            { open: '[', close: ']' },
            { open: '(', close: ')' },
            { open: '"', close: '"' },
        ],
        surroundingPairs: [
            { open: '{', close: '}' },
            { open: '[', close: ']' },
            { open: '(', close: ')' },
            { open: '"', close: '"' },
        ],
    };
    exports.language = {
        defaultToken: '',
        ignoreCase: true,
        tokenPostfix: '.scheme',
        brackets: [
            { open: '(', close: ')', token: 'delimiter.parenthesis' },
            { open: '{', close: '}', token: 'delimiter.curly' },
            { open: '[', close: ']', token: 'delimiter.square' },
        ],
        keywords: [
            'case',
            'do',
            'let',
            'loop',
            'if',
            'else',
            'when',
            'cons',
            'car',
            'cdr',
            'cond',
            'lambda',
            'lambda*',
            'syntax-rules',
            'format',
            'set!',
            'quote',
            'eval',
            'append',
            'list',
            'list?',
            'member?',
            'load',
        ],
        constants: ['#t', '#f'],
        operators: ['eq?', 'eqv?', 'equal?', 'and', 'or', 'not', 'null?'],
        tokenizer: {
            root: [
                [/#[xXoObB][0-9a-fA-F]+/, 'number.hex'],
                [/[+-]?\d+(?:(?:\.\d*)?(?:[eE][+-]?\d+)?)?/, 'number.float'],
                [
                    /(?:\b(?:(define|define-syntax|define-macro))\b)(\s+)((?:\w|\-|\!|\?)*)/,
                    ['keyword', 'white', 'variable'],
                ],
                { include: '@whitespace' },
                { include: '@strings' },
                [
                    /[a-zA-Z_#][a-zA-Z0-9_\-\?\!\*]*/,
                    {
                        cases: {
                            '@keywords': 'keyword',
                            '@constants': 'constant',
                            '@operators': 'operators',
                            '@default': 'identifier',
                        },
                    },
                ],
            ],
            comment: [
                [/[^\|#]+/, 'comment'],
                [/#\|/, 'comment', '@push'],
                [/\|#/, 'comment', '@pop'],
                [/[\|#]/, 'comment'],
            ],
            whitespace: [
                [/[ \t\r\n]+/, 'white'],
                [/#\|/, 'comment', '@comment'],
                [/;.*$/, 'comment'],
            ],
            strings: [
                [/"$/, 'string', '@popall'],
                [/"(?=.)/, 'string', '@multiLineString'],
            ],
            multiLineString: [
                [/[^\\"]+$/, 'string', '@popall'],
                [/[^\\"]+/, 'string'],
                [/\\./, 'string.escape'],
                [/"/, 'string', '@popall'],
                [/\\$/, 'string']
            ],
        },
    };
});
