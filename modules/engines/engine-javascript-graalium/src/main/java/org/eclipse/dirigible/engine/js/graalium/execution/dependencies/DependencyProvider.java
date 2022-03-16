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
package org.eclipse.dirigible.engine.js.graalium.execution.dependencies;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;

public class DependencyProvider {

    private final Path dependenciesCachePath;

    public DependencyProvider(Path dependenciesCachePath) {
        dependenciesCachePath.toFile().mkdirs();
        this.dependenciesCachePath = dependenciesCachePath;
    }

    public Path provideDependency(URI uri) {
        Path maybeCachePath = tryGetCachePath(uri);
        return maybeCachePath != null ? maybeCachePath : downloadDependency(uri);
    }

    @Nullable
    private Path tryGetCachePath(URI uri) {
        String base64DependencyName = getBase64FromURI(uri);
        Path expectedDependencyPath = dependenciesCachePath.resolve(base64DependencyName);
        File expectedDependencyFile = expectedDependencyPath.toFile();

        if (expectedDependencyFile.exists()) {
            return expectedDependencyPath;
        }

        return null;
    }

    private Path downloadDependency(URI uri) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        try (Response response = client.newCall(request).execute()) {
            File dependencyFile = getDependencyFileForOutput(uri);
            InputStream downloadedStream = response.body().byteStream();
            FileUtils.copyInputStreamToFile(downloadedStream, dependencyFile);
            return dependencyFile.toPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getDependencyFileForOutput(URI dependencyUri) {
        String base64DependencyName = getBase64FromURI(dependencyUri);
        return dependenciesCachePath.resolve(base64DependencyName).toFile();
    }

    private static String getBase64FromURI(URI uri) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytes = uri.toString().getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(bytes);
    }
}
