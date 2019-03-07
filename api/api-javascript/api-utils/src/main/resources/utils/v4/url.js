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
