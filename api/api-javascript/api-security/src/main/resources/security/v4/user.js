/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 User
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.getName = function() {
	return org.eclipse.dirigible.api.v3.security.UserFacade.getName();
};

exports.isInRole = function(role) {
	return org.eclipse.dirigible.api.v3.security.UserFacade.isInRole(role);
};

exports.getTimeout = function() {
	return org.eclipse.dirigible.api.v3.security.UserFacade.getTimeout();
};

exports.getAuthType = function() {
	return org.eclipse.dirigible.api.v3.security.UserFacade.getAuthType();
};

exports.getSecurityToken = function() {
	return org.eclipse.dirigible.api.v3.security.UserFacade.getSecurityToken();
};

exports.getInvocationCount = function() {
	return org.eclipse.dirigible.api.v3.security.UserFacade.getInvocationCount();
};

exports.getLanguage = function() {
	return org.eclipse.dirigible.api.v3.security.UserFacade.getLanguage();
};