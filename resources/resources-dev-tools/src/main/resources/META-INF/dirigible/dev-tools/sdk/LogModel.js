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

import * as Host from '../host/host.js';

import {Capability, SDKModel, Target} from './SDKModel.js';  // eslint-disable-line no-unused-vars

/**
 * @implements {Protocol.LogDispatcher}
 */
export class LogModel extends SDKModel {
  /**
   * @param {!Target} target
   */
  constructor(target) {
    super(target);
    target.registerLogDispatcher(this);
    this._logAgent = target.logAgent();
    this._logAgent.enable();
    if (!Host.InspectorFrontendHost.isUnderTest()) {
      this._logAgent.startViolationsReport([
        {name: 'longTask', threshold: 200}, {name: 'longLayout', threshold: 30}, {name: 'blockedEvent', threshold: 100},
        {name: 'blockedParser', threshold: -1}, {name: 'handler', threshold: 150},
        {name: 'recurringHandler', threshold: 50}, {name: 'discouragedAPIUse', threshold: -1}
      ]);
    }
  }

  /**
   * @override
   * @param {!Protocol.Log.LogEntry} payload
   */
  entryAdded(payload) {
    this.dispatchEventToListeners(Events.EntryAdded, {logModel: this, entry: payload});
  }

  requestClear() {
    this._logAgent.clear();
  }
}

/** @enum {symbol} */
export const Events = {
  EntryAdded: Symbol('EntryAdded')
};

SDKModel.register(LogModel, Capability.Log, true);
