/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.encode = function(input, charset) {
	var output = org.eclipse.dirigible.api.v3.utils.UrlFacade.encode(input, charset);
	return output;
};

exports.decode = function(input, charset) {
	var output = org.eclipse.dirigible.api.v3.utils.UrlFacade.decode(input, charset);
	return output;
};

exports.escape = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.UrlFacade.escape(input);
	return output;
};

exports.escapePath = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.UrlFacade.escapePath(input);
	return output;
};

exports.escapeForm = function(input) {
	var output = org.eclipse.dirigible.api.v3.utils.UrlFacade.escapeForm(input);
	return output;
};
