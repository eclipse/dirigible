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
package org.eclipse.dirigible.integration.tests.messaging;

/**
 * used by <b>background-handler.js as well</b>
 */
public class MessagesHolder {

    private static String latestReceivedMessage;
    private static String latestReceivedError;

    public static String getLatestReceivedMessage() {
        return latestReceivedMessage;
    }

    public static void setLatestReceivedMessage(String latestReceivedMessage) {
        MessagesHolder.latestReceivedMessage = latestReceivedMessage;
    }

    public static void clearLatestReceivedMessage() {
        latestReceivedMessage = null;
    }

    public static String getLatestReceivedError() {
        return latestReceivedError;
    }

    public static void setLatestReceivedError(String latestReceivedError) {
        MessagesHolder.latestReceivedError = latestReceivedError;
    }

    public static void clearLatestReceivedError() {
        latestReceivedError = null;
    }
}
