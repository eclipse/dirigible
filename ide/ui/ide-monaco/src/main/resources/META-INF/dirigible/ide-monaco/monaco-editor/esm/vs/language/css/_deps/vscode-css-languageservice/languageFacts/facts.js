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
'use strict';
import * as webCustomData from '../data/webCustomData.js';
import { CSSDataManager } from './dataManager.js';
import { CSSDataProvider } from './dataProvider.js';
export * from './entry.js';
export * from './colors.js';
export * from './builtinData.js';
export * from './dataProvider.js';
export * from './dataManager.js';
export var cssDataManager = new CSSDataManager([
    new CSSDataProvider(webCustomData.cssData)
]);
