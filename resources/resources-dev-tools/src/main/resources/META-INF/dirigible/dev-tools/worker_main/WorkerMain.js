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
// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Common from '../common/common.js';
import * as Components from '../components/components.js';
import * as MobileThrottling from '../mobile_throttling/mobile_throttling.js';
import * as SDK from '../sdk/sdk.js';

/**
 * @implements {Common.Runnable.Runnable}
 */
export class WorkerMainImpl extends Common.ObjectWrapper.ObjectWrapper {
  /**
   * @override
   */
  run() {
    SDK.Connections.initMainConnection(() => {
      SDK.SDKModel.TargetManager.instance().createTarget('main', ls`Main`, SDK.SDKModel.Type.ServiceWorker, null);
    }, Components.TargetDetachedDialog.TargetDetachedDialog.webSocketConnectionLost);
    new MobileThrottling.NetworkPanelIndicator.NetworkPanelIndicator();
  }
}

SDK.ChildTargetManager.ChildTargetManager.install(async ({target, waitingForDebugger}) => {
  // Only pause the new worker if debugging SW - we are going through the pause on start checkbox.
  if (target.parentTarget() || target.type() !== SDK.SDKModel.Type.ServiceWorker || !waitingForDebugger) {
    return;
  }
  const debuggerModel = target.model(SDK.DebuggerModel.DebuggerModel);
  if (!debuggerModel) {
    return;
  }
  if (!debuggerModel.isReadyToPause()) {
    await debuggerModel.once(SDK.DebuggerModel.Events.DebuggerIsReadyToPause);
  }
  debuggerModel.pause();
});
