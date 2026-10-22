/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.bridges.bpm;

import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.dirigible.components.engine.bpm.flowable.delegate.BpmScriptExecutor;
import org.eclipse.dirigible.components.engine.bpm.flowable.delegate.BpmnRuntimeException;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.flowable.engine.delegate.BpmnError;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;

/**
 * Runs the JavaScript handler of a {@code ${JSTask}} or {@code ${JSListener}}: the module is
 * evaluated with the execution or the task bound, then the export the handler expression names is
 * called, if it names one.
 */
@Component
class JavascriptBpmScriptExecutor implements BpmScriptExecutor {

    /** The js expression regex. */
    private static final Pattern JS_EXPRESSION_REGEX = Pattern.compile("(.*\\.(?:m?js|ts))(?:\\/(\\w*))?(?:\\/(\\w*))?");

    @Override
    public void execute(String handler, Map<Object, Object> context) {
        JSTask task = JSTask.fromHandler(handler);
        try (DirigibleJavascriptCodeRunner runner = new DirigibleJavascriptCodeRunner(context, false)) {
            Source source = runner.prepareSource(task.sourceFilePath());
            Value value = runner.run(source);

            if (task.hasExportedClassAndMethod()) {
                value.getMember(task.className())
                     .newInstance()
                     .getMember(task.methodName())
                     .executeVoid();
            } else if (task.hasExportedMethod()) {
                value.getMember(task.methodName())
                     .executeVoid();
            }
        } catch (PolyglotException e) {
            // A BPMN error the script throws reaches the engine as itself, so the process routes it.
            if (e.isHostException() && e.asHostException() instanceof BpmnError bpmnError) {
                throw bpmnError;
            }
            throw e;
        }
    }

    /**
     * A handler expression: the module, and the export to call - a function, or a method of a new
     * instance of an exported class.
     *
     * @param sourceFilePath the source file path
     * @param className the exported class, when the expression names a class and a method
     * @param methodName the export or method to call, when the expression names one
     */
    private record JSTask(Path sourceFilePath, @Nullable String className, @Nullable String methodName) {

        static JSTask fromHandler(String handler) {
            Matcher matcher = JS_EXPRESSION_REGEX.matcher(handler);
            if (!matcher.matches()) {
                throw new BpmnRuntimeException(
                        "Invalid JS expression provided for task! Path [" + handler + "] doesn't match " + JS_EXPRESSION_REGEX);
            }
            Path sourceFilePath = Path.of(matcher.group(1));
            if (matcher.group(2) != null && matcher.group(3) != null) {
                return new JSTask(sourceFilePath, matcher.group(2), matcher.group(3));
            }
            return new JSTask(sourceFilePath, null, matcher.group(2));
        }

        boolean hasExportedClassAndMethod() {
            return className != null && methodName != null;
        }

        boolean hasExportedMethod() {
            return className == null && methodName != null;
        }
    }
}
