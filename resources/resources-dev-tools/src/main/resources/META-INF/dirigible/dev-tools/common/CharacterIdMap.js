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

/**
 * @template T
 * @unrestricted
 */
export class CharacterIdMap {
  constructor() {
    /** @type {!Map<T, string>} */
    this._elementToCharacter = new Map();
    /** @type {!Map<string, T>} */
    this._characterToElement = new Map();
    this._charCode = 33;
  }

  /**
   * @param {T} object
   * @return {string}
   */
  toChar(object) {
    let character = this._elementToCharacter.get(object);
    if (!character) {
      if (this._charCode >= 0xFFFF) {
        throw new Error('CharacterIdMap ran out of capacity!');
      }
      character = String.fromCharCode(this._charCode++);
      this._elementToCharacter.set(object, character);
      this._characterToElement.set(character, object);
    }
    return character;
  }

  /**
   * @param {string} character
   * @return {?T}
   */
  fromChar(character) {
    const object = this._characterToElement.get(character);
    if (object === undefined) {
      return null;
    }
    return object;
  }
}
