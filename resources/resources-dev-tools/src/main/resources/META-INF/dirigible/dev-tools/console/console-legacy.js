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

import * as ConsoleModule from './console.js';

self.Console = self.Console || {};
Console = Console || {};

/**
 * @constructor
 */
Console.ConsoleFilter = ConsoleModule.ConsoleFilter.ConsoleFilter;

/**
 * @constructor
 */
Console.ConsolePanel = ConsoleModule.ConsolePanel.ConsolePanel;

/**
 * @constructor
 */
Console.ConsolePanel.WrapperView = ConsoleModule.ConsolePanel.WrapperView;

/**
 * @implements {Common.Revealer}
 */
Console.ConsolePanel.ConsoleRevealer = ConsoleModule.ConsolePanel.ConsoleRevealer;

/**
 * @constructor
 */
Console.ConsolePin = ConsoleModule.ConsolePinPane.ConsolePin;

/**
 * @constructor
 */
Console.ConsolePrompt = ConsoleModule.ConsolePrompt.ConsolePrompt;

/**
 * @constructor
 */
Console.ConsoleSidebar = ConsoleModule.ConsoleSidebar.ConsoleSidebar;

/**
 * @constructor
 */
Console.ConsoleView = ConsoleModule.ConsoleView.ConsoleView;

/** @constructor */
Console.ConsoleViewFilter = ConsoleModule.ConsoleView.ConsoleViewFilter;

/**
 * @implements {UI.ActionDelegate}
 */
Console.ConsoleView.ActionDelegate = ConsoleModule.ConsoleView.ActionDelegate;

/**
 * @constructor
 */
Console.ConsoleGroup = ConsoleModule.ConsoleView.ConsoleGroup;

/**
 * @implements {Console.ConsoleViewportElement}
 * @unrestricted
 * @constructor
 */
Console.ConsoleViewMessage = ConsoleModule.ConsoleViewMessage.ConsoleViewMessage;

/**
 * @constructor
 */
Console.ConsoleGroupViewMessage = ConsoleModule.ConsoleViewMessage.ConsoleGroupViewMessage;

/** @suppress {accessControls} */
Console.ConsoleViewMessage._MaxTokenizableStringLength = ConsoleModule.ConsoleViewMessage._MaxTokenizableStringLength;
/** @suppress {accessControls} */
Console.ConsoleViewMessage._LongStringVisibleLength = ConsoleModule.ConsoleViewMessage._LongStringVisibleLength;

/**
 * @constructor
 */
Console.ConsoleViewport = ConsoleModule.ConsoleViewport.ConsoleViewport;

/**
 * @interface
 */
Console.ConsoleViewportElement = ConsoleModule.ConsoleViewport.ConsoleViewportElement;
