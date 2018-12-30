/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');
   
exports.getCache = function() {
	var cacheInstnace = java.call("org.eclipse.dirigible.commons.config.ResourcesCache", "getThemeCache", [], true);
	var cache = new Cache();
	cache.uuid = cacheInstnace.uuid;
  	return cache;
};

function Cache() {

	this.getTag = function(tag) {
		return java.invoke(this.uuid, 'getTag', [tag]);
	};

	this.setTag = function(id, tag) {
		return java.invoke(this.uuid, 'setTag', [id, tag]);
	};

	this.generateTag = function() {
		return java.invoke(this.uuid, 'generateTag', []);
	};

	this.clear = function() {
		return java.invoke(this.uuid, 'clear', []);
	};
}