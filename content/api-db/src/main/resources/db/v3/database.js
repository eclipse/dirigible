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

var java = require('core/v3/java');

exports.listDatabaseTypes = function() {
	var types = java.call('org.eclipse.dirigible.api.v3.db.DatabasesFacade', 'listDatabaseTypes', []);
	return types;
};

exports.listDataSources = function(databaseType) {
	var datasources = java.call('org.eclipse.dirigible.api.v3.db.DatabasesFacade', 'listDataSources', [databaseType]);
	return datasources;
};

exports.listArtifacts = function(databaseType, datasourceName) {
	var artifacts = java.call('org.eclipse.dirigible.api.v3.db.DatabasesFacade', 'listArtifacts', [databaseType, datasourceName]);
	return artifacts;
};

exports.query = function(databaseType, datasourceName, sql, parameters) {
	var resultset = java.call('org.eclipse.dirigible.api.v3.db.DatabasesFacade', 'query', [databaseType, datasourceName]);
	return resultset;
};

exports.update = function(databaseType, datasourceName, sql, parameters) {
	var result = java.call('org.eclipse.dirigible.api.v3.db.DatabasesFacade', 'update', [databaseType, datasourceName]);
	return result;
};
