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

	this.getInternalObject = function() {
		return this.internalCollection;
	};

	this.create = function() {
		this.internalCollection.create();
	};

	this.delete = function() {
		this.internalCollection.delete();
	};

	this.exists = function() {
		return this.internalCollection.exists();
	};

	this.getName = function() {
		return this.internalCollection.getName();
	};

	this.getParent = function() {
		var internalParent = this.internalCollection.getParent();
		return new Collection(internalParent);
	};

	this.getPath = function() {
		return this.internalCollection.getPath();
	};

	this.createCollection = function(name) {
		return new Collection(this.internalCollection.createCollection(name));
	};

	this.createResource = function(name, content, isBinary, contentType) {
		return new Resource(this.internalCollection.createResource(name, streams.toJavaBytes(content), isBinary ? isBinary : false, contentType ? contentType : "plain/text"));
	};

//	this.getChildren = collectionGetChildren
//function collectionGetChildren() {
//	return this.internalCollection.getChildren();
//}

	this.getCollection = function(name) {
		return new Collection(this.internalCollection.getCollection(name));
	};

	this.getCollections = function() {
		var collections = [];
		var internalCollections = this.internalCollection.getCollections();
		for (var i = 0; i< internalCollections.size(); i++) {
			collections.push(new Collection(internalCollections.get(i)));
		}
		return collections;
	};

	this.getCollectionNames = function() {
		var collectionNames = [];
		var internalCollectionNames = this.internalCollection.getCollectionsNames();
		for (var i = 0; i< internalCollectionNames.size(); i++) {
			collectionNames.push(internalCollectionNames.get(i));
		}
		return collectionNames;
	};

	this.getResource = function(name) {
		return new Collection(this.internalCollection.getResource(name));
	};

	this.getResources = function() {
		var resources = [];
		var internalResources = this.internalCollection.getResources();
		for (var i = 0; i< internalResources.size(); i++) {
			resources.push(new Collection(internalResources.get(i)));
		}
		return resources;
	};

	this.getResourceNames = function() {
		var resourceNames = [];
		var internalResourceNames = this.internalCollection.getResourcesNames();
		for (var i = 0; i< internalResourceNames.size(); i++) {
			resourceNames.push(internalResourceNames.get(i));
		}
		return resourceNames;
	};

	this.isEmpty = function() {
		return this.internalCollection.isEmpty();
	};

	this.removeCollection = function(name) {
		return this.internalCollection.removeCollection(name);
	};

	this.removeResource = function(name) {
		return this.internalCollection.removeResoruce(name);
	};

	this.renameTo = function(name) {
		return this.internalCollection.renameTo(name);
	};

	this.moveTo = function(path) {
		return this.internalCollection.moveTo(path);
	};

	this.copyTo = function(path) {
		return this.internalCollection.copyTo(path);
	};
}

/**
 * Resource object
 */
function Resource(internalResource) {
	this.internalResource = internalResource;

	this.getInternalObject = function() {
		return this.internalResource;
	};

	this.create = function() {
		this.internalResource.create();
	};

	this.delete = function() {
		this.internalResource.delete();
	};

	this.exists = function() {
		return this.internalResource.exists();
	};

	this.getName = function() {
		return this.internalResource.getName();
	};

	this.getParent = function() {
		return new Collection(this.internalResource.getParent());
	};

	this.getPath = function() {
		return this.internalResource.getPath();
	};

	this.getContent = function() {
		return streams.toJavaScriptBytes(this.internalResource.getContent());
	};

	this.getTextContent = function() {
		return new java.lang.String(this.internalResource.getContent()) + "";
	};

	this.getContentType = function() {
		return this.internalResource.getContentType();
	};

	this.isBinary = function() {
		return this.internalResource.isBinary();
	};

	this.isEmpty = function() {
		return this.internalResource.isEmpty();
	};

	this.renameTo = function(name) {
		return this.internalResource.renameTo(name);
	};

	this.moveTo = function(path) {
		return this.internalResource.moveTo(path);
	};

	this.copyTo = function(path) {
		return this.internalResource.copyTo(path);
	};

	this.setContent = function(content) {
		this.internalResource.setContent(streams.toJavaBytes(content));
	};

	this.setTextContent = function(text) {
		var content = new java.lang.String(text).getBytes();
		this.internalResource.setTextContent(streams.toJavaBytes(content));
	};
}
