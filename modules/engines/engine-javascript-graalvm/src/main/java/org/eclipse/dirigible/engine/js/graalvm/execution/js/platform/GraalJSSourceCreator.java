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
package org.eclipse.dirigible.engine.js.graalvm.execution.js.platform;

import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class GraalJSSourceCreator {

    public static Source createSource(String source, String fileName) {
        Source.Builder sourceBuilder = Source.newBuilder("js", source, fileName).internal(true);
        return createSource(sourceBuilder);
    }

    public static Source createSource(Path sourceFilePath) {
        File codeFile = sourceFilePath.toFile();
        Source.Builder sourceBuilder = Source.newBuilder("js", codeFile);
        return createSource(sourceBuilder);
    }

    private static Source createSource(Source.Builder sourceBuilder) {
        try {
            return sourceBuilder
                    .cached(true)
                    .encoding(StandardCharsets.UTF_8)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
