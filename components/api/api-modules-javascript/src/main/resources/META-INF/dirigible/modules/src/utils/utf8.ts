/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */

const UTF8Facade = Java.type("org.eclipse.dirigible.components.api.utils.UTF8Facade");

/**
 * Encode the input (text or byte array) as text
 */
 export function encode(input) {
	return UTF8Facade.encode(input);
}

/**
 * Decode the input (text or byte array) as text
 */
export function decode(input) {
	return UTF8Facade.decode(input);
}

/**
 * Decode the input byte array as text
 */
export function bytesToString(bytes, offset, length) {
    return UTF8Facade.bytesToString(bytes, offset, length);
}