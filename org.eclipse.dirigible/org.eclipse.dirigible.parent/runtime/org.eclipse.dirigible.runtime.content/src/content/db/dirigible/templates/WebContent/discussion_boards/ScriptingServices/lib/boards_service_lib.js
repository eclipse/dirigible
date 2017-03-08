#set( $D = '$' )
/* globals ${D} */
/* eslint-env node, dirigible */
"use strict";

var userLib = require("net/http/user");

var DataService = require('arestme/data_service').DataService;

var BoardsDataService = function(){
	var boardDAO = require("${packageName}/lib/board_dao").get();
	DataService.call(this, boardDAO, 'Board Data Service');
	var self = this;	
	
	this.handlersProvider.onEntityInsert = function(entity){
	    entity.user = require("net/http/user").getName();
		entity.publishTime = Date.now();
		entity.visits = 0;
	   	entity.lastModifiedTime = entity.publishTime;
	};
	
	this.handlersProvider.onEntityUpdate = function(entity){
	   	entity.lastModifiedTime = Date.now();
	};

	var boardStatsDataService = require("${packageName}/lib/board_stats_service_lib").get();
	boardStatsDataService.logger = this.logger;
	//weave in resource handlers from BoardStats service into Board service 	
	var handlers = this.getResourceHandlersMap();
	var boardStatsHandlers = boardStatsDataService.getResourceHandlersMap();
	handlers[""].get[0].handler = function(context, io){
		boardStatsHandlers[""].get[0].handler.apply(boardStatsDataService, [context, io]);
	};
	handlers["{id}"].get[0].handler = function(context, io){
		boardStatsHandlers["{id}"].get[0].handler.apply(boardStatsDataService, [context, io]);
	};

	this.addResourceHandlers({
		"{id}/visit": {
			"put" : [{
				consumes: ["application/json"],	
				handler: function(context, io){
					//TODO: this is a PoC only. A much more elaborated solution should be in place (that would not easily allow overflow of integer unlike this one)
				    try{
						self.handlersProvider.dao.visit(context.pathParams.id);
						io.response.setStatus(io.response.NO_CONTENT);
					} catch(e) {
			    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
			    	    self.logger.error(e.message, e);
			        	self.sendError(errorCode, errorCode, e.message);
			        	throw e;
					}		
				}
			}]
		}
	})
	.addResourceHandlers({
		"{id}/vote": {
			"post": [{
				consumes: ["application/json"],
				handler: function(context, io){
					var input = io.request.readInputText();
				    try{
				    	var entity = JSON.parse(input);
						var boardVotesDAO = self.handlersProvider.dao.orm.getAssociation('votes').dao();
						boardVotesDAO.vote(context.pathParams.id, userLib.getName(), entity.vote);
						io.response.setStatus(io.response.OK);
					} catch(e) {
			    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
			    	    self.logger.error(e.message, e);
			        	self.sendError(errorCode, errorCode, e.message);
			        	throw e;
					}		
				}
			}]
		}
	})
	.addResourceHandlers({
		"{id}/tags": {
			"post": [{
				consumes: ["application/json"],
				handler: function(context, io){
					var input = io.request.readInputText();
				    try{
				    	var tags = JSON.parse(input);
				    	if(tags && !Array.isArray(tags)){
				    		tags = [tags];
				    	}
				    	var createOnDemand = context.queryParams['createOnDemand'] || true;
						self.handlersProvider.dao.setTags(context.pathParams.id, tags, createOnDemand);
						io.response.setStatus(io.response.OK);
					} catch(e) {
			    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
			    	    self.logger.error(e.message, e);
			        	self.sendError(errorCode, errorCode, e.message);
			        	throw e;
					}		
				}
			}]
		}
	});
};
BoardsDataService.prototype = Object.create(DataService.prototype);
BoardsDataService.prototype.constructor = BoardsDataService;

exports.BoardsDataService = BoardsDataService;
exports.get = function(){
	return new BoardsDataService();
};
