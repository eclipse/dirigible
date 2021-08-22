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
"use strict";

var tagsORMDef = exports.tagsORMDef = {
	table: "DIRIGIBLE_DISCUSSIONS_TAGS",
	properties: [
		{
			name: "id",
			column: "TAG_ID",
			id: true,
			required: true,
			type: "BIGINT"
		},{ 
			name: "defaultLabel",
			column: "TAG_DEFAULT_LABEL",
			type: "VARCHAR",
			size: 100,
		},{ 
			name: "description",
			column: "TAG_DESCR",
			type: "VARCHAR",
			size: 200,
		},{ 
			name: "namespace",
			column: "TAG_NS",
			type: "VARCHAR",
			size: 100,
		},{
			name: "uri",
			column: "TAG_URI",
			type: "VARCHAR",
			size: 255,
		}	
	]
};

var daos = require('db/dao');
exports.create = function(){
	return daos.dao(tagsORMDef, 'Tags DAO');
};
