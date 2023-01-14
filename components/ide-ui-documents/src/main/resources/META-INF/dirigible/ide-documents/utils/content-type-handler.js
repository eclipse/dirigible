/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let extensions = require('extensions/extensions');

exports.getContentTypeBeforeUpload = function (fileName, contentType) {
	let extension = getContentTypeExtension();
	if (extension !== null) {
		return extension.getContentTypeBeforeUpload(fileName, contentType);
	}
	return contentType;
};

exports.getContentTypeBeforeDownload = function (fileName, contentType) {
	let extension = getContentTypeExtension();
	if (extension !== null) {
		return extension.getContentTypeBeforeDownload(fileName, contentType);
	}
	return contentType;
};

function getContentTypeExtension() {
	let contentTypeExtensions = extensions.getExtensions('ide-documents-content-type');
	if (contentTypeExtensions !== null && contentTypeExtensions.length > 0) {
		return require(contentTypeExtensions[0]);
	}
	return null;
}