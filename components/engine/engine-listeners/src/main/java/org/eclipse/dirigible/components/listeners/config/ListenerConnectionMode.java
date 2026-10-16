/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.listeners.config;

import java.util.Arrays;
import java.util.Locale;

import org.eclipse.dirigible.commons.config.DirigibleConfig;

/**
 * How the message listeners attach to the broker -
 * {@link DirigibleConfig#MESSAGING_LISTENER_CONNECTIONS} (#7875).
 *
 * <p>
 * The connection is what a broker meters: Amazon MQ's smaller classes admit 300 wire-level
 * connections, and every one costs broker heap. A {@link #PER_LISTENER} deployment holds
 * {@code listeners x tenants} of them - a generated application suite with 130 handlers and one
 * tenant beside the default needs 260 - so {@link #SHARED} is the default: one connection per scope
 * (a tenant, the global destinations, the {@code .listener} artefacts), every subscription a
 * session on it, and the count no longer grows with the listeners.
 */
public enum ListenerConnectionMode {

    /**
     * One connection per tenant (plus one for the global destinations) for the client-Java listeners,
     * and one for all the {@code .listener} artefacts; each subscription is a session on it.
     */
    SHARED("shared"),

    /** One connection per subscription per tenant - the shape before #7875. */
    PER_LISTENER("per-listener");

    private final String key;

    ListenerConnectionMode(String key) {
        this.key = key;
    }

    /**
     * The value {@link DirigibleConfig#MESSAGING_LISTENER_CONNECTIONS} spells this mode as.
     *
     * @return the configuration value
     */
    public String key() {
        return key;
    }

    /**
     * The configured mode. A value that names neither mode is refused rather than defaulted: a
     * deployment that misspells its connection mode must not silently run the other one.
     *
     * @return the configured mode
     * @throws IllegalStateException if the configured value names no mode
     */
    public static ListenerConnectionMode configured() {
        String value = DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.getStringValue()
                                                                     .trim()
                                                                     .toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                     .filter(mode -> mode.key.equals(value))
                     .findFirst()
                     .orElseThrow(() -> new IllegalStateException(
                             "Unsupported value [" + value + "] for [" + DirigibleConfig.MESSAGING_LISTENER_CONNECTIONS.getKey()
                                     + "] - expected [" + SHARED.key + "] or [" + PER_LISTENER.key + "]"));
    }
}
