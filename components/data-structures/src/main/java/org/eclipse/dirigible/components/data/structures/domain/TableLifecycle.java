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
package org.eclipse.dirigible.components.data.structures.domain;

/**
 * The Enum ArtefactLifecycle.
 */
public enum TableLifecycle {
	
	/** The execute table create. */
	CREATE,
	/** The execute table update. */
	UPDATE,
	/** The execute table foreign keys create. */
	FOREIGN_KEYS_CREATE,
	/** The execute table foreign keys drop. */
	FOREIGN_KEYS_DROP,
	/** The execute table alter. */
	ALTER,
	/** The execute table drop. */
	DROP

}
