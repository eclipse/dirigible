"use strict";
var rsdata = require('http/v3/rs-data'); 
var tagsORMDef = require("ide-discussions/lib/tags_dao").tagsORMDef;
var svc = rsdata.service().dao(tagsORMDef);
svc.execute();
