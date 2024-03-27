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
package org.eclipse.dirigible.integration.tests.ui.tests.multitenancy;

import java.util.UUID;

class DirigibleTestTenant {
    private final String name;
    private final String id;
    private final String subdomain;
    private final String username;
    private final String password;

    DirigibleTestTenant(String name, String subdomain) {
        this.name = name;
        this.id = UUID.randomUUID()
                      .toString();
        this.subdomain = subdomain;
        this.username = UUID.randomUUID()
                            .toString();
        this.password = UUID.randomUUID()
                            .toString();
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    String getName() {
        return name;
    }

    String getId() {
        return id;
    }

    String getSubdomain() {
        return subdomain;
    }

    @Override
    public String toString() {
        return "DirigibleTestTenant{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", subdomain='" + subdomain + '\'' + ", username='"
                + username + '\'' + ", password='" + password + '\'' + '}';
    }
}
