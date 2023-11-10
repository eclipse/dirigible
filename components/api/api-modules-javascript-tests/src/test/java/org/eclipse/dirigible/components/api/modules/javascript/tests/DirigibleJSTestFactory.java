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
package org.eclipse.dirigible.components.api.modules.javascript.tests;

import org.eclipse.dirigible.graalium.core.DirigibleJavascriptCodeRunner;
import org.eclipse.dirigible.graalium.core.graal.globals.GlobalFunction;
import org.eclipse.dirigible.graalium.core.javascript.GraalJSSourceCreator;
import org.eclipse.dirigible.graalium.core.javascript.modules.ModuleType;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DirigibleJSTestFactory {

    private final DirigibleJavascriptCodeRunner codeRunner;
    private final String testProjectName;
    private final Predicate<Path> testProjectFileFilter;

    public DirigibleJSTestFactory(DirigibleJavascriptCodeRunner codeRunner, String testProjectName) {
        this(codeRunner, testProjectName, (p) -> true);
    }

    public DirigibleJSTestFactory(DirigibleJavascriptCodeRunner codeRunner, String testProjectName, Predicate<Path> testProjectFileFilter) {
        this.codeRunner = codeRunner;
        this.testProjectName = testProjectName;
        this.testProjectFileFilter = testProjectFileFilter;
    }

    List<DynamicContainer> createTestContainers() {
        return findAllTestFilesInProject(testProjectName).stream()
                                                         .map(testFilePath -> {
                                                             String testFilePathAsString = testFilePath.toString();
                                                             ModuleType moduleType = testFilePathAsString.endsWith(".mjs")
                                                                     ? ModuleType.ESM
                                                                     : ModuleType.CJS;
                                                             Source source =
                                                                     new GraalJSSourceCreator(moduleType).createSource(testFilePath);
                                                             TestFunction testFunction = new TestFunction();
                                                             codeRunner.getCodeRunner()
                                                                       .addGlobalFunctions(Collections.singletonList(testFunction));
                                                             codeRunner.run(source);

                                                             List<DynamicTest> dynamicTests = testFunction.getDynamicTests();
                                                             return DynamicContainer.dynamicContainer(testFilePath.getFileName()
                                                                                                                  .toString(),
                                                                     dynamicTests);
                                                         })
                                                         .toList();
    }

    private List<Path> findAllTestFilesInProject(String projectName) {
        Path projectPath = codeRunner.getSourceProvider()
                                     .getAbsoluteProjectPath(projectName);
        try {
            try (Stream<Path> filesStream = Files.walk(projectPath)
                                                 .filter(path -> {
                                                     String pathAsString = path.toString();
                                                     return (pathAsString.endsWith(".mjs") || pathAsString.endsWith(".js"))
                                                             && testProjectFileFilter.test(path);
                                                 })) {
                return filesStream.toList();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not get test files for project: " + projectName);
        }
    }

    static class TestFunction implements GlobalFunction {

        private final List<DynamicTest> dynamicTests = new ArrayList<>();

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Object execute(Value... arguments) {
            String testName = arguments[0].asString();
            Value testFunc = arguments[1];
            DynamicTest dynamicTest = DynamicTest.dynamicTest(testName, testFunc::execute);
            dynamicTests.add(dynamicTest);
            return null;
        }

        public List<DynamicTest> getDynamicTests() {
            return dynamicTests;
        }
    }
}
