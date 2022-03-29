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
package org.eclipse.dirigible.engine.js.graalvm.callbacks;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

/**
 * This class contains the source code of the require() function (by CommonJS
 * specification), added to the JavaScript scripting service execution with
 * Nashorn engine, where it is not included by default.
 */
public class Require {

    /**
     * The Constant CODE.
     */
    public static final String CODE = createRequireFunctionCode("require");
    public static final String DIRIGIBLE_REQUIRE_CODE = createRequireFunctionCode("dirigibleRequire");

    private static final String createRequireFunctionCode(String functionName) {
        return "var Require = (function(modulePath) {" //
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
                + "   compiledWrapper = load({ name: path, script: code});" //
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
                + "globalThis." + functionName + " = Require();";
    }

    public static final String MODULE_CODE() throws IOException {
        return IOUtils.toString(Require.class.getResourceAsStream("/Module.js"), Charset.defaultCharset());
    }

    public static final String MODULE_CREATE_CODE = "let mainModule = createModule(\".\");\n" +
            "mainModule;";

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
