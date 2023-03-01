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
const bytes = require("io/v4/bytes");

exports.getResource = function(path) {
	const resourceInstance = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.getResource(path);
	const resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.createResource = function(path, content, contentType) {
	const resourceInstance = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.createResource(path, content, contentType);
	const resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.createResourceNative = function(path, content, contentType) {
	const resourceInstance = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.createResourceNative(path, content, contentType);
	const resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.updateResource = function(path, content) {
	const resourceInstance = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.updateResource(path, content);
	const resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.updateResourceNative = function(path, content) {
	const resourceInstance = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.updateResourceNative(path, content);
	const resource = new Resource();
	resource.native = resourceInstance;
	return resource;
};

exports.deleteResource = function(path) {
	org.eclipse.dirigible.api.v3.platform.RepositoryFacade.deleteResource(path);
};

exports.getCollection = function(path) {
	const collectionInstnace = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.getCollection(path);
	const collection = new Collection();
	collection.native = collectionInstnace;
	return collection;
};

exports.createCollection = function(path) {
	const collectionInstnace = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.createCollection(path);
	const collection = new Collection();
	collection.native = collectionInstnace;
	return collection;
};

exports.deleteCollection = function(path) {
	org.eclipse.dirigible.api.v3.platform.RepositoryFacade.deleteCollection(path);
};

exports.find = function(path, pattern) {
	return JSON.parse(org.eclipse.dirigible.api.v3.platform.RepositoryFacade.find(path, pattern));
};

function Resource() {

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
		return this.native.getPath();
	};

	this.getParent = function() {
		const collectionInstance = this.native.getParent();
		const collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.getInformation = function() {
		const informationInstance = this.native.getInformation();
		const information = new EntityInformation();
		information.native = informationInstance;
		return information;
	};

	this.create = function() {
		this.native.create();
	};

	this.delete = function() {
		this.native.delete();
	};

	this.renameTo = function(name) {
		this.native.renameTo(name);
	};

	this.moveTo = function(path) {
		this.native.moveTo(path);
	};

	this.copyTo = function(path) {
		this.native.copyTo(path);
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.isEmpty = function() {
		return this.native.isEmpty();
	};

	this.getText = function() {
		return bytes.byteArrayToText(this.getContent());
	};

	this.getContent = function() {
		let nativeContent = this.native.getContent();
		return bytes.toJavaScriptBytes(nativeContent);
	};

	this.getContentNative = function() {
		return this.native.getContent();
	};

	this.setText = function(text) {
		let content = bytes.textToByteArray(text);
		this.setContent(content);
	};

	this.setContent = function(content) {
		let nativeContent = bytes.toJavaBytes(content);
		this.native.setContent(nativeContent);
	};

	this.setContentNative = function(content) {
		this.native.setContent(content);
	};

	this.isBinary = function() {
		this.native.isBinary();
	};

	this.getContentType = function() {
		this.native.getContentType();
	};
}

function Collection() {

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
		return this.native.getPath();
	};

	this.getParent = function() {
		const collectionInstance = this.native.getParent();
		const collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.getInformation = function() {
		const informationInstance = this.native.getInformation();
		const information = new EntityInformation();
		information.native = informationInstance;
		return information;
	};

	this.create = function() {
		this.native.create();
	};

	this.delete = function() {
		this.native.delete();
	};

	this.renameTo = function(name) {
		this.native.renameTo(name);
	};

	this.moveTo = function(path) {
		this.native.moveTo(path);
	};

	this.copyTo = function(path) {
		this.native.copyTo(path);
	};

	this.exists = function() {
		return this.native.exists();
	};

	this.isEmpty = function() {
		return this.native.isEmpty();
	};

	this.getCollectionsNames = function() {
		return this.native.getCollectionsNames();
	};

	this.createCollection = function(name) {
		const collectionInstance = this.native.createCollection(name);
		const collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.getCollection = function(name) {
		const collectionInstance = this.native.getCollection(name);
		const collection = new Collection();
		collection.native = collectionInstance;
		return collection;
	};

	this.removeCollection = function(name) {
		this.native.removeCollection(name);
	};

	this.getResourcesNames = function() {
		return this.native.getResourcesNames();
	};

	this.getResource = function(name) {
		const resourceInstance = this.native.getResource(name);
		const resource = new Resource();
		resource.native = resourceInstance;
		return resource;
	};

	this.removeResource = function(name) {
		this.native.removeResource(name);
	};

	this.createResource = function(name, content) {
		const resourceInstance = this.native.createResource(name, content);
		const resource = new Resource();
		resource.native = resourceInstance;
		return resource;
	};
}

function EntityInformation() {

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
		return this.native.getPath();
	};

	this.getPermissions = function() {
		return this.native.getPermissions();
	};

	this.getSize = function() {
		return this.native.getSize();
	};

	this.getCreatedBy = function() {
		return this.native.getCreatedBy();
	};

	this.getCreatedAt = function() {
		return this.native.getCreatedAt();
	};

	this.getModifiedBy = function() {
		return this.native.getModifiedBy();
	};

	this.getModifiedAt = function() {
		return this.native.getModifiedAt();
	};
}
