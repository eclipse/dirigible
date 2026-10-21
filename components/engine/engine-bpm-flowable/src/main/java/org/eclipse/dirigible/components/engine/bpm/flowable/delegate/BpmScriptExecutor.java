/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.bpm.flowable.delegate;

import java.util.Map;

import org.flowable.engine.delegate.BpmnError;

/**
 * Runs the script handler of a {@code ${JSTask}} service task or a {@code ${JSListener}} task
 * listener.
 *
 * <p>
 * The JavaScript engine provides the implementation. Where none is deployed both beans still
 * resolve - deployed process definitions name them - and the step fails with the missing engine as
 * the reason.
 */
public interface BpmScriptExecutor {

    /**
     * Runs the handler.
     *
     * @param handler the handler expression: the registry path of the script, optionally followed by
     *        {@code /method} or {@code /Class/method} naming an export to call
     * @param context the objects bound as the script's context - the execution or the task
     * @throws BpmnError when the script throws one, so the process routes it as a BPMN error
     */
    void execute(String handler, Map<Object, Object> context);
}
