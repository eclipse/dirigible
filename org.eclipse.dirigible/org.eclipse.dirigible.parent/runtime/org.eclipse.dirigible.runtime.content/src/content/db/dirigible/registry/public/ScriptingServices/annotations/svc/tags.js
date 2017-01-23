/* globals $ */
/* eslint-env node, dirigible */
"use strict";
	
var arester = require("arestme/arester");
var tagsDAO = require("annotations/lib/tags_dao");

var Tag = arester.asRestAPI(tagsDAO);
Tag.prototype.logger.ctx = "Tags Svc";

var create = function(context, io){
	var input = io.request.readInputText();
    var entity = JSON.parse(input);
    try{
		entity[this.dao.getPrimaryKey()] = this.dao.insert(entity, context.queryParams.cascaded);
		io.response.setStatus(io.response.OK);
		io.response.setHeader('Location', $.getRequest().getRequestURL().toString() + '/' + entity[this.dao.getPrimaryKey()]);
	} catch(e) {
	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    this.logger.error(errorCode, e.message, e.errContext);
    	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
    	throw e;
	}	
};

Tag.prototype.cfg[""].post = {
	consumes: ["application/json"],
	handler: function(context, io){
		var input = io.request.readInputText();
	    try{
	    	var tags = JSON.parse(input);
	    	if(!Array.isArray(tags)){
	    		tags = [tags];
	    	}
	    	for(var i=0;i<tags.length;i++){
	    		create.apply(this, [context, io]);
	    	}
			io.response.setStatus(io.response.NO_CONTENT);
		} catch(e) {
    	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
    	    this.logger.error(errorCode, e.message, e.errContext);
        	this.sendError(io, errorCode, errorCode, e.message, e.errContext);
        	throw e;
		}		
	}
};


var tag = new Tag(tagsDAO);	

var request = require("net/http/request");
var response = require("net/http/response");

tag.service(request, response);
