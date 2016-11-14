/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java org */
/* eslint-env node, dirigible */

exports.getIndex = function(name) {
	var internalIndex = $.getIndexingService().getIndex(name);
	return new Index(internalIndex);
};

/**
 * Index object
 */
function Index(internalIndex) {
	this.internalIndex = internalIndex;

	this.getInternalObject = function() {
		return this.internalIndex;
	};

	this.add = function(document) {
		if (!document.id) {
			throw new Error("Document should have an 'id' filed");
		}
		
		if (!document.content) {
			throw new Error("Document should have an 'content' filed");
		}
		var internalDocument = this.internalIndex.createDocument(document.id, document.content);
		this.internalIndex.indexDocument(internalDocument);
	};

	this.search = function(term) {
		var internalDocuments = this.internalIndex.search(term);
		var documents = [];
		for (var i=0;i<internalDocuments.size();i++) {
			var internalDocument = internalDocuments.get(i);
			var document = {};
			document.id = internalDocument.get('id');
			document.content = internalDocument.get('content');
			documents.push(document);
		}
		return documents;
	};

	this.clear = function() {
		this.internalIndex.clearIndex();
	};
}