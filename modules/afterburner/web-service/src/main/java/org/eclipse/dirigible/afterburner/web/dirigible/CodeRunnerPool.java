/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.afterburner.web.dirigible;

import org.eclipse.dirigible.afterburner.core.CodeRunner;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import stormpot.*;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class CodeRunnerPool {
    private static final CodeRunnerAllocator allocator = new CodeRunnerAllocator();
    private static final Pool<CodeRunnerPoolable> pool = Pool.from(allocator).build();

    public static CodeRunnerPoolable borrow() throws InterruptedException {
        var timeout = new Timeout(10, TimeUnit.MINUTES);
        return pool.claim(timeout);
    }

    public static class CodeRunnerAllocator implements Allocator<CodeRunnerPoolable> {

        @Override
        public CodeRunnerPoolable allocate(Slot slot) {
            var codeRunner = createJavaScriptCodeRunner();
            return new CodeRunnerPoolable(slot, codeRunner);
        }

        private static CodeRunner createJavaScriptCodeRunner() {
            var repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
            var repositoryRootPath = Path.of(repository.getRepositoryPath());
            var registryPath = repositoryRootPath.resolve("registry/public");

            return CodeRunnerFactory.createDirigibleJSCodeRunner(registryPath);
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
