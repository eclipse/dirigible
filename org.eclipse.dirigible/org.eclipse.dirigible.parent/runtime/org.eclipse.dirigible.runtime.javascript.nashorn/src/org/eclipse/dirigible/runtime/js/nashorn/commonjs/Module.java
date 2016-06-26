/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Coveo
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * Originated from:
 * https://github.com/coveo/nashorn-commonjs-modules
 */

package org.eclipse.dirigible.runtime.js.nashorn.commonjs;

import java.util.HashMap;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ECMAException;

public class Module extends SimpleBindings implements RequireFunction {
	private NashornScriptEngine engine;
	private ModuleCache cache;
	private ISourceProvider provider;

	private Bindings module = new SimpleBindings();
	private Bindings exports = new SimpleBindings();

	public Module(NashornScriptEngine engine, ModuleCache cache, String filename, Bindings global, ISourceProvider provider) {

		this.engine = engine;
		this.cache = cache;
		this.provider = provider;

		global.put("require", this);

		global.put("module", module);
		global.put("exports", exports);

		module.put("exports", exports);
		module.put("filename", filename);
		module.put("id", filename);
		module.put("loaded", false);
		module.put("provider", provider);
	}

	void setLoaded() {
		module.put("loaded", true);
	}

	@Override
	public Bindings require(String module) throws ScriptException {
		if (module == null) {
			throwModuleNotFoundException("<null>");
		}

		Module found = null;
		try {
			found = loadModule(module);
		} catch (Exception e) {
			throwModuleNotFoundException(module + ": " + e.getMessage());
		}

		if (found == null) {
			throwModuleNotFoundException(module);
		}

		return found.exports;
	}

	private Module loadModule(String name) throws Exception {

		Module found = cache.get(name);

		if (found == null) {
			found = loadModuleDirectly(name);
		}

		if (found != null) {
			// We keep a cache entry for the requested path even though the code that
			// compiles the module also adds it to the cache with the potentially different
			// effective path. This avoids having to load package.json every time, etc.
			cache.put(name, found);
		}

		return found;
	}

	private Module loadModuleDirectly(String name) throws Exception {
		String code = provider.loadSource(name);
		if (code == null) {
			return null;
		}

		return compileModuleAndPutInCache(name, code);
	}

	private Module compileModuleAndPutInCache(String name, String code) throws ScriptException {

		Module created;
		created = compileJavaScriptModule(name, code);

		// We keep a cache entry for the compiled module using it's effective path, to avoid
		// recompiling even if module is requested through a different initial path.
		cache.put(name, created);

		return created;
	}

	private Module compileJavaScriptModule(String name, String code) throws ScriptException {

		// We take a copy of the current engine scope and include it in the module scope.
		// Otherwise the eval'd code would run with a blank engine scope.
		Bindings engineScope = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		Bindings moduleGlobal = new SimpleBindings(new HashMap(engineScope));
		Module created = new Module(engine, cache, name, moduleGlobal, this.provider);
		engine.eval(code, moduleGlobal);

		// Scripts are free to replace the global exports symbol with their own, so we
		// reload it from the module object after compiling the code.
		created.exports = (Bindings) created.module.get("exports");

		created.setLoaded();
		return created;
	}

	private void throwModuleNotFoundException(String module) throws ScriptException {
		ScriptObjectMirror ctor = (ScriptObjectMirror) engine.eval("Error");
		Bindings error = (Bindings) ctor.newObject("Module not found: " + module);
		error.put("code", "MODULE_NOT_FOUND");
		throw new ECMAException(error, null);
	}

}
