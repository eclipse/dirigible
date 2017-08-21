/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

exports.call = function(className, methodName, params, deep) {
	var result = null;
	params = normalizeParameters(params);
	if (engine === "rhino") {
		result = org.eclipse.dirigible.api.v3.core.JavaFacade.call(className, methodName, params);
	} else if (engine === "nashorn") {
		result = Packages.org.eclipse.dirigible.api.v3.core.JavaFacade.call(className, methodName, params);
	} else if (engine === "v8") {
		result = j2v8call(className, methodName, params);
	}
	if (deep) {
		o = {};
		o['uuid'] = result;
		return o;
	}
	return result;
};

exports.instantiate = function(className, params) {
	var result = null;
	params = normalizeParameters(params);
	
	if (engine === "rhino") {
		uuid = org.eclipse.dirigible.api.v3.core.JavaFacade.instantiate(className, params);
	} else if (engine === "nashorn") {
		uuid = Packages.org.eclipse.dirigible.api.v3.core.JavaFacade.instantiate(className, params);
	} else if (engine === "v8") {
		uuid = j2v8instantiate(className, params);
	}
	result = {};
	result['uuid'] = uuid;
	return result;
};

exports.invoke = function(uuid, methodName, params, deep) {
	var result = null;
	params = normalizeParameters(params);
	if (engine === "rhino") {
		result = org.eclipse.dirigible.api.v3.core.JavaFacade.invoke(uuid, methodName, params);
	} else if (engine === "nashorn") {
		result = Packages.org.eclipse.dirigible.api.v3.core.JavaFacade.invoke(uuid, methodName, params);
	} else if (engine === "v8") {
		result = j2v8invoke(uuid, methodName, params);
	}
	if (deep) {
		o = {};
		o['uuid'] = result;
		return o;
	}
	return result;
};

function normalizeParameters(params) {
	if (Array.isArray(params)) {
		for (var i = 0; i < params.length; i++) {
			if (params[i] && params[i].uuid) {
				continue;
			}
			if (Array.isArray(params[i])) {
				params[i] = JSON.stringify(params[i]);
			}
		}
	}
	return params;
}
