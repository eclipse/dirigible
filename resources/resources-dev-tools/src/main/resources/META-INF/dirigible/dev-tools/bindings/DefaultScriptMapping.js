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
import * as Workspace from '../workspace/workspace.js';

import {ContentProviderBasedProject} from './ContentProviderBasedProject.js';
import {DebuggerSourceMapping, DebuggerWorkspaceBinding} from './DebuggerWorkspaceBinding.js';  // eslint-disable-line no-unused-vars

/**
 * @implements {DebuggerSourceMapping}
 * @unrestricted
 */
export class DefaultScriptMapping {
  /**
   * @param {!SDK.DebuggerModel.DebuggerModel} debuggerModel
   * @param {!Workspace.Workspace.WorkspaceImpl} workspace
   * @param {!DebuggerWorkspaceBinding} debuggerWorkspaceBinding
   */
  constructor(debuggerModel, workspace, debuggerWorkspaceBinding) {
    this._debuggerModel = debuggerModel;
    this._debuggerWorkspaceBinding = debuggerWorkspaceBinding;
    this._project = new ContentProviderBasedProject(
        workspace, 'debugger:' + debuggerModel.target().id(), Workspace.Workspace.projectTypes.Debugger, '',
        true /* isServiceProject */);
    this._eventListeners = [
      debuggerModel.addEventListener(SDK.DebuggerModel.Events.GlobalObjectCleared, this._debuggerReset, this),
      debuggerModel.addEventListener(SDK.DebuggerModel.Events.ParsedScriptSource, this._parsedScriptSource, this),
      debuggerModel.addEventListener(
          SDK.DebuggerModel.Events.DiscardedAnonymousScriptSource, this._discardedScriptSource, this)
    ];
    this._scriptSymbol = Symbol('symbol');
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {?SDK.Script.Script}
   */
  static scriptForUISourceCode(uiSourceCode) {
    const scripts = uiSourceCode[_scriptsSymbol];
    return scripts ? scripts.values().next().value : null;
  }

  /**
   * @override
   * @param {!SDK.DebuggerModel.Location} rawLocation
   * @return {?Workspace.UISourceCode.UILocation}
   */
  rawLocationToUILocation(rawLocation) {
    const script = rawLocation.script();
    if (!script) {
      return null;
    }
    const uiSourceCode = script[_uiSourceCodeSymbol];
    const lineNumber = rawLocation.lineNumber - (script.isInlineScriptWithSourceURL() ? script.lineOffset : 0);
    let columnNumber = rawLocation.columnNumber || 0;
    if (script.isInlineScriptWithSourceURL() && !lineNumber && columnNumber) {
      columnNumber -= script.columnOffset;
    }
    return uiSourceCode.uiLocation(lineNumber, columnNumber);
  }

  /**
   * @override
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @param {number} lineNumber
   * @param {number} columnNumber
   * @return {!Array<!SDK.DebuggerModel.Location>}
   */
  uiLocationToRawLocations(uiSourceCode, lineNumber, columnNumber) {
    const script = uiSourceCode[this._scriptSymbol];
    if (!script) {
      return [];
    }
    if (script.isInlineScriptWithSourceURL()) {
      return [this._debuggerModel.createRawLocation(
          script, lineNumber + script.lineOffset, lineNumber ? columnNumber : columnNumber + script.columnOffset)];
    }
    return [this._debuggerModel.createRawLocation(script, lineNumber, columnNumber)];
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _parsedScriptSource(event) {
    const script = /** @type {!SDK.Script.Script} */ (event.data);
    const name = Common.ParsedURL.ParsedURL.extractName(script.sourceURL);
    const url = 'debugger:///VM' + script.scriptId + (name ? ' ' + name : '');

    const uiSourceCode = this._project.createUISourceCode(url, Common.ResourceType.resourceTypes.Script);
    uiSourceCode[this._scriptSymbol] = script;
    if (!uiSourceCode[_scriptsSymbol]) {
      uiSourceCode[_scriptsSymbol] = new Set([script]);
    } else {
      uiSourceCode[_scriptsSymbol].add(script);
    }
    script[_uiSourceCodeSymbol] = uiSourceCode;
    this._project.addUISourceCodeWithProvider(uiSourceCode, script, null, 'text/javascript');
    this._debuggerWorkspaceBinding.updateLocations(script);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _discardedScriptSource(event) {
    const script = /** @type {!SDK.Script.Script} */ (event.data);
    const uiSourceCode = script[_uiSourceCodeSymbol];
    if (!uiSourceCode) {
      return;
    }
    delete script[_uiSourceCodeSymbol];
    delete uiSourceCode[this._scriptSymbol];
    uiSourceCode[_scriptsSymbol].delete(script);
    if (!uiSourceCode[_scriptsSymbol].size) {
      delete uiSourceCode[_scriptsSymbol];
    }
    this._project.removeUISourceCode(uiSourceCode.url());
  }

  _debuggerReset() {
    this._project.reset();
  }

  dispose() {
    Common.EventTarget.EventTarget.removeEventListeners(this._eventListeners);
    this._debuggerReset();
    this._project.dispose();
  }
}

const _scriptsSymbol = Symbol('symbol');
const _uiSourceCodeSymbol = Symbol('uiSourceCodeSymbol');
