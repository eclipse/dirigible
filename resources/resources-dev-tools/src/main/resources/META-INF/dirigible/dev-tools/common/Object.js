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
import {EventDescriptor, EventTarget, EventTargetEvent} from './EventTarget.js';  // eslint-disable-line no-unused-vars

/**
 * @typedef {!{thisObject: (!Object|undefined), listener: function(!EventTargetEvent):void, disposed: (boolean|undefined)}}
 */
let _listenerCallbackTuple;  // eslint-disable-line no-unused-vars

/**
 * @implements {EventTarget}
 * @unrestricted
 */
export class ObjectWrapper {
  constructor() {
    /** @type {(!Map<string|symbol, !Array<!_listenerCallbackTuple>>|undefined)} */
    this._listeners;
  }

  /**
   * @override
   * @param {string|symbol} eventType
   * @param {function(!EventTargetEvent):void} listener
   * @param {!Object=} thisObject
   * @return {!EventDescriptor}
   */
  addEventListener(eventType, listener, thisObject) {
    if (!listener) {
      console.assert(false);
    }

    if (!this._listeners) {
      this._listeners = new Map();
    }

    if (!this._listeners.has(eventType)) {
      this._listeners.set(eventType, []);
    }
    const listenerForEventType = this._listeners.get(eventType);
    if (listenerForEventType) {
      listenerForEventType.push({thisObject: thisObject, listener: listener, disposed: undefined});
    }
    return {eventTarget: this, eventType: eventType, thisObject: thisObject, listener: listener};
  }

  /**
   * @override
   * @param {string|symbol} eventType
   * @return {!Promise<*>}
   */
  once(eventType) {
    return new Promise(resolve => {
      const descriptor = this.addEventListener(eventType, event => {
        this.removeEventListener(eventType, descriptor.listener);
        resolve(event.data);
      });
    });
  }

  /**
   * @override
   * @param {string|symbol} eventType
   * @param {function(!EventTargetEvent):void} listener
   * @param {!Object=} thisObject
   */
  removeEventListener(eventType, listener, thisObject) {
    console.assert(!!listener);

    if (!this._listeners || !this._listeners.has(eventType)) {
      return;
    }
    const listeners = this._listeners.get(eventType) || [];
    for (let i = 0; i < listeners.length; ++i) {
      if (listeners[i].listener === listener && listeners[i].thisObject === thisObject) {
        listeners[i].disposed = true;
        listeners.splice(i--, 1);
      }
    }

    if (!listeners.length) {
      this._listeners.delete(eventType);
    }
  }

  /**
   * @override
   * @param {string|symbol} eventType
   * @return {boolean}
   */
  hasEventListeners(eventType) {
    return !!(this._listeners && this._listeners.has(eventType));
  }

  /**
   * @override
   * @param {string|symbol} eventType
   * @param {*=} eventData
   */
  dispatchEventToListeners(eventType, eventData) {
    if (!this._listeners || !this._listeners.has(eventType)) {
      return;
    }

    const event = /** @type {!EventTargetEvent} */ ({data: eventData});
    // @ts-ignore we do the check for undefined above
    const listeners = this._listeners.get(eventType).slice(0) || [];
    for (let i = 0; i < listeners.length; ++i) {
      if (!listeners[i].disposed) {
        listeners[i].listener.call(listeners[i].thisObject, event);
      }
    }
  }
}
