/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2014 Walter Higgins
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * Based on work from:
 * https://github.com/walterhiggins/commonjs-modules-javax-script
 *******************************************************************************/

package org.eclipse.dirigible.engine.js.v8.callbacks;

/**
 * This class contains the source code of the require() function (by CommonJS specification), added to the JavaScript
 * scripting service execution with Nashorn engine, where it is not included by default
 */
public class Require {

	public static final String CODE = "var Require = (function(modulePath) {" + "	var _loadedModules = {};" + " var _require = function(path) {"
			+ " var moduleInfo, buffered, head = '(function(exports,module,require){ ', code = '', tail = '})', line = null;"
			+ " moduleInfo = _loadedModules[path];" + " if (moduleInfo) {" + "   return moduleInfo;" + " }"
			+ " code = _j2v8loadSource(path);" + " moduleInfo = {" + "   loaded : false," + "   id : path," + "   exports : {},"
			+ "   require : _requireClosure()" + " };" + " code = head + code + tail;" + " _loadedModules[path] = moduleInfo;"
			+ " var compiledWrapper = null;" + " try {" + "   compiledWrapper = eval(code);" + " } catch (e) {"
			+ "   throw new Error('Error evaluating module ' + path + ' line #' + e.lineNumber + ' : ' + e.message, path, e.lineNumber);" + " }"
			+ " var parameters = [ moduleInfo.exports, /* exports */" + "   moduleInfo, /* module */" + "   moduleInfo.require /* require */" + " ];"
			+ " try {" + "   compiledWrapper.apply(moduleInfo.exports, /* this */" + "   parameters);" + " } catch (e) {"
			+ "   throw new Error('Error executing module ' + path + ' line #' + e.lineNumber + ' : ' + e.message, path, e.lineNumber);" + " }"
			+ " moduleInfo.loaded = true;" + " return moduleInfo;" + "};" + "var _requireClosure = function()" + " {" + "  return function(path) {"
			+ "  var module = _require(path);" + "  return module.exports;" + " };" + "};return _requireClosure();});" + "var require = Require();";
}
