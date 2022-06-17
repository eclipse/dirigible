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
var upload = require('http/v3/upload');
var request = require('http/v3/request');
var response = require('http/v3/response');

if (request.getMethod() === "POST") {
	if (upload.isMultipartContent()) {
		var fileItems = upload.parseRequest();
		for (i=0; i<fileItems.size(); i++) {
			var fileItem = fileItems.get(i);
			if (!fileItem.isFormField()) {
				response.println("File Name: " + fileItem.getName());
				response.println("File Bytes (as text): " + String.fromCharCode.apply(null, fileItem.getBytes()));
			} else {
				response.println("Field Name: " + fileItem.getFieldName());
				response.println("Field Text: " + fileItem.getText());
			}
		}
	} else {
		response.println("The request's content must be 'multipart'");
	}
} else if (request.getMethod() === "GET") {
	response.println("Use POST request.");
}

response.flush();
response.close();
