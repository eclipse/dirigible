/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

/**
 * API v4 Console
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.log = function(message) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.log(message);
};

exports.error = function(message, args) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.error(message, args);
};

exports.info = function(message, args) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.info(message, args);
};

exports.warn = function(message, args) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.warn(message, args);
};

exports.debug = function(message, args) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.debug(message, args);
};

exports.trace = function(message, args) {
	org.eclipse.dirigible.api.v3.core.ConsoleFacade.trace(message, args);
};
