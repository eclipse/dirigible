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
			type: "String"
		},{ 
			name: "description",
			dbName: "ANN_DESCR",
			type: "String"
		},{ 
			name: "namespace",
			dbName: "ANN_NS",
			type: "String"
		},{
			name: "uri",
			dbName: "ANN_URI",
			type: "String"
		}	
	]
};

var DAO = require('daoism/dao').DAO;
var TagDAO  = exports.TagDAO = function(orm){
	orm = orm || TagORM;
	DAO.call(this, orm, 'Tag DAO');
};
TagDAO.prototype = Object.create( DAO.prototype );

// Reads a single entity by id, parsed into JSON object 
TagDAO.prototype.findByTagValue = function(tag, namespace) {

	this.$log.info('Finding '+this.orm.dbName+' entity with label[' + tag + ']');

	if(tag=== undefined || tag === null){
		throw new Error('Illegal argument for tag parameter:' + tag + ' and namespace: ' + namespace);
	}

    var connection = this.datasource.getConnection();
    try {
        var entity;
        var qb = this.statements.builder().select().from(this.orm.dbName).where('ANN_DEFAULT_LABEL = ?', [this.orm.getProperty('defaultLabel')]);
        if(namespace!==undefined)
        	qb.where('ANN_NS = ?', [this.orm.getProperty('namespace')]);
        var params = {};
        params['defaultLabel'] = tag;
        if(namespace)
        	params['namespace'] = namespace;
        
        var resultSet = this.statements.execute(qb, connection, params);
        if (resultSet.next()) {
        	entity = this.createEntity(resultSet);
        	this.$log.info(this.orm.dbName+'[' + entity.id + '] entity with label[' + tag + '] found');
        } else {
        	this.$log.info('No '+this.orm.dbName+' records for label[' + tag + '] not found');
        }
        return entity;
    } finally {
        connection.close();
    }
};

exports.get = function(){
	return new TagDAO(TagORM);
};
