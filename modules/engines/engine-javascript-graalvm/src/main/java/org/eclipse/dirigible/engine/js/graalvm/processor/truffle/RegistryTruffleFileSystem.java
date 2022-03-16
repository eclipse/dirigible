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
package org.eclipse.dirigible.engine.js.graalvm.processor.truffle;

import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.graalvm.polyglot.io.FileSystem;

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

    private final DirigibleScopePathHandler scopeHandler;
    private final StandardPathHandler standardHandler;

    public RegistryTruffleFileSystem(IScriptEngineExecutor executor, String project) {
        this.scopeHandler = new DirigibleScopePathHandler(executor);
        this.standardHandler = new StandardPathHandler(project, IRepositoryStructure.PATH_REGISTRY_PUBLIC, executor);
    }

    @Override
    public Path parsePath(URI uri) {
        throw new ScriptingException("URIs are currently not supported!");
    }

    @Override
    public Path parsePath(String path) {
        if ("".equals(path)) {
            return Path.of("");
        }
        return standardHandler.handlePossibleRepositoryPath(path);
    }

    @Override
    public void checkAccess(
            Path path,
            Set<? extends AccessMode> modes,
            LinkOption... linkOptions
    ) throws IOException {

    }

    @Override
    public void createDirectory(
            Path dir,
            FileAttribute<?>... attrs
    ) throws IOException {

    }

    @Override
    public void delete(Path path) throws IOException {

    }

    @Override
    public SeekableByteChannel newByteChannel(
            Path path,
            Set<? extends OpenOption> options,
            FileAttribute<?>... attrs
    ) throws IOException {
        String pathString = path.toString().replace("\\", "/");
        String source = scopeHandler.resolve(pathString);
        if(!source.isEmpty()) {
            return new SeekableInMemoryByteChannel(source.getBytes(StandardCharsets.UTF_8));
        }

        source = standardHandler.resolve(pathString);
        return new SeekableInMemoryByteChannel(source.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(
            Path dir,
            DirectoryStream.Filter<? super Path> filter
    ) throws IOException {
        return null;
    }

    @Override
    public Path toAbsolutePath(Path path) {
    	Path maybeDirigiblePath = standardHandler.handlePossibleRepositoryPath(path);
        return maybeDirigiblePath.toAbsolutePath();
    }

    @Override
    public Path toRealPath(
            Path path,
            LinkOption... linkOptions
    ) throws IOException {
        return standardHandler.handlePossibleRepositoryPath(path);
    }

    @Override
    public Map<String, Object> readAttributes(
            Path path,
            String attributes,
            LinkOption... options
    ) throws IOException {
        Map<String, Object> attr = new HashMap<String, Object>();
        attr.put("isRegularFile", true);
        return attr;
    }
}
