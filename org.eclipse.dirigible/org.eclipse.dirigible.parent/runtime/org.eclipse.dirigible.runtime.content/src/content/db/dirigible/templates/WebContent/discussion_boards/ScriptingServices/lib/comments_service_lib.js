#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
"use strict";

var DataService = require('arestme/data_service').DataService;

var CommentsDataService = function(){
	var commentsDAO = require("${packageName}/lib/comment_dao").get();
	DataService.call(this, commentsDAO, 'Comments Data Service');
	this.handlersProvider.onEntityInsert = function(entity){
		entity.lastModifiedTime = entity.publishTime = Date.now();
		entity.replyToId = entity.replyToCommentId;
	};
	this.handlersProvider.onEntityUpdate = function(entity){
		entity.lastModifiedTime = Date.now();
		entity.replyToId = entity.replyToCommentId;
	};
	this.handlersProvider.postQuery = function(entities, context){
		var asDiscussionThread = (typeof context.queryParams.thread === 'string' && (context.queryParams.thread === 'true' || context.queryParams.thread.length===0)) ||  ((typeof context.queryParams.thread === 'boolean') ? true : false) ? true : false;
		if(asDiscussionThread && context.queryParams.${D}expand && context.queryParams.${D}expand.indexOf('replies')>-1){
			entities = entities.filter(function(entity){
				return entity.replyToCommentId===undefined || entity.replyToCommentId===0;//?
			});
		}
		return entities;
	};
};
CommentsDataService.prototype = Object.create(DataService.prototype);
CommentsDataService.prototype.constructor = CommentsDataService;

exports.CommentsDataService = CommentsDataService;
exports.get = function(){
	return new CommentsDataService();
};
