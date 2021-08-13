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
import { EditorOptions } from './common/config/editorOptions.js';
import { createMonacoBaseAPI } from './common/standalone/standaloneBase.js';
import { createMonacoEditorAPI } from './standalone/browser/standaloneEditor.js';
import { createMonacoLanguagesAPI } from './standalone/browser/standaloneLanguages.js';
var global = self;
// Set defaults for standalone editor
EditorOptions.wrappingIndent.defaultValue = 0 /* None */;
EditorOptions.glyphMargin.defaultValue = false;
EditorOptions.autoIndent.defaultValue = 3 /* Advanced */;
EditorOptions.overviewRulerLanes.defaultValue = 2;
var api = createMonacoBaseAPI();
api.editor = createMonacoEditorAPI();
api.languages = createMonacoLanguagesAPI();
export var CancellationTokenSource = api.CancellationTokenSource;
export var Emitter = api.Emitter;
export var KeyCode = api.KeyCode;
export var KeyMod = api.KeyMod;
export var Position = api.Position;
export var Range = api.Range;
export var Selection = api.Selection;
export var SelectionDirection = api.SelectionDirection;
export var MarkerSeverity = api.MarkerSeverity;
export var MarkerTag = api.MarkerTag;
export var Uri = api.Uri;
export var Token = api.Token;
export var editor = api.editor;
export var languages = api.languages;
global.monaco = api;
if (typeof global.require !== 'undefined' && typeof global.require.config === 'function') {
    global.require.config({
        ignoreDuplicateModules: [
            'vscode-languageserver-types',
            'vscode-languageserver-types/main',
            'vscode-nls',
            'vscode-nls/vscode-nls',
            'jsonc-parser',
            'jsonc-parser/main',
            'vscode-uri',
            'vscode-uri/index',
            'vs/basic-languages/typescript/typescript'
        ]
    });
}
