/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.jobs.handler;

/**
 * Runs a job whose handler is a script - every job that does not name the Java engine.
 *
 * <p>
 * The JavaScript engine provides the implementation. Where none is deployed a script job is not
 * scheduled: the job synchronizer registers it {@code FAILED}, with the missing engine as the
 * reason.
 */
public interface JobScriptExecutor {

    /**
     * Run the job's script.
     *
     * @param handler the registry path of the script, relative to the public registry
     * @throws Exception when the script throws, so the caller logs the run as failed
     */
    void execute(String handler) throws Exception;
}
