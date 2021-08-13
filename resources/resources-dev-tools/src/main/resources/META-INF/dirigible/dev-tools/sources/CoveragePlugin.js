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
// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Coverage from '../coverage/coverage.js';
import * as Formatter from '../formatter/formatter.js';
import * as SDK from '../sdk/sdk.js';
import * as SourceFrame from '../source_frame/source_frame.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';
import * as Workspace from '../workspace/workspace.js';  // eslint-disable-line no-unused-vars

import {Plugin} from './Plugin.js';

export class CoveragePlugin extends Plugin {
  /**
   * @param {!SourceFrame.SourcesTextEditor.SourcesTextEditor} textEditor
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   */
  constructor(textEditor, uiSourceCode) {
    super();

    this._textEditor = textEditor;
    this._uiSourceCode = uiSourceCode;

    /** @type {!Workspace.UISourceCode.UISourceCode} */
    this._originalSourceCode = Formatter.sourceFormatter.getOriginalUISourceCode(this._uiSourceCode);

    this._text = new UI.Toolbar.ToolbarButton(ls`Click to show Coverage Panel`);
    this._text.setSecondary();
    this._text.addEventListener(UI.Toolbar.ToolbarButton.Events.Click, () => {
      UI.ViewManager.ViewManager.instance().showView('coverage');
    });

    const mainTarget = SDK.SDKModel.TargetManager.instance().mainTarget();
    if (mainTarget) {
      this._model = mainTarget.model(Coverage.CoverageModel.CoverageModel);
      this._model.addEventListener(Coverage.CoverageModel.Events.CoverageReset, this._handleReset, this);

      this._coverage = this._model.getCoverageForUrl(this._originalSourceCode.url());
      if (this._coverage) {
        this._coverage.addEventListener(
            Coverage.CoverageModel.URLCoverageInfo.Events.SizesChanged, this._handleCoverageSizesChanged, this);
      }
    }

    this._updateStats();
  }

  /**
   * @override
   */
  dispose() {
    if (this._coverage) {
      this._coverage.removeEventListener(
          Coverage.CoverageModel.URLCoverageInfo.Events.SizesChanged, this._handleCoverageSizesChanged, this);
    }
    if (this._model) {
      this._model.removeEventListener(Coverage.CoverageModel.Events.CoverageReset, this._handleReset, this);
    }
  }

  /**
   * @override
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {boolean}
   */
  static accepts(uiSourceCode) {
    return uiSourceCode.contentType().isDocumentOrScriptOrStyleSheet();
  }

  _handleReset() {
    this._coverage = null;
    this._updateStats();
  }

  _handleCoverageSizesChanged() {
    this._updateStats();
  }

  _updateStats() {
    if (this._coverage) {
      this._text.setTitle(ls`Show Details`);
      this._text.setText(ls`Coverage: ${this._coverage.usedPercentage().toFixed(1)} %`);
    } else {
      this._text.setTitle(ls`Click to show Coverage Panel`);
      this._text.setText(ls`Coverage: n/a`);
    }
  }

  /**
   * @override
   * @return {!Promise<!Array<!UI.Toolbar.ToolbarItem>>}
   */
  async rightToolbarItems() {
    return [this._text];
  }
}
