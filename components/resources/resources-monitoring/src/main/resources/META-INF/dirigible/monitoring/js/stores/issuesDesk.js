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
 * issuesDesk store - the support desk of the instance (#7948): every tenant's issues, filtered by
 * tenant, status and assignee; the selected issue's thread with its internal memos; the operator's
 * decisions, replies, memos and assignment. The issue's lifecycle (triage, work) runs on the platform
 * BPM engine and its tasks also land in the operators' Inbox, whose form link opens this page with
 * the process instance id. Every read and write goes through MonitoringOps.
 */
document.addEventListener('alpine:init', () => {
  Alpine.store('issuesDesk', {
    items: [],
    loaded: false,
    /** Why the list could not be read: 'forbidden' (not an operator of the instance) or 'unavailable'. */
    loadError: null,
    filters: { tenant: '', status: '', assignee: '' },
    selected: null,
    reply: '',
    internal: false,
    decisionMessage: '',
    assignee: '',
    acting: false,
    /** The outcome of the last action: { variant, text }. */
    actionResult: null,

    async load() {
      try {
        this.items = await window.MonitoringOps.issues(this.filters);
        this.loadError = null;
      } catch (e) {
        console.error('monitoring: could not read the issues', e);
        this.items = [];
        this.loadError = e && e.httpStatus === 403 ? 'forbidden' : 'unavailable';
      } finally {
        this.loaded = true;
      }
    },

    async select(id) {
      try {
        this.selected = await window.MonitoringOps.issue(id);
        this.assignee = this.selected.issue.assignee || '';
        this.actionResult = null;
      } catch (e) {
        console.error('monitoring: could not read issue ' + id, e);
        this.actionResult = { variant: 'negative', text: T('monitoring:issues.readFailed', 'The issue could not be read.') };
      }
    },

    /** Opens the issue an Inbox task link names by its process instance. */
    async selectByProcess(processInstanceId) {
      try {
        const issue = await window.MonitoringOps.issueOfProcess(processInstanceId);
        await this.select(issue.id);
      } catch (e) {
        console.error('monitoring: no issue for process instance ' + processInstanceId, e);
      }
    },

    isSelected(issue) {
      return !!(this.selected && issue && this.selected.issue.id === issue.id);
    },

    /** The decisions the selected issue's status allows, in the order the page offers them. */
    get decisions() {
      const status = this.selected && this.selected.issue.status;
      if (status === 'NEW') return ['CONFIRM', 'REJECT'];
      if (status === 'OPEN') return ['RESOLVE', 'REQUEST', 'REJECT'];
      return [];
    },

    get selectedOpen() {
      return this.decisions.length > 0 || (this.selected && this.selected.issue.status === 'WAITING_FOR_REPORTER');
    },

    decisionLabel(decision) {
      return {
        CONFIRM: T('monitoring:issues.confirm', 'Confirm'),
        REJECT: T('monitoring:issues.reject', 'Reject'),
        RESOLVE: T('monitoring:issues.resolve', 'Resolve'),
        REQUEST: T('monitoring:issues.request', 'Ask the reporter'),
      }[decision];
    },

    decisionVariant(decision) {
      return { CONFIRM: 'primary', RESOLVE: 'primary', REQUEST: 'outline', REJECT: 'negative' }[decision] || 'outline';
    },

    async decide(decision) {
      await this.act(() => window.MonitoringOps.decideIssue(this.selected.issue.id, decision, this.decisionMessage),
        T('monitoring:issues.decided', 'The decision was recorded.'));
      if (this.actionResult && this.actionResult.variant === 'positive') this.decisionMessage = '';
    },

    async send() {
      const text = this.reply.trim();
      if (!text) return;
      await this.act(() => window.MonitoringOps.replyToIssue(this.selected.issue.id, text, this.internal),
        this.internal ? T('monitoring:issues.memoSaved', 'The memo was saved.') : T('monitoring:issues.replied', 'The reply was sent.'));
      if (this.actionResult && this.actionResult.variant === 'positive') this.reply = '';
    },

    async assign() {
      await this.act(() => window.MonitoringOps.assignIssue(this.selected.issue.id, this.assignee),
        T('monitoring:issues.assigned', 'The assignment was saved.'));
    },

    async act(call, done) {
      if (!this.selected || this.acting) return;
      this.acting = true;
      this.actionResult = null;
      const id = this.selected.issue.id;
      try {
        await call();
        await Promise.all([this.select(id), this.load()]);
        this.actionResult = { variant: 'positive', text: done };
      } catch (e) {
        console.error('monitoring: an action on issue ' + id + ' failed', e);
        this.actionResult = { variant: 'negative',
          text: App.services.apiErrors.refusalMessageFor(e, T('monitoring:issues.actionFailed', 'The action failed.')) };
      } finally {
        this.acting = false;
      }
    },

    attachmentUrl(attachment) {
      return window.MonitoringOps.issueAttachmentUrl(this.selected.issue.id, attachment.id);
    },

    /** The request ids the report carries, for the link into Logs. */
    get requestIds() {
      return (this.selected && this.selected.context && this.selected.context.requestIds) || [];
    },

    get logExcerpt() {
      return (this.selected && this.selected.context && this.selected.context.logExcerpt) || [];
    },

    get contextText() {
      return this.selected ? JSON.stringify(this.selected.context, null, 2) : '';
    },

    statusVariant(status) {
      return { NEW: 'information', OPEN: 'information', WAITING_FOR_REPORTER: 'warning', RESOLVED: 'positive', REJECTED: 'negative', CANCELLED: 'outline' }[status] || 'outline';
    },

    statusLabel(status) {
      return T('monitoring:issues.status.' + status, String(status || '').replace(/_/g, ' ').toLowerCase());
    },
  });
});
