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
exports.getEditor = function() {
	var editor = {
			"id":"access",
			"name":"Access",
			"factory":"frame",
			"region":"center-top",
			"label":"Access",
			"link":"../ide-security/editors/access/editor.html",
			"contentTypes":["application/json+access"]
	};
	return editor;
};
