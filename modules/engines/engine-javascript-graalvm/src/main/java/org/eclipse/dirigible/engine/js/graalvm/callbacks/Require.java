/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.callbacks;

/**
 * This class contains the source code of the require() function (by CommonJS
 * specification), added to the JavaScript scripting service execution with
 * Nashorn engine, where it is not included by default.
 */
public class Require {

	/** The Constant CODE. */
	public static final String CODE = "var Require = (function(modulePath) {" //
			+ "	var _loadedModules = {};" //
			+ " var _require = function(path) {" //
			+ " var moduleInfo, buffered, head = '(function(exports,module,require){ ', code = '', tail = '})', line = null;" //
			+ " moduleInfo = _loadedModules[path];" //
			+ " if (moduleInfo) {" //
			+ "   return moduleInfo;" //
			+ " }" //
			+ " code = SourceProvider.loadSource(path);" //
			+ " moduleInfo = {" //
			+ "   loaded : false," //
			+ "   id : path," //
			+ "   exports : {}," //
			+ "   require : _requireClosure()" //
			+ " };" //
			+ " code = head + code + tail;" //
			+ " _loadedModules[path] = moduleInfo;" //
			+ " var compiledWrapper = null;" //
			+ " try {" //
			+ "   compiledWrapper = eval(code);" //
			+ " } catch (e) {" //
			+ "   throw new Error('Error evaluating module ' + path + ' line #' + e.lineNumber + ' : ' + e.message, path, e.lineNumber);" //
			+ " }" //
			+ " var parameters = [ moduleInfo.exports, /* exports */" //
			+ "   moduleInfo, /* module */" //
			+ "   moduleInfo.require /* require */" //
			+ " ];" //
			+ " try {" //
			+ "   compiledWrapper.apply(moduleInfo.exports, /* this */" //
			+ "   parameters);" //
			+ " } catch (e) {" //
			+ "   throw new Error('Error executing module ' + path + ' line #' + e.lineNumber + ' : ' + e.message, path, e.lineNumber);" //
			+ " }" //
			+ " moduleInfo.loaded = true;" //
			+ " return moduleInfo;" //
			+ "};" //
			+ "var _requireClosure = function()" //
			+ " {" //
			+ "  return function(path) {" //
			+ "  var module = _require(path);" //
			+ "  return module.exports;" //
			+ " };" //
			+ "};return _requireClosure();});" //
			+ "var require = Require();";

