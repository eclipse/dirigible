/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var writer = require('indexing/v4/writer');
var searcher = require('indexing/v4/searcher');
var assertTrue = require('utils/assert').assertTrue;

writer.add("index2", "myfile1", "apache lucene", new Date(123));
writer.add("index2", "myfile2", "lucene - the search engine", new Date(234), {"name2":"value2"});
writer.add("index2", "myfile3", "search engine", new Date(345), {"name2":"value2"});

var found = searcher.between("index2", new Date(124), new Date(344));

console.log(JSON.stringify(found));

assertTrue((found !== null) && (found !== undefined) && found.length === 1);