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
import * as Common from '../common/common.js';
import * as SDK from '../sdk/sdk.js';

/**
 * @unrestricted
 */
export class DOMStorage extends Common.ObjectWrapper.ObjectWrapper {
  /**
   * @param {!DOMStorageModel} model
   * @param {string} securityOrigin
   * @param {boolean} isLocalStorage
   */
  constructor(model, securityOrigin, isLocalStorage) {
    super();
    this._model = model;
    this._securityOrigin = securityOrigin;
    this._isLocalStorage = isLocalStorage;
  }

  /**
   * @param {string} securityOrigin
   * @param {boolean} isLocalStorage
   * @return {!Protocol.DOMStorage.StorageId}
   */
  static storageId(securityOrigin, isLocalStorage) {
    return {securityOrigin: securityOrigin, isLocalStorage: isLocalStorage};
  }

  /** @return {!Protocol.DOMStorage.StorageId} */
  get id() {
    return DOMStorage.storageId(this._securityOrigin, this._isLocalStorage);
  }

  /** @return {string} */
  get securityOrigin() {
    return this._securityOrigin;
  }

  /** @return {boolean} */
  get isLocalStorage() {
    return this._isLocalStorage;
  }

  /**
   * @return {!Promise<?Array<!Protocol.DOMStorage.Item>>}
   */
  getItems() {
    return this._model._agent.getDOMStorageItems(this.id);
  }

  /**
   * @param {string} key
   * @param {string} value
   */
  setItem(key, value) {
    this._model._agent.setDOMStorageItem(this.id, key, value);
  }

  /**
   * @param {string} key
   */
  removeItem(key) {
    this._model._agent.removeDOMStorageItem(this.id, key);
  }

  clear() {
    this._model._agent.clear(this.id);
  }
}


/** @enum {symbol} */
DOMStorage.Events = {
  DOMStorageItemsCleared: Symbol('DOMStorageItemsCleared'),
  DOMStorageItemRemoved: Symbol('DOMStorageItemRemoved'),
  DOMStorageItemAdded: Symbol('DOMStorageItemAdded'),
  DOMStorageItemUpdated: Symbol('DOMStorageItemUpdated')
};

/**
 * @unrestricted
 */
export class DOMStorageModel extends SDK.SDKModel.SDKModel {
  /**
   * @param {!SDK.SDKModel.Target} target
   */
  constructor(target) {
    super(target);

    this._securityOriginManager = target.model(SDK.SecurityOriginManager.SecurityOriginManager);
    /** @type {!Object.<string, !DOMStorage>} */
    this._storages = {};
    this._agent = target.domstorageAgent();
  }

  enable() {
    if (this._enabled) {
      return;
    }

    this.target().registerDOMStorageDispatcher(new DOMStorageDispatcher(this));
    this._securityOriginManager.addEventListener(
        SDK.SecurityOriginManager.Events.SecurityOriginAdded, this._securityOriginAdded, this);
    this._securityOriginManager.addEventListener(
        SDK.SecurityOriginManager.Events.SecurityOriginRemoved, this._securityOriginRemoved, this);

    for (const securityOrigin of this._securityOriginManager.securityOrigins()) {
      this._addOrigin(securityOrigin);
    }
    this._agent.enable();

    this._enabled = true;
  }

