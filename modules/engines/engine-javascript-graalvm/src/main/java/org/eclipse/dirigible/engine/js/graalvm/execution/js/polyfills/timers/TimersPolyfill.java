package org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.timers;

import org.eclipse.dirigible.engine.js.graalvm.execution.js.polyfills.JSPolyfill;

public class TimersPolyfill implements JSPolyfill {
    @Override
    public String getSource() {
        return "function setTimeout(fn, delay) {\n" +
                "    const loop = getCurrentLoop();\n" +
                "    return loop.setTimeout(fn, delay);\n" +
                "}\n" +
                "\n" +
                "function setInterval(fn, delay) {\n" +
                "    const loop = getCurrentLoop();\n" +
                "    return loop.setInterval(fn, delay);\n" +
                "}\n" +
                "\n" +
                "function clearInterval(timerId) {\n" +
                "    const loop = getCurrentLoop();\n" +
                "    loop.clearInterval(timerId);\n" +
                "}\n" +
                "\n" +
                "function clearTimeout(timerId) {\n" +
                "    const loop = getCurrentLoop();\n" +
                "    loop.clearTimeout(timerId);\n" +
                "}\n" +
                "\n" +
                "function setImmediate(fn) {\n" +
                "    const loop = getCurrentLoop();\n" +
                "    loop.setImmediate(fn);\n" +
                "}\n" +
                "\n" +
                "function getCurrentLoop() {\n" +
                "    return Java.type(\"org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop.EventLoop\").CURRENT_EVENT_LOOP.get();\n" +
                "}";
    }

    @Override
    public String getFileName() {
        return "timers.js";
    }
}
