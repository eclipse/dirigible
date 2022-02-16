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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.dirigible.engine.api.script.IScriptEngineExecutor;
import org.eclipse.dirigible.engine.js.graalvm.processor.generation.ExportGenerator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class DirigibleScopePathHandler {

    private final ExportGenerator generator;

    DirigibleScopePathHandler(IScriptEngineExecutor executor) {
        this.generator = new ExportGenerator(executor);
    }

    String resolve(String pathString) {
        if (pathString.startsWith("/@dirigible-native/")) {
//            String packageName = parseImportedJavaPackage(pathString);
//            List<ClassName> classes = getClassesInPackage(packageName);
//            String generated = generateJavaExports(classes);
//            return generated;
        } else if (pathString.startsWith(Constants.DIRIGIBLE_SCOPE_VERSIONED)) {
            return resolveVersionedScopePath(pathString);
        } else if (pathString.startsWith(Constants.DIRIGIBLE_SCOPE_DEFAULT)) {
            return resolveDefaultScopePath(pathString);
        }

        return "";  // Not a dirigible scope path, generate nothing!
    }

    private String resolveVersionedScopePath(String pathString) {
        pathString = pathString.substring(Constants.DIRIGIBLE_SCOPE_VERSIONED.length() - 1);
        pathString = pathString.replace(Constants.SCOPED_PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        String apiVersion = pathString.split(Constants.PATH_SEPARATOR)[1];
        String apiVersionPath = Constants.PATH_SEPARATOR + apiVersion;
        pathString = pathString.replace(apiVersionPath, "");
        return generator.generate(pathString, apiVersion);
    }

    private String resolveDefaultScopePath(String pathString) {
        pathString = pathString.substring(Constants.DIRIGIBLE_SCOPE_DEFAULT.length());
        pathString = pathString.replace(Constants.SCOPED_PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        return generator.generate(pathString, "");
    }

    private String parseImportedJavaPackage(String importString) {
        String packageName = StringUtils.substringAfter(importString, "/@dirigible-native/");
        return packageName;
    }

    private List<ClassName> getClassesInPackage(String packageName) {
        Set<ClassName> classNames = new HashSet<ClassName>();

        try (ScanResult scanResult = new ClassGraph()
                .verbose()
                .enableClassInfo()
                .enableSystemJarsAndModules()
                .acceptPackages(packageName)
                .scan()) {
            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                ClassName className = new ClassName(classInfo.getSimpleName(), classInfo.getName());
                classNames.add(className);
            }
        }

        return new ArrayList<>(classNames);
    }

    private String generateJavaExports(List<ClassName> classes) {
    	StringBuilder exportsBuilder = new StringBuilder();
        List<String> exportedSymbolNames = new ArrayList<String>();

        for (ClassName klass : classes) {
            if (!StringUtils.isAlphanumeric(klass.getClassName())) {
                continue;
            }

            exportsBuilder
                    .append("export const ")
                    .append(klass.getClassName())
                    .append(" = Java.type('")
                    .append(klass.getPackageAndClassName())
                    .append("');")
                    .append(System.lineSeparator());

            exportedSymbolNames.add(klass.getClassName());
        }

        exportsBuilder
                .append(System.lineSeparator())
                .append("export default { ")
                .append(StringUtils.join(exportedSymbolNames, ","))
                .append(" }");

        return exportsBuilder.toString();
    }

    public static class ClassName {
        private final String className;
        private final String packageAndClassName;

        public ClassName(String className, String packageAndClassName) {
            this.className = className;
            this.packageAndClassName = packageAndClassName;
        }

        public String getClassName() {
            return className;
        }

        public String getPackageAndClassName() {
            return packageAndClassName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassName className1 = (ClassName) o;
            return Objects.equals(className, className1.className);
        }

        @Override
        public int hashCode() {
            return Objects.hash(className);
        }
    }
}
