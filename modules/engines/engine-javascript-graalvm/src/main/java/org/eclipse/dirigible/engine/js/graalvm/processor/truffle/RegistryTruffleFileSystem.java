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

/**
 * The Class RegistryTruffleFileSystem.
 */
public class RegistryTruffleFileSystem implements FileSystem {

    /** The scope handler. */
    private final DirigibleScopePathHandler scopeHandler;
    
    /** The standard handler. */
    private final StandardPathHandler standardHandler;

    /**
     * Instantiates a new registry truffle file system.
     *
     * @param executor the executor
     * @param project the project
     */
    public RegistryTruffleFileSystem(IScriptEngineExecutor executor, String project) {
        this.scopeHandler = new DirigibleScopePathHandler(executor);
        this.standardHandler = new StandardPathHandler(project, IRepositoryStructure.PATH_REGISTRY_PUBLIC, executor);
    }

    /**
     * Parses the path.
     *
     * @param uri the uri
     * @return the path
     */
    @Override
    public Path parsePath(URI uri) {
        throw new ScriptingException("URIs are currently not supported!");
    }

    /**
     * Parses the path.
     *
     * @param path the path
     * @return the path
     */
    @Override
    public Path parsePath(String path) {
        if ("".equals(path)) {
            return Path.of("");
        }
        return standardHandler.handlePossibleRepositoryPath(path);
    }

    /**
     * Check access.
     *
     * @param path the path
     * @param modes the modes
     * @param linkOptions the link options
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void checkAccess(
            Path path,
            Set<? extends AccessMode> modes,
            LinkOption... linkOptions
    ) throws IOException {

    }

    /**
     * Creates the directory.
     *
     * @param dir the dir
     * @param attrs the attrs
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void createDirectory(
            Path dir,
            FileAttribute<?>... attrs
    ) throws IOException {

    }

    /**
     * Delete.
     *
     * @param path the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void delete(Path path) throws IOException {

    }

    /**
     * New byte channel.
     *
     * @param path the path
     * @param options the options
     * @param attrs the attrs
     * @return the seekable byte channel
     * @throws IOException Signals that an I/O exception has occurred.
     */
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

    /**
     * New directory stream.
     *
     * @param dir the dir
     * @param filter the filter
     * @return the directory stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public DirectoryStream<Path> newDirectoryStream(
            Path dir,
            DirectoryStream.Filter<? super Path> filter
    ) throws IOException {
        return null;
    }

    /**
     * To absolute path.
     *
     * @param path the path
     * @return the path
     */
    @Override
    public Path toAbsolutePath(Path path) {
        if ("".equals(path.toString())) {
            return Path.of("");
        }

    	Path maybeDirigiblePath = standardHandler.handlePossibleRepositoryPath(path);
        return maybeDirigiblePath.toAbsolutePath();
    }

    /**
     * To real path.
     *
     * @param path the path
     * @param linkOptions the link options
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public Path toRealPath(
            Path path,
            LinkOption... linkOptions
    ) throws IOException {
        if ("".equals(path.toString())) {
            return Path.of("");
        }

        return standardHandler.handlePossibleRepositoryPath(path);
    }

    /**
     * Read attributes.
     *
     * @param path the path
     * @param attributes the attributes
     * @param options the options
     * @return the map
     * @throws IOException Signals that an I/O exception has occurred.
     */
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
