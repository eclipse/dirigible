/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

exports.getEditor = function() {
	var editor = {
			"id":"ace",
			"name":"ACE",
			"factory":"frame",
			"region":"center-top",
			"label":"ACE",
			"link":"../ide-ace/editor.html",
			"contentTypes":["application/javascript","application/json","text/plain","text/html"]
	};
	return editor;
}
