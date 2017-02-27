#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
(function(){
"use strict";

var CommentsORM = {
	dbName: "${fileNameNoExtension.toUpperCase()}_COMMENT",
	properties: [
		{
			name: "id",
			dbName: "${fileNameNoExtension.toUpperCase()}C_ID",
			id: true,
			required: true,
			type: "Long"
		},{
			name: "boardId",
			dbName: "${fileNameNoExtension.toUpperCase()}C_${fileNameNoExtension.toUpperCase()}B_ID",
			required: true,
			type: "Long"
		},{
			name: "replyToCommentId",
			dbName: "${fileNameNoExtension.toUpperCase()}C_REPLY_TO_${fileNameNoExtension.toUpperCase()}C_ID",
			type: "Long",
			dbValue: function(name, entity){
				return entity.replyToCommentId !==undefined ? entity.replyToCommentId : null;//TODO: Fixme as soon as all -1 entries are updated to null. Will work with null isntead of -1
			},
			value: function(dbValue){
				return dbValue === null || dbValue<1 ? undefined : dbValue;//TODO: Fixme as soon as all -1 entries are updated to null. Will work with null isntead of -1
			},
		},{
			name: "text",
			dbName: "${fileNameNoExtension.toUpperCase()}C_COMMENT_TEXT",
			type: "String",
			size: 10000
		},{
			name: "publishTime",
			dbName: "${fileNameNoExtension.toUpperCase()}C_PUBLISH_TIME",
			required: true,
			type: "Long",
			dbValue: function(name, entity){
				return entity.publishTime !== undefined ? new Date(entity.publishTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			},
			allowedOps: ['insert']
		},{
			name: "lastModifiedTime",
			dbName: "${fileNameNoExtension.toUpperCase()}C_LASTMODIFIED_TIME",
			type: "Long",
			dbValue: function(name, entity){
				return entity.lastModifiedTime !== undefined ? new Date(entity.lastModifiedTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			}
		},{
			name: "user",
			dbName: "${fileNameNoExtension.toUpperCase()}C_USER",
			type: "String",
			size: 100,
			dbValue: function(name, entity){
				return require("net/http/user").getName();
			}
		}	
	],
	associationSets: {
		replies: {
			joinKey: "replyToCommentId",
			associationType: 'one-to-many',
			defaults: {
				flat:true
			}
		},
		board: {
			dao: require("${packageName}/lib/board_dao").get,
			associationType: 'many-to-one',
			joinKey: "boardId"
		}
	}
};

var DAO = require('daoism/dao').DAO;
var CommentDAO  = exports.CommentDAO = function(orm){
	orm = orm || CommentsORM;
	DAO.call(this, orm, 'Comment DAO');
};
CommentDAO.prototype = Object.create(DAO.prototype);
CommentDAO.prototype.constructor = CommentDAO;

exports.get = function(){
	var dao = new CommentDAO(CommentsORM);
	return dao;
};

})();
