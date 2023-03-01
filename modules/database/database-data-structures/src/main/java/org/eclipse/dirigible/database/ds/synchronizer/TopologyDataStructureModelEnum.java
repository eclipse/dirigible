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
package org.eclipse.dirigible.database.ds.synchronizer;

/**
 * The Enum TopologyDataStructureModelEnum.
 */
public enum TopologyDataStructureModelEnum {

	/** The execute table update. */
	EXECUTE_TABLE_UPDATE,
	/** The execute table create. */
	EXECUTE_TABLE_CREATE,
	/** The execute table foreign keys create. */
	EXECUTE_TABLE_FOREIGN_KEYS_CREATE,

	/** The execute table alter. */
	EXECUTE_TABLE_ALTER,
	/** The execute table drop. */
	EXECUTE_TABLE_DROP,
	/** The execute table foreign keys drop. */
	EXECUTE_TABLE_FOREIGN_KEYS_DROP,

	/** The execute view create. */
	EXECUTE_VIEW_CREATE,
	/** The execute view drop. */
	EXECUTE_VIEW_DROP

}
