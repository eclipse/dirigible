package org.eclipse.dirigible.engine.js.graalvm.execution.js.eventloop;

import java.nio.file.Path;

public interface JSEventLoop {
    void loop(Path codeFilePath) throws InterruptedException;
}
