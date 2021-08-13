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

import * as SourceFrameModule from './source_frame.js';

self.SourceFrame = self.SourceFrame || {};
SourceFrame = SourceFrame || {};

/** @constructor */
SourceFrame.BinaryResourceViewFactory = SourceFrameModule.BinaryResourceViewFactory.BinaryResourceViewFactory;

/** @constructor */
SourceFrame.FontView = SourceFrameModule.FontView.FontView;

/** @constructor */
SourceFrame.ImageView = SourceFrameModule.ImageView.ImageView;

/** @constructor */
SourceFrame.JSONView = SourceFrameModule.JSONView.JSONView;

/** @constructor */
SourceFrame.ParsedJSON = SourceFrameModule.JSONView.ParsedJSON;

/** @constructor */
SourceFrame.PreviewFactory = SourceFrameModule.PreviewFactory.PreviewFactory;

/** @constructor */
SourceFrame.ResourceSourceFrame = SourceFrameModule.ResourceSourceFrame.ResourceSourceFrame;

/** @constructor */
SourceFrame.ResourceSourceFrame.SearchableContainer = SourceFrameModule.ResourceSourceFrame.SearchableContainer;

/** @constructor */
SourceFrame.SourceCodeDiff = SourceFrameModule.SourceCodeDiff.SourceCodeDiff;

/** @enum {symbol} */
SourceFrame.SourceCodeDiff.EditType = SourceFrameModule.SourceCodeDiff.EditType;

/** @constructor */
SourceFrame.SourceFrame = SourceFrameModule.SourceFrame.SourceFrameImpl;

/** @interface */
SourceFrame.LineDecorator = SourceFrameModule.SourceFrame.LineDecorator;

/** @constructor */
SourceFrame.SourcesTextEditor = SourceFrameModule.SourcesTextEditor.SourcesTextEditor;

SourceFrame.SourcesTextEditor.Events = SourceFrameModule.SourcesTextEditor.Events;
SourceFrame.SourcesTextEditor.lineNumbersGutterType = SourceFrameModule.SourcesTextEditor.lineNumbersGutterType;

/** @interface */
SourceFrame.SourcesTextEditorDelegate = SourceFrameModule.SourcesTextEditor.SourcesTextEditorDelegate;

SourceFrame.SourcesTextEditor.TokenHighlighter = SourceFrameModule.SourcesTextEditor.TokenHighlighter;

/** @constructor */
SourceFrame.XMLView = SourceFrameModule.XMLView.XMLView;

/** @constructor */
SourceFrame.XMLView.Node = SourceFrameModule.XMLView.XMLViewNode;
