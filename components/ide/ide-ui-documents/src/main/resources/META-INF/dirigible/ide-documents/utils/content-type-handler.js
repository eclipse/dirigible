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
import { extensions } from '@dirigible/extensions';

let contentTypeExtension = null;

const contentTypeExtensions = extensions.getExtensions('ide-documents-content-type');
if (contentTypeExtensions !== null && contentTypeExtensions.length > 0) {
	try {
		contentTypeExtension = await import(`../../${contentTypeExtensions[0]}`);
	} catch (e) {
		// Fallback for not migrated extensions
		contentTypeExtension = require(contentTypeExtensions[0]);
	}
}

export const getContentTypeBeforeUpload = (fileName, contentType) => {
	let extension = getContentTypeExtension();
	if (extension !== null) {
		return extension.getContentTypeBeforeUpload(fileName, contentType);
	}
	return contentType;
};

export const getContentTypeBeforeDownload = (fileName, contentType) => {
	let extension = getContentTypeExtension();
	if (extension !== null) {
		return extension.getContentTypeBeforeDownload(fileName, contentType);
	}
	return contentType;
};

function getContentTypeExtension() {
	return contentTypeExtension;
}