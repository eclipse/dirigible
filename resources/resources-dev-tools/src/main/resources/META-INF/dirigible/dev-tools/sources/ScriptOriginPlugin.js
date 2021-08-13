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
// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as Bindings from '../bindings/bindings.js';
import * as Components from '../components/components.js';
import * as SDK from '../sdk/sdk.js';                            // eslint-disable-line no-unused-vars
import * as SourceFrame from '../source_frame/source_frame.js';  // eslint-disable-line no-unused-vars
import * as UI from '../ui/ui.js';
import * as Workspace from '../workspace/workspace.js';  // eslint-disable-line no-unused-vars

import {Plugin} from './Plugin.js';

export class ScriptOriginPlugin extends Plugin {
  /**
   * @param {!SourceFrame.SourcesTextEditor.SourcesTextEditor} textEditor
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   */
  constructor(textEditor, uiSourceCode) {
    super();
    this._textEditor = textEditor;
    this._uiSourceCode = uiSourceCode;
  }

  /**
   * @override
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {boolean}
   */
  static accepts(uiSourceCode) {
    return uiSourceCode.contentType().hasScripts() || !!ScriptOriginPlugin._script(uiSourceCode);
  }

  /**
   * @override
   * @return {!Promise<!Array<!UI.Toolbar.ToolbarItem>>}
   */
  async rightToolbarItems() {
    const originURL = Bindings.CompilerScriptMapping.CompilerScriptMapping.uiSourceCodeOrigin(this._uiSourceCode);
    if (originURL) {
      const item =
          UI.UIUtils.formatLocalized('(source mapped from %s)', [Components.Linkifier.Linkifier.linkifyURL(originURL)]);
      return [new UI.Toolbar.ToolbarItem(item)];
    }

    // Handle anonymous scripts with an originStackTrace.
    const script = await ScriptOriginPlugin._script(this._uiSourceCode);
    if (!script || !script.originStackTrace) {
      return [];
    }
    const link = linkifier.linkifyStackTraceTopFrame(script.debuggerModel.target(), script.originStackTrace);
    return [new UI.Toolbar.ToolbarItem(link)];
  }

  /**
   * @param {!Workspace.UISourceCode.UISourceCode} uiSourceCode
   * @return {!Promise<?SDK.Script.Script>}
   */
  static async _script(uiSourceCode) {
    const locations =
        await Bindings.DebuggerWorkspaceBinding.DebuggerWorkspaceBinding.instance().uiLocationToRawLocations(
            uiSourceCode, 0, 0);
    for (const location of locations) {
      const script = location.script();
      if (script && script.originStackTrace) {
        return script;
      }
    }
    return null;
  }
}

export const linkifier = new Components.Linkifier.Linkifier();
