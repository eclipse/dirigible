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

import * as ExtensionsModule from './extensions.js';

self.Extensions = self.Extensions || {};
Extensions = Extensions || {};

Extensions.extensionAPI = {};
ExtensionsModule.ExtensionAPI.defineCommonExtensionSymbols(Extensions.extensionAPI);

/** @constructor */
Extensions.ExtensionSidebarPane = ExtensionsModule.ExtensionPanel.ExtensionSidebarPane;

/** @constructor */
Extensions.ExtensionServer = ExtensionsModule.ExtensionServer.ExtensionServer;

/** @enum {symbol} */
Extensions.ExtensionServer.Events = ExtensionsModule.ExtensionServer.Events;

/** @constructor */
Extensions.ExtensionStatus = ExtensionsModule.ExtensionServer.ExtensionStatus;

/** @constructor */
Extensions.ExtensionTraceProvider = ExtensionsModule.ExtensionTraceProvider.ExtensionTraceProvider;

/** @interface */
Extensions.TracingSession = ExtensionsModule.ExtensionTraceProvider.TracingSession;

/** @type {!Extensions.ExtensionServer} */
self.Extensions.extensionServer;
