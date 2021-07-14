/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.core;

import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.command.processor.CommandEngineExecutor;

import java.util.List;
import java.util.Map;

/**
 * The ExecFacade is used to execute command line code.
 */
public class ExecFacade implements IScriptingFacade {

    /**
     * Execute service module.
     *
     * @param command
     *            the command line code
     * @param forAdding
     *            variables to be declared
     * @param forRemoving
     *            variables to be removed
     * @return the output of the command or error message
     */

    public static String exec(String command, Map<String, String> forAdding, List<String> forRemoving) {
        String result = null;
        try {
            CommandEngineExecutor engineExecutor = new CommandEngineExecutor();
            result = engineExecutor.executeCommandLine(command, forAdding, forRemoving, false);
        } catch (ScriptingException e) {
            result = e.getMessage();
        }
        return result;
    }

}
