package org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop;

import org.eclipse.dirigible.engine.js.graalvm.execution.js.GraalJSCodeRunner;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.executor.AutoShutdownableLoopExecutor;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.*;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.function.Function;

public class GraalJSEventLoop implements JSEventLoop, JSGlobalObject {
    private static final String LOOPER_CONTEXT_KEY = "looper";
    private final AutoShutdownableLoopExecutor timersExecutor = new AutoShutdownableLoopExecutor();

    private final long timeout;
    private final TimeUnit timeoutTimeUnit;
    private final GraalJSCodeRunner codeRunner;

    public GraalJSEventLoop(long timeout, TimeUnit timeoutTimeUnit, Function<GraalJSEventLoop, GraalJSCodeRunner> graalJSCodeRunnerProvider) {
        this.timeout = timeout;
        this.timeoutTimeUnit = timeoutTimeUnit;

        Callable<GraalJSCodeRunner> runnable = () -> graalJSCodeRunnerProvider.apply(this);
        Future<GraalJSCodeRunner> runnerFuture = timersExecutor.submit(runnable);
        try {
            codeRunner = runnerFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loop(Path codeFilePath) throws InterruptedException {
        timersExecutor.submit(() -> {
            try {
                codeRunner.run(codeFilePath);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        timersExecutor.await(timeout, timeoutTimeUnit);
    }

    public static GraalJSEventLoop getCurrent() {
        return Context.getCurrent().getBindings("js").getMember(LOOPER_CONTEXT_KEY).as(GraalJSEventLoop.class);
    }

    public ScheduledFuture<?> postDelayed(Runnable runnable, int delay) {
        return timersExecutor.submitWithDelay(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> postDelayedAndRepeat(Runnable runnable, int delay) {
        return timersExecutor.submitRepeatableWithDelay(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public void post(Runnable runnable) {
        timersExecutor.submit(runnable);
    }

    public <V> void postAsync(Callable<V> callable, Value onCompletedCallback, Value onFailedCallback) {
        timersExecutor.submitAsync(callable, onCompletedCallback::execute, onFailedCallback::execute);
    }

    @Override
    public String getName() {
        return LOOPER_CONTEXT_KEY;
    }

    @Override
    public Object getValue() {
        return this;
    }
}
