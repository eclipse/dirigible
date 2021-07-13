/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Console
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.log = function(message) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.log(stringify(message));
};

exports.error = function(message) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.error(stringify(message));
};

exports.info = function(message) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.info(stringify(message));
};

exports.warn = function(message) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.warn(stringify(message));
};

exports.debug = function(message) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.debug(stringify(message));
};

exports.trace = function(message) {
	let traceMessage = new Error(stringify(`${message}`)).stack;
	if (traceMessage) {
		traceMessage = traceMessage.substring("Error: ".length, traceMessage.length);
	}
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.trace(traceMessage);
};

function stringify(message) {
	if (typeof message === 'object' && message !== null && message.class === undefined) {
		return JSON.stringify(message);
	}
	return "" + message;
}
