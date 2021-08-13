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

import * as TextEditorModule from './text_editor.js';

self.TextEditor = self.TextEditor || {};
TextEditor = TextEditor || {};

/** @constructor */
TextEditor.CodeMirrorTextEditor = TextEditorModule.CodeMirrorTextEditor.CodeMirrorTextEditor;

/** @constructor */
TextEditor.CodeMirrorTextEditor.SelectNextOccurrenceController =
    TextEditorModule.CodeMirrorTextEditor.SelectNextOccurrenceController;

/** @interface */
TextEditor.TextEditorPositionHandle = TextEditorModule.CodeMirrorTextEditor.TextEditorPositionHandle;

TextEditor.CodeMirrorTextEditor._loadedMimeModeExtensions =
    TextEditorModule.CodeMirrorTextEditor.loadedMimeModeExtensions;

/** @constructor */
TextEditor.CodeMirrorPositionHandle = TextEditorModule.CodeMirrorTextEditor.CodeMirrorPositionHandle;

/** @interface */
TextEditor.CodeMirrorMimeMode = TextEditorModule.CodeMirrorTextEditor.CodeMirrorMimeMode;

/** @constructor */
TextEditor.TextEditorBookMark = TextEditorModule.CodeMirrorTextEditor.TextEditorBookMark;

/** @constructor */
TextEditor.CodeMirrorTextEditorFactory = TextEditorModule.CodeMirrorTextEditor.CodeMirrorTextEditorFactory;

TextEditor.CodeMirrorUtils = {};

TextEditor.CodeMirrorUtils.toPos = TextEditorModule.CodeMirrorUtils.toPos;
TextEditor.CodeMirrorUtils.toRange = TextEditorModule.CodeMirrorUtils.toRange;
TextEditor.CodeMirrorUtils.changeObjectToEditOperation = TextEditorModule.CodeMirrorUtils.changeObjectToEditOperation;
TextEditor.CodeMirrorUtils.pullLines = TextEditorModule.CodeMirrorUtils.pullLines;

/** @constructor */
TextEditor.CodeMirrorUtils.TokenizerFactory = TextEditorModule.CodeMirrorUtils.TokenizerFactory;

/** @constructor */
TextEditor.TextEditorAutocompleteController =
    TextEditorModule.TextEditorAutocompleteController.TextEditorAutocompleteController;
