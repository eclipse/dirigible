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

const DatabaseFacade = Java.type("org.eclipse.dirigible.components.api.db.DatabaseFacade");

export class Sequence {
	public static nextval(sequence: string, datasourceName: string | null = null, tableName: string = null): number {
		return DatabaseFacade.nextval(sequence, datasourceName, tableName);
	};

	public static create(sequence: string, datasourceName: string | null = null): void {
		DatabaseFacade.createSequence(sequence, datasourceName);  
		//? in db facade there is no createSequence method with 2 string parameters
	};

	public static drop(sequence: string, datasourceName: string | null = null): void {
		DatabaseFacade.dropSequence(sequence, datasourceName);
	};
}