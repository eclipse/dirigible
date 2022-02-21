package org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AutoShutdownableLoopExecutor {

    private final ScheduledExecutorService backgroundExecutor = Executors.newScheduledThreadPool(4);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger tasksCounter = new AtomicInteger(0);
    private boolean isInAwaitState = false;

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        isInAwaitState = true;
        return scheduledExecutorService.awaitTermination(timeout, unit);
    }

    public ScheduledFuture<?> submitWithDelay(Runnable command, long delay, TimeUnit unit) {
        return scheduledExecutorService.schedule(tracked(command), delay, unit);
    }

    public ScheduledFuture<?> submitRepeatableWithDelay(Runnable command, long delay, TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(tracked(command), delay, delay, unit);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return scheduledExecutorService.submit(tracked(task));
    }

    public void submit(Runnable task) {
        scheduledExecutorService.submit(tracked(task));
    }

    public <V> void submitAsync(Callable<V> callable, Consumer<V> onCompleted, Consumer<Exception> onFailed) {
        incrementTasksCount();
        backgroundExecutor.submit(() -> {
            try {
                V res = callable.call();
                scheduledExecutorService.submit(tracked(() -> onCompleted.accept(res)));
            } catch (Exception e) {
                scheduledExecutorService.submit(tracked(() -> onFailed.accept(e)));
            } finally {
                decrementTasksCount();
            }
        });
    }

    private Runnable tracked(Runnable runnable) {
        incrementTasksCount();
        return () -> {
            try {
                runnable.run();
            } finally {
                decrementTasksCount();
            }
        };
    }

    private <V> Callable<V> tracked(Callable<V> callable) {
        incrementTasksCount();
        return () -> {
            try {
                return callable.call();
            } finally {
                decrementTasksCount();
            }
        };
    }

    private void incrementTasksCount() {
        tasksCounter.incrementAndGet();
    }

    private void decrementTasksCount() {
        int currentTasksCount = tasksCounter.decrementAndGet();
        if (currentTasksCount <= 0 && isInAwaitState) {
            scheduledExecutorService.shutdown();
        }
    }
}
