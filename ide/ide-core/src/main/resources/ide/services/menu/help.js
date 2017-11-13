/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

exports.getMenu = function() {
	var menu = {
			"name":"Help",
			"link":"#",
			"order":"900",
			"onClick":"alert('Help has been clicked')",
			"items":[
				{
					"name":"Contents",
					"link":"#",
					"order":"910",
					"event":"open",
					"data": "http://www.dirigible.io/help/index.html"
				},
				{
					"name":"API",
					"link":"#",
					"order":"915",
					"event":"open",
					"data": "http://www.dirigible.io/api/index.html"
				},
				{
					"name":"Issues",
					"link":"#",
					"order":"920",
					"event":"open",
					"data": "https://github.com/eclipse/dirigible/issues"
				},
				{
					"name":"Wiki",
					"link":"#",
					"order":"920",
					"event":"open",
					"data": "https://github.com/eclipse/dirigible/wiki"
				},
				{
					"name":"About",
					"link":"#",
					"order":"990",
					"event":"open",
					"data": "http://www.dirigible.io"
				}
			]
		};
	return menu;
}
