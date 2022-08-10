package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.UnsupportedOperationException;

import org.eclipse.dirigible.graalium.core.javascript.GraalJSCodeRunner;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;

/**
 * The Class GraalTSCodeRunner.
 */
public class GraalTSCodeRunner implements TypescriptCodeRunner<Source, Value> {

    /** The code runner. */
    private final GraalJSCodeRunner codeRunner;

    /**
     * Instantiates a new graal TS code runner.
     *
     * @param codeRunner the code runner
     */
    private GraalTSCodeRunner(GraalJSCodeRunner codeRunner) {
        this.codeRunner = codeRunner;
    }

    /**
     * From existing code runner.
     *
     * @param codeRunner the code runner
     * @return the graal TS code runner
     */
    public static GraalTSCodeRunner fromExistingCodeRunner(GraalJSCodeRunner codeRunner) {
        return new GraalTSCodeRunner(codeRunner);
    }

    /**
     * Run.
     *
     * @param codeFilePath the code file path
     * @return the value
     */
    @Override
    public Value run(Path codeFilePath) {
        String codeFilePathString = codeFilePath.toString();
        TypescriptCompiler typescriptCompiler = new TypescriptCompiler(codeRunner.getCurrentWorkingDirectoryPath());
        typescriptCompiler.compile(codeFilePathString);
        String compiledCodeFilePathString = codeFilePathString.replace(".ts", ".js");
        Path compiledCodeFilePath = Path.of(compiledCodeFilePathString);
        return codeRunner.run(compiledCodeFilePath);
    }

    /**
     * Run.
     *
     * @param codeSource the code source
     * @return the value
     */
    @Override
    public Value run(Source codeSource) {
        throw new UnsupportedOperationException("Running Source objects is currently not supported");
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        codeRunner.close();
    }
}