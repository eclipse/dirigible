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

export class NetworkPanelIndicator {
  constructor() {
    // TODO: we should not access network from other modules.
    if (!self.UI.inspectorView.hasPanel('network')) {
      return;
    }
    const manager = self.SDK.multitargetNetworkManager;
    manager.addEventListener(SDK.NetworkManager.MultitargetNetworkManager.Events.ConditionsChanged, updateVisibility);
    manager.addEventListener(
        SDK.NetworkManager.MultitargetNetworkManager.Events.BlockedPatternsChanged, updateVisibility);
    manager.addEventListener(SDK.NetworkManager.MultitargetNetworkManager.Events.InterceptorsChanged, updateVisibility);
    updateVisibility();

    function updateVisibility() {
      let icon = null;
      if (manager.isThrottling()) {
        icon = UI.Icon.Icon.create('smallicon-warning');
        icon.title = Common.UIString.UIString('Network throttling is enabled');
      } else if (self.SDK.multitargetNetworkManager.isIntercepting()) {
        icon = UI.Icon.Icon.create('smallicon-warning');
        icon.title = Common.UIString.UIString('Requests may be rewritten by local overrides');
      } else if (manager.isBlocking()) {
        icon = UI.Icon.Icon.create('smallicon-warning');
        icon.title = Common.UIString.UIString('Requests may be blocked');
      }
      self.UI.inspectorView.setPanelIcon('network', icon);
    }
  }
}
