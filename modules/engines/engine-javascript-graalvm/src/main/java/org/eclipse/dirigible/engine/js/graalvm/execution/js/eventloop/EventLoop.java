package org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop;

import org.eclipse.dirigible.engine.js.graalvm.execution.js.platform.GraalJSSourceCreator;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EventLoop {

    private final Map<Integer, Timer> timersCache = new HashMap<>();
    private int currentTimerId = 0;
    private final Queue<ExecutableValue> globalRegularEvents = new ArrayDeque<>();
    private final Queue<ExecutableValue> globalUnreadyTimerEvents = new ArrayDeque<>();
    private final List<ExecutableValue> globalReadyTimerEvents = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService timersExecutor = Executors.newScheduledThreadPool(1);

    public static ThreadLocal<EventLoop> CURRENT_EVENT_LOOP = new ThreadLocal<>();

    public EventLoop(Value initialExecutable) {
        this(List.of(initialExecutable));
    }

    EventLoop(List<Value> initialExecutables) {
        globalRegularEvents.addAll(initialExecutables.stream().map(ExecutableValue::new).collect(Collectors.toList()));
        CURRENT_EVENT_LOOP.set(this);
    }

    public int setTimeout(String code, Integer delay) {
        Value parsedSource = parseSourceForTimer(code);
        return setTimeout(parsedSource, delay);
    }

    public int setInterval(String code, Integer delay) {
        Value parsedSource = parseSourceForTimer(code);
        return setInterval(parsedSource, delay);
    }

    private Value parseSourceForTimer(String code) {
        Context currentContext = Context.getCurrent();
        Source source = GraalJSSourceCreator.createSource(code, "unknown created in timer");
        return currentContext.parse(source);
    }

    public int setTimeout(Value executable, Integer delay, Value... args) {
        ExecutableValue executableValue = registerExecutableTimerValue(executable, args);
        return scheduleTimeout(executableValue, zeroIfNull(delay));
    }

    public int setInterval(Value executable, Integer delay, Value... args) {
        ExecutableValue executableValue = registerExecutableTimerValue(executable, args);
        return scheduleInterval(executableValue, zeroIfNull(delay));
    }

    private ExecutableValue registerExecutableTimerValue(Value executable, Value[] args) {
        validateValueIsExecutable(executable);
        ExecutableValue executableValue = new ExecutableValue(executable, args);
        globalUnreadyTimerEvents.add(executableValue);
        return executableValue;
    }

    private static int zeroIfNull(Integer maybeInteger) {
        return maybeInteger != null ? maybeInteger : 0;
    }

    private int scheduleTimeout(ExecutableValue executableValue, int delay) {
        int timerId = currentTimerId++;
        ScheduledFuture<?> timerFuture = timersExecutor.schedule(() -> {
            globalReadyTimerEvents.add(executableValue);
            globalUnreadyTimerEvents.remove(executableValue);
            timersCache.remove(timerId);
        }, delay, TimeUnit.MILLISECONDS);

        Timer timer = new Timer(timerId, timerFuture, executableValue);
        timersCache.put(timerId, timer);
        return timerId;
    }

    private int scheduleInterval(ExecutableValue executableValue, int delay) {
        int timerId = currentTimerId++;
        ScheduledFuture<?> timerFuture = timersExecutor.scheduleAtFixedRate(() -> {
            globalReadyTimerEvents.add(executableValue);
            globalUnreadyTimerEvents.remove(executableValue);
        }, delay, delay, TimeUnit.MILLISECONDS);

        Timer timer = new Timer(timerId, timerFuture, executableValue);
        timersCache.put(timerId, timer);
        return timerId;
    }

    public void clearTimeout(int timerId) {
        clearTimer(timerId);
    }

    public void clearInterval(int timerId) {
        clearTimer(timerId);
    }

    private void clearTimer(int timerId) {
        Timer maybeTimer = timersCache.remove(timerId);
        if (maybeTimer != null) { // lol
            maybeTimer.getTimerFuture().cancel(false);
            globalUnreadyTimerEvents.remove(maybeTimer.timerExecutable);
        }
    }

    public void setImmediate(Value executable, Value... args) {
        validateValueIsExecutable(executable);
        ExecutableValue executableValue = new ExecutableValue(executable, args);
        globalRegularEvents.add(executableValue);
    }

    private void validateValueIsExecutable(Value maybeExecutable) {
        if (!maybeExecutable.canExecute()) {
            throw new RuntimeException("Passed argument is not executable!");
        }
    }

    public void loop() {
        while (!globalUnreadyTimerEvents.isEmpty() || !globalRegularEvents.isEmpty() || !globalReadyTimerEvents.isEmpty()) {
            if (globalRegularEvents.isEmpty() && globalReadyTimerEvents.isEmpty()) {
                // we have scheduled timers but no regular events left... sleep for now
//                Thread.sleepUntil(globalReadyTimerEvents.hasAny())
            }

            Queue<ExecutableValue> regularEvents = new ArrayDeque<>(globalRegularEvents);
            runReadyTimerEvents();

            for (ExecutableValue regularEvent : regularEvents) {
                regularEvent.execute();
                runReadyTimerEvents();
            }

            globalRegularEvents.removeAll(regularEvents);
        }
    }

    private void runReadyTimerEvents() {
        // readyTimerEvents.forEach(Value::execute);
        if (globalReadyTimerEvents.isEmpty()) {
            return;
        }

        List<ExecutableValue> currentIterableTimerExecutables = new ArrayList<>(globalReadyTimerEvents); // copy due to concurrency issues when iterating and removing
        for (ExecutableValue timerExecutable : currentIterableTimerExecutables) {
            timerExecutable.execute();
            globalReadyTimerEvents.remove(timerExecutable);
        }
    }

    static class ExecutableValue {
        private boolean isExecuted = false;
        private final Value executable;
        private final Value[] args;

        ExecutableValue(Value executable) {
            this.executable = executable;
            this.args = new Value[] {};
        }

        ExecutableValue(Value executable, Value[] args) {
            this.executable = executable;
            this.args = args;
        }

        public boolean isExecuted() {
            return isExecuted;
        }

        public void execute() {
            this.executable.execute(args);
            isExecuted = true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ExecutableValue that = (ExecutableValue) o;
            return isExecuted == that.isExecuted && executable.equals(that.executable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isExecuted, executable);
        }
    }

    class Timer {
        private final int id;
        private final ScheduledFuture<?> timerFuture;
        private final ExecutableValue timerExecutable;

        Timer(int id, ScheduledFuture<?> timerFuture, ExecutableValue timerExecutable) {
            this.id = id;
            this.timerFuture = timerFuture;
            this.timerExecutable = timerExecutable;
        }

        public int getId() {
            return id;
        }

        public ScheduledFuture<?> getTimerFuture() {
            return timerFuture;
        }

        public ExecutableValue getTimerExecutable() {
            return timerExecutable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Timer timer = (Timer) o;
            return id == timer.id && timerFuture.equals(timer.timerFuture) && timerExecutable.equals(timer.timerExecutable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, timerFuture, timerExecutable);
        }
    }
}
