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
import { createDecorator } from '../../instantiation/common/instantiation.js';
import { RawContextKey } from '../../contextkey/common/contextkey.js';
export var IAccessibilityService = createDecorator('accessibilityService');
export var CONTEXT_ACCESSIBILITY_MODE_ENABLED = new RawContextKey('accessibilityModeEnabled', false);
