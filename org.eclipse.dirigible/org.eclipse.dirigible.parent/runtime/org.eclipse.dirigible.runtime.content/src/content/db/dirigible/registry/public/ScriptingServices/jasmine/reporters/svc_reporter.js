/* globals $ */
/* eslint-env node, dirigible */

(function() {
	'use strict';
	
	var response = require("net/http/response");
	
	var jsonReporter = {
	
		/*
			Transformation Mapping Rules:
			
			testSuite -> suite (.describe)
			test -> spec (.it)
			assertions ->  passed/failedExpectaitons (.expect)
		*/
	
		data: {
			tests: [],
			testSuite: {
				runtime: 0
			}
		},
		
		jasmineStarted: function(suiteInfo) {
			this.data.testSuite.runtime = new Date().getTime();
			this.data.testSuite.total = suiteInfo.totalSpecsDefined;
		},
		suiteStarted: function(suite) {
		},	
		specStarted: function(spec) {
			var test = this.data.tests.filter(function(entry){
				return spec.id === entry.id;
			})[0];
			if(!test){
				var idx = spec.fullName.indexOf(spec.description);
				var _module = spec.fullName.slice(0,idx);
				test = {
					id: spec.id,
					name: spec.description,
					module: _module,
					runtime: new Date().getTime(),
					assertions: []
				};
				this.data.tests.push(test);
			}		
		},
		specDone: function(spec) {
			if(this.data.testSuite.passed===undefined || this.data.testSuite.passed==='passed')
				this.data.testSuite.passed = spec.status;
			var assertions = [];
			if(spec.failedExpectations.length>0){
				assertions = spec.failedExpectations.map(function(assertion){
					var message = assertion.message;
					if(typeof message === 'function')
						message = assertion.message();
					return {
						message: message,
						result: assertion.passed,
						stack: assertion.stack
					};
				});
				if(this.data.testSuite.failed === undefined)
					this.data.testSuite.failed = assertions.length;
				else 
					this.data.testSuite.failed += assertions.length;
			} else {
				assertions = spec.passedExpectations.map(function(assertion){
					return {
						message: spec.description + ' assertion[' + assertion.matcherName + '] ' + assertion.message.toLowerCase(),
						result: assertion.passed
					};
				});
			}
			var test = this.data.tests.filter(function(entry){
				return spec.id === entry.id;
			})[0];
			if(test){
				test.assertions = test.assertions.concat(assertions);
				test.failed = spec.status==='failed'? true: false;
				test.total = assertions.length;
				test.runtime = new Date().getTime() - test.runtime;
			} else {
				console.error('[Application Error]: Could not locate started test with id['+spec.id+']');
			}
		},	
		suiteDone: function(suite) {
	    	this.data.testSuite.passed = (this.data.testSuite.total - this.data.testSuite.failed);
		},
		jasmineDone: function() {
			this.data.testSuite.runtime = new Date().getTime() - this.data.testSuite.runtime;
			response.setContentType("application/json; charset=UTF-8");
			response.setCharacterEncoding("UTF-8");	
			response.print(JSON.stringify(this.data));
			response.flush();
			response.close();
		}
	};

	exports.forMedia = function(media){
		var _env = this.env || require('core/globals').get('JasmineEnv');
		if(media === 'json')
			_env.addReporter(jsonReporter);
	};	
	
})();
