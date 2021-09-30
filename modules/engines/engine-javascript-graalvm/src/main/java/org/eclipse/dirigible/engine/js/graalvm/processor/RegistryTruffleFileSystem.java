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
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.graalvm.polyglot.io.FileSystem;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.Set;

public class RegistryTruffleFileSystem implements FileSystem {
    private final IScriptEngineExecutor executor;

    public RegistryTruffleFileSystem(IScriptEngineExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Path parsePath(URI uri) {
        return null;
    }

    @Override
    public Path parsePath(String path) {
        return Paths.get(path);
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
        if(!path.toString().endsWith(".js") && !path.toString().endsWith(".mjs")) {
            source = new String(executor.retrieveModule(IRepositoryStructure.PATH_REGISTRY_PUBLIC,
                    path.toString(),
                    ".mjs").getContent(), StandardCharsets.UTF_8);
        }
        else  if(path.toString().toLowerCase().endsWith(".js")) {
            source = new String(executor.retrieveModule(IRepositoryStructure.PATH_REGISTRY_PUBLIC,
                    path.toString().replace(".js", ""),
                    ".mjs").getContent(), StandardCharsets.UTF_8);
        }
        else if(path.toString().toLowerCase().endsWith(".mjs")) {
            source = new String(executor.retrieveModule(IRepositoryStructure.PATH_REGISTRY_PUBLIC,
                    path.toString().replace(".mjs", ""),
                    ".mjs").getContent(), StandardCharsets.UTF_8);
        }
        else {
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
        return path.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        return path.normalize().;
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
//						Map<String, Object> attr = new HashMap<>();
//						// for testing purposes, we consider all files non-regular. In this way, we force the
//						// module loader to try all possible file names before throwing module not found
//						attr.put("isRegularFile", false);
//						return attr;
        return null;
    }
}
