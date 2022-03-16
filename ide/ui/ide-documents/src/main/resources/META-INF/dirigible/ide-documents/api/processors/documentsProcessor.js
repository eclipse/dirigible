let objectUtils = require("ide-documents/utils/cmis/object");
let folderUtils = require("ide-documents/utils/cmis/folder");
let documentUtils = require("ide-documents/utils/cmis/document");
let contentTypeHandler = require("ide-documents/utils/content-type-handler");
let registry = require("platform/v4/registry");
let { formatPath } = require("ide-documents/utils/string");
let user = require("security/v4/user");

exports.get = function (path) {
	let document = documentUtils.getDocument(path);
	let nameAndStream = documentUtils.getDocNameAndStream(document);
	let contentStream = nameAndStream[1];
	let contentType = contentStream.getMimeType();

	let result = {
		name: nameAndStream[0],
		content: nameAndStream[1],
		contentType: contentTypeHandler.getContentTypeBeforeDownload(nameAndStream[0], contentType)
	};
	return result;
};

exports.list = function (path) {
	let folder = folderUtils.getFolderOrRoot(path);
	let result = folderUtils.readFolder(folder);
	filterByAccessDefinitions(result);
	return result;
};

exports.create = function (path, documents, overwrite) {
	let result = [];
	for (let i = 0; i < documents.size(); i++) {
		let folder = folderUtils.getFolder(path);
		if (overwrite) {
			result.push(documentUtils.uploadDocumentOverwrite(folder, documents.get(i)));
		} else {
			result.push(documentUtils.uploadDocument(folder, documents.get(i)));
		}
	}
	return result;
};

exports.createFolder = function (path, name) {
	let folder = folderUtils.getFolderOrRoot(path);
	let result = folderUtils.createFolder(folder, name);
	return result;
};

exports.rename = function (path, name) {
	let object = objectUtils.getObject(path);
	objectUtils.renameObject(object, name);
};

exports.delete = function (objects, forceDelete) {
	for (let i in objects) {
		let object = objectUtils.getObject(objects[i]);
		let isFolder = object.getType().getId() === 'cmis:folder';
		if (isFolder && forceDelete) {
			folderUtils.deleteTree(object);
		} else {
			objectUtils.deleteObject(object);
		}
	}
};

function filterByAccessDefinitions(folder) {
	let accessDefinitions = JSON.parse(registry.getText("ide-documents/security/roles.access"));
	folder.children = folder.children.filter(e => {
		let path = formatPath(folder.path + "/" + e.name);
		if (path.startsWith("/__internal")) {
			return false;
		}
		return hasAccessPermissions(accessDefinitions.constraints, path);
	});
}

function hasAccessPermissions(constraints, path) {
	for (let i = 0; i < constraints.length; i++) {
		let method = constraints[i].method;
		let constraintPath = constraints[i].path;
		constraintPath = formatPath(constraintPath);
		if (constraintPath.length === 0 || (path.length >= constraintPath.length && constraintPath.startsWith(path))) {
			if (method !== null && method !== undefined && (method.toUpperCase() === "READ" || method === "*")) {
				let roles = constraints[i].roles;
				if (roles && roles.length) {
					for (let j = 0; j < roles.length; j++) {
						if (!user.isInRole(roles[j])) {
							return false;
						}
					}
				}

			}
		}
	}
	return true;
}