/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');
var streams = require('io/v3/streams');

var CMIS_METHOD_READ = 'READ';
var CMIS_METHOD_WRITE = 'WRITE';

exports.getSession = function() {
	var sessionInstance = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'getSession', [], true);
	var session = new Session();
	session.uuid = sessionInstance.uuid;
	return session;
};

/**
 * Session object
 */
function Session() {
	
	this.getRepositoryInfo = function() {
		var repositoryInfoInstance = java.invoke(this.uuid, 'getRepositoryInfo', [], true);
		var repositoryInfo = new RepositoryInfo();
		repositoryInfo.uuid = repositoryInfoInstance.uuid;
		return repositoryInfo;
	};

	this.getRootFolder = function() {
		var rootFolderInstance = java.invoke(this.uuid, 'getRootFolder', [], true);
		var rootFolder = new Folder();
		rootFolder.uuid = rootFolderInstance.uuid;
		return rootFolder;
	};

	this.getObjectFactory = function() {
		var objectFactoryInstance = java.invoke(this.uuid, 'getObjectFactory', [], true);
		var objectFactory = new ObjectFactory();
		objectFactory.uuid = objectFactoryInstance.uuid;
		return objectFactory;
	};

	this.getObject = function(objectId) {
		var objectInstance = java.invoke(this.uuid, 'getObject', [objectId], true);
		var objectInstanceType = java.invoke(objectInstance.uuid, 'getType', [], true);
		var objectInstanceTypeId = java.invoke(objectInstanceType.uuid, 'getId', []);
		if (objectInstanceTypeId === exports.OBJECT_TYPE_DOCUMENT) {
			var document = new Document();
			document.uuid = objectInstance.uuid;
			return document;
		} else if (objectInstanceTypeId === exports.OBJECT_TYPE_FOLDER) {
			var folder = new Folder();
			folder.uuid = objectInstance.uuid;
			return folder;
		}
		throw new Error("Unsupported CMIS object type: " + objectInstanceTypeId);
	};

	this.getObjectByPath = function(path) {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [path, CMIS_METHOD_READ]);
		if (!allowed) {
			throw new Error("Read access not allowed on: " + path);
		}
		var objectInstance = java.invoke(this.uuid, 'getObjectByPath', [path], true);
		var objectInstanceType = java.invoke(objectInstance.uuid, 'getType', [], true);
		var objectInstanceTypeId = java.invoke(objectInstanceType.uuid, 'getId', []);
		if (objectInstanceTypeId === exports.OBJECT_TYPE_DOCUMENT) {
			var document = new Document();
			document.uuid = objectInstance.uuid;
			return document;
		} else if (objectInstanceTypeId === exports.OBJECT_TYPE_FOLDER) {
			var folder = new Folder();
			folder.uuid = objectInstance.uuid;
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
		return java.invoke(this.uuid, 'getId', []);
	};

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};
}

/**
 * Folder object
 */
