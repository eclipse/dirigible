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
// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/**
 * @interface
 */
export class Linkifier {
  /**
   * @param {!Object} object
   * @param {!Options=} options
   * @return {!Node}
   */
  linkify(object, options) {
    throw new Error('linkify not implemented');
  }

  /**
   * @param {?Object} object
   * @param {!Options=} options
   * @return {!Promise<!Node>}
   */
  static linkify(object, options) {
    if (!object) {
      return Promise.reject(new Error('Can\'t linkify ' + object));
    }
    // @ts-ignore self.runtime needs to be moved to ESModules so we can import this.
    return self.runtime.extension(Linkifier, object).instance().then(linkifier => linkifier.linkify(object, options));
  }
}

/** @typedef {{tooltip: (string|undefined), preventKeyboardFocus: (boolean|undefined)}} */
// @ts-ignore typedef.
export let Options;
