/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
 function Module (id, parent) {
   this.id = id;
   this.exports = {};
   this.parent = parent;

   this.filename = null;
   this.loaded = false;
   this.exited = false;
   this.children = [];
 };


 var moduleCache = {};

 function createModule (id, parent) {
 if (id in moduleCache) {
   debug("found " + JSON.stringify(id) + " in cache");
   return moduleCache[id];
 }
 debug("didn't found " + JSON.stringify(id) + " in cache. creating new module");
 var m = new Module(id, parent);
 moduleCache[id] = m;
 return m;
 };

 function createInternalModule (id, constructor) {
   var m = createModule(id);
   constructor(m.exports);
   m.loaded = true;
   return m;
 };

 var debugLevel = 0;

 function debug (x) {
 if (debugLevel > 0) {
   console.log(x + "\n");
 }
 }

 var pathModule = createInternalModule("path", function (exports) {
   exports.join = function () {
     return exports.normalize(Array.prototype.join.call(arguments, "/"));
   };

   exports.normalizeArray = function (parts, keepBlanks) {
     var directories = [], prev;
     for (var i = 0, l = parts.length - 1; i <= l; i++) {
       var directory = parts[i];

       // if it's blank, but it's not the first thing, and not the last thing, skip it.
       if (directory === "" && i !== 0 && i !== l && !keepBlanks) continue;

       // if it's a dot, and there was some previous dir already, then skip it.
       if (directory === "." && prev !== undefined) continue;

       if (
         directory === ".."
         && directories.length
         && prev !== ".."
         && prev !== undefined
         && (prev !== "" || keepBlanks)
       ) {
         directories.pop();
         prev = directories.slice(-1)[0]
       } else {
         if (prev === ".") directories.pop();
         directories.push(directory);
         prev = directory;
       }
     }
     return directories;
   };

   exports.normalize = function (path, keepBlanks) {
     return exports.normalizeArray(path.split("/"), keepBlanks).join("/");
   };

   exports.dirname = function (path) {
     return path.substr(0, path.lastIndexOf("/")) || ".";
   };

   exports.filename = function () {
     throw new Error("path.filename is deprecated. Please use path.basename instead.");
   };
   exports.basename = function (path, ext) {
     var f = path.substr(path.lastIndexOf("/") + 1);
     if (ext && f.substr(-1 * ext.length) === ext) {
       f = f.substr(0, f.length - ext.length);
     }
     return f;
   };

   exports.extname = function (path) {
     var index = path.lastIndexOf('.');
     return index < 0 ? '' : path.substring(index);
   };

 });

 var path = pathModule.exports;

 function findModulePath (id, dirs) {

   if (dirs.length == 0) {
     return;
   }

   var dir = dirs[0];
   var rest = dirs.slice(1, dirs.length);

   if (id.charAt(0) == '/') {
     dir = '';
     rest = [];
   }

   var locations = [
     path.join(dir, id),
     path.join(dir, id + ".js"),
     path.join(dir, id, "index.js")
   ];


   var searchLocations = function() {
       var location = locations.shift();
       if (location === undefined) {
           return findModulePath(id, rest);
       }

       var resource = org.eclipse.dirigible.api.v3.platform.RepositoryFacade.getResource("/registry/public" + location)
       if (resource.exists()) {
           return location;
       }  else {

           try {
             var registry = org.eclipse.dirigible.api.v3.platform.RegistryFacade;
             resource = registry.getText(location);
             if (resource && resource.length && resource.length > 0) {
                 return location;
             }
           } catch (error) {
             return searchLocations();
           }

       }
       return searchLocations();
   }

   return searchLocations();
 }

 function loadModule (request, parent) {


   var id, paths;
   if (request.charAt(0) == "." && (request.charAt(1) == "/" || request.charAt(1) == ".")) {
     var parentIdPath = path.dirname(parent.id +
       (path.basename(parent.filename).match(/^index\.(js|addon)$/) ? "/" : ""));
     id = path.join(parentIdPath, request);
     paths = [path.dirname(parent.filename)];
   } else {
     id = request;
     // debug("ABSOLUTE: id="+id);
     paths = ["/"];
   }

   if (id in moduleCache) {
     debug("found  " + JSON.stringify(id) + " in cache");
     // In cache
     var module = moduleCache[id];

     return module.exports;
   } else {
     debug("looking for " + JSON.stringify(id) + " in " + JSON.stringify(paths));
     // Not in cache
     let filename = findModulePath(request, paths);
     if (!filename) {
       throw new Error("Cannot find module '" + request + "'");
     } else {
       var module = createModule(id, parent);
       return module.load(filename);
     }
   }

 };

 Module.prototype.load = function (filename) {
   debug("load " + JSON.stringify(filename) + " for module " + JSON.stringify(this.id));

   this.filename = filename;

   return this.loadScript(filename);
 };

 Module.prototype.loadScript = function (filename) {
       var self = this;
       let content = SourceProvider.loadSource(filename);
       content = content.replace(/^\#\!.*/, '');

       function require (url) {
           return loadModule(url, self);
       }

       require.paths = ["/"];
       require.main = __context.get("main_module");
       // create wrapper function
       var wrapper = "var __wrap__ = function (exports, require, module, __filename, __dirname) { "
                   + content
                   + "\n}; __wrap__;";


       try {
           var compiledWrapper = load({
            name: filename,
            script: wrapper
           });
           compiledWrapper.apply(self.exports, [self.exports, require, self, filename, path.dirname(filename)]);
           return self.exports;
       } catch (e) {
           throw e;
       }

 };

 Module.prototype.loadScriptString = function (script) {
       var self = this;
       let filename = "index.js"

       let content = script;
       content = content.replace(/^\#\!.*/, '');

       function require (url) {
           return loadModule(url, self);
       }

       require.paths = ["/"];
       require.main = __context.get("main_module");
       // create wrapper function
       var wrapper = "var __wrap__ = function (exports, require, module, __filename, __dirname) { "
                   + content
                   + "\n}; __wrap__;";


       try {
           var compiledWrapper = load({
             name: filename,
             script: wrapper
           });
           compiledWrapper.apply(self.exports, [self.exports, require, self, filename, path.dirname(filename)]);
           return self.exports;
       } catch (e) {
           throw e;
       }

 };
