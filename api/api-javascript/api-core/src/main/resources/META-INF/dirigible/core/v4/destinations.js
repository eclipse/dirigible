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
 * API v4 Destinations
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.get = function(name) {
	return JSON.parse(org.eclipse.dirigible.api.v3.core.DestinationsFacade.get(name));
};

exports.set = function(name, destination) {
	org.eclipse.dirigible.api.v3.core.DestinationsFacade.set(name, JSON.stringify(destination));
};
