package org.eclipse.dirigible.engine.js.graalium.execution;

import org.eclipse.dirigible.engine.js.graalium.execution.eventloop.GraalJSEventLoop;
import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.*;
import org.junit.Test;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class GraalJSCodeRunnerTest {

    @Test
    public void testDownloadFirebaseDependency() throws InterruptedException {
        Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/dependencies");
        GraalJSEventLoop loopedCodeRunner = new GraalJSEventLoop(
                20, TimeUnit.MINUTES,
                (looper) -> createLoopedCodeRunner(workingDir, looper)
        );

        Path codePath = Path.of("firebase-test.mjs");
        loopedCodeRunner.loop(codePath);

        int a = 5;
    }

    @Test
    public void testNewEngineWithEventLooper() throws InterruptedException {
        Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/timers");
        GraalJSEventLoop loopedCodeRunner = new GraalJSEventLoop(
                20, TimeUnit.MINUTES,
                (looper) -> createLoopedCodeRunner(workingDir, looper)
        );

        Path codePath = Path.of("timers-test.mjs");
        loopedCodeRunner.loop(codePath);
        int a = 5;
    }

    @Test
    public void testNewEngineWithDirigibleImports() {
        Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/ecmascript");
        GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

        Path codePath = Path.of("importDirigibleApi.mjs");
        codeRunner.run(codePath);
    }

    @Test
    public void testNewEngineWithRelativeImports() {
        Path workingDir = Path.of("/Users/c5326377/work/dirigible/dirigible/modules/engines/engine-javascript-graalvm/src/test/resources/META-INF/dirigible/graalvm/ecmascript");
        GraalJSCodeRunner codeRunner = createCodeRunner(workingDir);

        Path codePath = Path.of("relativeImports/l12/l12.mjs");
        codeRunner.run(codePath);
    }

    private GraalJSCodeRunner createLoopedCodeRunner(Path workingDir, GraalJSEventLoop looper) {
        return new GraalJSCodeRunner.Builder(workingDir)
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

    private static GraalJSCodeRunner createCodeRunner(Path workingDir) {
        return new GraalJSCodeRunner.Builder(workingDir)
                .addJSPolyfill(new RequirePolyfill())
                .waitForDebugger(false)
                .build();
    }

}