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
			"name":"access",
			"label":"Access Constraints",
			"extension":"access",
			"data":JSON.stringify(JSON.parse('{"constraints":[{"path":"/myproject/myfolder/myservice.js","method":"GET","roles":["administrator","operator"]}]}'), null, 2)
	};
	return view;
};
