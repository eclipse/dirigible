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