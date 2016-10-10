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

var streams = require("io/streams");

/**
 * Create a collection by the given path e.g. /myRoot/myCollection
 */
exports.createCollection = function(path) {
	$.getRepository().createCollection(path);
};

/**
 * Create a resource by the given path e.g. /myRoot/myCollection/myResource.txt,
 * content (a javascript bytes array object), isBinary - a flag indicating the type of the content and
 * the exact contentType following the corresponding RFC
 */
exports.createResource = function(path, content, isBinary, contentType) {
	$.getRepository().createResource(path, streams.toJavaBytes(content), isBinary ? isBinary : false, contentType ? contentType : "plain/text");
};

/**
 * Create a text resource by the given path e.g. /myRoot/myCollection/myResource and text.
 * Optionally you can provide the exact contentType of the provided text
 */
exports.createTextResource = function(path, text, contentType) {
    var content = new java.lang.String(text).getBytes();
	$.getRepository().createResource(path, content, false, contentType ? contentType : "plain/text");
};

/**
 * Get a Collection by the given path e.g. /myRoot/myCollection
 */
exports.getCollection = function(path) {
	var internalCollection = $.getRepository().getCollection(path);
	return new Collection(internalCollection);
};

/**
 * Gets a Resource by the given path e.g. /myRoot/myCollection/myResource.txt
 */
exports.getResource = function(path) {
	var internalResource = $.getRepository().getResource(path);
	return new Resource(internalResource);
};

/**
 * Gets the root Collection
 */
exports.getRoot = function(path) {
	var internalCollection = $.getRepository().getRoot();
	return new Collection(internalCollection);
};

/**
 * Check the existence of a Collection by the given path e.g. /myRoot/myCollection
 */
exports.hasCollection = function(path) {
	return $.getRepository().hasCollection(path);
};

/**
 * Check the existence of a Resource by the given path e.g. /myRoot/myCollection/myResource.txt
 */
exports.hasResource = function(path) {
	return $.getRepository().hasResource(path);
};

/**Removes a Collection by the given path e.g. /myRoot/myCollection
 */
exports.removeCollection = function(path) {
	return $.getRepository().removeCollection(path);
};

/**
 * Remove a Resource by the given path e.g. /myRoot/myCollection/myResource.txt
 */
exports.removeResource = function(path) {
	return $.getRepository().removeResource(path);
};

/**
 * Collection object
 */
function Collection(internalCollection) {
	this.internalCollection = internalCollection;
	this.getInternalObject = collectionGetInternalObject;
	this.create = collectionCreate;
	this.delete = collectionDelete;
	this.exists = collectionExists;
	this.getName = collectionGetName;
	this.getParent = collectionGetParent;
	this.getPath = collectionGetPath;
	this.createCollection = collectionCreateCollection;
	this.createResource = collectionCreateResource;
//	this.getChildren = collectionGetChildren;
	this.getCollection = collectionGetCollection;
	this.getCollections = collectionGetCollections;
	this.getCollectionNames = collectionGetCollectionNames;
	this.getResource = collectionGetResource;
	this.getResources = collectionGetResources;
	this.getResourceNames = collectionGetResourceNames;
	this.isEmpty = collectionIsEmpty;
	this.removeCollection = collectionRemoveCollection;
	this.removeResource = collectionRemoveResource;
	this.renameTo = collectionRenameTo;
	this.moveTo = collectionMoveTo;
	this.copyTo = collectionCopyTo;
}

function collectionGetInternalObject() {
	return this.internalCollection;
}

function collectionCreate() {
	this.internalCollection.create();
}

function collectionDelete() {
	this.internalCollection.delete();
}

function collectionExists() {
	return this.internalCollection.exists();
}

function collectionGetName() {
	return this.internalCollection.getName();
}

function collectionGetParent() {
	var internalParent = this.internalCollection.getParent();
	return new Collection(internalParent);
}

function collectionGetPath() {
	return this.internalCollection.getPath();
}

function collectionCreateCollection(name) {
	return new Collection(this.internalCollection.createCollection(name));
}

function collectionCreateResource(name, content, isBinary, contentType) {
	return new Resource(this.internalCollection.createResource(name, streams.toJavaBytes(content), isBinary ? isBinary : false, contentType ? contentType : "plain/text"));
}

