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
package org.eclipse.dirigible.afterburner.core.engine.modules.java;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.*;
import java.util.stream.Collectors;

public class JavaPackageProxyGenerator {
    public String generate(String javaPackageName) {
        List<ClassName> classesInPackage = getClassesInPackage(javaPackageName);
        return generateJavaExports(classesInPackage);
    }

    private List<ClassName> getClassesInPackage(String packageName) {
        try (ScanResult scanResult = new ClassGraph()
                .verbose()
                .enableClassInfo()
                .enableSystemJarsAndModules()
                .acceptPackages(packageName)
                .scan()) {

            return scanResult
                    .getAllClasses()
                    .stream()
                    .filter(c -> c.isPublic() && !c.isSynthetic() && !c.isAnonymousInnerClass() && !c.isAnnotation() && !c.isInnerClass())
                    .map(c -> new ClassName(c.getSimpleName(), c.getName()))
                    .distinct()
                    .collect(Collectors.toList());
        }
    }

    private String generateJavaExports(List<ClassName> classes) {
        StringBuilder exportsBuilder = new StringBuilder();
        List<String> exportedSymbolNames = new ArrayList<String>();

        for (ClassName klass : classes) {
            String className = klass.className;
            if (!isAlphanumeric(className)) {
                continue;
            }

            exportsBuilder
                    .append("export const ")
                    .append(className)
                    .append(" = Java.type('")
                    .append(klass.packageAndClassName)
                    .append("');")
                    .append(System.lineSeparator());

            exportedSymbolNames.add(className);
        }

        exportsBuilder
                .append(System.lineSeparator())
                .append("export default { ")
                .append(String.join(",", exportedSymbolNames))
                .append(" }");

        return exportsBuilder.toString();
    }

    private boolean isAlphanumeric(String str) {
        if (str == null || str.equals("")) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static class ClassName {
        private final String className;
        private final String packageAndClassName;

        public ClassName(String className, String packageAndClassName) {
            this.className = className;
            this.packageAndClassName = packageAndClassName;
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
