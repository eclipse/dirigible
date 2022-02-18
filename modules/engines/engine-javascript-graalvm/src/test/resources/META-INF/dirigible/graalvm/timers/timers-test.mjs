function setTimeout(fn, delay) {
    const loop = Java.type("org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.EventLoop").CURRENT_EVENT_LOOP.get();
    return loop.setTimeout(fn, delay);
}

function setInterval(fn, delay) {
    const loop = Java.type("org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.EventLoop").CURRENT_EVENT_LOOP.get();
    return loop.setInterval(fn, delay);
}

function clearInterval(timerId) {
    const loop = Java.type("org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.EventLoop").CURRENT_EVENT_LOOP.get();
    loop.clearInterval(timerId);
}

function clearTimeout(timerId) {
    const loop = Java.type("org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.EventLoop").CURRENT_EVENT_LOOP.get();
    loop.clearTimeout(timerId);
}

const intervalId = setInterval(() => {
}, 1000)

setTimeout(() => {
    clearInterval(intervalId);
}, 10000);
