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

var streams = require("io/streams");

var CMIS_METHOD_READ = "READ";
var CMIS_METHOD_WRITE = "WRITE";

exports.getSession = function() {
	var session = new Session();
	var native = org.eclipse.dirigible.components.api.cms.CmisFacade.getSession();
	session.native = native;
	return session;
};

exports.getAccessDefinitions = function(path, method) {
	let accessDefinitions = org.eclipse.dirigible.components.api.cms.CmisFacade.getAccessDefinitions(path, method);
	return JSON.parse(new com.google.gson.Gson().toJson(accessDefinitions));
};

/**
 * Session object
 */
function Session() {
	
	this.getRepositoryInfo = function() {
		var repositoryInfo = new RepositoryInfo();
		var native = this.native.getRepositoryInfo();
		repositoryInfo.native = native;
		return repositoryInfo;
	};

	this.getRootFolder = function() {
		var rootFolder = new Folder();
		var native = this.native.getRootFolder();
		rootFolder.native = native;
		return rootFolder;
	};

	this.getObjectFactory = function() {
		var objectFactory = new ObjectFactory();
		var native = this.native.getObjectFactory();
		objectFactory.native = native;
		return objectFactory;
	};

	this.getObject = function(objectId) {
		var objectInstance = this.native.getObject(objectId);
		var objectInstanceType = objectInstance.getType();
		var objectInstanceTypeId = objectInstanceType.getId();
		if (objectInstanceTypeId === exports.OBJECT_TYPE_DOCUMENT) {
			var document = new Document();
			document.native = objectInstance;
			return document;
		} else if (objectInstanceTypeId === exports.OBJECT_TYPE_FOLDER) {
			var folder = new Folder();
			folder.native = objectInstance;
			return folder;
		}
		throw new Error("Unsupported CMIS object type: " + objectInstanceTypeId);
	};

	this.getObjectByPath = function(path) {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(path, CMIS_METHOD_READ);
		if (!allowed) {
			throw new Error("Read access not allowed on: " + path);
		}
		var objectInstance = this.native.getObjectByPath(path);
		var objectInstanceType = objectInstance.getType();
		var objectInstanceTypeId = objectInstanceType.getId();
		if (objectInstanceTypeId === exports.OBJECT_TYPE_DOCUMENT) {
			var document = new Document();
			document.native = objectInstance;
			document.path = path;
			return document;
		} else if (objectInstanceTypeId === exports.OBJECT_TYPE_FOLDER) {
			var folder = new Folder();
			folder.native = objectInstance;
			folder.path = path;
			return folder;
		}
		throw new Error("Unsupported CMIS object type: " + objectInstanceTypeId);
	};
}

/**
 * RepositoryInfo object
 */
function RepositoryInfo() {

	this.getId = function() {
		return this.native.getId();
	};

	this.getName = function() {
		return this.native.getName();
	};
}

/**
 * Folder object
 */
function Folder() {
	
	this.getId = function() {
		return this.native.getId();
	};

	this.getName = function() {
		return this.native.getName();
	};

	this.createFolder = function(properties) {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var mapInstance = new java.util.HashMap();
		for (var property in properties) {
		    if (properties.hasOwnProperty(property)) {
		    	mapInstance.put(property, properties[property]);
		    }
		}
		var folder = new Folder();
		var native = this.native.createFolder(mapInstance);
		folder.native = native;
		return folder;
	};

	this.createDocument = function(properties, contentStream, versioningState) {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var mapInstance = new java.util.HashMap();
		for (var property in properties) {
		    if (properties.hasOwnProperty(property)) {
		    	mapInstance.put(property, properties[property]);
		    }
		}
		var state = org.eclipse.dirigible.components.api.cms.CmisFacade.getVersioningState(versioningState);
		
		var document = new Document();
		var native = this.native.createDocument(mapInstance, contentStream.native, state);
		document.native = native;
		return document;
	};

	this.getChildren = function() {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_READ);
		if (!allowed) {
			throw new Error("Read access not allowed on: " + this.getPath());
		}
		var children = [];
		var childrenInstance = this.native.getChildren();
		var childrenInstanceIterator = childrenInstance.iterator();
		while (childrenInstanceIterator.hasNext()) {
			var cmisObject = new CmisObject();
			var cmisObjectInstance = childrenInstanceIterator.next();
			cmisObject.native = cmisObjectInstance;
			children.push(cmisObject);
		}
		return children;
	};

	this.getPath = function() {
		return this.path;
	};

	this.isRootFolder = function() {
		return this.native.isRootFolder();
	};

	this.getFolderParent = function() {
		var folder = new Folder();
		var native = this.native.getFolderParent();
		folder.native = native;
		return folder;
	};

	this.delete = function() {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return this.native.delete();
	};

	this.rename = function(newName) {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return this.native.rename(newName);
	};
	
	this.deleteTree = function() {
		var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var unifiedObjectDelete = org.eclipse.dirigible.components.api.cms.CmisFacade.getUnifiedObjectDelete();
		return this.native.deleteTree(true, unifiedObjectDelete, true);
	};

	this.getType = function() {
		var type = new TypeDefinition();
		var native = this.native.getType();
		type.native = native;
		return type;
	};
}

