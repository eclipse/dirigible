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

import * as MainModule from './main.js';

self.Main = self.Main || {};
Main = Main || {};

/**
 * @constructor
 */
Main.ExecutionContextSelector = MainModule.ExecutionContextSelector.ExecutionContextSelector;

/**
 * @constructor
 */
Main.Main = MainModule.MainImpl.MainImpl;

/**
 * @constructor
 */
Main.Main.ZoomActionDelegate = MainModule.MainImpl.ZoomActionDelegate;

/**
 * @constructor
 */
Main.Main.SearchActionDelegate = MainModule.MainImpl.SearchActionDelegate;

/**
 * @constructor
 */
Main.Main.MainMenuItem = MainModule.MainImpl.MainMenuItem;

/**
 * @constructor
 */
Main.Main.SettingsButtonProvider = MainModule.MainImpl.SettingsButtonProvider;

/**
 * @constructor
 */
Main.ReloadActionDelegate = MainModule.MainImpl.ReloadActionDelegate;

/**
 * @constructor
 */
Main.SimpleApp = MainModule.SimpleApp.SimpleApp;

/**
 * @constructor
 */
Main.SimpleAppProvider = MainModule.SimpleApp.SimpleAppProvider;
