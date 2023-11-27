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
package org.eclipse.dirigible.integration.tests.ui.framework;

public enum HtmlElementType {
    BUTTON("button"), //
    INPUT("input"), //
    HEADER5("h5"), //
    TITLE("title"), //
    LI("li"), //
    ANCHOR("a");

    private final String type;

    HtmlElementType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
