/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var cmis = require('cms/v4/cmis');
var streams = require('io/v4/streams');

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var inputStream = streams.createByteArrayInputStream([101,102,103,104]);
var contentStream = session.getObjectFactory().createContentStream('test1.txt', 4, 'plain/text', inputStream);
var properties = {};
properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_DOCUMENT;
properties[cmis.NAME] = 'test1.txt';

var result = rootFolder.createDocument(properties, contentStream, cmis.VERSIONING_STATE_MAJOR);

result !== null && result !== undefined;
