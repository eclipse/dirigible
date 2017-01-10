/* globals $ */
/* eslint-env node, dirigible */
(function() {
	'use strict';

	var response = require("net/http/response");

	var QUnit = this.QUnit || require("core/globals").get("QUnit");
	
	var attachJSONhandler = function(){
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");	
		var data = {
			tests: [],
			moduleTests: []
		};
		
		QUnit.moduleDone(function(details) {
			data.moduleTests.push(details);
		});
		QUnit.testDone(function(details) {
		  data.tests.push(details);
		});	
		QUnit.done(function( details ) {
		  data.testSuite = details;
		  response.print(JSON.stringify(data));
		});	
	};
	
	var attachJUnitXmlhandler = function(){
		response.setContentType("text/xml; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		require("qunit/reporters/qunit_reporter_junit");
		
		QUnit.jUnitDone(function(report) {
			response.print(report.xml);
		});
	};	

	var svc_reporters = {
		"json": attachJSONhandler,
		"xml":	attachJUnitXmlhandler
	};	
	
	exports.forMedia = function(media){
		svc_reporters[media].apply(this);
	};
	
})();
