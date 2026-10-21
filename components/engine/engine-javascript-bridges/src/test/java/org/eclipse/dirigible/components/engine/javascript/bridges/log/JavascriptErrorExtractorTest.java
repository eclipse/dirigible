/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.javascript.bridges.log;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.ServiceLoader;

import org.eclipse.dirigible.components.api.log.ScriptErrorExtractor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.Test;

/**
 * The log's unwrapping of a JavaScript error, moved out of {@code LogFacade} so the Java SDK's
 * logging no longer reaches GraalJS internals (#8006): it is still found by the log, and still
 * turns the error a script hands over into an exception carrying the script's stack trace.
 */
class JavascriptErrorExtractorTest {

    @Test
    void theLogFindsTheExtractor() {
        assertThat(ServiceLoader.load(ScriptErrorExtractor.class)
                                .stream()
                                .map(ServiceLoader.Provider::type)).contains(JavascriptErrorExtractor.class);
    }

    @Test
    void aScriptErrorBecomesAnExceptionWithTheScriptStackTrace() {
        ErrorSink sink = new ErrorSink();
        // Nashorn compatibility as the platform's contexts run it - it is what names a stack frame by
        // its function, where plain GraalJS names only the frames of method calls.
        try (Context context = Context.newBuilder("js")
                                      .allowAllAccess(true)
                                      .option("js.nashorn-compat", "true")
                                      .option("engine.WarnInterpreterOnly", "false")
                                      .build()) {
            context.getBindings("js")
                   .putMember("sink", sink);
            context.eval(Source.newBuilder("js",
                    "function failing() { throw new Error('boom'); } try { failing(); } catch (e) { sink.log(e); }", "failing.js")
                               .buildLiteral());
        }

        assertThat(sink.extracted).hasValueSatisfying(error -> {
            assertThat(error.getMessage()).contains("boom");
            assertThat(error.getStackTrace()).anySatisfy(element -> assertThat(element.getMethodName()).isEqualTo("failing"));
        });
    }

    @Test
    void anObjectThatIsNoScriptErrorIsLeftToTheLog() {
        assertThat(new JavascriptErrorExtractor().extract("not an error")).isEmpty();
    }

    /**
     * Stands in for the log: it receives the error inside the script's call, as the log does - the
     * script's stack trace is only readable while the script's context is entered.
     */
    public static final class ErrorSink {

        private Optional<Throwable> extracted = Optional.empty();

        /**
         * Receives the error.
         *
         * @param error the error the script passed
         */
        public void log(Object error) {
            this.extracted = new JavascriptErrorExtractor().extract(error);
        }
    }
}
