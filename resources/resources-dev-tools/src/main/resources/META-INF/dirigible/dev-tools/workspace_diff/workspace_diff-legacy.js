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

import * as WorkspaceDiffModule from './workspace_diff.js';

self.WorkspaceDiff = self.WorkspaceDiff || {};
WorkspaceDiff = WorkspaceDiff || {};

/** @constructor */
WorkspaceDiff.WorkspaceDiff = WorkspaceDiffModule.WorkspaceDiff.WorkspaceDiffImpl;

/** @constructor */
WorkspaceDiff.WorkspaceDiff.UISourceCodeDiff = WorkspaceDiffModule.WorkspaceDiff.UISourceCodeDiff;

WorkspaceDiff.WorkspaceDiff.UpdateTimeout = WorkspaceDiffModule.WorkspaceDiff.UpdateTimeout;

/** @enum {symbol} */
WorkspaceDiff.Events = WorkspaceDiffModule.WorkspaceDiff.Events;

WorkspaceDiff.workspaceDiff = WorkspaceDiffModule.WorkspaceDiff.workspaceDiff;

/** @constructor */
WorkspaceDiff.DiffUILocation = WorkspaceDiffModule.WorkspaceDiff.DiffUILocation;
