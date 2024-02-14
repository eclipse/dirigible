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
import * as bytes from "@dirigible/io/bytes";
const RegistryFacade = Java.type("org.eclipse.dirigible.components.api.platform.RegistryFacade");

export class Registry{
	public static getContent(path: string): any {
		const nativeContent = RegistryFacade.getContent(path);
		return bytes.toJavaScriptBytes(nativeContent);
	};

	public static getContentNative(path: string): any {
		return RegistryFacade.getContent(path);
	};

	public static getText(path: string): string {
		return RegistryFacade.getText(path);
	};

	public static find(path: string, pattern: string): JSON {
		return JSON.parse(RegistryFacade.find(path, pattern));
	};
}
