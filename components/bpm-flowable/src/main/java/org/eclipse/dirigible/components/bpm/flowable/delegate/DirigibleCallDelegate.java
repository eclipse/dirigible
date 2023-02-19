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
package org.eclipse.dirigible.components.bpm.flowable.delegate;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.components.bpm.flowable.dto.ExecutionData;
import org.eclipse.dirigible.components.engine.javascript.service.JavascriptService;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.beans.BeanUtils;

/**
 * The Class DirigibleCallDelegate.
 */
public class DirigibleCallDelegate implements JavaDelegate {

    /** The handler. */
    private FixedValue handler;

    /** The type. */
    private FixedValue type;

    /**
     * Getter for the handler attribute.
     *
     * @return the handler
     */
    public FixedValue getHandler() {
        return handler;
    }

    /**
     * Setter of the handler attribute.
     *
     * @param handler the handler
     */
    public void setHandler(FixedValue handler) {
        this.handler = handler;
    }

    /**
     * Getter for the engine attribute.
     *
     * @return the type
     */
    public FixedValue getType() {
        return type;
    }

    /**
     * Setter of the engine attribute.
     *
     * @param type the type
     */
    public void setType(FixedValue type) {
        this.type = type;
    }

    /**
     * Execute.
     *
     * @param execution the execution
     */
    @Override
    public void execute(DelegateExecution execution) {

        try {
            Map<Object, Object> context = new HashMap<>();
            ExecutionData executionData = new ExecutionData();
            BeanUtils.copyProperties(execution, executionData);
            context.put("execution", GsonHelper.toJson(executionData));
            if (type == null) {
                type = new FixedValue("javascript");
            }
            if (handler == null) {
                throw new BpmnError("Handler cannot be null at the call delegate.");
            }

            executeJSHandler(context);

        } catch (ScriptingException e) {
            throw new BpmnError(e.getMessage());
        }
    }

    /**
     * Execute JS handler.
     *
     * @param context the context
     * @return the object
     */
    private Object executeJSHandler(Map<Object, Object> context) {
        RepositoryPath path = new RepositoryPath(handler.getExpressionText());
    	return JavascriptService.get().handleRequest(path.getSegments()[0], path.constructPathFrom(1), null, context, false);
    }

}
