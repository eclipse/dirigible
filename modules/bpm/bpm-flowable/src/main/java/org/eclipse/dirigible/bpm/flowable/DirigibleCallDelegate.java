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
package org.eclipse.dirigible.bpm.flowable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.bpm.flowable.dto.ExecutionData;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.resource.ResourcePath;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.beans.BeanUtils;

public class DirigibleCallDelegate implements JavaDelegate {

    private FixedValue handler;

    private FixedValue type;

    /**
     * Getter for the handler attribute
     *
     * @return the handler
     */
    public FixedValue getHandler() {
        return handler;
    }

    /**
     * Setter of the handler attribute
     *
     * @param handler the handler
     */
    public void setHandler(FixedValue handler) {
        this.handler = handler;
    }

    /**
     * Getter for the engine attribute
     *
     * @return the type
     */
    public FixedValue getType() {
        return type;
    }

    /**
     * Setter of the engine attribute
     *
     * @param type the type
     */
    public void setType(FixedValue type) {
        this.type = type;
    }

    @Override
    public void execute(DelegateExecution execution) {

        try {
            Map<Object, Object> context = new HashMap<>();
            ExecutionData executionData = new ExecutionData();
            BeanUtils.copyProperties(execution, executionData);
            context.put("execution", GsonHelper.GSON.toJson(executionData));
            if (type == null) {
                type = new FixedValue(IJavascriptEngineExecutor.JAVASCRIPT_TYPE_DEFAULT);
            }
            if (handler == null) {
                throw new BpmnError("Handler cannot be null at the call delegate.");
            }

            executeJSHandler(context);

        } catch (ScriptingException e) {
            throw new BpmnError(e.getMessage());
        }
    }

    private Object executeJSHandler(Map<Object, Object> context) {
        ResourcePath resourcePath = AbstractScriptExecutor.generateResourcePath(handler.getExpressionText(), new String[]{".mjs/", ".js/"});

        if (resourcePath.getModule().endsWith(".js")) {
            return ScriptEngineExecutorsManager.executeServiceModule(type.getExpressionText(), handler.getExpressionText(), context);
        }

        return executeES6ModuleMethod(resourcePath, context);
    }

    private Object executeES6ModuleMethod(ResourcePath resourcePath, Map<Object, Object> context) {
        String module = resourcePath.getModule();
        String member = resourcePath.getPath();

        String memberClass;
        String memberMethod;

        if (member.contains("/")) {
            String[] splits = member.split("/");
            memberClass = splits[0];
            memberMethod = splits[1];
        } else {
            memberClass = null;
            memberMethod = member;
        }

        return ScriptEngineExecutorsManager.executeMethodFromModule(type.getExpressionText(), module, memberClass, memberMethod, context);
    }

}
