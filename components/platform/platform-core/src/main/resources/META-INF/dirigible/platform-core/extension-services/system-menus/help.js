/*
 * Copyright (c) 2024 Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
// @ts-nocheck
import { configurations as config } from 'sdk/core';

const defaultMenuItems = [
	{
		label: 'Help Portal',
		action: 'open',
		data: 'https://www.dirigible.io/help/',
		divider: false
	},
	{
		label: 'Contact Support',
		action: 'open',
		data: 'https://github.com/eclipse/dirigible/issues',
		divider: false
	},
	{
		label: 'Suggest a Feature',
		action: 'open',
		data: 'https://github.com/eclipse/dirigible/issues/new?assignees=&labels=&template=feature_request.md&title=[New%20Feature]',
		divider: false
	},
	{
		label: 'What\'s New',
		action: 'open',
		data: 'https://twitter.com/dirigible_io',
		divider: false
	},
	{
		label: 'Check for Updates',
		action: 'open',
		data: 'http://download.dirigible.io/',
		divider: true
	}
];

export const getMenu = () => {
	const menu = {
		label: 'Help',
		items: []
	};

	const brandingHelpItems = config.get('DIRIGIBLE_BRANDING_HELP_ITEMS', '');
	if (brandingHelpItems && typeof brandingHelpItems === 'string') {
		const helpItems = brandingHelpItems.split(',');
		helpItems.forEach(e => {
			const item = e.trim();
			menu.items.push({
				label: config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_NAME`, item),
				order: parseInt(config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_ORDER`, '0')),
				action: 'open',
				data: config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_URL`, '#'),
				divider: config.get(`DIRIGIBLE_BRANDING_HELP_ITEM_${item}_DIVIDER`, 'false').toLowerCase() === 'true'
			});
		});
		menu.items.sort((a, b) => a.order - b.order);
	} else {
		menu.items = defaultMenuItems;
	}
	menu.items.push({
		label: 'About',
		action: 'openWindow',
		windowId: 'about',
		divider: false
	});
	return {
		systemMenu: true,
		id: 'help',
		menu: menu
	};
};