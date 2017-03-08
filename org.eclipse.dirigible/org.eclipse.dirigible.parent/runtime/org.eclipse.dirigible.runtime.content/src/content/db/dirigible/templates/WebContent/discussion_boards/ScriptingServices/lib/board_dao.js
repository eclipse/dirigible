#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
(function(){
"use strict";

var BoardsORM = {
	dbName: "${fileNameNoExtension.toUpperCase()}_BOARD",
	properties: [
		{
			name: "id",
			dbName: "${fileNameNoExtension.toUpperCase()}B_ID",
			id: true,
			required: true,
			type: "Long"
		},{
			name: "shortText",
			dbName: "${fileNameNoExtension.toUpperCase()}B_SHORT_TEXT",
			type: "String",
			size: 500
		},{
			name: "description",
			dbName: "${fileNameNoExtension.toUpperCase()}B_DESCRIPTION",
			type: "String",
			size: 10000
		},{
			name: "publishTime",
			dbName: "${fileNameNoExtension.toUpperCase()}B_PUBLISH_TIME",
			required: true,
			type: "Long",
			dbValue: function(publishTime){
				return publishTime !== undefined ? new Date(publishTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			},
			allowedOps: ['insert']
		},{
			name: "lastModifiedTime",
			dbName: "${fileNameNoExtension.toUpperCase()}B_LASTMODIFIED_TIME",
			type: "Long",
			dbValue: function(lastModifiedTime){
				return lastModifiedTime !== undefined ? new Date(lastModifiedTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			}
		},{
			name: "status",
			dbName: "${fileNameNoExtension.toUpperCase()}B_STATUS",
			type: "String",
			size:255
		},{
			name: "visits",
			dbName: "${fileNameNoExtension.toUpperCase()}B_VISITS",
			type: "Long"
		},{
			name: "locked",
			dbName: "${fileNameNoExtension.toUpperCase()}B_LOCKED",
			type: "Short",
			dbValue: function(locked){
				return locked ? 1 : 0;
			},
			value: function(dbValue){
				return dbValue>0 ? true : false;
			}
		},{
			name: "user",
			dbName: "${fileNameNoExtension.toUpperCase()}B_USER",
			type: "String",
			size: 255
		}	
	],
	associations: [{
			name: 'comments',
			targetDao: require("${packageName}/lib/comment_dao").get,
			joinKey: "boardId",
			type: "one-to-many",
			defaults: {
				flat:false
			}
		}, {
			name: 'tagRefs',
			targetDao: require("${packageName}/lib/board_tags_dao").get,
			joinKey: "boardId",
			type: "one-to-many"
		}, {
			name: 'tags',
			joinDao: require("${packageName}/lib/board_tags_dao").get,
			targetDao: require("annotations/lib/tags_dao").get,
			joinKey: "boardId",
			type: "many-to-many"
		}, {
			name: 'votes',
			targetDao: require("${packageName}/lib/board_votes_dao").get,
			joinKey: "boardId",
			type: "one-to-many"
		}]
};

var DAO = require('daoism/dao').DAO;
var BoardDAO  = exports.BoardDAO = function(orm){
	orm = orm || BoardsORM;
	DAO.call(this, orm, 'BoardDAO');
};
BoardDAO.prototype = Object.create( DAO.prototype );

BoardDAO.prototype.visit = function(boardId){
	this.${D}log.info('Updating '+this.orm.dbName+'['+boardId+'] entity visits');
	var connection = this.datasource.getConnection();
	var qb;
    try {
    	var visitsField = this.orm.getProperty('visits');
    	qb = this.ormstatements.builder().update().table(this.orm.dbName)
    		.set(visitsField, visitsField.dbName+'+1')
    		.where(this.orm.getPrimaryKey().dbName + '=?', [this.orm.getPrimaryKey()]);
    	var params = {}
    	params[this.orm.getPrimaryKey().name] = boardId;
    	this.ormstatements.execute(qb, connection, params);
        this.${D}log.info(this.orm.dbName+'['+boardId+'] entity visits updated');
        return this;
        
    } catch(e) {
		e.errContext = qb.toString();
		throw e;
    } finally {
        connection.close();
    }
};

const TAGS_NAMESPACE = "dboard";

BoardDAO.prototype.setTags = function(id, tags, createOnDemand){
	var tagRefsDAO = this.orm.getAssociation('tagRefs').targetDao();
	this.${D}log.info('Updating ' + tagRefsDAO.orm.dbName +' entity relations to '+this.orm.getPrimaryKey().dbName+'[' +  id + '] entity');
	//First, clear all existing tag references for this board
	var listSettings = {};
	listSettings[this.orm.getAssociation('tagRefs').joinKey] = id;
	var boardTags = tagRefsDAO.list(listSettings);
	if(boardTags){
		var connection = this.datasource.getConnection();
		try{ 
			for(var i=0; i < boardTags.length; i++){
				this.${D}log.info('Removing '+tagRefsDAO.orm.dbName+' entity relation between '+this.orm.getPrimaryKey().dbName+'['+id+'] entity and ANN_TAG['+boardTags[i].id+']');
				var qb = this.ormstatements.builder().remove().from(tagRefsDAO.orm.dbName)
						.where(tagRefsDAO.orm.getProperty('boardId').dbName + '=?', [tagRefsDAO.orm.getProperty('boardId')])
						.where(tagRefsDAO.orm.getProperty('tagId').dbName + '=?', [tagRefsDAO.orm.getProperty('tagId')]);
				var params={};
				params[tagRefsDAO.orm.getProperty('boardId').name] = boardTags[i][tagRefsDAO.orm.getProperty('boardId').name];
				params[tagRefsDAO.orm.getProperty('tagId').name] = boardTags[i][tagRefsDAO.orm.getProperty('tagId').name];
				var updatedRecordsCount = this.ormstatements.execute(qb, connection, params);
				//tagRefsDAO.remove(boardTags[i][tagRefsDAO.orm.getPrimaryKey().name]);
			    this.${D}log.info(tagRefsDAO.orm.dbName + ' entity relation between '+this.orm.getPrimaryKey().dbName+'['+id+'] entity and ANN_TAG['+boardTags[i].id+'] removed');
			}
			this.${D}log.info(boardTags.length + ' '+tagRefsDAO.orm.dbName+' entity relations to '+this.orm.getPrimaryKey().dbName+'[' +  id+ '] entity removed');
		} finally {
	        connection.close();
	    }
	}
	//Now, find the request tag records and add references from this board to them
	var tagsDAO = this.orm.getAssociation('tags').targetDao();
	for(var i=0; i < tags.length; i++){
		if(tags[i]!==null || tags[i]!==undefined){
			var tagEntity = tagsDAO.list({
				'defaultLabel': tags[i],
				"namespace": TAGS_NAMESPACE
			})[0];
			var tagId = tagEntity && tagEntity[tagsDAO.orm.getPrimaryKey().name];
			if(!tagEntity && createOnDemand){
				tagId = tagsDAO.insert({
										"defaultLabel": tags[i],
										"uri": tags[i],
										"namespace": TAGS_NAMESPACE
									});								
			}
			var entity = {};
			entity['boardId'] = id;
			entity['tagId'] = tagId;
			var boardTagId = tagRefsDAO.insert(entity);
		
	    	this.${D}log.info(tagRefsDAO.orm.dbName+'[' +  boardTagId + '] entity relation '+tagsDAO.orm.dbName+'['+tagId+'] entity and '+this.orm.dbName+'['+id+'] entity inserted');
		}
	}
};

exports.get = function(){
	var boardDAO = new BoardDAO(BoardsORM);
	return boardDAO;
};

})();
