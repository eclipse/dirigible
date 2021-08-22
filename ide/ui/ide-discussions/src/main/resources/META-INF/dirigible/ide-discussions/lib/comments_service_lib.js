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

var rsdata = require('http/v4/rs-data');

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
