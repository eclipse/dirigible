package org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.nodejs;

import org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.JSPolyfill;

public class RequirePolyfill implements JSPolyfill {
    private static final String REQUIRE_IMPLEMENTATION = "var Require = (function(modulePath) {" //
            + "	var _loadedModules = {};" //
            + " var _require = function(path) {" //
            + " var moduleInfo, buffered, head = '(function(exports,module,require){ ', code = '', tail = '})', line = null;" //
            + " moduleInfo = _loadedModules[path];" //
            + " if (moduleInfo) {" //
            + "   return moduleInfo;" //
            + " }" //
            + " code = Java.type('org.eclipse.dirigible.engine.js.graalvm.execution.js.modules.DirigibleCoreModuleProvider').loadSource(path);" //
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
            + "globalThis.require = Require();" //
            + "globalThis.dirigibleRequire = globalThis.require;";

    @Override
    public String getSource() {
        return REQUIRE_IMPLEMENTATION;
    }

    @Override
    public String getFileName() {
        return "require.js";
    }
}
