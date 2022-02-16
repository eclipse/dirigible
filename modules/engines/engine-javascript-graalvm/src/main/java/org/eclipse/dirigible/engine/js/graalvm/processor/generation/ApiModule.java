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
package org.eclipse.dirigible.engine.js.graalvm.processor.generation;

public class ApiModule {
    private final String name;
    private final String api;
    private final String[] versionedPaths;
    private final String pathDefault;
    private final boolean isPackageDescription;
    private final Boolean shouldBeUnexposedToESM;

    ApiModule(String name, String api, String[] versionedPaths, String pathDefault, boolean isPackageDescription, Boolean shouldBeUnexposedToESM) {
        this.name = name;
        this.api = api;
        this.versionedPaths = versionedPaths;
        this.pathDefault = pathDefault;
        this.isPackageDescription = isPackageDescription;
        this.shouldBeUnexposedToESM = shouldBeUnexposedToESM;
    }

    String getName() {
        return name;
    }

    public String getApi() {
        return api;
    }

    public String[] getVersionedPaths() {
        return versionedPaths;
    }

    public String getPathDefault() {
        return pathDefault;
    }

    public boolean getShouldBeUnexposedToESM() {
        if(shouldBeUnexposedToESM==null){
            return false;
        }

        return shouldBeUnexposedToESM;
    }

    public boolean isPackageDescription() {
        return isPackageDescription;
    }
}
