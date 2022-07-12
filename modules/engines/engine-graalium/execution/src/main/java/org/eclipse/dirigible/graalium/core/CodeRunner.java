package org.eclipse.dirigible.graalium.core;

import java.nio.file.Path;

public interface CodeRunner<TSource, TResult> extends AutoCloseable {
    TResult run(Path codeFilePath);

    TResult run(TSource codeSource);

    @Override
    void close();
}
