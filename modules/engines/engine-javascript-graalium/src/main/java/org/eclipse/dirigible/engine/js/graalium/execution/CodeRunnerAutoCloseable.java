package org.eclipse.dirigible.engine.js.graalium.execution;

public interface CodeRunnerAutoCloseable extends AutoCloseable {
    @Override
    void close();
}
