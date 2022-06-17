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
var elasticsearch = require("elasticsearch/client");
var assertTrue = require('utils/assert').assertTrue;

var client = elasticsearch.getClient();

const indexName = "posts-test-index";
const documentId = "1";
const documentSource = JSON.stringify({ user: "test", postDate: "2021-04-20", message: "test index document" });

before = function() {
    if(client.indexes.exists(indexName)) client.indexes.delete(indexName);
}

after = function() {
    client.close();

    return true;
}

testCreateIndex = function() {
    client.indexes.create("posts-test-index");

    return client.indexes.exists("posts-test-index");
}

testIndexDocument = function() {
    client.documents.index(indexName, documentId, documentSource);

    return client.documents.exists(indexName, documentId);
}

testGetIndexedDocument = function() {
    let indexedDocument = client.documents.get(indexName, documentId);

    return indexedDocument.localeCompare(documentSource) === 0;
}

testGetNonIndexedDocument = function() {
    let nonIndexedDocument = client.documents.get(indexName, "nonExistentId");

    return nonIndexedDocument === null;
}

testDeleteDocument = function() {
    client.documents.delete(indexName, documentId)

    return client.documents.exists(indexName, documentId) === false;
}

testDeleteIndex = function() {
    client.indexes.delete(indexName);

    return client.indexes.exists(indexName) === false;
}

before();

assertTrue(testCreateIndex()
    && testIndexDocument()
    && testGetIndexedDocument()
    && testGetNonIndexedDocument()
    && testDeleteDocument()
    && testDeleteIndex()
    && after());