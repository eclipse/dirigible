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
// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as LayerViewer from '../layer_viewer/layer_viewer.js';
import * as SDK from '../sdk/sdk.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';

export class LayerPaintProfilerView extends UI.SplitWidget.SplitWidget {
  /**
   * @param {function(string=)} showImageCallback
   */
  constructor(showImageCallback) {
    super(true, false);

    this._logTreeView = new LayerViewer.PaintProfilerView.PaintProfilerCommandLogView();
    this.setSidebarWidget(this._logTreeView);
    this._paintProfilerView = new LayerViewer.PaintProfilerView.PaintProfilerView(showImageCallback);
    this.setMainWidget(this._paintProfilerView);

    this._paintProfilerView.addEventListener(
        LayerViewer.PaintProfilerView.Events.WindowChanged, this._onWindowChanged, this);

    this._logTreeView.focus();
  }

  reset() {
    this._paintProfilerView.setSnapshotAndLog(null, [], null);
  }

  /**
   * @param {!SDK.PaintProfiler.PaintProfilerSnapshot} snapshot
   */
  profile(snapshot) {
    snapshot.commandLog().then(log => setSnapshotAndLog.call(this, snapshot, log));

    /**
     * @param {?SDK.PaintProfiler.PaintProfilerSnapshot} snapshot
     * @param {?Array<!SDK.PaintProfiler.PaintProfilerLogItem>} log
     * @this {LayerPaintProfilerView}
     */
    function setSnapshotAndLog(snapshot, log) {
      this._logTreeView.setCommandLog(log || []);
      this._paintProfilerView.setSnapshotAndLog(snapshot, log || [], null);
      if (snapshot) {
        snapshot.release();
      }
    }
  }

  /**
   * @param {number} scale
   */
  setScale(scale) {
    this._paintProfilerView.setScale(scale);
  }

  _onWindowChanged() {
    this._logTreeView.updateWindow(this._paintProfilerView.selectionWindow());
  }
}
