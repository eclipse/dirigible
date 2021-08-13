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
// Copyright 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Common from '../common/common.js';
import * as Host from '../host/host.js';
import * as Workspace from '../workspace/workspace.js';  // eslint-disable-line no-unused-vars

import {FilteredUISourceCodeListProvider} from './FilteredUISourceCodeListProvider.js';
import {SourcesView} from './SourcesView.js';

export class OpenFileQuickOpen extends FilteredUISourceCodeListProvider {
  /**
   * @override
   */
  attach() {
    this.setDefaultScores(SourcesView.defaultUISourceCodeScores());
    super.attach();
  }

  /**
   * @override
   * @param {?Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @param {number=} lineNumber
   * @param {number=} columnNumber
   */
  uiSourceCodeSelected(uiSourceCode, lineNumber, columnNumber) {
    Host.userMetrics.actionTaken(Host.UserMetrics.Action.SelectFileFromFilePicker);

    if (!uiSourceCode) {
      return;
    }
    if (typeof lineNumber === 'number') {
      Common.Revealer.reveal(uiSourceCode.uiLocation(lineNumber, columnNumber));
    } else {
      Common.Revealer.reveal(uiSourceCode);
    }
  }

  /**
   * @override
   * @param {!Workspace.Workspace.Project} project
   * @return {boolean}
   */
  filterProject(project) {
    return !project.isServiceProject();
  }

  /**
   * @override
   * @return {boolean}
   */
  renderAsTwoRows() {
    return true;
  }
}
