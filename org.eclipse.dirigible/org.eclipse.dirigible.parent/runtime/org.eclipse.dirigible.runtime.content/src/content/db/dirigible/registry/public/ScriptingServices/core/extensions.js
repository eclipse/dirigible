/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $*/
/* eslint-env node, dirigible */

var streams = require('io/streams');
var extensionService = $.getExtensionService();

exports.getExtensions = function(extensionPoint) {
	var extensions = extensionService.getExtensions(extensionPoint);
	return streams.toJavaScriptBytes(extensions);
};

exports.getExtension = function(extension, extensionPoint) {
	var internalExtensionDefinition = extensionService.getExtension(extension, extensionPoint);
	return internalExtensionDefinition !== null ? new ExtensionDefinition(internalExtensionDefinition) : null;
};
	
exports.getExtensionPoint = function(extensionPoint) {
	var internalExtensionPointDefinition = extensionService.getExtensionPoint(extensionPoint);
	return internalExtensionPointDefinition !== null ? new ExtensionPointDefinition(internalExtensionPointDefinition) : null;
};
	
exports.getExtensionPoints = function(){
	var extensionPoints = extensionService.getExtensionPoints();
	return streams.toJavaScriptBytes(extensionPoints);
};
	
exports.createExtension = function(extension, extensionPoint, description) {
	extensionService.createExtension(extension, extensionPoint, description);
};
	
exports.updateExtension = function(extension, extensionPoint, description) {
	extensionService.updateExtension(extension, extensionPoint, description);
};

exports.createExtensionPoint = function(extensionPoint, description) {
	extensionService.createExtensionPoint(extensionPoint, description);
};
	
exports.updateExtensionPoint = function(extensionPoint, description) {
	extensionService.updateExtensionPoint(extensionPoint, description);
};

exports.removeExtension = function(extension, extensionPoint) {
	extensionService.removeExtension(extension, extensionPoint);
};
	
exports.removeExtensionPoint = function(extensionPoint) {
	extensionService.removeExtensionPoint(extensionPoint);
};

function ExtensionDefinition(internalExtensionDefinition) {
	this.internalExtensionDefinition = internalExtensionDefinition;

	this.getLocation = function() {
		return internalExtensionDefinition.getLocation();
	};

	this.getExtensionPoint = function() {
		return internalExtensionDefinition.getExtensionPoint();
	};

	this.getDescription = function() {
		return internalExtensionDefinition.getDescription();
	};

	this.getCreatedBy = function() {
		return internalExtensionDefinition.getCreatedBy();
	};

	this.getCreatedAt = function() {
		return new Date(this.internalExtensionDefinition.getCreatedAt().getTime());
	};
}

function ExtensionPointDefinition(internalExtensionPointDefinition) {
	this.internalExtensionPointDefinition = internalExtensionPointDefinition;

	this.getLocation = function() {
		return internalExtensionPointDefinition.getLocation();
	};

	this.getDescription = function() {
		return internalExtensionPointDefinition.getDescription();
	};

	this.getCreatedBy = function() {
		return internalExtensionPointDefinition.getCreatedBy();
	};

	this.getCreatedAt = function() {
		return new Date(internalExtensionPointDefinition.getCreatedAt().getTime());
	};
}
