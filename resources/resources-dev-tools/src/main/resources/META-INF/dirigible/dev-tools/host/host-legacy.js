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

import * as HostModule from './host.js';

self.Host = self.Host || {};
Host = Host || {};

/** @type {!HostModule.InspectorFrontendHost.InspectorFrontendHostStub} */
Host.InspectorFrontendHost = HostModule.InspectorFrontendHost.InspectorFrontendHostInstance;

Host.isUnderTest = HostModule.InspectorFrontendHost.isUnderTest;

Host.InspectorFrontendHostAPI = {};

Host.InspectorFrontendHostAPI.Events = HostModule.InspectorFrontendHostAPI.Events;

Host.platform = HostModule.Platform.platform;
Host.isWin = HostModule.Platform.isWin;
Host.isMac = HostModule.Platform.isMac;
Host.isCustomDevtoolsFrontend = HostModule.Platform.isCustomDevtoolsFrontend;
Host.fontFamily = HostModule.Platform.fontFamily;

Host.ResourceLoader = HostModule.ResourceLoader.ResourceLoader;

/**
 * @param {string} url
 * @param {?Object.<string, string>} headers
 * @param {function(boolean, !Object.<string, string>, string, !HostModule.ResourceLoader.LoadErrorDescription)} callback
 */
Host.ResourceLoader.load = HostModule.ResourceLoader.load;

Host.ResourceLoader.loadAsStream = HostModule.ResourceLoader.loadAsStream;

/** @constructor */
Host.UserMetrics = HostModule.UserMetrics.UserMetrics;

Host.UserMetrics._PanelCodes = HostModule.UserMetrics.PanelCodes;

/** @enum {number} */
Host.UserMetrics.Action = HostModule.UserMetrics.Action;

/** @type {!Host.UserMetrics} */
Host.userMetrics = HostModule.userMetrics;
