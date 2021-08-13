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

import * as SDK from '../sdk/sdk.js';

/**
 * @typedef {{
 *     name: string,
 *     value: *,
 *     timestamp: (number|string|undefined),
 *     displayTimestamp: string
 * }}
 */
export let Event;

/**
 * @implements {Protocol.MediaDispatcher}
 */
export class MediaModel extends SDK.SDKModel.SDKModel {
  /**
   * @param {!SDK.SDKModel.Target} target
   */
  constructor(target) {
    super(target);

    this._enabled = false;
    this._agent = target.mediaAgent();

    target.registerMediaDispatcher(this);
  }

  /**
   * @override
   * @return {!Promise}
   */
  resumeModel() {
    if (!this._enabled) {
      return Promise.resolve();
    }
    return this._agent.enable();
  }

  ensureEnabled() {
    this._agent.enable();
    this._enabled = true;
  }

  /**
   * @param {!Protocol.Media.PlayerId} playerId
   * @param {!Array.<!Protocol.Media.PlayerProperty>} properties
   * @override
   */
  playerPropertiesChanged(playerId, properties) {
    this.dispatchEventToListeners(
        Events.PlayerPropertiesChanged, {playerId: playerId, properties: properties});
  }

  /**
   * @param {!Protocol.Media.PlayerId} playerId
   * @param {!Array.<!Protocol.Media.PlayerEvent>} events
   * @override
   */
  playerEventsAdded(playerId, events) {
    this.dispatchEventToListeners(Events.PlayerEventsAdded, {playerId: playerId, events: events});
  }

  /**
   * @param {!Array.<!Protocol.Media.PlayerId>} playerIds
   * @override
   */
  playersCreated(playerIds) {
    this.dispatchEventToListeners(Events.PlayersCreated, playerIds);
  }
}

SDK.SDKModel.SDKModel.register(MediaModel, SDK.SDKModel.Capability.DOM, false);

/** @enum {symbol} */
export const Events = {
  PlayerPropertiesChanged: Symbol('PlayerPropertiesChanged'),
  PlayerEventsAdded: Symbol('PlayerEventsAdded'),
  PlayersCreated: Symbol('PlayersCreated')
};

/** @enum {string} */
export const MediaChangeTypeKeys = {
  Event: 'Events',
  Property: 'Properties'
};
