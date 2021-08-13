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

import * as TextUtils from '../text_utils/text_utils.js';  // eslint-disable-line no-unused-vars

import * as CoverageModule from './coverage.js';

self.Coverage = self.Coverage || {};
Coverage = Coverage || {};

/**
 * @constructor
 */
Coverage.CoverageDecorationManager = CoverageModule.CoverageDecorationManager.CoverageDecorationManager;

/**
 * @constructor
 */
Coverage.CoverageListView = CoverageModule.CoverageListView.CoverageListView;

/**
 * @constructor
 */
Coverage.CoverageModel = CoverageModule.CoverageModel.CoverageModel;

/** @enum {symbol} */
Coverage.CoverageModel.Events = CoverageModule.CoverageModel.Events;

/**
 * @enum {number}
 */
Coverage.CoverageType = CoverageModule.CoverageModel.CoverageType;

/**
 * @constructor
 */
Coverage.URLCoverageInfo = CoverageModule.CoverageModel.URLCoverageInfo;

/**
 * @constructor
 */
Coverage.CoverageInfo = CoverageModule.CoverageModel.CoverageInfo;

/**
 * @constructor
 */
Coverage.CoverageView = CoverageModule.CoverageView.CoverageView;

/**
 * @constructor
 */
Coverage.CoverageView.LineDecorator = CoverageModule.CoverageView.LineDecorator;

/**
 * @constructor
 */
Coverage.CoverageView.ActionDelegate = CoverageModule.CoverageView.ActionDelegate;
