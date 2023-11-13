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

public class MessagesHolder {

    private static String latestReceivedMessage;
    private static String latestReceivedError;

    public static String getLatestReceivedMessage() {
        return MessagesHolder.latestReceivedMessage;
    }

    public static void setLatestReceivedMessage(String latestReceivedMessage) {
        MessagesHolder.latestReceivedMessage = latestReceivedMessage;
    }

    public static void clearLatestReceivedMessage() {
        MessagesHolder.latestReceivedMessage = null;
    }

    public static String getLatestReceivedError() {
        return MessagesHolder.latestReceivedError;
    }

    public static void setLatestReceivedError(String latestReceivedMessage) {
        MessagesHolder.latestReceivedError = latestReceivedMessage;
    }

    public static void clearLatestReceivedError() {
        MessagesHolder.latestReceivedError = null;
    }
}
