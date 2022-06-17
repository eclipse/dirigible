/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var cmis = require('cms/v4/cmis');
var streams = require('io/v4/streams');
var assertTrue = require('utils/assert').assertTrue;

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var inputStream = streams.createByteArrayInputStream([101,102,103,104]);
var contentStream = session.getObjectFactory().createContentStream('test1.txt', 4, 'plain/text', inputStream);
var properties = {};
properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
properties[cmis.NAME] = 'test1.txt';

var result = rootFolder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);

assertTrue(result !== null && result !== undefined);
