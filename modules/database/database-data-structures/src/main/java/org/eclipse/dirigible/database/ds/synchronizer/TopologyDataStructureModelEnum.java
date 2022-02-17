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
package org.eclipse.dirigible.database.ds.synchronizer;

public enum TopologyDataStructureModelEnum {
	
	EXECUTE_TABLE_UPDATE, EXECUTE_TABLE_CREATE, EXECUTE_TABLE_FOREIGN_KEYS_CREATE, 
	EXECUTE_TABLE_ALTER, EXECUTE_TABLE_DROP, EXECUTE_TABLE_FOREIGN_KEYS_DROP, 
	EXECUTE_VIEW_CREATE, EXECUTE_VIEW_DROP

}
