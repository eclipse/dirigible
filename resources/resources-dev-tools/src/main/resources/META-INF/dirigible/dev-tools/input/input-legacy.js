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

import * as InputModule from './input.js';

self.Input = self.Input || {};
Input = Input || {};

/** @constructor */
Input.InputModel = InputModule.InputModel.InputModel;

/**
 * @implements {SDK.SDKModel.SDKModelObserver<!Input.InputModel>}
 * @constructor
 * @unrestricted
 */
Input.InputTimeline = InputModule.InputTimeline.InputTimeline;

/** @enum {symbol} */
Input.InputTimeline.State = InputModule.InputTimeline.State;

/**
 * @constructor
 */
Input.InputTimeline.TracingClient = InputModule.InputTimeline.TracingClient;

/**
 * @constructor
 */
Input.InputTimeline.ActionDelegate = InputModule.InputTimeline.ActionDelegate;
