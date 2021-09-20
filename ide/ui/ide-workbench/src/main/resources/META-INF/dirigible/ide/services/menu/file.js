/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getMenu = function () {
	return {
		"name": "File",
		"link": "#",
		"order": "100",
		"items": [
			{
				"name": "New",
				"link": "#",
				"order": "101",
				"event": "",
				"data": "",
				"items": [
					{
						"name": "Project",
						"link": "#",
						"order": "101",
						"event": "workspace.create.project",
						"data": ""
					},
					{
						"name": "Linked Project",
						"link": "#",
						"order": "102",
						"event": "workspace.link.project",
						"data": ""
					},
					{
						"name": "Workspace",
						"link": "#",
						"order": "103",
						"event": "workspace.create.workspace",
						"data": ""
					}
				]
			},
			{
				"name": "Publish All",
				"link": "#",
				"order": "102",
				"event": "workspace.publish.all",
				"data": ""
			},
			{
				"name": "Export All",
				"link": "#",
				"order": "103",
				"event": "workspace.export.all",
				"data": ""
			},
			{
				"name": "Save All",
				"link": "#",
				"order": "105",
				"event": "workbench.editor.save",
				"data": "",
				"divider": true
			},
			{
				"name": "Exit",
				"link": "/logout",
				"order": "199",
				"event": "workbench.editor.save",
				"data": ""
			}
		]
	};
}