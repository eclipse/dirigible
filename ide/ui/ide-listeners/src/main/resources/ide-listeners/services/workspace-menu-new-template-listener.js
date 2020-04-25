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
exports.getTemplate = function() {
	var view = {
			"name":"listener",
			"label":"Message Listener",
			"extension":"listener",
			"data":JSON.stringify(JSON.parse('{"name":"/myproject/mylistener","type":"Q","handler":"myproject/myhandler.js","description":"My Listener"}'), null, 2)
	};
	return view;
};
