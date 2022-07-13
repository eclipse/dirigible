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
package org.eclipse.dirigible.engine.js.graalvm.processor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.api.resource.ResourcePath;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptModuleSourceProvider;
import org.eclipse.dirigible.engine.js.graalvm.callbacks.Require;
import org.eclipse.dirigible.engine.js.graalvm.debugger.GraalVMJavascriptDebugProcessor;
import org.eclipse.dirigible.engine.js.graalvm.processor.truffle.RegistryTruffleFileSystem;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.graalium.core.javascript.JSCodeRunner;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GraalVM Javascript Engine Executor.
 */
@SuppressWarnings("restriction")
public class GraalVMJavascriptEngineExecutor extends AbstractJavascriptExecutor implements JSCodeRunner<String, Object> {


    private static final Logger logger = LoggerFactory.getLogger(GraalVMJavascriptEngineExecutor.class);

    private static final String ENGINE_JAVA_SCRIPT = "js";
    private static final String SOURCE_PROVIDER = "SourceProvider";
    private static final String CODE_DEBUGGER = "debugger;\n\n";

    public static final String ENGINE_NAME = "GraalVM JavaScript Engine";

    public static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED = "DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_ENABLED";
    public static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT = "DIRIGIBLE_JAVASCRIPT_GRAALVM_DEBUGGER_PORT";
    public static final String DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA = "DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA";

    public static final String DEFAULT_DEBUG_PORT = "8081";

