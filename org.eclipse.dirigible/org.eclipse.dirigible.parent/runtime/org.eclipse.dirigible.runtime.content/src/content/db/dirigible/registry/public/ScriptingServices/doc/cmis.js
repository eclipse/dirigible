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

exports.getSession = function() {
	var internalSession = $.getDocumentService().getSession();
	return new Session(internalSession);
};

/**
 * Session object
 */
function Session(internalSession) {
	this.internalSession = internalSession;
	this.getInternalObject = sessionGetInternalObject;
	this.getRepositoryInfo = sessionGetRepositoryInfo;
	this.getRootFolder = sessionGetRootFolder;
	this.getObjectFactory = sessionGetObjectFactory;
	this.getObject = sessionGetObject;
	this.getObjectByPath = sessionGetObjectByPath;
}

function sessionGetInternalObject() {
	return this.internalSession;
}

function sessionGetRepositoryInfo() {
	var internalRepositoryInfo = this.internalSession.getRepositoryInfo();
	return new RepositoryInfo(internalRepositoryInfo);
}

function sessionGetRootFolder() {
	var internalRootFolder = this.internalSession.getRootFolder();
	return new Folder(internalRootFolder);
}

function sessionGetObjectFactory() {
	var internalObjectFactory = this.internalSession.getObjectFactory();
	return new ObjectFactory(internalObjectFactory);
}

function sessionGetObject(objectId) {
	var internalCmisObject = this.internalSession.getObject(objectId);
	var type = internalCmisObject.getType().getId();
	if (type === exports.OBJECT_TYPE_DOCUMENT) {
		return new Document(internalCmisObject);
	} else if (type === exports.OBJECT_TYPE_FOLDER) {
		return new Folder(internalCmisObject);
	}
	throw new Error("Unsupported CMIS object type: " + type);
}

function sessionGetObjectByPath(path) {
	var internalCmisObject = this.internalSession.getObjectByPath(path);
	var type = internalCmisObject.getType().getId();
	if (type === exports.OBJECT_TYPE_DOCUMENT) {
		return new Document(internalCmisObject);
	} else if (type === exports.OBJECT_TYPE_FOLDER) {
		return new Folder(internalCmisObject);
	}
	throw new Error("Unsupported CMIS object type: " + type);
}


/**
 * RepositoryInfo object
 */
function RepositoryInfo(internalRepositoryInfo) {
	this.internalRepositoryInfo = internalRepositoryInfo;
	this.getInternalObject = repositoryInfoGetInternalObject;
	this.getId = repositoryInfoGetId;
	this.getName = repositoryInfoGetName;
}

function repositoryInfoGetInternalObject() {
	return this.internalRepositoryInfo;
}

function repositoryInfoGetId() {
	return this.internalRepositoryInfo.getId();
}

function repositoryInfoGetName() {
	return this.internalRepositoryInfo.getName();
}

/**
 * Folder object
 */
function Folder(internalFolder) {
	this.internalFolder = internalFolder;
	this.getInternalObject = folderGetInternalObject;
	this.getId = folderGetId;
	this.getName = folderGetName;
	this.createFolder = folderCreateFolder;
	this.createDocument = folderCreateDocument;
	this.getChildren = folderGetChildren;
	this.getPath = folderGetPath;
	this.isRootFolder = folderIsRootFolder;
	this.getFolderParent = folderGetFolderParent;
	this.delete = folderDelete;
	this.rename = folderRename;
}

function folderGetInternalObject() {
	return this.internalFolder;
}

function folderGetId() {
	return this.internalFolder.getId();
}

function folderGetName() {
	return this.internalFolder.getName();
}

function folderCreateFolder(properties) {
	var map = new java.util.HashMap();
	for (var property in properties) {
	    if (properties.hasOwnProperty(property)) {
	        map.put(property, properties[property]);
	    }
	}

	var newFolder = this.internalFolder.createFolder(map);
	return new Folder(newFolder);
}

