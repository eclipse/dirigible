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
const defaultMenuItems = [
	{
		name: "Help Portal",
		link: "#",
		order: 910,
		event: "open",
		data: "https://www.dirigible.io/help/",
		divider: false
	},
	{
		name: "Contact Support",
		link: "#",
		order: 915,
		event: "open",
		data: "https://github.com/eclipse/dirigible/issues",
		divider: false
	},
	{
		name: "Suggest a Feature",
		link: "#",
		order: 920,
		event: "open",
		data: "https://github.com/eclipse/dirigible/issues/new?assignees=&labels=&template=feature_request.md&title=[New%20Feature]",
		divider: false
	},
	{
		name: "What's New",
		link: "#",
		order: 920,
		event: "open",
		data: "https://twitter.com/dirigible_io",
		divider: false
	},
	{
		name: "Check for Update",
		link: "#",
		order: 990,
		event: "open",
		data: "http://download.dirigible.io/",
		divider: true
	}
];

let config = require("core/v4/configurations");

exports.getMenu = function () {
	let menu = {
		name: "Help",
		link: "#",
		order: 900,
		items: [
			{
				name: "About",
				link: "#",
				order: 991,
				event: "openView",
				data: "",
				divider: false
			}]
	};

	let brandingHelpItems = config.get("DIRIGIBLE_BRANDING_HELP_ITEMS", "");
	if (brandingHelpItems && typeof brandingHelpItems === "string") {
		let helpItems = brandingHelpItems.split(",");
		helpItems.forEach(e => {
			let item = e.trim();
			menu.items.push({
				name: config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_NAME`, item),
				link: "#",
				order: parseInt(config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_ORDER`, "0")),
				event: "open",
				data: config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_URL`, "#"),
				divider: config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_DIVIDER`, "false").toLowerCase() === "true"
			});
		});
	} else {
		menu.items = menu.items.concat(defaultMenuItems);
	}

	menu.items.sort((a, b) => a.order - b.order);
	return menu;
};