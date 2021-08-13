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
import * as arrays from '../utils/arrays.js';
// As defined in https://www.w3.org/TR/html5/syntax.html#void-elements
export var VOID_ELEMENTS = ['area', 'base', 'br', 'col', 'embed', 'hr', 'img', 'input', 'keygen', 'link', 'menuitem', 'meta', 'param', 'source', 'track', 'wbr'];
export function isVoidElement(e) {
    return !!e && arrays.binarySearch(VOID_ELEMENTS, e.toLowerCase(), function (s1, s2) { return s1.localeCompare(s2); }) >= 0;
}
