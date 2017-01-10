/* globals $ */
/* eslint-env node, dirigible */
(function() {
	'use strict';

	var QUnit =require("core/globals").get("QUnit");

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
	  console.info(JSON.stringify(data));
	});	

})();
