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
			"name":"Help",
			"link":"#",
			"order":"900",
			"onClick":"alert('Help has been clicked')",
			"items":[
				{
					"name":"Contents",
					"link":"#",
					"order":"910",
					"onClick":"window.open('http://www.dirigible.io/help/index.html', '_blank')"
				},
				{
					"name":"API",
					"link":"#",
					"order":"915",
					"onClick":"window.open('http://www.dirigible.io/api/index.html', '_blank')"
				},
				{
					"name":"Issues",
					"link":"#",
					"order":"915",
					"onClick":"window.open('https://github.com/eclipse/dirigible/issues', '_blank')"
				},
				{
					"name":"About",
					"link":"#",
					"order":"990",
					"onClick":"window.open('http://www.dirigible.io', '_blank')"
				}
			]
		};
	return menu;
}