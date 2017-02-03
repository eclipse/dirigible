/* globals $ */
/* eslint-env node, dirigible */
"use strict";

var DataService = require('arestme/data_service').DataService;
var TagsDataService = function(dao){
	DataService.call(this, dao, 'Tags Data Service');
};

TagsDataService.prototype = Object.create(DataService.prototype);
TagsDataService.prototype.constructor = TagsDataService;

var tagsDAO = require("annotations/lib/tags_dao").get();
var tagsDataService = new TagsDataService(tagsDAO);

var handler = function(context, io){
	var ns = context.pathParams.ns;
	var label = context.queryParams.label;
    try{
    	//TODO: fix this to a native sql query
    	var entities = tagsDataService.handlersProvider.dao.findByTagValue(label, ns); 
    	io.response.println(JSON.stringify(entities, null, 2));
		io.response.setStatus(io.response.OK);
	} catch(e) {
	    var errorCode = io.response.INTERNAL_SERVER_ERROR;
	    this.logger.error(e.message, e);
    	this.sendError(errorCode, errorCode, e.message);
    	throw e;
	}		
};
tagsDataService.addResourceHandler('domains/{ns}', 'get', handler, undefined, ["application/json"]);

tagsDataService.service();