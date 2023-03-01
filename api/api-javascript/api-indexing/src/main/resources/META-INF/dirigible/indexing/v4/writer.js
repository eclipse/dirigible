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
/**
 * API v4 Writer
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.add = function(index, location, contents, lastModified, parameters) {
	if (!lastModified) {
		lastModified = new Date();
	}
	let map = "{}";
	if (parameters) {
		map = JSON.stringify(parameters);
	}
	org.eclipse.dirigible.api.v3.indexing.IndexingFacade.add(index, location, contents, '' + lastModified.getTime(), map);
};
