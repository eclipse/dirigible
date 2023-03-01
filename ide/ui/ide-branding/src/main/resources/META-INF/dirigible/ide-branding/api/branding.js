/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
// Deprecated

let rs = require('http/v4/rs');
let configurations = require('core/v4/configurations');

let DIRIGIBLE_BRANDING_NAME = 'DIRIGIBLE_BRANDING_NAME';
let DIRIGIBLE_BRANDING_BRAND = 'DIRIGIBLE_BRANDING_BRAND';
let DIRIGIBLE_BRANDING_BRAND_URL = 'DIRIGIBLE_BRANDING_BRAND_URL';
let DIRIGIBLE_BRANDING_ICON = 'DIRIGIBLE_BRANDING_ICON';
let DIRIGIBLE_BRANDING_LOGO = 'DIRIGIBLE_BRANDING_LOGO';
let DIRIGIBLE_BRANDING_WELCOME_PAGE = 'DIRIGIBLE_BRANDING_WELCOME_PAGE';

let DIRIGIBLE_BRANDING_NAME_DEFAULT = 'Eclipse Dirigible';
let DIRIGIBLE_BRANDING_BRAND_DEFAULT = 'Eclipse Dirigible';
let DIRIGIBLE_BRANDING_BRAND_URL_DEFAULT = "https://www.dirigible.io/";
let DIRIGIBLE_BRANDING_ICON_DEFAULT = '/services/v4/web/resources/images/favicon.png';
let DIRIGIBLE_BRANDING_LOGO_DEFAULT = '/services/v4/web/resources/images/dirigible.svg';
let DIRIGIBLE_BRANDING_WELCOME_PAGE_DEFAULT = '/services/v4/web/ide/welcome.html';

rs.service()
	.resource('')
	.get(function (ctx, request, response) {
		let branding = {
			'name': configurations.get(DIRIGIBLE_BRANDING_NAME, DIRIGIBLE_BRANDING_NAME_DEFAULT),
			'brand': configurations.get(DIRIGIBLE_BRANDING_BRAND, DIRIGIBLE_BRANDING_BRAND_DEFAULT),
			'brandUrl': configurations.get(DIRIGIBLE_BRANDING_BRAND_URL, DIRIGIBLE_BRANDING_BRAND_URL_DEFAULT),
			'icon': configurations.get(DIRIGIBLE_BRANDING_ICON, DIRIGIBLE_BRANDING_ICON_DEFAULT),
			'logo': configurations.get(DIRIGIBLE_BRANDING_LOGO, DIRIGIBLE_BRANDING_LOGO_DEFAULT),
			'welcomePage': configurations.get(DIRIGIBLE_BRANDING_WELCOME_PAGE, DIRIGIBLE_BRANDING_WELCOME_PAGE_DEFAULT)
		};
		response.println(JSON.stringify(branding));
	})
	.execute();