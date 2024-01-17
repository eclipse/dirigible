/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
import { extensions } from "@dirigible/extensions";
import { assert } from "@dirigible/test";
const assertEquals = assert.assertEquals;

const extensionPointsResult = extensions.getExtensionPoints();
assertEquals(extensionPointsResult[0], "test_extpoint1");

const extensionsResult = extensions.getExtensions('test_extpoint1');
assertEquals(extensionsResult[0], "/test_ext_module1");