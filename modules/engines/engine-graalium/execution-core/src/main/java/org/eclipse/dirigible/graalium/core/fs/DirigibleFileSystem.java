/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.graalium.core.fs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

public class DirigibleFileSystem extends FileSystem {

    private static final DirigibleFileSystem FROM_DEFAULT = new DirigibleFileSystem(FileSystems.getDefault());

    private final FileSystem internalFileSystem;

    private DirigibleFileSystem(FileSystem internalFileSystem) {
        this.internalFileSystem = internalFileSystem;
    }

    public static DirigibleFileSystem fromAnother(FileSystem fileSystem) {
        return new DirigibleFileSystem(fileSystem);
    }

    public static DirigibleFileSystem fromDefault() {
        return FROM_DEFAULT;
    }

    @Override
    public FileSystemProvider provider() {
        return internalFileSystem.provider();
    }

    @Override
    public void close() throws IOException {
        internalFileSystem.close();
    }

    @Override
    public boolean isOpen() {
        return internalFileSystem.isOpen();
    }

    @Override
    public boolean isReadOnly() {
        return internalFileSystem.isReadOnly();
    }

    @Override
    public String getSeparator() {
        return internalFileSystem.getSeparator();
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return internalFileSystem.getRootDirectories();
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return internalFileSystem.getFileStores();
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return internalFileSystem.supportedFileAttributeViews();
    }

    @Override
    public Path getPath(String first, String... more) {
        return DirigiblePath.fromAnother(internalFileSystem.getPath(first, more));
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return internalFileSystem.getPathMatcher(syntaxAndPattern);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return internalFileSystem.getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return internalFileSystem.newWatchService();
    }
}
