/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.eclipse.dirigible.commons.config.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * The origin a generated notification composes its links from (#7843): the application base URL
 * when one is configured, globally or for the tenant, else the instance's {@code DIRIGIBLE_HOST},
 * which every OAuth deployment already sets - never a silently relative link while either is known.
 */
class ConfigurationsTest {

    private static final String APP_BASE_URL = "DIRIGIBLE_APP_BASE_URL";
    private static final String HOST = "DIRIGIBLE_HOST";

    @AfterEach
    void clearConfiguration() {
        Configuration.remove(APP_BASE_URL);
        Configuration.remove(HOST);
        Configuration.removeThreadConfiguration();
    }

    @Test
    void usesTheApplicationBaseUrlWhenSet() {
        Configuration.set(APP_BASE_URL, "https://app.example.com");
        Configuration.set(HOST, "https://host.example.com");

        assertEquals("https://app.example.com", Configurations.appBaseUrl());
    }

    @Test
    void fallsBackToTheHostWhenTheApplicationBaseUrlIsNotSet() {
        Configuration.set(HOST, "https://host.example.com");

        assertEquals("https://host.example.com", Configurations.appBaseUrl());
    }

    @Test
    void fallsBackToTheHostWhenTheApplicationBaseUrlIsBlank() {
        Configuration.set(APP_BASE_URL, " ");
        Configuration.set(HOST, "https://host.example.com");

        assertEquals("https://host.example.com", Configurations.appBaseUrl());
    }

    @Test
    void prefersTheTenantApplicationBaseUrlOverTheHost() {
        Configuration.set(HOST, "https://host.example.com");
        Configuration.setThreadConfiguration(Map.of(APP_BASE_URL, "https://acme.example.com"));

        assertEquals("https://acme.example.com", Configurations.appBaseUrl());
    }

    @Test
    void stripsTrailingSlashesSoAnAppendedPathNeverDoublesThem() {
        Configuration.set(HOST, "https://host.example.com//");

        assertEquals("https://host.example.com", Configurations.appBaseUrl());
    }

    @Test
    void isEmptyWhenNeitherIsSet() {
        assertEquals("", Configurations.appBaseUrl());
    }
}
