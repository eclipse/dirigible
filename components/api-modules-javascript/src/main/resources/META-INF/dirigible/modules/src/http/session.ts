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
/**
 * HTTP API Session
 *
 */

const HttpSessionFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpSessionFacade");

export function isValid() {
	return HttpSessionFacade.isValid();
};

export function getAttribute(name) {
	return HttpSessionFacade.getAttribute(name);
};

export function getAttributeNames() {
	const attrNames = HttpSessionFacade.getAttributeNamesJson();
	if (attrNames) {
		return JSON.parse(attrNames);
	}
	return attrNames;
};

export function getCreationTime() {
	const time = HttpSessionFacade.getCreationTime();
	return new Date(time);
};

export function getId() {
	return HttpSessionFacade.getId();
};

export function getLastAccessedTime() {
	const time = HttpSessionFacade.getLastAccessedTime();
	return new Date(time);
};

export function getMaxInactiveInterval() {
	return HttpSessionFacade.getMaxInactiveInterval();
};

export function invalidate() {
	HttpSessionFacade.invalidate();
};

export function isNew() {
	return HttpSessionFacade.isNew();
};

export function setAttribute(name, value) {
	HttpSessionFacade.setAttribute(name, value);
};

export function removeAttribute(name) {
	HttpSessionFacade.removeAttribute(name);
};

export function setMaxInactiveInterval(interval) {
	HttpSessionFacade.setMaxInactiveInterval(interval);
};
