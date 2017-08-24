/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

exports.getBase64 = function() {
	var base64 = require('utils/v3/base64');
	return base64;
};

exports.getUuid = function() {
	var uuid = require('utils/v3/uuid');
	return uuid;
};

exports.getXml = function() {
	var xml = require('utils/v3/xml');
	return xml;
};