/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let rs = require('http/rs');
let configurations = require('core/configurations');

rs.service()
	.resource('')
	.post(function (ctx, request) {
		let data = request.getJSON();
		for (let i = 0; i < data.env.length; i++) {
			configurations.set(data.env[i].key, data.env[i].value);
		}
	})
	.execute();