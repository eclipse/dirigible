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

import * as AnimationModule from './animation.js';

self.Animation = self.Animation || {};
Animation = Animation || {};

/**
 * @constructor
 */
Animation.AnimationModel = AnimationModule.AnimationModel.AnimationModel;

/** @enum {symbol} */
Animation.AnimationModel.Events = AnimationModule.AnimationModel.Events;

/**
 * @constructor
 */
Animation.AnimationModel.Animation = AnimationModule.AnimationModel.AnimationImpl;

/**
 * @constructor
 */
Animation.AnimationModel.AnimationGroup = AnimationModule.AnimationModel.AnimationGroup;

/**
 * @constructor
 */
Animation.AnimationModel.ScreenshotCapture = AnimationModule.AnimationModel.ScreenshotCapture;

/**
 * @implements {SDK.SDKModelObserver<!Animation.AnimationModel>}
 * @constructor
 * @unrestricted
 */
Animation.AnimationTimeline = AnimationModule.AnimationTimeline.AnimationTimeline;

/**
 * @constructor
 */
Animation.AnimationUI = AnimationModule.AnimationUI.AnimationUI;

/**
 * @enum {string}
 */
Animation.AnimationUI.Events = AnimationModule.AnimationUI.Events;