	public static final String MODULE_CODE = "function Module (id, parent) {\n" +
			"  this.id = id;\n" +
			"  this.exports = {};\n" +
			"  this.parent = parent;\n" +
			"\n" +
			"  this.filename = null;\n" +
			"  this.loaded = false;\n" +
			"  this.exited = false;\n" +
			"  this.children = [];\n" +
			"};\n" +
			"\n" +
			"\n" +
			"var moduleCache = {};\n" +
			"\n" +
			"function createModule (id, parent) {\n" +
			"if (id in moduleCache) {\n" +
			"  debug(\"found \" + JSON.stringify(id) + \" in cache\");\n" +
			"  return moduleCache[id];\n" +
			"}\n" +
			"debug(\"didn't found \" + JSON.stringify(id) + \" in cache. creating new module\");\n" +
			"var m = new Module(id, parent);\n" +
			"moduleCache[id] = m;\n" +
			"return m;\n" +
			"};\n" +
			"\n" +
			"function createInternalModule (id, constructor) {\n" +
			"  var m = createModule(id);\n" +
			"  constructor(m.exports);\n" +
			"  m.loaded = true;\n" +
			"  return m;\n" +
			"};\n" +
			"\n" +
			"var debugLevel = 0;\n" +
			"\n" +
			"function debug (x) {\n" +
			"if (debugLevel > 0) {\n" +
			"  console.log(x + \"\\n\");\n" +
			"}\n" +
			"}\n" +
			"\n" +
			"var pathModule = createInternalModule(\"path\", function (exports) {\n" +
			"  exports.join = function () {\n" +
			"    return exports.normalize(Array.prototype.join.call(arguments, \"/\"));\n" +
			"  };\n" +
			"\n" +
			"  exports.normalizeArray = function (parts, keepBlanks) {\n" +
			"    var directories = [], prev;\n" +
			"    for (var i = 0, l = parts.length - 1; i <= l; i++) {\n" +
			"      var directory = parts[i];\n" +
			"\n" +
			"      // if it's blank, but it's not the first thing, and not the last thing, skip it.\n" +
			"      if (directory === \"\" && i !== 0 && i !== l && !keepBlanks) continue;\n" +
			"\n" +
			"      // if it's a dot, and there was some previous dir already, then skip it.\n" +
			"      if (directory === \".\" && prev !== undefined) continue;\n" +
			"\n" +
			"      if (\n" +
			"        directory === \"..\"\n" +
			"        && directories.length\n" +
			"        && prev !== \"..\"\n" +
			"        && prev !== undefined\n" +
			"        && (prev !== \"\" || keepBlanks)\n" +
			"      ) {\n" +
			"        directories.pop();\n" +
			"        prev = directories.slice(-1)[0]\n" +
			"      } else {\n" +
			"        if (prev === \".\") directories.pop();\n" +
			"        directories.push(directory);\n" +
			"        prev = directory;\n" +
			"      }\n" +
			"    }\n" +
			"    return directories;\n" +
			"  };\n" +
			"\n" +
			"  exports.normalize = function (path, keepBlanks) {\n" +
			"    return exports.normalizeArray(path.split(\"/\"), keepBlanks).join(\"/\");\n" +
			"  };\n" +
			"\n" +
			"  exports.dirname = function (path) {\n" +
			"    return path.substr(0, path.lastIndexOf(\"/\")) || \".\";\n" +
			"  };\n" +
			"\n" +
			"  exports.filename = function () {\n" +
			"    throw new Error(\"path.filename is deprecated. Please use path.basename instead.\");\n" +
			"  };\n" +
			"  exports.basename = function (path, ext) {\n" +
			"    var f = path.substr(path.lastIndexOf(\"/\") + 1);\n" +
			"    if (ext && f.substr(-1 * ext.length) === ext) {\n" +
			"      f = f.substr(0, f.length - ext.length);\n" +
			"    }\n" +
			"    return f;\n" +
			"  };\n" +
			"\n" +
			"  exports.extname = function (path) {\n" +
			"    var index = path.lastIndexOf('.');\n" +
			"    return index < 0 ? '' : path.substring(index);\n" +
			"  };\n" +
			"\n" +
			"});\n" +
			"\n" +
			"var path = pathModule.exports;\n" +
			"\n" +
			"function findModulePath (id, dirs) {\n" +
			"\n" +
			"  if (dirs.length == 0) {\n" +
			"    return;\n" +
			"  }\n" +
			"\n" +
			"  var dir = dirs[0];\n" +
			"  var rest = dirs.slice(1, dirs.length);\n" +
			"\n" +
			"  if (id.charAt(0) == '/') {\n" +
			"    dir = '';\n" +
			"    rest = [];\n" +
			"  }\n" +
			"\n" +
			"  var locations = [\n" +
			"    path.join(dir, id),\n" +
			"    path.join(dir, id + \".js\"),\n" +
			"  //   path.join(dir, id + \".node\"),\n" +
			"    path.join(dir, id, \"index.js\"),\n" +
			"  //   path.join(dir, id, \"index.addon\")\n" +
			"  ];\n" +
			"\n" +
			"  \n" +
			"  var searchLocations = function() {\n" +
			"      var location = locations.shift();\n" +
			"      if (location === undefined) {\n" +
			"          return findModulePath(id, rest);\n" +
			"      }\n" +
			"\n" +
			"      var resource = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.getResource(\"/registry/public/\" + location)\n" +
			"      if (resource.exists()) {\n" +
			"          return location;\n" +
			"      }  else {\n" +
			"        var registry = org.eclipse.dirigible.api.v3.platform.RegistryFacade;\n" +
			"\n" +
			"          try {\n" +
			"            resource = registry.getText(location)\n" +
			"            if (resource && resource.length && resource.length > 0) {\n" +
			"                return location;\n" +
			"            }\n" +
			"          } catch (error) {\n" +
			"            return searchLocations();\n" +
			"          }\n" +
			"          \n" +
			"\n" +
			"      }\n" +
			"      return searchLocations();\n" +
			"  }\n" +
			"\n" +
			"  return searchLocations();\n" +
			"}\n" +
			"\n" +
			"function loadModule (request, parent) {\n" +
			"\n" +
			"  debug(\"loadModule REQUEST  \" + (request) + \" parent: \" + JSON.stringify(parent));\n" +
			"\n" +
			"  var id, paths;\n" +
			"  if (request.charAt(0) == \".\" && (request.charAt(1) == \"/\" || request.charAt(1) == \".\")) {\n" +
			"    // Relative request\n" +
			"    var parentIdPath = path.dirname(parent.id +\n" +
			"      (path.basename(parent.filename).match(/^index\\.(js|addon)$/) ? \"/\" : \"\"));\n" +
			"    id = path.join(parentIdPath, request);\n" +
			"    debug(\"RELATIVE: requested:\"+request+\" set ID to: \"+id+\" from \"+parent.id+\"(\"+parentIdPath+\")\");;\n" +
			"    paths = [path.dirname(parent.filename)];\n" +
			"  } else {\n" +
			"    id = request;\n" +
			"    // debug(\"ABSOLUTE: id=\"+id);\n" +
			"    // paths = org.eclipse.dirigible.api.v3.core.ContextFacade.get(\"paths\") \n" +
			"    paths = [\"/\"];\n" +
			"  }\n" +
			"\n" +
			"  if (id in moduleCache) {\n" +
			"    debug(\"found  \" + JSON.stringify(id) + \" in cache\");\n" +
			"    // In cache\n" +
			"    var module = moduleCache[id];\n" +
			"\n" +
			"    return module.exports;\n" +
			"  } else {\n" +
			"    debug(\"looking for \" + JSON.stringify(id) + \" in \" + JSON.stringify(paths));\n" +
			"    // Not in cache\n" +
			"    let filename = findModulePath(request, paths);\n" +
			"    if (!filename) {\n" +
			"      throw new Error(\"Cannot find module '\" + request + \"'\");\n" +
			"    } else {\n" +
			"      var module = createModule(id, parent);\n" +
			"      return module.load(filename);\n" +
			"    }\n" +
			"  }\n" +
			"\n" +
			"};\n" +
			"\n" +
			"Module.prototype.load = function (filename) {\n" +
			"  debug(\"load \" + JSON.stringify(filename) + \" for module \" + JSON.stringify(this.id));\n" +
			"\n" +
			"  this.filename = filename;\n" +
			"\n" +
			"  return this.loadScript(filename); \n" +
			"};\n" +
			"\n" +
			"Module.prototype.loadScript = function (filename) {\n" +
			"      var self = this;\n" +
			"\n" +
			"\n" +
			"      let content = SourceProvider.loadSource(filename);\n" +
			"      content = content.replace(/^\\#\\!.*/, '');\n" +
			"  \n" +
			"      function require (url) {\n" +
			"          return loadModule(url, self); \n" +
			"      }\n" +
			"  \n" +
			"      // require.paths = org.eclipse.dirigible.api.v3.core.ContextFacade.get(\"paths\");\n" +
			"      require.paths = [\"/\"];\n" +
			"      //   require.async = requireAsync;\n" +
			"      require.main = org.eclipse.dirigible.api.v3.core.ContextFacade.get(\"main_module\");\n" +
			"      // create wrapper function\n" +
			"      var wrapper = \"var __wrap__ = function (exports, require, module, __filename, __dirname) { \"\n" +
			"                  + content \n" +
			"                  + \"\\n}; __wrap__;\";\n" +
			"  \n" +
			"   \n" +
			"      try {\n" +
			"          var compiledWrapper = eval(wrapper);\n" +
			"          compiledWrapper.apply(self.exports, [self.exports, require, self, filename, path.dirname(filename)]);\n" +
			"          return self.exports;\n" +
			"      } catch (e) {\n" +
			"          throw e;\n" +
			"      }\n" +
			"\n" +
			"};\n" +
			"\n" +
			"Module.prototype.loadScriptString = function (script) {\n" +
			"      var self = this;\n" +
			"      let filename = \"index.js\"\n" +
			"\n" +
			"      let content = script;\n" +
			"      content = content.replace(/^\\#\\!.*/, '');\n" +
			"  \n" +
			"      function require (url) {\n" +
			"          return loadModule(url, self); \n" +
			"      }\n" +
			"  \n" +
			"      // require.paths = org.eclipse.dirigible.api.v3.core.ContextFacade.get(\"paths\");\n" +
			"      require.paths = [\"/\"];\n" +
			"      //   require.async = requireAsync;\n" +
			"      require.main = org.eclipse.dirigible.api.v3.core.ContextFacade.get(\"main_module\");\n" +
			"      // create wrapper function\n" +
			"      var wrapper = \"var __wrap__ = function (exports, require, module, __filename, __dirname) { \"\n" +
			"                  + content \n" +
			"                  + \"\\n}; __wrap__;\";\n" +
			"  \n" +
			"   \n" +
			"      try {\n" +
			"          var compiledWrapper = eval(wrapper);\n" +
			"          compiledWrapper.apply(self.exports, [self.exports, require, self, filename, path.dirname(filename)]);\n" +
			"          return self.exports;\n" +
			"      } catch (e) {\n" +
			"          throw e;\n" +
			"      }\n" +
			"\n" +
			"};\n";

