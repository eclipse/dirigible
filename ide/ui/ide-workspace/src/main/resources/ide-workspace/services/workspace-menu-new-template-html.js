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
exports.getTemplate = function() {
	var view = {
			"name":"html",
			"label":"HTML5 Page",
			"extension":"html",
			"data":'<!DOCTYPE html>\n<head>\n</head>\n<body>\n</body>\n</html>'
	};
	return view;
};
