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

export class Configurations{

	static get(key, defaultValue) {
		if (defaultValue) {
			return Configuration.get(key, defaultValue);
		}
		return Configuration.get(key);
	};

	static set(key, value) {
		Configuration.set(key, value);
	};

	static remove(key) {
		Configuration.remove(key);
	};

	static getKeys() {
		let keys = [];
		let keysAsArray = Configuration.getKeys();
		for (let i = 0; i < keysAsArray.length; i ++) {
			keys.push(keysAsArray[i]);
		}
		return keys;
	};

	static load(path) {
		Configuration.load(path);
	};

	static update() {
		Configuration.update();
	};

	static getOS() {
		return Configuration.getOS();
	}

	static isOSWindows() {
		return Configuration.isOSWindows();
	}

	static isOSMac() {
		return Configuration.isOSMac();
	}

	static isOSUNIX() {
		return Configuration.isOSUNIX();
	}

	static isOSSolaris() {
		return Configuration.isOSSolaris();
	}

}