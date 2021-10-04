/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.processor;

import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.graalvm.polyglot.io.FileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RegistryTruffleFileSystem implements FileSystem {
    private final IScriptEngineExecutor executor;
    private final String project;
    private IRepository repository = (IRepository) StaticObjects.get(StaticObjects.REPOSITORY);

    public RegistryTruffleFileSystem(IScriptEngineExecutor executor, String project) {
        this.executor = executor;
        this.project = "/" + project;
    }

    @Override
    public Path parsePath(URI uri) {
        try {
            var packageName = "firebase.mjs";
            if (uri.toString().contains("firestore")) {
                packageName = "firestore.mjs";
            }

            if (repository.getResource(packageName).exists()) {
                return Paths.get(packageName);
            }

            CloseableHttpClient client = HttpClients.createDefault();
            try (CloseableHttpResponse response = client.execute(new HttpGet(uri))) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    var bytes = IOUtils.toByteArray(entity.getContent());
                    repository.createResource(IRepositoryStructure.PATH_REGISTRY_PUBLIC + "/" + packageName, bytes);
                }
            }


            return Paths.get(packageName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path parsePath(String path) {
        String dirigibleScope = "@dirigible";

        if(path.startsWith(dirigibleScope))
        {
            path = path.substring(dirigibleScope.length()).replace('-', '/');
            String api = StringUtils.substringBeforeLast(path, "/");
            api = StringUtils.substringAfterLast(api,"/") + ".mjs";
            path += "/" + api;
        }

        return handlePossibleDirigiblePath(Paths.get(path));
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {

    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

    }

    @Override
    public void delete(Path path) throws IOException {

    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        var source = "";
        var pathString = path.toString();
        var root = IRepositoryStructure.PATH_REGISTRY_PUBLIC;

        if (!pathString.endsWith(".js") && !pathString.endsWith(".mjs")) {
            var module = executor.retrieveModule(root, pathString, ".mjs");
            source = new String(module.getContent(), StandardCharsets.UTF_8);
        } else if (pathString.toLowerCase().endsWith(".js")) {
            var module = executor.retrieveModule(root, pathString.replace(".js", ""), ".mjs");
            source = new String(module.getContent(), StandardCharsets.UTF_8);
        } else if (pathString.toLowerCase().endsWith(".mjs")) {
            var module = executor.retrieveModule(root, pathString.replace(".mjs", ""), ".mjs");
            source = new String(module.getContent(), StandardCharsets.UTF_8);
        } else {
            source = "";
        }

        return new SeekableInMemoryByteChannel(source.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return null;
    }

    @Override
    public Path toAbsolutePath(Path path) {
        var maybeDirigiblePath = handlePossibleDirigiblePath(path);
        return maybeDirigiblePath.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        return handlePossibleDirigiblePath(path);
    }

    private Path handlePossibleDirigiblePath(String path) {
        return handlePossibleDirigiblePath(Paths.get(path));
    }

    private Path handlePossibleDirigiblePath(Path path) {
        var pathString = path.toString();

        if (pathString.startsWith("./")) {
            return Path.of(project, pathString);
        }

        if (pathString.startsWith("/")) {
            return path;
        }

        return Path.of("/" + pathString);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        var attr = new HashMap<String, Object>();
        attr.put("isRegularFile", true);
        return attr;
    }
}