/**
 * CmisObject object
 */
function CmisObject() {

	this.getId = function() {
		return this.native.getId();
	};

	this.getName = function() {
		return this.native.getName();
	};

	this.getPath = function() {
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

	this.getType = function() {
		var type = new TypeDefinition();
		var native = this.native.getType();
		type.native = native;
		return type;
	};

	this.delete = function() {
		return this.native.delete();
	};

	this.rename = function(newName) {
		return this.native.rename(newName);
	};

}

/**
 * ObjectFactory object
 */
function ObjectFactory() {

	this.createContentStream = function(filename, length, mimetype, inputStream) {
		console.info("File name: " + filename);
		console.info("Length: " + length);
		console.info("Mime Type: " + mimetype);
		var contentStream = new ContentStream();
		var native = this.native.createContentStream(filename, length, mimetype, inputStream.native);
		contentStream.native = native;
		return contentStream;
	};
}

/**
 * ContentStream object
 */
function ContentStream() {

	this.getStream = function() {
		var inputStream = new streams.InputStream();
		var native = this.native.getStream();
		inputStream.native = native;
		return inputStream;
	};

	this.getMimeType = function() {
		return this.native.getMimeType();
	};
}

/**
 * Document object
 */
function Document() {

	this.getId = function() {
		return this.native.getId();
	};

	this.getName = function() {
		return this.native.getName();
	};

	this.getType = function() {
		var type = new TypeDefinition();
		var native = this.native.getType();
		type.native = native;
		return type;
	};

	this.getPath = function() {
    	return this.path;
    };

	this.delete = function() {
	    var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
       	    if (!allowed) {
            	throw new Error("Write access not allowed on: " + this.getPath());
            }
		return this.native.delete(true);
	};

	this.getContentStream = function() {
		var native = this.native.getContentStream();
		if (native !== null) {
			var contentStream = new ContentStream();
			contentStream.native = native;
			return contentStream;
		}
		return null;
	};

	this.getSize = function() {
		return this.native.getSize();
	};

	this.rename = function(newName) {
	    var allowed = org.eclipse.dirigible.components.api.cms.CmisFacade.isAllowed(this.getPath(), CMIS_METHOD_WRITE);
    	    if (!allowed) {
    		throw new Error("Write access not allowed on: " + this.getPath());
    	    }
		return this.native.rename(newName);
	};
}

function TypeDefinition() {

	this.getId = function() {
		return this.native.getId();
	};
}
// CONSTANTS

exports.METHOD_READ = CMIS_METHOD_READ;
exports.METHOD_WRITE = CMIS_METHOD_WRITE;

// ---- Base ----
exports.NAME = "cmis:name";
exports.OBJECT_ID = "cmis:objectId";
exports.OBJECT_TYPE_ID = "cmis:objectTypeId";
exports.BASE_TYPE_ID = "cmis:baseTypeId";
exports.CREATED_BY = "cmis:createdBy";
exports.CREATION_DATE = "cmis:creationDate";
exports.LAST_MODIFIED_BY = "cmis:lastModifiedBy";
exports.LAST_MODIFICATION_DATE = "cmis:lastModificationDate";
exports.CHANGE_TOKEN = "cmis:changeToken";

// ---- Document ----
exports.IS_IMMUTABLE = "cmis:isImmutable";
exports.IS_LATEST_VERSION = "cmis:isLatestVersion";
exports.IS_MAJOR_VERSION = "cmis:isMajorVersion";
exports.IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";
exports.VERSION_LABEL = "cmis:versionLabel";
exports.VERSION_SERIES_ID = "cmis:versionSeriesId";
exports.IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";
exports.VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";
exports.VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";
exports.CHECKIN_COMMENT = "cmis:checkinComment";
exports.CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";
exports.CONTENT_STREAM_MIME_TYPE = "cmis:contentStreamMimeType";
exports.CONTENT_STREAM_FILE_NAME = "cmis:contentStreamFileName";
exports.CONTENT_STREAM_ID = "cmis:contentStreamId";

// ---- Folder ----
exports.PARENT_ID = "cmis:parentId";
exports.ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:allowedChildObjectTypeIds";
exports.PATH = "cmis:path";

// ---- Relationship ----
exports.SOURCE_ID = "cmis:sourceId";
exports.TARGET_ID = "cmis:targetId";

// ---- Policy ----
exports.POLICY_TEXT = "cmis:policyText";

// ---- Versioning States ----
exports.VERSIONING_STATE_NONE = "none";
exports.VERSIONING_STATE_MAJOR = "major";
exports.VERSIONING_STATE_MINOR = "minor";
exports.VERSIONING_STATE_CHECKEDOUT = "checkedout";

// ---- Object Types ----
exports.OBJECT_TYPE_DOCUMENT = "cmis:document";
exports.OBJECT_TYPE_FOLDER = "cmis:folder";
exports.OBJECT_TYPE_RELATIONSHIP = "cmis:relationship";
exports.OBJECT_TYPE_POLICY = "cmis:policy";
exports.OBJECT_TYPE_ITEM = "cmis:item";
exports.OBJECT_TYPE_SECONDARY = "cmis:secondary";