    private final GraalVMRepositoryModuleSourceProvider sourceProvider = new GraalVMRepositoryModuleSourceProvider(this, IRepositoryStructure.PATH_REGISTRY_PUBLIC);
    private final ExecutableFileTypeResolver executableFileTypeResolver = new ExecutableFileTypeResolver();

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceModule(java.lang.String,
     * java.util.Map)
     */
    @Override
    public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
        return executeService(module, executionContext, true, true);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#executeServiceCode(java.lang.String,
     * java.util.Map)
     */
    @Override
    public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
        return executeService(code, executionContext, false, true);
    }

    @Override
    public Object evalCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
        return executeService(code, executionContext, false, false);
    }

    @Override
    public Object evalModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
        return executeService(module, executionContext, true, false);
    }

    @Override
    public Object executeMethodFromModule(String module, String memberClass, String memberMethod, Map<Object, Object> executionContext) {
        CompletableFuture<Object> res = new CompletableFuture<>();
        executeService(module, executionContext, true, false,
                (context, value) -> {
                    if (memberClass != null && !memberClass.isEmpty()) {
                        Value memberClassValue = value.getMember(memberClass);
                        Value memberClassInstanceValue = memberClassValue.newInstance();
                        Value memberClassMethodValue = memberClassInstanceValue.getMember(memberMethod);
                        Value executionResult = memberClassMethodValue.execute();
                        res.complete(executionResult);
                    } else {
                        Value memberMethodValue = value.getMember(memberMethod);
                        Value executionResult = memberMethodValue.execute();
                        res.complete(executionResult);
                    }
                });
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new ScriptingException(e);
        }
    }

    @Override
    public Object run(Path codeFilePath) {
        return executeServiceModule(codeFilePath.toString(), new HashMap<>());
    }

    @Override
    public Object run(String codeSource) {
        return executeServiceCode(codeSource, new HashMap<>());
    }

    public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule, boolean commonJSModule) throws ScriptingException {
        return executeService(moduleOrCode, executionContext, isModule, commonJSModule, (c, v) -> {
        });
    }

    /**
     * Execute service.
     *
     * @param moduleOrCode     the module or code
     * @param executionContext the execution context
     * @param isModule         the is module
     * @return the object
     * @throws ScriptingException the scripting exception
     */
    public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule, boolean commonJSModule, BiConsumer<Context, Value> onAfterExecute) throws ScriptingException {
        if (moduleOrCode == null) {
            throw new ScriptingException("JavaScript module name cannot be null");
        }

        logger.trace("entering: executeServiceModule()"); //$NON-NLS-1$
        logger.trace("module or code=" + moduleOrCode); //$NON-NLS-1$

        if (executionContext == null) {
            executionContext = new HashMap<>();
        }

        if (isModule) {
            ResourcePath resourcePath = getResourcePath(moduleOrCode, MODULE_EXT_JS, MODULE_EXT_MJS, MODULE_EXT_GRAALVM);
            moduleOrCode = resourcePath.getModule();
            if (HttpRequestFacade.isValid()) {
                HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, resourcePath.getPath());
            }
        }
        boolean isDebugEnabled = isDebugEnabled();

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Bindings engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        engineBindings.put("polyglot.js.allowHostAccess", true);
        engineBindings.put("polyglot.js.allowHostClassLookup", (Predicate<String>) s -> true);

        Object result = null;
        GraalVMJavaScriptContextBuilder contextBuilder = new GraalVMJavaScriptContextBuilder();
        Context context = contextBuilder.createJavaScriptContext(moduleOrCode, projectName -> new RegistryTruffleFileSystem(this, projectName));

        try {
            Value bindings = context.getBindings(ENGINE_JAVA_SCRIPT);
            bindings.putMember(SOURCE_PROVIDER, getSourceProvider());
            bindings.putMember(JAVASCRIPT_ENGINE_TYPE, JAVASCRIPT_TYPE_GRAALVM);
            bindings.putMember(CONTEXT, executionContext);

            if (Boolean.parseBoolean(Configuration.get(DIRIGIBLE_JAVASCRIPT_GRAALVM_COMPATIBILITY_MODE_MOZILLA, "false"))) {
                context.eval(ENGINE_JAVA_SCRIPT, "load(\"nashorn:mozilla_compat.js\")");
            }

            String code;
            ExecutableFileType executableFileType = executableFileTypeResolver.resolveFileType(moduleOrCode, commonJSModule);
            if (executableFileType == ExecutableFileType.JAVASCRIPT_ESM) {
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.CODE, "internal-require.js").internal(true).build());
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.DIRIGIBLE_REQUIRE_CODE, "internal-dirigible-require.js").internal(true).build()); // alias of Require.CODE
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, "globalThis.console = require('core/v4/console');", "internal-console.js").internal(true).build());

                String fileName = isModule ? moduleOrCode : "unknown";
                code = (isModule ? loadSource(moduleOrCode) : moduleOrCode);
                Source src = Source.newBuilder(ENGINE_JAVA_SCRIPT, code, fileName).mimeType("application/javascript+module").build();

                beforeEval(context);
                Value evaluated = context.eval(src);
                onAfterExecute.accept(context, evaluated);
                result = null; // always return null as the evaluation of `evaluated.as(Object.class)` returns a PolyglotMap dying with the context
            } else if (executableFileType == ExecutableFileType.JAVASCRIPT_NODE_CJS) {
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.LOAD_CONSOLE_CODE, "internal-console.js").internal(true).build());
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.MODULE_CODE(), "Module.js").build());
                Object mainModule = context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.MODULE_CREATE_CODE, "internal-module-create-code.js").build()).as(Object.class);
                executionContext.put("main_module", mainModule);
                beforeEval(context);

                Source source;
                if (isModule) {
                    bindings.putMember("MODULE_FILENAME", moduleOrCode);
                    source = Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.MODULE_LOAD_CODE, "internal-module-load-code.js").build();
                } else {
                    bindings.putMember("SCRIPT_STRING", moduleOrCode);
                    source = Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.LOAD_STRING_CODE, "internal-module-load-string-code.js").build();
                }

                Value evaluated = context.eval(source);
                onAfterExecute.accept(context, evaluated);
            } else {
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, Require.CODE, "internal-require.js").internal(true).build());
                context.eval(Source.newBuilder(ENGINE_JAVA_SCRIPT, "globalThis.console = require('core/v4/console');", "internal-console.js").internal(true).build());
                code = (isModule ? loadSource(moduleOrCode) : moduleOrCode);
                if (isDebugEnabled) {
                    code = CODE_DEBUGGER + code;
                }

                beforeEval(context);
                Source source = Source.newBuilder(ENGINE_JAVA_SCRIPT, code, (isModule ? moduleOrCode : "unknown")).build();
                Value evaluated = context.eval(source);

                onAfterExecute.accept(context, evaluated);
                result = evaluated.as(Object.class);
            }

        } catch (Exception e) {
            if (e instanceof PolyglotException && ((PolyglotException) e).isHostException()) {
                Throwable hostException = ((PolyglotException) e).asHostException();
                throw new ScriptingException(hostException);
            }
            throw new ScriptingException(e);
        } finally {
            context.close();
        }

        logger.trace("exiting: executeServiceModule()");
        return result;
    }

    protected String loadSource(String module) throws IOException, URISyntaxException {
        return getSourceProvider().loadSource(module);
    }

    protected void beforeEval(Context context) throws IOException {

    }

    private boolean isDebugEnabled() {
        return GraalVMJavascriptDebugProcessor.haveUserSession(UserFacade.getName());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor#getType()
     */
    @Override
    public String getType() {
        return JAVASCRIPT_TYPE_GRAALVM;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.dirigible.engine.api.script.IEngineExecutor#getName()
     */
    @Override
    public String getName() {
        return ENGINE_NAME;
    }

    public IJavascriptModuleSourceProvider getSourceProvider() {
        return sourceProvider;
    }

    @Override
    public void close() {

    }
}
