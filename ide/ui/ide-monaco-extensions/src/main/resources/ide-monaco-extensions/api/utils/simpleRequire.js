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
var repositoryContent = require("repository/v4/content");

var Require = function(modulePath) {
	var _loadedModules = {};
	/*
	 require() function implementation
	 */
	var _require = function(path) {
		var moduleInfo, head = '(function(exports,module,require){ ', code = '', tail = '})';
		moduleInfo = _loadedModules[path];
		if (moduleInfo) {
			return moduleInfo;
		}
		code = repositoryContent.getText(path + ".js");;
		moduleInfo = {
			loaded : false,
			id : path,
			exports : {},
			require : _requireClosure()
		};
		code = head + code + tail;
		_loadedModules[path] = moduleInfo;
		var compiledWrapper = null;
		try {
			compiledWrapper = eval(code);
		} catch (e) {
			throw new Error('Error evaluating module ' + path + ' line #'
					+ e.lineNumber + ' : ' + e.message, path,
					e.lineNumber);
		}
		var parameters = [ moduleInfo.exports, /* exports */
		moduleInfo, /* module */
		moduleInfo.require /* require */
		];
		try {
			compiledWrapper.apply(moduleInfo.exports, /* this */
			parameters);
		} catch (e) {
			throw new Error('Error executing module ' + path + ' line #'
					+ e.lineNumber + ' : ' + e.message, path,
					e.lineNumber);
		}
		moduleInfo.loaded = true;
		return moduleInfo;
	};
	var _requireClosure = function() {
		return function(path) {
			var module = _require(path);
			return module.exports;
		};
	};
	return _requireClosure();
};
var requireModule = Require();

exports.load = function(path) {
	return requireModule(path);
};