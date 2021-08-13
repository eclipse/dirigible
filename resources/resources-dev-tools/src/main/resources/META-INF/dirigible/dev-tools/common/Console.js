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

import {ObjectWrapper} from './Object.js';
import {reveal} from './Revealer.js';

/**
 * @type {!Console}
 */
let consoleInstance;

/**
 * @unrestricted
 */
export class Console extends ObjectWrapper {
  /**
   * Instantiable via the instance() factory below.
   *
   * @private
   */
  constructor() {
    super();
    /** @type {!Array.<!Message>} */
    this._messages = [];
  }

  /**
   * @param {{forceNew: boolean}} opts
   */
  static instance({forceNew} = {forceNew: false}) {
    if (!consoleInstance || forceNew) {
      consoleInstance = new Console();
    }

    return consoleInstance;
  }

  /**
   * @param {string} text
   * @param {!MessageLevel} level
   * @param {boolean=} show
   */
  addMessage(text, level, show) {
    const message = new Message(text, level || MessageLevel.Info, Date.now(), show || false);
    this._messages.push(message);
    this.dispatchEventToListeners(Events.MessageAdded, message);
  }

  /**
   * @param {string} text
   */
  log(text) {
    this.addMessage(text, MessageLevel.Info);
  }

  /**
   * @param {string} text
   */
  warn(text) {
    this.addMessage(text, MessageLevel.Warning);
  }

  /**
   * @param {string} text
   */
  error(text) {
    this.addMessage(text, MessageLevel.Error, true);
  }

  /**
   * @return {!Array.<!Message>}
   */
  messages() {
    return this._messages;
  }

  show() {
    this.showPromise();
  }

  /**
   * @return {!Promise.<undefined>}
   */
  showPromise() {
    return reveal(this);
  }
}

/** @enum {symbol} */
export const Events = {
  MessageAdded: Symbol('messageAdded')
};

/**
 * @enum {string}
 */
export const MessageLevel = {
  Info: 'info',
  Warning: 'warning',
  Error: 'error'
};

/**
 * @unrestricted
 */
export class Message {
  /**
   * @param {string} text
   * @param {!MessageLevel} level
   * @param {number} timestamp
   * @param {boolean} show
   */
  constructor(text, level, timestamp, show) {
    this.text = text;
    this.level = level;
    this.timestamp = (typeof timestamp === 'number') ? timestamp : Date.now();
    this.show = show;
  }
}
