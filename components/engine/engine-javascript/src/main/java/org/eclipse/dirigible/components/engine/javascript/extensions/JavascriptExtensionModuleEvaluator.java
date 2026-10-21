/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.extensions;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleEvaluator;
import org.eclipse.dirigible.components.extensions.modules.ExtensionModuleExport;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * Evaluates script extension modules in GraalJS, the way the script API's
 * {@code extensions.loadExtensionModules} loaded them: a module is required as a CommonJS module,
 * and one that cannot be required is imported as an ES module.
 */
@Component
class JavascriptExtensionModuleEvaluator implements ExtensionModuleEvaluator {

    @Override
    public Session openSession() {
        return new JavascriptSession(new DirigibleJavascriptCodeRunner());
    }

    /**
     * One GraalJS context, shared by every module of an aggregation.
     */
    private static final class JavascriptSession implements Session {

        private static final Logger LOGGER = LoggerFactory.getLogger(JavascriptSession.class);

        private static final JsonMapper JSON = JsonMapper.builder()
                                                         .build();

        private static final TypeReference<Map<String, Object>> OBJECT = new TypeReference<>() {};

        private final DirigibleJavascriptCodeRunner runner;

        private JavascriptSession(DirigibleJavascriptCodeRunner runner) {
            this.runner = runner;
        }

        @Override
        public Optional<ExtensionModuleExport> evaluate(String module, List<String> exportNames, List<Object> arguments) {
            Value exports = load(module);
            if (exports == null || !exports.hasMembers()) {
                return Optional.empty();
            }
            for (String exportName : exportNames) {
                Value export = exports.getMember(exportName);
                if (export != null && export.canExecute()) {
                    Value result = export.execute(arguments.toArray());
                    return Optional.of(new ExtensionModuleExport(exportName, toMap(module, exportName, result)));
                }
            }
            return Optional.empty();
        }

        private Value load(String module) {
            try {
                return bindings().getMember("dirigibleRequire")
                                 .execute(module);
            } catch (PolyglotException e) {
                LOGGER.debug("Extension module [{}] cannot be required, importing it as an ES module", module, e);
                String relativePath = module.startsWith("/") ? module.substring(1) : module;
                return runner.run(Path.of(relativePath))
                             .module();
            }
        }

        private Map<String, Object> toMap(String module, String exportName, Value result) {
            if (result == null || !result.hasMembers() || result.hasArrayElements()) {
                throw new IllegalStateException(
                        "Export [" + exportName + "] of extension module [" + module + "] did not return an object");
            }
            String json = bindings().getMember("JSON")
                                    .invokeMember("stringify", result)
                                    .asString();
            return JSON.readValue(json, OBJECT);
        }

        private Value bindings() {
            return runner.getCodeRunner()
                         .getGraalContext()
                         .getBindings("js");
        }

        @Override
        public void close() {
            runner.close();
        }
    }
}
