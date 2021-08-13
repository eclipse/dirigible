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

import * as BindingsModule from './bindings.js';

self.Bindings = self.Bindings || {};
Bindings = Bindings || {};

/** @constructor */
Bindings.BlackboxManager = BindingsModule.BlackboxManager.BlackboxManager;

/** @constructor */
Bindings.BreakpointManager = BindingsModule.BreakpointManager.BreakpointManager;

/** @enum {symbol} */
Bindings.BreakpointManager.Events = BindingsModule.BreakpointManager.Events;

/** @constructor */
Bindings.BreakpointManager.Breakpoint = BindingsModule.BreakpointManager.Breakpoint;

Bindings.BreakpointManager.ModelBreakpoint = BindingsModule.BreakpointManager.ModelBreakpoint;

/** @constructor */
Bindings.CSSWorkspaceBinding = BindingsModule.CSSWorkspaceBinding.CSSWorkspaceBinding;

/** @interface */
Bindings.CSSWorkspaceBinding.SourceMapping = BindingsModule.CSSWorkspaceBinding.SourceMapping;

/** @constructor */
Bindings.CSSWorkspaceBinding.ModelInfo = BindingsModule.CSSWorkspaceBinding.ModelInfo;

/** @constructor */
Bindings.CompilerScriptMapping = BindingsModule.CompilerScriptMapping.CompilerScriptMapping;

/** @constructor */
Bindings.ContentProviderBasedProject = BindingsModule.ContentProviderBasedProject.ContentProviderBasedProject;

/** @constructor */
Bindings.DebuggerWorkspaceBinding = BindingsModule.DebuggerWorkspaceBinding.DebuggerWorkspaceBinding;

/** @interface */
Bindings.DebuggerSourceMapping = BindingsModule.DebuggerWorkspaceBinding.DebuggerSourceMapping;

/** @constructor */
Bindings.DefaultScriptMapping = BindingsModule.DefaultScriptMapping.DefaultScriptMapping;

/** @interface */
Bindings.ChunkedReader = BindingsModule.FileUtils.ChunkedReader;

/** @constructor */
Bindings.ChunkedFileReader = BindingsModule.FileUtils.ChunkedFileReader;

/** @constructor */
Bindings.FileOutputStream = BindingsModule.FileUtils.FileOutputStream;

/** @interface */
Bindings.LiveLocation = BindingsModule.LiveLocation.LiveLocation;

/** @constructor */
Bindings.LiveLocationPool = BindingsModule.LiveLocation.LiveLocationPool;

/** @constructor */
Bindings.NetworkProjectManager = BindingsModule.NetworkProject.NetworkProjectManager;

Bindings.NetworkProjectManager.Events = BindingsModule.NetworkProject.Events;

/** @constructor */
Bindings.NetworkProject = BindingsModule.NetworkProject.NetworkProject;

/** @constructor */
Bindings.PresentationConsoleMessageManager =
    BindingsModule.PresentationConsoleMessageHelper.PresentationConsoleMessageManager;

/** @constructor */
Bindings.PresentationConsoleMessage = BindingsModule.PresentationConsoleMessageHelper.PresentationConsoleMessage;

/** @constructor */
Bindings.ResourceMapping = BindingsModule.ResourceMapping.ResourceMapping;

Bindings.ResourceMapping._symbol = BindingsModule.ResourceMapping.symbol;
Bindings.ResourceMapping._offsetSymbol = BindingsModule.ResourceMapping.offsetSymbol;

/** @constructor */
Bindings.ResourceScriptFile = BindingsModule.ResourceScriptMapping.ResourceScriptFile;

Bindings.resourceForURL = BindingsModule.ResourceUtils.resourceForURL;
Bindings.displayNameForURL = BindingsModule.ResourceUtils.displayNameForURL;

/** @constructor */
Bindings.SASSSourceMapping = BindingsModule.SASSSourceMapping.SASSSourceMapping;

/** @constructor */
Bindings.StylesSourceMapping = BindingsModule.StylesSourceMapping.StylesSourceMapping;

/** @constructor */
Bindings.StyleFile = BindingsModule.StylesSourceMapping.StyleFile;

/** @constructor */
Bindings.TempFile = BindingsModule.TempFile.TempFile;

/** @constructor */
Bindings.TempFileBackingStorage = BindingsModule.TempFile.TempFileBackingStorage;

/** @type {!BindingsModule.BlackboxManager.BlackboxManager} */
self.Bindings.blackboxManager;

/** @type {!BindingsModule.BreakpointManager.BreakpointManager} */
self.Bindings.breakpointManager;

/**
 * @type {!BindingsModule.CSSWorkspaceBinding.CSSWorkspaceBinding}
 */
self.Bindings.cssWorkspaceBinding;

/**
 * @type {!BindingsModule.DebuggerWorkspaceBinding.DebuggerWorkspaceBinding}
 */
self.Bindings.debuggerWorkspaceBinding;
