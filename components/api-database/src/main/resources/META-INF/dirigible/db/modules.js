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
exports.getDatabase = function() {
	const database = require('db/database');
	return database;
};

exports.getQuery = function() {
	const query = require('db/query');
	return query;
};

exports.getUpdate = function() {
	const update = require('db/update');
	return update;
};

exports.getSequence = function() {
	const sequence = require('db/sequence');
	return sequence;
};

exports.getDao = function() {
	const dao = require('db/dao');
	return dao;
};

exports.getSql = function() {
	const sql = require('db/sql');
	return sql;
};
