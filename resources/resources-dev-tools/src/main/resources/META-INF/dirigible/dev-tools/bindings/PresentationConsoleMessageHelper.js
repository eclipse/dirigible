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
import * as Workspace from '../workspace/workspace.js';

import {DebuggerWorkspaceBinding} from './DebuggerWorkspaceBinding.js';
import {LiveLocation, LiveLocationPool} from './LiveLocation.js';  // eslint-disable-line no-unused-vars

/**
 * @implements {SDK.SDKModel.SDKModelObserver<!SDK.DebuggerModel.DebuggerModel>}
 */
export class PresentationConsoleMessageManager {
  constructor() {
    SDK.SDKModel.TargetManager.instance().observeModels(SDK.DebuggerModel.DebuggerModel, this);

    SDK.ConsoleModel.ConsoleModel.instance().addEventListener(
        SDK.ConsoleModel.Events.ConsoleCleared, this._consoleCleared, this);
    SDK.ConsoleModel.ConsoleModel.instance().addEventListener(
        SDK.ConsoleModel.Events.MessageAdded,
        event => this._consoleMessageAdded(/** @type {!SDK.ConsoleModel.ConsoleMessage} */ (event.data)));
    SDK.ConsoleModel.ConsoleModel.instance().messages().forEach(this._consoleMessageAdded, this);
  }

  /**
   * @override
   * @param {!SDK.DebuggerModel.DebuggerModel} debuggerModel
   */
  modelAdded(debuggerModel) {
    debuggerModel[PresentationConsoleMessageManager._symbol] = new PresentationConsoleMessageHelper(debuggerModel);
  }

  /**
   * @override
   * @param {!SDK.DebuggerModel.DebuggerModel} debuggerModel
   */
  modelRemoved(debuggerModel) {
    debuggerModel[PresentationConsoleMessageManager._symbol]._consoleCleared();
  }

  /**
   * @param {!SDK.ConsoleModel.ConsoleMessage} message
   */
  _consoleMessageAdded(message) {
    if (!message.isErrorOrWarning() || !message.runtimeModel() ||
        message.source === SDK.ConsoleModel.MessageSource.Violation) {
      return;
    }
    const debuggerModel = message.runtimeModel().debuggerModel();
    debuggerModel[PresentationConsoleMessageManager._symbol]._consoleMessageAdded(message);
  }

  _consoleCleared() {
    for (const debuggerModel of SDK.SDKModel.TargetManager.instance().models(SDK.DebuggerModel.DebuggerModel)) {
      debuggerModel[PresentationConsoleMessageManager._symbol]._consoleCleared();
    }
  }
}

PresentationConsoleMessageManager._symbol = Symbol('PresentationConsoleMessageHelper');

export class PresentationConsoleMessageHelper {
  /**
   * @param {!SDK.DebuggerModel.DebuggerModel} debuggerModel
   */
  constructor(debuggerModel) {
    this._debuggerModel = debuggerModel;

    /** @type {!Object.<string, !Array.<!SDK.ConsoleModel.ConsoleMessage>>} */
    this._pendingConsoleMessages = {};

    /** @type {!Array.<!PresentationConsoleMessage>} */
    this._presentationConsoleMessages = [];

    // TODO(dgozman): setImmediate because we race with DebuggerWorkspaceBinding on ParsedScriptSource event delivery.
    debuggerModel.addEventListener(SDK.DebuggerModel.Events.ParsedScriptSource, event => {
      setImmediate(this._parsedScriptSource.bind(this, event));
    });
    debuggerModel.addEventListener(SDK.DebuggerModel.Events.GlobalObjectCleared, this._debuggerReset, this);

    this._locationPool = new LiveLocationPool();
  }

  /**
   * @param {!SDK.ConsoleModel.ConsoleMessage} message
   */
  _consoleMessageAdded(message) {
    const rawLocation = this._rawLocation(message);
    if (rawLocation) {
      this._addConsoleMessageToScript(message, rawLocation);
    } else {
      this._addPendingConsoleMessage(message);
    }
  }

