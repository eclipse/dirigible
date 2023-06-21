/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.delegate;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.components.engine.bpm.flowable.dto.ExecutionData;
import org.eclipse.dirigible.components.engine.bpm.flowable.provider.BpmProviderFlowable;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.JavascriptSourceProvider;
import org.eclipse.dirigible.graalium.core.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.el.FixedValue;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;

/**
 * The Class DirigibleCallDelegate.
 */
public class DirigibleCallDelegate implements JavaDelegate {

    private static Pattern JS_EXPRESSION_REGEX = Pattern.compile("(.*\\.m?js)(?:\\/(\\w*))?(?:\\/(\\w*))?");

    /**
     * The handler.
     */
    private FixedValue handler;

    /**
     * The type.
     */
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

        } catch (Exception e) {
            throw new BpmnError(e.getMessage());
        }
    }

    /**
     * Execute JS handler.
     *
     * @param context the context
     */
    private void executeJSHandler(Map<Object, Object> context) {
        RepositoryPath path = new RepositoryPath(handler.getExpressionText());
        IRepository repository = BpmProviderFlowable.provideBean(IRepository.class);
        JavascriptSourceProvider sourceProvider = new DirigibleSourceProvider();

        JSTask task = JSTask.fromRepositoryPath(path);

        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner(context, false, repository, sourceProvider)) {
            Source source = runner.prepareSource(task.getSourceFilePath());
            Value value = runner.run(source);

            if (task.hasExportedClassAndMethod()) {
                value.getMember(task.getClassName()).newInstance().getMember(task.getMethodName()).executeVoid();
            } else if (task.hasExportedMethod()) {
                value.getMember(task.getMethodName()).executeVoid();
            }

        }
    }

    static class JSTask {
        private final Path sourceFilePath;
        private final @Nullable String className;
        private final @Nullable String methodName;
        private final boolean hasExportedClassAndMethod;
        private final boolean hasExportedMethod;

        JSTask(Path sourceFilePath, @Nullable String className, @Nullable String methodName) {
            this.sourceFilePath = sourceFilePath;
            this.className = className;
            this.methodName = methodName;
            this.hasExportedMethod = className == null && methodName != null;
            this.hasExportedClassAndMethod = className != null && methodName != null;
        }

        static JSTask fromRepositoryPath(RepositoryPath repositoryPath) {
            var matcher = JS_EXPRESSION_REGEX.matcher(repositoryPath.getPath());
            if (!matcher.matches()) {
                throw new BpmnError("Invalid JS expression provided for task!");
            }

            String maybeClassName;
            String maybeMethodName;

            if (matcher.group(2) != null && matcher.group(3) != null) {
                maybeClassName = matcher.group(2);
                maybeMethodName = matcher.group(3);
            } else {
                maybeClassName = null;
                maybeMethodName = matcher.group(2);
            }

            Path sourceFilePath = Path.of(matcher.group(1));
            return new JSTask(sourceFilePath, maybeClassName, maybeMethodName);
        }

        public Path getSourceFilePath() {
            return sourceFilePath;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean hasExportedClassAndMethod() {
            return hasExportedClassAndMethod;
        }

        public boolean hasExportedMethod() {
            return hasExportedMethod;
        }
    }

}
