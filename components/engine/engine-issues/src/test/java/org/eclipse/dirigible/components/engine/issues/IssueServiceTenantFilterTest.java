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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.eclipse.dirigible.components.engine.issues.IssueCaller.Caller;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * A reporter reaches an issue only through the tenant the request runs in and the caller's own
 * identity; anything else answers 404, exactly like a missing issue, and changes nothing.
 */
class IssueServiceTenantFilterTest {

    private static final Caller ALICE_IN_T1 = new Caller("t1", false, "alice");

    private final IssueRepository issues = mock(IssueRepository.class);

    private final IssueMessageRepository messages = mock(IssueMessageRepository.class);

    private final IssueAttachmentRepository attachments = mock(IssueAttachmentRepository.class);

    private final IssueLifecycle lifecycle = mock(IssueLifecycle.class);

    private final IssueService service = new IssueService(issues, messages, attachments, lifecycle, mock(IssueAttachmentStore.class));

    @Test
    void theListIsTheCallersOwnInTheCurrentTenant() {
        when(issues.findByTenantIdAndReporterOrderByUpdatedAtDescIdDesc("t1", "alice")).thenReturn(List.of(issue("t1", "alice")));

        assertEquals(1, service.mine(ALICE_IN_T1)
                               .size());
        verify(issues).findByTenantIdAndReporterOrderByUpdatedAtDescIdDesc("t1", "alice");
    }

    @Test
    void anotherReportersOrTenantsIssueIsNotFound() {
        when(issues.findByIdAndTenantIdAndReporter(7L, "t1", "alice")).thenReturn(Optional.empty());

        assertNotFound(() -> service.mine(ALICE_IN_T1, 7L));
        assertNotFound(() -> service.answer(ALICE_IN_T1, 7L, "hello?"));
        assertNotFound(() -> service.withdraw(ALICE_IN_T1, 7L));
        assertNotFound(() -> service.download(ALICE_IN_T1, 7L, 1L));
        assertNotFound(() -> service.attach(ALICE_IN_T1, 7L, null, "a.txt", "text/plain", new byte[] {1}));

        verify(messages, never()).save(any());
        verify(lifecycle, never()).cancel(any(), any());
    }

    @Test
    void theThreadAReporterReadsHasNoMemo() {
        Issue issue = issue("t1", "alice");
        when(issues.findByIdAndTenantIdAndReporter(7L, "t1", "alice")).thenReturn(Optional.of(issue));
        when(messages.findByIssueIdAndInternalFalseOrderBySequenceAsc(7L)).thenReturn(
                List.of(new IssueMessage(7L, 1, IssueAuthorKind.ENGINEER, "op", "We are on it", false, Instant.now())));
        when(attachments.findByIssueIdOrderByIdAsc(7L)).thenReturn(List.of());

        assertEquals(1, service.mine(ALICE_IN_T1, 7L)
                               .messages()
                               .size());
        verify(messages, never()).findByIssueIdOrderBySequenceAsc(any());
    }

    @Test
    void anAnswerToAnIssueWaitingForItsReporterReopensIt() {
        Issue issue = issue("t1", "alice");
        issue.moveTo(IssueStatus.WAITING_FOR_REPORTER, Instant.now());
        when(issues.findByIdAndTenantIdAndReporter(7L, "t1", "alice")).thenReturn(Optional.of(issue));
        when(messages.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.answer(ALICE_IN_T1, 7L, "Here is the file you asked for");

        verify(lifecycle).resume(issue);
        assertEquals(IssueStatus.OPEN, issue.getStatus());
    }

    @Test
    void aClosedIssueTakesNoMoreAnswers() {
        Issue issue = issue("t1", "alice");
        issue.moveTo(IssueStatus.RESOLVED, Instant.now());
        when(issues.findByIdAndTenantIdAndReporter(7L, "t1", "alice")).thenReturn(Optional.of(issue));

        ResponseStatusException refused = assertThrows(ResponseStatusException.class, () -> service.answer(ALICE_IN_T1, 7L, "again"));

        assertEquals(HttpStatus.CONFLICT, refused.getStatusCode());
    }

    @Test
    void aDecisionTheIssuesStatusDoesNotAllowIsRefused() {
        Issue issue = issue("t1", "alice");
        when(issues.findById(7L)).thenReturn(Optional.of(issue));

        ResponseStatusException refused =
                assertThrows(ResponseStatusException.class, () -> service.decide("op", 7L, IssueDecision.RESOLVE, null));

        assertEquals(HttpStatus.CONFLICT, refused.getStatusCode());
        verify(lifecycle, never()).complete(any(), any(), any());
    }

    @Test
    void askingTheReporterNeedsAMessage() {
        Issue issue = issue("t1", "alice");
        issue.moveTo(IssueStatus.OPEN, Instant.now());
        when(issues.findById(7L)).thenReturn(Optional.of(issue));

        ResponseStatusException refused =
                assertThrows(ResponseStatusException.class, () -> service.decide("op", 7L, IssueDecision.REQUEST, " "));

        assertEquals(HttpStatus.BAD_REQUEST, refused.getStatusCode());
    }

    @Test
    void aTriageDecisionCompletesTheTriageTaskAndMovesTheStatus() {
        Issue issue = issue("t1", "alice");
        when(issues.findById(7L)).thenReturn(Optional.of(issue));

        service.decide("op", 7L, IssueDecision.CONFIRM, null);

        verify(lifecycle).complete(issue, IssueLifecycle.TRIAGE_TASK, IssueDecision.CONFIRM);
        assertEquals(IssueStatus.OPEN, issue.getStatus());
    }

    private static void assertNotFound(org.junit.jupiter.api.function.Executable call) {
        ResponseStatusException refused = assertThrows(ResponseStatusException.class, call);
        assertEquals(HttpStatus.NOT_FOUND, refused.getStatusCode());
    }

    private static Issue issue(String tenantId, String reporter) {
        return new Issue(tenantId, reporter, reporter, "It broke", null, IssueSeverity.MEDIUM, null, "{}", Instant.now());
    }
}