  /**
   * @param {!SDK.ConsoleModel.ConsoleMessage} message
   * @return {?SDK.DebuggerModel.Location}
   */
  _rawLocation(message) {
    if (message.scriptId) {
      return this._debuggerModel.createRawLocationByScriptId(message.scriptId, message.line, message.column);
    }
    const callFrame = message.stackTrace && message.stackTrace.callFrames ? message.stackTrace.callFrames[0] : null;
    if (callFrame) {
      return this._debuggerModel.createRawLocationByScriptId(
          callFrame.scriptId, callFrame.lineNumber, callFrame.columnNumber);
    }
    if (message.url) {
      return this._debuggerModel.createRawLocationByURL(message.url, message.line, message.column);
    }
    return null;
  }

  /**
   * @param {!SDK.ConsoleModel.ConsoleMessage} message
   * @param {!SDK.DebuggerModel.Location} rawLocation
   */
  _addConsoleMessageToScript(message, rawLocation) {
    this._presentationConsoleMessages.push(new PresentationConsoleMessage(message, rawLocation, this._locationPool));
  }

  /**
   * @param {!SDK.ConsoleModel.ConsoleMessage} message
   */
  _addPendingConsoleMessage(message) {
    if (!message.url) {
      return;
    }
    if (!this._pendingConsoleMessages[message.url]) {
      this._pendingConsoleMessages[message.url] = [];
    }
    this._pendingConsoleMessages[message.url].push(message);
  }

  /**
   * @param {!Common.EventTarget.EventTargetEvent} event
   */
  _parsedScriptSource(event) {
    const script = /** @type {!SDK.Script.Script} */ (event.data);

    const messages = this._pendingConsoleMessages[script.sourceURL];
    if (!messages) {
      return;
    }

    const pendingMessages = [];
    for (let i = 0; i < messages.length; i++) {
      const message = messages[i];
      const rawLocation = this._rawLocation(message);
      if (!rawLocation) {
        continue;
      }
      if (script.scriptId === rawLocation.scriptId) {
        this._addConsoleMessageToScript(message, rawLocation);
      } else {
        pendingMessages.push(message);
      }
    }

    if (pendingMessages.length) {
      this._pendingConsoleMessages[script.sourceURL] = pendingMessages;
    } else {
      delete this._pendingConsoleMessages[script.sourceURL];
    }
  }

  _consoleCleared() {
    this._pendingConsoleMessages = {};
    this._debuggerReset();
  }

  _debuggerReset() {
    for (const message of this._presentationConsoleMessages) {
      message.dispose();
    }
    this._presentationConsoleMessages = [];
    this._locationPool.disposeAll();
  }
}

/**
 * @unrestricted
 */
export class PresentationConsoleMessage {
  /**
   * @param {!SDK.ConsoleModel.ConsoleMessage} message
   * @param {!SDK.DebuggerModel.Location} rawLocation
   * @param {!LiveLocationPool} locationPool
   */
  constructor(message, rawLocation, locationPool) {
    this._text = message.messageText;
    this._level = message.level === SDK.ConsoleModel.MessageLevel.Error ? Workspace.UISourceCode.Message.Level.Error :
                                                                          Workspace.UISourceCode.Message.Level.Warning;
    DebuggerWorkspaceBinding.instance().createLiveLocation(rawLocation, this._updateLocation.bind(this), locationPool);
  }

  /**
   * @param {!LiveLocation} liveLocation
   */
  async _updateLocation(liveLocation) {
    if (this._uiMessage) {
      this._uiMessage.remove();
    }
    const uiLocation = await liveLocation.uiLocation();
    if (!uiLocation) {
      return;
    }
    this._uiMessage =
        uiLocation.uiSourceCode.addLineMessage(this._level, this._text, uiLocation.lineNumber, uiLocation.columnNumber);
  }

  dispose() {
    if (this._uiMessage) {
      this._uiMessage.remove();
    }
  }
}
