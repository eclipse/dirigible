package org.eclipse.dirigible.engine.js.graalvm.execution;

import org.graalvm.polyglot.Value;

import java.nio.file.Path;

public interface CodeRunner {
    Value run(Path codeFilePath);
}
