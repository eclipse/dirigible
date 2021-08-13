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

/**
 * @fileoverview using private properties isn't a Closure violation in tests.
 * @suppress {accessControls}
 */

SecurityTestRunner.dumpSecurityPanelSidebarOrigins = function() {
  for (const key in Security.SecurityPanelSidebarTree.OriginGroup) {
    const originGroup = Security.SecurityPanelSidebarTree.OriginGroup[key];
    const element = Security.SecurityPanel._instance()._sidebarTree._originGroups.get(originGroup);

    if (element.hidden) {
      continue;
    }

    TestRunner.addResult('Group: ' + element.title);
    const originTitles = element.childrenListElement.getElementsByTagName('span');

    for (const originTitle of originTitles) {
      if (originTitle.className !== 'tree-element-title') {
        TestRunner.dumpDeepInnerHTML(originTitle);
      }
    }
  }
};

SecurityTestRunner.dispatchRequestFinished = function(request) {
  TestRunner.networkManager.dispatchEventToListeners(SDK.NetworkManager.Events.RequestFinished, request);
};