function Folder() {
	
	this.getId = function() {
		return java.invoke(this.uuid, 'getId', []);
	};

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};

	this.createFolder = function(properties) {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [this.getPath(), CMIS_METHOD_WRITE]);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var mapInstance = java.instantiate('java.util.HashMap', []);
		for (var property in properties) {
		    if (properties.hasOwnProperty(property)) {
		    	java.invoke(mapInstance.uuid, 'put', [property, properties[property]]);
		    }
		}
		var folderInstance = java.invoke(this.uuid, 'createFolder', [mapInstance.uuid], true);
		var folder = new Folder();
		folder.uuid = folderInstance.uuid;
		return folder;
	};

	this.createDocument = function(properties, contentStream, versioningState) {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [this.getPath(), CMIS_METHOD_WRITE]);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var mapInstance = java.instantiate('java.util.HashMap', []);
		for (var property in properties) {
		    if (properties.hasOwnProperty(property)) {
		    	java.invoke(mapInstance.uuid, 'put', [property, properties[property]]);
		    }
		}
		var state = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'getVersioningState', [versioningState], true);
		
		var documentInstance = java.invoke(this.uuid, 'createDocument', [mapInstance.uuid, contentStream.uuid, state.uuid], true);
		var document = new Document();
		document.uuid = documentInstance.uuid;
		return document;
	};

	this.getChildren = function() {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [this.getPath(), CMIS_METHOD_READ]);
		if (!allowed) {
			throw new Error("Read access not allowed on: " + this.getPath());
		}
		var children = [];
		var childrenInstance = java.invoke(this.uuid, 'getChildren', [], true);
		var childrenInstanceIterator = java.invoke(childrenInstance.uuid, 'iterator', [], true);
		while (java.invoke(childrenInstanceIterator.uuid, 'hasNext', [])) {
			var cmisObjectInstance = java.invoke(childrenInstanceIterator.uuid, 'next', [], true);
			var cmisObject = new CmisObject();
			cmisObject.uuid = cmisObjectInstance.uuid;
			children.push(cmisObject);
		}
		return children;
	};

	this.getPath = function() {
		return java.invoke(this.uuid, 'getPath', []);
	};

	this.isRootFolder = function() {
		return java.invoke(this.uuid, 'isRootFolder', []);
	};

	this.getFolderParent = function() {
		var folderParentInstance = java.invoke(this.uuid, 'getFolderParent', [], true);
		var folder = new Folder();
		folder.uuid = folderParentInstance.uuid;
		return folder;
	};

	this.delete = function() {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [this.getPath(), CMIS_METHOD_WRITE]);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return java.invoke(this.uuid, 'delete', [true]);
	};

	this.rename = function(newName) {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [this.getPath(), CMIS_METHOD_WRITE]);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		return java.invoke(this.uuid, 'rename', [newName]);
	};
	
	this.deleteTree = function() {
		var allowed = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'isAllowed', [this.getPath(), CMIS_METHOD_WRITE]);
		if (!allowed) {
			throw new Error("Write access not allowed on: " + this.getPath());
		}
		var unifiedObjectDelete = java.call('org.eclipse.dirigible.api.v3.cms.CmisFacade', 'getUnifiedObjectDelete', [], true);
		return java.invoke(this.uuid, 'deleteTree', [true, unifiedObjectDelete.uuid, true]);
	};

	this.getType = function() {
		var typeInstance = java.invoke(this.uuid, 'getType', [], true);
		var type = new TypeDefinition();
		type.uuid = typeInstance.uuid;
		return type;
	};
}

/**
 * CmisObject object
 */
function CmisObject() {

	this.getId = function() {
		return java.invoke(this.uuid, 'getId', []);
	};

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};

	this.getType = function() {
		var typeInstance = java.invoke(this.uuid, 'getType', [], true);
		var type = new TypeDefinition();
		type.uuid = typeInstance.uuid;
		return type;
	};

	this.delete = function() {
		return java.invoke(this.uuid, 'delete', []);
	};

	this.rename = function(newName) {
		return java.invoke(this.uuid, 'rename', [newName]);
	};

}

/**
 * ObjectFactory object
 */
function ObjectFactory() {

	this.createContentStream = function(filename, length, mimetype, inputStream) {
		console.warn('File name: ' + filename);
		console.warn('Length: ' + length);
		console.warn('Mime Type: ' + mimetype);
		var contentStreamInstance = java.invoke(this.uuid, 'createContentStream', [filename, length, mimetype, inputStream.uuid], true);
		var contentStream = new ContentStream();
		contentStream.uuid = contentStreamInstance.uuid;
		return contentStream;
	};
}

/**
 * ContentStream object
 */
function ContentStream() {

	this.getStream = function() {
		var streamInstance = java.invoke(this.uuid, 'getStream', [], true);
		var inputStream = new streams.InputStream();
		inputStream.uuid = streamInstance.uuid;
		return inputStream;
	};

	this.getMimeType = function() {
		return java.invoke(this.uuid, 'getMimeType', []);
	};
}

/**
 * Document object
 */
function Document() {

	this.getId = function() {
		return java.invoke(this.uuid, 'getId', []);
	};

	this.getName = function() {
		return java.invoke(this.uuid, 'getName', []);
	};

	this.getType = function() {
		var typeInstance = java.invoke(this.uuid, 'getType', [], true);
		var type = new TypeDefinition();
		type.uuid = typeInstance.uuid;
		return type;
	};

	this.delete = function() {
		return java.invoke(this.uuid, 'delete', [true]);
	};

	this.getContentStream = function() {
		var contentStreamInstance = java.invoke(this.uuid, 'getContentStream', [], true);
		if (contentStreamInstance !== null) {
			var contentStream = new ContentStream();
			contentStream.uuid = contentStreamInstance.uuid;
			return contentStream;
		}
		return null;
	};

	this.getSize = function() {
		return java.invoke(this.uuid, 'getSize', []);
	};

	this.rename = function(newName) {
		return java.invoke(this.uuid, 'rename', [newName]);
	};
}

function TypeDefinition() {

	this.getId = function() {
		return java.invoke(this.uuid, 'getId', []);
	};
}
// CONSTANTS

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
