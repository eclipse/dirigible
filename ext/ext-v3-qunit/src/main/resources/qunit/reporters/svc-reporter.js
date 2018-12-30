/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

(function() {
	'use strict';

	var response = require("http/response");

	var QUnit = this.QUnit || require("qunit/qunit");
	
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
		
		require("qunit/reporters/reporter-junit").QUnit = QUnit;
		
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
