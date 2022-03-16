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

let repositoryManager = require("platform/v4/repository");
let documentsUtils = require("ide-documents/utils/cmis/document");
let folderUtils = require("ide-documents/utils/cmis/folder");
let objectUtils = require("ide-documents/utils/cmis/object");
let bytes = require("io/v4/bytes");
let streams = require("io/v4/streams");

const INTERNAL_FOLDER = "__internal";
const INTERNAL_FOLDER_LOCATION = "/" + INTERNAL_FOLDER;
const ACCESS_CONSTRAINTS_FILE = "roles-access.json";
const ACCESS_CONSTRAINTS_FILE_LOCATION = "/" + INTERNAL_FOLDER + "/" + ACCESS_CONSTRAINTS_FILE;

function updateAccessDefinitionsInCMS(data) {
    let folder = folderUtils.getFolder(INTERNAL_FOLDER_LOCATION);
    if (!folder) {
        let rootFolder = folderUtils.getFolderOrRoot("/");
        folderUtils.createFolder(rootFolder, INTERNAL_FOLDER);
        folder = folderUtils.getFolder(INTERNAL_FOLDER_LOCATION);
    }
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

    let object = objectUtils.getObject(ACCESS_CONSTRAINTS_FILE_LOCATION);
    if (object) {
        objectUtils.deleteObject(object);
    }
    documentsUtils.uploadDocument(folder, document);
}

exports.getAccessDefinitions = function () {
    let document = documentsUtils.getDocument(ACCESS_CONSTRAINTS_FILE_LOCATION);
    let content = {
        constraints: []
    };

    try {
        let inputStream = documentsUtils.getDocumentStream(document);
        let data = inputStream.getStream().readBytes();
        content = JSON.parse(bytes.byteArrayToText(data));
    } catch (e) {
        // Do nothing
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