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
/**
 * API CMIS
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

import * as streams from "@dirigible/io/streams";
const CMIS_METHOD_READ = "READ";
const CMIS_METHOD_WRITE = "WRITE";
const CmisFacade = Java.type("org.eclipse.dirigible.components.api.cms.CmisFacade");
const Gson = Java.type("com.google.gson.Gson");
const HashMap = Java.type("java.util.HashMap");

export function getSession() {
	const native = CmisFacade.getSession();
	var session = new Session(native);
	return session;
};

export function getAccessDefinitions(path, method) {
	let accessDefinitions = CmisFacade.getAccessDefinitions(path, method);
	return JSON.parse(new Gson().toJson(accessDefinitions));
};

/**
 * Session object
 */
class Session {

	constructor(private native) { }

	getRepositoryInfo() {
		var native = this.native.getRepositoryInfo();
		var repositoryInfo = new RepositoryInfo(native);
		return repositoryInfo;
	};

	getRootFolder() {
		var native = this.native.getRootFolder();
		var rootFolder = new Folder(native, null);
		return rootFolder;
	};

	getObjectFactory() {
		var native = this.native.getObjectFactory();
		var objectFactory = new ObjectFactory(native);
		return objectFactory;
	};

	getObject(objectId) {
		var objectInstance = this.native.getObject(objectId);
		var objectInstanceType = objectInstance.getType();
		var objectInstanceTypeId = objectInstanceType.getId();
		if (objectInstanceTypeId === OBJECT_TYPE_DOCUMENT) {
			var document = new Document(objectInstance, null);
			return document;
		} else if (objectInstanceTypeId === OBJECT_TYPE_FOLDER) {
			var folder = new Folder(objectInstance, null);
			return folder;
		}
		throw new Error("Unsupported CMIS object type: " + objectInstanceTypeId);
	};

	getObjectByPath(path) {
		var allowed = CmisFacade.isAllowed(path, CMIS_METHOD_READ);
		if (!allowed) {
			throw new Error("Read access not allowed on: " + path);
		}
		var objectInstance = this.native.getObjectByPath(path);
		var objectInstanceType = objectInstance.getType();
		var objectInstanceTypeId = objectInstanceType.getId();
		if (objectInstanceTypeId === OBJECT_TYPE_DOCUMENT) {
			var document = new Document(objectInstance, path);
			return document;
		} else if (objectInstanceTypeId === OBJECT_TYPE_FOLDER) {
			var folder = new Folder(objectInstance, path);
			return folder;
		}
		throw new Error("Unsupported CMIS object type: " + objectInstanceTypeId);
	};

	createFolder(location: string): Folder {
		if (location.startsWith("/")) {
			location = location.substring(1, location.length);
		}
		if (location.endsWith("/")) {
			location = location.substring(0, location.length - 1);
		}
		const segments = location.split("/");
		let folder = this.getRootFolder();
		for (const next of segments) {
			const properties = {
				[OBJECT_TYPE_ID]: OBJECT_TYPE_FOLDER,
				[NAME]: next
			};
			folder = folder.createFolder(properties);
		}
		return folder;
	}

	createDocument(location: string, properties, contentStream, versioningState): Document {
		const folder = this.createFolder(location);
		return folder.createDocument(properties, contentStream, versioningState);
	}
}

/**
 * RepositoryInfo object
 */
class RepositoryInfo {

	constructor(private native) { }

	getId() {
		return this.native.getId();
	};

	getName() {
		return this.native.getName();
	};
}

/**
 * Folder object
 */
class Folder {

	constructor(private native, private path) { }

	getId() {
		return this.native.getId();
	};

	getName() {
		return this.native.getName();
	};

	createFolder(properties) {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var mapInstance = new HashMap();
		for (var property in properties) {
			if (properties.hasOwnProperty(property)) {
				mapInstance.put(property, properties[property]);
			}
		}
		var native = this.native.createFolder(mapInstance);
		var folder = new Folder(native, null);
		return folder;
	};

	createDocument(properties, contentStream, versioningState) {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var mapInstance = new HashMap();
		for (var property in properties) {
			if (properties.hasOwnProperty(property)) {
				mapInstance.put(property, properties[property]);
			}
		}
		var state = CmisFacade.getVersioningState(versioningState);

		var native = this.native.createDocument(mapInstance, contentStream.native, state);
		var document = new Document(native, null);
		return document;
	};

	getChildren() {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_READ);
		if (!allowed) {
			throw new Error("Read access not allowed on: " + this.getPath());
		}
		var children = [];
		var childrenInstance = this.native.getChildren();
		var childrenInstanceIterator = childrenInstance.iterator();
		while (childrenInstanceIterator.hasNext()) {
			var cmisObjectInstance = childrenInstanceIterator.next();
			var cmisObject = new CmisObject(cmisObjectInstance);
			children.push(cmisObject);
		}
		return children;
	};

	getPath() {
		return this.path;
	};

	isRootFolder() {
		return this.native.isRootFolder();
	};

	getFolderParent() {
		var native = this.native.getFolderParent();
		var folder = new Folder(native, null);
		return folder;
	};

	delete() {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return this.native.delete();
	};

	rename(newName) {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return this.native.rename(newName);
	};

	deleteTree() {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var unifiedObjectDelete = CmisFacade.getUnifiedObjectDelete();
		return this.native.deleteTree(true, unifiedObjectDelete, true);
	};

	getType() {
		var native = this.native.getType();
		var type = new TypeDefinition(native);
		return type;
	};
}

