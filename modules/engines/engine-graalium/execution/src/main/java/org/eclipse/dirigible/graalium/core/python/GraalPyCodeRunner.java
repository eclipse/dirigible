package org.eclipse.dirigible.graalium.core.python;

import org.eclipse.dirigible.graalium.core.CodeRunner;
import org.eclipse.dirigible.graalium.core.graal.ContextCreator;
import org.eclipse.dirigible.graalium.core.graal.EngineCreator;
import org.graalvm.polyglot.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GraalPyCodeRunner implements CodeRunner<Source, Value> {
    private final Context context;

    public GraalPyCodeRunner(
            Path workingDirectoryPath,
            Path projectDirectoryPath,
<<<<<<< HEAD
<<<<<<< Upstream, based on f530b92c25bd5140f1917d1397dc6fd7ea035287
<<<<<<< HEAD
=======
>>>>>>> 943f21dcc743d4917a07a779fbb85d19b8d6986f
            Path pythonModulesPath,
            boolean debug
    ) {
        var engine = debug ? EngineCreator.getOrCreateDebuggableEngine() : EngineCreator.getOrCreateEngine();
<<<<<<< HEAD
=======
            Path pythonModulesPath
=======
            Path pythonModulesPath,
            boolean debug
>>>>>>> c30d33d feat: enable python debugging
    ) {
<<<<<<< Upstream, based on f530b92c25bd5140f1917d1397dc6fd7ea035287
        var engine = EngineCreator.getOrCreateEngine();
>>>>>>> 47ad2a6b2d (feat: initial python support)
=======
        var engine = debug ? EngineCreator.getOrCreateDebuggableEngine() : EngineCreator.getOrCreateEngine();
>>>>>>> c30d33d feat: enable python debugging
=======
>>>>>>> 943f21dcc743d4917a07a779fbb85d19b8d6986f
        var fs = new GraalPyFileSystem(workingDirectoryPath, FileSystems.getDefault());
        context = new ContextCreator(engine, workingDirectoryPath, projectDirectoryPath, pythonModulesPath, fs).createContext();
    }

    @Override
    public Source prepareSource(Path codeFilePath) {
        try {
            return Source.newBuilder("python", codeFilePath.toFile()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Value run(Source codeSource) {
        return context.eval(codeSource);
    }

    @Override
    public void close() {
        context.close();
    }
}
