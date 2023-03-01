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
const LOGGER = require('log/logging').getLogger('http.rs.tests');

var rs = require('http/v3/rs').get();

rs.addResourceHandler("", "get", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with GET');
})
rs.addResourceHandler("", "put", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with PUT');
})
rs.addResourceHandler("", "post", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with POST');
})
rs.addResourceHandler("", "delete", function(ctx, io){
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with DELETE');
})
rs.addResourceHandler("a/{param1}/{param2}", "get", function(ctx, io){
	LOGGER.info('Handling resource with GET and parameterized path');
	io.response.println('OK');
}, undefined, undefined, function(){
	LOGGER.info('Handling resource with GET and parameterized path');
})
.service();