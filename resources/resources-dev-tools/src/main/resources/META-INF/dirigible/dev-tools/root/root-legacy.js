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
// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import * as RootModule from './root.js';

self.Root = self.Root || {};
Root = Root || {};

/** @constructor */
Root.Runtime = RootModule.Runtime.Runtime;

// This must be constructed after the query parameters have been parsed.
Root.Runtime.experiments = RootModule.Runtime.experiments;

Root.Runtime.queryParam = RootModule.Runtime.Runtime.queryParam;

/** @type {!RootModule.Runtime.Runtime} */
Root.runtime;

Root.Runtime.loadResourcePromise = RootModule.Runtime.loadResourcePromise;

/** @constructor */
Root.Runtime.Extension = RootModule.Runtime.Extension;

/** @constructor */
Root.Runtime.Module = RootModule.Runtime.Module;
