/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var cmis = require('cms/v4/cmis');

var session = cmis.getSession();

var rootFolder = session.getRootFolder();

var properties = {};
properties[cmis.OBJECT_TYPE_ID] = cmis.OBJECT_TYPE_FOLDER;
properties[cmis.NAME] = 'test1';
var result = rootFolder.createFolder(properties);

result !== null && result !== undefined;
