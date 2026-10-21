/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.base.script;

/**
 * Thrown where a script handler has to run and no JavaScript engine is deployed on the instance.
 *
 * <p>
 * Every consumer of a script SPI raises this one exception, so an artefact refused for this reason
 * reads the same wherever it is reported: the synchronizer's {@code FAILED} state, a job log, a
 * failed process step.
 */
public class ScriptEngineUnavailableException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new exception for a handler that cannot run.
     *
     * @param handler the script handler that needs the engine
     */
    public ScriptEngineUnavailableException(String handler) {
        super("The handler [" + handler + "] needs the JavaScript engine, which is not deployed on this instance");
    }
}
