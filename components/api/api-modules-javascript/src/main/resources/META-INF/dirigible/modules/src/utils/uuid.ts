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

const UuidFacade = Java.type("org.eclipse.dirigible.components.api.utils.UuidFacade");

export class Uuid{

	public static random(): string {
		return UuidFacade.random();
	};

	public static validate(input: string): boolean {
		return UuidFacade.validate(input);
	};
}

