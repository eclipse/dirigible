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

	public static get(key: string, defaultValue: string): string {
		if (defaultValue) {
			return Configuration.get(key, defaultValue);
		}
		return Configuration.get(key);
	};

	public static set(key: string, value:string): void {
		Configuration.set(key, value);
	};

	public static remove(key: string): void {
		Configuration.remove(key);
	};

	public static getKeys(): Array<string> {
		let keys = [];
		let keysAsArray = Configuration.getKeys();
		for (let i = 0; i < keysAsArray.length; i ++) {
			keys.push(keysAsArray[i]);
		}
		return keys;
	};

	public static load(path: string): void {
		Configuration.load(path);
	};

	public static update(): void {
		Configuration.update();
	};

	public static getOS(): string {
		return Configuration.getOS();
	}

	public static isOSWindows():boolean {
		return Configuration.isOSWindows();
	}

	public static isOSMac():boolean {
		return Configuration.isOSMac();
	}

	public static isOSUNIX(): boolean {
		return Configuration.isOSUNIX();
	}

	public static isOSSolaris(): boolean {
		return Configuration.isOSSolaris();
	}

}