//function collectionGetChildren() {
//	return this.internalCollection.getChildren();
//}

function collectionGetCollection(name) {
	return new Collection(this.internalCollection.getCollection(name));
}

function collectionGetCollection(name) {
	return new Collection(this.internalCollection.getCollection(name));
}

function collectionGetCollections() {
	var collections = [];
	var internalCollections = this.internalCollection.getCollections();
	for (var i = 0; i< internalCollections.size(); i++) {
		collections.push(new Collection(internalCollections.get(i)));
	}
	return collections;
}

function collectionGetCollectionNames() {
	var collectionNames = [];
	var internalCollectionNames = this.internalCollection.getCollectionNames();
	for (var i = 0; i< internalCollectionNames.size(); i++) {
		collectionNames.push(internalCollectionNames.get(i));
	}
	return collectionNames;
}

function collectionGetResource(name) {
	return new Collection(this.internalCollection.getResource(name));
}

function collectionGetResources() {
	var resources = [];
	var internalResources = this.internalCollection.getResources();
	for (var i = 0; i< internalResources.size(); i++) {
		resources.push(new Collection(internalResources.get(i)));
	}
	return resources;
}

function collectionGetResourceNames() {
	var resourceNames = [];
	var internalResourceNames = this.internalResource.getResourceNames();
	for (var i = 0; i< internalResourceNames.size(); i++) {
		resourceNames.push(internalResourceNames.get(i));
	}
	return resourceNames;
}

function collectionIsEmpty() {
	return this.internalCollection.isEmpty();
}

function collectionRemoveCollection(name) {
	return this.internalCollection.removeCollection(name);
}

function collectionRemoveResource(name) {
	return this.internalCollection.removeResoruce(name);
}

function collectionRenameTo(name) {
	return this.internalCollection.renameTo(name);
}

function collectionMoveTo(path) {
	return this.internalCollection.moveTo(path);
}

function collectionCopyTo(path) {
	return this.internalCollection.copyTo(path);
}




/**
 * Resource object
 */
function Resource(internalResource) {
	this.internalResource = internalResource;
	this.getInternalObject = resourceGetInternalObject;
	this.create = resourceCreate;
	this.delete = resourceDelete;
	this.exists = resourceExists;
	this.getName = resourceGetName;
	this.getParent = resourceGetParent;
	this.getPath = resourceGetPath;
	this.getContent = resourceGetContent;
	this.getTextContent = resourceGetTextContent;
	this.getContentType = resourceGetContentType;
	this.isBinary = resourceIsBinary;
	this.isEmpty = resourceIsEmpty;
	this.renameTo = resourceRenameTo;
	this.moveTo = resourceMoveTo;
	this.copyTo = resourceCopyTo;
	this.setContent = resourceSetContent;
	this.setTextContent = resourceSetTextContent;
}

function resourceGetInternalObject() {
	return this.internalResource;
}

function resourceCreate() {
	this.internalResource.create();
}

function resourceDelete() {
	this.internalResource.delete();
}

function resourceExists() {
	return this.internalResource.exists();
}

function resourceGetName() {
	return this.internalResource.getName();
}

function resourceGetParent() {
	return new Collection(this.internalResource.getParent());
}

function resourceGetPath() {
	return this.internalResource.getPath();
}

function resourceGetContent() {
	return streams.toJavaScriptBytes(this.internalResource.getContent());
}

function resourceGetTextContent() {
	return new java.lang.String(this.internalResource.getContent()) + "";
}

function resourceGetContentType() {
	return this.internalResource.getContentType();
}

function resourceIsBinary() {
	return this.internalResource.isBinary();
}

function resourceIsEmpty() {
	return this.internalResource.isEmpty();
}

function resourceRenameTo(name) {
	return this.internalResource.renameTo(name);
}

function resourceMoveTo(path) {
	return this.internalResource.moveTo(path);
}

function resourceCopyTo(path) {
	return this.internalResource.copyTo(path);
}

function resourceSetContent(content) {
	this.internalResource.setContent(streams.toJavaBytes(content));
}

function resourceSetTextContent(text) {
	var content = new java.lang.String(text).getBytes();
	this.internalResource.setTextContent(streams.toJavaBytes(content));
}
