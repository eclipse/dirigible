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

/**
 * The Class DirigibleScopePathHandler.
 */
class DirigibleScopePathHandler {

    /** The generator. */
    private final ExportGenerator generator;

    /**
     * Instantiates a new dirigible scope path handler.
     *
     * @param executor the executor
     */
    DirigibleScopePathHandler(IScriptEngineExecutor executor) {
        this.generator = new ExportGenerator(executor);
    }

    /**
     * Resolve.
     *
     * @param pathString the path string
     * @return the string
     */
    String resolve(String pathString) {
        if (pathString.startsWith("/@dirigible-native/")) {
            String packageName = parseImportedJavaPackage(pathString);
            List<ClassName> classes = getClassesInPackage(packageName);
            return generateJavaExports(classes);
        } else if (pathString.startsWith(Constants.DIRIGIBLE_SCOPE_VERSIONED)) {
            return resolveVersionedScopePath(pathString);
        } else if (pathString.startsWith(Constants.DIRIGIBLE_SCOPE_DEFAULT)) {
            return resolveDefaultScopePath(pathString);
        }

        return "";  // Not a dirigible scope path, generate nothing!
    }

    /**
     * Resolve versioned scope path.
     *
     * @param pathString the path string
     * @return the string
     */
    private String resolveVersionedScopePath(String pathString) {
        pathString = pathString.substring(Constants.DIRIGIBLE_SCOPE_VERSIONED.length() - 1);
        pathString = pathString.replace(Constants.SCOPED_PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        String apiVersion = pathString.split(Constants.PATH_SEPARATOR)[1];
        String apiVersionPath = Constants.PATH_SEPARATOR + apiVersion;
        pathString = pathString.replace(apiVersionPath, "");
        return generator.generate(pathString, apiVersion);
    }

    /**
     * Resolve default scope path.
     *
     * @param pathString the path string
     * @return the string
     */
    private String resolveDefaultScopePath(String pathString) {
        pathString = pathString.substring(Constants.DIRIGIBLE_SCOPE_DEFAULT.length());
        pathString = pathString.replace(Constants.SCOPED_PATH_SEPARATOR, Constants.PATH_SEPARATOR);
        return generator.generate(pathString, "");
    }

    /**
     * Parses the imported java package.
     *
     * @param importString the import string
     * @return the string
     */
    private String parseImportedJavaPackage(String importString) {
        String packageName = StringUtils.substringAfter(importString, "/@dirigible-native/");
        return packageName;
    }

    /**
     * Gets the classes in package.
     *
     * @param packageName the package name
     * @return the classes in package
     */
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

    /**
     * Generate java exports.
     *
     * @param classes the classes
     * @return the string
     */
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

    /**
     * The Class ClassName.
     */
    public static class ClassName {
        
        /** The class name. */
        private final String className;
        
        /** The package and class name. */
        private final String packageAndClassName;

        /**
         * Instantiates a new class name.
         *
         * @param className the class name
         * @param packageAndClassName the package and class name
         */
        public ClassName(String className, String packageAndClassName) {
            this.className = className;
            this.packageAndClassName = packageAndClassName;
        }

        /**
         * Gets the class name.
         *
         * @return the class name
         */
        public String getClassName() {
            return className;
        }

        /**
         * Gets the package and class name.
         *
         * @return the package and class name
         */
        public String getPackageAndClassName() {
            return packageAndClassName;
        }

        /**
         * Equals.
         *
         * @param o the o
         * @return true, if successful
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClassName className1 = (ClassName) o;
            return Objects.equals(className, className1.className);
        }

        /**
         * Hash code.
         *
         * @return the int
         */
        @Override
        public int hashCode() {
            return Objects.hash(className);
        }
    }
}
