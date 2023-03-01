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
exports.getBytes = function() {
	var bytes = require('io/bytes');
	return bytes;
};

exports.getFiles = function() {
	var files = require('io/files');
	return files;
};

exports.getFtp = function() {
	var ftp = require('io/ftp');
	return ftp;
}

exports.getImage = function() {
	var image = require('io/image');
	return image;
};

exports.getStreams = function() {
	var streams = require('io/streams');
	return streams;
};

exports.getZip = function() {
	var zip = require('io/zip');
	return zip;
};


