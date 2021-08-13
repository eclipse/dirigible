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

/**
 * @interface
 */
export class Revealer {
  /**
   * @param {!Object} object
   * @param {boolean=} omitFocus
   * @return {!Promise<void>}
   */
  reveal(object, omitFocus) {
    throw new Error('not implemented');
  }
}

/**
 * @param {?Object} revealable
 * @param {boolean=} omitFocus
 * @return {!Promise.<undefined>}
 */
export let reveal = function(revealable, omitFocus) {
  if (!revealable) {
    return Promise.reject(new Error('Can\'t reveal ' + revealable));
  }
  // @ts-ignore self.runtime needs to be moved to ESModules so we can import this
  return self.runtime.allInstances(Revealer, revealable).then(reveal);

  /**
   * @param {!Array.<!Revealer>} revealers
   * @return {!Promise.<void>}
   */
  function reveal(revealers) {
    const promises = [];
    for (let i = 0; i < revealers.length; ++i) {
      promises.push(revealers[i].reveal(/** @type {!Object} */ (revealable), omitFocus));
    }
    return Promise.race(promises);
  }
};

/**
 * @param {function(?Object, boolean=):!Promise.<undefined>} newReveal
 */
export function setRevealForTest(newReveal) {
  reveal = newReveal;
}

/**
 * @param {?Object} revealable
 * @return {?string}
 */
export const revealDestination = function(revealable) {
  // @ts-ignore self.runtime needs to be moved to ESModules so we can import this
  const extension = self.runtime.extension(Revealer, revealable);
  if (!extension) {
    return null;
  }
  return extension.descriptor()['destination'];
};
