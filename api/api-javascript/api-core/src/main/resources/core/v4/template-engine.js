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
/**
 * API v4 Template Engine
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

var repositoryManager = require("repository/v4/manager");

const REGISTRY_PUBLIC = "/registry/public/";
const MUSTACHE_FILE_EXTENSION = ".mustache";

exports.getDefaultEngine = function() {
	return exports.getVelocityEngine();
};

exports.getMustacheEngine = function() {
	var engine = org.eclipse.dirigible.api.v3.core.TemplateEngineFacade.getMustacheEngine();
	return new TemplateEngine(engine, "mustache");
};

exports.getVelocityEngine = function() {
	var engine = org.eclipse.dirigible.api.v3.core.TemplateEngineFacade.getVelocityEngine();
	return new TemplateEngine(engine, "velocity");
};

exports.generate = function(template, parameters) {
	return exports.getDefaultEngine().generate(template, parameters);
};

exports.generateFromFile = function(location, parameters) {
	var resource = repositoryManager.getResource(REGISTRY_PUBLIC + location);
	if (resource.exists()) {
		var isMustacheTemplate = location.endsWith(MUSTACHE_FILE_EXTENSION);
		var engine = isMustacheTemplate ? exports.getMustacheEngine() : exports.getDefaultEngine();
		var template = resource.getText();
		return engine.generate(template, parameters);
	}
	return null;
};

function TemplateEngine(engine, type) {
	this.sm = type === "mustache" ? "{{" : null;
	this.em = type === "mustache" ? "}}" : null;

	this.generate = function(template, parameters) {
		return engine.generate(template, JSON.stringify(parameters), this.sm, this.em);
	};

	this.setSm = function(sm) {
		this.sm = sm;
	};

	this.setEm = function(em) {
		this.em = em;
	};
}