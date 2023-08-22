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
package org.eclipse.dirigible.components.engine.typescript;

import org.apache.commons.io.FileUtils;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.RepositoryPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class TypeScriptService {

    private static final String TS_EXT = ".ts";
    private static final String DTS_EXT = ".d.ts";

    private final IRepository repository;

    @Autowired
    public TypeScriptService(IRepository repository) {
        this.repository = repository;
    }

    public boolean isTypeScriptFile(String path) {
        return path.endsWith(TS_EXT);
    }

    public boolean shouldCompileTypeScript(String projectName, String entryPath) {
        if (entryPath != null && !entryPath.equals("")) {
            return isTSButNotDTS(entryPath);
        }

        var projectDir = getProjectDirFile(projectName);
        return shouldCompileTypeScript(projectDir);
    }

    public boolean shouldCompileTypeScript(File dir) {
        if (shouldIgnoreProject(dir.getName())) {
            return false;
        }

        return dir.exists() && !getTypeScriptFilesInDir(dir).isEmpty();
    }

    private static boolean shouldIgnoreProject(String projectName) {
        return "dev-tools".equals(projectName) || "modules".equals(projectName);
    }

    private static boolean isTSButNotDTS(String entryPath) {
        return entryPath.endsWith(TS_EXT) && !entryPath.endsWith(DTS_EXT);
    }

    public void compileTypeScript(File dir) {
        compileTypeScript(dir, dir, getTypeScriptFilesInDir(dir));
    }

    public void compileTypeScript(String projectName, String entryPath) {
        var projectDir = getProjectDirFile(projectName);
        File outDir;
        Collection<File> tsFiles;

        if (entryPath != null && !entryPath.equals("")) {
            var tsFilePathString = new RepositoryPath(IRepositoryStructure.PATH_REGISTRY_PUBLIC, projectName, entryPath).toString();
            var tsFilePath = new File(repository.getInternalResourcePath(tsFilePathString)).toPath();
            outDir = tsFilePath.getParent().toFile();
            tsFiles = Collections.singletonList(tsFilePath.toFile());
        } else {
            tsFiles = getTypeScriptFilesInDir(projectDir);
            outDir = projectDir;
        }

        compileTypeScript(projectDir, outDir, tsFiles);
    }

    private static void compileTypeScript(File projectDir, File outDir, Collection<File> filesToCompile) {
        var esbuildCommand = new ArrayList<String>();
        esbuildCommand.add("esbuild");
        esbuildCommand.addAll(filesToCompile.stream().map(Object::toString).toList());
        esbuildCommand.add("--outdir=" + outDir);
        esbuildCommand.add("--out-extension:.js=.mjs");

        var processBuilder = new ProcessBuilder(esbuildCommand)
                .directory(projectDir)
                .redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            int statusCode = process.waitFor();
            if (statusCode != 0) {
                throw new RuntimeException("esbuild error: finished with: " + statusCode);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Could not run esbuild", e);
        }
    }

    private File getProjectDirFile(String projectName) {
        var registryRelativeProjectPath = new RepositoryPath(IRepositoryStructure.PATH_REGISTRY_PUBLIC, projectName).toString();
        return new File(repository.getInternalResourcePath(registryRelativeProjectPath));
    }

    private Collection<File> getTypeScriptFilesInDir(File projectDir) {
        return FileUtils
                .listFiles(projectDir, new String[]{"ts"}, true)
                .stream()
                .filter(x -> isTSButNotDTS(x.toString()))
                .collect(Collectors.toList());
    }
}
