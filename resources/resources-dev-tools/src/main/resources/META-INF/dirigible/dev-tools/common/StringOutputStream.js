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
// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * @interface
 */
export class OutputStream {
  /**
   * @param {string} data
   * @return {!Promise.<void>}
   */
  async write(data) {
  }

  /**
   * @return {!Promise.<void>}
   */
  async close() {
  }
}

/**
 * @implements {OutputStream}
 */
export class StringOutputStream {
  constructor() {
    this._data = '';
  }

  /**
   * @override
   * @param {string} chunk
   * @return {!Promise.<void>}
   */
  async write(chunk) {
    this._data += chunk;
  }

  /**
   * @override
   * @return {!Promise.<void>}
   */
  async close() {
  }

  /**
   * @return {string}
   */
  data() {
    return this._data;
  }
}
