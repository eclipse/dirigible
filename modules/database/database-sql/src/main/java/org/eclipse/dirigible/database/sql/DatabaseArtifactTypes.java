/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.sql;

/**
 * The Database Artifacts Types
 */
public interface DatabaseArtifactTypes {

	public static final int TABLE = 1;
	public static final int VIEW = 2;
	public static final int PROCEDURE = 3;
	public static final int FUNCTION = 4;
	public static final int SEQUENCE = 5;
	public static final int SYNONYM = 6;
	public static final int SCHEMA = 7;
	public static final int TABLE_TYPE = 8;
	
}
