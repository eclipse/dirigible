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
 * API Configurations
 */

const Configuration = Java.type("org.eclipse.dirigible.commons.config.Configuration");

export function get(key, defaultValue?) {
	if (defaultValue) {
		return Configuration.get(key, defaultValue);
	}
	return Configuration.get(key);
};

export function set(key, value) {
	Configuration.set(key, value);
};

export function remove(key) {
	Configuration.remove(key);
};

export function getKeys() {
	let keys = [];
	let keysAsArray = Configuration.getKeys();
	for (let i = 0; i < keysAsArray.length; i ++) {
		keys.push(keysAsArray[i]);
	}
	return keys;
};

export function load(path) {
	Configuration.load(path);
};

export function update() {
	Configuration.update();
};

export function getOS() {
	return Configuration.getOS();
}

export function isOSWindows() {
	return Configuration.isOSWindows();
}

export function isOSMac() {
	return Configuration.isOSMac();
}

export function isOSUNIX() {
	return Configuration.isOSUNIX();
}

export function isOSSolaris() {
	return Configuration.isOSSolaris();
}

