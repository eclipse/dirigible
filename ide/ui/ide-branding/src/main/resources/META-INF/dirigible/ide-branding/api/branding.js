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
var rs = require('http/v4/rs');
var configurations = require('core/v4/configurations');

var DIRIGIBLE_BRANDING_NAME = 'DIRIGIBLE_BRANDING_NAME';
var DIRIGIBLE_BRANDING_BRAND = 'DIRIGIBLE_BRANDING_BRAND';
var DIRIGIBLE_BRANDING_BRAND_URL = 'DIRIGIBLE_BRANDING_BRAND_URL';
var DIRIGIBLE_BRANDING_ICON = 'DIRIGIBLE_BRANDING_ICON';
var DIRIGIBLE_BRANDING_WELCOME_PAGE = 'DIRIGIBLE_BRANDING_WELCOME_PAGE';

var DIRIGIBLE_BRANDING_NAME_DEFAULT = 'Eclipse Dirigible';
var DIRIGIBLE_BRANDING_BRAND_DEFAULT = 'Eclipse Dirigible';
var DIRIGIBLE_BRANDING_BRAND_URL_DEFAULT = "https://www.dirigible.io/";
var DIRIGIBLE_BRANDING_ICON_DEFAULT = '/services/v4/web/resources/images/favicon.png';
var DIRIGIBLE_BRANDING_WELCOME_PAGE_DEFAULT = '/services/v4/web/ide/welcome.html';

rs.service()
	.resource('')
		.get(function(ctx, request, response) {
			var branding = {
				'name': configurations.get(DIRIGIBLE_BRANDING_NAME, DIRIGIBLE_BRANDING_NAME_DEFAULT),
				'brand': configurations.get(DIRIGIBLE_BRANDING_BRAND, DIRIGIBLE_BRANDING_BRAND_DEFAULT),
				'brandUrl': configurations.get(DIRIGIBLE_BRANDING_BRAND_URL, DIRIGIBLE_BRANDING_BRAND_URL_DEFAULT),
				'icon': configurations.get(DIRIGIBLE_BRANDING_ICON, DIRIGIBLE_BRANDING_ICON_DEFAULT),
				'welcomePage': configurations.get(DIRIGIBLE_BRANDING_WELCOME_PAGE, DIRIGIBLE_BRANDING_WELCOME_PAGE_DEFAULT)
			};
            response.println(JSON.stringify(branding));
		})
.execute();
