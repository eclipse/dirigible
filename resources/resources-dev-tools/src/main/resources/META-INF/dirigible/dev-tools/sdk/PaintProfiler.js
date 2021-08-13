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
import {Capability, SDKModel, Target} from './SDKModel.js';  // eslint-disable-line no-unused-vars

export class PaintProfilerModel extends SDKModel {
  /**
   * @param {!Target} target
   */
  constructor(target) {
    super(target);
    this._layerTreeAgent = target.layerTreeAgent();
  }

  /**
   * @param {!Array.<!PictureFragment>} fragments
   * @return {!Promise<?PaintProfilerSnapshot>}
   */
  async loadSnapshotFromFragments(fragments) {
    const snapshotId = await this._layerTreeAgent.loadSnapshot(fragments);
    return snapshotId && new PaintProfilerSnapshot(this, snapshotId);
  }

  /**
   * @param {string} encodedPicture
   * @return {!Promise<?PaintProfilerSnapshot>}
   */
  loadSnapshot(encodedPicture) {
    const fragment = {x: 0, y: 0, picture: encodedPicture};
    return this.loadSnapshotFromFragments([fragment]);
  }

  /**
   * @param {string} layerId
   * @return {!Promise<?PaintProfilerSnapshot>}
   */
  async makeSnapshot(layerId) {
    const snapshotId = await this._layerTreeAgent.makeSnapshot(layerId);
    return snapshotId && new PaintProfilerSnapshot(this, snapshotId);
  }
}

export class PaintProfilerSnapshot {
  /**
   * @param {!PaintProfilerModel} paintProfilerModel
   * @param {string} snapshotId
   */
  constructor(paintProfilerModel, snapshotId) {
    this._paintProfilerModel = paintProfilerModel;
    this._id = snapshotId;
    this._refCount = 1;
  }

  release() {
    console.assert(this._refCount > 0, 'release is already called on the object');
    if (!--this._refCount) {
      this._paintProfilerModel._layerTreeAgent.releaseSnapshot(this._id);
    }
  }

  addReference() {
    ++this._refCount;
    console.assert(this._refCount > 0, 'Referencing a dead object');
  }

  /**
   * @param {number=} scale
   * @param {number=} firstStep
   * @param {number=} lastStep
   * @return {!Promise<?string>}
   */
  replay(scale, firstStep, lastStep) {
    return this._paintProfilerModel._layerTreeAgent.replaySnapshot(this._id, firstStep, lastStep, scale || 1.0);
  }

  /**
   * @param {?Protocol.DOM.Rect} clipRect
   * @return {!Promise<?Array<!Protocol.LayerTree.PaintProfile>>}
   */
  profile(clipRect) {
    return this._paintProfilerModel._layerTreeAgent.profileSnapshot(this._id, 5, 1, clipRect || undefined);
  }

  /**
   * @return {!Promise<?Array<!PaintProfilerLogItem>>}
   */
  async commandLog() {
    const log = await this._paintProfilerModel._layerTreeAgent.snapshotCommandLog(this._id);
    return log &&
        log.map((entry, index) => new PaintProfilerLogItem(/** @type {!RawPaintProfilerLogItem} */ (entry), index));
  }
}

/**
 * @unrestricted
 */
export class PaintProfilerLogItem {
  /**
   * @param {!RawPaintProfilerLogItem} rawEntry
   * @param {number} commandIndex
   */
  constructor(rawEntry, commandIndex) {
    this.method = rawEntry.method;
    this.params = rawEntry.params;
    this.commandIndex = commandIndex;
  }
}

SDKModel.register(PaintProfilerModel, Capability.DOM, false);

/** @typedef {!{
        rect: !Protocol.DOM.Rect,
        snapshot: !PaintProfilerSnapshot
    }}
*/
export let SnapshotWithRect;

/**
 * @typedef {!{x: number, y: number, picture: string}}
 */
export let PictureFragment;

/**
 * @typedef {!{method: string, params: ?Object<string, *>}}
 */
export let RawPaintProfilerLogItem;
