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
import org.eclipse.dirigible.afterburner.core.dirigible.modules.DirigibleSourceProvider;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.repository.api.IRepository;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.nio.file.Path;

public class JavaScriptWebHandler {

    private final DirigibleSourceProvider dirigibleSourceProvider = new DirigibleSourceProvider();

    public void handleJavaScriptRequest(String projectName, String projectFilePath, String projectFilePathParam) throws IOException, InterruptedException {
        if (HttpRequestFacade.isValid()) {
            HttpRequestFacade.setAttribute(HttpRequestFacade.ATTRIBUTE_REST_RESOURCE_PATH, projectFilePathParam);
        }

        String maybeJSCode = dirigibleSourceProvider.getSource(projectName, projectFilePath);
        if (maybeJSCode == null) {
            throw new IOException("JS source for project name '" + projectName + "' and file name '" + projectFilePath + " could not be found");
        }

        Source jsSource = Source.newBuilder("js", maybeJSCode, projectFilePath).build();

        if ("test".equals(projectName)) {
            CodeRunnerPool.CodeRunnerPoolable codeRunnerPoolable = CodeRunnerPool.get();
            try {
                CodeRunner codeRunner = codeRunnerPoolable.getCodeRunner();
                codeRunner.run(jsSource);
            } finally {
                if (codeRunnerPoolable != null) {
                    codeRunnerPoolable.release();
                }
            }
        } else {
            CodeRunner codeRunner = createJavaScriptCodeRunner(projectName);
            codeRunner.run(jsSource);
        }
    }

    private static CodeRunner createJavaScriptCodeRunner(String projectName) {
        IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);
        Path projectPath = Path.of(projectName);
        Path repositoryRootPath = Path.of(repository.getRepositoryPath());
        Path projectDirectoryPath = repositoryRootPath.resolve("registry/public").resolve(projectPath);

        return CodeRunnerFactory.createDirigibleJSCodeRunner(projectDirectoryPath);
    }
}
