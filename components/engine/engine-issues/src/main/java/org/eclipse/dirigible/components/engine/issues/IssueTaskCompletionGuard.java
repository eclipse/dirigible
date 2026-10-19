/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.components.engine.issues;

import java.util.function.Supplier;

import org.eclipse.dirigible.sdk.db.ValidationException;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * Refuses the completion of an issue's task by anything but {@link IssueLifecycle} - the Inbox's
 * generic Complete in particular - so the issue's status, which {@link IssueService} writes in the
 * same transaction, always matches where its process is. The refusal is a client validation
 * failure, which the Inbox answers with its message as a 400.
 */
@Component("issueTaskCompletionGuard")
class IssueTaskCompletionGuard implements TaskListener {

    private static final ThreadLocal<Boolean> ALLOWED = new ThreadLocal<>();

    /**
     * Runs a completion the guard lets through. The completion listener runs synchronously, on the
     * completing thread.
     *
     * @param completion the completion
     * @param <T> its result
     * @return its result
     */
    static <T> T allow(Supplier<T> completion) {
        ALLOWED.set(Boolean.TRUE);
        try {
            return completion.get();
        } finally {
            ALLOWED.remove();
        }
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        if (!Boolean.TRUE.equals(ALLOWED.get())) {
            throw new ValidationException("The task [" + delegateTask.getName()
                    + "] of an issue is completed from the monitoring shell's Issues page, which records the outcome on the issue");
        }
    }
}
