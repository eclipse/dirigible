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
tagsDataService.service();
