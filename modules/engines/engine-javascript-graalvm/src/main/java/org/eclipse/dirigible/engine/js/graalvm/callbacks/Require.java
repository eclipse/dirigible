/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.engine.js.graalvm.callbacks;

/**
 * This class contains the source code of the require() function (by CommonJS specification), added to the JavaScript
 * scripting service execution with Nashorn engine, where it is not included by default.
 */
public class Require {

	/** The Constant CODE. */
	public static final String CODE = "var Require = (function(modulePath) {" + "	var _loadedModules = {};" + " var _require = function(path) {"
			+ " var moduleInfo, buffered, head = '(function(exports,module,require){ ', code = '', tail = '})', line = null;"
			+ " moduleInfo = _loadedModules[path];" + " if (moduleInfo) {" + "   return moduleInfo;" + " }"
			+ " code = SourceProvider.loadSource(path);" + " moduleInfo = {" + "   loaded : false," + "   id : path," + "   exports : {},"
			+ "   require : _requireClosure()" + " };" + " code = head + code + tail;" + " _loadedModules[path] = moduleInfo;"
			+ " var compiledWrapper = null;" + " try {" + "   compiledWrapper = eval(code);" + " } catch (e) {"
			+ "   throw new Error('Error evaluating module ' + path + ' line #' + e.lineNumber + ' : ' + e.message, path, e.lineNumber);" + " }"
			+ " var parameters = [ moduleInfo.exports, /* exports */" + "   moduleInfo, /* module */" + "   moduleInfo.require /* require */" + " ];"
			+ " try {" + "   compiledWrapper.apply(moduleInfo.exports, /* this */" + "   parameters);" + " } catch (e) {"
			+ "   throw new Error('Error executing module ' + path + ' line #' + e.lineNumber + ' : ' + e.message, path, e.lineNumber);" + " }"
			+ " moduleInfo.loaded = true;" + " return moduleInfo;" + "};" + "var _requireClosure = function()" + " {" + "  return function(path) {"
			+ "  var module = _require(path);" + "  return module.exports;" + " };" + "};return _requireClosure();});" + "var require = Require();";
}
