/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
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

exports.getSequence = function() {
	var sequence = require('db/v3/sequence');
	return sequence;
};

exports.getDao = function() {
	var dao = require('db/v3/dao');
	return dao;
};

exports.getSql = function() {
	var sql = require('db/v3/sql');
	return sql;
};
