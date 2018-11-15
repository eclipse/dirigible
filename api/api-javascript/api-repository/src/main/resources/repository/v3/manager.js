/*
 * Copyright (c) 2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var java = require('core/v3/java');

exports.getResource = function(path) {
	var resourceInstance = java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'getResource', [path], true);
	var resource = new Resource();
	resource.uuid = resourceInstance.uuid;
	return resource;
};

exports.createResource = function(path, content, contentType) {
	var resourceInstance = java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'createResource', [path, content, contentType], true);
	var resource = new Resource();
	resource.uuid = resourceInstance.uuid;
	return resource;
};

exports.updateResource = function(path, content) {
	var resourceInstance = java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'updateResource', [path, content], true);
	var resource = new Resource();
	resource.uuid = resourceInstance.uuid;
	return resource;	
};

exports.deleteResource = function(path) {
	java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'deleteResource', [path]);
};

exports.getCollection = function(path) {
	var collectionInstnace = java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'getCollection', [path], true);
	var collection = new Collection();
	collection.uuid = collectionInstnace.uuid;
	return collection;
};

exports.createCollection = function(path) {
	var collectionInstnace = java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'createCollection', [path], true);
	var collection = new Collection();
	collection.uuid = collectionInstnace.uuid;
	return collection;
};

exports.deleteCollection = function(path) {
	java.call('org.eclipse.dirigible.api.v3.repository.RepositoryFacade', 'deleteCollection', [path]);
};

function Resource() {

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};

	this.getPath = function() {
		return java.invoke(this.uuid, 'getPath', []);
	};

	this.getParent = function() {
		var collectionInstance = java.invoke(this.uuid, 'getParent', [], true);
		var collection = new Collection();
		collection.uuid = collectionInstance.uuid;
		return collection;
	};

	this.getInformation = function() {
		var informationInstance = java.invoke(this.uuid, 'getInformation', [], true);
		var information = new EntityInformation();
		information.uuid = informationInstance.uuid;
		return information;
	};

	this.create = function() {
		java.invoke(this.uuid, 'create', []);
	};

	this.delete = function() {
		java.invoke(this.uuid, 'delete', []);
	};

	this.renameTo = function(name) {
		java.invoke(this.uuid, 'renameTo', [name]);
	};

	this.moveTo = function(path) {
		java.invoke(this.uuid, 'moveTo', [path]);
	};

	this.copyTo = function(path) {
		java.invoke(this.uuid, 'copyTo', [path]);
	};

	this.exists = function() {
		return java.invoke(this.uuid, 'exists', []);
	};

	this.isEmpty = function() {
		return java.invoke(this.uuid, 'isEmpty', []);
	};

	this.getContent = function() {
		return java.invoke(this.uuid, 'getContent', []);
	};

	this.setContent = function(content) {
		java.invoke(this.uuid, 'setContent', [content]);
	};

	this.isBinary = function() {
		return java.invoke(this.uuid, 'isBinary', []);
	};

	this.getContentType = function() {
		return java.invoke(this.uuid, 'getContentType', []);
	};
}

function Collection() {

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};

	this.getPath = function() {
		return java.invoke(this.uuid, 'getPath', []);
	};

	this.getParent = function() {
		var collectionInstance = java.invoke(this.uuid, 'getParent', [], true);
		var collection = new Collection();
		collection.uuid = collectionInstance.uuid;
		return collection;
	};

	this.getInformation = function() {
		var informationInstance = java.invoke(this.uuid, 'getInformation', [], true);
		var information = new EntityInformation();
		information.uuid = informationInstance.uuid;
		return information;
	};

	this.create = function() {
		java.invoke(this.uuid, 'create', []);
	};

	this.delete = function() {
		java.invoke(this.uuid, 'delete', []);
	};

	this.renameTo = function(name) {
		java.invoke(this.uuid, 'renameTo', [name]);
	};

	this.moveTo = function(path) {
		java.invoke(this.uuid, 'moveTo', [path]);
	};

	this.copyTo = function(path) {
		java.invoke(this.uuid, 'copyTo', [path]);
	};

	this.exists = function() {
		return java.invoke(this.uuid, 'exists', []);
	};

	this.isEmpty = function() {
		return java.invoke(this.uuid, 'isEmpty', []);
	};

	this.getCollectionsNames = function() {
		return java.invoke(this.uuid, 'getCollectionsNames', []);
	};

	this.createCollection = function(name) {
		var collectionInstance = java.invoke(this.uuid, 'createCollection', [name], true);
		var collection = new Collection();
		collection.uuid = collectionInstance.uuid;
		return collection;
	}

	this.getCollection = function(name) {
		var collectionInstance = java.invoke(this.uuid, 'getCollection', [name], true);
		var collection = new Collection();
		collection.uuid = collectionInstance.uuid;
		return collection;
	}

	this.removeCollection = function(name) {
		java.invoke(this.uuid, 'removeCollection', [name]);
	};

	this.getResourcesNames = function() {
		return java.invoke(this.uuid, 'getResourcesNames', []);
	};

	this.getResource = function(name) {
		var resourceInstance = java.invoke(this.uuid, 'getResource', [name], true);
		var resource = new Resource();
		resource.uuid = resourceInstance.uuid;
		return resource;
	};

	this.removeResource = function(name) {
		java.invoke(this.uuid, 'removeResource', [name]);
	};

	this.createResource = function(name, content) {
		var resourceInstance = java.invoke(this.uuid, 'createResource', [name, content], true);
		var resource = new Resource();
		resource.uuid = resourceInstance.uuid;
		return resource;
	};
}

function EntityInformation() {

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};

	this.getPath = function() {
		return java.invoke(this.uuid, 'getPath', []);
	};

	this.getPermissions = function() {
		return java.invoke(this.uuid, 'getPermissions', []);
	};

	this.getSize = function() {
		return java.invoke(this.uuid, 'getSize', []);
	};

	this.getCreatedBy = function() {
		return java.invoke(this.uuid, 'getCreatedBy', []);
	};

	this.getCreatedAt = function() {
		return java.invoke(this.uuid, 'getCreatedAt', []);
	};

	this.getModifiedBy = function() {
		return java.invoke(this.uuid, 'getModifiedBy', []);
	};

	this.getModifiedAt = function() {
		return java.invoke(this.uuid, 'getModifiedAt', []);
	};
}