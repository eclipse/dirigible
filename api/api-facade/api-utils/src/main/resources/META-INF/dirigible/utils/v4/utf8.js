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
/**
 * Encode the input (text or byte array) as text
 */
 exports.encode = function(input) {
	return org.eclipse.dirigible.api.v3.utils.UTF8Facade.encode(input);
}

/**
 * Decode the input (text or byte array) as text
 */
exports.decode = function(input) {
	return org.eclipse.dirigible.api.v3.utils.UTF8Facade.decode(input);
}

/**
 * Decode the input byte array as text
 */
exports.bytesToString = function(bytes, offset, length) {
    return org.eclipse.dirigible.api.v3.utils.UTF8Facade.bytesToString(bytes, offset, length);
}