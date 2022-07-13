package org.eclipse.dirigible.graalium.core.typescript;

import java.lang.UnsupportedOperationException;

import org.eclipse.dirigible.graalium.core.javascript.GraalJSCodeRunner;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.nio.file.Path;

public class GraalTSCodeRunner implements TSCodeRunner<Source, Value> {

    private final GraalJSCodeRunner codeRunner;

    private GraalTSCodeRunner(GraalJSCodeRunner codeRunner) {
        this.codeRunner = codeRunner;
    }

    public static GraalTSCodeRunner fromExistingCodeRunner(GraalJSCodeRunner codeRunner) {
        return new GraalTSCodeRunner(codeRunner);
    }

    @Override
    public Value run(Path codeFilePath) {
        var codeFilePathString = codeFilePath.toString();
        var typescriptCompiler = new TSCompiler(codeRunner.getCurrentWorkingDirectoryPath());
        typescriptCompiler.compile(codeFilePathString);
        var compiledCodeFilePathString = codeFilePathString.replace(".ts", ".js");
        var compiledCodeFilePath = Path.of(compiledCodeFilePathString);
        return codeRunner.run(compiledCodeFilePath);
    }

    @Override
    public Value run(Source codeSource) {
        throw new UnsupportedOperationException("Running Source objects is currently not supported");
    }

    @Override
    public void close() {
        codeRunner.close();
    }
}