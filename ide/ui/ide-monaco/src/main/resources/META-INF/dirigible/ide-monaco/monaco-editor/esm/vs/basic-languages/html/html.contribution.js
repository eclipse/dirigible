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
import { registerLanguage } from '../_.contribution.js';
registerLanguage({
    id: 'html',
    extensions: ['.html', '.htm', '.shtml', '.xhtml', '.mdoc', '.jsp', '.asp', '.aspx', '.jshtm'],
    aliases: ['HTML', 'htm', 'html', 'xhtml'],
    mimetypes: ['text/html', 'text/x-jshtm', 'text/template', 'text/ng-template'],
    loader: function () { return import('./html.js'); }
});
