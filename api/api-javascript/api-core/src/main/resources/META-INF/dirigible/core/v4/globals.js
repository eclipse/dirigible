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
 * API v4 Globals
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.get = function(name) {
	return org.eclipse.dirigible.api.v3.core.GlobalsFacade.get(name);
};

exports.set = function(name, value) {
	org.eclipse.dirigible.api.v3.core.GlobalsFacade.set(name, value);
};

exports.list = function() {
	return org.eclipse.dirigible.api.v3.core.GlobalsFacade.list();
};
