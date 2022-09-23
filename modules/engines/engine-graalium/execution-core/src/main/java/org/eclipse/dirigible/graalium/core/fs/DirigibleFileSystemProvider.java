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
package org.eclipse.dirigible.graalium.core.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

public class DirigibleFileSystemProvider extends FileSystemProvider {

    private final FileSystemProvider internalFileSystemProvider;

    private DirigibleFileSystemProvider(FileSystemProvider internalFileSystemProvider) {
        this.internalFileSystemProvider = internalFileSystemProvider;
    }

    public static DirigibleFileSystemProvider fromAnother(FileSystemProvider fileSystemProvider) {
        return new DirigibleFileSystemProvider(fileSystemProvider);
    }

    @Override
    public String getScheme() {
        return internalFileSystemProvider.getScheme();
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        return DirigibleFileSystem.fromDefault();
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        return DirigibleFileSystem.fromDefault();
    }

    @Override
    public Path getPath(URI uri) {
        return DirigiblePath.fromAnother(internalFileSystemProvider.getPath(uri));
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return internalFileSystemProvider.newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return internalFileSystemProvider.newDirectoryStream(dir, filter);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        internalFileSystemProvider.createDirectory(dir, attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        internalFileSystemProvider.delete(path);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        internalFileSystemProvider.copy(source, target, options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        internalFileSystemProvider.move(source, target, options);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return internalFileSystemProvider.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return internalFileSystemProvider.isHidden(path);
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return internalFileSystemProvider.getFileStore(path);
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
//        System.out.println(123);
        internalFileSystemProvider.checkAccess(path, modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return internalFileSystemProvider.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return internalFileSystemProvider.readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return internalFileSystemProvider.readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        internalFileSystemProvider.setAttribute(path, attribute, value, options);
    }


}
