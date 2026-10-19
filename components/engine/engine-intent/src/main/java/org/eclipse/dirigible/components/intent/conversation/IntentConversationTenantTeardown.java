/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.intent.conversation;

import java.util.List;

import org.eclipse.dirigible.components.base.tenant.TenantTeardownParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Deprovisioning: deletes the tenant's intent assistant conversations and their messages.
 */
@Component
class IntentConversationTenantTeardown implements TenantTeardownParticipant {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(IntentConversationTenantTeardown.class);

    /** The order of this participant: the tenant's content, after what runs for it. */
    static final int ORDER = 600;

    /** The conversations. */
    private final IntentConversationRepository conversations;

    /** Their messages. */
    private final IntentConversationMessageRepository messages;

    /**
     * Instantiates a new intent conversation teardown.
     *
     * @param conversations the conversations
     * @param messages their messages
     */
    IntentConversationTenantTeardown(IntentConversationRepository conversations, IntentConversationMessageRepository messages) {
        this.conversations = conversations;
        this.messages = messages;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    @Transactional
    public void teardown(String tenantId) {
        List<IntentConversation> found = conversations.findByTenantId(tenantId);
        for (IntentConversation conversation : found) {
            messages.deleteByConversationId(conversation.getId());
            conversations.delete(conversation);
        }
        if (!found.isEmpty()) {
            LOGGER.info("Deleted [{}] intent conversation(s) of tenant [{}].", found.size(), tenantId);
        }
    }
}
