/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.js.graalvm.processor;

public class ApiModule {
    private String name;
    private String api;
    private String[] versionedPaths;
    private String pathDefault;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String[] getVersionedPaths() {
        return versionedPaths;
    }

    public void setVersionedPaths(String[] versionedPaths) {
        this.versionedPaths = versionedPaths;
    }

    public String getPathDefault() {
        return pathDefault;
    }

    public void setPathDefault(String pathDefault) {
        this.pathDefault = pathDefault;
    }

}
