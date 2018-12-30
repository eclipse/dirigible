/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
exports.getFiles = function() {
	var files = require('io/v3/files');
	return files;
};

exports.getStreams = function() {
	var streams = require('io/v3/streams');
	return streams;
};

exports.getZip = function() {
	var zip = require('io/v3/zip');
	return zip;
};

exports.getImage = function() {
	var image = require('io/v3/image');
	return image;
};
