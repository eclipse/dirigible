globalThis.setTimeout = function(fn, delay) {
    return timers.setTimeout(fn, delay);
}

globalThis.setInterval = function(fn, delay) {
    return timers.setInterval(fn, delay);
}

globalThis.clearInterval = function(timerId) {
    timers.clearInterval(timerId);
}

globalThis.clearTimeout = function(timerId) {
    timers.clearTimeout(timerId);
}

globalThis.setImmediate = function(fn) {
    timers.setImmediate(fn);
}