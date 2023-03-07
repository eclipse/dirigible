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
let repositoryManager = require("platform/repository");
let documentsUtils = require("ide-documents/utils/cmis/document");
let folderUtils = require("ide-documents/utils/cmis/folder");
let objectUtils = require("ide-documents/utils/cmis/object");
let bytes = require("io/bytes");
let streams = require("io/streams");

const INTERNAL_FOLDER = "__internal";
const INTERNAL_FOLDER_LOCATION = INTERNAL_FOLDER;
const ACCESS_CONSTRAINTS_FILE = "roles-access.json";
const ACCESS_CONSTRAINTS_FILE_LOCATION = INTERNAL_FOLDER + "/" + ACCESS_CONSTRAINTS_FILE;

function updateAccessDefinitionsInCMS(data) {
	
    if (!folderUtils.existFolder(INTERNAL_FOLDER_LOCATION)) {
        let rootFolder = folderUtils.getFolderOrRoot("/");
        folderUtils.createFolder(rootFolder, INTERNAL_FOLDER);
    }
    var folder = folderUtils.getFolder(INTERNAL_FOLDER_LOCATION);
    let document = {
        getName: function () {
            return ACCESS_CONSTRAINTS_FILE;
        },
        getContentType: function () {
            return "application/json";
        },
        getSize: function () {
            return data.length;
        },
        getInputStream: function () {
            let contentBytes = bytes.textToByteArray(data);
            return streams.createByteArrayInputStream(contentBytes);
        }
    }

		
	if (objectUtils.existObject(ACCESS_CONSTRAINTS_FILE_LOCATION)) {
		let object = objectUtils.getObject(ACCESS_CONSTRAINTS_FILE_LOCATION);
        objectUtils.deleteObject(object);
    }
    
    documentsUtils.uploadDocument(folder, document);
}

exports.getAccessDefinitions = function () {
	let content = {
        constraints: []
    };
	if (documentsUtils.existDocument(ACCESS_CONSTRAINTS_FILE_LOCATION)) {
	    let document = documentsUtils.getDocument(ACCESS_CONSTRAINTS_FILE_LOCATION);
	    
	    try {
	        let inputStream = documentsUtils.getDocumentStream(document);
	        let data = inputStream.getStream().readBytes();
	        content = JSON.parse(bytes.byteArrayToText(data));
	    } catch (e) {
	        // Do nothing
	    }
    }
    return content;
};

exports.updateAccessDefinitions = function (accessDefinitions) {
    let path = "/registry/public/ide-documents/security/roles.access";
    let content = JSON.stringify(accessDefinitions);
    let resource = repositoryManager.getResource(path);
    if (resource.exists()) {
        repositoryManager.deleteResource(path);
    }
    repositoryManager.createResource(path, content);
    updateAccessDefinitionsInCMS(content);
};