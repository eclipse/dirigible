/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var userLib = require("net/http/user");
var arester = require("arestme/arester");
var boardDAO = require("${packageName}/lib/board_dao");

var Board = arester.asRestAPI(boardDAO);
Board.prototype.logger.ctx = "${packageName.toUpperCase()} Board Svc";

Board.prototype.boardVotes = require("${packageName}/lib/board_votes");
Board.prototype.boardTags = require("${packageName}/lib/board_tags");

Board.prototype.cfg["{id}/comments/timeline"] = {
	"get" : {
		handler: function(context, io){
		    try{
				var comments = this.dao.getComments(context.pathParams.id, true);
				io.response.setStatus(io.response.OK);
				io.response.println(JSON.stringify(comments));
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		}
	}
};

Board.prototype.cfg["{id}/comments"] = {
	"get" : {
		handler: function(context, io){
		    try{
				var comments = this.dao.getComments(context.pathParams.id, false);
				io.response.setStatus(io.response.OK);
				io.response.println(JSON.stringify(comments));
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		}
	}
};

Board.prototype.cfg["{id}/visit"] = {
	"put" : {
		consumes: ["application/json"],	
		handler: function(context, io){
			//TODO: this is a PoC only. A much more elaborated solution should be in place (that would not easily allow overflow of integer unlike this one)
		    try{
				this.dao.visit(context.pathParams.id);
				io.response.setStatus(io.response.NO_CONTENT);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		}
	}
};

Board.prototype.cfg["{id}/vote"] = {
	"get": {
		handler: function(context, io){
		    try{
				var vote = this.boardVotes.getVote(context.pathParams.id, userLib.getName());
				io.response.setStatus(io.response.OK);
				io.response.println(JSON.stringify({"vote": vote}));
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}
		}
	},
	"post": {
		consumes: ["application/json"],
		handler: function(context, io){
			var input = io.request.readInputText();
		    try{
		    	var entity = JSON.parse(input);
				this.boardVotes.vote(context.pathParams.id, userLib.getName(), entity.vote);
				io.response.setStatus(io.response.OK);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		}
	}		
};

Board.prototype.cfg["{id}/tags"] = {
	"get": {
		handler: function(context, io){
		    try{
				var tags = this.boardTags.listBoardTags(context.pathParams.id);
				io.response.setStatus(io.response.OK);
				io.response.println(JSON.stringify(tags));
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}
		}
	},
	"post": {
		consumes: ["application/json"],
		handler: function(context, io){
			var input = io.request.readInputText();
		    try{
		    	var tags = JSON.parse(input);
		    	if(!Array.isArray(tags)){
		    		tags = [tags];
		    	}
				this.boardTags.setTags(context.pathParams.id, tags, true);
				io.response.setStatus(io.response.OK);
			} catch(e) {
	    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    	    this.logger.error(errorCode, e.message, e.errContext);
	        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
	        	throw e;
			}		
		}
	}
};

var board = new Board(boardDAO);	

var request = require("net/http/request");
var response = require("net/http/response");

board.service(request, response);
