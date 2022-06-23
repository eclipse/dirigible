package org.eclipse.dirigible.engine.js.service;

import org.eclipse.dirigible.afterburner.core.CodeRunner;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.js.graalium.platform.DirigibleCodeRunnerFactory;
import org.eclipse.dirigible.repository.api.IRepository;
import stormpot.*;

import java.util.concurrent.TimeUnit;

public class CodeRunnerPool {
    private static final CodeRunnerAllocator allocator = new CodeRunnerAllocator();
    private static final Pool<CodeRunnerPoolable> pool = Pool.from(allocator).build();

    public static CodeRunnerPoolable get() throws InterruptedException {
        Timeout timeout = new Timeout(10, TimeUnit.MINUTES);
        return pool.claim(timeout);
    }

    public static class CodeRunnerAllocator implements Allocator<CodeRunnerPoolable> {

        @Override
        public CodeRunnerPoolable allocate(Slot slot) throws Exception {
            CodeRunner codeRunner = createJavaScriptCodeRunner("test");
            return new CodeRunnerPoolable(slot, codeRunner);
        }

        public  static CodeRunner createJavaScriptCodeRunner(String projectName) {
            IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
            java.nio.file.Path projectPath = java.nio.file.Path.of(projectName);
            java.nio.file.Path repositoryRootPath = java.nio.file.Path.of(repository.getRepositoryPath());
            java.nio.file.Path projectDirectoryPath = repositoryRootPath.resolve("registry/public").resolve(projectPath);

            return DirigibleCodeRunnerFactory.createDirigibleJSCodeRunner(projectDirectoryPath);
        }

        @Override
        public void deallocate(CodeRunnerPoolable codeRunnerPoolable) {
            codeRunnerPoolable.getCodeRunner().close();
        }
    }

    public static class CodeRunnerPoolable implements Poolable {

        private final Slot slot;
        private final CodeRunner codeRunner;

        public CodeRunnerPoolable(Slot slot, CodeRunner codeRunner) {
            this.slot = slot;
            this.codeRunner = codeRunner;
        }

        public CodeRunner getCodeRunner() {
            return codeRunner;
        }

        @Override
        public void release() {
            slot.release(this);
        }
    }
}
