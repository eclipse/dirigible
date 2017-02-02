/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java org */
/* eslint-env node, dirigible */

var streams = require('io/streams');
var env = require('core/env');

exports.getSession = function() {
	var internalSession = $.getDocumentService().getSession();
	var session = new Session(internalSession);
	session = validateSession(session);
	return session;
};

/**
 * Session object
 */
function Session(internalSession) {
	this.internalSession = internalSession;

	this.getInternalObject = function() {
		return this.internalSession;
	};

	this.getRepositoryInfo = function() {
		var internalRepositoryInfo = this.internalSession.getRepositoryInfo();
		return new RepositoryInfo(internalRepositoryInfo);
	};

	this.getRootFolder = function() {
		var internalRootFolder = this.internalSession.getRootFolder();
		return new Folder(internalRootFolder);
	};

	this.getObjectFactory = function() {
		var internalObjectFactory = this.internalSession.getObjectFactory();
		return new ObjectFactory(internalObjectFactory);
	};

	this.getObject = function(objectId) {
		var internalCmisObject = this.internalSession.getObject(objectId);
		var type = internalCmisObject.getType().getId();
		if (type === exports.OBJECT_TYPE_DOCUMENT) {
			return new Document(internalCmisObject);
		} else if (type === exports.OBJECT_TYPE_FOLDER) {
			return new Folder(internalCmisObject);
		}
		throw new Error("Unsupported CMIS object type: " + type);
	};

	this.getObjectByPath = function(path) {
		var internalCmisObject = this.internalSession.getObjectByPath(path);
		var type = internalCmisObject.getType().getId();
		if (type === exports.OBJECT_TYPE_DOCUMENT) {
			return new Document(internalCmisObject);
		} else if (type === exports.OBJECT_TYPE_FOLDER) {
			return new Folder(internalCmisObject);
		}
		throw new Error("Unsupported CMIS object type: " + type);
	};
}

/**
 * RepositoryInfo object
 */
function RepositoryInfo(internalRepositoryInfo) {
	this.internalRepositoryInfo = internalRepositoryInfo;

	this.getInternalObject = function() {
		return this.internalRepositoryInfo;
	};

	this.getId = function() {
		return this.internalRepositoryInfo.getId();
	};

	this.getName = function() {
		return this.internalRepositoryInfo.getName();
	};
}

/**
 * Folder object
 */
function Folder(internalFolder) {
	this.internalFolder = internalFolder;

	this.getInternalObject = function() {
		return this.internalFolder;
	};

	this.getId = function() {
		return this.internalFolder.getId();
	};

	this.getName = function() {
		return this.internalFolder.getName();
	};

	this.createFolder = function(properties) {
		var map = new java.util.HashMap();
		for (var property in properties) {
		    if (properties.hasOwnProperty(property)) {
		        map.put(property, properties[property]);
		    }
		}
	
		var newFolder = this.internalFolder.createFolder(map);
		return new Folder(newFolder);
	};

	this.createDocument = function(properties, contentStream, versioningState) {
		var map = new java.util.HashMap();
		for (var property in properties) {
		    if (properties.hasOwnProperty(property)) {
		        map.put(property, properties[property]);
		    }
		}
	
		var newDocument = this.internalFolder.createDocument(map, contentStream.getInternalObject(), getInternalVersioningState(versioningState));
		return new Document(newDocument);
	};

	function getInternalVersioningState(state) {
		if (state === exports.VERSIONING_STATE_NONE) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.NONE;
		} else if (state === exports.VERSIONING_STATE_MAJOR) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
		} else if (state === exports.VERSIONING_STATE_MINOR) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.MINOR;
		}  else if (state === exports.VERSIONING_STATE_CHECKEDOUT) {
			return org.apache.chemistry.opencmis.commons.enums.VersioningState.CHECKEDOUT;
		}
		return org.apache.chemistry.opencmis.commons.enums.VersioningState.MAJOR;
	}

	this.getChildren = function() {
		var children = [];
		var internalChildren = this.internalFolder.getChildren();
		var iter = internalChildren.iterator();
		while (iter.hasNext()) {
			var internalCmisObject = iter.next();
			var child = new CmisObject(internalCmisObject);
			children.push(child);
		}
		return children;
	};

	this.getPath = function() {
		return this.internalFolder.getPath();
	};

	this.isRootFolder = function() {
		return this.internalFolder.isRootFolder();
	};

	this.getFolderParent = function() {
		var internalParentFolder = this.internalFolder.getFolderParent();
		return new Folder(internalParentFolder);
	};

	this.delete = function() {
		return this.internalFolder.delete(true);
	};

	this.rename = function(newName) {
		return this.internalFolder.rename(newName, true);
	};
	
	this.deleteTree = function() {
		return this.internalFolder.deleteTree(true, org.apache.chemistry.opencmis.commons.enums.UnfileObject.DELETE, true);
	};
}