var getInternalVersioningState = function(state) {
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

function folderCreateDocument(properties, contentStream, versioningState) {
	var map = new java.util.HashMap();
	for (var property in properties) {
	    if (properties.hasOwnProperty(property)) {
	        map.put(property, properties[property]);
	    }
	}

	var newDocument = this.internalFolder.createDocument(map, contentStream.getInternalObject(), getInternalVersioningState(versioningState));
	return new Document(newDocument);
}

function folderGetChildren() {
	var children = [];
	var internalChildren = this.internalFolder.getChildren();
	var iter = internalChildren.iterator();
	while (iter.hasNext()) {
		var internalCmisObject = iter.next();
		var child = new CmisObject(internalCmisObject);
		children.push(child);
	}
	return children;
}

function folderGetPath() {
	return this.internalFolder.getPath();
}

function folderIsRootFolder() {
	return this.internalFolder.isRootFolder();
}

function folderGetFolderParent() {
	var internalParentFolder = this.internalFolder.getFolderParent();
	return new Folder(internalParentFolder);
}

function folderDelete() {
	return this.internalFolder.delete(true);
}

function folderRename(newName) {
	return this.internalFolder.rename(newName, true);
}


/**
 * CmisObject object
 */
function CmisObject(internalCmisObject) {
	this.internalCmisObject = internalCmisObject;
	this.getInternalObject = cmisObjectGetInternalObject;
	this.getId = cmisObjectGetId;
	this.getName = cmisObjectGetName;
	this.getType = cmisObjectGetType;
	this.delete = cmisObjectDelete;
	this.rename = cmisObjectRename;
}

function cmisObjectGetInternalObject() {
	return this.internalCmisObject;
}

function cmisObjectGetId() {
	return this.internalCmisObject.getId();
}

function cmisObjectGetName() {
	return this.internalCmisObject.getName();
}

function cmisObjectGetType() {
	return this.internalCmisObject.getType().getId();
}

function cmisObjectDelete() {
	return this.internalCmisObject.delete(true);
}

function cmisObjectRename(newName) {
	return this.internalCmisObject.rename(newName, true);
}


/**
 * ObjectFactory object
 */
function ObjectFactory(internalObjectFactory) {
	this.internalObjectFactory = internalObjectFactory;
	this.getInternalObject = objectFactoryGetInternalObject;
	this.createContentStream = objectFactoryCreateContentStream;
}

function objectFactoryGetInternalObject() {
	return this.internalObjectFactory;
}

function objectFactoryCreateContentStream(filename, length, mimetype, inputStream) {
	var internalContentStream = this.internalObjectFactory.createContentStream(filename, length, mimetype, inputStream.getInternalObject());
	return new ContentStream(internalContentStream);
}

/**
 * ContentStream object
 */
function ContentStream(internalContentStream) {
	this.internalContentStream = internalContentStream;
	this.getInternalObject = contentStreamGetInternalObject;
	this.getStream = contentStreamGetStream;
}

function contentStreamGetInternalObject() {
	return this.internalContentStream;
}

function contentStreamGetStream() {
	var internalStream = this.internalContentStream.getStream();
	return new streams.InputStream(internalStream);
}


/**
 * Document object
 */
function Document(internalDocument) {
	this.internalDocument = internalDocument;
	this.getInternalObject = documentGetInternalObject;
	this.getId = documentGetId;
	this.getName = documentGetName;
	this.delete = documentDelete;
	this.getContentStream = documentGetContentStream;
	this.rename = documentRename;
}

function documentGetInternalObject() {
	return this.internalDocument;
}

function documentGetId() {
	return this.internalDocument.getId();
}

function documentGetName() {
	return this.internalDocument.getName();
}

function documentDelete() {
	return this.internalDocument.delete(true);
}

function documentRename(newName) {
	return this.internalDocument.rename(newName, true);
}


function documentGetContentStream() {
	var internalContentStream = this.internalDocument.getContentStream();
	if (internalContentStream !== null) {
		return new ContentStream(internalContentStream);
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
