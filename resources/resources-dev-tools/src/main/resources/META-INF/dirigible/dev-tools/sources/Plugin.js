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
// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as UI from '../ui/ui.js';                       // eslint-disable-line no-unused-vars
import * as Workspace from '../workspace/workspace.js';  // eslint-disable-line no-unused-vars

export class Plugin {
  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {boolean}
   */
  static accepts(uiSourceCode) {
    return false;
  }

  wasShown() {
  }

  willHide() {
  }

  /**
   * @return {!Promise<!Array<!UI.Toolbar.ToolbarItem>>}
   */
  async rightToolbarItems() {
    return [];
  }

  /**
   * @return {!Array<!UI.Toolbar.ToolbarItem>}
   *
   * TODO(szuend): It is OK to asyncify this function (similar to {rightToolbarItems}),
   *               but it is currently not strictly necessary.
   */
  leftToolbarItems() {
    return [];
  }

  /**
   * @param {!UI.ContextMenu.ContextMenu} contextMenu
   * @param {number} lineNumber
   * @return {!Promise}
   */
  populateLineGutterContextMenu(contextMenu, lineNumber) {
    return Promise.resolve();
  }

  /**
   * @param {!UI.ContextMenu.ContextMenu} contextMenu
   * @param {number} lineNumber
   * @param {number} columnNumber
   * @return {!Promise}
   */
  populateTextAreaContextMenu(contextMenu, lineNumber, columnNumber) {
    return Promise.resolve();
  }

  dispose() {
  }
}
