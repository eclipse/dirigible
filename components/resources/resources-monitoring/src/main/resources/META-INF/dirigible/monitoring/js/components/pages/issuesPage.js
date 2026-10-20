/*
 * Copyright (c) 2010-2026 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/*
 * Issues page (#7948) - the support desk over the issuesDesk store. Opened from an Inbox task, the
 * route carries the task's processInstanceId (the Inbox appends it to the form link) and the page
 * selects that issue.
 */
document.addEventListener('alpine:init', () => {
  Alpine.data('issuesPage', () => ({
    init() {
      const query = new URLSearchParams((window.location.hash.split('?')[1]) || '');
      const processInstanceId = query.get('processInstanceId');
      if (processInstanceId) this.$store.issuesDesk.selectByProcess(processInstanceId);
      this.$store.issuesDesk.load();
    },

    get state() {
      return this.$store.issuesDesk;
    },

    /** The Logs page's live stream, filtered to one request. */
    openLogs(requestId) {
      this.$store.logs.search = requestId;
      window.location.hash = '#/logs';
    },
  }));
});
