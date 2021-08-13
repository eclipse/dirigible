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

import * as ChangesModule from './changes.js';

self.Changes = self.Changes || {};
Changes = Changes || {};

Changes.ChangesHighlighter = ChangesModule.ChangesHighlighter.ChangesHighlighter;

/**
 * @constructor
 */
Changes.ChangesSidebar = ChangesModule.ChangesSidebar.ChangesSidebar;

/**
 * @constructor
 */
Changes.ChangesView = ChangesModule.ChangesView.ChangesView;

/** @enum {string} */
Changes.ChangesView.RowType = ChangesModule.ChangesView.RowType;

/**
 * @implements {Common.Revealer}
 */
Changes.ChangesView.DiffUILocationRevealer = ChangesModule.ChangesView.DiffUILocationRevealer;
