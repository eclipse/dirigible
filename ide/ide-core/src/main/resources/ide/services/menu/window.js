/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

exports.getMenu = function() {
	var menu = {
			"name":"Window",
			"link":"#",
			"order":"800",
			"onClick":"alert('Window has been clicked')",
			"items":[
				{
					"name":"Open Perspective",
					"link":"#",
					"order":"810",
					"onClick":"alert('Open Perspective has been clicked')"
				},
				{
					"name":"Show View",
					"link":"#",
					"order":"820",
					"onClick":"alert('Show View has been clicked')"
				}
			]
		};
	return menu;
}