/**
 * CmisObject object
 */
function CmisObject(internalCmisObject) {
	this.internalCmisObject = internalCmisObject;

	this.getInternalObject = function() {
		return this.internalCmisObject;
	};

	this.getId = function() {
		return this.internalCmisObject.getId();
	};

	this.getName = function() {
		return this.internalCmisObject.getName();
	};

	this.getType = function() {
		return this.internalCmisObject.getType().getId();
	};

	this.delete = function() {
		return this.internalCmisObject.delete(true);
	};

	this.rename = function(newName) {
		return this.internalCmisObject.rename(newName, true);
	};
}

/**
 * ObjectFactory object
 */
function ObjectFactory(internalObjectFactory) {
	this.internalObjectFactory = internalObjectFactory;

	this.getInternalObject = function() {
		return this.internalObjectFactory;
	};

	this.createContentStream = function(filename, length, mimetype, inputStream) {
		var internalContentStream = this.internalObjectFactory.createContentStream(filename, length, mimetype, inputStream.getInternalObject());
		return new ContentStream(internalContentStream);
	};
}

/**
 * ContentStream object
 */
function ContentStream(internalContentStream) {
	this.internalContentStream = internalContentStream;

	this.getInternalObject = function() {
		return this.internalContentStream;
	};

	this.getStream = function() {
		var internalStream = this.internalContentStream.getStream();
		return new streams.InputStream(internalStream);
	};
}

/**
 * Document object
 */
function Document(internalDocument) {
	this.internalDocument = internalDocument;

	this.getInternalObject = function() {
		return this.internalDocument;
	};

	this.getId = function() {
		return this.internalDocument.getId();
	};

	this.getName = function() {
		return this.internalDocument.getName();
	};

	this.delete = function() {
		return this.internalDocument.delete(true);
	};

	this.getContentStream = function() {
		var internalContentStream = this.internalDocument.getContentStream();
		if (internalContentStream !== null) {
			return new ContentStream(internalContentStream);
		}
		return null;
	};

	this.rename = function(newName) {
		return this.internalDocument.rename(newName, true);
	};
}

function validateSession(session) {
	if (session === null || session === undefined) {
		throw new Error("CMIS Session is not present");
	}
	if (session.getRepositoryInfo().getId().indexOf("local") !== -1) {
		// local session
		var cmisService = env.get("jndiCmisService");
		if (cmisService) {
			// we have to re-inject the underlying cmis service, which seems failed before
			var internalSession = null;
			var cmisAuthType = env.get("jndiCmisServiceAuth");
			if (cmisAuthType === null) {
				cmisAuthType = "key"; //default
			}
			if (cmisAuthType === "key") {
				var cmisName = env.get("jndiCmisServiceName");
				var cmisKey = env.get("jndiCmisServiceKey");
				if (cmisName === null || cmisKey === null) {
					var message = "CMIS Name or Key parameter is null";
					console.error(message);
					throw new Error(message);
				}
				internalSession = $.getInitialContext().lookup(cmisService).connect(cmisName, cmisKey);
			} else if (cmisAuthType === "destination"){
				var cmisServiceDestination = env.get("jndiCmisServiceDestination");
				if (cmisServiceDestination === null) {
					var message = "CMIS Service Destination is null";
					console.error(message);
					throw new Error(message);
				}

				var destination = getDestination(cmisServiceDestination);
				var cmisName = destination.get("User");
				var cmisKey = destination.get("Password");
				if (cmisName === null || cmisKey === null) {
					var message = "CMIS Name or Key parameter is null";
					console.error(message);
					throw new Error(message);
				}
				internalSession = $.getInitialContext().lookup(cmisService).connect(cmisName, cmisKey);
			} else {
				var message = "CMIS Authentication Type is unknown: " + cmisAuthType;
				console.error(message);
				throw new Error(message);
			}
			session = new Session(internalSession);
		}
	
	}
	return session;
}

function getDestination(destName) {
    
    var ctx = $.getInitialContext();
 
    if (ctx != null) {
        var configuration = $.getConnectivityService().getConnectivityConfiguration();
        var destinationConfiguration = configuration.getConfiguration(destName);
        var destinationPropeties = destinationConfiguration.getAllProperties();
        return destinationPropeties;
    }
    return null;
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


