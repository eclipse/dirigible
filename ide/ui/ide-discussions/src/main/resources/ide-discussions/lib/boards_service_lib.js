/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
"use strict";

var user = require('security/v4/user');

var rsdata = require('http/v4/rs-data');

/**
 * Factory function for Board data service instances.
 */
exports.create = function(){
console.error('>>>>>>>> create');
	var boardSvc = rsdata.service(undefined, undefined, undefined, 'ide-discussions.svc.BoardsService')
					.dao(require("ide-discussions/lib/board_dao").create().orm);
	
	boardSvc.mappings().create().onEntityInsert(function(entity){
	    entity.user = require("security/v4/user").getName();
		entity.publishTime = Date.now();
		entity.visits = 0;
	   	entity.lastModifiedTime = entity.publishTime;
	});

	boardSvc.mappings().update().onEntityUpdate(function(entity){
	   	entity.lastModifiedTime = Date.now();
	});
	
	var boardStatsDataService = require("ide-discussions/lib/board_stats_service_lib").create();

	//weave in resource handlers from BoardStats service into Board service 	
	boardSvc.mappings().query().serve = boardStatsDataService.mappings().query().serve.bind(boardStatsDataService);
	boardSvc.mappings().get().serve = boardStatsDataService.mappings().get().serve.bind(boardStatsDataService);

	boardSvc
		.resource("{id}/visit")
			.put(function(context, request, response){
					//TODO: this is a PoC only. A much more elaborated solution should be in place (that would not easily allow overflow of integer unlike this one)
					this.dao().visit(context.pathParameters.id);
					response.setStatus(response.NO_CONTENT);
				})
				.consumes(["application/json"])
		.resource("{id}/vote")
			.post(function(context, request, response){
			    	var entity = request.getJSON();
					var boardVotesDAO = this.dao().orm.getAssociation('votes').targetDao();	
					boardVotesDAO.vote(context.pathParameters.id, user.getName(), entity.vote);
					response.setStatus(response.OK);
				})
				.consumes(["application/json"])
		.resource("{id}/tags")
			.post(function(context, request, response){
				    	var tags = request.getJSON();
				    	if(tags && !Array.isArray(tags)){
				    		tags = [tags];
				    	}
				    	var createOnDemand = context.queryParams['createOnDemand'] || true;
						this.dao().setTags(context.pathParameters.id, tags, createOnDemand);
						response.setStatus(response.OK);
					})
				.consumes(["application/json"])

	return boardSvc;
};
