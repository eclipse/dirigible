/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible
 * contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.integration.tests.ui;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static List<Path> findFiles(File folder, String fileExtension) throws IOException {
        return findFiles(folder.toPath(), fileExtension);
    }

    public static List<Path> findFiles(Path path, String fileExtension) throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        try (Stream<Path> walk = Files.walk(path)) {
            return walk.filter(p -> !Files.isDirectory(p))
                       .filter(f -> f.toString()
                                     .toLowerCase()
                                     .endsWith(fileExtension))
                       .collect(Collectors.toList());
        }
    }

    public static List<Path> findFiles(String path, String fileExtension) throws IOException {
        return findFiles(Path.of(path), fileExtension);
    }

    public static void deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        deleteFolder(folder);
    }

    public static void deleteFolder(File folder) {
        if (folder.exists()) {
            LOGGER.info("Will delete folder [{}]", folder);
            Awaitility.await()
                      .atMost(15, TimeUnit.SECONDS)
                      .until(() -> FileSystemUtils.deleteRecursively(folder));
        }
    }

    public static void deleteFolder(Path path) {
        deleteFolder(path.toFile());
    }
}
