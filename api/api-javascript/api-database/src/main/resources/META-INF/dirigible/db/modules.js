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
	const database = require('db/v4/database');
	return database;
};

exports.getQuery = function() {
	const query = require('db/v4/query');
	return query;
};

exports.getUpdate = function() {
	const update = require('db/v4/update');
	return update;
};

exports.getSequence = function() {
	const sequence = require('db/v4/sequence');
	return sequence;
};

exports.getDao = function() {
	const dao = require('db/v4/dao');
	return dao;
};

exports.getSql = function() {
	const sql = require('db/v4/sql');
	return sql;
};
