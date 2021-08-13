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
import {Trie} from './Trie.js';

/**
 * @unrestricted
 */
export class TextDictionary {
  constructor() {
    /** @type {!Map<string, number>} */
    this._words = new Map();
    this._index = new Trie();
  }

  /**
   * @param {string} word
   */
  addWord(word) {
    let count = this._words.get(word) || 0;
    ++count;
    this._words.set(word, count);
    this._index.add(word);
  }

  /**
   * @param {string} word
   */
  removeWord(word) {
    let count = this._words.get(word) || 0;
    if (!count) {
      return;
    }
    if (count === 1) {
      this._words.delete(word);
      this._index.remove(word);
      return;
    }
    --count;
    this._words.set(word, count);
  }

  /**
   * @param {string} prefix
   * @return {!Array.<string>}
   */
  wordsWithPrefix(prefix) {
    return this._index.words(prefix);
  }

  /**
   * @param {string} word
   * @return {boolean}
   */
  hasWord(word) {
    return this._words.has(word);
  }

  /**
   * @param {string} word
   * @return {number}
   */
  wordCount(word) {
    return this._words.get(word) || 0;
  }

  reset() {
    this._words.clear();
    this._index.clear();
  }
}
