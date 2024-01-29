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
import {bytes} from "@dirigible/io";
const RepositoryFacade = Java.type("org.eclipse.dirigible.components.api.platform.RepositoryFacade");

export function getResource(path) {
	const resourceInstance = RepositoryFacade.getResource(path);
	return new Resource(resourceInstance);
};

export function createResource(path, content, contentType) {
	const resourceInstance = RepositoryFacade.createResource(path, content, contentType);
	return new Resource(resourceInstance);
};

export function createResourceNative(path, content, contentType) {
	const resourceInstance = RepositoryFacade.createResourceNative(path, content, contentType);
	return new Resource(resourceInstance);
};

export function updateResource(path, content) {
	const resourceInstance = RepositoryFacade.updateResource(path, content);
	return new Resource(resourceInstance);
};

export function updateResourceNative(path, content) {
	const resourceInstance = RepositoryFacade.updateResourceNative(path, content);
	return new Resource(resourceInstance);
};

export function deleteResource(path) {
	RepositoryFacade.deleteResource(path);
};

export function getCollection(path) {
	const collectionInstance = RepositoryFacade.getCollection(path);
	return new Collection(collectionInstance);
};

export function createCollection(path) {
	const collectionInstance = RepositoryFacade.createCollection(path);
	return new Collection(collectionInstance);
};

export function deleteCollection(path) {
	RepositoryFacade.deleteCollection(path);
};

export function find(path, pattern) {
	return JSON.parse(RepositoryFacade.find(path, pattern));
};

class Resource {

	constructor(private native) { }

	getName() {
		return this.native.getName();
	};

	getPath() {
		return this.native.getPath();
	};

	getParent() {
		const collectionInstance = this.native.getParent();
		return new Collection(collectionInstance);
	};

	getInformation() {
		const informationInstance = this.native.getInformation();
		return new EntityInformation(informationInstance);
	};

	create() {
		this.native.create();
	};

	delete() {
		this.native.delete();
	};

	renameTo(name) {
		this.native.renameTo(name);
	};

	moveTo(path) {
		this.native.moveTo(path);
	};

	copyTo(path) {
		this.native.copyTo(path);
	};

	exists() {
		return this.native.exists();
	};

	isEmpty() {
		return this.native.isEmpty();
	};

	getText() {
		return bytes.byteArrayToText(this.getContent());
	};

	getContent() {
		let nativeContent = this.native.getContent();
		return bytes.toJavaScriptBytes(nativeContent);
	};

	getContentNative() {
		return this.native.getContent();
	};

	setText(text) {
		let content = bytes.textToByteArray(text);
		this.setContent(content);
	};

	setContent(content) {
		let nativeContent = bytes.toJavaBytes(content);
		this.native.setContent(nativeContent);
	};

	setContentNative(content) {
		this.native.setContent(content);
	};

	isBinary() {
		this.native.isBinary();
	};

	getContentType() {
		this.native.getContentType();
	};
}

class Collection {

	constructor(private native) { }

	getName() {
		return this.native.getName();
	};

	getPath() {
		return this.native.getPath();
	};

	getParent() {
		const collectionInstance = this.native.getParent();
		return new Collection(collectionInstance);
	};

	getInformation() {
		const informationInstance = this.native.getInformation();
		return new EntityInformation(informationInstance);
	};

	create() {
		this.native.create();
	};

	delete() {
		this.native.delete();
	};

	renameTo(name) {
		this.native.renameTo(name);
	};

	moveTo(path) {
		this.native.moveTo(path);
	};

	copyTo(path) {
		this.native.copyTo(path);
	};

	exists() {
		return this.native.exists();
	};

	isEmpty() {
		return this.native.isEmpty();
	};

	getCollectionsNames() {
		return this.native.getCollectionsNames();
	};

	createCollection(name) {
		const collectionInstance = this.native.createCollection(name);
		return new Collection(collectionInstance);
	};

	getCollection(name) {
		const collectionInstance = this.native.getCollection(name);
		return new Collection(collectionInstance);
	};

	removeCollection(name) {
		this.native.removeCollection(name);
	};

	getResourcesNames() {
		return this.native.getResourcesNames();
	};

	getResource(name) {
		const resourceInstance = this.native.getResource(name);
		return new Resource(resourceInstance);
	};

	removeResource(name) {
		this.native.removeResource(name);
	};

	createResource(name, content) {
		const resourceInstance = this.native.createResource(name, content);
		return new Resource(resourceInstance);
	};
}

class EntityInformation {

	constructor(private native) { }

	getName() {
		return this.native.getName();
	};

	getPath() {
		return this.native.getPath();
	};

	getPermissions() {
		return this.native.getPermissions();
	};

	getSize() {
		return this.native.getSize();
	};

	getCreatedBy() {
		return this.native.getCreatedBy();
	};

	getCreatedAt() {
		return this.native.getCreatedAt();
	};

	getModifiedBy() {
		return this.native.getModifiedBy();
	};

	getModifiedAt() {
		return this.native.getModifiedAt();
	};
}
