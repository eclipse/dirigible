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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.dirigible.components.api.log.ScriptErrorExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.truffle.js.runtime.GraalJSException;
import com.oracle.truffle.js.runtime.Strings;
import com.oracle.truffle.js.runtime.builtins.JSErrorObject;

/**
 * Extracts a JavaScript {@code Error} a script hands to the log: the GraalJS exception behind it,
 * with the script's stack trace in place of the engine's. Registered as a
 * {@link java.util.ServiceLoader} provider of {@link ScriptErrorExtractor}.
 */
public class JavascriptErrorExtractor implements ScriptErrorExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavascriptErrorExtractor.class);

    @Override
    public Optional<Throwable> extract(Object scriptError) {
        Optional<Method> guestObjectMethod = getPublicMethod(scriptError, "getGuestObject");
        if (guestObjectMethod.isPresent()) {
            Object guestObject = invokeMethod(guestObjectMethod.get(), scriptError);
            if (guestObject instanceof JSErrorObject jsErrorObject) {
                if (jsErrorObject.isException()) {
                    GraalJSException graalJSException = jsErrorObject.getException();
                    GraalJSException.JSStackTraceElement[] jsStackTrace = graalJSException.getJSStackTrace();
                    StackTraceElement[] jsStackTraceInJavaFormat = toJavaStackTrace(jsStackTrace);

                    LOGGER.debug("A script error occurred (before changing the trace)", graalJSException);
                    // set js stack trace to be printed when logged
                    graalJSException.setStackTrace(jsStackTraceInJavaFormat);

                    return Optional.of(graalJSException);
                }
            }
        }
        return Optional.empty();
    }

    private static StackTraceElement[] toJavaStackTrace(GraalJSException.JSStackTraceElement[] jsStackTrace) {
        if (null == jsStackTrace) {
            return null;
        }

        return Arrays.stream(jsStackTrace)
                     .map(JavascriptErrorExtractor::toJavaStackTrace)
                     .toArray(StackTraceElement[]::new);
    }

    private static StackTraceElement toJavaStackTrace(GraalJSException.JSStackTraceElement jsStackTrace) {
        String declaringClass = "<js>";
        String methodName = jsStackTrace.getMethodName();
        String fileName = Strings.toJavaString(jsStackTrace.getFileName());
        int lineNumber = jsStackTrace.getLineNumber();

        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    private static Object invokeMethod(Method method, Object object) {
        try {
            method.trySetAccessible();
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException("Failed to call method " + method + " in object " + object, ex);
        }
    }

    private static Optional<Method> getPublicMethod(Object obj, String methodName) {
        try {
            return Optional.of(obj.getClass()
                                  .getMethod(methodName));
        } catch (NoSuchMethodException e) {
            LOGGER.debug("Missing method [{}] in object [{}]", methodName, obj, e);
            return Optional.empty();
        }
    }
}
