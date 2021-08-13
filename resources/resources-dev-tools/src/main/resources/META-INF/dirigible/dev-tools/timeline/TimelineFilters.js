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

import * as SDK from '../sdk/sdk.js';  // eslint-disable-line no-unused-vars
import * as TimelineModel from '../timeline_model/timeline_model.js';

import {TimelineUIUtils} from './TimelineUIUtils.js';

export class IsLong extends TimelineModel.TimelineModelFilter.TimelineModelFilter {
  constructor() {
    super();
    this._minimumRecordDuration = 0;
  }

  /**
   * @param {number} value
   */
  setMinimumRecordDuration(value) {
    this._minimumRecordDuration = value;
  }

  /**
   * @override
   * @param {!SDK.TracingModel.Event} event
   * @return {boolean}
   */
  accept(event) {
    const duration = event.endTime ? event.endTime - event.startTime : 0;
    return duration >= this._minimumRecordDuration;
  }
}

export class Category extends TimelineModel.TimelineModelFilter.TimelineModelFilter {
  constructor() {
    super();
  }

  /**
   * @override
   * @param {!SDK.TracingModel.Event} event
   * @return {boolean}
   */
  accept(event) {
    return !TimelineUIUtils.eventStyle(event).category.hidden;
  }
}

export class TimelineRegExp extends TimelineModel.TimelineModelFilter.TimelineModelFilter {
  /**
   * @param {!RegExp=} regExp
   */
  constructor(regExp) {
    super();
    /** @type {?RegExp} */
    this._regExp;
    this.setRegExp(regExp || null);
  }

  /**
   * @param {?RegExp} regExp
   */
  setRegExp(regExp) {
    this._regExp = regExp;
  }

  /**
   * @return {?RegExp}
   */
  regExp() {
    return this._regExp;
  }

  /**
   * @override
   * @param {!SDK.TracingModel.Event} event
   * @return {boolean}
   */
  accept(event) {
    return !this._regExp || TimelineUIUtils.testContentMatching(event, this._regExp);
  }
}
