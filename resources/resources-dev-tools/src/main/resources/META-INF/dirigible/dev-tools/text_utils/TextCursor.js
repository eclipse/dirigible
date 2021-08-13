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

/**
 * @unrestricted
 */
export class TextCursor {
  /**
   * @param {!Array<number>} lineEndings
   */
  constructor(lineEndings) {
    this._lineEndings = lineEndings;
    this._offset = 0;
    this._lineNumber = 0;
    this._columnNumber = 0;
  }

  /**
   * @param {number} offset
   */
  advance(offset) {
    this._offset = offset;
    while (this._lineNumber < this._lineEndings.length && this._lineEndings[this._lineNumber] < this._offset) {
      ++this._lineNumber;
    }
    this._columnNumber = this._lineNumber ? this._offset - this._lineEndings[this._lineNumber - 1] - 1 : this._offset;
  }

  /**
   * @return {number}
   */
  offset() {
    return this._offset;
  }

  /**
   * @param {number} offset
   */
  resetTo(offset) {
    this._offset = offset;
    this._lineNumber = this._lineEndings.lowerBound(offset);
    this._columnNumber = this._lineNumber ? this._offset - this._lineEndings[this._lineNumber - 1] - 1 : this._offset;
  }

  /**
   * @return {number}
   */
  lineNumber() {
    return this._lineNumber;
  }

  /**
   * @return {number}
   */
  columnNumber() {
    return this._columnNumber;
  }
}
