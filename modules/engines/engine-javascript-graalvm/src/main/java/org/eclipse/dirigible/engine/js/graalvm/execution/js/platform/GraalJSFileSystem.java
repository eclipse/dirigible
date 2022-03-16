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

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.dependencies.DependencyProvider;
import org.eclipse.dirigible.engine.js.graalvm.execution.js.modules.DirigibleCoreModuleResolver;
import org.graalvm.polyglot.io.FileSystem;

import java.io.*;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

public class GraalJSFileSystem implements FileSystem {

    private static final FileSystemProvider DELEGATE = FileSystems.getDefault().provider();
    private Path currentWorkingDirectoryPath;
    private final DirigibleCoreModuleResolver dirigibleCoreModuleResolver;
    private DependencyProvider dependencyProvider;

    public GraalJSFileSystem(Path currentWorkingDirectoryPath, DirigibleCoreModuleResolver dirigibleCoreModuleResolver) {
        this.currentWorkingDirectoryPath = currentWorkingDirectoryPath;
        this.dirigibleCoreModuleResolver = dirigibleCoreModuleResolver;
        this.dependencyProvider = new DependencyProvider();
    }

    @Override
    public Path parsePath(URI uri) {
        Path dependencyPath = dependencyProvider.provideDependency(uri);
        return dependencyPath;
    }

    /**
     * Given a path string returns a absolute path relative to the CWD
     */
    @Override
    public Path parsePath(String path) {
        if ("".equals(path)) { // TODO: for exceptions stacktrace building maybe?
            return currentWorkingDirectoryPath;
        }

        if (dirigibleCoreModuleResolver.isCoreModule(path)) {
            return dirigibleCoreModuleResolver.resolveCoreModulePath(path);
        }

        Path requiredPath = Path.of(path);
        return requiredPath;
    }

    @Override
    public Path toAbsolutePath(Path path) {
        Path absolutePath = path.toAbsolutePath();
        return absolutePath;
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        Path realPath = path.toRealPath(linkOptions);
        return realPath;
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return DELEGATE.newByteChannel(path, options, attrs);
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
        if (isFollowLinks(linkOptions)) {
            DELEGATE.checkAccess(path, modes.toArray(new AccessMode[0]));
        } else if (modes.isEmpty()) {
            DELEGATE.readAttributes(path, "isRegularFile", LinkOption.NOFOLLOW_LINKS);
        } else {
            throw new UnsupportedOperationException("CheckAccess for NIO Provider is unsupported with non empty AccessMode and NOFOLLOW_LINKS.");
        }
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        DELEGATE.createDirectory(dir, attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        DELEGATE.delete(path);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        DELEGATE.copy(source, target, options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        DELEGATE.move(source, target, options);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return DELEGATE.newDirectoryStream(dir, filter);
    }

    @Override
    public void createLink(Path link, Path existing) throws IOException {
        DELEGATE.createLink(link, existing);
    }

    @Override
    public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
        DELEGATE.createSymbolicLink(link, target, attrs);
    }

    @Override
    public Path readSymbolicLink(Path link) throws IOException {
        return DELEGATE.readSymbolicLink(link);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return DELEGATE.readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        DELEGATE.setAttribute(path, attribute, value, options);
    }

    @Override
    public void setCurrentWorkingDirectory(Path currentWorkingDirectoryPath) {
        this.currentWorkingDirectoryPath = currentWorkingDirectoryPath;
    }

    private static boolean isFollowLinks(final LinkOption... linkOptions) {
        for (LinkOption lo : linkOptions) {
            if (lo == LinkOption.NOFOLLOW_LINKS) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSameFile(Path path1, Path path2, LinkOption... options) throws IOException {
        return DELEGATE.isSameFile(path1, path2);
    }

    @Override
    public Path getTempDirectory() {
        return new File(System.getProperty("java.io.tmpdir")).toPath();
    }
}
