/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * @interface
 */
export class HistoryEntry {
  /**
   * @return {boolean}
   */
  valid() {
  }

  reveal() {}
}

/**
 * @unrestricted
 */
export class SimpleHistoryManager {
  /**
   * @param {number} historyDepth
   */
  constructor(historyDepth) {
    this._entries = [];
    this._activeEntryIndex = -1;
    this._coalescingReadonly = 0;
    this._historyDepth = historyDepth;
  }

  readOnlyLock() {
    ++this._coalescingReadonly;
  }

  releaseReadOnlyLock() {
    --this._coalescingReadonly;
  }

  /**
   * @return {boolean}
   */
  readOnly() {
    return !!this._coalescingReadonly;
  }

  /**
   * @param {function(!HistoryEntry):boolean} filterOutCallback
   */
  filterOut(filterOutCallback) {
    if (this.readOnly()) {
      return;
    }
    const filteredEntries = [];
    let removedBeforeActiveEntry = 0;
    for (let i = 0; i < this._entries.length; ++i) {
      if (!filterOutCallback(this._entries[i])) {
        filteredEntries.push(this._entries[i]);
      } else if (i <= this._activeEntryIndex) {
        ++removedBeforeActiveEntry;
      }
    }
    this._entries = filteredEntries;
    this._activeEntryIndex = Math.max(0, this._activeEntryIndex - removedBeforeActiveEntry);
  }

  /**
   * @return {boolean}
   */
  empty() {
    return !this._entries.length;
  }

  /**
   * @return {?HistoryEntry}
   */
  active() {
    return this.empty() ? null : this._entries[this._activeEntryIndex];
  }

  /**
   * @param {!HistoryEntry} entry
   */
  push(entry) {
    if (this.readOnly()) {
      return;
    }
    if (!this.empty()) {
      this._entries.splice(this._activeEntryIndex + 1);
    }
    this._entries.push(entry);
    if (this._entries.length > this._historyDepth) {
      this._entries.shift();
    }
    this._activeEntryIndex = this._entries.length - 1;
  }

  /**
   * @return {boolean}
   */
  rollback() {
    if (this.empty()) {
      return false;
    }

    let revealIndex = this._activeEntryIndex - 1;
    while (revealIndex >= 0 && !this._entries[revealIndex].valid()) {
      --revealIndex;
    }
    if (revealIndex < 0) {
      return false;
    }

    this.readOnlyLock();
    this._entries[revealIndex].reveal();
    this.releaseReadOnlyLock();

    this._activeEntryIndex = revealIndex;
    return true;
  }

  /**
   * @return {boolean}
   */
  rollover() {
    let revealIndex = this._activeEntryIndex + 1;

    while (revealIndex < this._entries.length && !this._entries[revealIndex].valid()) {
      ++revealIndex;
    }
    if (revealIndex >= this._entries.length) {
      return false;
    }

    this.readOnlyLock();
    this._entries[revealIndex].reveal();
    this.releaseReadOnlyLock();

    this._activeEntryIndex = revealIndex;
    return true;
  }
}
