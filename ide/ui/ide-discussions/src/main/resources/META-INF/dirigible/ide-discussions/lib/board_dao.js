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

var BoardsORM = exports.BoardsORM  = {
	"table": "DIRIGIBLE_DISCUSSIONS_BOARD",
	"properties": [
		{
			name: "id",
			column: "DISB_ID",
			id: true,
			required: true,
			type: "BIGINT"
		},{
			name: "shortText",
			column: "DISB_SHORT_TEXT",
			type: "VARCHAR",
			size: 500
		},{
			name: "description",
			column: "DISB_DESCRIPTION",
			type: "VARCHAR",
			size: 4000
		},{
			name: "publishTime",
			column: "DISB_PUBLISH_TIME",
			required: true,
			type: "BIGINT",
			dbValue: function(publishTime){
				return publishTime !== undefined ? new Date(publishTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			},
			allowedOps: ['insert']
		},{
			name: "lastModifiedTime",
			column: "DISB_LASTMODIFIED_TIME",
			type: "BIGINT",
			dbValue: function(lastModifiedTime){
				return lastModifiedTime !== undefined ? new Date(lastModifiedTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			}
		},{
			name: "status",
			column: "DISB_STATUS",
			type: "VARCHAR",
			size:255
		},{
			name: "visits",
			column: "DISB_VISITS",
			type: "BIGINT"
		},{
			name: "locked",
			column: "DISB_LOCKED",
			type: "SMALLINT",
			dbValue: function(locked){
				return locked ? 1 : 0;
			},
			value: function(dbValue){
				return dbValue>0 ? true : false;
			}
		},{
			name: "user",
			column: "DISB_USER",
			type: "VARCHAR",
			size: 255
		}	
	],
	associations: [{
			name: 'comments',
			targetDao: require("ide-discussions/lib/comment_dao").create,
			joinKey: "boardId",
			type: "one-to-many",
			defaults: {
				flat:false
			}
		}, {
			name: 'tagRefs',
			targetDao: require("ide-discussions/lib/board_tags_dao").create,
			joinKey: "boardId",
			type: "one-to-many"
		}, {
			name: 'tags',
			joinDao: require("ide-discussions/lib/board_tags_dao").create,
			targetDao: require("ide-discussions/lib/tags_dao").create,
			joinKey: "boardId",
			type: "many-to-many"
		}, {
		    name: 'votes',
			targetDao: require("ide-discussions/lib/board_votes_dao").create,
			joinKey: "boardId",
			type: "one-to-many"
		}]
};

var visit = function(boardId){
	this.$log.info('Updating {}}[{}] entity visits', this.orm.table, boardId);
	var qb;
    try {
    	var visitsField = this.orm.getProperty('visits');    	
    	qb = require('db/v4/sql').getDialect()
    		.update()
    		.table(this.orm.table)
    		.set(visitsField.column, visitsField.column+'+1')
    		.where(this.orm.getPrimaryKey().column + '=?', [this.orm.getPrimaryKey()]);

    	var params = {}
    	params[this.orm.getPrimaryKey().name] = parseInt(boardId,10);

    	var updatedRecordCount = this.execute(qb, params);
    	
        this.$log.info('{}[{}] entity visits updated', this.orm.table, boardId);
        return this;
        
    } catch(e) {
		e.errContext = qb.toString();
		throw e;
    }
};

const TAGS_NAMESPACE = "dboard";

var setTags = function(id, tags, createOnDemand){
	var tagRefsDAO = this.orm.getAssociation('tagRefs').targetDao();
	this.$log.info('Updating {}} entity relations to {}[{}] entity',  tagRefsDAO.orm.table,  this.orm.getPrimaryKey().column, id);
	//First, clear all existing tag references for this board
	var listSettings = {};
	listSettings[this.orm.getAssociation('tagRefs').joinKey] = id;
	var boardTags = tagRefsDAO.list(listSettings);
	if(boardTags){
		for(var i=0; i < boardTags.length; i++){
			this.$log.info('Removing {} entity relation between {}[{}] entity and TAG_TAG[{}]', tagRefsDAO.orm.table, this.orm.getPrimaryKey().column, id, boardTags[i].id);
			var qb =  require('db/v4/sql').getDialect()
						.delete()
						.from(tagRefsDAO.orm.table)
						.where(tagRefsDAO.orm.getProperty('boardId').column + '=?', [tagRefsDAO.orm.getProperty('boardId')])
						.where(tagRefsDAO.orm.getProperty('tagId').column + '=?', [tagRefsDAO.orm.getProperty('tagId')]);
			var parameterBindings = {};
			parameterBindings[tagRefsDAO.orm.getProperty('boardId').name] = boardTags[i][tagRefsDAO.orm.getProperty('boardId').name];
			parameterBindings[tagRefsDAO.orm.getProperty('tagId').name] = boardTags[i][tagRefsDAO.orm.getProperty('tagId').name];
			var updatedRecordsCount = this.execute(qb, parameterBindings);
			//tagRefsDAO.remove(boardTags[i][tagRefsDAO.orm.getPrimaryKey().name]);
			if(updatedRecordsCount > 0)
		    	this.$log.info('{} entity relation between {}[{}] entity and TAG_TAG[{}] removed', tagRefsDAO.orm.table, this.orm.getPrimaryKey().column, id, boardTags[i].id);
		}
		this.$log.info('{} {} entity relations to {}[{}] entity removed', boardTags.length, tagRefsDAO.orm.table, this.orm.getPrimaryKey().column, id); 
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
		
	    	this.$log.info('{}[{}] entity relation {}[{}] entity and {}[{}] entity inserted', tagRefsDAO.orm.table, boardTagId, tagsDAO.orm.table, tagId, this.orm.table, id);
		}
	}
};


/**
 * Factory function for DAO instances for boards based on BoardsORM definition.
 */
exports.create = function(){
	var boardDAO = require('db/v4/dao').create(BoardsORM, 'BoardsDAO');
	boardDAO.visit = visit.bind(boardDAO);
	boardDAO.setTags = setTags.bind(boardDAO);
	return boardDAO;
};
