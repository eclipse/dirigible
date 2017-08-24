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

exports.getDatabase = function() {
	var database = require('db/v3/database');
	return database;
};

exports.getQuery = function() {
	var query = require('db/v3/query');
	return query;
};

exports.getUpdate = function() {
	var update = require('db/v3/update');
	return update;
};