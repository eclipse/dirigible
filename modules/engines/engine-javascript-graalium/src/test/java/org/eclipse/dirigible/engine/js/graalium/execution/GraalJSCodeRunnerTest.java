package org.eclipse.dirigible.engine.js.graalium.execution;

import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.eclipse.dirigible.engine.js.graalium.execution.eventloop.GraalJSEventLoop;
import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.*;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.junit.Test;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class GraalJSCodeRunnerTest extends AbstractDirigibleTest {

    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    @Test
    public void testDownloadFirebaseDependency() throws InterruptedException {
        Path workingDir = Path.of("");
        GraalJSEventLoop loopedCodeRunner = new GraalJSEventLoop(
                20, TimeUnit.MINUTES,
                (looper) -> createLoopedCodeRunner(workingDir, looper)
        );

        Path codePath = Path.of("firebase-test.mjs");
        loopedCodeRunner.loop(codePath);
    }

    @Test
    public void testNewEngineWithEventLooper() throws InterruptedException {
        Path workingDir = Path.of("");
        GraalJSEventLoop loopedCodeRunner = new GraalJSEventLoop(
                20, TimeUnit.MINUTES,
                (looper) -> createLoopedCodeRunner(workingDir, looper)
        );

        Path codePath = Path.of("timers-test.mjs");
        loopedCodeRunner.loop(codePath);
    }

    @Test
    public void testNewEngineWithDirigibleImports() {
        Path workingDir = Path.of("");
        GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

        Path codePath = Path.of("importDirigibleApi.mjs");
        codeRunner.run(codePath);
    }

    @Test
    public void testNewEngineWithRelativeImports() {
        Path workingDir = Path.of("");
        GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

        Path codePath = Path.of("relativeImports/l12/l12.mjs");
        codeRunner.run(codePath);
    }

    private GraalJSCodeRunner createLoopedCodeRunner(Path workingDir, GraalJSEventLoop looper) {
        return GraalJSCodeRunner.newBuilder(workingDir, getOrCreateInternalFolder("dependencies"), getOrCreateInternalFolder("core-modules"))
                .addGlobalObject(looper)
                .addGlobalObject(new TimersJSGlobalObject())
                .addJSPolyfill(new GlobalPolyfill())
                .addJSPolyfill(new RequirePolyfill())
                .addJSPolyfill(new TimersPolyfill())
                .addJSPolyfill(new XhrPolyfill())
                .addJSPolyfill(new FetchPolyfill())
                .waitForDebugger(false)
                .build();
    }

    private static Path getOrCreateInternalFolder(String folderName) {
        ICollection folder = REPOSITORY.getCollection(folderName);
        if (!folder.exists()) {
            folder.create();
        }

        String dependenciesCollectionPathString = folder.getPath();
        String dependenciesCollectionInternalPathString = REPOSITORY.getInternalResourcePath(dependenciesCollectionPathString);
        return Path.of(dependenciesCollectionInternalPathString);
    }

    private static GraalJSCodeRunner createCodeRunner(Path workingDir) {
        return GraalJSCodeRunner.newBuilder(workingDir, getOrCreateInternalFolder("dependencies"), getOrCreateInternalFolder("core-modules"))
                .addJSPolyfill(new RequirePolyfill())
                .waitForDebugger(false)
                .build();
    }

}