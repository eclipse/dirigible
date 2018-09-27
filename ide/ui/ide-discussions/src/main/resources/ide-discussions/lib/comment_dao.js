"use strict";

var CommentsORM = exports.CommentsORM = {
	table: "DIRIGIBLE_DISCUSSIONS_COMMENT",
	properties: [
		{
			name: "id",
			column: "DISC_ID",
			id: true,
			required: true,
			type: "BIGINT"
		},{
			name: "boardId",
			column: "DISC_DISB_ID",
			required: true,
			type: "BIGINT"
		},{
			name: "replyToCommentId",
			column: "DISC_REPLY_TO_DISC_ID",
			type: "BIGINT",
			dbValue: function(replyToCommentId){ 
				return replyToCommentId !==undefined ? replyToCommentId : null;
			},
			value: function(dbValue){
				return dbValue === null ? undefined : dbValue;
			},
		},{
			name: "text",
			column: "DISC_COMMENT_TEXT",
			type: "VARCHAR",
			size: 4000
		},{
			name: "publishTime",
			column: "DISC_PUBLISH_TIME",
			required: true,
			type: "BIGINT",
			dbValue: function(publishTime){
				return publishTime !== undefined ? new Date(publishTime).getTime() : Date.now();
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			},
			allowedOps: ['insert']
		},{
			name: "lastModifiedTime",
			column: "DISC_LASTMODIFIED_TIME",
			type: "BIGINT",
			dbValue: function(lastModifiedTime){
				return lastModifiedTime !== undefined ? new Date(lastModifiedTime).getTime() : null;
			},
			value: function(dbValue){
				return dbValue !== null ? new Date(dbValue).toISOString() : undefined;
			}
		},{
			name: "user",
			column: "DISC_USER",
			type: "VARCHAR",
			size: 100,
			dbValue: function(user){
				return require("security/v3/user").getName();
			}
		}	
	],
	associations: [{
			name: 'replies',
			joinKey: "replyToCommentId",
			type: 'one-to-many',
			defaults: {
				flat:true
			}
		},{
			name: 'board',
			targetDao: require("ide-discussions/lib/board_dao").create,
			type: 'many-to-one',
			joinKey: "boardId"
		}]
};

var daos = require('db/v3/dao');

/**
 * Factory function for DAO instances for comments based on CommentsORM definition.
 */
exports.create = function(){
	return daos.dao(CommentsORM, 'ide-discussions.dao.CommentDAO');
};