	public static final String MODULE_CREATE_CODE = "let mainModule = createModule(\".\");\n" +
			"org.eclipse.dirigible.api.v3.core.ContextFacade.set(\"main_module\", mainModule);";

	public static final String MODULE_LOAD_CODE = "mainModule.load(MODULE_FILENAME);";

	public static final String LOAD_STRING_CODE = "mainModule.loadScriptString(SCRIPT_STRING);";

	public static final String LOAD_CONSOLE_CODE = "let console = {};\n" +
			"console.log = function(message) {\n" +
			"\torg.eclipse.dirigible.api.v3.core.ConsoleFacade.log(stringify(message));\n" +
			"};\n" +
			"\n" +
			"console.error = function(message) {\n" +
			"\torg.eclipse.dirigible.api.v3.core.ConsoleFacade.error(stringify(message));\n" +
			"};\n" +
			"\n" +
			"console.info = function(message) {\n" +
			"\torg.eclipse.dirigible.api.v3.core.ConsoleFacade.info(stringify(message));\n" +
			"};\n" +
			"\n" +
			"console.warn = function(message) {\n" +
			"\torg.eclipse.dirigible.api.v3.core.ConsoleFacade.warn(stringify(message));\n" +
			"};\n" +
			"\n" +
			"console.debug = function(message) {\n" +
			"\torg.eclipse.dirigible.api.v3.core.ConsoleFacade.debug(stringify(message));\n" +
			"};\n" +
			"\n" +
			"console.trace = function(message) {\n" +
			"\tlet traceMessage = new Error(stringify(`${message}`)).stack;\n" +
			"\tif (traceMessage) {\n" +
			"\t\ttraceMessage = traceMessage.substring(\"Error: \".length, traceMessage.length);\n" +
			"\t}\n" +
			"\torg.eclipse.dirigible.api.v3.core.ConsoleFacade.trace(traceMessage);\n" +
			"};\n" +
			"\n" +
			"function stringify(message) {\n" +
			"\tif (typeof message === 'object' && message !== null && message.class === undefined) {\n" +
			"\t\treturn JSON.stringify(message);\n" +
			"\t}\n" +
			"\treturn \"\" + message;\n" +
			"}";

}
