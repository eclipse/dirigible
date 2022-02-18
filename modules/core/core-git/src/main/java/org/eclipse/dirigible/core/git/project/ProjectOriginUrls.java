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
package org.eclipse.dirigible.core.git.project;

public class ProjectOriginUrls {
    private String fetchUrl;
    private String pushUrl;

    public ProjectOriginUrls(String fetchUrl, String pushUrl) {
        this.fetchUrl = fetchUrl;
        this.pushUrl = pushUrl;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }
    public String getPushUrl() {
        return pushUrl;
    }

    public String setFetchUrl(String fetchURL) {
        this.fetchUrl = fetchURL;
        return this.fetchUrl;
    }
    public String setPushUrl(String pushURL) {
        this.pushUrl = pushURL;
        return this.pushUrl;
    }
}
