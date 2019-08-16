/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
