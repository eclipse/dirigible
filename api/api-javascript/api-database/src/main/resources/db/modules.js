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
exports.getDatabase = function() {
	var database = require('db/v4/database');
	return database;
};

exports.getQuery = function() {
	var query = require('db/v4/query');
	return query;
};

exports.getUpdate = function() {
	var update = require('db/v4/update');
	return update;
};

exports.getSequence = function() {
	var sequence = require('db/v4/sequence');
	return sequence;
};

exports.getDao = function() {
	var dao = require('db/v4/dao');
	return dao;
};

exports.getSql = function() {
	var sql = require('db/v4/sql');
	return sql;
};
