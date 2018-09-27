"use strict";

var rsdata = require('http/v3/rs-data');

/**
 * Factory function for Comments data service instances.
 */
exports.create = function(){
	var commentsDao = require("ide-discussions/lib/comment_dao").create();
	var commentsSvc = rsdata.service(undefined, undefined, undefined, 'ide-discussions.svc.CommentsService').dao(commentsDao);
	commentsSvc.mappings().query().postQuery = function(entities, context){
		var asDiscussionThread = (typeof context.queryParams.thread === 'string' && (context.queryParams.thread === 'true' || context.queryParams.thread.length===0)) ||  ((typeof context.queryParams.thread === 'boolean') ? true : false) ? true : false;
		if(asDiscussionThread && context.queryParams.$expand && context.queryParams.$expand.indexOf('replies')>-1){
			entities = entities.filter(function(entity){
				return entity.replyToCommentId===undefined || entity.replyToCommentId===0;//?
			});
		}
		return entities;
	};
	commentsSvc.mappings().create().onEntityInsert(function(dbEntity){
		dbEntity.lastModifiedTime = dbEntity.publishTime = Date.now();
		dbEntity.replyToId = dbEntity.replyToCommentId;
	});
	commentsSvc.mappings().update().onEntityUpdate(function(dbEntity){
		dbEntity.lastModifiedTime = Date.now();
		dbEntity.replyToId = dbEntity.replyToCommentId;
	});

	
	return commentsSvc;
};
