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
// Copyright 2016 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Extensions from '../extensions/extensions.js';  // eslint-disable-line no-unused-vars
import * as SDK from '../sdk/sdk.js';                       // eslint-disable-line no-unused-vars

import {PerformanceModel} from './PerformanceModel.js';      // eslint-disable-line no-unused-vars
import {Client, TimelineLoader} from './TimelineLoader.js';  // eslint-disable-line no-unused-vars

/**
 * @implements {Extensions.ExtensionTraceProvider.TracingSession}
 * @implements {Client}
 */
export class ExtensionTracingSession {
  /**
   * @param {!Extensions.ExtensionTraceProvider.ExtensionTraceProvider} provider
   * @param {!PerformanceModel} performanceModel
   */
  constructor(provider, performanceModel) {
    this._provider = provider;
    this._performanceModel = performanceModel;
    /** @type {function()} */
    this._completionCallback;
    this._completionPromise = new Promise(fulfill => {
      this._completionCallback = fulfill;
    });
    this._timeOffset = 0;
  }

  /** @override */
  loadingStarted() {
  }

  /** @override */
  processingStarted() {
  }

  /**
   * @override
   * @param {number=} progress
   */
  loadingProgress(progress) {
  }

  /**
   * @override
   * @param {?SDK.TracingModel.TracingModel} tracingModel
   */
  loadingComplete(tracingModel) {
    if (!tracingModel) {
      return;
    }
    this._performanceModel.addExtensionEvents(this._provider.longDisplayName(), tracingModel, this._timeOffset);
    this._completionCallback();
  }

  /**
   * @override
   * @param {string} url
   * @param {number} timeOffsetMicroseconds
   */
  complete(url, timeOffsetMicroseconds) {
    if (!url) {
      this._completionCallback();
      return;
    }
    this._timeOffset = timeOffsetMicroseconds;
    TimelineLoader.loadFromURL(url, this);
  }

  start() {
    this._provider.start(this);
  }

  /** @return {!Promise<string>} */
  stop() {
    this._provider.stop();
    return this._completionPromise;
  }
}
