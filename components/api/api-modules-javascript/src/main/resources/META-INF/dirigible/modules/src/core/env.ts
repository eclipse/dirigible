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
 * API Env
 */

const EnvFacade = Java.type("org.eclipse.dirigible.components.api.core.EnvFacade");

export class Env{

	static get(name: string): string {
		return EnvFacade.get(name);
	};

	static list(): string {
		return EnvFacade.list();
	};
}
