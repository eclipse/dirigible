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
			"name":"schema",
			"label":"Database Schema Model",
			"extension":"dsm",
			"data":'<schema><structures></structures><mxGraphModel><root></root></mxGraphModel></schema>'
	};
	return view;
};
