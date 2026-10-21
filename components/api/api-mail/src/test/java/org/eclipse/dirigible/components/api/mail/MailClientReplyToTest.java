/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.api.mail;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.Address;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;

/**
 * A tenant mails from one shared SMTP account but asks for replies at its own address (dirigible
 * #7878): the Reply-To header carries that address, and is absent when none is configured, so a
 * reply goes to the sender as it always did.
 */
class MailClientReplyToTest {

    @Test
    void theReplyToHeaderNamesTheGivenAddresses() throws Exception {
        MimeMessage message = message("Acme Billing <billing@acme.com>", "accounts@acme.com, support@acme.com");

        assertEquals(new InternetAddress("Acme Billing <billing@acme.com>"), message.getFrom()[0]);
        assertArrayEquals(new Address[] {new InternetAddress("accounts@acme.com"), new InternetAddress("support@acme.com")},
                message.getReplyTo());
    }

    @Test
    void noReplyToLeavesTheHeaderOut() throws Exception {
        assertNull(message("billing@acme.com", null).getHeader("Reply-To"));
        assertNull(message("billing@acme.com", " ").getHeader("Reply-To"));
    }

    private static MimeMessage message(String from, String replyTo) throws Exception {
        List<Map> parts = new ArrayList<>();
        parts.add(Map.of("type", "text", "contentType", "text/plain", "text", "Invoice attached"));
        MimeMessage message = MailClient.createMimeMessage(Session.getInstance(new Properties()), from, replyTo,
                new String[] {"customer@example.com"}, new String[0], new String[0], "Invoice", parts);
        message.saveChanges();
        return message;
    }
}
