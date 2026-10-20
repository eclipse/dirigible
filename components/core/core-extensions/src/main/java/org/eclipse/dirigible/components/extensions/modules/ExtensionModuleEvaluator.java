/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.extensions.modules;

import java.util.List;
import java.util.Optional;

/**
 * Evaluates a script extension module - a {@code .js} / {@code .mjs} module an {@code .extension}
 * artefact registers - and calls one of its exports.
 *
 * <p>
 * The shell bootstrap reads {@code .json} extension modules itself and needs no script engine. A
 * script module keeps loading wherever an implementation of this interface is present (the
 * JavaScript engine provides one), so every module written before JSON modules existed keeps
 * working, and both kinds can be mixed on one extension point. Without an implementation a script
 * module is skipped with a warning.
 */
public interface ExtensionModuleEvaluator {

    /**
     * Opens an evaluation session. Every module of one aggregation is evaluated in the same session, so
     * the engine is started once per aggregation rather than once per module.
     *
     * @return the session, to be closed by the caller
     */
    Session openSession();

    /**
     * One evaluation session.
     */
    interface Session extends AutoCloseable {

        /**
         * Evaluates the module and calls the first of the given exports it provides.
         *
         * @param module the module path, relative to the public registry, as the extension declares it
         * @param exportNames the export names to try, in order
         * @return the export called and the object it returned, or empty when the module provides none of
         *         the exports
         * @throws RuntimeException when the module fails to load or the export fails to run
         */
        Optional<ExtensionModuleExport> evaluate(String module, List<String> exportNames);

        /**
         * Releases the engine resources the session holds.
         */
        @Override
        void close();
    }
}
