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
// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Common from '../common/common.js';
import * as SDK from '../sdk/sdk.js';
import * as UI from '../ui/ui.js';

import {NetworkGroupNode} from './NetworkDataGridNode.js';
import {GroupLookupInterface, NetworkLogView} from './NetworkLogView.js';  // eslint-disable-line no-unused-vars

/**
 * @implements {GroupLookupInterface}
 */
export class NetworkFrameGrouper {
  /**
   * @param {!NetworkLogView} parentView
   */
  constructor(parentView) {
    this._parentView = parentView;
    /** @type {!Map<!SDK.ResourceTreeModel.ResourceTreeFrame, !FrameGroupNode>} */
    this._activeGroups = new Map();
  }

  /**
   * @override
   * @param {!SDK.NetworkRequest.NetworkRequest} request
   * @return {?NetworkGroupNode}
   */
  groupNodeForRequest(request) {
    const frame = SDK.ResourceTreeModel.ResourceTreeModel.frameForRequest(request);
    if (!frame || frame.isTopFrame()) {
      return null;
    }
    let groupNode = this._activeGroups.get(frame);
    if (groupNode) {
      return groupNode;
    }
    groupNode = new FrameGroupNode(this._parentView, frame);
    this._activeGroups.set(frame, groupNode);
    return groupNode;
  }

  /**
   * @override
   */
  reset() {
    this._activeGroups.clear();
  }
}

export class FrameGroupNode extends NetworkGroupNode {
  /**
   * @param {!NetworkLogView} parentView
   * @param {!SDK.ResourceTreeModel.ResourceTreeFrame} frame
   */
  constructor(parentView, frame) {
    super(parentView);
    this._frame = frame;
  }

  /**
   * @override
   */
  displayName() {
    return new Common.ParsedURL.ParsedURL(this._frame.url).domain() || this._frame.name || '<iframe>';
  }

  /**
   * @override
   * @param {!Element} cell
   * @param {string} columnId
   */
  renderCell(cell, columnId) {
    super.renderCell(cell, columnId);
    const columnIndex = this.dataGrid.indexOfVisibleColumn(columnId);
    if (columnIndex === 0) {
      const name = this.displayName();
      cell.appendChild(UI.Icon.Icon.create('largeicon-navigator-frame', 'network-frame-group-icon'));
      cell.createTextChild(name);
      cell.title = name;
      this.setCellAccessibleName(cell.textContent, cell, columnId);
    }
  }
}
