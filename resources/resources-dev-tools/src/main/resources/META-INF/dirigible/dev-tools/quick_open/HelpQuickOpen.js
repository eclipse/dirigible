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

import {Provider} from './FilteredListWidget.js';
import {QuickOpenImpl} from './QuickOpen.js';

export class HelpQuickOpen extends Provider {
  constructor() {
    super();
    /** @type {!Array<{prefix: string, title: string}>} */
    this._providers = [];
    self.runtime.extensions(Provider).forEach(this._addProvider.bind(this));
  }

  /**
   * @param {!Root.Runtime.Extension} extension
   */
  _addProvider(extension) {
    if (extension.title()) {
      this._providers.push({prefix: extension.descriptor()['prefix'], title: extension.title()});
    }
  }

  /**
   * @override
   * @return {number}
   */
  itemCount() {
    return this._providers.length;
  }

  /**
   * @override
   * @param {number} itemIndex
   * @return {string}
   */
  itemKeyAt(itemIndex) {
    return this._providers[itemIndex].prefix;
  }

  /**
   * @override
   * @param {number} itemIndex
   * @param {string} query
   * @return {number}
   */
  itemScoreAt(itemIndex, query) {
    return -this._providers[itemIndex].prefix.length;
  }

  /**
   * @override
   * @param {number} itemIndex
   * @param {string} query
   * @param {!Element} titleElement
   * @param {!Element} subtitleElement
   */
  renderItem(itemIndex, query, titleElement, subtitleElement) {
    const provider = this._providers[itemIndex];
    const prefixElement = titleElement.createChild('span', 'monospace');
    prefixElement.textContent = (provider.prefix || '…') + ' ';
    titleElement.createTextChild(provider.title);
  }

  /**
   * @override
   * @param {?number} itemIndex
   * @param {string} promptValue
   */
  selectItem(itemIndex, promptValue) {
    if (itemIndex !== null) {
      QuickOpenImpl.show(this._providers[itemIndex].prefix);
    }
  }

  /**
   * @override
   * @return {boolean}
   */
  renderAsTwoRows() {
    return false;
  }
}
