/* globals $ */
/* eslint-env node, dirigible */
"use strict";
exports.service = function(env){
	var _env = env || require('core/globals').get('JasmineEnv');
	var svc_reporter = require("jasmine/reporters/svc_reporter");
	svc_reporter.env = _env;
	require("tests/test_runner_svc").service({
		"execute": function(){
			_env.execute();
		},
		"serviceReporter": svc_reporter
	});
};
