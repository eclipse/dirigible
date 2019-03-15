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
var writer = require('indexing/v4/writer');
var searcher = require('indexing/v4/searcher');

writer.add("index3", "myfile1", "apache lucene", new Date(), {"name1":"value1"});
writer.add("index3", "myfile2", "lucene - the search engine", new Date(), {"name2":"value2"});

var found = searcher.search("index3", "engine");

console.log(JSON.stringify(found));

((found !== null) && (found !== undefined) && found.length === 1);