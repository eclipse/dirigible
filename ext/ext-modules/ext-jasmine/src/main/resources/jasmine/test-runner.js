/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
/* eslint-env node, dirigible */
"use strict";
exports.run = function(env, config){
	
	config = config || {};
	
	var svc_reporter;
	
	if(!config["disable-service-reporter"]){
		svc_reporter = require("jasmine/reporters/svc-reporter");
		svc_reporter.env = env;
	}
	if(!config["disable-console-reporter"]){
		var console_reporter = require("jasmine/reporters/console-reporter");
		if(config["prettyPrint"] !== undefined)
			console_reporter.settings["prettyPrint"] = config["prettyPrint"];
		env.addReporter(console_reporter.settings);
	}
	
	require("test/runner").run({
		"execute": function(){
			env.execute();
		}.bind(this),
		"serviceReporter": svc_reporter
	});
};
