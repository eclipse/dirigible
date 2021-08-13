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
import * as Common from '../common/common.js';  // eslint-disable-line no-unused-vars
import * as SDK from '../sdk/sdk.js';

/**
 * @unrestricted
 */
export class ApplicationCacheModel extends SDK.SDKModel.SDKModel {
  /**
   * @param {!SDK.SDKModel.Target} target
   */
  constructor(target) {
    super(target);

    target.registerApplicationCacheDispatcher(new ApplicationCacheDispatcher(this));
    this._agent = target.applicationCacheAgent();
    this._agent.enable();

    const resourceTreeModel = target.model(SDK.ResourceTreeModel.ResourceTreeModel);
    resourceTreeModel.addEventListener(SDK.ResourceTreeModel.Events.FrameNavigated, event => {
      this._frameNavigated(event);
    }, this);
    resourceTreeModel.addEventListener(SDK.ResourceTreeModel.Events.FrameDetached, this._frameDetached, this);

    this._statuses = {};
    this._manifestURLsByFrame = {};

    this._mainFrameNavigated();
    this._onLine = true;
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  async _frameNavigated(event) {
    const frame = /** @type {!SDK.ResourceTreeModel.ResourceTreeFrame} */ (event.data);
    if (frame.isMainFrame()) {
      this._mainFrameNavigated();
      return;
    }

    const frameId = frame.id;
    const manifestURL = await this._agent.getManifestForFrame(frameId);
    if (manifestURL !== null && !manifestURL) {
      this._frameManifestRemoved(frameId);
    }
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _frameDetached(event) {
    const frame = /** @type {!SDK.ResourceTreeModel.ResourceTreeFrame} */ (event.data);
    this._frameManifestRemoved(frame.id);
  }

  reset() {
    this._statuses = {};
    this._manifestURLsByFrame = {};
    this.dispatchEventToListeners(Events.FrameManifestsReset);
  }

  async _mainFrameNavigated() {
    const framesWithManifests = await this._agent.getFramesWithManifests();
    for (const frame of framesWithManifests || []) {
      this._frameManifestUpdated(frame.frameId, frame.manifestURL, frame.status);
    }
  }

  /**
   * @param {string} frameId
   * @param {string} manifestURL
   * @param {number} status
   */
  _frameManifestUpdated(frameId, manifestURL, status) {
    if (status === UNCACHED) {
      this._frameManifestRemoved(frameId);
      return;
    }

    if (!manifestURL) {
      return;
    }

    if (this._manifestURLsByFrame[frameId] && manifestURL !== this._manifestURLsByFrame[frameId]) {
      this._frameManifestRemoved(frameId);
    }

    const statusChanged = this._statuses[frameId] !== status;
    this._statuses[frameId] = status;

    if (!this._manifestURLsByFrame[frameId]) {
      this._manifestURLsByFrame[frameId] = manifestURL;
      this.dispatchEventToListeners(Events.FrameManifestAdded, frameId);
    }

    if (statusChanged) {
      this.dispatchEventToListeners(Events.FrameManifestStatusUpdated, frameId);
    }
  }

  /**
   * @param {string} frameId
   */
  _frameManifestRemoved(frameId) {
    if (!this._manifestURLsByFrame[frameId]) {
      return;
    }

    delete this._manifestURLsByFrame[frameId];
    delete this._statuses[frameId];

    this.dispatchEventToListeners(Events.FrameManifestRemoved, frameId);
  }

  /**
   * @param {string} frameId
   * @return {string}
   */
  frameManifestURL(frameId) {
    return this._manifestURLsByFrame[frameId] || '';
  }

  /**
   * @param {string} frameId
   * @return {number}
   */
  frameManifestStatus(frameId) {
    return this._statuses[frameId] || UNCACHED;
  }

  /**
   * @return {boolean}
   */
  get onLine() {
    return this._onLine;
  }

  /**
   * @param {string} frameId
   * @param {string} manifestURL
   * @param {number} status
   */
  _statusUpdated(frameId, manifestURL, status) {
    this._frameManifestUpdated(frameId, manifestURL, status);
  }

  /**
   * @param {string} frameId
   * @return {!Promise<?Protocol.ApplicationCache.ApplicationCache>}
   */
  requestApplicationCache(frameId) {
    return this._agent.getApplicationCacheForFrame(frameId);
  }

  /**
   * @param {boolean} isNowOnline
   */
  _networkStateUpdated(isNowOnline) {
    this._onLine = isNowOnline;
    this.dispatchEventToListeners(Events.NetworkStateChanged, isNowOnline);
  }
}

SDK.SDKModel.SDKModel.register(ApplicationCacheModel, SDK.SDKModel.Capability.DOM, false);

/** @enum {symbol} */
export const Events = {
  FrameManifestStatusUpdated: Symbol('FrameManifestStatusUpdated'),
  FrameManifestAdded: Symbol('FrameManifestAdded'),
  FrameManifestRemoved: Symbol('FrameManifestRemoved'),
  FrameManifestsReset: Symbol('FrameManifestsReset'),
  NetworkStateChanged: Symbol('NetworkStateChanged')
};

/**
 * @implements {Protocol.ApplicationCacheDispatcher}
 * @unrestricted
 */
export class ApplicationCacheDispatcher {
  constructor(applicationCacheModel) {
    this._applicationCacheModel = applicationCacheModel;
  }

  /**
   * @override
   * @param {string} frameId
   * @param {string} manifestURL
   * @param {number} status
   */
  applicationCacheStatusUpdated(frameId, manifestURL, status) {
    this._applicationCacheModel._statusUpdated(frameId, manifestURL, status);
  }

  /**
   * @override
   * @param {boolean} isNowOnline
   */
  networkStateUpdated(isNowOnline) {
    this._applicationCacheModel._networkStateUpdated(isNowOnline);
  }
}

export const UNCACHED = 0;
export const IDLE = 1;
export const CHECKING = 2;
export const DOWNLOADING = 3;
export const UPDATEREADY = 4;
export const OBSOLETE = 5;