  /**
   * @param {string} origin
   */
  clearForOrigin(origin) {
    if (!this._enabled) {
      return;
    }
    for (const isLocal of [true, false]) {
      const key = this._storageKey(origin, isLocal);
      const storage = this._storages[key];
      storage.clear();
    }
    this._removeOrigin(origin);
    this._addOrigin(origin);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _securityOriginAdded(event) {
    this._addOrigin(/** @type {string} */ (event.data));
  }

  /**
   * @param {string} securityOrigin
   */
  _addOrigin(securityOrigin) {
    const parsed = new Common.ParsedURL.ParsedURL(securityOrigin);
    // These are "opaque" origins which are not supposed to support DOM storage.
    if (!parsed.isValid || parsed.scheme === 'data' || parsed.scheme === 'about' || parsed.scheme === 'javascript') {
      return;
    }

    for (const isLocal of [true, false]) {
      const key = this._storageKey(securityOrigin, isLocal);
      console.assert(!this._storages[key]);
      const storage = new DOMStorage(this, securityOrigin, isLocal);
      this._storages[key] = storage;
      this.dispatchEventToListeners(Events.DOMStorageAdded, storage);
    }
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _securityOriginRemoved(event) {
    this._removeOrigin(/** @type {string} */ (event.data));
  }

  /**
   * @param {string} securityOrigin
   */
  _removeOrigin(securityOrigin) {
    for (const isLocal of [true, false]) {
      const key = this._storageKey(securityOrigin, isLocal);
      const storage = this._storages[key];
      if (!storage) {
        continue;
      }
      delete this._storages[key];
      this.dispatchEventToListeners(Events.DOMStorageRemoved, storage);
    }
  }

  /**
   * @param {string} securityOrigin
   * @param {boolean} isLocalStorage
   * @return {string}
   */
  _storageKey(securityOrigin, isLocalStorage) {
    return JSON.stringify(DOMStorage.storageId(securityOrigin, isLocalStorage));
  }

  /**
   * @param {!Protocol.DOMStorage.StorageId} storageId
   */
  _domStorageItemsCleared(storageId) {
    const domStorage = this.storageForId(storageId);
    if (!domStorage) {
      return;
    }

    const eventData = {};
    domStorage.dispatchEventToListeners(DOMStorage.Events.DOMStorageItemsCleared, eventData);
  }

  /**
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @param {string} key
   */
  _domStorageItemRemoved(storageId, key) {
    const domStorage = this.storageForId(storageId);
    if (!domStorage) {
      return;
    }

    const eventData = {key: key};
    domStorage.dispatchEventToListeners(DOMStorage.Events.DOMStorageItemRemoved, eventData);
  }

  /**
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @param {string} key
   * @param {string} value
   */
  _domStorageItemAdded(storageId, key, value) {
    const domStorage = this.storageForId(storageId);
    if (!domStorage) {
      return;
    }

    const eventData = {key: key, value: value};
    domStorage.dispatchEventToListeners(DOMStorage.Events.DOMStorageItemAdded, eventData);
  }

  /**
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @param {string} key
   * @param {string} oldValue
   * @param {string} value
   */
  _domStorageItemUpdated(storageId, key, oldValue, value) {
    const domStorage = this.storageForId(storageId);
    if (!domStorage) {
      return;
    }

    const eventData = {key: key, oldValue: oldValue, value: value};
    domStorage.dispatchEventToListeners(DOMStorage.Events.DOMStorageItemUpdated, eventData);
  }

  /**
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @return {!DOMStorage}
   */
  storageForId(storageId) {
    return this._storages[JSON.stringify(storageId)];
  }

  /**
   * @return {!Array.<!DOMStorage>}
   */
  storages() {
    const result = [];
    for (const id in this._storages) {
      result.push(this._storages[id]);
    }
    return result;
  }
}

SDK.SDKModel.SDKModel.register(DOMStorageModel, SDK.SDKModel.Capability.DOM, false);

/** @enum {symbol} */
export const Events = {
  DOMStorageAdded: Symbol('DOMStorageAdded'),
  DOMStorageRemoved: Symbol('DOMStorageRemoved')
};

/**
 * @implements {Protocol.DOMStorageDispatcher}
 * @unrestricted
 */
export class DOMStorageDispatcher {
  /**
   * @param {!DOMStorageModel} model
   */
  constructor(model) {
    this._model = model;
  }

  /**
   * @override
   * @param {!Protocol.DOMStorage.StorageId} storageId
   */
  domStorageItemsCleared(storageId) {
    this._model._domStorageItemsCleared(storageId);
  }

  /**
   * @override
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @param {string} key
   */
  domStorageItemRemoved(storageId, key) {
    this._model._domStorageItemRemoved(storageId, key);
  }

  /**
   * @override
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @param {string} key
   * @param {string} value
   */
  domStorageItemAdded(storageId, key, value) {
    this._model._domStorageItemAdded(storageId, key, value);
  }

  /**
   * @override
   * @param {!Protocol.DOMStorage.StorageId} storageId
   * @param {string} key
   * @param {string} oldValue
   * @param {string} value
   */
  domStorageItemUpdated(storageId, key, oldValue, value) {
    this._model._domStorageItemUpdated(storageId, key, oldValue, value);
  }
}
