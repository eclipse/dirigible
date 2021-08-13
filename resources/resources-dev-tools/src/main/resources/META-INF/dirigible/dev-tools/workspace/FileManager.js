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
import * as Host from '../host/host.js';

/**
 * @unrestricted
 */
export class FileManager extends Common.ObjectWrapper.ObjectWrapper {
  constructor() {
    super();
    /** @type {!Map<string, function(?{fileSystemPath: (string|undefined)}):void>} */
    this._saveCallbacks = new Map();
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.SavedURL, this._savedURL, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.CanceledSaveURL, this._canceledSavedURL, this);
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.events.addEventListener(
        Host.InspectorFrontendHostAPI.Events.AppendedToURL, this._appendedToURL, this);
  }

  /**
   * @param {string} url
   * @param {string} content
   * @param {boolean} forceSaveAs
   * @return {!Promise<?{fileSystemPath: (string|undefined)}>}
   */
  save(url, content, forceSaveAs) {
    // Remove this url from the saved URLs while it is being saved.
    const result = new Promise(resolve => this._saveCallbacks.set(url, resolve));
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.save(url, content, forceSaveAs);
    return result;
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _savedURL(event) {
    const url = /** @type {string} */ (event.data.url);
    const callback = this._saveCallbacks.get(url);
    this._saveCallbacks.delete(url);
    if (callback) {
      callback({fileSystemPath: /** @type {string} */ (event.data.fileSystemPath)});
    }
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _canceledSavedURL(event) {
    const url = /** @type {string} */ (event.data);
    const callback = this._saveCallbacks.get(url);
    this._saveCallbacks.delete(url);
    if (callback) {
      callback(null);
    }
  }

  /**
   * @param {string} url
   * @param {string} content
   */
  append(url, content) {
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.append(url, content);
  }

  /**
   * @param {string} url
   */
  close(url) {
    Host.InspectorFrontendHost.InspectorFrontendHostInstance.close(url);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _appendedToURL(event) {
    const url = /** @type {string} */ (event.data);
    this.dispatchEventToListeners(Events.AppendedToURL, url);
  }
}

/** @enum {symbol} */
export const Events = {
  AppendedToURL: Symbol('AppendedToURL')
};
