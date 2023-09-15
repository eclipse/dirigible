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

const UserFacade = Java.type("org.eclipse.dirigible.components.api.security.UserFacade");

export function getName() {
    return UserFacade.getName();
};

export function isInRole(role) {
    return UserFacade.isInRole(role);
};

export function getTimeout() {
    return UserFacade.getTimeout();
};

export function getAuthType() {
    return UserFacade.getAuthType();
};

export function getSecurityToken() {
    return UserFacade.getSecurityToken();
};

export function getInvocationCount() {
    return UserFacade.getInvocationCount();
};

export function getLanguage() {
    return UserFacade.getLanguage();
};
