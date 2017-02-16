/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java org */
/* eslint-env node, dirigible */

exports.generate = function(template, parameters) {
	var temlatingService = $.getTemplatingService();
	var internalParameters = $.getTemplatingService().createParameters();
	for (var parameter in parameters) {
		internalParameters.put(parameter, parameters[parameter]);		
	}
	var generated = temlatingService.generate(template, internalParameters, "generation_service_api");
	return generated;
};


exports.getWorker = function(category) {
	var internalWorker = $.getGenerationService().getGenerationWorker(category, $.getRequest());
	return new Worker(internalWorker);
};

/**
 * Worker object
 */
function Worker(internalWorker) {
	this.internalWorker = internalWorker;

	this.getInternalObject = function() {
		return this.internalWorker;
	};

	this.generate = function(parameters) {
		return this.internalWorker.generate(parameters, $.getRequest());
	};

	this.getTemplates = function() {
		return this.internalWorker.getTemplates($.getRequest());
	};
}

// CONSTANTS

// ---- Worker Types ----
exports.WORKER_CATEGORY_DATA_STRUCTURES = "DataStructures";
exports.WORKER_CATEGORY_SCRIPTING_SERVICES = "ScriptingServices";
exports.WORKER_CATEGORY_WEB_CONTENT = "WebContent";
exports.WORKER_CATEGORY_WEB_CONTENT_FOR_ENTITY = "WebContentForEntity";
