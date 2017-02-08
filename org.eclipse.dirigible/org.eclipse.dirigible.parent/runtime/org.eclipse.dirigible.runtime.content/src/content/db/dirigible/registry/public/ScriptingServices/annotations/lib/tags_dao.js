/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var TagORM = {
	dbName: "ANN_TAG",
	properties: [
		{
			name: "id",
			dbName: "ANN_ID",
			id: true,
			required: true,
			type: "Long"
		},{ 
			name: "defaultLabel",
			dbName: "ANN_DEFAULT_LABEL",
			type: "String",
			size: 100,
		},{ 
			name: "description",
			dbName: "ANN_DESCR",
			type: "String",
			size: 200,
		},{ 
			name: "namespace",
			dbName: "ANN_NS",
			type: "String",
			size: 100,
		},{
			name: "uri",
			dbName: "ANN_URI",
			type: "String",
			size: 3000,
		}	
	]
};

exports.get = function(){
	return require('daoism/dao').get(TagORM);
};
