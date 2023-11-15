/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.javascript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSSourceCreator;
import org.eclipse.dirigible.graalium.core.javascript.modules.ModuleType;
import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DirigibleJavaScriptTestsFactory implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirigibleJavaScriptTestsFactory.class);

    private static final String TESTS_PROJECT_NAME = "modules-tests";

    private DirigibleJavascriptCodeRunner codeRunner;

    synchronized List<DynamicContainer> createTestContainers() {
        if (codeRunner == null) {
            codeRunner = new DirigibleJavascriptCodeRunner();
        }
        List<Path> testFilesInProject = findAllTestFiles(TESTS_PROJECT_NAME);
        return testFilesInProject.stream()
                                 .map(Object::toString)
                                 .map(this::registerTest)
                                 .toList();
    }

    private List<Path> findAllTestFiles(String projectName) {
        Path projectPath = codeRunner.getSourceProvider()
                                     .getAbsoluteProjectPath(projectName);
        try {
            try (Stream<Path> filesStream = Files.walk(projectPath)
                                                 .filter(path -> path.toString()
                                                                     .endsWith(".mjs")
                                                         || path.toString()
                                                                .endsWith(".js"))) {
                return filesStream.toList();
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not get test files for project: " + projectName, ex);
        }
    }

    private DynamicContainer registerTest(String testFilePath) {
        ModuleType moduleType = testFilePath.endsWith(".mjs") ? ModuleType.ESM : ModuleType.CJS;
        Source source = new GraalJSSourceCreator(moduleType).createSource(testFilePath);
        TestFunction testFunction = runSource(source);

        List<DynamicTest> dynamicTests = testFunction.getDynamicTests();
        String testDisplayName = createTestDisplayName(testFilePath);
        return DynamicContainer.dynamicContainer(testDisplayName, dynamicTests);
    }

    private TestFunction runSource(Source source) {
        try {
            TestFunction testFunction = new TestFunction();
            codeRunner.getCodeRunner()
                      .addGlobalFunctions(Collections.singletonList(testFunction));
            codeRunner.run(source);
            return testFunction;
        } catch (RuntimeException ex) {
            String errorMessage = String.format("Failed to run [%s], path [%s]", source.getName(), source.getPath());
            throw new IllegalStateException(errorMessage, ex);
        }
    }

    private String createTestDisplayName(String testFilePath) {
        System.out.println("Creating ");
        LOGGER.info("Creating test display name for file [{}]", testFilePath);

        String rootRelativePath = String.format("%s%sdist%sesm%s", TESTS_PROJECT_NAME, File.separator, File.separator, File.separator);
        LOGGER.info("Relative path is [{}]", rootRelativePath);

        String testDisaplyName = StringUtils.substringAfterLast(testFilePath, rootRelativePath);
        LOGGER.info("Created test display name is [{}]", testDisaplyName);
        return testDisaplyName;
    }

    @Override
    public void close() {
        if (null != codeRunner) {
            codeRunner.close();
            codeRunner = null;
        }
    }

}
