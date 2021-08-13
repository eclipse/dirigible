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
    brackets: [],
    autoClosingPairs: [],
    surroundingPairs: []
};
export var language = {
    // Set defaultToken to invalid to see what you do not tokenize yet
    // defaultToken: 'invalid',
    keywords: [],
    typeKeywords: [],
    tokenPostfix: '.csp',
    operators: [],
    symbols: /[=><!~?:&|+\-*\/\^%]+/,
    escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,
    tokenizer: {
        root: [
            [/child-src/, 'string.quote'],
            [/connect-src/, 'string.quote'],
            [/default-src/, 'string.quote'],
            [/font-src/, 'string.quote'],
            [/frame-src/, 'string.quote'],
            [/img-src/, 'string.quote'],
            [/manifest-src/, 'string.quote'],
            [/media-src/, 'string.quote'],
            [/object-src/, 'string.quote'],
            [/script-src/, 'string.quote'],
            [/style-src/, 'string.quote'],
            [/worker-src/, 'string.quote'],
            [/base-uri/, 'string.quote'],
            [/plugin-types/, 'string.quote'],
            [/sandbox/, 'string.quote'],
            [/disown-opener/, 'string.quote'],
            [/form-action/, 'string.quote'],
            [/frame-ancestors/, 'string.quote'],
            [/report-uri/, 'string.quote'],
            [/report-to/, 'string.quote'],
            [/upgrade-insecure-requests/, 'string.quote'],
            [/block-all-mixed-content/, 'string.quote'],
            [/require-sri-for/, 'string.quote'],
            [/reflected-xss/, 'string.quote'],
            [/referrer/, 'string.quote'],
            [/policy-uri/, 'string.quote'],
            [/'self'/, 'string.quote'],
            [/'unsafe-inline'/, 'string.quote'],
            [/'unsafe-eval'/, 'string.quote'],
            [/'strict-dynamic'/, 'string.quote'],
            [/'unsafe-hashed-attributes'/, 'string.quote']
        ]
    }
};
