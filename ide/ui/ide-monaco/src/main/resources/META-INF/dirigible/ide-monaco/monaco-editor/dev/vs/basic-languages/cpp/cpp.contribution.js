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
define(["require", "exports", "../_.contribution"], function (require, exports, __contribution_1) {
    /*---------------------------------------------------------------------------------------------
     *  Copyright (c) Microsoft Corporation. All rights reserved.
     *  Licensed under the MIT License. See License.txt in the project root for license information.
     *--------------------------------------------------------------------------------------------*/
    'use strict';
    Object.defineProperty(exports, "__esModule", { value: true });
    __contribution_1.registerLanguage({
        id: 'c',
        extensions: ['.c', '.h'],
        aliases: ['C', 'c'],
        loader: function () { return new Promise(function (resolve_1, reject_1) { require(['./cpp'], resolve_1, reject_1); }); }
    });
    __contribution_1.registerLanguage({
        id: 'cpp',
        extensions: ['.cpp', '.cc', '.cxx', '.hpp', '.hh', '.hxx'],
        aliases: ['C++', 'Cpp', 'cpp'],
        loader: function () { return new Promise(function (resolve_2, reject_2) { require(['./cpp'], resolve_2, reject_2); }); }
    });
});
