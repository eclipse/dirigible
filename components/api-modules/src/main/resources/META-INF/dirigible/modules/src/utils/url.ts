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

const UrlFacade = Java.type("org.eclipse.dirigible.components.api.utils.UrlFacade");

export function encode(input, charset) {
	return UrlFacade.encode(input, charset);
};

export function decode(input, charset) {
	return UrlFacade.decode(input, charset);
};

export function escape(input) {
	return UrlFacade.escape(input);
};

export function escapePath(input) {
	return UrlFacade.escapePath(input);
};

export function escapeForm(input) {
	return UrlFacade.escapeForm(input);
};
