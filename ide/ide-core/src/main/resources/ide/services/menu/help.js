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
					"name":"Content",
					"link":"#",
					"order":"910",
					"onClick":"alert('Eclipse Dirigible 3.0 - Help Content')"
				},
				{
					"name":"About",
					"link":"#",
					"order":"990",
					"onClick":"alert('Eclipse Dirigible 3.0')"
				}
			]
		};
	return menu;
}