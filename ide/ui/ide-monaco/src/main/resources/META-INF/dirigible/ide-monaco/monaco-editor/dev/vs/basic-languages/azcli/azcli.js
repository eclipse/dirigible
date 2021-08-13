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
            lineComment: '#',
        }
    };
    exports.language = {
        defaultToken: 'keyword',
        ignoreCase: true,
        tokenPostfix: '.azcli',
        str: /[^#\s]/,
        tokenizer: {
            root: [
                { include: '@comment' },
                [/\s-+@str*\s*/, {
                        cases: {
                            '@eos': { token: 'key.identifier', next: '@popall' },
                            '@default': { token: 'key.identifier', next: '@type' }
                        }
                    }],
                [/^-+@str*\s*/, {
                        cases: {
                            '@eos': { token: 'key.identifier', next: '@popall' },
                            '@default': { token: 'key.identifier', next: '@type' }
                        }
                    }]
            ],
            type: [
                { include: '@comment' },
                [/-+@str*\s*/, {
                        cases: {
                            '@eos': { token: 'key.identifier', next: '@popall' },
                            '@default': 'key.identifier'
                        }
                    }],
                [/@str+\s*/, {
                        cases: {
                            '@eos': { token: 'string', next: '@popall' },
                            '@default': 'string'
                        }
                    }]
            ],
            comment: [
                [/#.*$/, {
                        cases: {
                            '@eos': { token: 'comment', next: '@popall' }
                        }
                    }]
            ]
        }
    };
});
