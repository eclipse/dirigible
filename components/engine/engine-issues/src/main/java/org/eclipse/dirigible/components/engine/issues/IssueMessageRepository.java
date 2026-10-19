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

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** The issue threads. */
@Repository("issueMessageRepository")
public interface IssueMessageRepository extends JpaRepository<IssueMessage, Long> {

    /**
     * An issue's whole thread, memos included.
     *
     * @param issueId the issue
     * @return the messages, in order
     */
    List<IssueMessage> findByIssueIdOrderBySequenceAsc(Long issueId);

    /**
     * An issue's thread as its reporter sees it.
     *
     * @param issueId the issue
     * @return the messages that are not memos, in order
     */
    List<IssueMessage> findByIssueIdAndInternalFalseOrderBySequenceAsc(Long issueId);

    /**
     * One message of an issue.
     *
     * @param id the message
     * @param issueId the issue
     * @return the message
     */
    Optional<IssueMessage> findByIdAndIssueId(Long id, Long issueId);

    /**
     * The last position taken in an issue's thread.
     *
     * @param issueId the issue
     * @return the highest sequence, 0 for an empty thread
     */
    @Query("SELECT COALESCE(MAX(m.sequence), 0) FROM IssueMessage m WHERE m.issueId = :issueId")
    int lastSequence(@Param("issueId") Long issueId);
}