/**
 * CmisObject object
 */
class CmisObject {

	constructor(private native) { }

	getId() {
		return this.native.getId();
	};

	getName() {
		return this.native.getName();
	};

	getPath() {
		//this is caused by having different underlying native objects in different environments.
		if (this.native.getPath) {
			return this.native.getPath();
		}

		//Apache Chemistry CmisObject has no getPath() but getPaths() - https://chemistry.apache.org/docs/cmis-samples/samples/retrieve-objects/index.html
		if (this.native.getPaths) {
			return this.native.getPaths()[0];
		}

		throw new Error(`Path not found for CmisObject with id ${this.getId()}`);
	}

	getType() {
		var native = this.native.getType();
		var type = new TypeDefinition(native);
		return type;
	};

	delete() {
		return this.native.delete();
	};

	rename(newName) {
		return this.native.rename(newName);
	};

}

/**
 * ObjectFactory object
 */
class ObjectFactory {

	constructor(private native) { }

	createContentStream(filename, length, mimetype, inputStream) {
		var native = this.native.createContentStream(filename, length, mimetype, inputStream.native);
		var contentStream = new ContentStream(native);
		return contentStream;
	};
}

/**
 * ContentStream object
 */
class ContentStream {

	constructor(private native) { }

	getStream() {
		const native = this.native.getStream();
		return new streams.InputStream(native);
	};

	getMimeType() {
		return this.native.getMimeType();
	};
}

/**
 * Document object
 */
class Document {

	constructor(private native, private path) { }

	getId() {
		return this.native.getId();
	};

	getName() {
		return this.native.getName();
	};

	getType() {
		var native = this.native.getType();
		var type = new TypeDefinition(native);
		return type;
	};

	getPath() {
		return this.path;
	};

	delete() {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return this.native.delete(true);
	};

	getContentStream() {
		var native = this.native.getContentStream();
		if (native !== null) {
			var contentStream = new ContentStream(native);
			return contentStream;
		}
		return null;
	};

	getSize() {
		return this.native.getSize();
	};

	rename(newName) {
		var allowed = CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return this.native.rename(newName);
	};
}

class TypeDefinition {

	constructor(private native) { }

	getId() {
		return this.native.getId();
	};
}
// CONSTANTS

export const METHOD_READ = CMIS_METHOD_READ;
export const METHOD_WRITE = CMIS_METHOD_WRITE;

// ---- Base ----
export const NAME = "cmis:name";
export const OBJECT_ID = "cmis:objectId";
export const OBJECT_TYPE_ID = "cmis:objectTypeId";
export const BASE_TYPE_ID = "cmis:baseTypeId";
export const CREATED_BY = "cmis:createdBy";
export const CREATION_DATE = "cmis:creationDate";
export const LAST_MODIFIED_BY = "cmis:lastModifiedBy";
export const LAST_MODIFICATION_DATE = "cmis:lastModificationDate";
export const CHANGE_TOKEN = "cmis:changeToken";

// ---- Document ----
export const IS_IMMUTABLE = "cmis:isImmutable";
export const IS_LATEST_VERSION = "cmis:isLatestVersion";
export const IS_MAJOR_VERSION = "cmis:isMajorVersion";
export const IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";
export const VERSION_LABEL = "cmis:versionLabel";
export const VERSION_SERIES_ID = "cmis:versionSeriesId";
export const IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";
export const VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";
export const VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";
export const CHECKIN_COMMENT = "cmis:checkinComment";
export const CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";
export const CONTENT_STREAM_MIME_TYPE = "cmis:contentStreamMimeType";
export const CONTENT_STREAM_FILE_NAME = "cmis:contentStreamFileName";
export const CONTENT_STREAM_ID = "cmis:contentStreamId";

// ---- Folder ----
export const PARENT_ID = "cmis:parentId";
export const ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:allowedChildObjectTypeIds";
export const PATH = "cmis:path";

// ---- Relationship ----
export const SOURCE_ID = "cmis:sourceId";
export const TARGET_ID = "cmis:targetId";

// ---- Policy ----
export const POLICY_TEXT = "cmis:policyText";

// ---- Versioning States ----
export const VERSIONING_STATE_NONE = "none";
export const VERSIONING_STATE_MAJOR = "major";
export const VERSIONING_STATE_MINOR = "minor";
export const VERSIONING_STATE_CHECKEDOUT = "checkedout";

// ---- Object Types ----
export const OBJECT_TYPE_DOCUMENT = "cmis:document";
export const OBJECT_TYPE_FOLDER = "cmis:folder";
export const OBJECT_TYPE_RELATIONSHIP = "cmis:relationship";
export const OBJECT_TYPE_POLICY = "cmis:policy";
export const OBJECT_TYPE_ITEM = "cmis:item";
export const OBJECT_TYPE_SECONDARY = "cmis:secondary";
