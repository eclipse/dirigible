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
			"name":"File",
			"link":"#",
			"order":"100",
			"onClick":"alert('File has been clicked')",
			"items":[
				{
					"name":"Import",
					"link":"#",
					"order":"110",
					"onClick":"alert('Import has been clicked')",
					"items":[
						{
							"name":"Project",
							"link":"#",
							"order":"111",
							"onClick":"alert('Import Project has been clicked')"
						},
						{
							"name":"Snapshot",
							"link":"#",
							"order":"112",
							"onClick":"alert('Import Snapshot has been clicked')"
						}
					]
				},
				{
					"name":"Export",
					"link":"#",
					"order":"120",
					"onClick":"alert('Export has been clicked')",
					"items":[
						{
							"name":"Project",
							"link":"#",
							"order":"111",
							"onClick":"alert('Export Project has been clicked')"
						},
						{
							"name":"Snapshot",
							"link":"#",
							"order":"112",
							"onClick":"alert('Export Snapshot has been clicked')"
						}
					]
				},
				{
					"name":"Preferences",
					"link":"#",
					"order":"190",
					"onClick":"alert('Preferences has been clicked')"
				}
			]
		};
	return menu;
}