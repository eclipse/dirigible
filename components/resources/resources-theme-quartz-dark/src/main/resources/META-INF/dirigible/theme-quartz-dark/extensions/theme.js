/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
export function getTheme() {
	return {
		id: 'quartz-dark',
		module: 'theme-quartz-dark',
		name: 'Quartz Dark',
		type: 'dark',
		version: 8,
		oldThemeId: 'default',
		links: [
			'/webjars/sap-theming__theming-base-content/11.7.0/content/Base/baseLib/sap_fiori_3_dark/css_variables.css',
			'/webjars/fundamental-styles/0.30.2/dist/theming/sap_fiori_3_dark.css',
		]
	};
};
