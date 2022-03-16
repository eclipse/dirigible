/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills;

import org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.GraalJSEventLoop;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.platform.GraalJSSourceCreator;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class TimersJSGlobalObject implements JSGlobalObject {

    private final Map<Integer, ScheduledFuture<?>> timersMap = new HashMap<>();
    private int currentTimerId = 0;

    private static GraalJSEventLoop getLooper() {
        return GraalJSEventLoop.getCurrent();
    }

    @Override
    public String getName() {
        return "timers";
    }

    @Override
    public Object getValue() {
        return this;
    }

    public void setImmediate(Value executable, Object... args) {
        Runnable runnable = () -> executable.execute(args);
        getLooper().post(runnable);
    }

    public void clearTimeout(int timerId) {
        stopAndClearTimer(timerId);
    }

    public void clearInterval(int timerId) {
        stopAndClearTimer(timerId);
    }

    public int setTimeout(String code, Integer delay) {
        Value parsedSource = parseSourceForTimer(code);
        return setTimeout(parsedSource, delay);
    }

    public int setInterval(String code, Integer delay) {
        Value parsedSource = parseSourceForTimer(code);
        return setInterval(parsedSource, delay);
    }

    public int setTimeout(Value executable, Integer delay, Object... args) {
        Runnable runnable = () -> executable.execute(args);
        ScheduledFuture<?> scheduledFuture = getLooper().postDelayed(runnable, zeroIfNull(delay));
        return storeScheduledFuture(scheduledFuture);
    }

    public int setInterval(Value executable, Integer delay, Object... args) {
        Runnable runnable = () -> executable.execute(args);
        ScheduledFuture<?> scheduledFuture = getLooper().postDelayedAndRepeat(runnable, zeroIfNull(delay));
        return storeScheduledFuture(scheduledFuture);
    }

    private int storeScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        currentTimerId += 1;
        timersMap.put(currentTimerId, scheduledFuture);
        return currentTimerId;
    }

    private void stopAndClearTimer(int timerId) {
        ScheduledFuture<?> removedTimer = timersMap.remove(timerId);
        if (removedTimer != null) {
            removedTimer.cancel(false);
        }
    }

    private static int zeroIfNull(Integer maybeInteger) {
        return maybeInteger != null ? maybeInteger : 0;
    }

    private Value parseSourceForTimer(String code) {
        Context currentContext = Context.getCurrent();
        Source source = GraalJSSourceCreator.createSource(code, "unknown created in timer");
        return currentContext.parse(source);
    }
}
