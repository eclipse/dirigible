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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class DirigiblePath implements Path {

    private final Path internalPath;

    public DirigiblePath(Path internalPath) {
        this.internalPath = internalPath;
    }

    public static DirigiblePath fromAnother(Path path) {
        return new DirigiblePath(path);
    }
    
    @Override
    public FileSystem getFileSystem() {
        return DirigibleFileSystem.fromDefault();
    }

    @Override
    public boolean isAbsolute() {
        return internalPath.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return fromAnother(internalPath.getRoot());
    }

    @Override
    public Path getFileName() {
        return fromAnother(internalPath.getFileName());
    }

    @Override
    public Path getParent() {
        return fromAnother(internalPath.getParent());
    }

    @Override
    public int getNameCount() {
        return internalPath.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return fromAnother(internalPath.getName(index));
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return fromAnother(internalPath.subpath(beginIndex, endIndex));
    }

    @Override
    public boolean startsWith(Path other) {
        return internalPath.startsWith(other);
    }

    @Override
    public boolean startsWith(String other) {
        return internalPath.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return internalPath.endsWith(other);
    }

    @Override
    public boolean endsWith(String other) {
        return internalPath.endsWith(other);
    }

    @Override
    public Path normalize() {
        return fromAnother(internalPath.normalize());
    }

    @Override
    public Path resolve(Path other) {
        return fromAnother(internalPath.resolve(other));
    }

    @Override
    public Path resolve(String other) {
        return fromAnother(internalPath.resolve(other));
    }

    @Override
    public Path resolveSibling(Path other) {
        return fromAnother(internalPath.resolveSibling(other));
    }

    @Override
    public Path resolveSibling(String other) {
        return fromAnother(internalPath.resolveSibling(other));
    }

    @Override
    public Path relativize(Path other) {
        return fromAnother(internalPath.relativize(other));
    }

    @Override
    public URI toUri() {
        return internalPath.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return fromAnother(internalPath.toAbsolutePath());
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return fromAnother(internalPath.toRealPath(options));
    }

    @Override
    public File toFile() {
        return internalPath.toFile();
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return internalPath.register(watcher, events, modifiers);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return internalPath.register(watcher, events);
    }

    @Override
    public Iterator<Path> iterator() {
        return internalPath.iterator();
    }

    @Override
    public void forEach(Consumer<? super Path> action) {
        internalPath.forEach(action);
    }

    @Override
    public Spliterator<Path> spliterator() {
        return internalPath.spliterator();
    }

    @Override
    public int compareTo(Path other) {
        return internalPath.compareTo(other);
    }
}
