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
package org.eclipse.dirigible.graalium.engine;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.graalium.handler.GraaliumJavascriptHandler;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class GraaliumJavascriptEngineExecutor extends AbstractJavascriptExecutor {
	
	/** The Constant ENGINE_NAME. */
    public static final String ENGINE_NAME = "Graalium JavaScript Engine";
    
    /** The Constant JAVASCRIPT_TYPE_GRAALIUM. */
	public static final String JAVASCRIPT_TYPE_GRAALIUM = "graalium";

	/** The JavaScript handler. */
    private final GraaliumJavascriptHandler javascriptHandler = new GraaliumJavascriptHandler();

	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_GRAALIUM;
	}

	@Override
	public String getName() {
		return ENGINE_NAME;
	}

	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(module, executionContext, true, true);
	}

	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		String path = storeToRegistry(code);
		return executeService(path, executionContext, false, true);
	}

	@Override
	public Object evalCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		String path = storeToRegistry(code);
		return executeService(path, executionContext, false, false);
	}
	
	private String storeToRegistry(String code) {
		RepositoryPath path = new RepositoryPath(IRepositoryStructure.PATH_REGISTRY_PUBLIC,  "__generated__", code.hashCode() + ".js");
		getRepository().createResource(path.build(), code.getBytes());
		return path.constructPathFrom(2);
	}

	@Override
	public Object evalModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		return executeService(module, executionContext, true, false);
	}

	@Override
	public Object executeMethodFromModule(String module, String memberClass, String memberMethod,
			Map<Object, Object> executionContext) {
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
	
	/**
     * Execute service.
     *
     * @param moduleOrCode the module or code
     * @param executionContext the execution context
     * @param isModule the is module
     * @param commonJSModule the common JS module
     * @return the object
     * @throws ScriptingException the scripting exception
     */
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
     * @param commonJSModule the common JS module
     * @param onAfterExecute the on after execute
     * @return the object
     * @throws ScriptingException the scripting exception
     */
    public Object executeService(String moduleOrCode, Map<Object, Object> executionContext, boolean isModule, boolean commonJSModule, BiConsumer<Context, Value> onAfterExecute) throws ScriptingException {
    	RepositoryPath path = new RepositoryPath(moduleOrCode);
    	String projectName;
    	String projectFilePath;
    	if (path.getSegments().length > 1) {
    		projectName = path.getSegments()[0];
    		projectFilePath = path.constructPathFrom(1);
    	} else {
    		projectName = "";
    		projectFilePath = path.constructPathFrom(0);
    	}
    	return javascriptHandler.handleRequest(projectName, projectFilePath, "", executionContext, false);
	}

}
