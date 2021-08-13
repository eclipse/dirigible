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
    id: 'xml',
    extensions: ['.xml', '.dtd', '.ascx', '.csproj', '.config', '.wxi', '.wxl', '.wxs', '.xaml', '.svg', '.svgz', '.opf', '.xsl'],
    firstLine: '(\\<\\?xml.*)|(\\<svg)|(\\<\\!doctype\\s+svg)',
    aliases: ['XML', 'xml'],
    mimetypes: ['text/xml', 'application/xml', 'application/xaml+xml', 'application/xml-dtd'],
    loader: function () { return import('./xml.js'); }
});
