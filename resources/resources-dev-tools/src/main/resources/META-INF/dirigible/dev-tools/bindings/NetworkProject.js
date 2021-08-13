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
import * as Workspace from '../workspace/workspace.js';  // eslint-disable-line no-unused-vars

/**
 * @type {!NetworkProjectManager}
 */
let networkProjectManagerInstance;

export class NetworkProjectManager extends Common.ObjectWrapper.ObjectWrapper {
  /**
   * @private
   */
  constructor() {
    super();
  }

  /**
   * @param {{forceNew: boolean}} opts
   */
  static instance({forceNew} = {forceNew: false}) {
    if (!networkProjectManagerInstance || forceNew) {
      networkProjectManagerInstance = new NetworkProjectManager();
    }

    return networkProjectManagerInstance;
  }
}

export const Events = {
  FrameAttributionAdded: Symbol('FrameAttributionAdded'),
  FrameAttributionRemoved: Symbol('FrameAttributionRemoved')
};

/**
 * @unrestricted
 */
export class NetworkProject {
  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @param {string} frameId
   */
  static _resolveFrame(uiSourceCode, frameId) {
    const target = NetworkProject.targetForUISourceCode(uiSourceCode);
    const resourceTreeModel = target && target.model(SDK.ResourceTreeModel.ResourceTreeModel);
    return resourceTreeModel ? resourceTreeModel.frameForId(frameId) : null;
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @param {string} frameId
   */
  static setInitialFrameAttribution(uiSourceCode, frameId) {
    const frame = NetworkProject._resolveFrame(uiSourceCode, frameId);
    if (!frame) {
      return;
    }
    /** @type {!Map<string, !{frame: !SDK.ResourceTreeModel.ResourceTreeFrame, count: number}>} */
    const attribution = new Map();
    attribution.set(frameId, {frame: frame, count: 1});
    uiSourceCode[_frameAttributionSymbol] = attribution;
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} fromUISourceCode
   * @param {!Workspace.UISourceCode.UISourceCode} toUISourceCode
   */
  static cloneInitialFrameAttribution(fromUISourceCode, toUISourceCode) {
    const fromAttribution = fromUISourceCode[_frameAttributionSymbol];
    if (!fromAttribution) {
      return;
    }
    /** @type {!Map<string, !{frame: !SDK.ResourceTreeModel.ResourceTreeFrame, count: number}>} */
    const toAttribution = new Map();
    toUISourceCode[_frameAttributionSymbol] = toAttribution;
    for (const frameId of fromAttribution.keys()) {
      const value = fromAttribution.get(frameId);
      toAttribution.set(frameId, {frame: value.frame, count: value.count});
    }
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @param {string} frameId
   */
  static addFrameAttribution(uiSourceCode, frameId) {
    const frame = NetworkProject._resolveFrame(uiSourceCode, frameId);
    if (!frame) {
      return;
    }
    const frameAttribution = uiSourceCode[_frameAttributionSymbol];
    const attributionInfo = frameAttribution.get(frameId) || {frame: frame, count: 0};
    attributionInfo.count += 1;
    frameAttribution.set(frameId, attributionInfo);
    if (attributionInfo.count !== 1) {
      return;
    }

    const data = {uiSourceCode: uiSourceCode, frame: frame};
    NetworkProjectManager.instance().dispatchEventToListeners(Events.FrameAttributionAdded, data);
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @param {string} frameId
   */
  static removeFrameAttribution(uiSourceCode, frameId) {
    const frameAttribution = uiSourceCode[_frameAttributionSymbol];
    if (!frameAttribution) {
      return;
    }
    const attributionInfo = frameAttribution.get(frameId);
    console.assert(attributionInfo, 'Failed to remove frame attribution for url: ' + uiSourceCode.url());
    attributionInfo.count -= 1;
    if (attributionInfo.count > 0) {
      return;
    }
    frameAttribution.delete(frameId);
    const data = {uiSourceCode: uiSourceCode, frame: attributionInfo.frame};
    NetworkProjectManager.instance().dispatchEventToListeners(Events.FrameAttributionRemoved, data);
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {?SDK.SDKModel.Target} target
   */
  static targetForUISourceCode(uiSourceCode) {
    return uiSourceCode.project()[_targetSymbol] || null;
  }

  /**
   * @param {!Workspace.Workspace.Project} project
   * @param {!SDK.SDKModel.Target} target
   */
  static setTargetForProject(project, target) {
    project[_targetSymbol] = target;
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {!Array<!SDK.ResourceTreeModel.ResourceTreeFrame>}
   */
  static framesForUISourceCode(uiSourceCode) {
    const target = NetworkProject.targetForUISourceCode(uiSourceCode);
    const resourceTreeModel = target && target.model(SDK.ResourceTreeModel.ResourceTreeModel);
    const attribution = uiSourceCode[_frameAttributionSymbol];
    if (!resourceTreeModel || !attribution) {
      return [];
    }
    const frames = Array.from(attribution.keys()).map(frameId => resourceTreeModel.frameForId(frameId));
    return frames.filter(frame => !!frame);
  }
}

const _targetSymbol = Symbol('target');
const _frameAttributionSymbol = Symbol('_frameAttributionSymbol');
