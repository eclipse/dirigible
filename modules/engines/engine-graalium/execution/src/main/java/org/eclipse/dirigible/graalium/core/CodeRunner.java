package org.eclipse.dirigible.graalium.core;

import java.nio.file.Path;

/**
 * The Interface CodeRunner.
 *
 * @param <TSource> the generic type
 * @param <TResult> the generic type
 */
public interface CodeRunner<TSource, TResult> extends AutoCloseable {
	
    /**
     * Run.
     *
     * @param codeFilePath the code file path
     * @return the t result
     */
    TResult run(Path codeFilePath);

    /**
     * Run.
     *
     * @param codeSource the code source
     * @return the t result
     */
    TResult run(TSource codeSource);

    /**
     * Close.
     */
    @Override
    void close();
}
