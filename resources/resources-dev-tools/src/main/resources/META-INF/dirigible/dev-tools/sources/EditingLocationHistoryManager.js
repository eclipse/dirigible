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
import * as Common from '../common/common.js';  // eslint-disable-line no-unused-vars
import * as SourceFrame from '../source_frame/source_frame.js';
import * as TextUtils from '../text_utils/text_utils.js';  // eslint-disable-line no-unused-vars
import * as Workspace from '../workspace/workspace.js';    // eslint-disable-line no-unused-vars

import {HistoryEntry, SimpleHistoryManager} from './SimpleHistoryManager.js';  // eslint-disable-line no-unused-vars
import {SourcesView} from './SourcesView.js';                                  // eslint-disable-line no-unused-vars
import {UISourceCodeFrame} from './UISourceCodeFrame.js';                      // eslint-disable-line no-unused-vars

/**
 * @unrestricted
 */
export class EditingLocationHistoryManager {
  /**
   * @param {!SourcesView} sourcesView
   * @param {function():?UISourceCodeFrame} currentSourceFrameCallback
   */
  constructor(sourcesView, currentSourceFrameCallback) {
    this._sourcesView = sourcesView;
    this._historyManager = new SimpleHistoryManager(HistoryDepth);
    this._currentSourceFrameCallback = currentSourceFrameCallback;
  }

  /**
   * @param {!UISourceCodeFrame} sourceFrame
   */
  trackSourceFrameCursorJumps(sourceFrame) {
    sourceFrame.textEditor.addEventListener(
        SourceFrame.SourcesTextEditor.Events.JumpHappened, this._onJumpHappened.bind(this));
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _onJumpHappened(event) {
    if (event.data.from) {
      this._updateActiveState(event.data.from);
    }
    if (event.data.to) {
      this._pushActiveState(event.data.to);
    }
  }

  rollback() {
    this._historyManager.rollback();
  }

  rollover() {
    this._historyManager.rollover();
  }

  updateCurrentState() {
    const sourceFrame = this._currentSourceFrameCallback();
    if (!sourceFrame) {
      return;
    }
    this._updateActiveState(sourceFrame.textEditor.selection());
  }

  pushNewState() {
    const sourceFrame = this._currentSourceFrameCallback();
    if (!sourceFrame) {
      return;
    }
    this._pushActiveState(sourceFrame.textEditor.selection());
  }

  /**
   * @param {!TextUtils.TextRange.TextRange} selection
   */
  _updateActiveState(selection) {
    const active = /** @type {?EditingLocationHistoryEntry} */ (this._historyManager.active());
    if (!active) {
      return;
    }
    const sourceFrame = this._currentSourceFrameCallback();
    if (!sourceFrame) {
      return;
    }
    const entry = new EditingLocationHistoryEntry(this._sourcesView, this, sourceFrame, selection);
    active.merge(entry);
  }

  /**
   * @param {!TextUtils.TextRange.TextRange} selection
   */
  _pushActiveState(selection) {
    const sourceFrame = this._currentSourceFrameCallback();
    if (!sourceFrame) {
      return;
    }
    const entry = new EditingLocationHistoryEntry(this._sourcesView, this, sourceFrame, selection);
    this._historyManager.push(entry);
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   */
  removeHistoryForSourceCode(uiSourceCode) {
    function filterOut(entry) {
      return entry._projectId === uiSourceCode.project().id() && entry._url === uiSourceCode.url();
    }

    this._historyManager.filterOut(filterOut);
  }
}

export const HistoryDepth = 20;

/**
 * @implements {HistoryEntry}
 * @unrestricted
 */
export class EditingLocationHistoryEntry {
  /**
   * @param {!SourcesView} sourcesView
   * @param {!EditingLocationHistoryManager} editingLocationManager
   * @param {!UISourceCodeFrame} sourceFrame
   * @param {!TextUtils.TextRange.TextRange} selection
   */
  constructor(sourcesView, editingLocationManager, sourceFrame, selection) {
    this._sourcesView = sourcesView;
    this._editingLocationManager = editingLocationManager;
    const uiSourceCode = sourceFrame.uiSourceCode();
    this._projectId = uiSourceCode.project().id();
    this._url = uiSourceCode.url();

    const position = this._positionFromSelection(selection);
    this._positionHandle = sourceFrame.textEditor.textEditorPositionHandle(position.lineNumber, position.columnNumber);
  }

  /**
   * @param {!EditingLocationHistoryEntry} entry
   */
  merge(entry) {
    if (this._projectId !== entry._projectId || this._url !== entry._url) {
      return;
    }
    this._positionHandle = entry._positionHandle;
  }

  /**
   * @param {!TextUtils.TextRange.TextRange} selection
   * @return {!{lineNumber: number, columnNumber: number}}
   */
  _positionFromSelection(selection) {
    return {lineNumber: selection.endLine, columnNumber: selection.endColumn};
  }

  /**
   * @override
   * @return {boolean}
   */
  valid() {
    const position = this._positionHandle.resolve();
    const uiSourceCode = Workspace.Workspace.WorkspaceImpl.instance().uiSourceCode(this._projectId, this._url);
    return !!(position && uiSourceCode);
  }

  /**
   * @override
   */
  reveal() {
    const position = this._positionHandle.resolve();
    const uiSourceCode = Workspace.Workspace.WorkspaceImpl.instance().uiSourceCode(this._projectId, this._url);
    if (!position || !uiSourceCode) {
      return;
    }

    this._editingLocationManager.updateCurrentState();
    this._sourcesView.showSourceLocation(uiSourceCode, position.lineNumber, position.columnNumber);
  }
}
