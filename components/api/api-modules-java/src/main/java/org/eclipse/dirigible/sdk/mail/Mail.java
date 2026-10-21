/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.sdk.mail;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.dirigible.commons.config.DirigibleConfig;
import org.eclipse.dirigible.components.api.mail.MailClient;
import org.eclipse.dirigible.components.api.mail.MailFacade;

/**
 * SMTP delivery entry point. {@link #getInstance()} returns the platform-configured
 * {@link MailClient} (host, port, credentials picked up from {@code DIRIGIBLE_MAIL_*} config); the
 * {@link Properties}-accepting overload lets you override settings per call for one-off messages or
 * multi-tenant fan-out.
 * <p>
 * Each {@code send} accepts a list of {@code Map} parts — one per MIME body — that the underlying
 * client packs into a {@code multipart/mixed} or {@code multipart/alternative} structure. Use a
 * {@code text/plain} and {@code text/html} pair for typical transactional mail.
 */
public final class Mail {

    private Mail() {}

    public static MailClient getInstance() {
        return MailFacade.getInstance();
    }

    public static MailClient getInstance(Properties properties) {
        return MailFacade.getInstance(properties);
    }

    public static Map send(String from, String[] to, String[] cc, String[] bcc, String subject, List<Map> parts) throws MessagingException {
        return getInstance().send(from, to, cc, bcc, subject, parts);
    }

    /**
     * Sends a message in the application's own name - what a generated {@code notify:} sends: From is
     * {@code DIRIGIBLE_MAIL_SENDER} and Reply-To is {@code DIRIGIBLE_MAIL_REPLY_TO} when set. Both are
     * tenant-overridable and read on every call, so the message goes out in the name of the tenant the
     * caller runs for.
     *
     * @param to the recipients
     * @param cc the copy recipients, {@code null} for none
     * @param bcc the blind-copy recipients, {@code null} for none
     * @param subject the subject
     * @param parts the MIME bodies, as for {@link #send}
     * @return the message id and the server's final reply
     * @throws MessagingException if the message cannot be sent
     */
    public static Map sendAsApplication(String[] to, String[] cc, String[] bcc, String subject, List<Map> parts) throws MessagingException {
        return getInstance().send(DirigibleConfig.MAIL_SENDER.getStringValue(), DirigibleConfig.MAIL_REPLY_TO.getStringValue(), to, cc, bcc,
                subject, parts);
    }
}
