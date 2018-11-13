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

}

function EntityInformation() {
	
}