package org.eclipse.dirigible.engine.js.graalium.execution.adapter;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.graalium.execution.CodeRunner;
import org.eclipse.dirigible.engine.js.graalium.execution.GraalJSCodeRunner;
import org.eclipse.dirigible.engine.js.graalium.execution.modules.DirigibleModuleProvider;
import org.eclipse.dirigible.engine.js.graalium.execution.platform.GraalJSSourceCreator;
import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.RequirePolyfill;
import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.internal.DirigibleContextGlobalObject;
import org.eclipse.dirigible.engine.js.graalium.execution.polyfills.internal.DirigibleEngineTypeGlobalObject;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class GraaliumJavascriptEngineExecutor extends AbstractJavascriptExecutor {

    private static final IRepository REPOSITORY = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    @Override
    public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
        Source moduleSource = loadModuleSource(module);
        ExecutableFile executableFile = createExecutableFile(module);
        try (CodeRunner codeRunner = createJSCodeRunner(executableFile.getWorkingDirectoryPath(), executionContext)) {
            return codeRunner.run(moduleSource).as(Object.class);
        }
    }

    @Override
    public Object evalModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
        Source moduleSource = loadModuleSource(module);
        ExecutableFile executableFile = createExecutableFile(module);
        try (CodeRunner codeRunner = createJSCodeRunner(executableFile.getWorkingDirectoryPath(), executionContext)) {
            return codeRunner.run(moduleSource).as(Object.class);
        }
    }

    @Override
    public Object executeMethodFromModule(String module, String memberClass, String memberClassMethod, Map<Object, Object> executionContext) {
        Source moduleSource = loadModuleSource(module);
        ExecutableFile executableFile = createExecutableFile(module);
        try (CodeRunner codeRunner = createJSCodeRunner(executableFile.getWorkingDirectoryPath(), executionContext)) {
            return codeRunner.run(moduleSource).as(Object.class);
        }
    }

    private static ExecutableFile createExecutableFile(String module) {
        Path modulePath = Path.of(module);
        Path repositoryRootPath = Path.of(REPOSITORY.getRepositoryPath());
        Path projectDirectoryPath = repositoryRootPath.resolve(modulePath);

        Path executableFilePath = Path.of("/");
        for (int partIndex = 1; partIndex < modulePath.getNameCount(); partIndex += 1) {
            executableFilePath = executableFilePath.resolve(modulePath.getName(partIndex));
        }

        return new ExecutableFile(projectDirectoryPath, executableFilePath);
    }

    private static class ExecutableFile {
        private final Path workingDirectoryPath;
        private final Path executableFilePath;

        private ExecutableFile(Path workingDirectoryPath, Path executableFilePath) {
            this.workingDirectoryPath = workingDirectoryPath;
            this.executableFilePath = executableFilePath;
        }

        public Path getWorkingDirectoryPath() {
            return workingDirectoryPath;
        }

        public Path getExecutableFilePath() {
            return executableFilePath;
        }
    }

    private static Source loadModuleSource(String module) {
        try {
            String moduleSourceString = DirigibleModuleProvider.loadSource(module);
            return GraalJSSourceCreator.createSource(moduleSourceString, module);
        } catch (IOException e) {
            throw new ScriptingException(e);
        }
    }

    @Override
    public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
        Source source = GraalJSSourceCreator.createSource(code, "Unknown");
        try (CodeRunner codeRunner = createJSCodeRunner(Path.of(REPOSITORY.getRepositoryPath()), executionContext)) {
            return codeRunner.run(source).as(Object.class);
        }
    }

    @Override
    public Object evalCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
        Source source = GraalJSSourceCreator.createSource(code, "Unknown");
        try (CodeRunner codeRunner = createJSCodeRunner(Path.of(REPOSITORY.getRepositoryPath()), executionContext)) {
            return codeRunner.run(source).as(Object.class);
        }
    }

    private static CodeRunner createJSCodeRunner(Path workingDirectoryPath, Map<Object, Object> executionContext) {
        return GraalJSCodeRunner.newBuilder(workingDirectoryPath, getOrCreateInternalFolder("dependencies"), getOrCreateInternalFolder("core-modules"))
                .addJSPolyfill(new RequirePolyfill())
                .addGlobalObject(new DirigibleContextGlobalObject(executionContext))
                .addGlobalObject(new DirigibleEngineTypeGlobalObject())
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

    @Override
    public String getType() {
        return JAVASCRIPT_TYPE_GRAALIUM;
    }

    @Override
    public String getName() {
        return "Graalium JavaScript Engine";
    }
}
