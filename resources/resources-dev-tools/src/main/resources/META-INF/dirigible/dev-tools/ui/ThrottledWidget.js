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

import * as Common from '../common/common.js';
import {VBox} from './Widget.js';

/**
 * @unrestricted
 */
export class ThrottledWidget extends VBox {
  /**
   * @param {boolean=} isWebComponent
   * @param {number=} timeout
   */
  constructor(isWebComponent, timeout) {
    super(isWebComponent);
    this._updateThrottler = new Common.Throttler.Throttler(timeout === undefined ? 100 : timeout);
    this._updateWhenVisible = false;
  }

  /**
   * @protected
   * @return {!Promise<?>}
   */
  doUpdate() {
    return Promise.resolve();
  }

  update() {
    this._updateWhenVisible = !this.isShowing();
    if (this._updateWhenVisible) {
      return;
    }
    this._updateThrottler.schedule(innerUpdate.bind(this));

    /**
     * @this {ThrottledWidget}
     * @return {!Promise<?>}
     */
    function innerUpdate() {
      if (this.isShowing()) {
        return this.doUpdate();
      }
      this._updateWhenVisible = true;
      return Promise.resolve();
    }
  }

  /**
   * @override
   */
  wasShown() {
    super.wasShown();
    if (this._updateWhenVisible) {
      this.update();
    }
  }
}
