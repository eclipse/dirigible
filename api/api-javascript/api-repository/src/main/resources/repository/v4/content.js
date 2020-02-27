/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */

var bytes = require("io/v4/bytes");

exports.getContent = function(path) {
	var nativeContent = org.eclipse.dirigible.api.v3.repository.ContentFacade.getContent(path);
	return bytes.toJavaScriptBytes(nativeContent);
};

exports.getText = function(path) {
	return org.eclipse.dirigible.api.v3.repository.ContentFacade.getText(path);
};