/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

exports.getWriter = function() {
	var writer = require('messaging/v3/writer');
	return writer;
};

exports.getSearcher = function() {
	var searcher = require('messaging/v3/searcher');
	return searcher;
};
